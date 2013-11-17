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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBException;

import nl.clockwork.ebms.Constants.EbMSMessageStatus;
import nl.clockwork.ebms.admin.CPAUtils;
import nl.clockwork.ebms.admin.dao.EbMSDAO;
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.BootstrapFeedbackPanel;
import nl.clockwork.ebms.admin.web.BootstrapFormComponentFeedbackBorder;
import nl.clockwork.ebms.admin.web.ResetButton;
import nl.clockwork.ebms.common.XMLMessageBuilder;
import nl.clockwork.ebms.model.MessageStatus;
import nl.clockwork.ebms.service.CPAService;
import nl.clockwork.ebms.service.EbMSMessageService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.io.IClusterable;
import org.oasis_open.committees.ebxml_cppa.schema.cpp_cpa_2_0.CollaborationProtocolAgreement;

public class MessageStatusPage extends BasePage
{
	private static final long serialVersionUID = 1L;
	protected transient Log logger = LogFactory.getLog(getClass());
	@SpringBean(name="ebMSAdminDAO")
	public EbMSDAO ebMSDAO;
	@SpringBean(name="cpaService")
	private CPAService cpaService;
	@SpringBean(name="ebMSMessageService")
	private EbMSMessageService ebMSMessageService;

	public MessageStatusPage()
	{
		add(new BootstrapFeedbackPanel("feedback").setOutputMarkupId(true));
		add(new MessageStatusForm("form"));
	}
	
	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("messageStatus",this);
	}

	public class MessageStatusForm extends Form<MessageStatusFormModel>
	{
		private static final long serialVersionUID = 1L;

		public MessageStatusForm(String id)
		{
			super(id,new CompoundPropertyModel<MessageStatusFormModel>(new MessageStatusFormModel()));

			DropDownChoice<String> cpaIds = new DropDownChoice<String>("cpaIds",new PropertyModel<String>(this.getModelObject(),"cpaId"),Model.ofList(cpaService.getCPAIds()))
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.cpaId",MessageStatusForm.this));
				}

				@Override
				public boolean isRequired()
				{
					return !MessageStatusForm.this.getModelObject().getManual();
				}
			};
			cpaIds.setRequired(true);
			add(new BootstrapFormComponentFeedbackBorder("cpaIdFeedback",cpaIds));

			cpaIds.add(new AjaxFormComponentUpdatingBehavior("onchange")
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					try
					{
						MessageStatusFormModel model = MessageStatusForm.this.getModelObject();
						CollaborationProtocolAgreement cpa = XMLMessageBuilder.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
						model.resetFromParties(CPAUtils.getPartyNames(cpa));
						model.resetToParties();
						model.resetMessageId();
						target.add(getPage().get("feedback"));
						target.add(getPage().get("form"));
					}
					catch (JAXBException e)
					{
						logger.error("",e);
						error(e.getMessage());
					}
				}
			});

			DropDownChoice<String> fromParties = new DropDownChoice<String>("fromParties",new PropertyModel<String>(this.getModelObject(),"fromParty"),new PropertyModel<List<String>>(this.getModelObject(),"fromParties"))
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.fromParty",MessageStatusForm.this));
				}

				@Override
				public boolean isRequired()
				{
					return !MessageStatusForm.this.getModelObject().getManual();
				}
			};
			fromParties.setRequired(true);
			fromParties.setOutputMarkupId(true);
			add(new BootstrapFormComponentFeedbackBorder("fromPartyFeedback",fromParties));
			
			fromParties.add(new AjaxFormComponentUpdatingBehavior("onchange")
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					try
					{
						MessageStatusFormModel model = MessageStatusForm.this.getModelObject();
						CollaborationProtocolAgreement cpa = XMLMessageBuilder.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
						model.resetToParties(CPAUtils.getOtherPartyName(cpa,model.getFromParty()));
						model.resetMessageIds(ebMSDAO.selectMessageIds(model.getCpaId(),model.getFromParty(),model.getToParty(),EbMSMessageStatus.SENT));
						if (model.getMessageIds().size() == 0)
							info("No messages found");
						target.add(getPage().get("feedback"));
						target.add(getPage().get("form"));
					}
					catch (JAXBException e)
					{
						logger.error("",e);
						error(e.getMessage());
					}
				}
			});

			DropDownChoice<String> toParties = new DropDownChoice<String>("toParties",new PropertyModel<String>(this.getModelObject(),"toParty"),new PropertyModel<List<String>>(this.getModelObject(),"toParties"))
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.toParty",MessageStatusForm.this));
				}
				
				@Override
				public boolean isRequired()
				{
					return !MessageStatusForm.this.getModelObject().getManual();
				}
			};
			toParties.setRequired(true);
			toParties.setOutputMarkupId(true);
			add(new BootstrapFormComponentFeedbackBorder("toPartyFeedback",toParties));
			
			final DropDownChoice<String> messageIds = new DropDownChoice<String>("messageIds",new PropertyModel<String>(this.getModelObject(),"messageId"),new PropertyModel<List<String>>(this.getModelObject(),"messageIds"))
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.messageId",MessageStatusForm.this));
				}
				
				@Override
				public boolean isVisible()
				{
					return !MessageStatusForm.this.getModelObject().getManual();
				}
			};
			messageIds.setOutputMarkupPlaceholderTag(true);
			messageIds.setRequired(true);
			//add(new BootstrapFormComponentFeedbackBorder("messageIdFeedback",messageIds));
			
			final TextField<String> messageId = new TextField<String>("messageId")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.messageId",MessageStatusForm.this));
				}
				
				@Override
				public boolean isVisible()
				{
					return MessageStatusForm.this.getModelObject().getManual();
				}
			};
			messageId.setOutputMarkupPlaceholderTag(true);
			messageId.setRequired(true);
			add(new BootstrapFormComponentFeedbackBorder("messageIdFeedback",messageIds,messageId));

			CheckBox manual = new CheckBox("manual")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.manual",MessageStatusForm.this));
				}
			};
			add(manual);
			
			manual.add(new AjaxFormComponentUpdatingBehavior("onchange")
      {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					target.add(messageIds);
					target.add(messageId);
				}
      });

			Button ping = new Button("check",new ResourceModel("cmd.check"))
			{
				private static final long serialVersionUID = 1L;
	
				@Override
				public void onSubmit()
				{
					try
					{
						MessageStatusFormModel model = MessageStatusForm.this.getModelObject();
						//if (!model.getManual() || (model.getCpaId() != null && model.getFromParty() != null && model.getToParty() != null))
						{
							CollaborationProtocolAgreement cpa = XMLMessageBuilder.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
							MessageStatus messageStatus = ebMSMessageService.getMessageStatus(model.getCpaId(),CPAUtils.getPartyIdbyPartyName(cpa,model.getFromParty()),CPAUtils.getPartyIdbyPartyName(cpa,model.getToParty()),model.getMessageId());
							info(new StringResourceModel("getMessageStatus.ok",Model.of(messageStatus.getStatus())).getString());
						}
						//else
						//{
						//	model.setCpaId(null);
						//	model.resetFromParties();
						//	model.resetToParties();
						//	MessageStatus messageStatus = ebMSMessageService.getMessageStatus(model.getMessageId());
						//	info(new StringResourceModel("getMessageStatus.ok",Model.of(messageStatus.getStatus())).getString());
						//}
					}
					catch (Exception e)
					{
						logger.error("",e);
						error(e.getMessage());
					}
				}
			};
			setDefaultButton(ping);
			add(ping);

			add(new ResetButton("reset",new ResourceModel("cmd.reset"),MessageStatusPage.class));
		}
	}

	public class MessageStatusFormModel implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		private String cpaId;
		private List<String> fromParties = new ArrayList<String>();
		private String fromParty;
		private List<String> toParties = new ArrayList<String>();
		private String toParty;
		private List<String> messageIds = new ArrayList<String>();
		private String messageId;
		private boolean manual;
		
		public String getCpaId()
		{
			return cpaId;
		}
		public void setCpaId(String cpaId)
		{
			this.cpaId = cpaId;
		}
		public List<String> getFromParties()
		{
			return fromParties;
		}
		public String getFromParty()
		{
			return fromParty;
		}
		public void setFromParty(String fromParty)
		{
			this.fromParty = fromParty;
		}
		public void resetFromParties()
		{
			getFromParties().clear();
			setFromParty(null);
		}
		public void resetFromParties(ArrayList<String> partyNames)
		{
			resetFromParties();
			getFromParties().addAll(partyNames);
		}
		public List<String> getToParties()
		{
			return toParties;
		}
		public String getToParty()
		{
			return toParty;
		}
		public void setToParty(String toParty)
		{
			this.toParty = toParty;
		}
		public void resetToParties()
		{
			getToParties().clear();
			setToParty(null);
		}
		public void resetToParties(String otherPartyName)
		{
			resetToParties();
			getToParties().addAll(Arrays.asList(otherPartyName));
			setToParty(otherPartyName);
		}
		public List<String> getMessageIds()
		{
			return messageIds;
		}
		public String getMessageId()
		{
			return messageId;
		}
		public void setMessageId(String messageId)
		{
			this.messageId = messageId;
		}
		public void resetMessageId()
		{
			setMessageId(null);
		}
		public void resetMessageIds(List<String> messageIds)
		{
			getMessageIds().clear();
			getMessageIds().addAll(messageIds);
			setMessageId(null);
		}
		public boolean getManual()
		{
			return manual;
		}
		public void setManual(boolean manual)
		{
			this.manual = manual;
		}
	}		

}
