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
import java.util.List;

import javax.xml.bind.JAXBException;

import nl.clockwork.ebms.admin.CPAUtils;
import nl.clockwork.ebms.admin.Utils;
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.BootstrapFeedbackPanel;
import nl.clockwork.ebms.admin.web.BootstrapFormComponentFeedbackBorder;
import nl.clockwork.ebms.admin.web.MessageProvider;
import nl.clockwork.ebms.admin.web.ResetButton;
import nl.clockwork.ebms.admin.web.WebMarkupContainer;
import nl.clockwork.ebms.admin.web.WicketApplication;
import nl.clockwork.ebms.common.XMLMessageBuilder;
import nl.clockwork.ebms.model.EbMSMessageContent;
import nl.clockwork.ebms.model.EbMSMessageContext;
import nl.clockwork.ebms.model.Role;
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
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.oasis_open.committees.ebxml_cppa.schema.cpp_cpa_2_0.CollaborationProtocolAgreement;

public class SendMessagePageX extends BasePage
{
	private static final long serialVersionUID = 1L;
	protected transient Log logger = LogFactory.getLog(getClass());
	@SpringBean(name="cpaService")
	private CPAService cpaService;
	@SpringBean(name="ebMSMessageService")
	private EbMSMessageService ebMSMessageService;

