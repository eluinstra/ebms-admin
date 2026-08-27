/*
 * Copyright 2013 - 2026 Clockwork
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

import jakarta.servlet.DispatcherType;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.management.ManagementFactory;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.management.remote.JMXServiceURL;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import nl.clockwork.ebms.PluginProvider;
import nl.clockwork.ebms.admin.web.ExtensionProvider;
import nl.clockwork.ebms.security.KeyStoreType;
import nl.clockwork.ebms.server.servlet.HealthServlet;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.eclipse.jetty.ee10.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.ee10.servlet.FilterHolder;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.ee10.servlet.security.ConstraintMapping;
import org.eclipse.jetty.ee10.servlet.security.ConstraintSecurityHandler;
import org.eclipse.jetty.jmx.ConnectorServer;
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.security.Constraint;
import org.eclipse.jetty.security.Constraint.Authorization;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.ConnectionLimit;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.jline.prompt.InputResult;
import org.jline.prompt.Prompter;
import org.jline.prompt.PrompterFactory;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@RequiredArgsConstructor
public class Start implements SystemInterface
{
	protected static final String HELP_OPTION = "h";
	private static final String HOST_OPTION = "host";
	private static final String PORT_OPTION = "port";
	private static final String PATH_OPTION = "path";
	private static final String DISABLE_HOSTNAME_VERIFICATION_OPTION = "disableHostnameVerification";
	protected static final String SOAP_OPTION = "soap";
	protected static final String HEADLESS_OPTION = "headless";
	protected static final String HEALTH_OPTION = "health";
	private static final String HEALTH_PORT_OPTION = "healthPort";
	private static final String CONNECTION_LIMIT_OPTION = "connectionLimit";
	private static final String QUERIES_PER_SECOND_OPTION = "queriesPerSecond";
	private static final String USER_QUERIES_PER_SECOND_OPTION = "userQueriesPerSecond";
	private static final String AUDIT_LOGGING_OPTION = "auditLogging";
	private static final String ECHO_HEADER_NAMES_OPTION = "echoHeaderNames";
	private static final String MDC_HEADER_NAMES_OPTION = "mdcHeaderNames";
	private static final String SSL_OPTION = "ssl";
	private static final String PROTOCOLS_OPTION = "protocols";
	private static final String CIPHER_SUITES_OPTION = "cipherSuites";
	private static final String KEY_STORES_TYPE_OPTION = "keyStoresType";
	private static final String KEY_STORE_TYPE_OPTION = "keyStoreType";
	private static final String KEY_STORE_PATH_OPTION = "keyStorePath";
	private static final String KEY_STORE_PASSWORD_OPTION = "keyStorePassword";
	private static final String CLIENT_AUTHENTICATION_OPTION = "clientAuthentication";
	private static final String CLIENT_CERTIFICATE_HEADER_OPTION = "clientCertificateHeader";
	private static final String TRUST_STORE_TYPE_OPTION = "trustStoreType";
	private static final String TRUST_STORE_PATH_OPTION = "trustStorePath";
	private static final String TRUST_STORE_PASSWORD_OPTION = "trustStorePassword";
	private static final String AUTHENTICATION_OPTION = "authentication";
	private static final String CLIENT_TRUST_STORE_TYPE_OPTION = "clientTrustStoreType";
	private static final String CLIENT_TRUST_STORE_PATH_OPTION = "clientTrustStorePath";
	private static final String CLIENT_TRUST_STORE_PASSWORD_OPTION = "clientTrustStorePassword";
	private static final String CONFIG_DIR_OPTION = "configDir";
	protected static final String JMX_OPTION = "jmx";
	private static final String JMX_PORT_OPTION = "jmxPort";
	private static final String JMX_ACCESS_FILE_OPTION = "jmxAccessFile";
	private static final String JMX_PASSWORD_FILE_OPTION = "jmxPasswordFile";
	protected static final String DEFAULT_HOST = "0.0.0.0";
	private static final String DEFAULT_PORT = "8080";
	private static final String DEFAULT_SSL_PORT = "8443";
	private static final String DEFAULT_PATH = "/";
	private static final String DEFAULT_HEALTH_PORT = "8008";
	private static final String DEFAULT_JMS_PORT = "1999";
	private static final String DEFAULT_KEYSTORE_TYPE = KeyStoreType.PKCS12.name();
	private static final String DEFAULT_KEYSTORE_FILE = "nl/clockwork/ebms/keystore.p12";
	private static final String DEFAULT_KEYSTORE_PASSWORD = "password";
	private static final String DEFAULT_CONFIG_DIR = "";
	private static final String WEB_CONNECTOR_NAME = "web";
	private static final String HEALTH_CONNECTOR_NAME = "health";
	private static final String SOAP_URL = "/service";
	private static final String HEALTH_URL = "/health";
	private static final String NONE = "<none>";
	private static final String REALM = "Realm";
	private static final String REALM_FILE = "realm.properties";

	Server server = new Server();
	Handler.Sequence handlerCollection = new Handler.Sequence();
	Terminal terminal = createTerminal();
	Prompter prompter = PrompterFactory.create(terminal);

	public static void main(String[] args) throws Exception
	{
		LogUtils.setLoggerClass(org.apache.cxf.common.logging.Slf4jLogger.class);
		val app = new Start();
		app.startService(args);
	}

	private void startService(String[] args) throws Exception
	{
		val options = createOptions();
		val cmd = new DefaultParser().parse(options, args);
		if (cmd.hasOption(HELP_OPTION))
			printUsage(options);
		init(cmd);
		server.setHandler(handlerCollection);
		if (cmd.hasOption(JMX_OPTION))
			initJMX(cmd, server);
		try (val context = new AnnotationConfigWebApplicationContext())
		{
			context.scan("nl.clockwork.ebms");
			getPluginConfigClasses().forEach(context::register);
			getConfigClasses().forEach(context::register);
			val contextLoaderListener = new ContextLoaderListener(context);
			if (cmd.hasOption(SOAP_OPTION) || !cmd.hasOption(HEADLESS_OPTION))
			{
				initWebServer(cmd, server);
				handlerCollection.addHandler(createWebContextHandler(cmd, contextLoaderListener));
			}
			if (cmd.hasOption(HEALTH_OPTION))
			{
				initHealthServer(cmd, server);
				handlerCollection.addHandler(createHealthContextHandler(cmd, contextLoaderListener));
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
	}

	protected Options createOptions()
	{
		val result = new Options();
		result.addOption(HELP_OPTION, false, "print this message");
		result.addOption(HOST_OPTION, true, "set host [default: " + DEFAULT_HOST + "]");
		result.addOption(PORT_OPTION, true, "set port [default: <" + DEFAULT_PORT + "|" + DEFAULT_SSL_PORT + ">]");
		result.addOption(PATH_OPTION, true, "set path [default: " + DEFAULT_PATH + "]");
		result.addOption(SOAP_OPTION, false, "start SOAP service");
		result.addOption(HEADLESS_OPTION, false, "start without web interface");
		result.addOption(HEALTH_OPTION, false, "start health service");
		result.addOption(HEALTH_PORT_OPTION, true, "set health service port [default: " + DEFAULT_HEALTH_PORT + "]");
		result.addOption(CONNECTION_LIMIT_OPTION, true, "set connection limit [default: " + NONE + "]");
		result.addOption(QUERIES_PER_SECOND_OPTION, true, "set max requests per second [default: " + NONE + "]");
		result.addOption(USER_QUERIES_PER_SECOND_OPTION, true, "set max requests per user per second [default: " + NONE + "]");
		result.addOption(AUDIT_LOGGING_OPTION, false, "enable audit logging");
		result.addOption(ECHO_HEADER_NAMES_OPTION, true, "set echo header names [default: " + NONE + "]");
		result.addOption(MDC_HEADER_NAMES_OPTION, true, "set logging MDC header names [default: " + NONE + "]");
		result.addOption(SSL_OPTION, false, "enable SSL");
		result.addOption(PROTOCOLS_OPTION, true, "set SSL Protocols [default: " + NONE + "]");
		result.addOption(CIPHER_SUITES_OPTION, true, "set SSL CipherSuites [default: " + NONE + "]");
		result.addOption(KEY_STORES_TYPE_OPTION, true, "set keystores type [default: " + NONE + "]");
		result.addOption(KEY_STORE_TYPE_OPTION, true, "set keystore type [default: " + DEFAULT_KEYSTORE_TYPE + "]");
		result.addOption(KEY_STORE_PATH_OPTION, true, "set keystore path [default: " + DEFAULT_KEYSTORE_FILE + "]");
		result.addOption(KEY_STORE_PASSWORD_OPTION, true, "set keystore password [default: " + DEFAULT_KEYSTORE_PASSWORD + "]");
		result.addOption(CLIENT_AUTHENTICATION_OPTION, false, "enable SSL client authentication");
		result.addOption(CLIENT_CERTIFICATE_HEADER_OPTION, true, "set client certificate header [default: " + NONE + "]");
		result.addOption(TRUST_STORE_TYPE_OPTION, true, "set truststore type [default: " + DEFAULT_KEYSTORE_TYPE + "]");
		result.addOption(TRUST_STORE_PATH_OPTION, true, "set truststore path [default: " + NONE + "]");
		result.addOption(TRUST_STORE_PASSWORD_OPTION, true, "set truststore password [default: " + NONE + "]");
		result.addOption(AUTHENTICATION_OPTION, false, "enable basic | client certificate authentication");
		result.addOption(CLIENT_TRUST_STORE_TYPE_OPTION, true, "set client truststore type [default: " + DEFAULT_KEYSTORE_TYPE + "]");
		result.addOption(CLIENT_TRUST_STORE_PATH_OPTION, true, "set client truststore path [default: " + NONE + "]");
		result.addOption(CLIENT_TRUST_STORE_PASSWORD_OPTION, true, "set client truststore password [default: " + NONE + "]");
		result.addOption(CONFIG_DIR_OPTION, true, "set config directory [default: <startup_directory>]");
		result.addOption(JMX_OPTION, false, "start JMX server");
		result.addOption(JMX_PORT_OPTION, true, "set JMX port [default: " + DEFAULT_JMS_PORT + "]");
		result.addOption(JMX_ACCESS_FILE_OPTION, true, "set JMX access file [default: " + NONE + "]");
		result.addOption(JMX_PASSWORD_FILE_OPTION, true, "set JMX password file [default: " + NONE + "]");
		return result;
	}

	protected void printUsage(Options options)
	{
		val formatter = new HelpFormatter();
		formatter.printHelp(getClass().getSimpleName(), options, true);
		exit(0);
	}

	protected List<Class<?>> getConfigClasses()
	{
		return ExtensionProvider.get()
				.stream()
				.filter(p -> p.getSpringConfigurationClass() != null)
				.map(ExtensionProvider::getSpringConfigurationClass)
				.collect(Collectors.toList());
	}

	protected List<Class<?>> getPluginConfigClasses()
	{
		return PluginProvider.get()
				.stream()
				.filter(p -> p.getSpringConfigurationClass() != null)
				.map(PluginProvider::getSpringConfigurationClass)
				.collect(Collectors.toList());
	}

	protected void init(CommandLine cmd)
	{
		val configDir = cmd.getOptionValue(CONFIG_DIR_OPTION, DEFAULT_CONFIG_DIR);
		setProperty("ebms.configDir", configDir);
		println("Using config directory: " + configDir);
	}

	protected void initWebServer(CommandLine cmd, Server server) throws GeneralSecurityException, IOException
	{
		val connector = cmd.hasOption(SSL_OPTION)
				? createHttpsConnector(cmd, createSslContextFactory(cmd, cmd.hasOption(CLIENT_AUTHENTICATION_OPTION)))
				: createHttpConnector(cmd);
		server.addConnector(connector);
		if (cmd.hasOption(CONNECTION_LIMIT_OPTION))
			server.addBean(new ConnectionLimit(Integer.parseInt(cmd.getOptionValue(CONNECTION_LIMIT_OPTION)), connector));
	}

	private ServerConnector createHttpConnector(CommandLine cmd)
	{
		val httpConfig = new HttpConfiguration();
		httpConfig.setSendServerVersion(false);
		val result = new ServerConnector(server, new HttpConnectionFactory(httpConfig));
		result.setHost(cmd.getOptionValue(HOST_OPTION, DEFAULT_HOST));
		result.setPort(Integer.parseInt(cmd.getOptionValue(PORT_OPTION, DEFAULT_PORT)));
		result.setName(WEB_CONNECTOR_NAME);
		if (!cmd.hasOption(HEADLESS_OPTION))
			println("Web Server configured on http://" + Utils.getHost(result.getHost()) + ":" + result.getPort() + getPath(cmd));
		if (cmd.hasOption(SOAP_OPTION))
			println("SOAP Service configured on http://" + Utils.getHost(result.getHost()) + ":" + result.getPort() + SOAP_URL);
		return result;
	}

	protected void initHealthServer(CommandLine cmd, Server server)
	{
		val connector = createHealthConnector(cmd, server);
		server.addConnector(connector);
	}

	private ServerConnector createHealthConnector(CommandLine cmd, Server server)
	{
		val result = new ServerConnector(server);
		result.setHost(cmd.getOptionValue(HOST_OPTION, DEFAULT_HOST));
		result.setPort(Integer.parseInt(cmd.getOptionValue(HEALTH_PORT_OPTION, DEFAULT_HEALTH_PORT)));
		result.setName(HEALTH_CONNECTOR_NAME);
		println("Health Service configured on http://" + Utils.getHost(result.getHost()) + ":" + result.getPort() + HEALTH_URL);
		return result;
	}

	private SslContextFactory.Server createSslContextFactory(CommandLine cmd, boolean clientAuthentication) throws GeneralSecurityException, IOException
	{
		val result = new SslContextFactory.Server();
		val ebMSKeyStore = EbMSKeyStore.of(
				KeyStoreType.valueOf(cmd.getOptionValue(KEY_STORE_TYPE_OPTION, DEFAULT_KEYSTORE_TYPE)),
				cmd.getOptionValue(KEY_STORE_PATH_OPTION, DEFAULT_KEYSTORE_FILE),
				cmd.getOptionValue(KEY_STORE_PASSWORD_OPTION, DEFAULT_KEYSTORE_PASSWORD));
		addKeyStore(cmd, result, ebMSKeyStore);
		if (clientAuthentication)
			addTrustStore(cmd, result);
		return result;
	}

	private void addKeyStore(CommandLine cmd, SslContextFactory.Server sslContextFactory, EbMSKeyStore ebMSKeyStore)
	{
		val protocols = cmd.getOptionValue(PROTOCOLS_OPTION);
		if (!StringUtils.isEmpty(protocols))
			sslContextFactory.setIncludeProtocols(StringUtils.stripAll(StringUtils.split(protocols, ',')));
		val cipherSuites = cmd.getOptionValue(CIPHER_SUITES_OPTION);
		if (!StringUtils.isEmpty(cipherSuites))
			sslContextFactory.setIncludeCipherSuites(StringUtils.stripAll(StringUtils.split(cipherSuites, ',')));
		sslContextFactory.setKeyStore(ebMSKeyStore.getKeyStore());
		sslContextFactory.setKeyStorePassword(ebMSKeyStore.getPassword());
	}

	private void addTrustStore(CommandLine cmd, SslContextFactory.Server sslContextFactory) throws IOException
	{
		val trustStoreType = cmd.getOptionValue(TRUST_STORE_TYPE_OPTION, DEFAULT_KEYSTORE_TYPE);
		val trustStorePath = cmd.getOptionValue(TRUST_STORE_PATH_OPTION);
		val trustStorePassword = cmd.getOptionValue(TRUST_STORE_PASSWORD_OPTION);
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

	private ServerConnector createHttpsConnector(CommandLine cmd, SslContextFactory.Server sslContextFactory)
	{
		val httpConfig = new HttpConfiguration();
		httpConfig.setSendServerVersion(false);
		httpConfig.addCustomizer(new SecureRequestCustomizer(!cmd.hasOption(DISABLE_HOSTNAME_VERIFICATION_OPTION)));
		val result = new ServerConnector(server, sslContextFactory, new HttpConnectionFactory(httpConfig));
		result.setHost(cmd.getOptionValue(HOST_OPTION, DEFAULT_HOST));
		result.setPort(Integer.parseInt(cmd.getOptionValue(PORT_OPTION, DEFAULT_SSL_PORT)));
		result.setName(WEB_CONNECTOR_NAME);
		if (!cmd.hasOption(HEADLESS_OPTION))
			println("Web Server configured on https://" + Utils.getHost(result.getHost()) + ":" + result.getPort() + getPath(cmd));
		if (cmd.hasOption(SOAP_OPTION))
			println("SOAP Service configured on https://" + Utils.getHost(result.getHost()) + ":" + result.getPort() + SOAP_URL);
		return result;
	}

	protected String getPath(CommandLine cmd)
	{
		return cmd.getOptionValue(PATH_OPTION, DEFAULT_PATH);
	}

	protected void initJMX(CommandLine cmd, Server server) throws Exception
	{
		println("Starting JMX Server...");
		val mBeanContainer = new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
		server.addBean(mBeanContainer);
		val jmxURL = new JMXServiceURL("rmi", null, Integer.parseInt(cmd.getOptionValue(JMX_PORT_OPTION, DEFAULT_JMS_PORT)), "/jndi/rmi:///jmxrmi");
		val sslContextFactory = cmd.hasOption("ssl") ? createSslContextFactory(cmd, false) : null;
		val jmxServer = new ConnectorServer(jmxURL, createEnv(cmd), "org.eclipse.jetty.jmx:name=rmiconnectorserver", sslContextFactory);
		server.addBean(jmxServer);
		println("JMX Server configured on " + jmxURL);
	}

	private Map<String, Object> createEnv(CommandLine cmd)
	{
		val result = new HashMap<String, Object>();
		if (cmd.hasOption(JMX_ACCESS_FILE_OPTION) && cmd.hasOption(JMX_PASSWORD_FILE_OPTION))
		{
			result.put("jmx.remote.x.access.file", cmd.hasOption(JMX_ACCESS_FILE_OPTION));
			result.put("jmx.remote.x.password.file", cmd.hasOption(JMX_PASSWORD_FILE_OPTION));
		}
		return result;
	}

	protected ServletContextHandler createWebContextHandler(CommandLine cmd, ContextLoaderListener contextLoaderListener) throws Exception
	{
		val result = new ServletContextHandler(ServletContextHandler.SESSIONS);
		result.setVirtualHosts(List.of("@" + WEB_CONNECTOR_NAME));
		result.setInitParameter("configuration", "deployment");
		result.setContextPath(getPath(cmd));
		if (cmd.hasOption(ECHO_HEADER_NAMES_OPTION))
			result.addFilter(createEchoServletFilterHolder(cmd.getOptionValue(ECHO_HEADER_NAMES_OPTION)), "/*", EnumSet.allOf(DispatcherType.class));
		if (cmd.hasOption(MDC_HEADER_NAMES_OPTION))
			result.addFilter(createMDCServletFilterHolder(cmd.getOptionValue(MDC_HEADER_NAMES_OPTION)), "/*", EnumSet.allOf(DispatcherType.class));
		if (cmd.hasOption(AUDIT_LOGGING_OPTION))
			result.addFilter(createRemoteAddressMDCFilterHolder(), "/*", EnumSet.allOf(DispatcherType.class));
		if (!StringUtils.isEmpty(cmd.getOptionValue(QUERIES_PER_SECOND_OPTION)))
			result.addFilter(createRateLimiterFilterHolder(cmd.getOptionValue(QUERIES_PER_SECOND_OPTION)), "/*", EnumSet.allOf(DispatcherType.class));
		if (!StringUtils.isEmpty(cmd.getOptionValue(USER_QUERIES_PER_SECOND_OPTION)))
			result.addFilter(createUserRateLimiterFilterHolder(cmd.getOptionValue(USER_QUERIES_PER_SECOND_OPTION)), "/*", EnumSet.allOf(DispatcherType.class));
		if (cmd.hasOption(AUTHENTICATION_OPTION))
			addAuthenticationHandler(cmd, result);
		if (cmd.hasOption(SOAP_OPTION))
			result.addServlet(CXFServlet.class, SOAP_URL + "/*");
		if (!cmd.hasOption(HEADLESS_OPTION))
			addWicketServletHolder(result);
		result.setErrorHandler(createErrorHandler());
		result.addEventListener(contextLoaderListener);
		return result;
	}

	protected FilterHolder createEchoServletFilterHolder(String headerNames)
	{
		val result = new FilterHolder(nl.clockwork.ebms.server.servlet.EchoServletFilter.class);
		result.setInitParameter("headerNames", headerNames);
		return result;
	}

	protected FilterHolder createMDCServletFilterHolder(String headerNames)
	{
		val result = new FilterHolder(nl.clockwork.ebms.server.servlet.MDCServletFilter.class);
		result.setInitParameter("headerNames", headerNames);
		return result;
	}

	protected FilterHolder createRemoteAddressMDCFilterHolder()
	{
		return new FilterHolder(nl.clockwork.ebms.server.servlet.RemoteAddressMDCFilter.class);
	}

	protected FilterHolder createRateLimiterFilterHolder(String queriesPerSecond)
	{
		val result = new FilterHolder(nl.clockwork.ebms.server.servlet.RateLimiterFilter.class);
		result.setInitParameter(QUERIES_PER_SECOND_OPTION, queriesPerSecond);
		return result;
	}

	protected FilterHolder createUserRateLimiterFilterHolder(String queriesPerSecond)
	{
		val result = new FilterHolder(nl.clockwork.ebms.server.servlet.RateLimiterFilter.class);
		result.setInitParameter(USER_QUERIES_PER_SECOND_OPTION, queriesPerSecond);
		return result;
	}

	private void addAuthenticationHandler(CommandLine cmd, final ServletContextHandler result) throws IOException, NoSuchAlgorithmException
	{
		if (!cmd.hasOption(CLIENT_AUTHENTICATION_OPTION))
		{
			println("Configuring Web Server basic authentication:");
			val file = new File(REALM_FILE);
			if (file.exists())
				println("Using file " + file.getAbsoluteFile());
			else
				createRealmFile(file);
			result.setSecurityHandler(getSecurityHandler());
		}
		else if (cmd.hasOption(SSL_OPTION) && cmd.hasOption(CLIENT_AUTHENTICATION_OPTION))
		{
			result.addFilter(
					createClientCertificateManagerFilterHolder(cmd.getOptionValue(CLIENT_CERTIFICATE_HEADER_OPTION)),
					"/*",
					EnumSet.of(DispatcherType.REQUEST, DispatcherType.ERROR));
			result.addFilter(createClientCertificateAuthenticationFilterHolder(cmd), "/*", EnumSet.of(DispatcherType.REQUEST, DispatcherType.ERROR));
		}
	}

	private void addWicketServletHolder(final ServletContextHandler result)
	{
		val servletHolder = new ServletHolder(nl.clockwork.ebms.admin.web.ResourceServlet.class);
		result.addServlet(servletHolder, "/css/*");
		result.addServlet(servletHolder, "/fonts/*");
		result.addServlet(servletHolder, "/images/*");
		result.addServlet(servletHolder, "/js/*");
		result.addFilter(createWicketFilterHolder(), "/*", EnumSet.of(DispatcherType.REQUEST, DispatcherType.ERROR));
	}

	protected FilterHolder createClientCertificateManagerFilterHolder(String clientCertificateHeader)
	{
		val result = new FilterHolder(nl.clockwork.ebms.server.servlet.ClientCertificateManagerFilter.class);
		result.setInitParameter("x509CertificateHeader", clientCertificateHeader);
		return result;
	}

	private FilterHolder createClientCertificateAuthenticationFilterHolder(CommandLine cmd) throws IOException
	{
		println("Configuring Web Server client certificate authentication:");
		val result = new FilterHolder(nl.clockwork.ebms.server.servlet.ClientCertificateAuthenticationFilter.class);
		val clientTrustStoreType = cmd.getOptionValue(CLIENT_TRUST_STORE_TYPE_OPTION, DEFAULT_KEYSTORE_TYPE);
		val clientTrustStorePath = cmd.getOptionValue(CLIENT_TRUST_STORE_PATH_OPTION);
		val clientTrustStorePassword = cmd.getOptionValue(CLIENT_TRUST_STORE_PASSWORD_OPTION);
		val trustStore = getResource(clientTrustStorePath);
		if (trustStore != null && trustStore.exists())
		{
			println("Using clientTrustStore " + trustStore.getURI());
			result.setInitParameter(TRUST_STORE_TYPE_OPTION, clientTrustStoreType);
			result.setInitParameter(TRUST_STORE_PATH_OPTION, clientTrustStorePath);
			result.setInitParameter(TRUST_STORE_PASSWORD_OPTION, clientTrustStorePassword);
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
		result.setInitParameter("applicationFactoryClassName", "org.apache.wicket.spring.SpringWebApplicationFactory");
		result.setInitParameter("applicationBean", "wicketApplication");
		result.setInitParameter("filterMappingUrlPattern", "/*");
		return result;
	}

	private ErrorPageErrorHandler createErrorHandler()
	{
		val result = new ErrorPageErrorHandler();
		val errorPages = new HashMap<String, String>();
		errorPages.put("404", "/404");
		result.setErrorPages(errorPages);
		return result;
	}

	protected ServletContextHandler createHealthContextHandler(CommandLine cmd, ContextLoaderListener contextLoaderListener) throws Exception
	{
		val result = new ServletContextHandler();
		result.setVirtualHosts(List.of("@" + HEALTH_CONNECTOR_NAME));
		result.setInitParameter("configuration", "deployment");
		result.setContextPath(DEFAULT_PATH);
		result.addServlet(HealthServlet.class, HEALTH_URL + "/*");
		return result;
	}

	protected Resource getResource(String path) throws IOException
	{
		val rh = new ResourceHandler();
		val result = ResourceFactory.of(rh).newResource(path);
		return result.exists() ? result : ResourceFactory.of(rh).newClassLoaderResource(path);
	}

	protected void createRealmFile(File file) throws IOException, NoSuchAlgorithmException
	{
		val builder = prompter.newBuilder();
		builder.createInputPrompt().name("username").message("enter username").defaultValue("admin").addPrompt();
		val results = prompter.prompt(Collections.emptyList(), builder.build());
		val username = ((InputResult)results.get("username")).getInput();
		val password = readPassword();
		println("Writing to file: " + file.getAbsoluteFile());
		FileUtils.writeStringToFile(file, username + ": " + password + ",user", Charset.defaultCharset(), false);
	}

	private String readPassword() throws IOException, NoSuchAlgorithmException
	{
		while (true)
		{
			val builder = prompter.newBuilder();
			builder.createInputPrompt().name("password").message("enter password").mask('*').validator(input -> input.length() >= 8).filter(this::toMD5).addPrompt();
			builder.createInputPrompt().name("password2").message("re-enter password").mask('*').filter(this::toMD5).addPrompt();
			val results = prompter.prompt(Collections.emptyList(), builder.build());
			val result = ((InputResult)results.get("password")).getInput();
			val password = ((InputResult)results.get("password2")).getInput();
			if (result.equals(password))
				return result;
			else
				println("Passwords don't match! Try again.");
		}
	}

	private String toMD5(String s)
	{
		return "MD5:" + DigestUtils.md5Hex(s);
	}

	private static Terminal createTerminal()
	{
		try
		{
			return TerminalBuilder.builder().system(true).build();
		}
		catch (IOException e)
		{
			throw new UncheckedIOException(e);
		}
	}

	protected SecurityHandler getSecurityHandler()
	{
		val result = new ConstraintSecurityHandler();
		val constraint = createSecurityConstraint();
		val mapping = createSecurityConstraintMapping(constraint);
		result.setConstraintMappings(Collections.singletonList(mapping));
		result.setAuthenticator(new BasicAuthenticator());
		result.setLoginService(new HashLoginService(REALM, createResource(REALM_FILE)));
		return result;
	}

	private Resource createResource(String realmFile)
	{
		return ResourceFactory.of(new ResourceHandler()).newResource(Path.of(realmFile));
	}

	private Constraint createSecurityConstraint()
	{
		return new Constraint.Builder().name("auth").roles("user", "admin").authorization(Authorization.FORBIDDEN).build();
	}

	private ConstraintMapping createSecurityConstraintMapping(final Constraint constraint)
	{
		val result = new ConstraintMapping();
		result.setPathSpec("/*");
		result.setConstraint(constraint);
		return result;
	}
}
