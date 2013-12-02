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
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.BootstrapFeedbackPanel;
import nl.clockwork.ebms.admin.web.BootstrapFormComponentFeedbackBorder;
import nl.clockwork.ebms.admin.web.MessageProvider;
import nl.clockwork.ebms.admin.web.ResetButton;
import nl.clockwork.ebms.admin.web.WicketApplication;
import nl.clockwork.ebms.common.XMLMessageBuilder;
import nl.clockwork.ebms.model.EbMSMessageContent;
import nl.clockwork.ebms.model.EbMSMessageContext;
import nl.clockwork.ebms.service.CPAService;
import nl.clockwork.ebms.service.EbMSMessageService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
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

			DropDownChoice<String> cpaIds = new DropDownChoice<String>("cpaIds",new PropertyModel<String>(this.getModelObject(),"cpaId"),Model.ofList(cpaService.getCPAIds()))
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.cpaId",MessageForm.this));
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
						EbMSMessageContextModel model = MessageForm.this.getModelObject();
						CollaborationProtocolAgreement cpa = XMLMessageBuilder.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
						model.resetFromRoles(CPAUtils.getRoleNames(cpa));
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

			DropDownChoice<String> fromRoles = new DropDownChoice<String>("fromRoles",new PropertyModel<String>(this.getModelObject(),"fromRole"),new PropertyModel<List<String>>(this.getModelObject(),"fromRoles"))
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.fromRole",MessageForm.this));
				}
			};
			fromRoles.setRequired(true);
			fromRoles.setOutputMarkupId(true);
			add(new BootstrapFormComponentFeedbackBorder("fromRoleFeedback",fromRoles));
			
			fromRoles.add(new AjaxFormComponentUpdatingBehavior("onchange")
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					try
					{
						EbMSMessageContextModel model = MessageForm.this.getModelObject();
						CollaborationProtocolAgreement cpa = XMLMessageBuilder.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
						model.resetServices(CPAUtils.getServiceNames(cpa,model.getFromRole()));
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

			DropDownChoice<String> services = new DropDownChoice<String>("services",new PropertyModel<String>(this.getModelObject(),"service"),new PropertyModel<List<String>>(this.getModelObject(),"services"))
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.service",MessageForm.this));
				}
			};
			services.setRequired(true);
			services.setOutputMarkupId(true);
			add(new BootstrapFormComponentFeedbackBorder("serviceFeedback",services));
			
			services.add(new AjaxFormComponentUpdatingBehavior("onchange")
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					try
					{
						EbMSMessageContextModel model = MessageForm.this.getModelObject();
						CollaborationProtocolAgreement cpa = XMLMessageBuilder.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
						model.resetActions(CPAUtils.getFromActionNames(cpa,model.getFromRole(),model.getService()));
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

			DropDownChoice<String> actions = new DropDownChoice<String>("actions",new PropertyModel<String>(this.getModelObject(),"action"),new PropertyModel<List<String>>(this.getModelObject(),"actions"))
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.action",MessageForm.this));
				}
			};
			actions.setRequired(true);
			actions.setOutputMarkupId(true);
			add(new BootstrapFormComponentFeedbackBorder("actionFeedback",actions));

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

			add(new TextField<String>("conversationId")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.conversationId",MessageForm.this));
				}
			});

			add(new TextField<String>("messageId")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.messageId",MessageForm.this));
				}
			});

			add(new TextField<String>("refToMessageId")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.refToMessageId",MessageForm.this));
				}
			});

			WebMarkupContainer rawInputContainer = new WebMarkupContainer("rawInputContainer")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isVisible()
				{
					EbMSMessageContextModel model = MessageForm.this.getModelObject();
					return model.getAction() != null && (WicketApplication.get().getMessageEditPanels().containsKey(MessageProvider.createId(model.getService(),model.getAction()))) || (dataSources != null && !(dataSources instanceof EmptyDataSourcesPanel || dataSources instanceof DefaultDataSourcesPanel));
				}
			};
			add(rawInputContainer);
			
			CheckBox rawInput = new CheckBox("rawInput")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.rawInput",MessageForm.this));
				}
			};
			rawInputContainer.add(rawInput);

			rawInput.add(new AjaxFormComponentUpdatingBehavior("onchange")
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

			add(dataSources = new EmptyDataSourcesPanel("dataSources"));

			Button send = new Button("send",new ResourceModel("cmd.send"))
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
			setDefaultButton(send);
			add(send);

			add(new ResetButton("reset",new ResourceModel("cmd.reset"),SendMessagePageX.class));
		}
	}

	public class EbMSMessageContextModel extends EbMSMessageContext
	{
		private static final long serialVersionUID = 1L;
		//private List<String> fromParties = new ArrayList<String>();
		private List<String> fromRoles = new ArrayList<String>();
		private List<String> services = new ArrayList<String>();
		private List<String> actions = new ArrayList<String>();
		private boolean rawInput;

		public List<String> getFromRoles()
		{
			return fromRoles;
		}
		public void resetFromRoles()
		{
			getFromRoles().clear();
			setFromRole(null);
		}
		public void resetFromRoles(List<String> roles)
		{
			resetFromRoles();
			getFromRoles().addAll(roles);
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
	}
}
