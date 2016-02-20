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

import nl.clockwork.ebms.Constants.EbMSEventStatus;
import nl.clockwork.ebms.Constants.EbMSMessageStatus;
import nl.clockwork.ebms.admin.Constants;
import nl.clockwork.ebms.admin.dao.EbMSDAO;
import nl.clockwork.ebms.admin.model.EbMSAttachment;
import nl.clockwork.ebms.admin.model.EbMSEventLog;
import nl.clockwork.ebms.admin.model.EbMSMessage;
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.PageLink;
import nl.clockwork.ebms.admin.web.Utils;
import nl.clockwork.ebms.admin.web.WebMarkupContainer;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class MessagePage extends BasePage
{
	private static final long serialVersionUID = 1L;
	@SpringBean(name="ebMSAdminDAO")
	private EbMSDAO ebMSDAO;
	protected boolean showContent;

	public MessagePage(final EbMSMessage message, final WebPage responsePage)
	{
		add(new Label("messageId",message.getMessageId()));
		add(new Label("messageNr",message.getMessageNr()));
		add(new Label("conversationId",message.getConversationId()));
		add(createRefToMessageIdLink("viewRefToMessageId",message));
		add(DateLabel.forDatePattern("timestamp",new Model<Date>(message.getTimestamp()),Constants.DATETIME_FORMAT));
		add(new Label("cpaId",message.getCpaId()));
		add(new Label("fromPartyId",message.getFromPartyId()));
		add(new Label("fromRole",message.getFromRole()));
		add(new Label("toPartyId",message.getToPartyId()));
		add(new Label("toRole",message.getToRole()));
		add(new Label("service",message.getService()));
		add(new Label("action",message.getAction()));
		add(createViewMessageErrorLink("viewMessageError",message));
		add(new Label("statusTime",message.getStatusTime()));
		add(createAttachmentsView("attachments",message));
		add(createNextEventContainer("nextEvent",message));
		add(createEventsView("events",message));
		add(new PageLink("back",responsePage));
		add(new DownloadEbMSMessageLink("download",ebMSDAO,message));
		TextArea<String> content = createContentField("content",message);
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

	private Link<Void> createRefToMessageIdLink(String id, final EbMSMessage message)
	{
		Link<Void> result = new Link<Void>(id)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick()
			{
				setResponsePage(new MessagePage(ebMSDAO.findMessage(message.getRefToMessageId()),MessagePage.this));
			}
		};
		result.add(new Label("refToMessageId",message.getRefToMessageId()));
		return result;
	}
	
	private AjaxLink<Void> createViewMessageErrorLink(String id, final EbMSMessage message)
	{
		AjaxLink<Void> result = new AjaxLink<Void>(id)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target)
			{
				setResponsePage(new MessagePage(ebMSDAO.findResponseMessage(message.getMessageId()),MessagePage.this));
			}
		};
		result.setEnabled(EbMSMessageStatus.DELIVERY_FAILED.equals(message.getStatus()));
		result.add(AttributeModifier.replace("class",Model.of(Utils.getTableCellCssClass(message.getStatus()))));
		result.add(new Label("status",message.getStatus()));
		return result;
	}

	private PropertyListView<EbMSAttachment> createAttachmentsView(String id, final EbMSMessage message)
	{
		return new PropertyListView<EbMSAttachment>(id,message.getAttachments())
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<EbMSAttachment> item)
			{
				item.add(new Label("name"));
				DownloadEbMSAttachmentLink link = new DownloadEbMSAttachmentLink("downloadAttachment",ebMSDAO,item.getModelObject());
				link.add(new Label("contentId"));
				item.add(link);
				item.add(new Label("contentType"));
			}
		};
	}

	private WebMarkupContainer createNextEventContainer(String id, final EbMSMessage message)
	{
		WebMarkupContainer result = new WebMarkupContainer(id);
		result.setVisible(message.getEvent() != null);
		if (message.getEvent() != null)
		{
			result.add(DateLabel.forDatePattern("timestamp",new Model<Date>(message.getEvent().getTimestamp()),Constants.DATETIME_FORMAT));
			result.add(new Label("retry",new Model<Integer>(message.getEvent().getRetries())));
			result.add(DateLabel.forDatePattern("timeToLive",new Model<Date>(message.getEvent().getTimeToLive()),Constants.DATETIME_FORMAT));
		}
		return result;
	}

	private PropertyListView<EbMSEventLog> createEventsView(String id, final EbMSMessage message)
	{
		return new PropertyListView<EbMSEventLog>(id,message.getEvents())
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<EbMSEventLog> item)
			{
				final ModalWindow errorMessageModalWindow = new ErrorMessageModalWindow("errorMessageWindow",item.getModelObject().getErrorMessage());
				item.add(DateLabel.forDatePattern("timestamp",new Model<Date>(item.getModelObject().getTimestamp()),Constants.DATETIME_FORMAT));
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
	}

	private TextArea<String> createContentField(String id, final EbMSMessage message)
	{
		TextArea<String> result = new TextArea<String>(id,Model.of(message.getContent()))
		{
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible()
			{
				return showContent;
			}
		};
		result.setOutputMarkupPlaceholderTag(true);
		result.setEnabled(false);
		return result;
	}

	private AjaxLink<String> createToggleContentLink(String id, final Component content)
	{
		AjaxLink<String> result = new AjaxLink<String>(id)
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
		result.add(new Label("label",new Model<String>()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public String getObject()
			{
				return MessagePage.this.getLocalizer().getString(showContent ? "cmd.hide" : "cmd.show",MessagePage.this);
			}
		}));
		return result;
	}

}
