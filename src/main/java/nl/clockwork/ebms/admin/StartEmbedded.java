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
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.MBeanServer;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
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
		SocketConnector connector = new SocketConnector();

		if (!cmd.hasOption("ssl"))
		{
			connector.setPort(cmd.getOptionValue("p") == null ? 8888 : Integer.parseInt(cmd.getOptionValue("p")));
			server.addConnector(connector);
			System.out.println("Web server configured on http://localhost:" + connector.getPort());
		}
		else
		{
			String keyStore = getRequiredArg("keystore");
			String password = getRequiredArg("password");
			Resource keystore = Resource.newClassPathResource(keyStore);
			if (keystore != null && keystore.exists())
			{
				connector.setConfidentialPort(cmd.getOptionValue("p") == null ? 8433 : Integer.parseInt(cmd.getOptionValue("p")));
				SslContextFactory factory = new SslContextFactory();
				factory.setKeyStoreResource(keystore);
				factory.setKeyStorePassword(password);
				//factory.setNeedClientAuth(clientAuth);
				//factory.setTrustStoreResource(truststore);
				//factory.setTrustStorePassword(truststore.password);
				SslSocketConnector sslConnector = new SslSocketConnector(factory);
				sslConnector.setPort(connector.getConfidentialPort());
				//sslConnector.setAcceptors(4);
				server.addConnector(sslConnector);
				System.out.println("Web server configured on https://localhost:" + connector.getPort());
			}
			else
				System.out.println("Web server not available: keystore" + args[0] + " not found!");
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

		context.addServlet(nl.clockwork.ebms.servlet.EbMSServlet.class,"/digipoortStub");

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

	private static Options createOptions()
	{
		options = new Options();
		options.addOption("h",false,"print this message");
		options.addOption("p",true,"set port");
		options.addOption("ssl",false,"use ssl");
		options.addOption("keystore",true,"set keystore");
		options.addOption("password",true,"set keystore password");
		options.addOption("jmx",false,"start mbean server");
		options.addOption("hsqldb",false,"start hsqldb server");
		options.addOption("hsqldbFile",true,"set hsqldb file location (default: hsqldb/ebms)");
		options.addOption("hsqldbPort",true,"set hsqldb port (default: 9001)");
		return options;
	}
	
	private static String getRequiredArg(String arg)
	{
		String result = cmd.getOptionValue("keystore");
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
		
	}

}
