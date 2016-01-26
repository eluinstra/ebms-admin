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
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.clockwork.ebms.admin.web.ExtensionPageProvider;
import nl.clockwork.ebms.admin.web.configuration.JdbcURL;

import org.apache.commons.cli.Options;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.DispatcherType;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.server.ssl.SslSocketConnector;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.hsqldb.persist.HsqlProperties;
import org.hsqldb.server.EbMSServerProperties;
import org.hsqldb.server.ServerAcl.AclFormatException;
import org.hsqldb.server.ServerConfiguration;
import org.hsqldb.server.ServerConstants;
import org.hsqldb.server.ServerProperties;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.ContextLoaderListener;

public class StartEmbedded extends Start
{
	protected Map<String,String> properties;

	public static void main(String[] args) throws Exception
	{
		StartEmbedded start = new StartEmbedded();
		start.initCmd(args);

		if (start.cmd.hasOption("h"))
			start.printUsage();

		start.properties = start.getProperties("nl/clockwork/ebms/admin/applicationConfig.embedded.xml");
		start.server = new Server();

		start.initHSQLDB();
		start.initWebServer();
		start.initEbMSServer();
		start.initJMX();
		start.initWebContext();

		System.out.println("Starting web server...");

		start.server.start();
		start.server.join();
	}

	@Override
	protected Options createOptions()
	{
		options = new Options();
		options.addOption("h",false,"print this message");
		options.addOption("p",true,"set port");
		options.addOption("ssl",false,"use ssl");
		options.addOption("keystore",true,"set keystore");
		options.addOption("password",true,"set keystore password");
		options.addOption("authentication",false,"use basic authentication");
		options.addOption("jmx",false,"start mbean server");
		options.addOption("hsqldb",false,"start hsqldb server");
		options.addOption("hsqldbDir",true,"set hsqldb location (default: hsqldb)");
		return options;
	}
	
	private Map<String,String> getProperties(String...files)
	{
		try (AbstractApplicationContext applicationContext = new ClassPathXmlApplicationContext(files))
		{
			PropertyPlaceholderConfigurer properties = (PropertyPlaceholderConfigurer)applicationContext.getBean("propertyConfigurer");
			return properties.getProperties();
		}
	}

	private void initHSQLDB() throws IOException, AclFormatException, URISyntaxException
	{
		if ("org.hsqldb.jdbcDriver".equals(properties.get("ebms.jdbc.driverClassName")) && cmd.hasOption("hsqldb"))
		{
			JdbcURL jdbcURL = nl.clockwork.ebms.admin.web.configuration.Utils.parseJdbcURL(properties.get("ebms.jdbc.url"),new JdbcURL());
			if (!jdbcURL.getHost().matches("(localhost|127.0.0.1)"))
			{
				System.out.println("Cannot start server on " + jdbcURL.getHost());
				System.exit(1);
			}
			System.out.println("Starting hsqldb...");
			startHSQLDBServer(jdbcURL);
		}
	}

	public void startHSQLDBServer(JdbcURL jdbcURL) throws IOException, AclFormatException, URISyntaxException
	{
		List<String> options = new ArrayList<String>();
		options.add("-database.0");
		options.add((cmd.hasOption("hsqldbDir") ? "file:" + cmd.getOptionValue("hsqldbDir") : "file:hsqldb") + "/" + jdbcURL.getDatabase());
		options.add("-dbname.0");
		options.add(jdbcURL.getDatabase());
		if (jdbcURL.getPort() != null)
		{
			options.add("-port");
			options.add(jdbcURL.getPort().toString());
		}
		HsqlProperties argProps = HsqlProperties.argArrayToProps(options.toArray(new String[0]),"server");
		ServerProperties props = new EbMSServerProperties(ServerConstants.SC_PROTOCOL_HSQL);
		props.addProperties(argProps);
		ServerConfiguration.translateDefaultDatabaseProperty(props);
		ServerConfiguration.translateDefaultNoSystemExitProperty(props);
		ServerConfiguration.translateAddressProperty(props);
		org.hsqldb.server.Server server = new org.hsqldb.server.Server();
		server.setProperties(props);
		server.start();
		initDatabase(server);
	}

