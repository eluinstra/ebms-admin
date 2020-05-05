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

import java.util.EnumSet;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.IGenericComponent;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.datetime.markup.html.basic.DateLabel;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;
import nl.clockwork.ebms.EbMSMessageStatus;
import nl.clockwork.ebms.admin.Constants;
import nl.clockwork.ebms.admin.dao.EbMSDAO;
import nl.clockwork.ebms.admin.model.EbMSEventLog;
import nl.clockwork.ebms.admin.model.EbMSMessage;
import nl.clockwork.ebms.admin.web.AjaxLink;
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.Consumer;
import nl.clockwork.ebms.admin.web.Link;
import nl.clockwork.ebms.admin.web.PageLink;
import nl.clockwork.ebms.admin.web.StringModel;
import nl.clockwork.ebms.admin.web.TextArea;
import nl.clockwork.ebms.admin.web.Utils;
import nl.clockwork.ebms.admin.web.WebMarkupContainer;
import nl.clockwork.ebms.event.processor.EbMSEventStatus;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessagePage extends BasePage implements IGenericComponent<EbMSMessage,MessagePage>
{
	private class EbMSEventLogPropertyListView extends PropertyListView<EbMSEventLog>
	{
		private static final long serialVersionUID = 1L;

		public EbMSEventLogPropertyListView(String id, List<EbMSEventLog> list)
		{
			super(id,list);
		}

		@Override
		protected void populateItem(ListItem<EbMSEventLog> item)
		{
			val errorMessageModalWindow = new ErrorMessageModalWindow("errorMessageWindow","eventError",item.getModelObject().getErrorMessage());
			item.add(DateLabel.forDatePattern("timestamp",Constants.DATETIME_FORMAT));
			item.add(new Label("uri"));
			item.add(errorMessageModalWindow);
			val link = AjaxLink.<Void>builder()
					.id("showErrorMessageWindow")
					.onClick(t -> errorMessageModalWindow.show(t))
					.build();
			link.setEnabled(EbMSEventStatus.FAILED.equals(item.getModelObject().getStatus()));
			link.add(new Label("status"));
			item.add(link);
		}
	}

	private static final long serialVersionUID = 1L;
	@SpringBean(name="ebMSAdminDAO")
	EbMSDAO ebMSDAO;
	boolean showContent;

	public MessagePage(final EbMSMessage message, final WebPage responsePage)
	{
		setModel(new CompoundPropertyModel<>(message));
		add(new Label("messageId"));
		add(new Label("messageNr"));
		add(new Label("conversationId"));
		add(createRefToMessageIdLink("viewRefToMessageId",message));
		add(DateLabel.forDatePattern("timestamp",Constants.DATETIME_FORMAT));
		add(new Label("cpaId"));
		add(new Label("fromPartyId"));
		add(new Label("fromRole"));
		add(new Label("toPartyId"));
		add(new Label("toRole"));
		add(new Label("service"));
		add(createActionField("action",message));
		add(createViewMessageErrorLink("viewMessageError",message));
		add(new Label("statusTime"));
		add(new AttachmentsPanel("attachments",message.getAttachments()).setVisible(message.getAttachments().size() > 0));
		add(createNextEventContainer("nextEvent",message));
		add(createEventLogContainer("eventLog",message));
		add(new PageLink("back",responsePage));
		add(new DownloadEbMSMessageLink("download",ebMSDAO,message));
		val content = createContentField("content",message);
		add(content);
		add(createToggleContentLink("toggleContent",content));
	}

	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("message",this);
	}
	
	public boolean getShowContent()
	{
		return showContent;
	}

	@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
	public class ErrorMessageModalWindow extends ModalWindow
	{
		private static final long serialVersionUID = 1L;
		@NonNull
		String title;

		public ErrorMessageModalWindow(String id, @NonNull String title, String errorMessage)
		{
			super(id);
			this.title = title;
			setCssClassName(ModalWindow.CSS_CLASS_GRAY);
			setContent(new ErrorMessagePanel(this,Model.of(errorMessage)));
			setCookieName("eventError");
			setCloseButtonCallback(new nl.clockwork.ebms.admin.web.CloseButtonCallback());
		}

		@Override
		public IModel<String> getTitle()
		{
			return new Model<>(getLocalizer().getString(title,this));
		}
	}

	private Link<Void> createRefToMessageIdLink(String id, final EbMSMessage message)
	{
		val result = Link.<Void>builder()
				.id(id)
				.onClick(() -> setResponsePage(new MessagePage(ebMSDAO.findMessage(message.getRefToMessageId()),MessagePage.this)))
				.build();
		result.add(new Label("refToMessageId"));
		return result;
	}
	
	private Component[] createActionField(String id, EbMSMessage message)
	{
		val messageErrorModalWindow = new ErrorMessageModalWindow("messageErrorWindow","messageError",Utils.getErrorList(getModelObject().getContent()));
		val link = AjaxLink.<Void>builder()
				.id("showMessageErrorWindow")
				.onClick(t -> messageErrorModalWindow.show(t))
				.build();
		link.setEnabled(nl.clockwork.ebms.Constants.EBMS_SERVICE_URI.equals(getModelObject().getService()) && "MessageError".equals(getModelObject().getAction()));
		link.add(new Label(id));
		return new Component[] {link,messageErrorModalWindow};
	}

	private AjaxLink<Void> createViewMessageErrorLink(String id, final EbMSMessage message)
	{
		val result = AjaxLink.<Void>builder()
				.id(id)
				.onClick(t -> setResponsePage(new MessagePage(ebMSDAO.findResponseMessage(message.getMessageId()),MessagePage.this)))
				.build();
		result.setEnabled(EnumSet.of(EbMSMessageStatus.RECEIVED,EbMSMessageStatus.PROCESSED,EbMSMessageStatus.DELIVERED,EbMSMessageStatus.FAILED,EbMSMessageStatus.DELIVERY_FAILED).contains(message.getStatus()) ? ebMSDAO.existsResponseMessage(message.getMessageId()) : false);
		result.add(AttributeModifier.replace("class",Model.of(Utils.getTableCellCssClass(message.getStatus()))));
		result.add(new Label("status"));
		return result;
	}

	private WebMarkupContainer createNextEventContainer(String id, final EbMSMessage message)
	{
		WebMarkupContainer result = new WebMarkupContainer(id);
		result.setVisible(message.getEvent() != null);
		if (message.getEvent() != null)
		{
			result.add(DateLabel.forDatePattern("event.timestamp",Constants.DATETIME_FORMAT));
			result.add(new Label("event.retries"));
			result.add(DateLabel.forDatePattern("event.timeToLive",Constants.DATETIME_FORMAT));
		}
		return result;
	}

	private WebMarkupContainer createEventLogContainer(String id, final EbMSMessage message)
	{
		val result = new WebMarkupContainer(id);
		result.setVisible(message.getEvents().size() > 0);
		result.add(new EbMSEventLogPropertyListView("events",message.getEvents()));
		return result;
	}

	private TextArea<String> createContentField(String id, final EbMSMessage message)
	{
		val result = TextArea.<String>builder()
				.id(id)
				.model(Model.of(message.getContent()))
				.isVisible(() -> showContent)
				.build();
		result.setOutputMarkupPlaceholderTag(true);
		result.setEnabled(false);
		return result;
	}

	private AjaxLink<String> createToggleContentLink(String id, final Component content)
	{
		Consumer<AjaxRequestTarget> onClick = t ->
		{
			showContent = !showContent;
			t.add(this);
			t.add(content);
		};
		val result = new AjaxLink<String>(id,onClick);
		result.add(new Label("label",new StringModel(() -> MessagePage.this.getLocalizer().getString(showContent ? "cmd.hide" : "cmd.show",MessagePage.this))));
		return result;
	}

}
