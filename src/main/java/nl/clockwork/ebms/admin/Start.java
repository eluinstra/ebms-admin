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

import org.apache.wicket.util.time.Duration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.server.ssl.SslSocketConnector;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;

public class Start
{
	public static void main(String[] args) throws Exception
	{
		int timeout = (int)Duration.ONE_HOUR.getMilliseconds();

		Server server = new Server();
		SocketConnector connector = new SocketConnector();

		if (args.length == 1 && "-help".equals(args[0]))
		{
			printUsage();
			return;
		}
		if (args.length == 0)
		{
			connector.setMaxIdleTime(timeout);
			connector.setSoLingerTime(-1);
			connector.setPort(8080);
			server.addConnector(connector);
			System.out.println("Application available on http://localhost:8080");
		}
		else if (args.length == 2)
		{
			Resource keystore = Resource.newClassPathResource(args[0]);
			if (keystore != null && keystore.exists())
			{
				connector.setConfidentialPort(8443);
				SslContextFactory factory = new SslContextFactory();
				factory.setKeyStoreResource(keystore);
				factory.setKeyStorePassword(args[1]);
				factory.setTrustStoreResource(keystore);
				factory.setKeyManagerPassword(args[1]);
				SslSocketConnector sslConnector = new SslSocketConnector(factory);
				sslConnector.setMaxIdleTime(timeout);
				sslConnector.setPort(8443);
				sslConnector.setAcceptors(4);
				server.addConnector(sslConnector);
				System.out.println("Application available on https://localhost:8443");
			}
			else
				System.out.println("Application not available: keystore" + args[0] + " not found!");
		}
		System.out.println();

		WebAppContext bb = new WebAppContext();
		bb.setServer(server);
		bb.setContextPath("/");
		bb.setWar("src/main/webapp");

		// MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
		// MBeanContainer mBeanContainer = new MBeanContainer(mBeanServer);
		// server.getContainer().addEventListener(mBeanContainer);
		// mBeanContainer.start();

		server.setHandler(bb);

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

	private static void printUsage()
	{
		System.out.println("Usage: nl.clockwork.ebms.admin.Start -help");
		System.out.println("       nl.clockwork.ebms.admin.Start");
		System.out.println("       nl.clockwork.ebms.admin.Start <keystore> <password>");
	}
}
