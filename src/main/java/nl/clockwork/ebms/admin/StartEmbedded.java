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
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Properties;

import javax.servlet.DispatcherType;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.common.logging.LogUtils;
import org.eclipse.jetty.server.ConnectionLimit;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.hsqldb.persist.HsqlProperties;
import org.hsqldb.server.EbMSServerProperties;
import org.hsqldb.server.ServerAcl.AclFormatException;
import org.hsqldb.server.ServerConfiguration;
import org.hsqldb.server.ServerConstants;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import lombok.AccessLevel;
import lombok.val;
import lombok.experimental.FieldDefaults;
import nl.clockwork.ebms.admin.web.configuration.JdbcURL;

@FieldDefaults(level = AccessLevel.PROTECTED)
public class StartEmbedded extends Start
{
	public static void main(String[] args) throws Exception
	{
		LogUtils.setLoggerClass(org.apache.cxf.common.logging.Slf4jLogger.class);
		val options = createOptions();
		val cmd = new DefaultParser().parse(options,args);
		if (cmd.hasOption("h"))
			printUsage(options);
		System.setProperty("org.apache.activemq.SERIALIZABLE_PACKAGES","*");
		if (cmd.hasOption("disableEbMSClient"))
			System.setProperty("eventProcessor.start","false");
		val start = new StartEmbedded();
		start.init(cmd);
		start.server.setHandler(start.handlerCollection);
		val properties = getProperties();
		start.startHSQLDB(cmd,properties);
		if (cmd.hasOption("jmx"))
			start.initJMX(cmd,start.server);
		if (cmd.hasOption("soap") || cmd.hasOption("health") || !cmd.hasOption("headless") || !cmd.hasOption("disableEbMSServer"))
			try (val context = new AnnotationConfigWebApplicationContext())
			{
				context.register(EmbeddedAppConfig.class);
				getConfigClasses().forEach(c -> context.register(c));
				val contextLoaderListener = new ContextLoaderListener(context);
				if (cmd.hasOption("soap") || !cmd.hasOption("headless"))
				{
					start.initWebServer(cmd,start.server);
					start.handlerCollection.addHandler(start.createWebContextHandler(cmd,contextLoaderListener));
				}
				if (cmd.hasOption("health"))
				{
					start.initHealthServer(cmd,start.server);
					start.handlerCollection.addHandler(start.createHealthContextHandler(cmd,contextLoaderListener));
				}
				if (!cmd.hasOption("disableEbMSServer"))
				{
					start.initEbMSServer(properties,start.server);
					start.handlerCollection.addHandler(start.createEbMSContextHandler(properties,contextLoaderListener));
				}
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
		else
			try (val context = new AnnotationConfigApplicationContext())
			{
				context.register(EmbeddedAppConfig.class);
				getConfigClasses().forEach(c -> context.register(c));
				System.out.println("Starting server...");
				context.refresh();
				context.start();
				System.out.println("Server started.");
				Thread.currentThread().join();
			}
	}

	protected static Options createOptions()
	{
		val result = Start.createOptions();
		result.addOption("hsqldb",false,"start hsqldb server");
		result.addOption("hsqldbDir",true,"set hsqldb location (default: hsqldb)");
		result.addOption("disableEbMSServer",false,"disable ebms server");
		result.addOption("disableEbMSClient",false,"disable ebms client");
		return result;
	}
	
	private static Properties getProperties() throws IOException
	{
		return EmbeddedAppConfig.PROPERTY_SOURCE.getProperties();
	}

	private void startHSQLDB(CommandLine cmd, Properties properties) throws IOException, AclFormatException, URISyntaxException, ParseException
	{
		val jdbcURL = getHsqlDbJdbcUrl(cmd,properties);
		if (jdbcURL != null)
		{
			System.setProperty("ebms.jdbc.update","true");
			System.out.println("Starting hsqldb...");
			startHSQLDBServer(cmd,jdbcURL);
		}
	}

	private JdbcURL getHsqlDbJdbcUrl(CommandLine cmd, Properties properties) throws IOException, AclFormatException, URISyntaxException, ParseException
	{
		JdbcURL result = null;
		if (properties.getProperty("ebms.jdbc.driverClassName").startsWith("org.hsqldb.jdbc") && cmd.hasOption("hsqldb"))
		{
			result = nl.clockwork.ebms.admin.web.configuration.Utils.parseJdbcURL(properties.getProperty("ebms.jdbc.url"),new JdbcURL());
			if (!result.getHost().matches("(localhost|127.0.0.1)"))
			{
				System.out.println("Cannot start server on " + result.getHost());
				System.exit(1);
			}
		}
		return result;
	}

	public org.hsqldb.server.Server startHSQLDBServer(CommandLine cmd, JdbcURL jdbcURL) throws IOException, AclFormatException, URISyntaxException, ParseException
	{
		val options = new ArrayList<>();
		options.add("-database.0");
		options.add((cmd.hasOption("hsqldbDir") ? "file:" + cmd.getOptionValue("hsqldbDir") : "file:hsqldb") + "/" + jdbcURL.getDatabase());
		options.add("-dbname.0");
		options.add(jdbcURL.getDatabase());
		if (jdbcURL.getPort() != null)
		{
			options.add("-port");
			options.add(jdbcURL.getPort().toString());
		}
		val argProps = HsqlProperties.argArrayToProps(options.toArray(new String[0]),"server");
		val props = new EbMSServerProperties(ServerConstants.SC_PROTOCOL_HSQL);
		props.addProperties(argProps);
		ServerConfiguration.translateDefaultDatabaseProperty(props);
		ServerConfiguration.translateDefaultNoSystemExitProperty(props);
		ServerConfiguration.translateAddressProperty(props);
		val server = new org.hsqldb.server.Server();
		server.setProperties(props);
		server.start();
		return server;
	}

	private void initEbMSServer(Properties properties, Server server) throws MalformedURLException, IOException
	{
		
		val connector = "true".equals(properties.getProperty("ebms.ssl")) ? 
				createEbMSHttpsConnector(properties,createEbMSSslContextFactory(properties)) : 
					createEbMSHttpConnector(properties);
		server.addConnector(connector);
		val connectionLimit = properties.getProperty("ebms.connectionLimit");
		if (StringUtils.isNotEmpty(connectionLimit))
			server.addBean(new ConnectionLimit(Integer.parseInt(connectionLimit),connector));
	}

	private ServerConnector createEbMSHttpConnector(Properties properties)
	{
		val httpConfig = new HttpConfiguration();
		httpConfig.setSendServerVersion(false);
		val result = new ServerConnector(this.server,new HttpConnectionFactory(httpConfig));
		result.setHost(StringUtils.isEmpty(properties.getProperty("ebms.host")) ? "0.0.0.0" : properties.getProperty("ebms.host"));
		result.setPort(StringUtils.isEmpty(properties.getProperty("ebms.port"))  ? 8888 : Integer.parseInt(properties.getProperty("ebms.port")));
		result.setName("ebms");
		System.out.println("EbMS service configured on http://" + Utils.getHost(result.getHost()) + ":" + result.getPort() + properties.getProperty("ebms.path"));
		return result;
	}

	private SslContextFactory.Server createEbMSSslContextFactory(Properties properties) throws MalformedURLException, IOException
	{
		val result = new SslContextFactory.Server();
		addEbMSKeyStore(properties,result);
		if ("true".equals(properties.getProperty("https.requireClientAuthentication")))
			addEbMSTrustStore(properties,result);
		result.setExcludeCipherSuites();
		return result;
	}

	private void addEbMSKeyStore(Properties properties, SslContextFactory.Server sslContextFactory) throws MalformedURLException, IOException
	{
		val keyStore = getResource(properties.getProperty("keystore.path"));
		if (keyStore != null && keyStore.exists())
		{
			if (!StringUtils.isEmpty(properties.getProperty("https.protocols")))
				sslContextFactory.setIncludeProtocols(StringUtils.stripAll(StringUtils.split(properties.getProperty("https.protocols"),',')));
			if (!StringUtils.isEmpty(properties.getProperty("https.cipherSuites")))
				sslContextFactory.setIncludeCipherSuites(StringUtils.stripAll(StringUtils.split(properties.getProperty("https.cipherSuites"),',')));
			sslContextFactory.setKeyStoreType(properties.getProperty("keystore.type"));
			sslContextFactory.setKeyStoreResource(keyStore);
			sslContextFactory.setKeyStorePassword(properties.getProperty("keystore.password"));
			String certAlias = properties.getProperty("keystore.defaultAlias");
			if (StringUtils.isNotEmpty(certAlias))
				sslContextFactory.setCertAlias(certAlias);
		}
		else
		{
			System.out.println("EbMS service not available: keyStore " + properties.getProperty("keystore.path") + " not found!");
			System.exit(1);
		}
	}

	private void addEbMSTrustStore(Properties properties, SslContextFactory.Server sslContextFactory) throws MalformedURLException, IOException
	{
		val trustStore = getResource(properties.getProperty("truststore.path"));
		if (trustStore != null && trustStore.exists())
		{
			sslContextFactory.setNeedClientAuth(true);
			sslContextFactory.setTrustStoreType(properties.getProperty("truststore.type"));
			sslContextFactory.setTrustStoreResource(trustStore);
			sslContextFactory.setTrustStorePassword(properties.getProperty("truststore.password"));
		}
		else
		{
			System.out.println("EbMS service not available: trustStore " + properties.getProperty("truststore.path") + " not found!");
			System.exit(1);
		}
	}

	private ServerConnector createEbMSHttpsConnector(Properties properties, SslContextFactory.Server sslContextFactory)
	{
		val httpConfig = new HttpConfiguration();
		httpConfig.setSendServerVersion(false);
		val result = new ServerConnector(this.server,sslContextFactory,new HttpConnectionFactory(httpConfig));
		result.setHost(StringUtils.isEmpty(properties.getProperty("ebms.host")) ? "0.0.0.0" : properties.getProperty("ebms.host"));
		result.setPort(StringUtils.isEmpty(properties.getProperty("ebms.port"))  ? 8888 : Integer.parseInt(properties.getProperty("ebms.port")));
		result.setName("ebms");
		System.out.println("EbMS service configured on https://" + Utils.getHost(result.getHost()) + ":" + result.getPort() + properties.getProperty("ebms.path"));
		return result;
	}

	protected ServletContextHandler createEbMSContextHandler(Properties properties, ContextLoaderListener contextLoaderListener) throws IOException, NoSuchAlgorithmException
	{
		val result = new ServletContextHandler(ServletContextHandler.SESSIONS);
		result.setVirtualHosts(new String[] {"@ebms"});
		result.setContextPath("/");
		if (!StringUtils.isEmpty(properties.getProperty("ebms.queriesPerSecond")))
			result.addFilter(createRateLimiterFilterHolder(properties.getProperty("ebms.queriesPerSecond")),"/*",EnumSet.allOf(DispatcherType.class));
		if (!StringUtils.isEmpty(properties.getProperty("ebms.userQueriesPerSecond")))
			result.addFilter(createUserRateLimiterFilterHolder(properties.getProperty("ebms.userQueriesPerSecond")),"/*",EnumSet.allOf(DispatcherType.class));
		if ("true".equals(properties.getProperty("https.clientCertificateAuthentication").toLowerCase()))
			result.addFilter(createClientCertificateManagerFilterHolder(properties.getProperty("https.clientCertificateHeader")),"/*",EnumSet.allOf(DispatcherType.class));
		result.addServlet(nl.clockwork.ebms.server.servlet.EbMSServlet.class,properties.getProperty("ebms.path"));
		result.addEventListener(contextLoaderListener);
		return result;
	}
}
