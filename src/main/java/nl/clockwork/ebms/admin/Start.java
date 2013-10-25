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

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.server.ssl.SslSocketConnector;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.FilterMapping;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.web.context.ContextLoaderListener;

public class Start
{
	private static Options options;
	private static CommandLine cmd;

	public static void main(String[] args) throws Exception
	{
		createOptions();
		cmd = new BasicParser().parse(options,args);

		if (cmd.hasOption("h"))
			printUsage();

		Server server = new Server();
		SocketConnector connector = new SocketConnector();

		if (!cmd.hasOption("ssl"))
		{
			connector.setPort(cmd.getOptionValue("p") == null ? 8080 : Integer.parseInt(cmd.getOptionValue("p")));
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
			MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
			MBeanContainer mBeanContainer = new MBeanContainer(mBeanServer);
			server.getContainer().addEventListener(mBeanContainer);
			mBeanContainer.start();
		}
		
		server.setHandler(context);

		context.setInitParameter("configuration","deployment");
		context.setInitParameter("contextConfigLocation","classpath:applicationContext.xml");

		context.addServlet(org.eclipse.jetty.servlet.DefaultServlet.class,"/");
		
		FilterHolder filterHolder = new FilterHolder(org.apache.wicket.protocol.http.WicketFilter.class); 
		filterHolder.setInitParameter("applicationClassName","nl.clockwork.ebms.admin.web.WicketApplication");
		filterHolder.setInitParameter("filterMappingUrlPattern","/*");
		context.addFilter(filterHolder,"/*",FilterMapping.DEFAULT);
		
		ContextLoaderListener listener = new ContextLoaderListener();
		context.addEventListener(listener);
		
		System.out.println();
		System.out.println("Starting web server...");

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
}
