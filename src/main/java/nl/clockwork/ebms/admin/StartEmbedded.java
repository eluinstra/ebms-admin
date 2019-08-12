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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.Options;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
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
import org.springframework.web.context.support.XmlWebApplicationContext;

import nl.clockwork.ebms.admin.web.ExtensionProvider;
import nl.clockwork.ebms.admin.web.configuration.JdbcURL;

public class StartEmbedded extends Start
{
	protected Map<String,String> properties;

	public static void main(String[] args) throws Exception
	{
		StartEmbedded start = new StartEmbedded();
		start.initCmd(args);

		if (start.cmd.hasOption("h"))
			start.printUsage();

		start.server.setHandler(start.handlerCollection);

		start.properties = start.getProperties("nl/clockwork/ebms/admin/applicationConfig.embedded.xml");

		System.setProperty("javax.net.ssl.trustStore","");
		if (!StringUtils.isEmpty(start.properties.get("https.protocols")))
			System.setProperty("https.protocols",start.properties.get("https.protocols"));
		if (!StringUtils.isEmpty(start.properties.get("https.cipherSuites")))
			System.setProperty("https.cipherSuites",start.properties.get("https.cipherSuites"));

		start.initHSQLDB();
		start.initWebServer();
		start.initEbMSServer();
		start.initJMX();
		XmlWebApplicationContext context = new XmlWebApplicationContext();
		context.setConfigLocations(getConfigLocations("classpath:nl/clockwork/ebms/admin/applicationContext.embedded.xml"));
		ContextLoaderListener contextLoaderListener = new ContextLoaderListener(context);
		start.initWebContext(contextLoaderListener);
		start.initEbMSContext(contextLoaderListener);

		System.out.println("Starting web server...");

		start.server.start();
		start.server.join();
	}

	@Override
	protected Options createOptions()
	{
		options = new Options();
		options.addOption("h",false,"print this message");
		options.addOption("host",true,"set host");
		options.addOption("port",true,"set port");
		options.addOption("ssl",false,"use ssl");
		options.addOption("keystore",true,"set keystore");
		options.addOption("password",true,"set keystore password");
		options.addOption("authentication",false,"use basic authentication");
		options.addOption("jmx",false,"start mbean server");
		options.addOption("hsqldb",false,"start hsqldb server");
		options.addOption("hsqldbDir",true,"set hsqldb location (default: hsqldb)");
		options.addOption("soap",false,"start soap service");
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
				System.out.println("EbMS tables created");
			}
			else
				System.out.println("EbMS tables already exist");
			for (ExtensionProvider extensionProvider : ExtensionProvider.get())
				if (!StringUtils.isEmpty(extensionProvider.getHSQLDBFile()))
					try
					{
						c.createStatement().executeUpdate(IOUtils.toString(this.getClass().getResourceAsStream(extensionProvider.getHSQLDBFile())));
						System.out.println(extensionProvider.getName() + " tables created");
					}
					catch (Exception e)
					{
						if (e.getMessage().contains("already exists"))
							System.out.println(extensionProvider.getName() + " tables already exist");
						else
							e.printStackTrace();
					}
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
			ServerConnector connector = new ServerConnector(this.server);
			connector.setHost(StringUtils.isEmpty(properties.get("ebms.host")) ? "0.0.0.0" : properties.get("ebms.host"));
			connector.setPort(StringUtils.isEmpty(properties.get("ebms.port"))  ? 8888 : Integer.parseInt(properties.get("ebms.port")));
			connector.setName("ebms");
			server.addConnector(connector);
			System.out.println("EbMS service configured on http://" + getHost(connector.getHost()) + ":" + connector.getPort() + properties.get("ebms.path"));
		}
		else
		{
			Resource keystore = getResource(properties.get("keystore.path"));
			if (keystore != null && keystore.exists())
			{
				SslContextFactory factory = new SslContextFactory();
				if (!StringUtils.isEmpty(properties.get("https.protocols")))
					factory.setIncludeProtocols(StringUtils.stripAll(StringUtils.split(properties.get("https.protocols"),',')));
				if (!StringUtils.isEmpty(properties.get("https.cipherSuites")))
					factory.setIncludeCipherSuites(StringUtils.stripAll(StringUtils.split(properties.get("https.cipherSuites"),',')));
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
				ServerConnector connector = new ServerConnector(this.server,factory);
				connector.setHost(StringUtils.isEmpty(properties.get("ebms.host")) ? "0.0.0.0" : properties.get("ebms.host"));
				connector.setPort(StringUtils.isEmpty(properties.get("ebms.port"))  ? 8888 : Integer.parseInt(properties.get("ebms.port")));
				connector.setName("ebms");
				server.addConnector(connector);
				System.out.println("EbMS service configured on https://" + connector.getHost() + ":" + connector.getPort() + properties.get("ebms.path"));
			}
			else
			{
				System.out.println("EbMS service not available: keystore " + properties.get("keystore.path") + " not found!");
				System.exit(1);
			}
		}
	}

	protected void initEbMSContext(ContextLoaderListener contextLoaderListener) throws IOException, NoSuchAlgorithmException
	{
		ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		handler.setVirtualHosts(new String[] {"@ebms"});
		handlerCollection.addHandler(handler);

		handler.setContextPath("/");

		handler.addServlet(nl.clockwork.ebms.servlet.EbMSServlet.class,properties.get("ebms.path"));

		handler.addServlet(org.eclipse.jetty.servlet.DefaultServlet.class,"/");
		
		handler.addEventListener(contextLoaderListener);
	}

}
