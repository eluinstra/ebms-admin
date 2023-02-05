/*
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
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import nl.clockwork.ebms.admin.Constants;
import nl.clockwork.ebms.admin.web.Action;
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.BootstrapFeedbackPanel;
import nl.clockwork.ebms.admin.web.InstantLabel;
import nl.clockwork.ebms.admin.web.Link;
import nl.clockwork.ebms.admin.web.PageLink;
import nl.clockwork.ebms.service.EbMSMessageService;
import nl.clockwork.ebms.service.model.DataSource;
import nl.clockwork.ebms.service.model.Message;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.IGenericComponent;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessagePage extends BasePage implements IGenericComponent<Message, MessagePage>
{
	@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
	private class EbMSDataSourcePropertyListView extends PropertyListView<DataSource>
	{
		private static final long serialVersionUID = 1L;
		AtomicInteger i = new AtomicInteger(1);

		public EbMSDataSourcePropertyListView(String id, IModel<List<DataSource>> list)
		{
			super(id, list);
		}

		@Override
		protected void populateItem(ListItem<DataSource> item)
		{
			val o = item.getModelObject();
			if (StringUtils.isEmpty(o.getName()))
				o.setName("dataSource." + i.getAndIncrement());
			item.add(new DownloadEbMSDataSourceLink("downloadDataSource", item.getModel()));
			item.add(new Label("contentType"));
		}
	}

	private class LoadableDetachableEbMSDataSourceModel extends LoadableDetachableModel<List<DataSource>>
	{
		private static final long serialVersionUID = 1L;

		@Override
		protected List<DataSource> load()
		{
			return getModelObject().getDataSources();
		}
	}

	private static final long serialVersionUID = 1L;
	@SpringBean(name = "ebMSMessageService")
	EbMSMessageService ebMSMessageService;

	public MessagePage(IModel<Message> model, WebPage responsePage, MessageProcessor messageProcessor)
	{
		setModel(new CompoundPropertyModel<>(model));
		add(new BootstrapFeedbackPanel("feedback"));
		add(new Label("properties.messageId"));
		add(new Label("properties.conversationId"));
		add(createViewRefToMessageIdLink("viewRefToMessageId", messageProcessor));
		add(InstantLabel.of("properties.timestamp", Constants.DATETIME_FORMAT));
		add(new Label("properties.cpaId"));
		add(new Label("properties.fromParty.partyId"));
		add(new Label("properties.fromParty.role"));
		add(new Label("properties.toParty.partyId"));
		add(new Label("properties.toParty.role"));
		add(new Label("properties.service"));
		add(new Label("properties.action"));
		add(new EbMSDataSourcePropertyListView("dataSources", new LoadableDetachableEbMSDataSourceModel()));
		add(new PageLink("back", responsePage));
		add(new DownloadEbMSMessageLink("download", model));
		add(createProcessLink("process", messageProcessor, responsePage));
	}

	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("message", this);
	}

	private Link<Void> createViewRefToMessageIdLink(String id, final MessageProcessor messageProcessor)
	{
		Action onClick = () -> setResponsePage(
				new MessagePage(Model.of(ebMSMessageService.getMessage(getModelObject().getProperties().getRefToMessageId(), null)), this, messageProcessor));
		val result = new Link<Void>(id, onClick);
		result.add(new Label("properties.refToMessageId"));
		return result;
	}

	private Link<Void> createProcessLink(String id, final MessageProcessor messageProcessor, final WebPage responsePage)
	{
		Action onClick = () ->
		{
			try
			{
				messageProcessor.processMessage(getModelObject().getProperties().getMessageId());
				setResponsePage(responsePage);
			}
			catch (Exception e)
			{
				log.error("", e);
				error(e.getMessage());
			}
		};
		val result = new Link<Void>(id, onClick);
		result.add(AttributeModifier.replace("onclick", "return confirm('" + getLocalizer().getString("confirm", this) + "');"));
		return result;
	}

}
