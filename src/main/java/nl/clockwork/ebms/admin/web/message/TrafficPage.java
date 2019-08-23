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
package nl.clockwork.ebms.admin.web.message;

import java.util.Date;

import nl.clockwork.ebms.admin.Constants;
import nl.clockwork.ebms.admin.dao.EbMSDAO;
import nl.clockwork.ebms.admin.model.EbMSMessage;
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.BootstrapPagingNavigator;
import nl.clockwork.ebms.admin.web.MaxItemsPerPageChoice;
import nl.clockwork.ebms.admin.web.PageLink;
import nl.clockwork.ebms.admin.web.Utils;
import nl.clockwork.ebms.admin.web.WebMarkupContainer;
import nl.clockwork.ebms.admin.web.message.MessageFilterPanel.MessageFilterFormModel;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.datetime.markup.html.basic.DateLabel;

public class TrafficPage extends BasePage
{
	private class EbMSMessageDataView extends DataView<EbMSMessage>
	{
		private static final long serialVersionUID = 1L;

		protected EbMSMessageDataView(String id, IDataProvider<EbMSMessage> dataProvider)
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
		protected void populateItem(final Item<EbMSMessage> item)
		{
			EbMSMessage message = item.getModelObject();
			item.add(createViewLink("view",message));
			item.add(createFilterConversationIdLink("filterConversationId",message));
			item.add(DateLabel.forDatePattern("timestamp",new Model<>(message.getTimestamp()),Constants.DATETIME_FORMAT));
			item.add(new Label("cpaId",message.getCpaId()));
			item.add(new Label("fromPartyId",message.getFromPartyId()));
			item.add(new Label("fromRole",message.getFromRole()));
			item.add(new Label("toPartyId",message.getToPartyId()));
			item.add(new Label("toRole",message.getToRole()));
			item.add(new Label("service",message.getService()));
			item.add(new Label("action",message.getAction()));
			item.add(new Label("status",message.getStatus()).add(AttributeModifier.replace("class",Model.of(Utils.getTableCellCssClass(message.getStatus())))));
			item.add(DateLabel.forDatePattern("statusTime",new Model<>(message.getStatusTime()),Constants.DATETIME_FORMAT));
			item.add(AttributeModifier.replace("class",Model.of(Utils.getTableRowCssClass(message.getStatus()))));
		}

		private Link<Void> createViewLink(String id, final EbMSMessage message)
		{
			Link<Void> result = new Link<Void>(id)
			{
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick()
				{
					//setResponsePage(new MessagePageX(ebMSDAO.getMessage(message.getMessageId(),message.getMessageNr()),MessagesPage.this));
					setResponsePage(new MessagePageX(message,TrafficPage.this));
				}
			};
			result.add(new Label("messageId",message.getMessageId()));
			return result;
		}

		private Link<Void> createFilterConversationIdLink(String id, final EbMSMessage message)
		{
			Link<Void> result = new Link<Void>(id)
			{
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick()
				{
					MessageFilterFormModel filter = (MessageFilterFormModel)SerializationUtils.clone(TrafficPage.this.filter);
					filter.setConversationId(message.getConversationId());
					setResponsePage(new TrafficPage(filter,TrafficPage.this));
				}
			};
			result.add(new Label("conversationId",message.getConversationId()));
			result.setEnabled(TrafficPage.this.filter.getConversationId() == null);
			return result;
		}
	}

	private static final long serialVersionUID = 1L;
	@SpringBean(name="ebMSAdminDAO")
	private EbMSDAO ebMSDAO;
	@SpringBean(name="maxItemsPerPage")
	private Integer maxItemsPerPage;
	private EbMSMessageFilter filter;

	public TrafficPage()
	{
		this(MessageFilterPanel.createMessageFilter());
	}

	public TrafficPage(EbMSMessageFilter filter)
	{
		this(filter,null);
	}

	public TrafficPage(EbMSMessageFilter filter, final WebPage responsePage)
	{
		this.filter = filter;
		filter.setMessageNr(0);
		filter.setServiceMessage(false);
		add(createMessageFilterPanel("messageFilter",filter));
		WebMarkupContainer container = new WebMarkupContainer("container");
		add(container);
		DataView<EbMSMessage> messages = new EbMSMessageDataView("messages",new MessageDataProvider(ebMSDAO,this.filter));
		container.add(messages);
		BootstrapPagingNavigator navigator = new BootstrapPagingNavigator("navigator",messages);
		add(navigator);
		add(new MaxItemsPerPageChoice("maxItemsPerPage",new PropertyModel<>(this,"maxItemsPerPage"),navigator,container));
		add(new PageLink("back",responsePage).setVisible(responsePage != null));
		add(new DownloadEbMSMessagesCSVLink("download",ebMSDAO,filter));
	}

	private MessageFilterPanel createMessageFilterPanel(String id, EbMSMessageFilter filter)
	{
		return new MessageFilterPanel(id,(MessageFilterFormModel)filter)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public BasePage getPage(MessageFilterFormModel filter)
			{
				return new TrafficPage(filter);
			}
		};
	}

	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("messages",this);
	}
}
