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

import java.util.Date;

import nl.clockwork.ebms.admin.Constants;
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.CSSFeedbackPanel;
import nl.clockwork.ebms.model.EbMSDataSource;
import nl.clockwork.ebms.model.EbMSMessageContent;
import nl.clockwork.ebms.service.EbMSMessageService;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class MessagePage extends BasePage
{
	protected transient Log logger = LogFactory.getLog(this.getClass());
	private static final long serialVersionUID = 1L;
	@SpringBean(name="ebMSMessageService")
	private EbMSMessageService ebMSMessageService;

	public MessagePage(final EbMSMessageContent messageContent, final WebPage responsePage)
	{
		add(new CSSFeedbackPanel("feedback"));
		add(new Label("messageId",messageContent.getContext().getMessageId()));
		add(new Label("conversationId",messageContent.getContext().getConversationId()));
		Link<Void> link = new Link<Void>("viewRefToMessageId")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick()
			{
				setResponsePage(new MessagePage(ebMSMessageService.getMessage(messageContent.getContext().getRefToMessageId(),null),MessagePage.this));
			}
		};
		link.add(new Label("refToMessageId",messageContent.getContext().getRefToMessageId()));
		add(link);
		add(DateLabel.forDatePattern("timestamp",new Model<Date>(messageContent.getContext().getTimestamp()),Constants.DATETIME_FORMAT));
		add(new Label("cpaId",messageContent.getContext().getCpaId()));
		add(new Label("fromRole",messageContent.getContext().getFromRole()));
		add(new Label("toRole",messageContent.getContext().getToRole()));
		add(new Label("service",messageContent.getContext().getService()));
		add(new Label("action",messageContent.getContext().getAction()));
		
		PropertyListView<EbMSDataSource> dataSources = 
			new PropertyListView<EbMSDataSource>("dataSources",messageContent.getDataSources())
			{
				private static final long serialVersionUID = 1L;
				private int i = 1;

				@Override
				protected void populateItem(ListItem<EbMSDataSource> item)
				{
					EbMSDataSource dataSource = item.getModelObject();
					if (StringUtils.isEmpty(dataSource.getName()))
						dataSource.setName("dataSource." + i++);
					DownloadEbMSDataSourceLink link = new DownloadEbMSDataSourceLink("downloadDataSource",dataSource);
					link.add(new Label("name"));
					item.add(link);
					item.add(new Label("contentType"));
				}
			}
		;
		add(dataSources);
		
		add(new Link<Object>("back")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick()
			{
				setResponsePage(responsePage);
			}
		});
		add(new DownloadEbMSMessageContentLink("download",messageContent));
		link = new Link<Void>("process")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick()
			{
				try
				{
					ebMSMessageService.processMessage(messageContent.getContext().getMessageId());
					setResponsePage(responsePage);
				}
				catch (Exception e)
				{
					logger.error("",e);
					error(e.getMessage());
				}
			}
		};
		link.add(AttributeModifier.replace("onclick","return confirm('" + getLocalizer().getString("confirm",this) + "');"));
		add(link);
	}
	
	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("message",this);
	}

}
