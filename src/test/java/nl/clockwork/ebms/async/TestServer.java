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
package nl.clockwork.ebms.async;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.DispatcherType;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.ContextLoaderListener;

import nl.clockwork.ebms.admin.PropertyPlaceholderConfigurer;
import nl.clockwork.ebms.admin.web.ExtensionProvider;
import nl.clockwork.ebms.service.CPAService;
import nl.clockwork.ebms.service.EbMSMessageService;

public class TestServer
{
	private Server server;
	private org.hsqldb.server.Server dbServer;
	protected final String DEFAULT_KEYSTORE_FILE = "nl/clockwork/ebms/admin/keystore.jks";
	protected final String DEFAULT_KEYSTORE_PASSWORD = "password";
	protected final String REALM = "Realm";
	protected final String REALM_FILE = "realm.properties";
	protected Map<String,String> properties;

	private CPAService cpaService = null;
	private EbMSMessageService ebmsMessageService = null;
	
	public void start() throws Exception
	{
		this.properties = getProperties("nl/clockwork/ebms/admin/applicationConfig.embedded.xml");

		startHSQLDBServer();
		this.server = new Server();
		initJMX();
		initWebContext();
		
		server.start();
	}
	
	private Map<String,String> getProperties(String...files)
	{
		try (AbstractApplicationContext applicationContext = new ClassPathXmlApplicationContext(files))
		{
			PropertyPlaceholderConfigurer properties = (PropertyPlaceholderConfigurer)applicationContext.getBean("propertyConfigurer");
			return properties.getProperties();
		}
	}

	public CPAService getCPAService()
	{
		if (cpaService == null)
		{
			JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean(); 
			factory.setServiceClass(CPAService.class); 
			factory.setAddress(String.format("http://%s:%d%s/service/cpa", "localhost", server.getConnectors()[0].getPort(), getPath())); 
			cpaService = (CPAService) factory.create();
		}

		return cpaService;
	}
	
	public String getEbmsEndpoint()
	{
		return String.format("http://%s:%s%s%s", "localhost", properties.get("ebms.port"), getPath(), properties.get("ebms.path"));
	}
	
	public String getEbmsServiceUrl()
	{
		return String.format("http://%s:%d%s/service/ebms", "localhost", server.getConnectors()[0].getPort(), getPath());
	}
	
	public EbMSMessageService getEbmsService()
	{
		if (ebmsMessageService == null)
		{
			JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean(); 
			factory.setServiceClass(EbMSMessageService.class); 
			factory.setAddress(getEbmsServiceUrl()); 
			ebmsMessageService = (EbMSMessageService) factory.create();
		}

		return ebmsMessageService;
	}
	
	public void stop() throws Exception
	{
		server.stop();
	}

	protected String getHost(String host)
	{
		return "0.0.0.0".equals(host) ? "localhost" : host;
	}

	private String getPath()
	{
		return "/ebms";
	}
	
	private void startHSQLDBServer() throws Exception
	{
		dbServer = new org.hsqldb.server.Server();
		dbServer.setDatabaseName(0, "ebms");
		dbServer.setDatabasePath(0, "mem:ebms");
		dbServer.setPort(9001);
		dbServer.setSilent(true);
		
		dbServer.start();
		// init database
		try (Connection c = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost:" + dbServer.getPort() + "/" + dbServer.getDatabaseName(0,true), "sa", ""))
		{
			c.createStatement().executeUpdate(IOUtils.toString(this.getClass().getResourceAsStream("/nl/clockwork/ebms/admin/database/hsqldb.sql")));
			System.out.println("EbMS tables created");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
		
	protected void initJMX() throws Exception
	{
		System.out.println("Starting mbean server...");
		MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
		MBeanContainer mBeanContainer = new MBeanContainer(mBeanServer);
		server.getContainer().addEventListener(mBeanContainer);
		mBeanContainer.start();
	}

	protected void initWebContext() throws Exception
	{
		// add connector 1 (webinterface / service)
		SocketConnector connector = new SocketConnector();
		connector.setHost("0.0.0.0");
		connector.setPort(8080);
		server.addConnector(connector);
		// add connector 2 (ebms)
		SocketConnector ebmsConnector = new SocketConnector();
		ebmsConnector.setHost(StringUtils.isEmpty(properties.get("ebms.host")) ? "0.0.0.0" : properties.get("ebms.host"));
		ebmsConnector.setPort(StringUtils.isEmpty(properties.get("ebms.port"))  ? 8888 : Integer.parseInt(properties.get("ebms.port")));
		server.addConnector(ebmsConnector);
		System.out.println("EbMS service configured on " + getEbmsEndpoint());
		System.out.println("Web server configured on http://" + getHost(connector.getHost()) + ":" + connector.getPort() + getPath());
		System.out.println("SOAP service configured on http://" + getHost(connector.getHost()) + ":" + connector.getPort() + getPath() + "/service");
		
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		server.setHandler(context);

		context.setContextPath(getPath());
		context.setInitParameter("configuration", "deployment");

		String contextConfigLocation = "classpath:nl/clockwork/ebms/admin/applicationContext.embedded.xml";
		for (ExtensionProvider extensionProvider : ExtensionProvider.get())
			if (!StringUtils.isEmpty(extensionProvider.getSpringConfigurationFile()))
				contextConfigLocation = "," + extensionProvider.getSpringConfigurationFile();

		context.setInitParameter("contextConfigLocation", contextConfigLocation);

		ServletHolder servletHolder = new ServletHolder(nl.clockwork.ebms.admin.web.ResourceServlet.class);
		context.addServlet(servletHolder,"/css/*");
		context.addServlet(servletHolder,"/fonts/*");
		context.addServlet(servletHolder,"/images/*");
		context.addServlet(servletHolder,"/js/*");

		context.addServlet(org.apache.cxf.transport.servlet.CXFServlet.class, "/service/*");
		context.addServlet(nl.clockwork.ebms.servlet.EbMSServlet.class, "/digipoortStub" /*properties.get("ebms.path")*/);
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

	protected Resource getResource(String path) throws MalformedURLException, IOException
	{
		Resource result = Resource.newResource(path);
		return result.exists() ? result : Resource.newClassPathResource(path);
	}


}
