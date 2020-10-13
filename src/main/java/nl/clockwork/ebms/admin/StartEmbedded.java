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
import nl.clockwork.ebms.server.servlet.EbMSServlet;
import nl.clockwork.ebms.util.LoggingUtils;

@FieldDefaults(level = AccessLevel.PROTECTED)
public class StartEmbedded extends Start
{
	private static final String DELIVERY_TASK_HANDLER_START_PROPERTY = "deliveryTaskHandler.start";
	private static final String HSQLDB_OPTION = "hsqldb";
	private static final String HSQLDB_DIR_OPTION = "hsqldbDir";
	private static final String DISABLE_EBMS_SERVER_OPTION = "disableEbMSServer";
	private static final String DISABLE_EBMS_CLIENT_OPTION = "disableEbMSClient";

	private static final String EBMS_HOST_PROPERTY = "ebms.host";
	private static final String EBMS_PORT_PROPERTY = "ebms.port";
	private static final String EBMS_PATH_PROPERTY = "ebms.path";
	private static final String EBMS_CONNECTION_LIMIT_PROPERTY = "ebms.connectionLimit";
	private static final String EBMS_USER_QUERIES_PER_SECOND_PROPERTY = "ebms.userQueriesPerSecond";
	private static final String EBMS_QUERIES_PER_SECOND_PROPERTY = "ebms.queriesPerSecond";
	private static final String EBMS_SSL_PROPERTY = "ebms.ssl";
	private static final String HTTPS_PROTOCOLS_PROPERTY = "https.protocols";
	private static final String HTTPS_CIPHER_SUITES_PROPERTY = "https.cipherSuites";
	private static final String HTTPS_REQUIRE_CLIENT_AUTHENTICATION_PROPERTY = "https.requireClientAuthentication";
	private static final String HTTPS_CLIENT_CERTIFICATE_HEADER_PROPERTY = "https.clientCertificateHeader";
	private static final String HTTPS_CLIENT_CERTIFICATE_AUTHENTICATION_PROPERTY = "https.clientCertificateAuthentication";
	private static final String KEYSTORE_TYPE_PROPERTY = "keystore.type";
	private static final String KEYSTORE_PATH_PROPERTY = "keystore.path";
	private static final String KEYSTORE_PASSWORD_PROPERTY = "keystore.password";
	private static final String KEYSTORE_DEFAULT_ALIAS_PROPERTY = "keystore.defaultAlias";
	private static final String TRUSTSTORE_TYPE_PROPERTY = "truststore.type";
	private static final String TRUSTSTORE_PATH_PROPERTY = "truststore.path";
	private static final String TRUSTSTORE_PASSWORD_PROPERTY = "truststore.password";
	private static final String EBMS_JDBC_DRIVER_CLASS_NAME_PROPERTY = "ebms.jdbc.driverClassName";
	private static final String EBMS_JDBC_UPDATE_PROPERTY = "ebms.jdbc.update";
	private static final String EBMS_JDBC_URL_PROPERTY = "ebms.jdbc.url";
	private static final String LOGGING_MDC_PROPERTY = "logging.mdc";
	private static final String LOGGING_MDC_AUDIT_PROPERTY = "logging.mdc.audit";

	private static final String TRUE = "true";
	private static final String FALSE = "false";
	private static final String DEFAULT_EBMS_PORT = "8888";
	private static final String DEFAULT_HSQLDB_DIR = "hsqldb";
	private static final String EBMS_CONNECTOR_NAME = "ebms";

	public static void main(String[] args) throws Exception
	{
		LogUtils.setLoggerClass(org.apache.cxf.common.logging.Slf4jLogger.class);
		val app = new StartEmbedded();
		app.startService(args);
	}

