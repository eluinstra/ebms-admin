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

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.IGenericComponent;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.datetime.markup.html.basic.DateLabel;

import nl.clockwork.ebms.Constants.EbMSEventStatus;
import nl.clockwork.ebms.Constants.EbMSMessageStatus;
import nl.clockwork.ebms.admin.Constants;
import nl.clockwork.ebms.admin.dao.EbMSDAO;
import nl.clockwork.ebms.admin.model.EbMSEventLog;
import nl.clockwork.ebms.admin.model.EbMSMessage;
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.PageLink;
import nl.clockwork.ebms.admin.web.Utils;
import nl.clockwork.ebms.admin.web.WebMarkupContainer;

public class MessagePage extends BasePage implements IGenericComponent<EbMSMessage,MessagePage>
{
	private static final long serialVersionUID = 1L;
	@SpringBean(name="ebMSAdminDAO")
	private EbMSDAO ebMSDAO;
	protected boolean showContent;

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
		TextArea<String> content = createContentField("content",message);
		add(content);
		add(createToggleContentLink("toggleContent",content));
	}

	private Component[] createActionField(String id, EbMSMessage message)
	{
		final ModalWindow messageErrorModalWindow = new ErrorMessageModalWindow("messageErrorWindow","messageError",getErrorList(getModelObject().getContent()));
		AjaxLink<Void> link = new AjaxLink<Void>("showMessageErrorWindow")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target)
			{
				messageErrorModalWindow.show(target);
			}
		};
		link.setEnabled(nl.clockwork.ebms.Constants.EBMS_SERVICE_URI.equals(getModelObject().getService()) && "MessageError".equals(getModelObject().getAction()));
		link.add(new Label(id));
		return new Component[] {link,messageErrorModalWindow};
	}

	private String getErrorList(String content)
	{
		return content.replaceFirst("(?ms)^.*(<[^<>]*:?ErrorList.*ErrorList>).*$","$1");
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
		private String title;

		public ErrorMessageModalWindow(String id, String title, String errorMessage)
		{
			super(id);
			this.title = title;
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
			return new Model<>(getLocalizer().getString(title,this));
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
		result.add(new Label("refToMessageId"));
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
		result.setEnabled(EbMSMessageStatus.DELIVERY_FAILED.equals(message.getStatus()) ? ebMSDAO.existsResponseMessage(message.getMessageId()) : false);
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
		WebMarkupContainer result = new WebMarkupContainer(id);
		result.setVisible(message.getEvents().size() > 0);
		PropertyListView<EbMSEventLog> events = new PropertyListView<EbMSEventLog>("events",message.getEvents())
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<EbMSEventLog> item)
			{
				final ModalWindow errorMessageModalWindow = new ErrorMessageModalWindow("errorMessageWindow","eventError",item.getModelObject().getErrorMessage());
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
		result.add(events);
		return result;
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
