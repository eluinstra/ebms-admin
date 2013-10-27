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
import java.util.List;

import nl.clockwork.ebms.admin.web.menu.MenuItem;
import nl.clockwork.ebms.admin.web.menu.MenuLinkItem;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;

/**
 * Application object for your web application. If you want to run this application without deploying, run the Start class.
 * 
 * @see nl.clockwork.ebms.admin.Start#main(String[])
 */
public class WicketApplication extends WebApplication
{
	public List<MenuItem> menuItems = new ArrayList<MenuItem>();
	
	public WicketApplication()
	{
		MenuItem home = new MenuLinkItem("0","Home",nl.clockwork.ebms.admin.web.HomePage.class);
		menuItems.add(home);
		
		MenuItem cpa = new MenuItem("1","cpaService");
		new MenuLinkItem(cpa,"1","cpas",nl.clockwork.ebms.admin.web.service.cpa.CPAsPage.class);
		new MenuLinkItem(cpa,"2","cpa",nl.clockwork.ebms.admin.web.service.cpa.CPAUploadPage.class);
		menuItems.add(cpa);

		MenuItem message = new MenuItem("2","messageService");
		new MenuLinkItem(message,"1","ping",nl.clockwork.ebms.admin.web.service.message.PingPage.class);
		new MenuLinkItem(message,"2","messages",nl.clockwork.ebms.admin.web.service.message.MessagesPage.class);
		new MenuLinkItem(message,"3","message",nl.clockwork.ebms.admin.web.service.message.SendMessagePage.class);
		new MenuLinkItem(message,"3","status",nl.clockwork.ebms.admin.web.service.message.MessageStatusPage.class);
		menuItems.add(message);

		MenuItem advanced = new MenuItem("3","advanced");
		new MenuLinkItem(advanced,"1","traffic",nl.clockwork.ebms.admin.web.message.TrafficPage.class);
		new MenuLinkItem(advanced,"2","trafficChart",nl.clockwork.ebms.admin.web.message.TrafficChartPage.class);
		new MenuLinkItem(advanced,"3","cpas",nl.clockwork.ebms.admin.web.cpa.CPAsPage.class);
		new MenuLinkItem(advanced,"4","messages",nl.clockwork.ebms.admin.web.message.MessagesPage.class);
		menuItems.add(advanced);

		MenuItem configuration = new MenuItem("4","configuration");
		new MenuLinkItem(configuration,"1","ebMSAdmin",nl.clockwork.ebms.admin.web.configuration.EbMSAdminPropertiesPage.class);
		new MenuLinkItem(configuration,"2","ebMSCore",nl.clockwork.ebms.admin.web.HomePage.class);
		menuItems.add(configuration);

		MenuItem about = new MenuLinkItem("5","about",nl.clockwork.ebms.admin.web.HomePage.class);
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
	}
	
	public List<MenuItem> getMenuItems()
	{
		return menuItems;
	}

	public static WicketApplication get()
	{
		return (WicketApplication)WebApplication.get();
	}
}
