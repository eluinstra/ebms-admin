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

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class MessagesPage extends BasePage
{
	private static final long serialVersionUID = 1L;
	@SpringBean(name="ebMSDAO")
	private EbMSDAO ebMSDAO;
	@SpringBean(name="maxItemsPerPage")
	private Integer maxItemsPerPage;
	private EbMSMessageFilter filter;

	public MessagesPage()
	{
		this(new EbMSMessageFilter());
	}

	public MessagesPage(EbMSMessageFilter filter)
	{
		this.filter = filter;
		WebMarkupContainer container = new WebMarkupContainer("container");
		container.setOutputMarkupId(true);

		DataView<EbMSMessage> messages = new DataView<EbMSMessage>("messages",new MessageDataProvider(ebMSDAO,this.filter))
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final Item<EbMSMessage> item)
			{
				final EbMSMessage message = item.getModelObject();
				Link<Void> link = new Link<Void>("view")
				{
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick()
					{
						//setResponsePage(new MessagePage(ebMSDAO.getMessage(message.getMessageId(),message.getMessageNr()),MessagesPage.this));
						setResponsePage(new MessagePage(message,MessagesPage.this));
					}
				};
				link.add(new Label("messageId",message.getMessageId()));
				item.add(link);
				item.add(new Label("messageNr",message.getMessageNr()));
				link = new Link<Void>("filterConversationId")
				{
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick()
					{
						EbMSMessageFilter filter = new EbMSMessageFilter();
						filter.setConversationId(message.getConversationId());
						setResponsePage(new MessagesPage(filter)); //,MessagesPage.this
					}
				};
				link.add(new Label("conversationId",message.getConversationId()));
				link.setEnabled(MessagesPage.this.filter.getConversationId() == null);
				item.add(link);
				link = new Link<Void>("viewRefToMessageId")
				{
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick()
					{
						setResponsePage(new MessagePage(ebMSDAO.getMessage(message.getRefToMessageId()),MessagesPage.this));
					}
				};
				link.add(new Label("refToMessageId",message.getRefToMessageId()));
				item.add(link);
				item.add(DateLabel.forDatePattern("timestamp",new Model<Date>(message.getTimestamp()),Constants.DATETIME_FORMAT));
				item.add(new Label("cpaId",message.getCpaId()));
				item.add(new Label("fromRole",message.getFromRole()));
				item.add(new Label("toRole",message.getToRole()));
				item.add(new Label("service",message.getService()));
				item.add(new Label("action",message.getAction()));
				item.add(new Label("status",message.getStatus()));
				item.add(DateLabel.forDatePattern("statusTime",new Model<Date>(message.getStatusTime()),Constants.DATETIME_FORMAT));
				item.add(AttributeModifier.replace("class",new AbstractReadOnlyModel<String>()
				{
					private static final long serialVersionUID = 1L;
				
					@Override
					public String getObject()
					{
						return (item.getIndex() % 2 == 0) ? "even" : "odd";
					}
				}));
			}
		};
		messages.setOutputMarkupId(true);
		messages.setItemsPerPage(maxItemsPerPage);

		container.add(messages);
		add(container);
		add(new AjaxPagingNavigator("navigator",messages));
	}

	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("messages",this);
	}
}
