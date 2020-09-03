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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.core.request.handler.RenderPageRequestHandler;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.IRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;
import nl.clockwork.ebms.admin.EmbeddedAppConfig;
import nl.clockwork.ebms.admin.PropertySourcesPlaceholderConfigurer;
import nl.clockwork.ebms.admin.web.menu.MenuDivider;
import nl.clockwork.ebms.admin.web.menu.MenuItem;
import nl.clockwork.ebms.admin.web.menu.MenuLinkItem;
import nl.clockwork.ebms.event.MessageEventListenerConfig.EventListenerType;

/**
 * Application object for your web application. If you want to run this application without deploying, run the Start class.
 * 
 * @see nl.clockwork.ebms.admin.Start#main(String[])
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class WicketApplication extends WebApplication
{
	@NonNull
	Integer maxItemsPerPage;
	@NonNull
	EventListenerType eventListenerType;
	List<MenuItem> menuItems = new ArrayList<>();
	Map<String,MessageProvider.MessageViewPanel> messageViewPanels = new HashMap<>();
	Map<String,MessageProvider.MessageEditPanel> messageEditPanels = new HashMap<>();
	PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = EmbeddedAppConfig.PROPERTY_SOURCE;
	
	public WicketApplication(@NonNull Integer maxItemsPerPage, @NonNull EventListenerType eventListenerType)
	{
		this.maxItemsPerPage = maxItemsPerPage;
		this.eventListenerType = eventListenerType;
		menuItems.add(createHomeMenuItem("0"));
		menuItems.add(createCPAServiceMenuItem("1"));
		menuItems.add(createMEssageServiceMenuItem("2"));
		menuItems.add(createAdvancedMenuItem("3"));
		menuItems.add(createConfigurationMenuItem("4"));

		val extensionProviders = ExtensionProvider.get();
		if (extensionProviders.size() > 0)
			menuItems.add(createExtensionsMenuItem("5",extensionProviders));

		val messageProviders = MessageProvider.get();
		messageProviders.forEach(p ->p.getMessageViewPanels().forEach(vp -> messageViewPanels.put(vp.getId(),vp)));
		messageProviders.forEach(p -> p.getMessageEditPanels().forEach(ep -> messageEditPanels.put(ep.getId(),ep)));

		menuItems.add(createAboutMEnuItem("6"));
	}

	private MenuItem createHomeMenuItem(String id)
	{
		return new MenuLinkItem(id,"home",getHomePage());
	}

	private MenuItem createCPAServiceMenuItem(String id)
	{
		val result = new MenuItem(id,"cpaService");
		new MenuLinkItem(result,"1","cpas",nl.clockwork.ebms.admin.web.service.cpa.CPAsPage.class);
		new MenuDivider(result,"2");
		new MenuLinkItem(result,"3","urlMappings",nl.clockwork.ebms.admin.web.service.cpa.URLMappingsPage.class);
		new MenuDivider(result,"4");
		new MenuLinkItem(result,"5","certificateMappings",nl.clockwork.ebms.admin.web.service.cpa.CertificateMappingsPage.class);
		return result;
	}

	private MenuItem createMEssageServiceMenuItem(String id)
	{
		val result = new MenuItem(id,"messageService");
		new MenuLinkItem(result,"1","ping",nl.clockwork.ebms.admin.web.service.message.PingPage.class);
		new MenuDivider(result,"2");
		new MenuLinkItem(result,"3","unprocessedMessages",nl.clockwork.ebms.admin.web.service.message.MessagesPage.class);
		if (EventListenerType.DAO == eventListenerType)
			new MenuLinkItem(result,"4","messageEvents",nl.clockwork.ebms.admin.web.service.message.MessageEventsPage.class);
		new MenuDivider(result,"5");
		//new MenuLinkItem(message,"6","messageSend",nl.clockwork.ebms.admin.web.service.message.SendMessagePage.class);
		new MenuLinkItem(result,"6","messageSend",nl.clockwork.ebms.admin.web.service.message.SendMessagePageX.class);
		new MenuLinkItem(result,"7","messageResend",nl.clockwork.ebms.admin.web.service.message.ResendMessagePage.class);
		new MenuDivider(result,"8");
		new MenuLinkItem(result,"9","messageStatus",nl.clockwork.ebms.admin.web.service.message.MessageStatusPage.class);
		return result;
	}

	private MenuItem createAdvancedMenuItem(String id)
	{
		val result = new MenuItem(id,"advanced");
		new MenuLinkItem(result,"1","traffic",nl.clockwork.ebms.admin.web.message.TrafficPage.class);
		new MenuLinkItem(result,"2","trafficChart",nl.clockwork.ebms.admin.web.message.TrafficChartPage.class);
		new MenuDivider(result,"3");
		new MenuLinkItem(result,"4","cpas",nl.clockwork.ebms.admin.web.cpa.CPAsPage.class);
		new MenuLinkItem(result,"5","messages",nl.clockwork.ebms.admin.web.message.MessagesPage.class);
		return result;
	}

	private MenuItem createConfigurationMenuItem(String id)
	{
		val result = new MenuItem(id,"configuration");
		new MenuLinkItem(result,"1","ebMSAdminProperties",nl.clockwork.ebms.admin.web.configuration.EbMSAdminPropertiesPage.class);
		return result;
	}

	private MenuItem createExtensionsMenuItem(String id, List<ExtensionProvider> extensionProviders)
	{
		val result = new MenuItem(id,"extensions");
		val i = new AtomicInteger(1);
		extensionProviders.stream()
				.map(p -> p.createSubMenu(result,i.getAndIncrement(),p.getName()))
				.collect(Collectors.toList());
		return result;
	}

	private MenuItem createAboutMEnuItem(String id)
	{
		return new MenuLinkItem(id,"about",nl.clockwork.ebms.admin.web.AboutPage.class);
	}
	
	@Override
	public Class<? extends WebPage> getHomePage()
	{
		return HomePage.class;
	}

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
	
	public static WicketApplication get()
	{
		return (WicketApplication)WebApplication.get();
	}

}
