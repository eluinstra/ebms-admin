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
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import javax.servlet.DispatcherType;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.FilterHolder;
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
		start.options = start.createOptions();
		start.cmd = new DefaultParser().parse(start.options,args);

		if (start.cmd.hasOption("h"))
			start.printUsage();

		start.server.setHandler(start.handlerCollection);

		start.properties = start.getProperties("nl/clockwork/ebms/admin/applicationConfig.embedded.xml");

		start.initHSQLDB(start.cmd,start.properties);
		start.initWebServer(start.cmd,start.server);
		start.initEbMSServer(start.properties,start.server);
		start.initJMX(start.cmd,start.server);

		XmlWebApplicationContext context = new XmlWebApplicationContext();
		context.setConfigLocations(getConfigLocations("classpath:nl/clockwork/ebms/admin/applicationContext.embedded.xml"));
		ContextLoaderListener contextLoaderListener = new ContextLoaderListener(context);
		if (start.cmd.hasOption("soap") || !start.cmd.hasOption("headless"))
			start.handlerCollection.addHandler(start.createWebContextHandler(start.cmd,contextLoaderListener));
		start.handlerCollection.addHandler(start.createEbMSContextHandler(start.properties,contextLoaderListener));

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
		start.server.join();
	}

	@Override
	protected Options createOptions()
	{
		Options result = super.createOptions();
		result.addOption("hsqldb",false,"start hsqldb server");
		result.addOption("hsqldbDir",true,"set hsqldb location (default: hsqldb)");
		result.addOption("soap",false,"start soap service");
		result.addOption("headless",false,"start without web interface");
		return result;
	}
	
	private Map<String,String> getProperties(String...files)
	{
		try (AbstractApplicationContext applicationContext = new ClassPathXmlApplicationContext(files))
		{
			PropertyPlaceholderConfigurer properties = (PropertyPlaceholderConfigurer)applicationContext.getBean("propertyConfigurer");
			return properties.getProperties();
		}
	}

	private void initHSQLDB(CommandLine cmd, Map<String,String> properties) throws IOException, AclFormatException, URISyntaxException
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
			startHSQLDBServer(cmd,jdbcURL);
		}
	}

	public void startHSQLDBServer(CommandLine cmd, JdbcURL jdbcURL) throws IOException, AclFormatException, URISyntaxException
	{
		List<String> options = new ArrayList<>();
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
				c.createStatement().executeUpdate(IOUtils.toString(this.getClass().getResourceAsStream("/nl/clockwork/ebms/admin/database/hsqldb.sql"),Charset.defaultCharset()));
				System.out.println("EbMS tables created");
			}
			else
				System.out.println("EbMS tables already exist");
			ExtensionProvider.get().stream()
					.filter(p -> StringUtils.isNotEmpty(p.getHSQLDBFile()))
					.forEach(p -> executeSQLFile(c,p));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void executeSQLFile(Connection c, ExtensionProvider provider)
	{
		try
		{
			c.createStatement().executeUpdate(IOUtils.toString(this.getClass().getResourceAsStream(provider.getHSQLDBFile()),Charset.defaultCharset()));
			System.out.println(provider.getName() + " tables created");
		}
		catch (Exception e)
		{
			if (e.getMessage().contains("already exists"))
				System.out.println(provider.getName() + " tables already exist");
			else
				e.printStackTrace();
		}
	}

	private void initEbMSServer(Map<String,String> properties, Server server) throws MalformedURLException, IOException
	{
		if (!"true".equals(properties.get("ebms.ssl")))
		{
			server.addConnector(createEbMSHttpConnector(properties));
		}
		else
		{
			SslContextFactory factory = createEbMSSslContextFactory(properties);
			server.addConnector(createEbMSHttpsConnector(properties,factory));
		}
	}

	private ServerConnector createEbMSHttpConnector(Map<String,String> properties)
	{
		ServerConnector result = new ServerConnector(this.server);
		result.setHost(StringUtils.isEmpty(properties.get("ebms.host")) ? "0.0.0.0" : properties.get("ebms.host"));
		result.setPort(StringUtils.isEmpty(properties.get("ebms.port"))  ? 8888 : Integer.parseInt(properties.get("ebms.port")));
		result.setName("ebms");
		System.out.println("EbMS service configured on http://" + getHost(result.getHost()) + ":" + result.getPort() + properties.get("ebms.path"));
		return result;
	}

	private SslContextFactory createEbMSSslContextFactory(Map<String,String> properties) throws MalformedURLException, IOException
	{
		SslContextFactory result = new SslContextFactory();
		addEbMSKeyStore(properties,result);
		if ("true".equals(properties.get("https.requireClientAuthentication")))
			addEbMSTrustStore(properties,result);
		return result;
	}

	private void addEbMSKeyStore(Map<String,String> properties, SslContextFactory sslContextFactory) throws MalformedURLException, IOException
	{
		Resource keyStore = getResource(properties.get("keystore.path"));
		if (keyStore != null && keyStore.exists())
		{
			if (!StringUtils.isEmpty(properties.get("https.protocols")))
				sslContextFactory.setIncludeProtocols(StringUtils.stripAll(StringUtils.split(properties.get("https.protocols"),',')));
			if (!StringUtils.isEmpty(properties.get("https.cipherSuites")))
				sslContextFactory.setIncludeCipherSuites(StringUtils.stripAll(StringUtils.split(properties.get("https.cipherSuites"),',')));
			sslContextFactory.setKeyStoreType(properties.get("keystore.type"));
			sslContextFactory.setKeyStoreResource(keyStore);
			sslContextFactory.setKeyStorePassword(properties.get("keystore.password"));
		}
		else
		{
			System.out.println("EbMS service not available: keyStore " + properties.get("keystore.path") + " not found!");
			System.exit(1);
		}
	}

	private void addEbMSTrustStore(Map<String,String> properties, SslContextFactory sslContextFactory) throws MalformedURLException, IOException
	{
		Resource trustStore = getResource(properties.get("truststore.path"));
		if (trustStore != null && trustStore.exists())
		{
			sslContextFactory.setNeedClientAuth(true);
			sslContextFactory.setTrustStoreType(properties.get("truststore.type"));
			sslContextFactory.setTrustStoreResource(trustStore);
			sslContextFactory.setTrustStorePassword(properties.get("truststore.password"));
		}
		else
		{
			System.out.println("EbMS service not available: trustStore " + properties.get("truststore.path") + " not found!");
			System.exit(1);
		}
	}

	private ServerConnector createEbMSHttpsConnector(Map<String,String> properties, SslContextFactory factory)
	{
		ServerConnector result = new ServerConnector(this.server,factory);
		result.setHost(StringUtils.isEmpty(properties.get("ebms.host")) ? "0.0.0.0" : properties.get("ebms.host"));
		result.setPort(StringUtils.isEmpty(properties.get("ebms.port"))  ? 8888 : Integer.parseInt(properties.get("ebms.port")));
		result.setName("ebms");
		System.out.println("EbMS service configured on https://" + result.getHost() + ":" + result.getPort() + properties.get("ebms.path"));
		return result;
	}

	protected ServletContextHandler createEbMSContextHandler(Map<String,String> properties, ContextLoaderListener contextLoaderListener) throws IOException, NoSuchAlgorithmException
	{
		ServletContextHandler result = new ServletContextHandler(ServletContextHandler.SESSIONS);
		result.setVirtualHosts(new String[] {"@ebms"});

		result.setContextPath("/");

		if ("true".equals(properties.get("https.clientCertificateAuthentication").toLowerCase()))
			result.addFilter(createClientCertificateManagerFilterHolder(properties),"/*",EnumSet.allOf(DispatcherType.class));

		result.addServlet(nl.clockwork.ebms.servlet.EbMSServlet.class,properties.get("ebms.path"));

		result.addServlet(org.eclipse.jetty.servlet.DefaultServlet.class,"/");
		
		result.addEventListener(contextLoaderListener);
		
		return result;
	}

	private FilterHolder createClientCertificateManagerFilterHolder(Map<String,String> properties)
	{
		FilterHolder result = new FilterHolder(nl.clockwork.ebms.servlet.ClientCertificateManagerFilter.class); 
		result.setInitParameter("x509CertificateHeader",properties.get("https.clientCertificateHeader"));
		return result;
	}

}