	public SendMessagePageX()
	{
		add(new BootstrapFeedbackPanel("feedback").setOutputMarkupId(true));
		add(new MessageForm("form"));
	}
	
	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("messageSend",this);
	}

	public class MessageForm extends Form<EbMSMessageContextModel>
	{
		private static final long serialVersionUID = 1L;
		private DataSourcesPanel dataSources;

		public MessageForm(String id)
		{
			super(id,new CompoundPropertyModel<EbMSMessageContextModel>(new EbMSMessageContextModel()));
			setMultiPart(true);
			add(new BootstrapFormComponentFeedbackBorder("cpaIdFeedback",createCPAIdChoice("cpaId")));
			add(new BootstrapFormComponentFeedbackBorder("fromPartyIdFeedback",createFromPartyIdChoice("fromRole.partyId")));
			add(new BootstrapFormComponentFeedbackBorder("fromRoleFeedback",createFromRoleChoice("fromRole.role")));
			add(createToPartyIdFeedbackBorder("toPartyIdFeedback",createToPartyIdChoice("toRole.partyId")));
			add(createToRoleFeedbackBorder("toRoleFeedback",createToRoleChoice("toRole.role")));
			add(new BootstrapFormComponentFeedbackBorder("serviceFeedback",createServiceChoice("service")));
			add(new BootstrapFormComponentFeedbackBorder("actionFeedback",createActionChoice("action")));
			add(new TextField<String>("conversationId").setLabel(new ResourceModel("lbl.conversationId")));
			add(new TextField<String>("messageId").setLabel(new ResourceModel("lbl.messageId")));
			add(new TextField<String>("refToMessageId").setLabel(new ResourceModel("lbl.refToMessageId")));
			WebMarkupContainer rawInputContainer = createRawInputContainer();
			add(rawInputContainer);
			rawInputContainer.add(createRawInputCheckBox("rawInput"));
			add(dataSources = new EmptyDataSourcesPanel("dataSources"));
			add(createExtendedCheckBox("extended"));
			Button send = createSendButton("send");
			setDefaultButton(send);
			add(send);
			add(new ResetButton("reset",new ResourceModel("cmd.reset"),SendMessagePageX.class));
		}

		private DropDownChoice<String> createCPAIdChoice(String id)
		{
			DropDownChoice<String> result = new DropDownChoice<String>(id,Model.ofList(Utils.toList(cpaService.getCPAIds())));
			result.setLabel(new ResourceModel("lbl.cpaId"));
			result.setRequired(true);
			result.add(new AjaxFormComponentUpdatingBehavior("onchange")
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					try
					{
						EbMSMessageContextModel model = MessageForm.this.getModelObject();
						CollaborationProtocolAgreement cpa = XMLMessageBuilder.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
						model.resetFromPartyIds(CPAUtils.getPartyIds(cpa));
						model.resetFromRoles(CPAUtils.getRoleNames(cpa));
						if (model.extended)
						{
							model.resetToPartyIds();
							model.resetToRoles();
						}
						model.resetServices();
						model.resetActions();
						dataSources.replaceWith(dataSources = new EmptyDataSourcesPanel(dataSources.getId()));
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

		private DropDownChoice<String> createFromPartyIdChoice(String id)
		{
			DropDownChoice<String> result = new DropDownChoice<String>(id,new PropertyModel<List<String>>(this.getModelObject(),"fromPartyIds"));
			result.setLabel(new ResourceModel("lbl.fromPartyId"));
			result.setRequired(false).setOutputMarkupId(true);
			result.add(new AjaxFormComponentUpdatingBehavior("onchange")
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					try
					{
						EbMSMessageContextModel model = MessageForm.this.getModelObject();
						CollaborationProtocolAgreement cpa = XMLMessageBuilder.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
						model.resetFromRoles(CPAUtils.getRoleNames(cpa,model.getFromRole().getPartyId()));
						if (model.extended)
						{
							if (model.getFromRole().getRole() != null)
								model.resetToRoles(CPAUtils.getOtherRoleNames(cpa,model.getFromRole().getPartyId(),model.getFromRole().getRole()));
							model.resetToPartyIds(CPAUtils.getPartyIdsByRoleName(cpa,model.getToRole().getRole()));
						}
						model.resetServices(CPAUtils.getServiceNames(cpa,model.getFromRole().getRole()));
						model.resetActions();
						dataSources.replaceWith(dataSources = new EmptyDataSourcesPanel(dataSources.getId()));
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

		private DropDownChoice<String> createFromRoleChoice(String id)
		{
			DropDownChoice<String> result = new DropDownChoice<String>(id,new PropertyModel<List<String>>(this.getModelObject(),"fromRoles"));
			result.setLabel(new ResourceModel("lbl.fromRole"));
			result.setRequired(true).setOutputMarkupId(true);
			result.add(new AjaxFormComponentUpdatingBehavior("onchange")
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					try
					{
						EbMSMessageContextModel model = MessageForm.this.getModelObject();
						CollaborationProtocolAgreement cpa = XMLMessageBuilder.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
						model.resetFromPartyIds(CPAUtils.getPartyIdsByRoleName(cpa,model.getFromRole().getRole()));
						if (model.extended)
						{
							model.resetToRoles(CPAUtils.getOtherRoleNames(cpa,model.getFromRole().getPartyId(),model.getFromRole().getRole()));
							model.resetToPartyIds(CPAUtils.getPartyIdsByRoleName(cpa,model.getToRole().getRole()));
						}
						model.resetServices(CPAUtils.getServiceNames(cpa,model.getFromRole().getRole()));
						model.resetActions();
						dataSources.replaceWith(dataSources = new EmptyDataSourcesPanel(dataSources.getId()));
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

		private DropDownChoice<String> createToPartyIdChoice(String id)
		{
			DropDownChoice<String> result = new DropDownChoice<String>(id,new PropertyModel<List<String>>(this.getModelObject(),"toPartyIds"));
			result.setLabel(new ResourceModel("lbl.toPartyId"));
			result.setRequired(true).setOutputMarkupId(true);
			result.add(new AjaxFormComponentUpdatingBehavior("onchange")
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					try
					{
						EbMSMessageContextModel model = MessageForm.this.getModelObject();
						CollaborationProtocolAgreement cpa = XMLMessageBuilder.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
						if (model.extended)
							if (model.getToRole().getRole() == null)
								model.resetToRoles(CPAUtils.getRoleNames(cpa,model.getToRole().getPartyId()));
						model.resetServices(CPAUtils.getServiceNames(cpa,model.getFromRole().getRole()));
						model.resetActions();
						dataSources.replaceWith(dataSources = new EmptyDataSourcesPanel(dataSources.getId()));
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

		private BootstrapFormComponentFeedbackBorder createToPartyIdFeedbackBorder(String id, DropDownChoice<String> toPartyIdChoice)
		{
			return new BootstrapFormComponentFeedbackBorder(id,toPartyIdChoice)
			{
				private static final long serialVersionUID = 1L;
				
				@Override
				public boolean isVisible()
				{
					return MessageForm.this.getModelObject().extended;
				}
			};
		}

		private DropDownChoice<String> createToRoleChoice(String id)
		{
			DropDownChoice<String> result = new DropDownChoice<String>(id,new PropertyModel<List<String>>(this.getModelObject(),"toRoles"));
			result.setLabel(new ResourceModel("lbl.toRole"));
			result.setRequired(true).setOutputMarkupId(true);
			result.add(new AjaxFormComponentUpdatingBehavior("onchange")
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					try
					{
						EbMSMessageContextModel model = MessageForm.this.getModelObject();
						CollaborationProtocolAgreement cpa = XMLMessageBuilder.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
						if (model.extended)
//							if (model.getToRole().getPartyId() == null)
								model.resetToPartyIds(CPAUtils.getPartyIdsByRoleName(cpa,model.getToRole().getRole()));
						model.resetServices(CPAUtils.getServiceNames(cpa,model.getFromRole().getRole()));
						model.resetActions();
						dataSources.replaceWith(dataSources = new EmptyDataSourcesPanel(dataSources.getId()));
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

		private BootstrapFormComponentFeedbackBorder createToRoleFeedbackBorder(String id, DropDownChoice<String> toRoleChoice)
		{
			return new BootstrapFormComponentFeedbackBorder(id,toRoleChoice)
			{
				private static final long serialVersionUID = 1L;
				
				@Override
				public boolean isVisible()
				{
					return MessageForm.this.getModelObject().extended;
				}
			};
		}

		private DropDownChoice<String> createServiceChoice(String id)
		{
			DropDownChoice<String> result = new DropDownChoice<String>(id,new PropertyModel<List<String>>(this.getModelObject(),"services"));
			result.setLabel(new ResourceModel("lbl.service"));
			result.setRequired(true);
			result.setOutputMarkupId(true);
			result.add(new AjaxFormComponentUpdatingBehavior("onchange")
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					try
					{
						EbMSMessageContextModel model = MessageForm.this.getModelObject();
						CollaborationProtocolAgreement cpa = XMLMessageBuilder.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
						model.resetActions(CPAUtils.getFromActionNames(cpa,model.getFromRole().getRole(),model.getService()));
						dataSources.replaceWith(dataSources = new EmptyDataSourcesPanel(dataSources.getId()));
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

		private DropDownChoice<String> createActionChoice(String id)
		{
			DropDownChoice<String> actions = new DropDownChoice<String>(id,new PropertyModel<List<String>>(this.getModelObject(),"actions"));
			actions.setLabel(new ResourceModel("lbl.action"));
			actions.setRequired(true);
			actions.setOutputMarkupId(true);
			actions.add(new AjaxFormComponentUpdatingBehavior("onchange")
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					EbMSMessageContextModel model = MessageForm.this.getModelObject();
					if (WicketApplication.get().getMessageEditPanels().containsKey(MessageProvider.createId(model.getService(),model.getAction())))
						dataSources.replaceWith(dataSources = WicketApplication.get().getMessageEditPanels().get(MessageProvider.createId(model.getService(),model.getAction())).getPanel(dataSources.getId()));
					else
						dataSources.replaceWith(dataSources = new DefaultDataSourcesPanel(dataSources.getId()));
					model.setRawInput(false);
					target.add(getPage().get("feedback"));
					target.add(getPage().get("form"));
				}
			});
			return actions;
		}

		private WebMarkupContainer createRawInputContainer()
		{
			return new WebMarkupContainer("rawInputContainer")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isVisible()
				{
					EbMSMessageContextModel model = MessageForm.this.getModelObject();
					return model.getAction() != null && (WicketApplication.get().getMessageEditPanels().containsKey(MessageProvider.createId(model.getService(),model.getAction()))) || (dataSources != null && !(dataSources instanceof EmptyDataSourcesPanel || dataSources instanceof DefaultDataSourcesPanel));
				}
			};
		}

		private CheckBox createRawInputCheckBox(String id)
		{
			CheckBox result = new CheckBox(id);
			result.setLabel(new ResourceModel("lbl.rawInput"));
			result.add(new AjaxFormComponentUpdatingBehavior("onchange")
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					EbMSMessageContextModel model = MessageForm.this.getModelObject();
					if (model.getRawInput())
						dataSources.replaceWith(dataSources = new DefaultDataSourcesPanel(dataSources.getId()));
					else
						dataSources.replaceWith(dataSources = WicketApplication.get().getMessageEditPanels().get(MessageProvider.createId(model.getService(),model.getAction())).getPanel(dataSources.getId()));
					target.add(getPage().get("feedback"));
					target.add(getPage().get("form"));
				}
			});
			return result;
		}

		private CheckBox createExtendedCheckBox(String id)
		{
			CheckBox result = new CheckBox(id);
			result.setLabel(new ResourceModel("lbl.extended"));
			result.add(new AjaxFormComponentUpdatingBehavior("onchange")
      {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					try
					{
						EbMSMessageContextModel model = MessageForm.this.getModelObject();
						if (model.extended)
						{
							model.setToRole(new Role());
							if (model.getCpaId() != null)
							{
								CollaborationProtocolAgreement cpa = XMLMessageBuilder.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
								if (model.getFromRole().getRole() != null)
									model.resetToRoles(CPAUtils.getOtherRoleNames(cpa,model.getFromRole().getPartyId(),model.getFromRole().getRole()));
								model.resetToPartyIds(CPAUtils.getPartyIdsByRoleName(cpa,model.getToRole().getRole()));
							}
						}
						else
							model.setToRole(null);
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

		private Button createSendButton(String id)
		{
			Button result = new Button(id,new ResourceModel("cmd.send"))
			{
				private static final long serialVersionUID = 1L;
	
				@Override
				public void onSubmit()
				{
					try
					{
						EbMSMessageContextModel model = MessageForm.this.getModelObject();
						EbMSMessageContent messageContent = new EbMSMessageContent(model,dataSources.getDataSources());
						String messageId = ebMSMessageService.sendMessage(messageContent);
						info(new StringResourceModel("sendMessage.ok",Model.of(messageId)).getString());
					}
					catch (Exception e)
					{
						logger.error("",e);
						error(e.getMessage());
					}
				}
			};
			return result;
		}

	}

	public class EbMSMessageContextModel extends EbMSMessageContext
	{
		private static final long serialVersionUID = 1L;
		private List<String> fromPartyIds = new ArrayList<String>();
		private List<String> fromRoles = new ArrayList<String>();
		private List<String> toPartyIds = new ArrayList<String>();
		private List<String> toRoles = new ArrayList<String>();
		private List<String> services = new ArrayList<String>();
		private List<String> actions = new ArrayList<String>();
		private boolean rawInput;
		private boolean extended = true;

		public EbMSMessageContextModel()
		{
			setFromRole(new Role());
			setToRole(new Role());
		}
		
		public List<String> getFromPartyIds()
		{
			return fromPartyIds;
		}
		public void resetFromPartyIds()
		{
			getFromPartyIds().clear();
			getFromRole().setPartyId(null);
		}
		public void resetFromPartyIds(List<String> partyIds)
		{
			resetFromPartyIds();
			getFromPartyIds().addAll(partyIds);
		}
		public List<String> getFromRoles()
		{
			return fromRoles;
		}
		public void resetFromRoles()
		{
			getFromRoles().clear();
			getFromRole().setRole(null);
		}
		public void resetFromRoles(List<String> roles)
		{
			resetFromRoles();
			getFromRoles().addAll(roles);
			getFromRole().setRole(getFromRoles().size() == 1 ? getFromRoles().get(0) : null);
		}
		public List<String> getToPartyIds()
		{
			return toPartyIds;
		}
		public void resetToPartyIds()
		{
			getToPartyIds().clear();
			getToRole().setPartyId(null);
		}
		public void resetToPartyIds(List<String> partyIds)
		{
			resetToPartyIds();
			getToPartyIds().addAll(partyIds);
			getToRole().setPartyId(getFromRole().getPartyId() != null && getToPartyIds().size() == 1 ? getToPartyIds().get(0) : null);
		}
		public List<String> getToRoles()
		{
			return toRoles;
		}
		public void resetToRoles()
		{
			getToRoles().clear();
			getToRole().setRole(null);
		}
		public void resetToRoles(List<String> roles)
		{
			resetToRoles();
			getToRoles().addAll(roles);
			getToRole().setRole(getToRoles().size() == 1 ? getToRoles().get(0) : null);
		}
		public List<String> getServices()
		{
			return services;
		}
		public void resetServices()
		{
			getServices().clear();
			setService(null);
		}
		public void resetServices(List<String> serviceNames)
		{
			resetServices();
			getServices().addAll(serviceNames);
		}
		public List<String> getActions()
		{
			return actions;
		}
		public void resetActions()
		{
			getActions().clear();
			setAction(null);
		}
		public void resetActions(List<String> actionNames)
		{
			resetActions();
			getActions().addAll(actionNames);
		}
		public boolean getRawInput()
		{
			return rawInput;
		}
		public void setRawInput(boolean rawInput)
		{
			this.rawInput = rawInput;
		}
		public boolean getExtended()
		{
			return extended;
		}
		public void setExtended(boolean extended)
		{
			this.extended = extended;
		}
	}
}