	private void startService(String[] args) throws ParseException, IOException, AclFormatException, URISyntaxException, Exception, MalformedURLException, NoSuchAlgorithmException, InterruptedException
	{
		val options = createOptions();
		val cmd = new DefaultParser().parse(options,args);
		if (cmd.hasOption(HELP_OPTION))
			printUsage(options);
		setProperty("org.apache.activemq.SERIALIZABLE_PACKAGES","*");
		if (cmd.hasOption(DISABLE_EBMS_CLIENT_OPTION))
			setProperty(DELIVERY_TASK_HANDLER_START_PROPERTY,FALSE);
		init(cmd);
		server.setHandler(handlerCollection);
		server.addBean(new CustomErrorHandler());
		val properties = getProperties();
		startHSQLDB(cmd,properties);
		if (cmd.hasOption(JMX_OPTION))
			initJMX(cmd,server);
		if (cmd.hasOption(SOAP_OPTION) || cmd.hasOption(HEALTH_OPTION) || !cmd.hasOption(HEADLESS_OPTION) || !cmd.hasOption(DISABLE_EBMS_SERVER_OPTION))
			try (val context = new AnnotationConfigWebApplicationContext())
			{
				context.register(EmbeddedAppConfig.class);
				getConfigClasses().forEach(c -> context.register(c));
				val contextLoaderListener = new ContextLoaderListener(context);
				if (cmd.hasOption(SOAP_OPTION) || !cmd.hasOption(HEADLESS_OPTION))
				{
					initWebServer(cmd,server);
					handlerCollection.addHandler(createWebContextHandler(cmd,contextLoaderListener));
				}
				if (cmd.hasOption(HEALTH_OPTION))
				{
					initHealthServer(cmd,server);
					handlerCollection.addHandler(createHealthContextHandler(cmd,contextLoaderListener));
				}
				if (!cmd.hasOption(DISABLE_EBMS_SERVER_OPTION))
				{
					initEbMSServer(properties,server);
					handlerCollection.addHandler(createEbMSContextHandler(properties,contextLoaderListener));
				}
				println("Starting Server...");
				try
				{
					server.start();
				}
				catch (Exception e)
				{
					server.stop();
					exit(1);
				}
				println("Server started.");
				server.join();
			}
		else
			try (val context = new AnnotationConfigApplicationContext())
			{
				context.register(EmbeddedAppConfig.class);
				getConfigClasses().forEach(c -> context.register(c));
				println("Starting Server...");
				context.refresh();
				context.start();
				println("Server started.");
				Thread.currentThread().join();
			}
	}

	protected Options createOptions()
	{
		val result = super.createOptions();
		result.addOption(HSQLDB_OPTION,false,"start hsqldb server");
		result.addOption(HSQLDB_DIR_OPTION,true,"set hsqldb location [default: " + DEFAULT_HSQLDB_DIR + "]");
		result.addOption(DISABLE_EBMS_SERVER_OPTION,false,"disable ebms server");
		result.addOption(DISABLE_EBMS_CLIENT_OPTION,false,"disable ebms client");
		return result;
	}
	
	private Properties getProperties() throws IOException
	{
		return EmbeddedAppConfig.PROPERTY_SOURCE.getProperties();
	}

	private void startHSQLDB(CommandLine cmd, Properties properties) throws IOException, AclFormatException, URISyntaxException, ParseException
	{
		val jdbcURL = getHsqlDbJdbcUrl(cmd,properties);
		if (jdbcURL != null)
		{
			setProperty(EBMS_JDBC_UPDATE_PROPERTY,TRUE);
			println("Starting HSQLDB Server...");
			startHSQLDBServer(cmd,jdbcURL);
		}
	}

	private JdbcURL getHsqlDbJdbcUrl(CommandLine cmd, Properties properties) throws IOException, AclFormatException, URISyntaxException, ParseException
	{
		JdbcURL result = null;
		if (properties.getProperty(EBMS_JDBC_DRIVER_CLASS_NAME_PROPERTY).startsWith("org.hsqldb.jdbc") && cmd.hasOption(HSQLDB_OPTION))
		{
			result = nl.clockwork.ebms.admin.web.configuration.Utils.parseJdbcURL(properties.getProperty(EBMS_JDBC_URL_PROPERTY),new JdbcURL());
			val allowedHosts = "localhost|127.0.0.1";
			if (!result.getHost().matches("^(" + allowedHosts + ")$"))
			{
				println("Cannot start HSQLDB Server on " + result.getHost() + ". Use " + allowedHosts + " instead.");
				exit(1);
			}
		}
		return result;
	}

