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

import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.BootstrapPagingNavigator;
import nl.clockwork.ebms.admin.web.MaxItemsPerPageChoice;
import nl.clockwork.ebms.admin.web.OddOrEvenIndexStringModel;
import nl.clockwork.ebms.admin.web.PageLink;
import nl.clockwork.ebms.admin.web.WebMarkupContainer;
import nl.clockwork.ebms.model.EbMSMessageContext;
import nl.clockwork.ebms.service.EbMSMessageService;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class MessagesPage extends BasePage
{
	private class MessageDataView extends DataView<String>
	{
		protected MessageDataView(String id, IDataProvider<String> dataProvider)
		{
			super(id,dataProvider);
			setOutputMarkupId(true);
		}

		private static final long serialVersionUID = 1L;

		@Override
		public long getItemsPerPage()
		{
			return maxItemsPerPage;
		}

		@Override
		protected void populateItem(final Item<String> item)
		{
			final String messageId = item.getModelObject();
			item.add(createViewLink("view",messageId,new Label("messageId",messageId)));
			item.add(AttributeModifier.replace("class",new OddOrEvenIndexStringModel(item.getIndex())));
		}

		private Link<Void> createViewLink(String id, final String messageId, Component...components)
		{
			Link<Void> link = new Link<Void>(id)
			{
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick()
				{
					setResponsePage(new MessagePage(ebMSMessageService.getMessage(messageId,null),MessagesPage.this,
					new MessageProcessor()
					{
						private static final long serialVersionUID = 1L;

						@Override
						public void processMessage(String messageId)
						{
							ebMSMessageService.processMessage(messageId);
						}
					}));
				}
			};
			link.add(components);
			return link;
		}
	}

	private static final long serialVersionUID = 1L;
	@SpringBean(name="ebMSMessageService")
	private EbMSMessageService ebMSMessageService;
	@SpringBean(name="maxItemsPerPage")
	private Integer maxItemsPerPage;
	private EbMSMessageContext filter;

	public MessagesPage()
	{
		this(new EbMSMessageContext());
	}

	public MessagesPage(EbMSMessageContext filter)
	{
		this(filter,null);
	}

	public MessagesPage(EbMSMessageContext filter, final WebPage responsePage)
	{
		this.filter = filter;
		final WebMarkupContainer container = new WebMarkupContainer("container");
		add(container);
		DataView<String> messages = new MessageDataView("messages",new MessageDataProvider(ebMSMessageService,this.filter));
		container.add(messages);
		final BootstrapPagingNavigator navigator = new BootstrapPagingNavigator("navigator",messages);
		add(navigator);
		add(new MaxItemsPerPageChoice("maxItemsPerPage",new PropertyModel<Integer>(this,"maxItemsPerPage"),navigator,container));
		add(new PageLink("back",responsePage).setVisible(responsePage != null));
		add(new DownloadEbMSMessageIdsCSVLink("download",ebMSMessageService,filter));
	}

	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("receivedMessages",this);
	}
}
