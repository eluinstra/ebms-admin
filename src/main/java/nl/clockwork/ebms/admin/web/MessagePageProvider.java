package nl.clockwork.ebms.admin.web;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import nl.clockwork.ebms.admin.model.EbMSMessage;
import nl.clockwork.ebms.admin.web.menu.MenuItem;
import nl.clockwork.ebms.admin.web.service.message.DataSourcesPanel;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebPage;

public abstract class MessagePageProvider
{
	public static List<MessagePageProvider> get()
	{
		ServiceLoader<MessagePageProvider> providers = ServiceLoader.load(MessagePageProvider.class);
		List<MessagePageProvider> result = new ArrayList<MessagePageProvider>();
		for (MessagePageProvider provider : providers)
			result.add(provider);
		return result;
	}

	public static Object createId(EbMSMessage message)
	{
		return createId(message.getService(),message.getAction());
	}
	public static String createId(String service, String action)
	{
		return service + ":" + action;
	}

	public abstract List<MenuItem> getMenuItems();

	public abstract List<MessagePage> getMessagePages();

	public abstract List<MessagePanel> getMessagePanels();

	public static abstract class MessagePage
	{
		private String service;
		private String action;

		public MessagePage()
		{
		}
		public MessagePage(String service, String action)
		{
			this.service = service;
			this.action = action;
		}
		public String getId()
		{
			return createId(service,action);
		}
		public String getService()
		{
			return service;
		}
		public void setService(String service)
		{
			this.service = service;
		}
		public String getAction()
		{
			return action;
		}
		public void setAction(String action)
		{
			this.action = action;
		}
		public abstract Page getPage(EbMSMessage message, WebPage responsePage);
	}

	public static abstract class MessagePanel
	{
		private String service;
		private String action;

		public MessagePanel()
		{
		}
		public MessagePanel(String service, String action)
		{
			this.service = service;
			this.action = action;
		}
		public String getId()
		{
			return createId(service,action);
		}
		public String getService()
		{
			return service;
		}
		public void setService(String service)
		{
			this.service = service;
		}
		public String getAction()
		{
			return action;
		}
		public void setAction(String action)
		{
			this.action = action;
		}
		public abstract DataSourcesPanel getPanel(String id);
	}
}
