package nl.clockwork.ebms.admin;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.FilterMapping;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.springframework.web.context.ContextLoaderListener;

public class Main
{
	public static void main(String[] args) throws Exception
	{
		Server server = new Server(8080);
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		server.setHandler(context);

		context.setInitParameter("configuration","deployment"); //"development"
		context.setInitParameter("contextConfigLocation","classpath:applicationContext.xml");

		context.addServlet(org.eclipse.jetty.servlet.DefaultServlet.class,"/");
		
		FilterHolder filterHolder = new FilterHolder(org.apache.wicket.protocol.http.WicketFilter.class); 
		filterHolder.setInitParameter("applicationClassName","nl.clockwork.ebms.admin.web.WicketApplication");
		filterHolder.setInitParameter("filterMappingUrlPattern","/*");
		context.addFilter(filterHolder,"/*",FilterMapping.DEFAULT);
		
		ContextLoaderListener listener = new ContextLoaderListener();
		context.addEventListener(listener);

		server.start();
		server.join();
	}
}