	public org.hsqldb.server.Server startHSQLDBServer(CommandLine cmd, JdbcURL jdbcURL) throws IOException, AclFormatException, URISyntaxException, ParseException
	{
		val options = new ArrayList<>();
		options.add("-database.0");
		options.add((cmd.hasOption(HSQLDB_DIR_OPTION) ? "file:" + cmd.getOptionValue(HSQLDB_DIR_OPTION) : "file:" + DEFAULT_HSQLDB_DIR) + "/" + jdbcURL.getDatabase());
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
		
		val connector = TRUE.equals(properties.getProperty(EBMS_SSL_PROPERTY)) ? 
				createEbMSHttpsConnector(properties,createEbMSSslContextFactory(properties)) : 
					createEbMSHttpConnector(properties);
		server.addConnector(connector);
		val connectionLimit = properties.getProperty(EBMS_CONNECTION_LIMIT_PROPERTY);
		if (StringUtils.isNotEmpty(connectionLimit))
			server.addBean(new ConnectionLimit(Integer.parseInt(connectionLimit),connector));
	}

	private ServerConnector createEbMSHttpConnector(Properties properties)
	{
		val httpConfig = new HttpConfiguration();
		httpConfig.setSendServerVersion(false);
		val result = new ServerConnector(server,new HttpConnectionFactory(httpConfig));
		result.setHost(StringUtils.isEmpty(properties.getProperty(EBMS_HOST_PROPERTY)) ? DEFAULT_HOST : properties.getProperty(EBMS_HOST_PROPERTY));
		result.setPort(Integer.parseInt(StringUtils.isEmpty(properties.getProperty(EBMS_PORT_PROPERTY))  ? DEFAULT_EBMS_PORT : properties.getProperty(EBMS_PORT_PROPERTY)));
		result.setName(EBMS_CONNECTOR_NAME);
		println("EbMS Service configured on http://" + Utils.getHost(result.getHost()) + ":" + result.getPort() + properties.getProperty(EBMS_PATH_PROPERTY));
		return result;
	}

	private SslContextFactory.Server createEbMSSslContextFactory(Properties properties) throws MalformedURLException, IOException
	{
		val result = new SslContextFactory.Server();
		addEbMSKeyStore(properties,result);
		if (TRUE.equals(properties.getProperty(HTTPS_REQUIRE_CLIENT_AUTHENTICATION_PROPERTY)))
			addEbMSTrustStore(properties,result);
		result.setExcludeCipherSuites();
		return result;
	}

	private void addEbMSKeyStore(Properties properties, SslContextFactory.Server sslContextFactory) throws MalformedURLException, IOException
	{
		val keyStore = getResource(properties.getProperty(KEYSTORE_PATH_PROPERTY));
		if (keyStore != null && keyStore.exists())
		{
			if (!StringUtils.isEmpty(properties.getProperty(HTTPS_PROTOCOLS_PROPERTY)))
				sslContextFactory.setIncludeProtocols(StringUtils.stripAll(StringUtils.split(properties.getProperty(HTTPS_PROTOCOLS_PROPERTY),',')));
			if (!StringUtils.isEmpty(properties.getProperty(HTTPS_CIPHER_SUITES_PROPERTY)))
				sslContextFactory.setIncludeCipherSuites(StringUtils.stripAll(StringUtils.split(properties.getProperty(HTTPS_CIPHER_SUITES_PROPERTY),',')));
			sslContextFactory.setKeyStoreType(properties.getProperty(KEYSTORE_TYPE_PROPERTY));
			sslContextFactory.setKeyStoreResource(keyStore);
			sslContextFactory.setKeyStorePassword(properties.getProperty(KEYSTORE_PASSWORD_PROPERTY));
			String certAlias = properties.getProperty(KEYSTORE_DEFAULT_ALIAS_PROPERTY);
			if (StringUtils.isNotEmpty(certAlias))
				sslContextFactory.setCertAlias(certAlias);
		}
		else
		{
			println("EbMS Service not available: keyStore " + properties.getProperty(KEYSTORE_PATH_PROPERTY) + " not found!");
			exit(1);
		}
	}

