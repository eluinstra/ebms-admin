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
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import lombok.AccessLevel;
import lombok.val;
import lombok.experimental.FieldDefaults;
import nl.clockwork.ebms.EbMSAction;
import nl.clockwork.ebms.EbMSMessageStatus;
import nl.clockwork.ebms.admin.Constants;
import nl.clockwork.ebms.admin.dao.EbMSDAO;
import nl.clockwork.ebms.admin.model.EbMSAttachment;
import nl.clockwork.ebms.admin.model.EbMSEventLog;
import nl.clockwork.ebms.admin.model.EbMSMessage;
import nl.clockwork.ebms.admin.web.AjaxFormComponentUpdatingBehavior;
import nl.clockwork.ebms.admin.web.AjaxLink;
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.BootstrapFeedbackPanel;
import nl.clockwork.ebms.admin.web.Consumer;
import nl.clockwork.ebms.admin.web.InstantLabel;
import nl.clockwork.ebms.admin.web.Link;
import nl.clockwork.ebms.admin.web.MessageProvider;
import nl.clockwork.ebms.admin.web.PageLink;
import nl.clockwork.ebms.admin.web.StringModel;
import nl.clockwork.ebms.admin.web.TextArea;
import nl.clockwork.ebms.admin.web.Utils;
import nl.clockwork.ebms.admin.web.WebMarkupContainer;
import nl.clockwork.ebms.admin.web.WicketApplication;
import nl.clockwork.ebms.event.processor.EbMSEventStatus;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessagePageX extends BasePage implements IGenericComponent<EbMSMessage,MessagePageX>
{
	private class EbMSEventLogPropertyListView extends PropertyListView<EbMSEventLog>
	{
		private static final long serialVersionUID = 1L;

		public EbMSEventLogPropertyListView(String id, IModel<List<EbMSEventLog>> list)
		{
			super(id,list);
		}

		@Override
		protected void populateItem(ListItem<EbMSEventLog> item)
		{
			val o = item.getModelObject();
			val errorMessageModalWindow = new ErrorMessageModalWindow("errorMessageWindow","eventError",o.getErrorMessage());
			item.add(InstantLabel.of("timestamp",Constants.DATETIME_FORMAT));
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
	private class LoadableDetachableEbMSEventLogModel extends LoadableDetachableModel <List<EbMSEventLog>>
	{
		private static final long serialVersionUID = 1L;

		@Override
		protected List<EbMSEventLog> load()
		{
			return getModelObject().getEvents();
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
	@SpringBean(name="ebMSAdminDAO")
	EbMSDAO ebMSDAO;
	boolean showContent;
	Panel messageViewPanel;
	boolean rawOutput;

	public MessagePageX(final IModel<EbMSMessage> model, final WebPage responsePage)
	{
		setModel(new CompoundPropertyModel<>(model));
		add(new BootstrapFeedbackPanel("feedback"));
		add(new Label("messageId"));
		add(new Label("messageNr"));
		add(new Label("conversationId"));
		add(createViewRefToMessageIdLink("viewRefToMessageId"));
		add(InstantLabel.of("timestamp",Constants.DATETIME_FORMAT));
		add(new Label("cpaId"));
		add(new Label("fromPartyId"));
		add(new Label("fromRole"));
		add(new Label("toPartyId"));
		add(new Label("toRole"));
		add(new Label("service"));
		add(createActionField("action"));
		add(createViewMessageErrorLink("viewMessageError"));
		add(InstantLabel.of("statusTime",Constants.DATETIME_FORMAT));
		add(createNextEventContainer("nextEvent"));
		add(createEventLogContainer("eventLog"));
		add(createRawOutputContainer("rawOutputContainer"));
		messageViewPanel = createMessageViewPanel("attachments");
		add(messageViewPanel);
		add(new PageLink("back",responsePage));
		add(new DownloadEbMSMessageLink("download",ebMSDAO,model));
		val content = createContentField("content");
		add(content);
		add(createContentToggleLink("toggleContent",content));
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
	
	public boolean getRawOutput()
	{
		return rawOutput;
	}
	
	public void setRawOutput(boolean rawOutput)
	{
		this.rawOutput = rawOutput;
	}
	
	@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
	public class ErrorMessageModalWindow extends ModalWindow
	{
		private static final long serialVersionUID = 1L;
		String title;

		public ErrorMessageModalWindow(String id, String title, String errorMessage)
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

	private Link<Void> createViewRefToMessageIdLink(String id)
	{
		val link = Link.<Void>builder()
				.id(id)
				.onClick(() -> setResponsePage(new MessagePageX(MessageDataModel.of(ebMSDAO,ebMSDAO.findMessage(getModelObject().getRefToMessageId())),MessagePageX.this)))
				.build();
		link.add(new Label("refToMessageId"));
		return link;
	}
	
	private Component[] createActionField(String id)
	{
		//TODO improve: do not generate messageErrorModalWindow and link if message is not of type MessageError
		val messageErrorModalWindow = new ErrorMessageModalWindow("messageErrorWindow","messageError",Utils.getErrorList(getModelObject().getContent()));
		val link = AjaxLink.<Void>builder()
				.id("showMessageErrorWindow")
				.onClick(t ->messageErrorModalWindow.show(t))
				.build();
		link.setEnabled(EbMSAction.EBMS_SERVICE_URI.equals(getModelObject().getService()) && "MessageError".equals(getModelObject().getAction()));
		link.add(new Label(id));
		return new Component[] {link,messageErrorModalWindow};
	}

	private AjaxLink<Void> createViewMessageErrorLink(String id)
	{
		val linkMessageError = AjaxLink.<Void>builder()
				.id(id)
				.onClick(t -> setResponsePage(new MessagePageX(Model.of(ebMSDAO.findResponseMessage(getModelObject().getMessageId())),MessagePageX.this)))
				.build();
		linkMessageError.setEnabled(
				EnumSet.of(EbMSMessageStatus.RECEIVED,EbMSMessageStatus.PROCESSED,EbMSMessageStatus.DELIVERED,EbMSMessageStatus.FAILED,EbMSMessageStatus.DELIVERY_FAILED)
				.contains(getModelObject().getStatus()) ? ebMSDAO.existsResponseMessage(getModelObject().getMessageId()) : false);
		linkMessageError.add(AttributeModifier.replace("class",Model.of(Utils.getTableCellCssClass(getModelObject().getStatus()))));
		linkMessageError.add(new Label("status"));
		return linkMessageError;
	}

	private WebMarkupContainer createNextEventContainer(String id)
	{
		val nextEvent = new WebMarkupContainer(id);
		nextEvent.setVisible(getModelObject().getEvent() != null);
		if (getModelObject().getEvent() != null)
		{
			nextEvent.add(InstantLabel.of("event.timestamp",Constants.DATETIME_FORMAT));
			nextEvent.add(new Label("event.retries"));
			nextEvent.add(InstantLabel.of("event.timeToLive",Constants.DATETIME_FORMAT));
		}
		return nextEvent;
	}

	private WebMarkupContainer createEventLogContainer(String id)
	{
		WebMarkupContainer eventLog = new WebMarkupContainer(id);
		eventLog.setVisible(getModelObject().getEvents().size() > 0);
		eventLog.add(new EbMSEventLogPropertyListView("events",new LoadableDetachableEbMSEventLogModel()));
		return eventLog;
	}

	private WebMarkupContainer createRawOutputContainer(String id)
	{
		WebMarkupContainer rawOutputContainer = WebMarkupContainer.builder()
				.id(id)
				.isVisible(() -> WicketApplication.get().getMessageViewPanels().containsKey(MessageProvider.createId(getModelObject().getService(),getModelObject().getAction())))
				.build();
		val rawOutput = new CheckBox("rawOutput",new PropertyModel<>(this,"rawOutput"));
		rawOutput.setLabel(new ResourceModel("lbl.rawOutput"));
		Consumer<AjaxRequestTarget> onUpdate = t ->
		{
			if (getRawOutput())
				messageViewPanel.replaceWith(messageViewPanel = new AttachmentsPanel("attachments",new LoadableDetachableEbMSAttachmentModel()));
			else
			{
				try
				{
					messageViewPanel.replaceWith(messageViewPanel = WicketApplication.get().getMessageViewPanels().get(MessageProvider.createId(getModelObject().getService(),getModelObject().getAction())).getPanel("attachments",getModelObject().getAttachments()));
				}
				catch (Exception e)
				{
					warn("Unable to view message for action" + MessageProvider.createId(getModelObject().getService(),getModelObject().getAction()) + ". " + e.getMessage());
					messageViewPanel.replaceWith(messageViewPanel = new AttachmentsPanel("attachments",new LoadableDetachableEbMSAttachmentModel()));
				}
			}
			t.add(getPage());
		};
		rawOutput.add(new AjaxFormComponentUpdatingBehavior("change",onUpdate));
		rawOutputContainer.add(rawOutput);
		return rawOutputContainer;
	}

	private Panel createMessageViewPanel(String id)
	{
		if (WicketApplication.get().getMessageViewPanels().containsKey(MessageProvider.createId(getModelObject().getService(),getModelObject().getAction())))
		{
			try
			{
				return WicketApplication.get().getMessageViewPanels().get(MessageProvider.createId(getModelObject().getService(),getModelObject().getAction())).getPanel(id,getModelObject().getAttachments());
			}
			catch (Exception e)
			{
				warn("Unable to view message for action" + MessageProvider.createId(getModelObject().getService(),getModelObject().getAction()) + ". " + e.getMessage());
				return new AttachmentsPanel(id,new LoadableDetachableEbMSAttachmentModel());
			}
		}
		else
			return new AttachmentsPanel(id,new LoadableDetachableEbMSAttachmentModel());
	}

	private TextArea<String> createContentField(String id)
	{
		val result = TextArea.<String>builder()
				.id(id)
				.model(Model.of(getModelObject().getContent()))
				.isVisible(() -> showContent)
				.build();
		result.setOutputMarkupPlaceholderTag(true);
		result.setEnabled(false);
		return result;
	}

	private AjaxLink<String> createContentToggleLink(String id, final Component content)
	{
		Consumer<AjaxRequestTarget> onClick = t ->
		{
			showContent = !showContent;
			t.add(this);
			t.add(content);
		};
		val result = new AjaxLink<String>(id,onClick);
		result.add(new Label("label",StringModel.of(() -> getLocalizer().getString(showContent ? "cmd.hide" : "cmd.show",this))));
		return result;
	}

}
