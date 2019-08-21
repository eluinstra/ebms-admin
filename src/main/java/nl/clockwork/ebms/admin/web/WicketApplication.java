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

import nl.clockwork.ebms.admin.web.menu.MenuDivider;
import nl.clockwork.ebms.admin.web.menu.MenuItem;
import nl.clockwork.ebms.admin.web.menu.MenuLinkItem;

import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.core.request.handler.RenderPageRequestHandler;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.IRequestCycleListener;
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
	public Map<String,MessageProvider.MessageViewPanel> messageViewPanels = new HashMap<String,MessageProvider.MessageViewPanel>();
	public Map<String,MessageProvider.MessageEditPanel> messageEditPanels = new HashMap<String,MessageProvider.MessageEditPanel>();
	
	public WicketApplication()
	{
		MenuItem home = new MenuLinkItem("0","home",getHomePage());
		menuItems.add(home);
		
		MenuItem cpa = new MenuItem("1","cpaService");
		new MenuLinkItem(cpa,"1","cpas",nl.clockwork.ebms.admin.web.service.cpa.CPAsPage.class);
		new MenuDivider(cpa,"2");
		new MenuLinkItem(cpa,"3","urlMappings",nl.clockwork.ebms.admin.web.service.cpa.URLMappingsPage.class);
		menuItems.add(cpa);

		MenuItem message = new MenuItem("2","messageService");
		new MenuLinkItem(message,"1","ping",nl.clockwork.ebms.admin.web.service.message.PingPage.class);
		new MenuDivider(message,"2");
		new MenuLinkItem(message,"3","receivedMessages",nl.clockwork.ebms.admin.web.service.message.MessagesPage.class);
		new MenuLinkItem(message,"4","messageEvents",nl.clockwork.ebms.admin.web.service.message.MessageEventsPage.class);
		new MenuDivider(message,"5");
		//new MenuLinkItem(message,"6","messageSend",nl.clockwork.ebms.admin.web.service.message.SendMessagePage.class);
		new MenuLinkItem(message,"6","messageSend",nl.clockwork.ebms.admin.web.service.message.SendMessagePageX.class);
		new MenuLinkItem(message,"7","messageResend",nl.clockwork.ebms.admin.web.service.message.ResendMessagePage.class);
		new MenuDivider(message,"8");
		new MenuLinkItem(message,"9","messageStatus",nl.clockwork.ebms.admin.web.service.message.MessageStatusPage.class);
		menuItems.add(message);

		MenuItem advanced = new MenuItem("3","advanced");
		new MenuLinkItem(advanced,"1","traffic",nl.clockwork.ebms.admin.web.message.TrafficPage.class);
		new MenuLinkItem(advanced,"2","trafficChart",nl.clockwork.ebms.admin.web.message.TrafficChartPage.class);
		new MenuDivider(advanced,"3");
		new MenuLinkItem(advanced,"4","cpas",nl.clockwork.ebms.admin.web.cpa.CPAsPage.class);
		new MenuLinkItem(advanced,"5","messages",nl.clockwork.ebms.admin.web.message.MessagesPage.class);
		menuItems.add(advanced);

		MenuItem configuration = new MenuItem("4","configuration");
		new MenuLinkItem(configuration,"1","ebMSAdminProperties",nl.clockwork.ebms.admin.web.configuration.EbMSAdminPropertiesPage.class);
		new MenuDivider(configuration,"2");
		new MenuLinkItem(configuration,"3","ebMSCoreProperties",nl.clockwork.ebms.admin.web.configuration.EbMSCorePropertiesPage.class);
		menuItems.add(configuration);

		List<ExtensionProvider> extensionProviders = ExtensionProvider.get();
		if (extensionProviders.size() > 0)
		{
			MenuItem extensions = new MenuItem("5","extensions");
			menuItems.add(extensions);
			int i = 1;
			for (ExtensionProvider provider : extensionProviders)
			{
				MenuItem epmi = new MenuItem("" + i++,provider.getName());
				extensions.addChild(epmi);
				for (MenuItem menuItem : provider.getMenuItems())
					epmi.addChild(menuItem);
			}
		}

		List<MessageProvider> messageProviders = MessageProvider.get();
		for (MessageProvider provider : messageProviders)
			for (MessageProvider.MessageViewPanel messagePanel : provider.getMessageViewPanels())
				messageViewPanels.put(messagePanel.getId(),messagePanel);

		for (MessageProvider provider : messageProviders)
			for (MessageProvider.MessageEditPanel messagePanel : provider.getMessageEditPanels())
				messageEditPanels.put(messagePanel.getId(),messagePanel);

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
		getRequestCycleListeners().add(new IRequestCycleListener()
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

	public Map<String,MessageProvider.MessageViewPanel> getMessageViewPanels()
	{
		return messageViewPanels;
	}

	public Map<String,MessageProvider.MessageEditPanel> getMessageEditPanels()
	{
		return messageEditPanels;
	}

	public static WicketApplication get()
	{
		return (WicketApplication)WebApplication.get();
	}
}
