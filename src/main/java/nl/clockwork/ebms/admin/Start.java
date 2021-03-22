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

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.management.remote.JMXServiceURL;
import javax.servlet.DispatcherType;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
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

import com.microsoft.applicationinsights.web.internal.ApplicationInsightsServletContextListener;
import com.microsoft.applicationinsights.web.internal.WebRequestTrackingFilter;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.experimental.FieldDefaults;
import nl.clockwork.ebms.admin.web.ExtensionProvider;
import nl.clockwork.ebms.security.KeyStoreType;
import nl.clockwork.ebms.server.servlet.HealthServlet;

@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@RequiredArgsConstructor
public class Start implements SystemInterface
{
	protected static final String DEFAULT_KEYSTORE_TYPE = KeyStoreType.PKCS12.name();
	protected static final String DEFAULT_KEYSTORE_FILE = "nl/clockwork/ebms/keystore.p12";
	protected static final String DEFAULT_KEYSTORE_PASSWORD = "password";
	protected static final String REALM = "Realm";
	protected static final String REALM_FILE = "realm.properties";
	Server server = new Server();
	ContextHandlerCollection handlerCollection = new ContextHandlerCollection();
	TextIO textIO = TextIoFactory.getTextIO();

	public static void main(String[] args) throws Exception
	{
		LogUtils.setLoggerClass(org.apache.cxf.common.logging.Slf4jLogger.class);
		val app = new Start();
		app.startService(args);
	}

	private void startService(String[] args) throws ParseException, Exception, MalformedURLException, IOException, InterruptedException
	{
		val options = createOptions();
		val cmd = new DefaultParser().parse(options,args);
		if (cmd.hasOption("h"))
			printUsage(options);
		init(cmd);
		server.setHandler(handlerCollection);
		if (cmd.hasOption("jmx"))
			initJMX(cmd,server);
		try (val context = new AnnotationConfigWebApplicationContext())
		{
			context.register(AppConfig.class);
			getConfigClasses().forEach(c -> context.register(c));
			val contextLoaderListener = new ContextLoaderListener(context);
			if (cmd.hasOption("soap") || !cmd.hasOption("headless"))
			{
				initWebServer(cmd,server);
				handlerCollection.addHandler(createWebContextHandler(cmd,contextLoaderListener));
			}
			if (cmd.hasOption("health"))
			{
				initHealthServer(cmd,server);
				handlerCollection.addHandler(createHealthContextHandler(cmd,contextLoaderListener));
			}
			println("Starting Server...");
			try
			{
				server.start();
				println("Server started.");
				server.join();
			}
			catch (Exception e)
			{
				server.stop();
				context.close();
				exit(1);
			}
		}
	}

	protected Options createOptions()
	{
		val result = new Options();
		result.addOption("h",false,"print this message");
		result.addOption("host",true,"set host [default: 0.0.0.0]");
		result.addOption("port",true,"set port [default: <8080|8443>]");
		result.addOption("path",true,"set path [default: /]");
		result.addOption("soap",false,"start soap service");
		result.addOption("headless",false,"start without web interface");
		result.addOption("health",false,"start health service");
		result.addOption("healthPort",true,"set health service port [default: 8008]");
		result.addOption("connectionLimit",true,"set connection limit [default: <none>]");
		result.addOption("queriesPerSecond",true,"set requests per second limit [default: <none>]");
		result.addOption("userQueriesPerSecond",true,"set requests per user per secondlimit [default: <none>]");
		result.addOption("auditLogging",false,"enable audit logging");
		result.addOption("applicationInsights",false,"enable applicationInsights");
		result.addOption("ssl",false,"use ssl");
		result.addOption("protocols",true,"set ssl protocols [default: <none>]");
		result.addOption("cipherSuites",true,"set ssl cipherSuites [default: <none>]");
		result.addOption("keyStoresType",true,"set keystores type [default: <none>]");
		result.addOption("keyStoreType",true,"set keystore type [default: " + DEFAULT_KEYSTORE_TYPE + "]");
		result.addOption("keyStorePath",true,"set keystore path [default: " + DEFAULT_KEYSTORE_FILE + "]");
		result.addOption("keyStorePassword",true,"set keystore password [default: " + DEFAULT_KEYSTORE_PASSWORD + "]");

		result.addOption("keyvaultUri",true,"set keystore uri [default: <none>]");
		result.addOption("keyvaultTennantId",true,"set keystore tennant identity [default: <none>]");
		result.addOption("keyvaultClientId",true,"set keyvault client id [default: <none>]");
		result.addOption("keyvaultClientSecret",true,"set keyvault client secret [default: <none>]");

		result.addOption("clientAuthentication",false,"require ssl client authentication");
		result.addOption("clientCertificateHeader",true,"set client certificate header [default: <none>]");
		result.addOption("trustStoreType",true,"set truststore type [default: " + DEFAULT_KEYSTORE_TYPE + "]");
		result.addOption("trustStorePath",true,"set truststore path [default: <none>]");
		result.addOption("trustStorePassword",true,"set truststore password [default: <none>]");
		result.addOption("authentication",false,"use basic | client certificate authentication");
		result.addOption("clientTrustStoreType",true,"set client truststore type [default: " + DEFAULT_KEYSTORE_TYPE + "]");
		result.addOption("clientTrustStorePath",true,"set client truststore path [default: <none>]");
		result.addOption("clientTrustStorePassword",true,"set client truststore password [default: <none>]");
		result.addOption("configDir",true,"set config directory [default: <startup_directory>]");
		result.addOption("jmx",false,"start jmx server");
		result.addOption("jmxPort",true,"set jmx port [default: 1999]");
		result.addOption("jmxAccessFile",true,"set jmx access file [default: <none>]");
		result.addOption("jmxPasswordFile",true,"set jmx password file [default: <none>]");
		return result;
	}

