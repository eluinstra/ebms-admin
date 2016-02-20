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
import nl.clockwork.ebms.admin.Utils;
import nl.clockwork.ebms.admin.dao.EbMSDAO;
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.BootstrapFeedbackPanel;
import nl.clockwork.ebms.admin.web.BootstrapFormComponentFeedbackBorder;
import nl.clockwork.ebms.admin.web.CheckBox;
import nl.clockwork.ebms.admin.web.LocalizedStringResource;
import nl.clockwork.ebms.admin.web.ResetButton;
import nl.clockwork.ebms.admin.web.StringTextField;
import nl.clockwork.ebms.common.XMLMessageBuilder;
import nl.clockwork.ebms.model.MessageStatus;
import nl.clockwork.ebms.model.Party;
import nl.clockwork.ebms.service.CPAService;
import nl.clockwork.ebms.service.EbMSMessageService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.Button;
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
	@SpringBean(name="cleoPatch")
	private Boolean cleoPatch;

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
			add(new BootstrapFormComponentFeedbackBorder("cpaIdFeedback",createCPAIdsChoice("cpaIds")));
			add(new BootstrapFormComponentFeedbackBorder("fromPartyIdFeedback",createFromPartyIdsChoice("fromPartyIds")));
			add(new BootstrapFormComponentFeedbackBorder("fromRoleFeedback",createFromRolesChoice("fromRoles")).setVisible(cleoPatch));
			add(new BootstrapFormComponentFeedbackBorder("toPartyIdFeedback",createToPartyIdsChoice("toPartyIds")));
			add(new BootstrapFormComponentFeedbackBorder("toRoleFeedback",createToRolesChoice("toRoles")).setVisible(cleoPatch));
			final DropDownChoice<String> messageIds = createMessageIdsChoice("messageIds");
			add(new BootstrapFormComponentFeedbackBorder("messageIdFeedback",messageIds,createMessageIdField("messageId")));
			add(createManualChackBox("manual",messageIds));
			Button ping = createCheckButton("check");
			setDefaultButton(ping);
			add(ping);
			add(new ResetButton("reset",new ResourceModel("cmd.reset"),MessageStatusPage.class));
		}

		protected List<String> getFromRoles(MessageStatusFormModel model)
		{
			if (model.getFromRole() != null)
				return Arrays.asList(model.getFromRole());
			else
				return model.getFromRoles();
		}

		protected List<String> getToRoles(MessageStatusFormModel model)
		{
			if (model.getToRole() != null)
				return Arrays.asList(model.getToRole());
			else
				return model.getToRoles();
		}

		private DropDownChoice<String> createCPAIdsChoice(String id)
		{
			DropDownChoice<String> result = new DropDownChoice<String>(id,new PropertyModel<String>(this.getModelObject(),"cpaId"),Model.ofList(Utils.toList(cpaService.getCPAIds())))
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
			//cpaIds.setRequired(true);
			result.add(new AjaxFormComponentUpdatingBehavior("onchange")
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					try
					{
						MessageStatusFormModel model = MessageStatusForm.this.getModelObject();
						CollaborationProtocolAgreement cpa = XMLMessageBuilder.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
						model.resetFromPartyIds(CPAUtils.getPartyIds(cpa));
						model.resetFromRoles(CPAUtils.getRoleNames(cpa));
						model.resetToPartyIds();
						model.resetToRoles();
						//model.resetMessageId();
						model.resetMessageIds(ebMSDAO.selectMessageIds(model.getCpaId(),getFromRoles(model),getToRoles(model),EbMSMessageStatus.SENT));
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
			return result;
		}

		private DropDownChoice<String> createFromPartyIdsChoice(String id)
		{
			DropDownChoice<String> result = new DropDownChoice<String>(id,new PropertyModel<String>(this.getModelObject(),"fromPartyId"),new PropertyModel<List<String>>(this.getModelObject(),"fromPartyIds"))
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.fromPartyId",MessageStatusForm.this));
				}
				
				@Override
				public boolean isEnabled()
				{
					return !MessageStatusForm.this.getModelObject().getManual();
				}

				@Override
				public boolean isRequired()
				{
					return !MessageStatusForm.this.getModelObject().getManual();
				}
			};
			result.setOutputMarkupId(true);
			result.add(new AjaxFormComponentUpdatingBehavior("onchange")
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					try
					{
						MessageStatusFormModel model = MessageStatusForm.this.getModelObject();
						CollaborationProtocolAgreement cpa = XMLMessageBuilder.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
						model.resetFromRoles(CPAUtils.getRoleNames(cpa,model.getFromPartyId()));
						model.resetToPartyIds(CPAUtils.getOtherPartyIds(cpa,model.getFromPartyId()));
						if (model.getFromRole() != null)
							model.resetToRoles(CPAUtils.getRoleNames(cpa,model.getToPartyId()));
						model.resetMessageIds(ebMSDAO.selectMessageIds(model.getCpaId(),getFromRoles(model),getToRoles(model),EbMSMessageStatus.SENT));
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
			return result;
		}

		private DropDownChoice<String> createFromRolesChoice(String id)
		{
			DropDownChoice<String> result = new DropDownChoice<String>(id,new PropertyModel<String>(this.getModelObject(),"fromRole"),new PropertyModel<List<String>>(this.getModelObject(),"fromRoles"))
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.fromRole",MessageStatusForm.this));
				}
				
				@Override
				public boolean isEnabled()
				{
					return !MessageStatusForm.this.getModelObject().getManual();
				}
			};
			result.setRequired(false).setOutputMarkupId(true);
			result.add(new AjaxFormComponentUpdatingBehavior("onchange")
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					try
					{
						MessageStatusFormModel model = MessageStatusForm.this.getModelObject();
						CollaborationProtocolAgreement cpa = XMLMessageBuilder.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
						model.resetFromPartyIds(CPAUtils.getPartyIdsByRoleName(cpa,model.getFromRole()));
						model.resetToPartyIds(CPAUtils.getOtherPartyIds(cpa,model.getFromPartyId()));
						model.resetToRoles(CPAUtils.getRoleNames(cpa,model.getToPartyId()));
						model.resetMessageIds(ebMSDAO.selectMessageIds(model.getCpaId(),getFromRoles(model),getToRoles(model),EbMSMessageStatus.SENT));
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
			return result;
		}

		private DropDownChoice<String> createToPartyIdsChoice(String id)
		{
			DropDownChoice<String> result = new DropDownChoice<String>(id,new PropertyModel<String>(this.getModelObject(),"toPartyId"),new PropertyModel<List<String>>(this.getModelObject(),"toPartyIds"))
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.toPartyId",MessageStatusForm.this));
				}
				
				@Override
				public boolean isEnabled()
				{
					return !MessageStatusForm.this.getModelObject().getManual();
				}

				@Override
				public boolean isRequired()
				{
					return !MessageStatusForm.this.getModelObject().getManual();
				}
			};
			result.setOutputMarkupId(true);
			result.add(new AjaxFormComponentUpdatingBehavior("onchange")
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					try
					{
						MessageStatusFormModel model = MessageStatusForm.this.getModelObject();
						CollaborationProtocolAgreement cpa = XMLMessageBuilder.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
						model.resetToRoles(CPAUtils.getRoleNames(cpa,model.getToPartyId()));
						model.resetMessageIds(ebMSDAO.selectMessageIds(model.getCpaId(),getFromRoles(model),getToRoles(model),EbMSMessageStatus.SENT));
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
			return result;
		}

		private DropDownChoice<String> createToRolesChoice(String id)
		{
			DropDownChoice<String> result = new DropDownChoice<String>(id,new PropertyModel<String>(this.getModelObject(),"toRole"),new PropertyModel<List<String>>(this.getModelObject(),"toRoles"))
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.toRole",MessageStatusForm.this));
				}
				
				@Override
				public boolean isEnabled()
				{
					return !MessageStatusForm.this.getModelObject().getManual();
				}
			};
			result.setRequired(false).setOutputMarkupId(true);
			result.add(new AjaxFormComponentUpdatingBehavior("onchange")
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					try
					{
						MessageStatusFormModel model = MessageStatusForm.this.getModelObject();
						CollaborationProtocolAgreement cpa = XMLMessageBuilder.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
						if (model.getToPartyId() == null)
							model.resetToPartyIds(CPAUtils.getPartyIdsByRoleName(cpa,model.getToRole()));
						model.resetMessageIds(ebMSDAO.selectMessageIds(model.getCpaId(),getFromRoles(model),getToRoles(model),EbMSMessageStatus.SENT));
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
			return result;
		}

		private DropDownChoice<String> createMessageIdsChoice(String id)
		{
			final DropDownChoice<String> result = new DropDownChoice<String>(id,new PropertyModel<String>(this.getModelObject(),"messageId"),new PropertyModel<List<String>>(this.getModelObject(),"messageIds"))
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
			result.setOutputMarkupPlaceholderTag(true);
			result.setRequired(true);
			return result;
		}

		private TextField<String> createMessageIdField(String id)
		{
			final TextField<String> result = new StringTextField(id,new LocalizedStringResource("lbl.messageId",MessageStatusForm.this))
			{
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isVisible()
				{
					return MessageStatusForm.this.getModelObject().getManual();
				}
			};
			result.setRequired(true).setOutputMarkupPlaceholderTag(true);
			return result;
		}

		private CheckBox createManualChackBox(String id, final DropDownChoice<String> messageIds)
		{
			CheckBox result = new CheckBox(id,new LocalizedStringResource("lbl.manual",MessageStatusForm.this));
			result.add(new AjaxFormComponentUpdatingBehavior("onchange")
      {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					if (messageIds.isVisible())
					{
						MessageStatusFormModel model = MessageStatusForm.this.getModelObject();
						model.resetMessageIds(ebMSDAO.selectMessageIds(model.getCpaId(),getFromRoles(model),getToRoles(model),EbMSMessageStatus.SENT));
						if (model.getMessageIds().size() == 0)
							info("No messages found");
					}
					target.add(getPage().get("feedback"));
					target.add(getPage().get("form"));
				}
      });
			return result;
		}

		private Button createCheckButton(String id)
		{
			Button ping = new Button(id,new ResourceModel("cmd.check"))
			{
				private static final long serialVersionUID = 1L;
	
				@Override
				public void onSubmit()
				{
					try
					{
						MessageStatusFormModel model = MessageStatusForm.this.getModelObject();
						if (!model.getManual() || (model.getCpaId() != null && model.getFromPartyId() != null && model.getToPartyId() != null))
						{
							MessageStatus messageStatus = ebMSMessageService.getMessageStatus(model.getCpaId(),new Party(model.getFromPartyId(),model.getFromRole()),new Party(model.getToPartyId(),model.getToRole()),model.getMessageId());
							info(new StringResourceModel("getMessageStatus.ok",Model.of(messageStatus.getStatus())).getString());
						}
						else
						{
							MessageStatus messageStatus = ebMSMessageService.getMessageStatus(model.getMessageId());
							info(new StringResourceModel("getMessageStatus.ok",Model.of(messageStatus.getStatus())).getString());
						}
					}
					catch (Exception e)
					{
						logger.error("",e);
						error(e.getMessage());
					}
				}
			};
			return ping;
		}

	}

	public class MessageStatusFormModel implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		private String cpaId;
		private List<String> fromPartyIds = new ArrayList<String>();
		private String fromPartyId;
		private List<String> fromRoles = new ArrayList<String>();
		private String fromRole;
		private List<String> toPartyIds = new ArrayList<String>();
		private String toPartyId;
		private List<String> toRoles = new ArrayList<String>();
		private String toRole;
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
		public List<String> getFromPartyIds()
		{
			return fromPartyIds;
		}
		public void setFromPartyId(String fromPartyId)
		{
			this.fromPartyId = fromPartyId;
		}
		public String getFromPartyId()
		{
			return fromPartyId;
		}
		public void resetFromPartyIds()
		{
			getFromPartyIds().clear();
			setFromPartyId(null);
		}
		public void resetFromPartyIds(ArrayList<String> partyIds)
		{
			resetFromPartyIds();
			getFromPartyIds().addAll(partyIds);
			setFromPartyId(getFromPartyIds().size() == 1 ? getFromPartyIds().get(0) : null);
		}
		public List<String> getFromRoles()
		{
			return fromRoles;
		}
		public String getFromRole()
		{
			return fromRole;
		}
		public void setFromRole(String fromRole)
		{
			this.fromRole = fromRole;
		}
		public void resetFromRoles()
		{
			getFromRoles().clear();
			setFromRole(null);
		}
		public void resetFromRoles(List<String> roleNames)
		{
			resetFromRoles();
			getFromRoles().addAll(roleNames);
		}
		public List<String> getToPartyIds()
		{
			return toPartyIds;
		}
		public String getToPartyId()
		{
			return toPartyId;
		}
		public void setToPartyId(String toPartyId)
		{
			this.toPartyId = toPartyId;
		}
		public void resetToPartyIds()
		{
			getToPartyIds().clear();
			setToPartyId(null);
		}
		public void resetToPartyIds(List<String> partyIds)
		{
			resetToPartyIds();
			getToPartyIds().addAll(partyIds);
			setToPartyId(getToPartyIds().size() == 1 ? getToPartyIds().get(0) : null);
		}
		public List<String> getToRoles()
		{
			return toRoles;
		}
		public String getToRole()
		{
			return toRole;
		}
		public void setToRole(String toRole)
		{
			this.toRole = toRole;
		}
		public void resetToRoles()
		{
			getToRoles().clear();
			setToRole(null);
		}
		public void resetToRoles(ArrayList<String> roleNames)
		{
			resetToRoles();
			getToRoles().addAll(roleNames);
			setToRole(getFromRole() != null && getToRoles().size() == 1 ? getToRoles().get(0) : null);
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
