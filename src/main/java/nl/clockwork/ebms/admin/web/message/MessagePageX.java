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

import nl.clockwork.ebms.Constants.EbMSEventStatus;
import nl.clockwork.ebms.Constants.EbMSMessageStatus;
import nl.clockwork.ebms.admin.Constants;
import nl.clockwork.ebms.admin.dao.EbMSDAO;
import nl.clockwork.ebms.admin.model.EbMSEventLog;
import nl.clockwork.ebms.admin.model.EbMSMessage;
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.BootstrapFeedbackPanel;
import nl.clockwork.ebms.admin.web.MessageProvider;
import nl.clockwork.ebms.admin.web.PageLink;
import nl.clockwork.ebms.admin.web.Utils;
import nl.clockwork.ebms.admin.web.WebMarkupContainer;
import nl.clockwork.ebms.admin.web.WicketApplication;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.IGenericComponent;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class MessagePageX extends BasePage implements IGenericComponent<EbMSMessage>
{
	private static final long serialVersionUID = 1L;
	@SpringBean(name="ebMSAdminDAO")
	private EbMSDAO ebMSDAO;
	protected boolean showContent;
	protected Panel messageViewPanel;
	protected boolean rawOutput;

	public MessagePageX(final EbMSMessage message, final WebPage responsePage)
	{
		setModel(new CompoundPropertyModel<EbMSMessage>(message));
		add(new BootstrapFeedbackPanel("feedback"));
		add(new Label("messageId"));
		add(new Label("messageNr"));
		add(new Label("conversationId"));
		add(createViewRefToMessageIdLink("viewRefToMessageId",message));
		add(DateLabel.forDatePattern("timestamp",Constants.DATETIME_FORMAT));
		add(new Label("cpaId"));
		add(new Label("fromPartyId"));
		add(new Label("fromRole"));
		add(new Label("toPartyId"));
		add(new Label("toRole"));
		add(new Label("service"));
		add(new Label("action"));
		add(createViewMessageErrorLink("viewMessageError",message));
		add(new Label("statusTime"));
		add(createNextEventContainer("nextEvent",message));
		add(createEventLogContainer("eventLog",message));
		add(createRawOutputContainer("rawOutputContainer",message));
		messageViewPanel = createMessageViewPanel("attachments",message);
		add(messageViewPanel);
		add(new PageLink("back",responsePage));
		add(new DownloadEbMSMessageLink("download",ebMSDAO,message));
		final TextArea<String> content = createContentField("content",message);
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
	
	public class ErrorMessageModalWindow extends ModalWindow
	{
		private static final long serialVersionUID = 1L;

		public ErrorMessageModalWindow(String id, String errorMessage)
		{
			super(id);
			setCssClassName(ModalWindow.CSS_CLASS_GRAY);
			setContent(new ErrorMessagePanel(this,Model.of(errorMessage)));
			setCookieName("eventError");
			setCloseButtonCallback(new ModalWindow.CloseButtonCallback()
			{
				private static final long serialVersionUID = 1L;

				public boolean onCloseButtonClicked(AjaxRequestTarget target)
				{
					return true;
				}
			});
		}
		
		@Override
		public IModel<String> getTitle()
		{
			return new Model<String>(getLocalizer().getString("eventError",this));
		}
	}

	private Link<Void> createViewRefToMessageIdLink(String id, final EbMSMessage message)
	{
		Link<Void> link = new Link<Void>(id)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick()
			{
				setResponsePage(new MessagePageX(ebMSDAO.findMessage(message.getRefToMessageId()),MessagePageX.this));
			}
		};
		link.add(new Label("refToMessageId"));
		return link;
	}
	
	private AjaxLink<Void> createViewMessageErrorLink(String id, final EbMSMessage message)
	{
		AjaxLink<Void> linkMessageError = new AjaxLink<Void>(id)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target)
			{
				setResponsePage(new MessagePageX(ebMSDAO.findResponseMessage(message.getMessageId()),MessagePageX.this));
			}
		};
		linkMessageError.setEnabled(EbMSMessageStatus.DELIVERY_FAILED.equals(message.getStatus()));
		linkMessageError.add(AttributeModifier.replace("class",Model.of(Utils.getTableCellCssClass(message.getStatus()))));
		linkMessageError.add(new Label("status"));
		return linkMessageError;
	}

	private WebMarkupContainer createNextEventContainer(String id, final EbMSMessage message)
	{
		WebMarkupContainer nextEvent = new WebMarkupContainer(id);
		nextEvent.setVisible(message.getEvent() != null);
		if (message.getEvent() != null)
		{
			nextEvent.add(DateLabel.forDatePattern("event.timestamp",Constants.DATETIME_FORMAT));
			nextEvent.add(new Label("event.retries"));
			nextEvent.add(DateLabel.forDatePattern("event.timeToLive",Constants.DATETIME_FORMAT));
		}
		return nextEvent;
	}

	private WebMarkupContainer createEventLogContainer(String id, final EbMSMessage message)
	{
		WebMarkupContainer eventLog = new WebMarkupContainer(id);
		eventLog.setVisible(message.getEvents().size() > 0);
		PropertyListView<EbMSEventLog> events = new PropertyListView<EbMSEventLog>("events",message.getEvents())
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<EbMSEventLog> item)
			{
				final ModalWindow errorMessageModalWindow = new ErrorMessageModalWindow("errorMessageWindow",item.getModelObject().getErrorMessage());
				item.add(DateLabel.forDatePattern("timestamp",Constants.DATETIME_FORMAT));
				item.add(new Label("uri"));
				item.add(errorMessageModalWindow);
				AjaxLink<Void> link = new AjaxLink<Void>("showErrorMessageWindow")
				{
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(AjaxRequestTarget target)
					{
						errorMessageModalWindow.show(target);
					}
				};
				link.setEnabled(EbMSEventStatus.FAILED.equals(item.getModelObject().getStatus()));
				link.add(new Label("status"));
				item.add(link);
			}
		};
		eventLog.add(events);
		return eventLog;
	}

	private WebMarkupContainer createRawOutputContainer(String id, final EbMSMessage message)
	{
		WebMarkupContainer rawOutputContainer = new WebMarkupContainer(id)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible()
			{
				return WicketApplication.get().getMessageViewPanels().containsKey(MessageProvider.createId(message.getService(),message.getAction()));
			}
		};
		CheckBox rawOutput = new CheckBox("rawOutput",new PropertyModel<Boolean>(this,"rawOutput"))
		{
			private static final long serialVersionUID = 1L;

			@Override
			public IModel<String> getLabel()
			{
				return Model.of(getLocalizer().getString("lbl.rawOutput",MessagePageX.this));
			}
		};
		rawOutput.add(new AjaxFormComponentUpdatingBehavior("onchange")
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target)
			{
				if (getRawOutput())
					messageViewPanel.replaceWith(messageViewPanel = new AttachmentsPanel("attachments",message.getAttachments()));
				else
				{
					try
					{
						messageViewPanel.replaceWith(messageViewPanel = WicketApplication.get().getMessageViewPanels().get(MessageProvider.createId(message.getService(),message.getAction())).getPanel("attachments",message.getAttachments()));
					}
					catch (Exception e)
					{
						warn("Unable to view message for action" + MessageProvider.createId(message.getService(),message.getAction()) + ". " + e.getMessage());
						messageViewPanel.replaceWith(messageViewPanel = new AttachmentsPanel("attachments",message.getAttachments()));
					}
				}
				target.add(getPage());
			}
		});
		rawOutputContainer.add(rawOutput);
		return rawOutputContainer;
	}

	private Panel createMessageViewPanel(String id, final EbMSMessage message)
	{
		if (WicketApplication.get().getMessageViewPanels().containsKey(MessageProvider.createId(message.getService(),message.getAction())))
		{
			try
			{
				return WicketApplication.get().getMessageViewPanels().get(MessageProvider.createId(message.getService(),message.getAction())).getPanel(id,message.getAttachments());
			}
			catch (Exception e)
			{
				warn("Unable to view message for action" + MessageProvider.createId(message.getService(),message.getAction()) + ". " + e.getMessage());
				return new AttachmentsPanel(id,message.getAttachments());
			}

		}
		else
			return new AttachmentsPanel(id,message.getAttachments());
	}

	private TextArea<String> createContentField(String id, final EbMSMessage message)
	{
		final TextArea<String> content = new TextArea<String>(id,Model.of(message.getContent()))
		{
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible()
			{
				return showContent;
			}
		};
		content.setOutputMarkupPlaceholderTag(true);
		content.setEnabled(false);
		return content;
	}

	private AjaxLink<String> createContentToggleLink(String id, final Component content)
	{
		AjaxLink<String> toggleContent = new AjaxLink<String>(id)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target)
			{
				showContent = !showContent;
				target.add(this);
				target.add(content);
			}
		};
		toggleContent.add(new Label("label",new Model<String>()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public String getObject()
			{
				return MessagePageX.this.getLocalizer().getString(showContent ? "cmd.hide" : "cmd.show",MessagePageX.this);
			}
		}));
		return toggleContent;
	}

	@Override
	public EbMSMessage getModelObject()
	{
		return (EbMSMessage)getDefaultModelObject();
	}

	@Override
	public void setModelObject(EbMSMessage object)
	{
		setDefaultModelObject(object);
	}

	@SuppressWarnings("unchecked")
	@Override
	public IModel<EbMSMessage> getModel()
	{
		return (IModel<EbMSMessage>)getDefaultModel();
	}

	@Override
	public void setModel(IModel<EbMSMessage> model)
	{
		setDefaultModel(model);
	}

}
