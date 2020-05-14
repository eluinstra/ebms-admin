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
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.IGenericComponent;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import lombok.AccessLevel;
import lombok.val;
import lombok.experimental.FieldDefaults;
import lombok.extern.apachecommons.CommonsLog;
import nl.clockwork.ebms.admin.Constants;
import nl.clockwork.ebms.admin.web.Action;
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.BootstrapFeedbackPanel;
import nl.clockwork.ebms.admin.web.InstantLabel;
import nl.clockwork.ebms.admin.web.Link;
import nl.clockwork.ebms.admin.web.PageLink;
import nl.clockwork.ebms.service.EbMSMessageService;
import nl.clockwork.ebms.service.model.EbMSDataSource;
import nl.clockwork.ebms.service.model.EbMSMessageContent;

@CommonsLog
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessagePage extends BasePage implements IGenericComponent<EbMSMessageContent,MessagePage>
{
	@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
	private class EbMSDataSourcePropertyListView extends PropertyListView<EbMSDataSource>
	{
		private static final long serialVersionUID = 1L;
		AtomicInteger i = new AtomicInteger(1);

		public EbMSDataSourcePropertyListView(String id, List<EbMSDataSource> list)
		{
			super(id,list);
		}

		@Override
		protected void populateItem(ListItem<EbMSDataSource> item)
		{
			val o = item.getModelObject();
			if (StringUtils.isEmpty(o.getName()))
				o.setName("dataSource." + i.getAndIncrement());
			item.add(new DownloadEbMSDataSourceLink("downloadDataSource",item.getModel()));
			item.add(new Label("contentType"));
		}
	}

	private static final long serialVersionUID = 1L;
	@SpringBean(name="ebMSMessageService")
	EbMSMessageService ebMSMessageService;

	public MessagePage(IModel<EbMSMessageContent> model, WebPage responsePage, MessageProcessor messageProcessor)
	{
		setModel(new CompoundPropertyModel<>(model));
		add(new BootstrapFeedbackPanel("feedback"));
		add(new Label("context.messageId"));
		add(new Label("context.conversationId"));
		add(createViewRefToMessageIdLink("viewRefToMessageId",messageProcessor));
		add(InstantLabel.of("context.timestamp",Constants.DATETIME_FORMAT));
		add(new Label("context.cpaId"));
		add(new Label("context.fromParty.partyId"));
		add(new Label("context.fromParty.role"));
		add(new Label("context.toParty.partyId"));
		add(new Label("context.toParty.role"));
		add(new Label("context.service"));
		add(new Label("context.action"));
		add(new EbMSDataSourcePropertyListView("dataSources",model.getObject().getDataSources()));
		add(new PageLink("back",responsePage));
		add(new DownloadEbMSMessageContentLink("download",model));
		add(createProcessLink("process",messageProcessor,responsePage));
	}

	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("message",this);
	}

	private Link<Void> createViewRefToMessageIdLink(String id, final MessageProcessor messageProcessor)
	{
		Action onClick = () -> setResponsePage(
				new MessagePage(
						Model.of(ebMSMessageService.getMessage(getModelObject().getContext().getRefToMessageId(),null)),
						MessagePage.this,
						messageProcessor));
		val result = new Link<Void>(id,onClick);
		result.add(new Label("context.refToMessageId"));
		return result;
	}
	
	private Link<Void> createProcessLink(String id, final MessageProcessor messageProcessor, final WebPage responsePage)
	{
		Action onClick = () ->
		{
			try
			{
				messageProcessor.processMessage(getModelObject().getContext().getMessageId());
				setResponsePage(responsePage);
			}
			catch (Exception e)
			{
				log.error("",e);
				error(e.getMessage());
			}
		};
		val result = new Link<Void>(id,onClick);
		result.add(AttributeModifier.replace("onclick","return confirm('" + getLocalizer().getString("confirm",this) + "');"));
		return result;
	}

}
