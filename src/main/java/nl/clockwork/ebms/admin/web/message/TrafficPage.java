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

import org.apache.commons.lang3.SerializationUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.transaction.annotation.Transactional;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;
import nl.clockwork.ebms.admin.Constants;
import nl.clockwork.ebms.admin.dao.EbMSDAO;
import nl.clockwork.ebms.admin.model.EbMSMessage;
import nl.clockwork.ebms.admin.web.Action;
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.BootstrapPagingNavigator;
import nl.clockwork.ebms.admin.web.InstantLabel;
import nl.clockwork.ebms.admin.web.Link;
import nl.clockwork.ebms.admin.web.MaxItemsPerPageChoice;
import nl.clockwork.ebms.admin.web.PageLink;
import nl.clockwork.ebms.admin.web.Utils;
import nl.clockwork.ebms.admin.web.WebMarkupContainer;
import nl.clockwork.ebms.admin.web.WicketApplication;
import nl.clockwork.ebms.admin.web.message.MessageFilterPanel.MessageFilterFormData;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Transactional(transactionManager = "dataSourceTransactionManager")
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
			val o = item.getModelObject();
			item.add(createViewLink("view",item.getModel()));
			item.add(createFilterConversationIdLink("filterConversationId",item.getModel()));
			item.add(InstantLabel.of("timestamp",new Model<>(o.getTimestamp()),Constants.DATETIME_FORMAT));
			item.add(new Label("cpaId",o.getCpaId()));
			item.add(new Label("fromPartyId",o.getFromPartyId()));
			item.add(new Label("fromRole",o.getFromRole()));
			item.add(new Label("toPartyId",o.getToPartyId()));
			item.add(new Label("toRole",o.getToRole()));
			item.add(new Label("service",o.getService()));
			item.add(new Label("action",o.getAction()));
			item.add(new Label("status",o.getStatus()).add(AttributeModifier.replace("class",Model.of(Utils.getTableCellCssClass(o.getStatus())))));
			item.add(InstantLabel.of("statusTime",new Model<>(o.getStatusTime()),Constants.DATETIME_FORMAT));
			item.add(AttributeModifier.replace("class",Model.of(Utils.getTableRowCssClass(o.getStatus()))));
		}

		private Link<Void> createViewLink(String id, final IModel<EbMSMessage> model)
		{
			val result = Link.<Void>builder()
					.id(id)
					.onClick(() -> setResponsePage(new MessagePageX(model,TrafficPage.this)))
					.build();
			result.add(new Label("messageId",model.getObject().getMessageId()));
			return result;
		}

		private Link<Void> createFilterConversationIdLink(String id, final IModel<EbMSMessage> model)
		{
			Action onClick = () ->
			{
				val filter = (MessageFilterFormData)SerializationUtils.clone(TrafficPage.this.filter.getObject());
				filter.setConversationId(model.getObject().getConversationId());
				setResponsePage(new TrafficPage(Model.of(filter),TrafficPage.this));
			};
			val result = new Link<Void>(id,onClick);
			result.add(new Label("conversationId",model.getObject().getConversationId()));
			result.setEnabled(TrafficPage.this.filter.getObject().getConversationId() == null);
			return result;
		}
	}

	private static final long serialVersionUID = 1L;
	@SpringBean(name="ebMSAdminDAO")
	EbMSDAO ebMSDAO;
	@NonNull
	final Integer maxItemsPerPage;
	@NonNull
	final IModel<MessageFilterFormData> filter;

	public TrafficPage()
	{
		this(Model.of(MessageFilterPanel.createMessageFilter()));
	}

	public TrafficPage(IModel<MessageFilterFormData> filter)
	{
		this(filter,null);
	}

	public TrafficPage(IModel<MessageFilterFormData> filter, final WebPage responsePage)
	{
		this.maxItemsPerPage = WicketApplication.get().getMaxItemsPerPage();
		this.filter = filter;
		filter.getObject().setMessageNr(0);
		filter.getObject().setServiceMessage(false);
		add(createMessageFilterPanel("messageFilter",filter));
		val container = new WebMarkupContainer("container");
		add(container);
		val messages = new EbMSMessageDataView("messages",MessageDataProvider.of(ebMSDAO,filter.getObject()));
		container.add(messages);
		val navigator = new BootstrapPagingNavigator("navigator",messages);
		add(navigator);
		add(new MaxItemsPerPageChoice("maxItemsPerPage",new PropertyModel<>(this,"maxItemsPerPage"),navigator,container));
		add(new PageLink("back",responsePage).setVisible(responsePage != null));
		add(new DownloadEbMSMessagesCSVLink("download",ebMSDAO,filter));
	}

	private MessageFilterPanel createMessageFilterPanel(String id, IModel<MessageFilterFormData> filter)
	{
		return new MessageFilterPanel(id,filter,f -> new TrafficPage(f));
	}

	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("messages",this);
	}
}
