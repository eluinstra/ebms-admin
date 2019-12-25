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
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
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
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.jmx.ConnectorServer;
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
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
import org.springframework.web.context.support.XmlWebApplicationContext;

import nl.clockwork.ebms.admin.web.ExtensionProvider;
import nl.clockwork.ebms.common.KeyStoreManager.KeyStoreType;
import nl.clockwork.ebms.common.util.SecurityUtils;

public class Start
{
	protected final String DEFAULT_KEYSTORE_TYPE = KeyStoreType.PKCS12.name();
	protected final String DEFAULT_KEYSTORE_FILE = "keystore.p12";
	protected final String DEFAULT_KEYSTORE_PASSWORD = "password";
	protected final String REALM = "Realm";
	protected final String REALM_FILE = "realm.properties";
	protected Options options;
	protected CommandLine cmd;
	protected Server server = new Server();
	protected ContextHandlerCollection handlerCollection = new ContextHandlerCollection();

	public static void main(String[] args) throws Exception
	{
		Start start = new Start();
		start.options = start.createOptions();
		start.cmd = new DefaultParser().parse(start.options,args);

		if (start.cmd.hasOption("h"))
			start.printUsage();

		start.server.setHandler(start.handlerCollection);

		start.initWebServer(start.cmd,start.server);
		start.initJMX(start.cmd,start.server);

		XmlWebApplicationContext context = new XmlWebApplicationContext();
		context.setConfigLocations(getConfigLocations("classpath:nl/clockwork/ebms/admin/applicationContext.xml"));
		ContextLoaderListener contextLoaderListener = new ContextLoaderListener(context);
		start.handlerCollection.addHandler(start.createWebContextHandler(start.cmd,contextLoaderListener));

		System.out.println("Starting web server...");

		start.server.start();
		start.server.join();
	}

	protected Options createOptions()
	{
		Options result = new Options();
		result.addOption("h",false,"print this message");
		result.addOption("host",true,"set host");
		result.addOption("port",true,"set port");
		result.addOption("path",true,"set path");
		result.addOption("ssl",false,"use ssl");
		result.addOption("keyStoreType",true,"set keystore type (deault=" + DEFAULT_KEYSTORE_TYPE + ")");
		result.addOption("keyStorePath",true,"set keystore path");
		result.addOption("keyStorePassword",true,"set keystore password");
		result.addOption("clientAuthentication", false,"require ssl client authentication");
		result.addOption("trustStoreType",true,"set truststore type (deault=" + DEFAULT_KEYSTORE_TYPE + ")");
		result.addOption("trustStorePath",true,"set truststore path");
		result.addOption("trustStorePassword",true,"set truststore password");
		result.addOption("authentication",false,"use basic / client certificate authentication");
		result.addOption("clientTrustStoreType",true,"set client truststore type (deault=" + DEFAULT_KEYSTORE_TYPE + ")");
		result.addOption("clientTrustStorePath",true,"set client truststore path");
		result.addOption("clientTrustStorePassword",true,"set client truststore password");
		result.addOption("jmx",false,"start mbean server");
		return result;
	}
	
	protected static String[] getConfigLocations(String configLocation)
	{
		List<String> result = ExtensionProvider.get().stream()
				.filter(p -> !StringUtils.isEmpty(p.getSpringConfigurationFile()))
				.map(p -> p.getSpringConfigurationFile())
				.collect(Collectors.toList());
		result.add(0,configLocation);
		return result.toArray(new String[]{});
	}

	protected void initWebServer(CommandLine cmd, Server server) throws MalformedURLException, IOException
	{
		if (!cmd.hasOption("ssl"))
		{
			server.addConnector(createHttpConnector(cmd));
		}
		else
		{
			SslContextFactory factory = createSslContextFactory(cmd);
			server.addConnector(createHttpsConnector(cmd,factory));
		}
	}

	private ServerConnector createHttpConnector(CommandLine cmd)
	{
		ServerConnector result = new ServerConnector(this.server);
		result.setHost(cmd.getOptionValue("host") == null ? "0.0.0.0" : cmd.getOptionValue("host"));
		result.setPort(cmd.getOptionValue("port") == null ? 8080 : Integer.parseInt(cmd.getOptionValue("port")));
		result.setName("web");
		System.out.println("Web server configured on http://" + getHost(result.getHost()) + ":" + result.getPort() + getPath(cmd));
		if (cmd.hasOption("soap"))
			System.out.println("SOAP service configured on http://" + getHost(result.getHost()) + ":" + result.getPort() + "/service");
		return result;
	}

