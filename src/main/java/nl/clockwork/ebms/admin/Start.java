/**
 * Copyright 2013 Clockwork
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.clockwork.ebms.admin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.management.remote.JMXServiceURL;
import javax.servlet.DispatcherType;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.common.logging.LogUtils;
import org.eclipse.jetty.jmx.ConnectorServer;
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.ConnectionLimit;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.experimental.FieldDefaults;
import nl.clockwork.ebms.admin.web.ExtensionProvider;
import nl.clockwork.ebms.security.KeyStoreType;

@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@RequiredArgsConstructor
public class Start
{
	protected static final String DEFAULT_KEYSTORE_TYPE = KeyStoreType.PKCS12.name();
	protected static final String DEFAULT_KEYSTORE_FILE = "nl/clockwork/ebms/keystore.p12";
	protected static final String DEFAULT_KEYSTORE_PASSWORD = "password";
	protected static final String REALM = "Realm";
	protected static final String REALM_FILE = "realm.properties";
	Server server = new Server();
	ContextHandlerCollection handlerCollection = new ContextHandlerCollection();

	public static void main(String[] args) throws Exception
	{
		LogUtils.setLoggerClass(org.apache.cxf.common.logging.Slf4jLogger.class);
		val options = createOptions();
		val cmd = new DefaultParser().parse(options,args);
		if (cmd.hasOption("h"))
			printUsage(options);

		val start = new Start();
		start.init(cmd);
		start.server.setHandler(start.handlerCollection);

		start.initWebServer(cmd,start.server);
		if (cmd.hasOption("jmx"))
			start.initJMX(cmd,start.server);

		try (val context = new AnnotationConfigWebApplicationContext())
		{
			context.register(AppConfig.class);
			val contextLoaderListener = new ContextLoaderListener(context);
			start.handlerCollection.addHandler(start.createWebContextHandler(cmd,contextLoaderListener));
	
			System.out.println("Starting web server...");
	
			try
			{
				start.server.start();
			}
			catch (Exception e)
			{
				start.server.stop();
				System.exit(1);
			}
			System.out.println("Web server started.");
			start.server.join();
		}
	}

	protected static Options createOptions()
	{
		val result = new Options();
		result.addOption("h",false,"print this message");
		result.addOption("host",true,"set host");
		result.addOption("port",true,"set port");
		result.addOption("path",true,"set path");
		result.addOption("connectionLimit",true,"set connection limit (default: none)");
		result.addOption("ssl",false,"use ssl");
		result.addOption("protocols",true,"set ssl protocols");
		result.addOption("cipherSuites",true,"set ssl cipherSuites");
		result.addOption("keyStoreType",true,"set keystore type (deault=" + DEFAULT_KEYSTORE_TYPE + ")");
		result.addOption("keyStorePath",true,"set keystore path");
		result.addOption("keyStorePassword",true,"set keystore password");
		result.addOption("clientAuthentication",false,"require ssl client authentication");
		result.addOption("clientCertificateHeader",true,"set client certificate header");
		result.addOption("trustStoreType",true,"set truststore type (deault=" + DEFAULT_KEYSTORE_TYPE + ")");
		result.addOption("trustStorePath",true,"set truststore path");
		result.addOption("trustStorePassword",true,"set truststore password");
		result.addOption("authentication",false,"use basic / client certificate authentication");
		result.addOption("clientTrustStoreType",true,"set client truststore type (deault=" + DEFAULT_KEYSTORE_TYPE + ")");
		result.addOption("clientTrustStorePath",true,"set client truststore path");
		result.addOption("clientTrustStorePassword",true,"set client truststore password");
		result.addOption("configDir",true,"set config directory (default=current dir)");
		result.addOption("jmx",false,"start jmx server (default: false)");
		result.addOption("jmxPort",true,"set jmx port");
		result.addOption("jmxAccessFile",true,"set jmx access file");
		result.addOption("jmxPasswordFile",true,"set jmx password file");
		return result;
	}
	
	protected static String[] getConfigLocations(String configLocation)
	{
		val result = ExtensionProvider.get().stream()
				.filter(p -> !StringUtils.isEmpty(p.getSpringConfigurationFile()))
				.map(p -> p.getSpringConfigurationFile())
				.collect(Collectors.toList());
		result.add(0,configLocation);
		return result.toArray(new String[]{});
	}

	protected void init(CommandLine cmd)
	{
		val configDir = cmd.getOptionValue("configDir","");
		System.setProperty("ebms.configDir",configDir);
		System.out.println("Using config directory: " + configDir);
	}

	protected void initWebServer(CommandLine cmd, Server server) throws MalformedURLException, IOException
	{
		val connector = cmd.hasOption("ssl") ? createHttpsConnector(cmd,createSslContextFactory(cmd,cmd.hasOption("clientAuthentication"))) : createHttpConnector(cmd);
		server.addConnector(connector);
		if (cmd.hasOption("connectionLimit"))
			server.addBean(new ConnectionLimit(Integer.parseInt(cmd.getOptionValue("connectionLimit")),connector));
	}

	private ServerConnector createHttpConnector(CommandLine cmd)
	{
		val result = new ServerConnector(this.server);
		result.setHost(cmd.getOptionValue("host","0.0.0.0"));
		result.setPort(Integer.parseInt(cmd.getOptionValue("port","8080")));
		result.setName("web");
		System.out.println("Web server configured on http://" + Utils.getHost(result.getHost()) + ":" + result.getPort() + getPath(cmd));
		if (cmd.hasOption("soap"))
			System.out.println("SOAP service configured on http://" + Utils.getHost(result.getHost()) + ":" + result.getPort() + "/service");
		return result;
	}

	private SslContextFactory.Server createSslContextFactory(CommandLine cmd, boolean clientAuthentication) throws MalformedURLException, IOException
	{
		val result = new SslContextFactory.Server();
		addKeyStore(cmd,result);
		if (clientAuthentication)
			addTrustStore(cmd,result);
		result.setExcludeCipherSuites();
		return result;
	}

	private void addKeyStore(CommandLine cmd, SslContextFactory.Server sslContextFactory) throws MalformedURLException, IOException
	{
		val keyStoreType = cmd.getOptionValue("keyStoreType",DEFAULT_KEYSTORE_TYPE);
		val keyStorePath = cmd.getOptionValue("keyStorePath",DEFAULT_KEYSTORE_FILE);
		val keyStorePassword = cmd.getOptionValue("keyStorePassword",DEFAULT_KEYSTORE_PASSWORD);
		val keyStore = getResource(keyStorePath);
		if (keyStore != null && keyStore.exists())
		{
			System.out.println("Using keyStore " + keyStore.getURI());
			String protocols = cmd.getOptionValue("protocols");
			if (!StringUtils.isEmpty(protocols))
				sslContextFactory.setIncludeProtocols(StringUtils.stripAll(StringUtils.split(protocols,',')));
			String cipherSuites = cmd.getOptionValue("cipherSuites");
			if (!StringUtils.isEmpty(cipherSuites))
				sslContextFactory.setIncludeCipherSuites(StringUtils.stripAll(StringUtils.split(cipherSuites,',')));
			sslContextFactory.setKeyStoreType(keyStoreType);
			sslContextFactory.setKeyStoreResource(keyStore);
			sslContextFactory.setKeyStorePassword(keyStorePassword);
		}
		else
		{
			System.out.println("Web server not available: keyStore " + keyStorePath + " not found!");
			System.exit(1);
		}
	}

	private void addTrustStore(CommandLine cmd, SslContextFactory.Server sslContextFactory) throws MalformedURLException, IOException
	{
		val trustStoreType = cmd.getOptionValue("trustStoreType",DEFAULT_KEYSTORE_TYPE);
		val trustStorePath = cmd.getOptionValue("trustStorePath");
		val trustStorePassword = cmd.getOptionValue("trustStorePassword");
		val trustStore = getResource(trustStorePath);
		if (trustStore != null && trustStore.exists())
		{
			System.out.println("Using trustStore " + trustStore.getURI());
			sslContextFactory.setNeedClientAuth(true);
			sslContextFactory.setTrustStoreType(trustStoreType);
			sslContextFactory.setTrustStoreResource(trustStore);
			sslContextFactory.setTrustStorePassword(trustStorePassword);
		}
		else
		{
			System.out.println("Web server not available: trustStore " + trustStorePath + " not found!");
			System.exit(1);
		}
	}

	private ServerConnector createHttpsConnector(CommandLine cmd, SslContextFactory.Server sslContectFactory)
	{
		val connector = new ServerConnector(this.server,sslContectFactory);
		connector.setHost(cmd.getOptionValue("host","0.0.0.0"));
		connector.setPort(Integer.parseInt(cmd.getOptionValue("port","8443")));
		connector.setName("web");
		System.out.println("Web server configured on https://" + Utils.getHost(connector.getHost()) + ":" + connector.getPort() + getPath(cmd));
		if (cmd.hasOption("soap"))
			System.out.println("SOAP service configured on https://" + Utils.getHost(connector.getHost()) + ":" + connector.getPort() + "/service");
		return connector;
	}

	protected String getPath(CommandLine cmd)
	{
		return cmd.getOptionValue("path","/");
	}

	protected void initJMX(CommandLine cmd, Server server) throws Exception
	{
		System.out.println("Starting jmx server...");
		val mBeanContainer = new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
		server.addBean(mBeanContainer);
		server.addBean(Log.getLog());
		val jmxURL = new JMXServiceURL("rmi",null,Integer.parseInt(cmd.getOptionValue("jmxPort","1999")),"/jndi/rmi:///jmxrmi");
		//val sslContextFactory = cmd.hasOption("ssl") ? createSslContextFactory(cmd,false) : null;
		val jmxServer = new ConnectorServer(jmxURL,createEnv(cmd),"org.eclipse.jetty.jmx:name=rmiconnectorserver");//,sslContextFactory);
		server.addBean(jmxServer);
		System.out.println("Jmx server configured on " + jmxURL);
	}

	private Map<String,Object> createEnv(CommandLine cmd)
	{
		val result = new HashMap<String, Object>();
		if (cmd.hasOption("jmxAccessFile") && cmd.hasOption("jmxPasswordFile"))
		{
			result.put("jmx.remote.x.access.file",cmd.hasOption("jmxAccessFile"));
			result.put("jmx.remote.x.password.file",cmd.hasOption("jmxPasswordFile"));
		}
		return result;
	}

	protected ServletContextHandler createWebContextHandler(CommandLine cmd, ContextLoaderListener contextLoaderListener) throws Exception
	{
		val result = new ServletContextHandler(ServletContextHandler.SESSIONS);
		result.setVirtualHosts(new String[] {"@web"});
		result.setInitParameter("configuration","deployment");
		result.setContextPath(getPath(cmd));
		if (cmd.hasOption("authentication"))
		{
			if (!cmd.hasOption("clientAuthentication"))
			{
				System.out.println("Configuring web server basic authentication:");
				val file = new File(REALM_FILE);
				if (file.exists())
					System.out.println("Using file " + file.getAbsoluteFile());
				else
					createRealmFile(file);
				result.setSecurityHandler(getSecurityHandler());
			}
			else if (cmd.hasOption("ssl") && cmd.hasOption("clientAuthentication"))
			{
				result.addFilter(createClientCertificateManagerFilterHolder(cmd),"/*",EnumSet.of(DispatcherType.REQUEST,DispatcherType.ERROR));
				result.addFilter(createClientCertificateAuthenticationFilterHolder(cmd),"/*",EnumSet.of(DispatcherType.REQUEST,DispatcherType.ERROR));
			}
		}
		if (cmd.hasOption("soap"))
			result.addServlet(org.apache.cxf.transport.servlet.CXFServlet.class,"/service/*");
		if (!cmd.hasOption("headless"))
		{
			val servletHolder = new ServletHolder(nl.clockwork.ebms.admin.web.ResourceServlet.class);
			result.addServlet(servletHolder,"/css/*");
			result.addServlet(servletHolder,"/fonts/*");
			result.addServlet(servletHolder,"/images/*");
			result.addServlet(servletHolder,"/js/*");
			result.addFilter(createWicketFilterHolder(),"/*",EnumSet.of(DispatcherType.REQUEST,DispatcherType.ERROR));
		}
		result.setErrorHandler(createErrorHandler());
		result.addEventListener(contextLoaderListener);
		return result;
	}

	private FilterHolder createClientCertificateManagerFilterHolder(CommandLine cmd)
	{
		val result = new FilterHolder(nl.clockwork.ebms.server.servlet.ClientCertificateManagerFilter.class); 
		result.setInitParameter("x509CertificateHeader",cmd.getOptionValue("clientCertificateHeader"));
		return result;
	}

	private FilterHolder createClientCertificateAuthenticationFilterHolder(CommandLine cmd) throws MalformedURLException, IOException
	{
		System.out.println("Configuring web server client certificate authentication:");
		val result = new FilterHolder(nl.clockwork.ebms.server.servlet.ClientCertificateAuthenticationFilter.class); 
		val clientTrustStoreType = cmd.getOptionValue("clientTrustStoreType",DEFAULT_KEYSTORE_TYPE);
		val clientTrustStorePath = cmd.getOptionValue("clientTrustStorePath");
		val clientTrustStorePassword = cmd.getOptionValue("clientTrustStorePassword");
		val trustStore = getResource(clientTrustStorePath);
		if (trustStore != null && trustStore.exists())
		{
			System.out.println("Using clientTrustStore " + trustStore.getURI());
			result.setInitParameter("trustStoreType",clientTrustStoreType);
			result.setInitParameter("trustStorePath",clientTrustStorePath);
			result.setInitParameter("trustStorePassword",clientTrustStorePassword);
			return result;
		}
		else
		{
			System.out.println("Web server not available: clientTrustStore " + clientTrustStorePath + " not found!");
			System.exit(1);
			return null;
		}
	}

	private FilterHolder createWicketFilterHolder()
	{
		val result = new FilterHolder(org.apache.wicket.protocol.http.WicketFilter.class); 
		result.setInitParameter("applicationFactoryClassName","org.apache.wicket.spring.SpringWebApplicationFactory");
		result.setInitParameter("applicationBean","wicketApplication");
		result.setInitParameter("filterMappingUrlPattern","/*");
		return result;
	}

	private ErrorPageErrorHandler createErrorHandler()
	{
		val result = new ErrorPageErrorHandler();
		val errorPages = new HashMap<String,String>();
		errorPages.put("404","/404");
		result.setErrorPages(errorPages);
		return result;
	}

	protected static void printUsage(Options options)
	{
		val formatter = new HelpFormatter();
		formatter.printHelp("Start",options,true);
		System.exit(0);
	}

	protected Resource getResource(String path) throws MalformedURLException, IOException
	{
		val result = Resource.newResource(path);
		return result.exists() ? result : Resource.newClassPathResource(path);
	}

	protected void createRealmFile(File file) throws IOException, NoSuchAlgorithmException
	{
		val reader = new BufferedReader(new InputStreamReader(System.in));
		val username = readLine("enter username: ",reader);
		val password = readPassword(reader);
		System.out.println("Writing to file: " + file.getAbsoluteFile());
		FileUtils.writeStringToFile(file,username + ": " + password + ",user",Charset.defaultCharset(),false);
	}

	protected String readLine(String prompt, BufferedReader reader) throws IOException
	{
		while (true)
		{
			System.out.print(prompt);
			val result = reader.readLine();
			if (StringUtils.isNotBlank(result))
				return result;
		}
	}

	private String readPassword(BufferedReader reader) throws IOException, NoSuchAlgorithmException
	{
		while (true)
		{
			val result = toMD5(readLine("enter password: ",reader));
			val password = toMD5(readLine("re-enter password: ",reader));
			if (result.equals(password))
				return result;
			else
				System.out.println("Passwords don't match! Try again.");
		}
	}
	
	private String toMD5(String s) throws NoSuchAlgorithmException, UnsupportedEncodingException
	{
		return "MD5:" + DigestUtils.md5Hex(s);
	}

	protected SecurityHandler getSecurityHandler()
	{
		val result = new ConstraintSecurityHandler();
		val constraintMappings = Collections.singletonList(createConstraintMapping(createAuthenticationConstraint()));
		result.setConstraintMappings(constraintMappings);
		result.setAuthenticator(new BasicAuthenticator());
		result.setLoginService(new HashLoginService(REALM,REALM_FILE));
		return result;
	}

	private ConstraintMapping createConstraintMapping(Constraint constraint)
	{
		val mapping = new ConstraintMapping();
		mapping.setPathSpec("/*");
		mapping.setConstraint(constraint);
		return mapping;
	}

	private Constraint createAuthenticationConstraint()
	{
		val constraint = new Constraint();
		constraint.setName("auth");
		constraint.setAuthenticate(true);
		constraint.setRoles(new String[]{"user","admin"});
		return constraint;
	}
}
