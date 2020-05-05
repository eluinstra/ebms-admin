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
package nl.clockwork.ebms.admin.web.service.message;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import lombok.AccessLevel;
import lombok.val;
import lombok.experimental.FieldDefaults;
import nl.clockwork.ebms.admin.web.Action;
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.BootstrapPagingNavigator;
import nl.clockwork.ebms.admin.web.Link;
import nl.clockwork.ebms.admin.web.MaxItemsPerPageChoice;
import nl.clockwork.ebms.admin.web.OddOrEvenIndexStringModel;
import nl.clockwork.ebms.admin.web.PageLink;
import nl.clockwork.ebms.admin.web.WebMarkupContainer;
import nl.clockwork.ebms.event.listener.EbMSMessageEventType;
import nl.clockwork.ebms.service.EbMSMessageService;
import nl.clockwork.ebms.service.model.EbMSMessageContext;
import nl.clockwork.ebms.service.model.EbMSMessageEvent;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageEventsPage extends BasePage
{
	private class MessageEventDataView extends DataView<EbMSMessageEvent>
	{
		private static final long serialVersionUID = 1L;

		protected MessageEventDataView(String id, IDataProvider<EbMSMessageEvent> dataProvider)
		{
			super(id,dataProvider);
			setOutputMarkupId(true);
		}

		@Override
		public long getItemsPerPage()
		{
			return maxItemsPerPage;
		}

		@Override
		protected void populateItem(final Item<EbMSMessageEvent> item)
		{
			val messageEvent = item.getModelObject();
			item.add(createViewLink("view",messageEvent,new Label("messageId",messageEvent.getMessageId())));
			item.add(new Label("type",messageEvent.getType()));
			item.add(AttributeModifier.replace("class",new OddOrEvenIndexStringModel(item.getIndex())));
		}

		private Link<Void> createViewLink(String id, final EbMSMessageEvent messageEvent, Component...components)
		{
			Action onClick = () ->
			{
				setResponsePage(
						new MessagePage(
								ebMSMessageService.getMessage(messageEvent.getMessageId(),null),
								MessageEventsPage.this,
								messageId -> ebMSMessageService.processMessageEvent(messageId)));
			};
			val link = new Link<Void>(id,onClick);
			link.add(components);
			return link;
		}
	}

	private static final long serialVersionUID = 1L;
	@SpringBean(name="ebMSMessageService")
	EbMSMessageService ebMSMessageService;
	@SpringBean(name="maxItemsPerPage")
	Integer maxItemsPerPage;
	EbMSMessageContext filter;
	EbMSMessageEventType[] eventTypes;

	public MessageEventsPage()
	{
		this(new EbMSMessageContext(),EbMSMessageEventType.values());
	}

	public MessageEventsPage(EbMSMessageContext filter, EbMSMessageEventType[] eventTypes)
	{
		this(filter,eventTypes,null);
	}

	public MessageEventsPage(EbMSMessageContext filter, EbMSMessageEventType[] eventTypes, final WebPage responsePage)
	{
		this.filter = filter;
		this.eventTypes = eventTypes;
		val container = new WebMarkupContainer("container");
		add(container);
		val messageEvents = new MessageEventDataView("messageEvents",new MessageEventDataProvider(ebMSMessageService,this.filter,this.eventTypes));
		container.add(messageEvents);
		val navigator = new BootstrapPagingNavigator("navigator",messageEvents);
		add(navigator);
		add(new MaxItemsPerPageChoice("maxItemsPerPage",new PropertyModel<>(this,"maxItemsPerPage"),navigator,container));
		add(new PageLink("back",responsePage).setVisible(responsePage != null));
		add(new DownloadEbMSMessageEventsCSVLink("download",ebMSMessageService,filter,EbMSMessageEventType.values()));
	}

	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("messageEvents",this);
	}
}
