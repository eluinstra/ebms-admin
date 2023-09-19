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
package nl.clockwork.ebms.admin.web.message;

import java.util.EnumSet;
import java.util.List;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.val;
import nl.clockwork.ebms.EbMSAction;
import nl.clockwork.ebms.EbMSMessageStatus;
import nl.clockwork.ebms.admin.Constants;
import nl.clockwork.ebms.admin.dao.EbMSDAO;
import nl.clockwork.ebms.admin.model.DeliveryLog;
import nl.clockwork.ebms.admin.model.EbMSAttachment;
import nl.clockwork.ebms.admin.model.EbMSMessage;
import nl.clockwork.ebms.admin.web.AjaxLink;
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.Consumer;
import nl.clockwork.ebms.admin.web.InstantLabel;
import nl.clockwork.ebms.admin.web.Link;
import nl.clockwork.ebms.admin.web.PageLink;
import nl.clockwork.ebms.admin.web.StringModel;
import nl.clockwork.ebms.admin.web.TextArea;
import nl.clockwork.ebms.admin.web.Utils;
import nl.clockwork.ebms.admin.web.WebMarkupContainer;
import nl.clockwork.ebms.delivery.task.DeliveryTaskStatus;
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
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessagePage extends BasePage implements IGenericComponent<EbMSMessage, MessagePage>
{
	private class DeliveryLogPropertyListView extends PropertyListView<DeliveryLog>
	{
		private static final long serialVersionUID = 1L;

		public DeliveryLogPropertyListView(String id, IModel<List<DeliveryLog>> list)
		{
			super(id, list);
		}

		@Override
		protected void populateItem(ListItem<DeliveryLog> item)
		{
			val errorMessageModalWindow = new ErrorMessageModalWindow("errorMessageWindow", "sendError", item.getModelObject().getErrorMessage());
			item.add(InstantLabel.of("timestamp", Constants.DATETIME_FORMAT));
			item.add(new Label("uri"));
			item.add(errorMessageModalWindow);
			val link = AjaxLink.<Void>builder().id("showErrorMessageWindow").onClick(t -> errorMessageModalWindow.show(t)).build();
			link.setEnabled(DeliveryTaskStatus.FAILED.equals(item.getModelObject().getStatus()));
			link.add(new Label("status"));
			item.add(link);
		}
	}

	private class LoadableDetachableDeliveryLogModel extends LoadableDetachableModel<List<DeliveryLog>>
	{
		private static final long serialVersionUID = 1L;

		@Override
		protected List<DeliveryLog> load()
		{
			return getModelObject().getDeliveryLogs();
		}
	}

	private class LoadableDetachableEbMSAttachmentModel extends LoadableDetachableModel<List<EbMSAttachment>>
	{
		private static final long serialVersionUID = 1L;

		@Override
		protected List<EbMSAttachment> load()
		{
			return getModelObject().getAttachments();
		}
	}

	private static final long serialVersionUID = 1L;
	@SpringBean(name = "ebMSAdminDAO")
	EbMSDAO ebMSDAO;
	boolean showContent;

	public MessagePage(final IModel<EbMSMessage> model, final WebPage responsePage)
	{
		setModel(new CompoundPropertyModel<>(model));
		add(new Label("messageId"));
		add(new Label("conversationId"));
		add(createRefToMessageIdLink("viewRefToMessageId"));
		add(InstantLabel.of("timestamp", Constants.DATETIME_FORMAT));
		add(new Label("cpaId"));
		add(new Label("fromPartyId"));
		add(new Label("fromRole"));
		add(new Label("toPartyId"));
		add(new Label("toRole"));
		add(new Label("service"));
		add(createActionField("action"));
		add(createViewMessageErrorLink("viewMessageError"));
		add(InstantLabel.of("statusTime", Constants.DATETIME_FORMAT));
		add(new AttachmentsPanel("attachments", new LoadableDetachableEbMSAttachmentModel()).setVisible(getModelObject().getAttachments().size() > 0));
		add(createDeliveryTaskContainer("deliveryTask"));
		add(createDeliveryLogContainer("deliveryLog"));
		add(new PageLink("back", responsePage));
		add(new DownloadEbMSMessageLink("download", ebMSDAO, model));
		val content = createContentField("content");
		add(content);
		add(createToggleContentLink("toggleContent", content));
	}

	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("message", this);
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
			setContent(new ErrorMessagePanel(this, Model.of(errorMessage)));
			setCookieName("sendError");
			setCloseButtonCallback(new nl.clockwork.ebms.admin.web.CloseButtonCallback());
		}

		@Override
		public IModel<String> getTitle()
		{
			return new Model<>(getLocalizer().getString(title, this));
		}
	}

	private Link<Void> createRefToMessageIdLink(String id)
	{
		val result = Link.<Void>builder()
				.id(id)
				.onClick(() -> setResponsePage(new MessagePage(MessageDataModel.of(ebMSDAO, ebMSDAO.findMessage(getModelObject().getRefToMessageId())), this)))
				.build();
		result.add(new Label("refToMessageId"));
		return result;
	}

	private Component[] createActionField(String id)
	{
		val messageErrorModalWindow = new ErrorMessageModalWindow("messageErrorWindow", "messageError", Utils.getErrorList(getModelObject().getContent()));
		val link = AjaxLink.<Void>builder().id("showMessageErrorWindow").onClick(t -> messageErrorModalWindow.show(t)).build();
		link.setEnabled(EbMSAction.EBMS_SERVICE_URI.equals(getModelObject().getService()) && "MessageError".equals(getModelObject().getAction()));
		link.add(new Label(id));
		return new Component[]{link, messageErrorModalWindow};
	}

	private AjaxLink<Void> createViewMessageErrorLink(String id)
	{
		val result = AjaxLink.<Void>builder()
				.id(id)
				.onClick(t -> setResponsePage(new MessagePage(Model.of(ebMSDAO.findResponseMessage(getModelObject().getMessageId())), this)))
				.build();
		result.setEnabled(
				EnumSet
						.of(
								EbMSMessageStatus.RECEIVED,
								EbMSMessageStatus.PROCESSED,
								EbMSMessageStatus.DELIVERED,
								EbMSMessageStatus.FAILED,
								EbMSMessageStatus.DELIVERY_FAILED)
						.contains(getModelObject().getStatus()) ? ebMSDAO.existsResponseMessage(getModelObject().getMessageId()) : false);
		result.add(AttributeModifier.replace("class", Model.of(Utils.getTableCellCssClass(getModelObject().getStatus()))));
		result.add(new Label("status"));
		return result;
	}

	private WebMarkupContainer createDeliveryTaskContainer(String id)
	{
		val result = new WebMarkupContainer(id);
		result.setVisible(getModelObject().getDeliveryTask() != null);
		if (getModelObject().getDeliveryTask() != null)
		{
			result.add(InstantLabel.of("deliveryTask.timestamp", Constants.DATETIME_FORMAT));
			result.add(new Label("deliveryTask.retries"));
			result.add(InstantLabel.of("deliveryTask.timeToLive", Constants.DATETIME_FORMAT));
		}
		return result;
	}

	private WebMarkupContainer createDeliveryLogContainer(String id)
	{
		val result = new WebMarkupContainer(id);
		result.setVisible(getModelObject().getDeliveryLogs().size() > 0);
		result.add(new DeliveryLogPropertyListView("deliveryLogs", new LoadableDetachableDeliveryLogModel()));
		return result;
	}

	private TextArea<String> createContentField(String id)
	{
		val result = TextArea.<String>builder().id(id).model(PropertyModel.of(getModel(), "content")).isVisible(() -> showContent).build();
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
		val result = new AjaxLink<String>(id, onClick);
		result.add(new Label("label", StringModel.of(() -> getLocalizer().getString(showContent ? "cmd.hide" : "cmd.show", this))));
		return result;
	}

}
