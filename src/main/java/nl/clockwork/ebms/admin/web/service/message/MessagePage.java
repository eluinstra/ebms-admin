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

import java.util.List;

import nl.clockwork.ebms.admin.Constants;
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.BootstrapFeedbackPanel;
import nl.clockwork.ebms.admin.web.PageLink;
import nl.clockwork.ebms.model.EbMSDataSource;
import nl.clockwork.ebms.model.EbMSMessageContent;
import nl.clockwork.ebms.service.EbMSMessageService;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.IGenericComponent;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class MessagePage extends BasePage implements IGenericComponent<EbMSMessageContent>
{
	private class EbMSDataSourcePropertyListView extends PropertyListView<EbMSDataSource>
	{
		private static final long serialVersionUID = 1L;
		private int i = 1;

		public EbMSDataSourcePropertyListView(String id, List<? extends EbMSDataSource> list)
		{
			super(id,list);
		}

		@Override
		protected void populateItem(ListItem<EbMSDataSource> item)
		{
			EbMSDataSource dataSource = item.getModelObject();
			if (StringUtils.isEmpty(dataSource.getName()))
				dataSource.setName("dataSource." + i++);
			item.add(new DownloadEbMSDataSourceLink("downloadDataSource",dataSource));
			item.add(new Label("contentType"));
		}
	}

	protected transient Log logger = LogFactory.getLog(this.getClass());
	private static final long serialVersionUID = 1L;
	@SpringBean(name="ebMSMessageService")
	private EbMSMessageService ebMSMessageService;

	public MessagePage(final EbMSMessageContent messageContent, final WebPage responsePage)
	{
		setModel(new CompoundPropertyModel<EbMSMessageContent>(messageContent));
		add(new BootstrapFeedbackPanel("feedback"));
		add(new Label("context.messageId"));
		add(new Label("context.conversationId"));
		add(createViewRefToMessageIdLink("viewRefToMessageId",messageContent));
		add(DateLabel.forDatePattern("context.timestamp",Constants.DATETIME_FORMAT));
		add(new Label("context.cpaId"));
		add(new Label("context.fromRole.partyId"));
		add(new Label("context.fromRole.role"));
		add(new Label("context.toRole.partyId"));
		add(new Label("context.toRole.role"));
		add(new Label("context.service"));
		add(new Label("context.action"));
		add(new EbMSDataSourcePropertyListView("dataSources",messageContent.getDataSources()));
		add(new PageLink("back",responsePage));
		add(new DownloadEbMSMessageContentLink("download",messageContent));
		add(createProcessLink("process",messageContent,responsePage));
	}

	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("message",this);
	}

	private Link<Void> createViewRefToMessageIdLink(String id, final EbMSMessageContent messageContent)
	{
		Link<Void> result = new Link<Void>(id)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick()
			{
				setResponsePage(new MessagePage(ebMSMessageService.getMessage(messageContent.getContext().getRefToMessageId(),null),MessagePage.this));
			}
		};
		result.add(new Label("context.refToMessageId"));
		return result;
	}
	
	private Link<Void> createProcessLink(String id, final EbMSMessageContent messageContent, final WebPage responsePage)
	{
		Link<Void> result = new Link<Void>(id)
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
		result.add(AttributeModifier.replace("onclick","return confirm('" + getLocalizer().getString("confirm",this) + "');"));
		return result;
	}

	@Override
	public EbMSMessageContent getModelObject()
	{
		return (EbMSMessageContent)getDefaultModelObject();
	}

	@Override
	public void setModelObject(EbMSMessageContent object)
	{
		setDefaultModelObject(object);
	}

	@SuppressWarnings("unchecked")
	@Override
	public IModel<EbMSMessageContent> getModel()
	{
		return (IModel<EbMSMessageContent>)getDefaultModel();
	}

	@Override
	public void setModel(IModel<EbMSMessageContent> model)
	{
		setDefaultModel(model);
	}

}