	protected void printUsage(Options options)
	{
		val formatter = new HelpFormatter();
		formatter.printHelp(getClass().getSimpleName(),options,true);
		exit(0);
	}

	protected List<Class<?>> getConfigClasses()
	{
		return ExtensionProvider.get().stream()
				.filter(p -> p.getSpringConfigurationClass() != null)
				.map(p -> p.getSpringConfigurationClass())
				.collect(Collectors.toList());
	}

	protected void init(CommandLine cmd)
	{
		val configDir = cmd.getOptionValue("configDir","");
		setProperty("ebms.configDir",configDir);
		println("Using config directory: " + configDir);
	}

	protected void initWebServer(CommandLine cmd, Server server) throws GeneralSecurityException, IOException
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
		if (!cmd.hasOption("headless"))
			println("Web Server configured on http://" + Utils.getHost(result.getHost()) + ":" + result.getPort() + getPath(cmd));
		if (cmd.hasOption("soap"))
			println("SOAP Service configured on http://" + Utils.getHost(result.getHost()) + ":" + result.getPort() + "/service");
		return result;
	}

	protected void initHealthServer(CommandLine cmd, Server server) throws MalformedURLException, IOException
	{
		val connector = createHealthConnector(cmd,server);
		server.addConnector(connector);
	}

	private ServerConnector createHealthConnector(CommandLine cmd, Server server)
	{
		val result = new ServerConnector(server);
		result.setHost(cmd.getOptionValue("host","0.0.0.0"));
		result.setPort(Integer.parseInt(cmd.getOptionValue("healthPort","8008")));
		result.setName("health");
		println("Health Service configured on http://" + Utils.getHost(result.getHost()) + ":" + result.getPort() + "/health");
		return result;
	}

	private SslContextFactory.Server createSslContextFactory(CommandLine cmd, boolean clientAuthentication) throws GeneralSecurityException, IOException
	{
		val result = new SslContextFactory.Server();
		EbMSKeyStore ebMSKeyStore = cmd.getOptionValue("keyStoresType","").equals("AZURE")
				? EbMSKeyStore.of(
						cmd.getOptionValue("keyvaultUri"),
						cmd.getOptionValue("keyvaultTennantId"),
						cmd.getOptionValue("keyvaultClientId"),
						cmd.getOptionValue("keyvaultClientSecret")
						)
				: EbMSKeyStore.of(
						KeyStoreType.valueOf(cmd.getOptionValue("keyStoreType",DEFAULT_KEYSTORE_TYPE)),
						cmd.getOptionValue("keyStorePath",DEFAULT_KEYSTORE_FILE),
						cmd.getOptionValue("keyStorePassword",DEFAULT_KEYSTORE_PASSWORD));
		addKeyStore(cmd,result,ebMSKeyStore);
		if (clientAuthentication)
			addTrustStore(cmd,result);
		result.setExcludeCipherSuites();
		return result;
	}

	private void addKeyStore(CommandLine cmd, SslContextFactory.Server sslContextFactory, EbMSKeyStore ebMSKeyStore) throws MalformedURLException, IOException
	{
		val protocols = cmd.getOptionValue("protocols");
		if (!StringUtils.isEmpty(protocols))
			sslContextFactory.setIncludeProtocols(StringUtils.stripAll(StringUtils.split(protocols,',')));
		val cipherSuites = cmd.getOptionValue("cipherSuites");
		if (!StringUtils.isEmpty(cipherSuites))
			sslContextFactory.setIncludeCipherSuites(StringUtils.stripAll(StringUtils.split(cipherSuites,',')));
		sslContextFactory.setKeyStore(ebMSKeyStore.getKeyStore());
		sslContextFactory.setKeyStorePassword(ebMSKeyStore.getPassword());
	}

	private void addTrustStore(CommandLine cmd, SslContextFactory.Server sslContextFactory) throws MalformedURLException, IOException
	{
		val trustStoreType = cmd.getOptionValue("trustStoreType",DEFAULT_KEYSTORE_TYPE);
		val trustStorePath = cmd.getOptionValue("trustStorePath");
		val trustStorePassword = cmd.getOptionValue("trustStorePassword");
		val trustStore = getResource(trustStorePath);
		if (trustStore != null && trustStore.exists())
		{
			println("Using trustStore " + trustStore.getURI());
			sslContextFactory.setNeedClientAuth(true);
			sslContextFactory.setTrustStoreType(trustStoreType);
			sslContextFactory.setTrustStoreResource(trustStore);
			sslContextFactory.setTrustStorePassword(trustStorePassword);
		}
		else
		{
			println("Web Server not available: trustStore " + trustStorePath + " not found!");
			exit(1);
		}
	}

	private ServerConnector createHttpsConnector(CommandLine cmd, SslContextFactory.Server sslContectFactory)
	{
		val connector = new ServerConnector(this.server,sslContectFactory);
		connector.setHost(cmd.getOptionValue("host","0.0.0.0"));
		connector.setPort(Integer.parseInt(cmd.getOptionValue("port","8443")));
		connector.setName("web");
		if (!cmd.hasOption("headless"))
			println("Web Server configured on https://" + Utils.getHost(connector.getHost()) + ":" + connector.getPort() + getPath(cmd));
		if (cmd.hasOption("soap"))
			println("SOAP Service configured on https://" + Utils.getHost(connector.getHost()) + ":" + connector.getPort() + "/service");
		return connector;
	}

	protected String getPath(CommandLine cmd)
	{
		return cmd.getOptionValue("path","/");
	}

	protected void initJMX(CommandLine cmd, Server server) throws Exception
	{
		println("Starting JMX Server...");
		val mBeanContainer = new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
		server.addBean(mBeanContainer);
		server.addBean(Log.getLog());
		val jmxURL = new JMXServiceURL("rmi",null,Integer.parseInt(cmd.getOptionValue("jmxPort","1999")),"/jndi/rmi:///jmxrmi");
		//val sslContextFactory = cmd.hasOption("ssl") ? createSslContextFactory(cmd,false) : null;
		val jmxServer = new ConnectorServer(jmxURL,createEnv(cmd),"org.eclipse.jetty.jmx:name=rmiconnectorserver");//,sslContextFactory);
		server.addBean(jmxServer);
		println("JMX Server configured on " + jmxURL);
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
		if (cmd.hasOption("applicationInsights"))
		{
			result.addFilter(createWebRequestTrackingFilterHolder(),"/*",EnumSet.allOf(DispatcherType.class));
			result.addEventListener(new ApplicationInsightsServletContextListener());
		}
		if (cmd.hasOption("auditLogging"))
			result.addFilter(createRemoteAddressMDCFilterHolder(),"/*",EnumSet.allOf(DispatcherType.class));
		if (!StringUtils.isEmpty(cmd.getOptionValue("queriesPerSecond")))
			result.addFilter(createRateLimiterFilterHolder(cmd.getOptionValue("queriesPerSecond")),"/*",EnumSet.allOf(DispatcherType.class));
		if (!StringUtils.isEmpty(cmd.getOptionValue("userQueriesPerSecond")))
			result.addFilter(createUserRateLimiterFilterHolder(cmd.getOptionValue("userQueriesPerSecond")),"/*",EnumSet.allOf(DispatcherType.class));
		if (cmd.hasOption("authentication"))
		{
			if (!cmd.hasOption("clientAuthentication"))
			{
				println("Configuring Web Server basic authentication:");
				val file = new File(REALM_FILE);
				if (file.exists())
					println("Using file " + file.getAbsoluteFile());
				else
					createRealmFile(file);
				result.setSecurityHandler(getSecurityHandler());
			}
			else if (cmd.hasOption("ssl") && cmd.hasOption("clientAuthentication"))
			{
				result.addFilter(createClientCertificateManagerFilterHolder(cmd.getOptionValue("clientCertificateHeader")),"/*",EnumSet.of(DispatcherType.REQUEST,DispatcherType.ERROR));
				result.addFilter(createClientCertificateAuthenticationFilterHolder(cmd),"/*",EnumSet.of(DispatcherType.REQUEST,DispatcherType.ERROR));
			}
		}
		if (cmd.hasOption("soap"))
			result.addServlet(CXFServlet.class,"/service/*");
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

	protected FilterHolder createWebRequestTrackingFilterHolder()
	{
		return new FilterHolder(WebRequestTrackingFilter.class);
	}

	protected FilterHolder createRemoteAddressMDCFilterHolder()
	{
		return new FilterHolder(nl.clockwork.ebms.server.servlet.RemoteAddressMDCFilter.class);
	}

	protected FilterHolder createRateLimiterFilterHolder(String queriesPerSecond)
	{
		val result = new FilterHolder(nl.clockwork.ebms.server.servlet.RateLimiterFilter.class); 
		result.setInitParameter("queriesPerSecond",queriesPerSecond);
		return result;
	}

	protected FilterHolder createUserRateLimiterFilterHolder(String queriesPerSecond)
	{
		val result = new FilterHolder(nl.clockwork.ebms.server.servlet.RateLimiterFilter.class); 
		result.setInitParameter("queriesPerSecond",queriesPerSecond);
		return result;
	}

	protected FilterHolder createClientCertificateManagerFilterHolder(String clientCertificateHeader)
	{
		val result = new FilterHolder(nl.clockwork.ebms.server.servlet.ClientCertificateManagerFilter.class); 
		result.setInitParameter("x509CertificateHeader",clientCertificateHeader);
		return result;
	}

	private FilterHolder createClientCertificateAuthenticationFilterHolder(CommandLine cmd) throws MalformedURLException, IOException
	{
		println("Configuring Web Server client certificate authentication:");
		val result = new FilterHolder(nl.clockwork.ebms.server.servlet.ClientCertificateAuthenticationFilter.class); 
		val clientTrustStoreType = cmd.getOptionValue("clientTrustStoreType",DEFAULT_KEYSTORE_TYPE);
		val clientTrustStorePath = cmd.getOptionValue("clientTrustStorePath");
		val clientTrustStorePassword = cmd.getOptionValue("clientTrustStorePassword");
		val trustStore = getResource(clientTrustStorePath);
		if (trustStore != null && trustStore.exists())
		{
			println("Using clientTrustStore " + trustStore.getURI());
			result.setInitParameter("trustStoreType",clientTrustStoreType);
			result.setInitParameter("trustStorePath",clientTrustStorePath);
			result.setInitParameter("trustStorePassword",clientTrustStorePassword);
			return result;
		}
		else
		{
			println("Web Server not available: clientTrustStore " + clientTrustStorePath + " not found!");
			exit(1);
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

	protected ServletContextHandler createHealthContextHandler(CommandLine cmd, ContextLoaderListener contextLoaderListener) throws Exception
	{
		val result = new ServletContextHandler(ServletContextHandler.SESSIONS);
		result.setVirtualHosts(new String[] {"@health"});
		result.setInitParameter("configuration","deployment");
		result.setContextPath("/");
		result.addServlet(HealthServlet.class,"/health/*");
		return result;
	}

	protected Resource getResource(String path) throws MalformedURLException, IOException
	{
		val result = Resource.newResource(path);
		return result.exists() ? result : Resource.newClassPathResource(path);
	}

	protected void createRealmFile(File file) throws IOException, NoSuchAlgorithmException
	{
		val username = textIO.newStringInputReader()
				.withDefaultValue("admin")
				.read("enter username");
		val password = readPassword();
		println("Writing to file: " + file.getAbsoluteFile());
		FileUtils.writeStringToFile(file,username + ": " + password + ",user",Charset.defaultCharset(),false);
	}

	private String readPassword() throws IOException, NoSuchAlgorithmException
	{
		val reader = textIO.newStringInputReader()
				.withMinLength(8)
				.withInputMasking(true);
		while (true)
		{
			val result = toMD5(reader.read("enter password"));
			val password = toMD5(reader.read("re-enter password"));
			if (result.equals(password))
				return result;
			else
				println("Passwords don't match! Try again.");
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
