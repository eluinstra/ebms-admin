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
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.MBeanServer;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.jmx.MBeanContainer;
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
import org.springframework.web.context.ContextLoaderListener;

public class StartEmbedded
{
	private static Options options;
	private static CommandLine cmd;

	public static void main(String[] args) throws Exception
	{
		createOptions();
		cmd = new BasicParser().parse(options,args);

		if (cmd.hasOption("h"))
			printUsage();

		if (cmd.hasOption("hsqldb"))
		{
			System.out.println("Starting hsqldb...");
			startHSQLDBServerX();
		}
		System.out.println();

		Server server = new Server();

		if (!cmd.hasOption("ssl"))
		{
			SocketConnector connector = new SocketConnector();
			connector.setPort(cmd.getOptionValue("p") == null ? 8080 : Integer.parseInt(cmd.getOptionValue("p")));
			server.addConnector(connector);
			System.out.println("Web Server configured on http://localhost:" + connector.getPort());
		}
		else
		{
			String keystorePath = getRequiredArg("keystore");
			String keystorePassword = getRequiredArg("password");
			Resource keystore = getResource(keystorePath);
			if (keystore != null && keystore.exists())
			{
				SocketConnector connector = new SocketConnector();
				connector.setConfidentialPort(cmd.getOptionValue("p") == null ? 8433 : Integer.parseInt(cmd.getOptionValue("p")));
				SslContextFactory factory = new SslContextFactory();
				factory.setKeyStoreResource(keystore);
				factory.setKeyStorePassword(keystorePassword);
				SslSocketConnector sslConnector = new SslSocketConnector(factory);
				sslConnector.setPort(connector.getConfidentialPort());
				//sslConnector.setAcceptors(4);
				server.addConnector(sslConnector);
				System.out.println("Web Server configured on https://localhost:" + connector.getPort());
			}
			else
			{
				System.out.println("Web Server not available: keystore " + keystorePath + " not found!");
				System.exit(1);
			}
		}

		if (!cmd.hasOption("ebmsSsl"))
		{
			SocketConnector connector = new SocketConnector();
			connector.setPort(cmd.getOptionValue("ebmsPort") == null ? 8888 : Integer.parseInt(cmd.getOptionValue("ebmsPort")));
			server.addConnector(connector);
			System.out.println("EbMS Service configured on http://localhost:" + connector.getPort() + getEbMSUrl());
		}
		else
		{
			String keystorePath = getRequiredArg("ebmsKeystore");
			String keystorePassword = getRequiredArg("ebmsKeystorePassword");
			Resource keystore = getResource(keystorePath);
			if (keystore != null && keystore.exists())
			{
				SocketConnector connector = new SocketConnector();
				connector.setConfidentialPort(cmd.getOptionValue("ebmsPort") == null ? 8888 : Integer.parseInt(cmd.getOptionValue("ebmsPort")));
				SslContextFactory factory = new SslContextFactory();
				if (cmd.hasOption("ebmsSslCipherSuites"))
					factory.setIncludeCipherSuites(cmd.getOptionValues("ebmsSslCipherSuites"));
				factory.setKeyStoreResource(keystore);
				factory.setKeyStorePassword(keystorePassword);
				if (cmd.hasOption("ebmsClientAuth"))
				{
					String truststorePath = getRequiredArg("ebmsTruststore");
					String truststorePassword = getRequiredArg("ebmsTruststorePassword");
					Resource truststore = getResource(truststorePath);
					if (truststore != null && truststore.exists())
					{
						factory.setNeedClientAuth(true);
						factory.setTrustStoreResource(truststore);
						factory.setTrustStorePassword(truststorePassword);
					}
					else
					{
						System.out.println("EbMS Service not available: truststore " + truststorePath + " not found!");
						System.exit(1);
					}
				}
				SslSocketConnector sslConnector = new SslSocketConnector(factory);
				sslConnector.setPort(connector.getConfidentialPort());
				//sslConnector.setAcceptors(4);
				server.addConnector(sslConnector);
				System.out.println("EbMS Service configured on https://localhost:" + connector.getConfidentialPort() + getEbMSUrl());
				if (!cmd.hasOption("ebmsSslVerifyHostnames"))
					HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier()
					{
						@Override
						public boolean verify(String hostname, SSLSession sslSession)
						{
							return true;
						}
					});
			}
			else
			{
				System.out.println("EbMS Service not available: keystore " + keystorePath + " not found!");
				System.exit(1);
			}
		}

		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");

		if (cmd.hasOption("jmx"))
		{
			System.out.println("Starting mbean server...");
			startMBeanServer(server);
		}

		server.setHandler(context);

		context.setInitParameter("configuration","deployment");
		context.setInitParameter("contextConfigLocation","classpath:applicationContext.embedded.xml");

		ServletHolder servletHolder = new ServletHolder(nl.clockwork.ebms.admin.web.ResourceServlet.class);
		context.addServlet(servletHolder,"/css/*");
		context.addServlet(servletHolder,"/fonts/*");
		context.addServlet(servletHolder,"/images/*");
		context.addServlet(servletHolder,"/js/*");

