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
import nl.clockwork.ebms.admin.web.ResetButton;
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

			DropDownChoice<String> cpaIds = new DropDownChoice<String>("cpaIds",new PropertyModel<String>(this.getModelObject(),"cpaId"),Model.ofList(Utils.toList(cpaService.getCPAIds())))
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

			DropDownChoice<String> fromPartyIds = new DropDownChoice<String>("fromPartyIds",new PropertyModel<String>(this.getModelObject(),"fromPartyId"),new PropertyModel<List<String>>(this.getModelObject(),"fromPartyIds"))
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
			fromPartyIds.setOutputMarkupId(true);
			add(new BootstrapFormComponentFeedbackBorder("fromPartyIdFeedback",fromPartyIds));
			
			fromPartyIds.add(new AjaxFormComponentUpdatingBehavior("onchange")
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

			DropDownChoice<String> fromRoles = new DropDownChoice<String>("fromRoles",new PropertyModel<String>(this.getModelObject(),"fromRole"),new PropertyModel<List<String>>(this.getModelObject(),"fromRoles"))
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
			fromRoles.setRequired(false).setOutputMarkupId(true);
			add(new BootstrapFormComponentFeedbackBorder("fromRoleFeedback",fromRoles).setVisible(cleoPatch));
			
			fromRoles.add(new AjaxFormComponentUpdatingBehavior("onchange")
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

			DropDownChoice<String> toPartyIds = new DropDownChoice<String>("toPartyIds",new PropertyModel<String>(this.getModelObject(),"toPartyId"),new PropertyModel<List<String>>(this.getModelObject(),"toPartyIds"))
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
			toPartyIds.setOutputMarkupId(true);
			add(new BootstrapFormComponentFeedbackBorder("toPartyIdFeedback",toPartyIds));

			toPartyIds.add(new AjaxFormComponentUpdatingBehavior("onchange")
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

			DropDownChoice<String> toRoles = new DropDownChoice<String>("toRoles",new PropertyModel<String>(this.getModelObject(),"toRole"),new PropertyModel<List<String>>(this.getModelObject(),"toRoles"))
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
			toRoles.setRequired(false).setOutputMarkupId(true);
			add(new BootstrapFormComponentFeedbackBorder("toRoleFeedback",toRoles).setVisible(cleoPatch));
			
			toRoles.add(new AjaxFormComponentUpdatingBehavior("onchange")
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
			messageId.setRequired(true).setOutputMarkupPlaceholderTag(true);
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

			Button ping = new Button("check",new ResourceModel("cmd.check"))
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
