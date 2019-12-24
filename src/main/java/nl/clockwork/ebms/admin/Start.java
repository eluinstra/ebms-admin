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

public class Start
{
	protected final String DEFAULT_KEYSTORE_FILE = "keystore.jks";
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
		start.initCmd(args);

		if (start.cmd.hasOption("h"))
			start.printUsage();

		start.server.setHandler(start.handlerCollection);

		start.initWebServer();
		start.initJMX();
		XmlWebApplicationContext context = new XmlWebApplicationContext();
		context.setConfigLocations(getConfigLocations("classpath:nl/clockwork/ebms/admin/applicationContext.xml"));
		ContextLoaderListener contextLoaderListener = new ContextLoaderListener(context);
		start.initWebContext(contextLoaderListener);

		System.out.println("Starting web server...");

		start.server.start();
		start.server.join();
	}

	protected void initCmd(String[] args) throws ParseException
	{
		createOptions();
		cmd = new DefaultParser().parse(options,args);
	}

	protected Options createOptions()
	{
		options = new Options();
		options.addOption("h",false,"print this message");
		options.addOption("host",true,"set host");
		options.addOption("port",true,"set port");
		options.addOption("path",true,"set path");
		options.addOption("ssl",false,"use ssl");
		options.addOption("keystoreType",true,"set keystore type");
		options.addOption("keystorePath",true,"set keystore path");
		options.addOption("keystorePassword",true,"set keystore password");
		options.addOption("clientAuthentication", false,"use ssl client authentication");
		options.addOption("truststoreType",true,"set truststore type");
		options.addOption("truststorePath",true,"set truststore path");
		options.addOption("truststorePassword",true,"set truststore password");
		options.addOption("authentication",false,"use basic / client certificate authentication");
		options.addOption("clientTruststoreType",true,"set client truststore type");
		options.addOption("clientTruststorePath",true,"set client truststore path");
		options.addOption("clientTruststorePassword",true,"set client truststore password");
		options.addOption("jmx",false,"start mbean server");
		return options;
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

	protected void initWebServer() throws MalformedURLException, IOException
	{
		if (!cmd.hasOption("ssl"))
		{
			ServerConnector connector = new ServerConnector(this.server);
			connector.setHost(cmd.getOptionValue("host") == null ? "0.0.0.0" : cmd.getOptionValue("host"));
			connector.setPort(cmd.getOptionValue("port") == null ? 8080 : Integer.parseInt(cmd.getOptionValue("port")));
			connector.setName("web");
			server.addConnector(connector);
			System.out.println("Web server configured on http://" + getHost(connector.getHost()) + ":" + connector.getPort() + getPath());
			if (cmd.hasOption("soap"))
				System.out.println("SOAP service configured on http://" + getHost(connector.getHost()) + ":" + connector.getPort() + "/service");
		}
		else
		{
			String keystorePath = cmd.getOptionValue("keystorePath",DEFAULT_KEYSTORE_FILE);
			String keystorePassword = cmd.getOptionValue("keystorePassword",DEFAULT_KEYSTORE_PASSWORD);
			if (DEFAULT_KEYSTORE_FILE.equals(keystorePath))
				System.out.println("Using default keystore!");
			else
				System.out.println("Using keystore " + new File(keystorePath).getAbsolutePath());
			Resource keystore = getResource(keystorePath);
			if (keystore != null && keystore.exists())
			{
				SslContextFactory factory = new SslContextFactory();
				factory.setKeyStoreResource(keystore);
				factory.setKeyStorePassword(keystorePassword);

				if (cmd.hasOption("clientAuthentication"))
				{
					String truststorePath = cmd.getOptionValue("truststorePath");
					String truststorePassword = cmd.getOptionValue("truststorePassword");
					Resource truststore = getResource(truststorePath);
					factory.setTrustStoreResource(truststore);
					factory.setTrustStorePassword(truststorePassword);
				}

				ServerConnector connector = new ServerConnector(this.server,factory);
				connector.setHost(cmd.getOptionValue("host") == null ? "0.0.0.0" : cmd.getOptionValue("host"));
				connector.setPort(cmd.getOptionValue("port") == null ? 8433 : Integer.parseInt(cmd.getOptionValue("port")));
				connector.setName("web");
				server.addConnector(connector);
				System.out.println("Web server configured on https://" + getHost(connector.getHost()) + ":" + connector.getPort() + getPath());
				if (cmd.hasOption("soap"))
					System.out.println("SOAP service configured on https://" + getHost(connector.getHost()) + ":" + connector.getPort() + "/service");
			}
			else
			{
				System.out.println("Web server not available: keystore " + keystorePath + " not found!");
				System.exit(1);
			}
		}
	}

	protected String getPath()
	{
		return cmd.getOptionValue("path") == null ? "/" : cmd.getOptionValue("path");
	}

	protected void initJMX() throws Exception
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

	protected void initWebContext(ContextLoaderListener contextLoaderListener) throws Exception
	{
		ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		handler.setVirtualHosts(new String[] {"@web"});
		handlerCollection.addHandler(handler);

		handler.setContextPath(getPath());

		if (cmd.hasOption("authentication") && !cmd.hasOption("clientAuthentication"))
		{
			System.out.println("Configuring web server authentication:");
			File file = new File(REALM_FILE);
			if (file.exists())
				System.out.println("Using file: " + file.getAbsoluteFile());
			else
				createRealmFile(file);
			handler.setSecurityHandler(getSecurityHandler());
		}

		handler.setInitParameter("configuration","deployment");

		ServletHolder servletHolder = new ServletHolder(nl.clockwork.ebms.admin.web.ResourceServlet.class);
		handler.addServlet(servletHolder,"/css/*");
		handler.addServlet(servletHolder,"/fonts/*");
		handler.addServlet(servletHolder,"/images/*");
		handler.addServlet(servletHolder,"/js/*");

		handler.addServlet(org.eclipse.jetty.servlet.DefaultServlet.class,"/");

		if (cmd.hasOption("authentication") && cmd.hasOption("ssl") && cmd.hasOption("clientAuthentication"))
		{
			String clientTruststorePath = cmd.getOptionValue("clientTruststorePath");
			String clientTruststorePassword = cmd.getOptionValue("clientTruststorePassword");
			FilterHolder filterHolder = new FilterHolder(nl.clockwork.ebms.servlet.ClientCertificateAuthenticationFilter.class); 
			filterHolder.setInitParameter("truststorePath",clientTruststorePath);
			filterHolder.setInitParameter("truststorePassword",clientTruststorePassword);
			handler.addFilter(filterHolder,"/*",EnumSet.of(DispatcherType.REQUEST,DispatcherType.ERROR));
		}

		FilterHolder filterHolder = new FilterHolder(org.apache.wicket.protocol.http.WicketFilter.class); 
		filterHolder.setInitParameter("applicationClassName","nl.clockwork.ebms.admin.web.WicketApplication");
		filterHolder.setInitParameter("filterMappingUrlPattern","/*");
		handler.addFilter(filterHolder,"/*",EnumSet.of(DispatcherType.REQUEST,DispatcherType.ERROR));
		
		ErrorPageErrorHandler errorHandler = new ErrorPageErrorHandler();
		handler.setErrorHandler(errorHandler);
		Map<String,String> errorPages = new HashMap<>();
		errorPages.put("404","/404");
		errorHandler.setErrorPages(errorPages);
		
		handler.addEventListener(contextLoaderListener);
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
			result = toMD5(readLine("enter password: ",reader));
			String password = toMD5(readLine("re-enter password: ",reader));
			if (!result.equals(password))
				System.out.println("Passwords don't match! Try again.");
			else
				break;
		}
		return result;
	}
	
	private String toMD5(String s) throws NoSuchAlgorithmException, UnsupportedEncodingException
	{
		return "MD5:" + DigestUtils.md5Hex(s);
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