	private void initDatabase(org.hsqldb.server.Server server)
	{
    try (Connection c = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost:" + server.getPort() + "/" + server.getDatabaseName(0,true), "sa", ""))
		{
			if (!c.createStatement().executeQuery("select table_name from information_schema.tables where table_name = 'CPA'").next())
			{
				c.createStatement().executeUpdate(IOUtils.toString(this.getClass().getResourceAsStream("/nl/clockwork/ebms/admin/database/hsqldb.sql")));
				for (ExtensionPageProvider extensionPageProvider : ExtensionPageProvider.get())
					if (StringUtils.isEmpty(extensionPageProvider.getHSQLDBFile()))
						c.createStatement().executeUpdate(IOUtils.toString(this.getClass().getResourceAsStream(extensionPageProvider.getHSQLDBFile())));
				System.out.println("EbMS tables created");
			}
			else
				System.out.println("EbMS tables already exist");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void initEbMSServer() throws MalformedURLException, IOException
	{
		if (!"true".equals(properties.get("ebms.ssl")))
		{
			SocketConnector connector = new SocketConnector();
			connector.setPort(StringUtils.isEmpty(properties.get("ebms.port"))  ? 8888 : Integer.parseInt(properties.get("ebms.port")));
			server.addConnector(connector);
			System.out.println("EbMS service configured on http://localhost:" + connector.getPort() + properties.get("ebms.path"));
		}
		else
		{
			Resource keystore = getResource(properties.get("keystore.path"));
			if (keystore != null && keystore.exists())
			{
				SslContextFactory factory = new SslContextFactory();
				if (!StringUtils.isEmpty(properties.get("https.allowedCipherSuites")))
					factory.setIncludeCipherSuites(StringUtils.stripAll(StringUtils.split(properties.get("https.allowedCipherSuites"),',')));
				factory.setKeyStoreResource(keystore);
				factory.setKeyStorePassword(properties.get("keystore.password"));
				if ("true".equals(properties.get("https.requireClientAuthentication")))
				{
					Resource truststore = getResource(properties.get("truststore.path"));
					if (truststore != null && truststore.exists())
					{
						factory.setNeedClientAuth(true);
						factory.setTrustStoreResource(truststore);
						factory.setTrustStorePassword(properties.get("truststore.password"));
					}
					else
					{
						System.out.println("EbMS service not available: truststore " + properties.get("truststore.path") + " not found!");
						System.exit(1);
					}
				}
				SslSocketConnector connector = new SslSocketConnector(factory);
				connector.setPort(StringUtils.isEmpty(properties.get("ebms.port"))  ? 8888 : Integer.parseInt(properties.get("ebms.port")));
				server.addConnector(connector);
				System.out.println("EbMS service configured on https://localhost:" + connector.getPort() + properties.get("ebms.path"));
			}
			else
			{
				System.out.println("EbMS service not available: keystore " + properties.get("keystore.path") + " not found!");
				System.exit(1);
			}
		}
	}

	@Override
	protected void initWebContext() throws IOException, NoSuchAlgorithmException
	{
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		server.setHandler(context);

		context.setContextPath("/");

		if (cmd.hasOption("authentication"))
		{
			System.out.println("Configuring web server authentication:");
			File file = new File(REALM_FILE);
			if (file.exists())
				System.out.println("Using file: " + file.getAbsoluteFile());
			else
				createRealmFile(file);
			context.setSecurityHandler(getSecurityHandler());
		}

		context.setInitParameter("configuration","deployment");

		String contextConfigLocation = "classpath:nl/clockwork/ebms/admin/applicationContext.embedded.xml";
		for (ExtensionPageProvider extensionPageProvider : ExtensionPageProvider.get())
			if (!StringUtils.isEmpty(extensionPageProvider.getSpringConfigurationFile()))
				contextConfigLocation = "," + extensionPageProvider.getSpringConfigurationFile();

		context.setInitParameter("contextConfigLocation",contextConfigLocation);

		ServletHolder servletHolder = new ServletHolder(nl.clockwork.ebms.admin.web.ResourceServlet.class);
		context.addServlet(servletHolder,"/css/*");
		context.addServlet(servletHolder,"/fonts/*");
		context.addServlet(servletHolder,"/images/*");
		context.addServlet(servletHolder,"/js/*");

		context.addServlet(nl.clockwork.ebms.servlet.EbMSServlet.class,properties.get("ebms.path"));

		context.addServlet(org.eclipse.jetty.servlet.DefaultServlet.class,"/");
		
		FilterHolder filterHolder = new FilterHolder(org.apache.wicket.protocol.http.WicketFilter.class); 
		filterHolder.setInitParameter("applicationClassName","nl.clockwork.ebms.admin.web.WicketApplication");
		filterHolder.setInitParameter("filterMappingUrlPattern","/*");
		context.addFilter(filterHolder,"/*",EnumSet.of(DispatcherType.REQUEST,DispatcherType.ERROR));
		
		ErrorPageErrorHandler errorHandler = new ErrorPageErrorHandler();
		context.setErrorHandler(errorHandler);
		Map<String,String> errorPages = new HashMap<String,String>();
		errorPages.put("404","/404");
		errorHandler.setErrorPages(errorPages);
		
		ContextLoaderListener listener = new ContextLoaderListener();
		context.addEventListener(listener);
	}

}
