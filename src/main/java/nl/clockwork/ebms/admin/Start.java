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
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;

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

		int timeout = Integer.MAX_VALUE;
		Server server = new Server();
		SocketConnector connector = new SocketConnector();

		if (!cmd.hasOption("ssl"))
		{
			connector.setMaxIdleTime(timeout);
			connector.setSoLingerTime(-1);
			connector.setPort(cmd.getOptionValue("p") == null ? 8080 : Integer.parseInt(cmd.getOptionValue("p")));
			server.addConnector(connector);
			System.out.println("Application available on http://localhost:" + connector.getPort());
		}
		else
		{
			String keyStore = getRequiredArg("keystore");
			String password = getRequiredArg("password");
			Resource keystore = Resource.newClassPathResource(keyStore);
			if (keystore != null && keystore.exists())
			{
				connector.setConfidentialPort(cmd.getOptionValue("p") == null ? 8080 : Integer.parseInt(cmd.getOptionValue("p")));
				SslContextFactory factory = new SslContextFactory();
				factory.setKeyStoreResource(keystore);
				factory.setKeyStorePassword(password);
				factory.setTrustStoreResource(keystore);
				factory.setKeyManagerPassword(password);
				SslSocketConnector sslConnector = new SslSocketConnector(factory);
				sslConnector.setMaxIdleTime(timeout);
				sslConnector.setPort(connector.getConfidentialPort());
				sslConnector.setAcceptors(4);
				server.addConnector(sslConnector);
				System.out.println("Application available on https://localhost:" + connector.getPort());
			}
			else
				System.out.println("Application not available: keystore" + args[0] + " not found!");
		}
		System.out.println();

		WebAppContext context = new WebAppContext();
		context.setServer(server);
		context.setContextPath("/");
		context.setWar("src/main/webapp");

		if (cmd.hasOption("jmx"))
		{
			MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
			MBeanContainer mBeanContainer = new MBeanContainer(mBeanServer);
			server.getContainer().addEventListener(mBeanContainer);
			mBeanContainer.start();
		}

		server.setHandler(context);

		try
		{
			System.out.println(">>> STARTING EMBEDDED JETTY SERVER, PRESS ANY KEY TO STOP");
			server.start();
			System.in.read();
			System.out.println(">>> STOPPING EMBEDDED JETTY SERVER");
			server.stop();
			server.join();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
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