		context.addServlet(nl.clockwork.ebms.servlet.EbMSServlet.class,getEbMSUrl());

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
		
//		Server ebMSServer = new Server();
//		connector = new SocketConnector();
//		connector.setPort(8888);
//		ebMSServer.addConnector(connector);
//		context = new ServletContextHandler(ServletContextHandler.SESSIONS);
//		context.setContextPath("/");
//		context.setInitParameter("contextConfigLocation","classpath:applicationContext.embedded.xml");
//		context.addServlet(nl.clockwork.ebms.servlet.EbMSServlet.class,"/digipoortStub");
//		listener = new ContextLoaderListener();
//		context.addEventListener(listener);
//		ebMSServer.start();

		System.out.println("Starting web server...");
		System.out.println();

		server.start();
		server.join();
	}

	private static Resource getResource(String path) throws MalformedURLException, IOException
	{
		return path.startsWith("classpath:") ? Resource.newClassPathResource(path.substring("classpath:".length())) : Resource.newResource(path);
	}

	private static String getEbMSUrl()
	{
		if (cmd.hasOption("ebmsUrl"))
			return cmd.getOptionValue("ebmsUrl");
		else
			return "/digipoortStub";
	}

	private static Options createOptions()
	{
		options = new Options();
		options.addOption("h",false,"print this message");
		options.addOption("p",true,"set port");
		options.addOption("ssl",false,"use ssl");
		options.addOption("keystore",true,"set keystore");
		options.addOption("password",true,"set keystore password");
		options.addOption("ebmsPort",true,"set ebms port");
		options.addOption("ebmsUrl",true,"set ebms url");
		options.addOption("ebmsSsl",false,"use ebms ssl");
		Option option = new Option("ebmsSslCipherSuites",true,"allowed ebms ssl cipher suites");
		option.setValueSeparator(',');
		option.setArgs(Integer.MAX_VALUE);
		options.addOption(option);
		options.addOption("ebmsSslVerifyHostnames",false,"verify ebms ssl hostnames");
		options.addOption("ebmsClientAuth",false,"use ebms ssl client authentication");
		options.addOption("ebmsKeystore",true,"set ebms keystore");
		options.addOption("ebmsKeystorePassword",true,"set ebms keystore password");
		options.addOption("ebmsTruststore",true,"set ebms truststore");
		options.addOption("ebmsTruststorePassword",true,"set ebms truststore password");
		options.addOption("jmx",false,"start mbean server");
		options.addOption("hsqldb",false,"start hsqldb server");
		options.addOption("hsqldbFile",true,"set hsqldb file location (default: hsqldb/ebms)");
		options.addOption("hsqldbPort",true,"set hsqldb port (default: 9001)");
		return options;
	}
	
	private static String getRequiredArg(String arg)
	{
		String result = cmd.getOptionValue(arg);
		if (result == null)
		{
			System.out.println(arg + " is not set!");
			System.out.println();
			printUsage();
		}
		return result;
	}
	
	private static void printUsage()
	{
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("Start",options,true);
		System.exit(0);
	}
	
	public static void startMBeanServer(Server server) throws Exception
	{
		MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
		MBeanContainer mBeanContainer = new MBeanContainer(mBeanServer);
		server.getContainer().addEventListener(mBeanContainer);
		mBeanContainer.start();
	}

	public static void startHSQLDBServer()
	{
		List<String> options = new ArrayList<String>();
		options.add("-database.0");
		options.add(cmd.hasOption("hsqldbFile") ? "file:" + cmd.getOptionValue("hsqldbFile") : "file:hsqldb/ebms");
		options.add("-dbname.0");
		options.add("ebms");
		options.add("-port");
		options.add(cmd.hasOption("hsqldbPort") ? cmd.getOptionValue("hsqldbPort") : "9001");
		org.hsqldb.Server.main(options.toArray(new String[0]));
	}

	public static void startHSQLDBServerX() throws IOException, AclFormatException
	{
		List<String> options = new ArrayList<String>();
		options.add("-database.0");
		options.add(cmd.hasOption("hsqldbFile") ? "file:" + cmd.getOptionValue("hsqldbFile") : "file:hsqldb/ebms");
		options.add("-dbname.0");
		options.add("ebms");
		options.add("-port");
		options.add(cmd.hasOption("hsqldbPort") ? cmd.getOptionValue("hsqldbPort") : "9001");
		
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

	private static void initDatabase(org.hsqldb.server.Server server)
	{
		Connection c = null;
    try
		{
			c = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost:" + server.getPort() + "/ebms", "sa", "");
			if (!c.createStatement().executeQuery("select table_name from information_schema.tables where table_name = 'CPA'").next())
			{
				c.createStatement().executeUpdate(IOUtils.toString(StartEmbedded.class.getResourceAsStream("hsqldb.sql")));
				System.out.println("EbMS tables created");
			}
			else
				System.out.println("EbMS tables already exist");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
    finally
    {
    	if (c != null)
				try
				{
					c.close();
				}
				catch (SQLException e)
				{
					e.printStackTrace();
				}
    }
	}

}