	private void addEbMSTrustStore(Properties properties, SslContextFactory.Server sslContextFactory) throws MalformedURLException, IOException
	{
		val trustStore = getResource(properties.getProperty(TRUSTSTORE_PATH_PROPERTY));
		if (trustStore != null && trustStore.exists())
		{
			sslContextFactory.setNeedClientAuth(true);
			sslContextFactory.setTrustStoreType(properties.getProperty(TRUSTSTORE_TYPE_PROPERTY));
			sslContextFactory.setTrustStoreResource(trustStore);
			sslContextFactory.setTrustStorePassword(properties.getProperty(TRUSTSTORE_PASSWORD_PROPERTY));
		}
		else
		{
			println("EbMS Service not available: trustStore " + properties.getProperty(TRUSTSTORE_PATH_PROPERTY) + " not found!");
			exit(1);
		}
	}

	private ServerConnector createEbMSHttpsConnector(Properties properties, SslContextFactory.Server sslContextFactory)
	{
		val httpConfig = new HttpConfiguration();
		httpConfig.setSendServerVersion(false);
		val result = new ServerConnector(server,sslContextFactory,new HttpConnectionFactory(httpConfig));
		result.setHost(StringUtils.isEmpty(properties.getProperty(EBMS_HOST_PROPERTY)) ? DEFAULT_HOST : properties.getProperty(EBMS_HOST_PROPERTY));
		result.setPort(Integer.parseInt(StringUtils.isEmpty(properties.getProperty(EBMS_PORT_PROPERTY))  ? DEFAULT_EBMS_PORT : properties.getProperty(EBMS_PORT_PROPERTY)));
		result.setName(EBMS_CONNECTOR_NAME);
		println("EbMS Service configured on https://" + Utils.getHost(result.getHost()) + ":" + result.getPort() + properties.getProperty(EBMS_PATH_PROPERTY));
		return result;
	}

	protected ServletContextHandler createEbMSContextHandler(Properties properties, ContextLoaderListener contextLoaderListener) throws IOException, NoSuchAlgorithmException
	{
		val result = new ServletContextHandler(ServletContextHandler.SESSIONS);
		result.setVirtualHosts(new String[] {"@" + EBMS_CONNECTOR_NAME});
		result.setContextPath("/");
		if (LoggingUtils.Status.ENABLED.name().equals(properties.getProperty(LOGGING_MDC_PROPERTY)) && LoggingUtils.Status.ENABLED.name().equals(properties.getProperty(LOGGING_MDC_AUDIT_PROPERTY)))
			result.addFilter(createRemoteAddressMDCFilterHolder(),"/*",EnumSet.allOf(DispatcherType.class));
		if (!StringUtils.isEmpty(properties.getProperty(EBMS_QUERIES_PER_SECOND_PROPERTY)))
			result.addFilter(createRateLimiterFilterHolder(properties.getProperty(EBMS_QUERIES_PER_SECOND_PROPERTY)),"/*",EnumSet.allOf(DispatcherType.class));
		if (!StringUtils.isEmpty(properties.getProperty(EBMS_USER_QUERIES_PER_SECOND_PROPERTY)))
			result.addFilter(createUserRateLimiterFilterHolder(properties.getProperty(EBMS_USER_QUERIES_PER_SECOND_PROPERTY)),"/*",EnumSet.allOf(DispatcherType.class));
		if (TRUE.equals(properties.getProperty(HTTPS_CLIENT_CERTIFICATE_AUTHENTICATION_PROPERTY).toLowerCase()))
			result.addFilter(createClientCertificateManagerFilterHolder(properties.getProperty(HTTPS_CLIENT_CERTIFICATE_HEADER_PROPERTY)),"/*",EnumSet.allOf(DispatcherType.class));
		result.addServlet(EbMSServlet.class,properties.getProperty(EBMS_PATH_PROPERTY));
		result.addEventListener(contextLoaderListener);
		return result;
	}
}
