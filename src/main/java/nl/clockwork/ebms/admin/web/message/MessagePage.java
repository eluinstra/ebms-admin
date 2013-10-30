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
import java.util.HashMap;
import java.util.Map;

import nl.clockwork.ebms.Constants.EbMSEventStatus;
import nl.clockwork.ebms.admin.Constants;
import nl.clockwork.ebms.admin.dao.EbMSDAO;
import nl.clockwork.ebms.admin.model.EbMSAttachment;
import nl.clockwork.ebms.admin.model.EbMSEvent;
import nl.clockwork.ebms.admin.model.EbMSMessage;
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.Utils;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.resource.TextTemplateResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class MessagePage extends BasePage
{
	private static final long serialVersionUID = 1L;
	@SpringBean(name="ebMSAdminDAO")
	private EbMSDAO ebMSDAO;

	public MessagePage(final EbMSMessage message, final WebPage responsePage)
	{
		add(new Label("messageId",message.getMessageId()));
		add(new Label("messageNr",message.getMessageNr()));
		add(new Label("conversationId",message.getConversationId()));
		Link<Void> link = new Link<Void>("viewRefToMessageId")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick()
			{
				setResponsePage(new MessagePage(ebMSDAO.findMessage(message.getRefToMessageId()),MessagePage.this));
			}
		};
		link.add(new Label("refToMessageId",message.getRefToMessageId()));
		add(link);
		add(DateLabel.forDatePattern("timestamp",new Model<Date>(message.getTimestamp()),Constants.DATETIME_FORMAT));
		add(new Label("cpaId",message.getCpaId()));
		add(new Label("fromRole",message.getFromRole()));
		add(new Label("toRole",message.getToRole()));
		add(new Label("service",message.getService()));
		add(new Label("action",message.getAction()));
		add(new Label("status",message.getStatus()).add(AttributeModifier.replace("class",Model.of(Utils.getTableCellCssClass(message.getStatus())))));
		add(new Label("statusTime",message.getStatusTime()));
		
		PropertyListView<EbMSAttachment> attachments = 
			new PropertyListView<EbMSAttachment>("attachments",message.getAttachments())
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
			}
		;
		add(attachments);
		
		PropertyListView<EbMSEvent> events = 
			new PropertyListView<EbMSEvent>("events",message.getEvents())
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void populateItem(ListItem<EbMSEvent> item)
				{
					final ModalWindow errorMessageModalWindow = new ErrorMessageModalWindow("errorMessageWindow",item.getModelObject().getErrorMessage());
					item.add(errorMessageModalWindow);
					item.add(DateLabel.forDatePattern("time",new Model<Date>(item.getModelObject().getTime()),Constants.DATETIME_FORMAT));
					item.add(new Label("type"));
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
					item.add(DateLabel.forDatePattern("statusTime",new Model<Date>(item.getModelObject().getStatusTime()),Constants.DATETIME_FORMAT));
					item.add(new Label("uri"));
				}
			}
		;
		add(events);

		add(new Link<Object>("back")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick()
			{
				setResponsePage(responsePage);
			}
		});
		add(new DownloadEbMSMessageLink("download",ebMSDAO,message));

		TextArea<String> content = new TextArea<String>("content",Model.of(message.getContent()));
		content.setEnabled(false);
		add(content);
	}
	
	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("message",this);
	}

	public class ErrorMessageModalWindow extends ModalWindow
	{
		private static final long serialVersionUID = 1L;

		public ErrorMessageModalWindow(String id, String errorMessage)
		{
			super(id);
			//setTitle(getLocalizer().getString("eventError",this));
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
			return Model.of(getLocalizer().getString("eventError",this));
		}
	}

	@Override
	public void renderHead(IHeaderResponse response)
	{
		super.renderHead(response);
		response.render(CssReferenceHeaderItem.forReference(new TextTemplateResourceReference(this.getClass(),"style.css","text/css",new LoadableDetachableModel<Map<String,Object>>()
		{
			private static final long serialVersionUID = 1L;

			public Map<String,Object> load()
			{
				final Map<String,Object> vars = new HashMap<String,Object>();
				vars.put("show",MessagePage.this.getLocalizer().getString("cmd.show",MessagePage.this));
				vars.put("hide",MessagePage.this.getLocalizer().getString("cmd.hide",MessagePage.this));
				return vars;
			}
		})));
	}
}