	private SslContextFactory createSslContextFactory(CommandLine cmd) throws MalformedURLException, IOException
	{
		SslContextFactory result = new SslContextFactory();
		addKeyStore(result);
		if (cmd.hasOption("clientAuthentication"))
			addTrustStore(result);
		return result;
	}

	private void addKeyStore(SslContextFactory result) throws MalformedURLException, IOException
	{
		String keyStoreType = cmd.getOptionValue("keyStoreType",DEFAULT_KEYSTORE_TYPE);
		String keyStorePath = cmd.getOptionValue("keyStorePath",DEFAULT_KEYSTORE_FILE);
		String keyStorePassword = cmd.getOptionValue("keyStorePassword",DEFAULT_KEYSTORE_PASSWORD);
		Resource keyStore = getResource(keyStorePath);
		System.out.println("Using keyStore " + keyStore.getURI());
		if (keyStore != null && keyStore.exists())
		{
			result.setKeyStoreType(keyStoreType);
			result.setKeyStoreResource(keyStore);
			result.setKeyStorePassword(keyStorePassword);
		}
		else
		{
			System.out.println("Web server not available: keyStore " + keyStorePath + " not found!");
			System.exit(1);
		}
	}

	private void addTrustStore(SslContextFactory sslContextFactory) throws MalformedURLException, IOException
	{
		String trustStoreType = cmd.getOptionValue("trustStoreType",DEFAULT_KEYSTORE_TYPE);
		String trustStorePath = cmd.getOptionValue("trustStorePath");
		String trustStorePassword = cmd.getOptionValue("trustStorePassword");
		Resource trustStore = getResource(trustStorePath);
		System.out.println("Using trustStore " + trustStore.getURI());
		if (trustStore != null && trustStore.exists())
		{
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

	private ServerConnector createHttpsConnector(CommandLine cmd, SslContextFactory factory)
	{
		ServerConnector connector = new ServerConnector(this.server,factory);
		connector.setHost(cmd.getOptionValue("host") == null ? "0.0.0.0" : cmd.getOptionValue("host"));
		connector.setPort(cmd.getOptionValue("port") == null ? 8443 : Integer.parseInt(cmd.getOptionValue("port")));
		connector.setName("web");
		System.out.println("Web server configured on https://" + getHost(connector.getHost()) + ":" + connector.getPort() + getPath(cmd));
		if (cmd.hasOption("soap"))
			System.out.println("SOAP service configured on https://" + getHost(connector.getHost()) + ":" + connector.getPort() + "/service");
		return connector;
	}

	protected String getPath(CommandLine cmd)
	{
		return cmd.getOptionValue("path") == null ? "/" : cmd.getOptionValue("path");
	}

	protected void initJMX(CommandLine cmd, Server server) throws Exception
	{
		if (cmd.hasOption("jmx"))
		{
			System.out.println("Starting mbean server...");
			MBeanContainer mBeanContainer = new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
			server.addBean(mBeanContainer);
			server.addBean(Log.getLog());
			JMXServiceURL jmxURL = new JMXServiceURL("rmi",null,1999,"/jndi/rmi:///jmxrmi");
			ConnectorServer jmxServer = new ConnectorServer(jmxURL,"org.eclipse.jetty.jmx:name=rmiconnectorserver");
			server.addBean(jmxServer);
		}
	}

	protected ServletContextHandler createWebContextHandler(CommandLine cmd, ContextLoaderListener contextLoaderListener) throws Exception
	{
		ServletContextHandler result = new ServletContextHandler(ServletContextHandler.SESSIONS);
		result.setVirtualHosts(new String[] {"@web"});

		result.setInitParameter("configuration","deployment");

		result.setContextPath(getPath(cmd));
		if (cmd.hasOption("authentication"))
		{
			if (!cmd.hasOption("clientAuthentication"))
			{
				System.out.println("Configuring web server basic authentication:");
				File file = new File(REALM_FILE);
				if (file.exists())
					System.out.println("Using file " + file.getAbsoluteFile());
				else
					createRealmFile(file);
				result.setSecurityHandler(getSecurityHandler());
			}
			else if (cmd.hasOption("ssl") && cmd.hasOption("clientAuthentication"))
				result.addFilter(createAuthenticationFilterHolder(),"/*",EnumSet.of(DispatcherType.REQUEST,DispatcherType.ERROR));
		}

		ServletHolder servletHolder = new ServletHolder(nl.clockwork.ebms.admin.web.ResourceServlet.class);
		result.addServlet(servletHolder,"/css/*");
		result.addServlet(servletHolder,"/fonts/*");
		result.addServlet(servletHolder,"/images/*");
		result.addServlet(servletHolder,"/js/*");

		result.addServlet(org.eclipse.jetty.servlet.DefaultServlet.class,"/");

		result.addFilter(createWicketFilterHolder(),"/*",EnumSet.of(DispatcherType.REQUEST,DispatcherType.ERROR));
		
		result.setErrorHandler(createErrorHandler());
		
		result.addEventListener(contextLoaderListener);
		
		return result;
	}

	private FilterHolder createAuthenticationFilterHolder() throws MalformedURLException, IOException
	{
		System.out.println("Configuring web server client certificate authentication:");
		FilterHolder result = new FilterHolder(nl.clockwork.ebms.servlet.ClientCertificateAuthenticationFilter.class); 
		String clientTrustStoreType = cmd.getOptionValue("clientTrustStoreType",DEFAULT_KEYSTORE_TYPE);
		String clientTrustStorePath = cmd.getOptionValue("clientTrustStorePath");
		String clientTrustStorePassword = cmd.getOptionValue("clientTrustStorePassword");
		Resource trustStore = getResource(clientTrustStorePath);
		System.out.println("Using clientTrustStore " + trustStore.getURI());
		if (trustStore != null && trustStore.exists())
		{
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
		FilterHolder result = new FilterHolder(org.apache.wicket.protocol.http.WicketFilter.class); 
		result.setInitParameter("applicationClassName","nl.clockwork.ebms.admin.web.WicketApplication");
		result.setInitParameter("filterMappingUrlPattern","/*");
		return result;
	}

	private ErrorPageErrorHandler createErrorHandler()
	{
		ErrorPageErrorHandler result = new ErrorPageErrorHandler();
		Map<String,String> errorPages = new HashMap<>();
		errorPages.put("404","/404");
		result.setErrorPages(errorPages);
		return result;
	}

	protected void printUsage()
	{
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("Start",options,true);
		System.exit(0);
	}

	protected Resource getResource(String path) throws MalformedURLException, IOException
	{
		Resource result = Resource.newResource(path);
		return result.exists() ? result : Resource.newClassPathResource(path);
	}

	protected void createRealmFile(File file) throws IOException, NoSuchAlgorithmException
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String username = readLine("enter username: ",reader);
		String password = readPassword(reader);
		System.out.println("Writing to file: " + file.getAbsoluteFile());
		FileUtils.writeStringToFile(file,username + ": " + password + ",user",Charset.defaultCharset(),false);
	}

	private String readLine(String prompt, BufferedReader reader) throws IOException
	{
		String result = null;
		while (StringUtils.isBlank(result))
		{
			System.out.print(prompt);
			result = reader.readLine();
		}
		return result;
	}

	private String readPassword(BufferedReader reader) throws IOException, NoSuchAlgorithmException
	{
		String result = null;
		while (true)
		{
			result = SecurityUtils.toMD5(readLine("enter password: ",reader));
			String password = SecurityUtils.toMD5(readLine("re-enter password: ",reader));
			if (!result.equals(password))
				System.out.println("Passwords don't match! Try again.");
			else
				break;
		}
		return result;
	}
	
	protected SecurityHandler getSecurityHandler()
	{
		ConstraintSecurityHandler result = new ConstraintSecurityHandler();

		Constraint constraint = new Constraint();
		constraint.setName("auth");
		constraint.setAuthenticate(true);
		constraint.setRoles(new String[]{"user","admin"});

		ConstraintMapping mapping = new ConstraintMapping();
		mapping.setPathSpec("/*");
		mapping.setConstraint(constraint);

		result.setConstraintMappings(Collections.singletonList(mapping));
		result.setAuthenticator(new BasicAuthenticator());
		result.setLoginService(new HashLoginService(REALM,REALM_FILE));

		return result;
	}

	protected String getHost(String host)
	{
		return "0.0.0.0".equals(host) ? "localhost" : host;
	}

}
