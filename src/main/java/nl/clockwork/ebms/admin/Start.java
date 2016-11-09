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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServer;

import nl.clockwork.ebms.admin.web.ExtensionProvider;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.DispatcherType;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.server.ssl.SslSocketConnector;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.web.context.ContextLoaderListener;

public class Start
{
	protected final String DEFAULT_KEYSTORE_FILE = "nl/clockwork/ebms/admin/keystore.jks";
	protected final String DEFAULT_KEYSTORE_PASSWORD = "password";
	protected final String REALM = "Realm";
	protected final String REALM_FILE = "realm.properties";
	protected Options options;
	protected CommandLine cmd;
	protected Server server;

	public static void main(String[] args) throws Exception
	{
		Start start = new Start();
		start.initCmd(args);

		if (start.cmd.hasOption("h"))
			start.printUsage();

		start.server = new Server();

		start.initWebServer();
		start.initJMX();
		start.initWebContext();

		System.out.println("Starting web server...");

		start.server.start();
		start.server.join();
	}

	protected void initCmd(String[] args) throws ParseException
	{
		createOptions();
		cmd = new BasicParser().parse(options,args);
	}

	protected Options createOptions()
	{
		options = new Options();
		options.addOption("h",false,"print this message");
		options.addOption("host",true,"set host");
		options.addOption("port",true,"set port");
		options.addOption("path",true,"set path");
		options.addOption("ssl",false,"use ssl");
		options.addOption("keystore",true,"set keystore");
		options.addOption("password",true,"set keystore password");
		options.addOption("authentication",false,"use basic authentication");
		options.addOption("jmx",false,"start mbean server");
		return options;
	}
	
	protected void initWebServer() throws MalformedURLException, IOException
	{
		if (!cmd.hasOption("ssl"))
		{
			SocketConnector connector = new SocketConnector();
			connector.setHost(cmd.getOptionValue("host") == null ? "0.0.0.0" : cmd.getOptionValue("host"));
			connector.setPort(cmd.getOptionValue("port") == null ? 8080 : Integer.parseInt(cmd.getOptionValue("port")));
			server.addConnector(connector);
			System.out.println("Web server configured on http://" + getHost(connector.getHost()) + ":" + connector.getPort() + getPath());
		}
		else
		{
			String keystorePath = cmd.getOptionValue("keystore",DEFAULT_KEYSTORE_FILE);
			String keystorePassword = cmd.getOptionValue("password",DEFAULT_KEYSTORE_PASSWORD);
			if (DEFAULT_KEYSTORE_FILE.equals(keystorePath))
				System.out.println("Using default keystore!");
			else
				System.out.println("Using keystore " + new File(keystorePath).getAbsolutePath());
			Resource keystore = getResource(keystorePath);
			if (keystore != null && keystore.exists())
			{
				SslContextFactory factory = new SslContextFactory();
				factory.setKeyStoreResource(keystore);
				factory.setKeyStorePassword(keystorePassword);
				SslSocketConnector connector = new SslSocketConnector(factory);
				connector.setHost(cmd.getOptionValue("host") == null ? "0.0.0.0" : cmd.getOptionValue("host"));
				connector.setPort(cmd.getOptionValue("port") == null ? 8433 : Integer.parseInt(cmd.getOptionValue("port")));
				server.addConnector(connector);
				System.out.println("Web server configured on https://" + getHost(connector.getHost()) + ":" + connector.getPort());
			}
			else
			{
				System.out.println("Web server not available: keystore " + keystorePath + " not found!");
				System.exit(1);
			}
		}
	}

	protected String getPath()
	{
		return cmd.getOptionValue("path") == null ? "/" : cmd.getOptionValue("path");
	}

	protected void initJMX() throws Exception
	{
		if (cmd.hasOption("jmx"))
		{
			System.out.println("Starting mbean server...");
			MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
			MBeanContainer mBeanContainer = new MBeanContainer(mBeanServer);
			server.getContainer().addEventListener(mBeanContainer);
			mBeanContainer.start();
		}
	}

	protected void initWebContext() throws Exception
	{
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		server.setHandler(context);

		context.setContextPath(getPath());

		if (cmd.hasOption("authentication"))
		{
			System.out.println("Configuring web server authentication:");
			File file = new File(REALM_FILE);
			if (file.exists())
				System.out.println("Using file: " + file.getAbsoluteFile());
			else
				createRealmFile(file);
			context.setSecurityHandler(getSecurityHandler());
		}

		context.setInitParameter("configuration","deployment");

		String contextConfigLocation = "classpath:nl/clockwork/ebms/admin/applicationContext.xml";
		for (ExtensionProvider extensionProvider : ExtensionProvider.get())
			if (!StringUtils.isEmpty(extensionProvider.getSpringConfigurationFile()))
				contextConfigLocation = "," + extensionProvider.getSpringConfigurationFile();

		context.setInitParameter("contextConfigLocation",contextConfigLocation);

		ServletHolder servletHolder = new ServletHolder(nl.clockwork.ebms.admin.web.ResourceServlet.class);
		context.addServlet(servletHolder,"/css/*");
		context.addServlet(servletHolder,"/fonts/*");
		context.addServlet(servletHolder,"/images/*");
		context.addServlet(servletHolder,"/js/*");

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
	}

	protected void printUsage()
	{
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("Start",options,true);
		System.exit(0);
	}

	protected Resource getResource(String path) throws MalformedURLException, IOException
	{
		Resource result = Resource.newResource(path);
		return result.exists() ? result : Resource.newClassPathResource(path);
	}

	protected void createRealmFile(File file) throws IOException, NoSuchAlgorithmException
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String username = readLine("enter username: ",reader);
		String password = readPassword(reader);
		System.out.println("Writing to file: " + file.getAbsoluteFile());
		FileUtils.writeStringToFile(file,username + ": " + password + ",user",false);
	}

	private String readLine(String prompt, BufferedReader reader) throws IOException
	{
		String result = null;
		while (StringUtils.isBlank(result))
		{
			System.out.print(prompt);
			result = reader.readLine();
		}
		return result;
	}

	private String readPassword(BufferedReader reader) throws IOException, NoSuchAlgorithmException
	{
		String result = null;
		while (true)
		{
			result = toMD5(readLine("enter password: ",reader));
			String password = toMD5(readLine("re-enter password: ",reader));
			if (!result.equals(password))
				System.out.println("Passwords don't match! Try again.");
			else
				break;
		}
		return result;
	}
	
	private String toMD5(String s) throws NoSuchAlgorithmException, UnsupportedEncodingException
	{
		return "MD5:" + DigestUtils.md5Hex(s);
	}

	protected SecurityHandler getSecurityHandler()
	{
		ConstraintSecurityHandler security = new ConstraintSecurityHandler();

		Constraint constraint = new Constraint();
		constraint.setName("auth");
		constraint.setAuthenticate(true);
		constraint.setRoles(new String[]{"user","admin"});

		ConstraintMapping mapping = new ConstraintMapping();
		mapping.setPathSpec("/*");
		mapping.setConstraint(constraint);

		security.setConstraintMappings(Collections.singletonList(mapping));
		security.setAuthenticator(new BasicAuthenticator());
		security.setLoginService(new HashLoginService(REALM,REALM_FILE));

		return security;
	}

	protected String getHost(String host)
	{
		return "0.0.0.0".equals(host) ? "localhost" : host;
	}

}
