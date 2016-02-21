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
import nl.clockwork.ebms.admin.web.LocalizedStringResource;
import nl.clockwork.ebms.admin.web.ResetButton;
import nl.clockwork.ebms.admin.web.TextField;
import nl.clockwork.ebms.common.XMLMessageBuilder;
import nl.clockwork.ebms.model.EbMSDataSource;
import nl.clockwork.ebms.model.EbMSMessageContent;
import nl.clockwork.ebms.model.EbMSMessageContext;
import nl.clockwork.ebms.model.Role;
import nl.clockwork.ebms.service.CPAService;
import nl.clockwork.ebms.service.EbMSMessageService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.oasis_open.committees.ebxml_cppa.schema.cpp_cpa_2_0.CollaborationProtocolAgreement;

public class SendMessagePage extends BasePage
{
	private static final long serialVersionUID = 1L;
	protected transient Log logger = LogFactory.getLog(getClass());
	@SpringBean(name="cpaService")
	private CPAService cpaService;
	@SpringBean(name="ebMSMessageService")
	private EbMSMessageService ebMSMessageService;

	public SendMessagePage()
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

		public MessageForm(String id)
		{
			super(id,new CompoundPropertyModel<EbMSMessageContextModel>(new EbMSMessageContextModel()));
			setMultiPart(true);
			add(new BootstrapFormComponentFeedbackBorder("cpaIdFeedback",createCPAIdsChoice("cpaIds")));
			add(new BootstrapFormComponentFeedbackBorder("fromPartyIdFeedback",createFromPartyIdsChoice("fromPartyIds")));
			add(new BootstrapFormComponentFeedbackBorder("fromRoleFeedback",createFromRolesChoice("fromRoles")));
			add(new BootstrapFormComponentFeedbackBorder("serviceFeedback",createServicesChoice("services")));
			add(new BootstrapFormComponentFeedbackBorder("actionFeedback",createActionsChoice("actions")));
			add(new TextField<String>("conversationId",new LocalizedStringResource("lbl.conversationId",MessageForm.this)));
			add(new TextField<String>("messageId",new LocalizedStringResource("lbl.messageId",MessageForm.this)));
			add(new TextField<String>("refToMessageId",new LocalizedStringResource("lbl.refToMessageId",MessageForm.this)));
			add(new DataSourcesForm("form",getModelObject().getDataSources()));
			Button send = createSendButton("send");
			setDefaultButton(send);
			add(send);
			add(new ResetButton("reset",new ResourceModel("cmd.reset"),SendMessagePage.class));
		}

		private DropDownChoice<String> createCPAIdsChoice(String id)
		{
			DropDownChoice<String> result = new DropDownChoice<String>(id,new PropertyModel<String>(this.getModelObject(),"cpaId"),Model.ofList(Utils.toList(cpaService.getCPAIds())))
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.cpaId",MessageForm.this));
				}
			};
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
						model.resetServices();
						model.resetActions();
						model.resetDataSources();
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
			DropDownChoice<String> result = new DropDownChoice<String>(id,new PropertyModel<String>(this.getModelObject(),"fromRole.partyId"),new PropertyModel<List<String>>(this.getModelObject(),"fromPartyIds"))
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.fromPartyId",MessageForm.this));
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
						EbMSMessageContextModel model = MessageForm.this.getModelObject();
						CollaborationProtocolAgreement cpa = XMLMessageBuilder.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
						model.resetFromRoles(CPAUtils.getRoleNames(cpa,model.getFromRole().getPartyId()));
						model.resetServices(CPAUtils.getServiceNames(cpa,model.getFromRole().getRole()));
						model.resetActions();
						model.resetDataSources();
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
			DropDownChoice<String> result = new DropDownChoice<String>(id,new PropertyModel<String>(this.getModelObject(),"fromRole.role"),new PropertyModel<List<String>>(this.getModelObject(),"fromRoles"))
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.fromRole",MessageForm.this));
				}
			};
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
						if (model.getFromRole().getPartyId() == null)
							model.resetFromPartyIds(CPAUtils.getPartyIdsByRoleName(cpa,model.getFromRole().getRole()));
						model.resetServices(CPAUtils.getServiceNames(cpa,model.getFromRole().getRole()));
						model.resetActions();
						model.resetDataSources();
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

		private DropDownChoice<String> createServicesChoice(String id)
		{
			DropDownChoice<String> result = new DropDownChoice<String>(id,new PropertyModel<String>(this.getModelObject(),"service"),new PropertyModel<List<String>>(this.getModelObject(),"services"))
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.service",MessageForm.this));
				}
			};
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
						model.resetDataSources();
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

		private DropDownChoice<String> createActionsChoice(String id)
		{
			DropDownChoice<String> result = new DropDownChoice<String>(id,new PropertyModel<String>(this.getModelObject(),"action"),new PropertyModel<List<String>>(this.getModelObject(),"actions"))
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.action",MessageForm.this));
				}
			};
			result.setRequired(true);
			result.setOutputMarkupId(true);
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
						EbMSMessageContent messageContent = new EbMSMessageContent(model,model.getDataSources());
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
		private List<String> services = new ArrayList<String>();
		private List<String> actions = new ArrayList<String>();
		private List<EbMSDataSource> dataSources = new ArrayList<EbMSDataSource>();

		public EbMSMessageContextModel()
		{
			setFromRole(new Role());
		}
		
		public List<String> getFromPartyIds()
		{
			return fromPartyIds;
		}
		public void resetFromPartyIds()
		{
			getFromRoles().clear();
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
		public List<EbMSDataSource> getDataSources()
		{
			return dataSources;
		}
		public void resetDataSources()
		{
			getDataSources().clear();
		}
	}

	public class DataSourcesForm extends Form<List<? extends EbMSDataSource>>
	{
		private static final long serialVersionUID = 1L;

		public DataSourcesForm(String id, List<EbMSDataSource> dataSources)
		{
			super(id,Model.ofList(dataSources));

			ListView<EbMSDataSource> dataSources_ = new ListView<EbMSDataSource>("dataSources",dataSources)
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void populateItem(final ListItem<EbMSDataSource> item)
				{
					item.setModel(new CompoundPropertyModel<EbMSDataSource>(item.getModelObject()));
					item.add(new Label("name"));
					item.add(new Label("contentType"));
					item.add(new AjaxButton("remove",new ResourceModel("cmd.remove"),DataSourcesForm.this)
					{
						private static final long serialVersionUID = 1L;
						
						@Override
						protected void onSubmit(AjaxRequestTarget target, Form<?> form)
						{
							DataSourcesForm.this.getModelObject().remove(item.getModelObject());
							target.add(DataSourcesForm.this);
						}
					});
				}
			};
			dataSources_.setOutputMarkupId(true);
			add(dataSources_);

			final ModalWindow dataSourceModalWindow = new DataSourceModalWindow("dataSourceModelWindow",dataSources,DataSourcesForm.this);
			add(dataSourceModalWindow);
			
			AjaxButton add = new AjaxButton("add")
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form)
				{
					dataSourceModalWindow.show(target);
				}
			};
			add(add);
		}

	}

}
