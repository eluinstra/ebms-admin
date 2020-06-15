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
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.DispatcherType;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.common.logging.LogUtils;
import org.eclipse.jetty.server.ConnectionLimit;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.Location;
import org.hsqldb.persist.HsqlProperties;
import org.hsqldb.server.EbMSServerProperties;
import org.hsqldb.server.ServerAcl.AclFormatException;
import org.hsqldb.server.ServerConfiguration;
import org.hsqldb.server.ServerConstants;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import lombok.AccessLevel;
import lombok.val;
import lombok.var;
import lombok.experimental.FieldDefaults;
import nl.clockwork.ebms.admin.DBMigrate.BaselineVersion;
import nl.clockwork.ebms.admin.web.ExtensionProvider;
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
		System.setProperty("eventProcessor.start","" + !cmd.hasOption("disableEbMSClient"));

		val start = new StartEmbedded();
		start.init(cmd);
		start.server.setHandler(start.handlerCollection);
		val properties = getProperties("nl/clockwork/ebms/admin/applicationConfig.embedded.xml");

		start.initHSQLDB(cmd,properties);
		start.initWebServer(cmd,start.server);
		if (!cmd.hasOption("disableEbMSServer"))
			start.initEbMSServer(properties,start.server);
		if (cmd.hasOption("jmx"))
			start.initJMX(start.server);

		try (val context = new AnnotationConfigWebApplicationContext())
		{
			context.register(EmbeddedAppConfig.class);
			val contextLoaderListener = new ContextLoaderListener(context);
			if (cmd.hasOption("soap") || !cmd.hasOption("headless"))
				start.handlerCollection.addHandler(start.createWebContextHandler(cmd,contextLoaderListener));
			start.handlerCollection.addHandler(start.createEbMSContextHandler(properties,contextLoaderListener));
	
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
		val result = Start.createOptions();
		result.addOption("hsqldb",false,"start hsqldb server");
		result.addOption("hsqldbDir",true,"set hsqldb location (default: hsqldb)");
		result.addOption("ebmsVersion",true,"set current ebmsVersion for hsqldb update (default: none)");
		result.addOption("soap",false,"start soap service");
		result.addOption("headless",false,"start without web interface");
		result.addOption("disableEbMSServer",false,"disable ebms server");
		result.addOption("disableEbMSClient",false,"disable ebms client");
		result.addOption("ebms.connectionLimit",true,"set connection limit on ebms interface (default: none)");
		return result;
	}
	
	private static Map<String,String> getProperties(String...files) throws IOException
	{
		return EmbeddedAppConfig.properties().getProperties();
	}

	private void initHSQLDB(CommandLine cmd, Map<String,String> properties) throws IOException, AclFormatException, URISyntaxException, ParseException
	{
		if ("org.hsqldb.jdbcDriver".equals(properties.get("ebms.jdbc.driverClassName")) && cmd.hasOption("hsqldb"))
		{
			val jdbcURL = nl.clockwork.ebms.admin.web.configuration.Utils.parseJdbcURL(properties.get("ebms.jdbc.url"),new JdbcURL());
			if (!jdbcURL.getHost().matches("(localhost|127.0.0.1)"))
			{
				System.out.println("Cannot start server on " + jdbcURL.getHost());
				System.exit(1);
			}
			System.out.println("Starting hsqldb...");
			startHSQLDBServer(cmd,jdbcURL);
		}
	}

	public void startHSQLDBServer(CommandLine cmd, JdbcURL jdbcURL) throws IOException, AclFormatException, URISyntaxException, ParseException
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
		org.hsqldb.server.Server server = new org.hsqldb.server.Server();
		server.setProperties(props);
		server.start();
		initDatabase(server,cmd.getOptionValue("ebmsVersion"));
	}

	private void initDatabase(org.hsqldb.server.Server server, String ebmsVersion) throws ParseException
	{
		val url = "jdbc:hsqldb:hsql://localhost:" + server.getPort() + "/" + server.getDatabaseName(0,true);
		val user = "sa";
		val password = "";
		val locations = getDbMigrationLocations(new Location("classpath:/nl/clockwork/ebms/db/migration/hsqldb"));
		var config = Flyway.configure()
				.dataSource(url,user,password)
				.locations(locations)
				.ignoreMissingMigrations(true)
				.outOfOrder(true);
		if (StringUtils.isNotEmpty(ebmsVersion))
				config = config
						.baselineVersion(BaselineVersion.getBaselineVersion(ebmsVersion).orElseThrow(() -> new ParseException("ebmsVersion " + ebmsVersion + " not found!")))
						.baselineOnMigrate(true);
		config.load().migrate();
	}

	private static Location[] getDbMigrationLocations(Location dbMigrationLocation)
	{
		val result = ExtensionProvider.get().stream()
				.filter(p -> !StringUtils.isEmpty(p.getDbMigrationLocation()))
				.map(p -> new Location(p.getDbMigrationLocation() + "/hsqldb"))
				.collect(Collectors.toList());
		result.add(0,dbMigrationLocation);
		return result.toArray(new Location[]{});
	}

	private void initEbMSServer(Map<String,String> properties, Server server) throws MalformedURLException, IOException
	{
		
		Connector connector = "true".equals(properties.get("ebms.ssl")) ? 
				createEbMSHttpsConnector(properties,createEbMSSslContextFactory(properties)) : 
					createEbMSHttpConnector(properties);
		if (properties.containsKey("ebms.connectionLimit"))
			server.addBean(new ConnectionLimit(Integer.parseInt(properties.get("ebms.connectionLimit")),connector));
		server.addConnector(connector);
	}

	private ServerConnector createEbMSHttpConnector(Map<String,String> properties)
	{
		val result = new ServerConnector(this.server);
		result.setHost(StringUtils.isEmpty(properties.get("ebms.host")) ? "0.0.0.0" : properties.get("ebms.host"));
		result.setPort(StringUtils.isEmpty(properties.get("ebms.port"))  ? 8888 : Integer.parseInt(properties.get("ebms.port")));
		result.setName("ebms");
		System.out.println("EbMS service configured on http://" + Utils.getHost(result.getHost()) + ":" + result.getPort() + properties.get("ebms.path"));
		return result;
	}

	private SslContextFactory.Server createEbMSSslContextFactory(Map<String,String> properties) throws MalformedURLException, IOException
	{
		val result = new SslContextFactory.Server();
		addEbMSKeyStore(properties,result);
		if ("true".equals(properties.get("https.requireClientAuthentication")))
			addEbMSTrustStore(properties,result);
		result.setExcludeCipherSuites();
		return result;
	}

	private void addEbMSKeyStore(Map<String,String> properties, SslContextFactory.Server sslContextFactory) throws MalformedURLException, IOException
	{
		val keyStore = getResource(properties.get("keystore.path"));
		if (keyStore != null && keyStore.exists())
		{
			if (!StringUtils.isEmpty(properties.get("https.protocols")))
				sslContextFactory.setIncludeProtocols(StringUtils.stripAll(StringUtils.split(properties.get("https.protocols"),',')));
			if (!StringUtils.isEmpty(properties.get("https.cipherSuites")))
				sslContextFactory.setIncludeCipherSuites(StringUtils.stripAll(StringUtils.split(properties.get("https.cipherSuites"),',')));
			sslContextFactory.setKeyStoreType(properties.get("keystore.type"));
			sslContextFactory.setKeyStoreResource(keyStore);
			sslContextFactory.setKeyStorePassword(properties.get("keystore.password"));
			String certAlias = properties.get("keystore.defaultAlias");
			if (StringUtils.isNotEmpty(certAlias))
				sslContextFactory.setCertAlias(certAlias);
		}
		else
		{
			System.out.println("EbMS service not available: keyStore " + properties.get("keystore.path") + " not found!");
			System.exit(1);
		}
	}

	private void addEbMSTrustStore(Map<String,String> properties, SslContextFactory.Server sslContextFactory) throws MalformedURLException, IOException
	{
		val trustStore = getResource(properties.get("truststore.path"));
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

	private ServerConnector createEbMSHttpsConnector(Map<String,String> properties, SslContextFactory.Server sslContextFactory)
	{
		val result = new ServerConnector(this.server,sslContextFactory);
		result.setHost(StringUtils.isEmpty(properties.get("ebms.host")) ? "0.0.0.0" : properties.get("ebms.host"));
		result.setPort(StringUtils.isEmpty(properties.get("ebms.port"))  ? 8888 : Integer.parseInt(properties.get("ebms.port")));
		result.setName("ebms");
		System.out.println("EbMS service configured on https://" + Utils.getHost(result.getHost()) + ":" + result.getPort() + properties.get("ebms.path"));
		return result;
	}

	protected ServletContextHandler createEbMSContextHandler(Map<String,String> properties, ContextLoaderListener contextLoaderListener) throws IOException, NoSuchAlgorithmException
	{
		val result = new ServletContextHandler(ServletContextHandler.SESSIONS);
		result.setVirtualHosts(new String[] {"@ebms"});
		result.setContextPath("/");
		if ("true".equals(properties.get("https.clientCertificateAuthentication").toLowerCase()))
			result.addFilter(createClientCertificateManagerFilterHolder(properties),"/*",EnumSet.allOf(DispatcherType.class));
		result.addServlet(nl.clockwork.ebms.server.servlet.EbMSServlet.class,properties.get("ebms.path"));
		result.addEventListener(contextLoaderListener);
		return result;
	}

	private FilterHolder createClientCertificateManagerFilterHolder(Map<String,String> properties)
	{
		FilterHolder result = new FilterHolder(nl.clockwork.ebms.server.servlet.ClientCertificateManagerFilter.class); 
		result.setInitParameter("x509CertificateHeader",properties.get("https.clientCertificateHeader"));
		return result;
	}

}
