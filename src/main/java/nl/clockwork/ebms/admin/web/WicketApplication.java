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
package nl.clockwork.ebms.admin.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.clockwork.ebms.admin.web.menu.MenuItem;
import nl.clockwork.ebms.admin.web.menu.MenuLinkItem;

import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.core.request.handler.RenderPageRequestHandler;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;

/**
 * Application object for your web application. If you want to run this application without deploying, run the Start class.
 * 
 * @see nl.clockwork.ebms.admin.Start#main(String[])
 */
public class WicketApplication extends WebApplication
{
	public List<MenuItem> menuItems = new ArrayList<MenuItem>();
	public Map<String,MessagePageProvider.MessagePage> messagePages = new HashMap<String,MessagePageProvider.MessagePage>();
	public Map<String,MessagePageProvider.MessagePanel> messagePanels = new HashMap<String,MessagePageProvider.MessagePanel>();
	
	public WicketApplication()
	{
		MenuItem home = new MenuLinkItem("0","home",getHomePage());
		menuItems.add(home);
		
		MenuItem cpa = new MenuItem("1","cpaService");
		new MenuLinkItem(cpa,"1","cpas",nl.clockwork.ebms.admin.web.service.cpa.CPAsPage.class);
		new MenuLinkItem(cpa,"2","cpa",nl.clockwork.ebms.admin.web.service.cpa.CPAUploadPage.class);
		menuItems.add(cpa);

		MenuItem message = new MenuItem("2","messageService");
		new MenuLinkItem(message,"1","ping",nl.clockwork.ebms.admin.web.service.message.PingPage.class);
		new MenuLinkItem(message,"2","messages",nl.clockwork.ebms.admin.web.service.message.MessagesPage.class);
		new MenuLinkItem(message,"3","messageSend",nl.clockwork.ebms.admin.web.service.message.SendMessagePage.class);
		new MenuLinkItem(message,"3a","messageSend",nl.clockwork.ebms.admin.web.service.message.SendMessagePageX.class);
		new MenuLinkItem(message,"4","messageStatus",nl.clockwork.ebms.admin.web.service.message.MessageStatusPage.class);
		menuItems.add(message);

		MenuItem advanced = new MenuItem("3","advanced");
		new MenuLinkItem(advanced,"1","traffic",nl.clockwork.ebms.admin.web.message.TrafficPage.class);
		new MenuLinkItem(advanced,"2","trafficChart",nl.clockwork.ebms.admin.web.message.TrafficChartPage.class);
		new MenuLinkItem(advanced,"3","cpas",nl.clockwork.ebms.admin.web.cpa.CPAsPage.class);
		new MenuLinkItem(advanced,"4","messages",nl.clockwork.ebms.admin.web.message.MessagesPage.class);
		menuItems.add(advanced);

		MenuItem configuration = new MenuItem("4","configuration");
		new MenuLinkItem(configuration,"1","ebMSAdminProperties",nl.clockwork.ebms.admin.web.configuration.EbMSAdminPropertiesPage.class);
		new MenuLinkItem(configuration,"2","ebMSCoreProperties",nl.clockwork.ebms.admin.web.configuration.EbMSCorePropertiesPage.class);
		menuItems.add(configuration);

		List<MessagePageProvider> messagePageProviders = MessagePageProvider.get();
		if (messagePageProviders.size() > 0)
		{
			MenuItem extensions = new MenuItem("5","extensions");
			menuItems.add(extensions);
			for (MessagePageProvider provider : messagePageProviders)
				for (MenuItem menuItem : provider.getMenuItems())
					extensions.addChild(menuItem);
		}

		for (MessagePageProvider provider : messagePageProviders)
			for (MessagePageProvider.MessagePage messagePage : provider.getMessagePages())
				messagePages.put(messagePage.getId(),messagePage);
		
		for (MessagePageProvider provider : messagePageProviders)
			for (MessagePageProvider.MessagePanel messagePanel : provider.getMessagePanels())
				messagePanels.put(messagePanel.getId(),messagePanel);

		MenuItem about = new MenuLinkItem("6","about",nl.clockwork.ebms.admin.web.AboutPage.class);
		menuItems.add(about);
	}
	
	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	@Override
	public Class<? extends WebPage> getHomePage()
	{
		return HomePage.class;
	}

	/**
	 * @see org.apache.wicket.Application#init()
	 */
	@Override
	public void init()
	{
		super.init();
		getDebugSettings().setDevelopmentUtilitiesEnabled(true);
		getComponentInstantiationListeners().add(new SpringComponentInjector(this));
		getJavaScriptLibrarySettings().setJQueryReference(new JavaScriptResourceReference(HomePage.class,"../../../../../js/jquery-min.js"));
		getRequestCycleListeners().add(new AbstractRequestCycleListener()
		{
			@Override
			public IRequestHandler onException(RequestCycle cycle, Exception e)
			{
				return new RenderPageRequestHandler(new PageProvider(new ErrorPage(e)));
			}
		});
		mountPage("/404",PageNotFoundPage.class); 
	}
	
	public List<MenuItem> getMenuItems()
	{
		return menuItems;
	}
	
	public Map<String,MessagePageProvider.MessagePage> getMessagePages()
	{
		return messagePages;
	}

	public Map<String,MessagePageProvider.MessagePanel> getMessagePanels()
	{
		return messagePanels;
	}

	public static WicketApplication get()
	{
		return (WicketApplication)WebApplication.get();
	}
}
