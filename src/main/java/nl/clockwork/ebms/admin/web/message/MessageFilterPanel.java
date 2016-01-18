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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBException;

import nl.clockwork.ebms.Constants.EbMSMessageStatus;
import nl.clockwork.ebms.admin.CPAUtils;
import nl.clockwork.ebms.admin.Utils;
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.BootstrapDateTimePicker;
import nl.clockwork.ebms.admin.web.BootstrapFeedbackPanel;
import nl.clockwork.ebms.common.XMLMessageBuilder;
import nl.clockwork.ebms.service.CPAService;
import nl.clockwork.ebms.service.EbMSMessageService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.oasis_open.committees.ebxml_cppa.schema.cpp_cpa_2_0.CollaborationProtocolAgreement;

public abstract class MessageFilterPanel extends Panel
{
	private static final long serialVersionUID = 1L;
	protected transient Log logger = LogFactory.getLog(getClass());
	@SpringBean(name="cpaService")
	private CPAService cpaService;
	@SpringBean(name="ebMSMessageService")
	private EbMSMessageService ebMSMessageService;
	private BootstrapDateTimePicker from;
	private BootstrapDateTimePicker to;

	public MessageFilterPanel(String id, MessageFilterFormModel filter)
	{
		super(id);
		add(new BootstrapFeedbackPanel("feedback").setOutputMarkupId(true));
		add(new MessageFilterForm("form",filter));
		add(new Link<Void>("clear")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick()
			{
				setResponsePage(getPage().getClass());
			}
		});
	}
	
	@Override
	public void renderHead(IHeaderResponse response)
	{
		response.render(OnDomReadyHeaderItem.forScript(BootstrapDateTimePicker.getLinkBootstrapDateTimePickersJavaScript(from,to)));
		super.renderHead(response);
	}

	public class MessageFilterForm extends Form<MessageFilterFormModel>
	{
		private static final long serialVersionUID = 1L;

		public MessageFilterForm(String id, MessageFilterFormModel model)
		{
			super(id,new CompoundPropertyModel<MessageFilterFormModel>(model));

			DropDownChoice<String> cpaIds = new DropDownChoice<String>("cpaIds",new PropertyModel<String>(this.getModelObject(),"cpaId"),Model.ofList(Utils.toList(cpaService.getCPAIds())))
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.cpaId",MessageFilterForm.this));
				}
			};
			add(cpaIds);

			cpaIds.add(new AjaxFormComponentUpdatingBehavior("onchange")
      {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					try
					{
						MessageFilterFormModel model = MessageFilterForm.this.getModelObject();
						CollaborationProtocolAgreement cpa = XMLMessageBuilder.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
						model.resetFromPartyIds(CPAUtils.getPartyIds(cpa));
						model.resetFromRoles();
						model.resetToPartyIds(CPAUtils.getPartyIds(cpa));
						model.resetToRoles();
						model.resetServices();
						model.resetActions();
						target.add(getFeedbackComponent());
						target.add(getForm());
					}
					catch (JAXBException e)
					{
						logger.error("",e);
						error(e.getMessage());
					}
				}
      });

			DropDownChoice<String> fromPartyIds = new DropDownChoice<String>("fromPartyIds",new PropertyModel<String>(this.getModelObject(),"fromRole.partyId"),new PropertyModel<List<String>>(this.getModelObject(),"fromPartyIds"))
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.fromPartyId",MessageFilterForm.this));
				}
				
				@Override
				public boolean isEnabled()
				{
					return MessageFilterForm.this.getModelObject().getToRole() == null;
				}
			};
			fromPartyIds.setOutputMarkupId(true);
			add(fromPartyIds);
			
			fromPartyIds.add(new AjaxFormComponentUpdatingBehavior("onchange")
      {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					try
					{
						MessageFilterFormModel model = MessageFilterForm.this.getModelObject();
						CollaborationProtocolAgreement cpa = XMLMessageBuilder.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
						model.resetFromRoles(CPAUtils.getRoleNames(cpa,model.getFromRole().getPartyId()));
						model.resetServices();
						model.resetActions();
						target.add(getFeedbackComponent());
						target.add(getForm());
					}
					catch (JAXBException e)
					{
						logger.error("",e);
						error(e.getMessage());
					}
				}
      });

			DropDownChoice<String> fromRoles = new DropDownChoice<String>("fromRoles",new PropertyModel<String>(this.getModelObject(),"fromRole.role"),new PropertyModel<List<String>>(this.getModelObject(),"fromRoles"))
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.fromRole",MessageFilterForm.this));
				}
				
				@Override
				public boolean isEnabled()
				{
					return MessageFilterForm.this.getModelObject().getToRole() == null;
				}
			};
			fromRoles.setOutputMarkupId(true);
			add(fromRoles);
			
			fromRoles.add(new AjaxFormComponentUpdatingBehavior("onchange")
      {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					try
					{
						MessageFilterFormModel model = MessageFilterForm.this.getModelObject();
						CollaborationProtocolAgreement cpa = XMLMessageBuilder.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
						model.resetServices(CPAUtils.getServiceNames(cpa,model.getFromRole().getRole()));
						model.resetActions();
						target.add(getFeedbackComponent());
						target.add(getForm());
					}
					catch (JAXBException e)
					{
						logger.error("",e);
						error(e.getMessage());
					}
				}
      });

			DropDownChoice<String> toPartyIds = new DropDownChoice<String>("toPartyIds",new PropertyModel<String>(this.getModelObject(),"toRole.partyId"),new PropertyModel<List<String>>(this.getModelObject(),"toPartyIds"))
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.toPartyId",MessageFilterForm.this));
				}

				@Override
				public boolean isEnabled()
				{
					return MessageFilterForm.this.getModelObject().getFromRole() == null;
				}
			};
			toPartyIds.setOutputMarkupId(true);
			add(toPartyIds);
			
			toPartyIds.add(new AjaxFormComponentUpdatingBehavior("onchange")
      {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					try
					{
						MessageFilterFormModel model = MessageFilterForm.this.getModelObject();
						CollaborationProtocolAgreement cpa = XMLMessageBuilder.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
						model.resetToRoles(CPAUtils.getRoleNames(cpa,model.getToRole().getPartyId()));
						model.resetServices();
						model.resetActions();
						target.add(getFeedbackComponent());
						target.add(getForm());
					}
					catch (JAXBException e)
					{
						logger.error("",e);
						error(e.getMessage());
					}
				}
      });

			DropDownChoice<String> toRoles = new DropDownChoice<String>("toRoles",new PropertyModel<String>(this.getModelObject(),"toRole.role"),new PropertyModel<List<String>>(this.getModelObject(),"toRoles"))
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.toRole",MessageFilterForm.this));
				}

				@Override
				public boolean isEnabled()
				{
					return MessageFilterForm.this.getModelObject().getFromRole() == null;
				}
			};
			toRoles.setOutputMarkupId(true);
			add(toRoles);
			
			toRoles.add(new AjaxFormComponentUpdatingBehavior("onchange")
      {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					try
					{
						MessageFilterFormModel model = MessageFilterForm.this.getModelObject();
						CollaborationProtocolAgreement cpa = XMLMessageBuilder.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
						model.resetServices(CPAUtils.getServiceNames(cpa,model.getToRole().getRole()));
						model.resetActions();
						target.add(getFeedbackComponent());
						target.add(getForm());
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
					return Model.of(getLocalizer().getString("lbl.service",MessageFilterForm.this));
				}
			};
			services.setOutputMarkupId(true);
			add(services);

			services.add(new AjaxFormComponentUpdatingBehavior("onchange")
      {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					try
					{
						MessageFilterFormModel model = MessageFilterForm.this.getModelObject();
						CollaborationProtocolAgreement cpa = XMLMessageBuilder.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
						model.resetActions(model.getFromRole() == null ? CPAUtils.getToActionNames(cpa,model.getToRole().getRole(),model.getService()) : CPAUtils.getFromActionNames(cpa,model.getFromRole().getRole(),model.getService()));
						target.add(getFeedbackComponent());
						target.add(getForm());
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
					return Model.of(getLocalizer().getString("lbl.action",MessageFilterForm.this));
				}
			};
			actions.setOutputMarkupId(true);
			add(actions);

			add(new TextField<String>("conversationId")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.conversationId",MessageFilterForm.this));
				}
			});

			add(new TextField<String>("messageId")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.messageId",MessageFilterForm.this));
				}
			});

			add(new TextField<String>("refToMessageId")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.refToMessageId",MessageFilterForm.this));
				}
			});

			add(new ListMultipleChoice<EbMSMessageStatus>("statuses",new PropertyModel<List<EbMSMessageStatus>>(this.getModelObject(),"statuses"),Model.ofList(Arrays.asList(EbMSMessageStatus.values())))
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.status",MessageFilterForm.this));
				}
				
				@Override
				protected boolean localizeDisplayValues()
				{
					return true;
				}
			}.setMaxRows(4));
			
			from = new BootstrapDateTimePicker("from","dd-MM-yyyy")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.from",MessageFilterForm.this));
				}
			};
			from.setType(BootstrapDateTimePicker.Type.DATE);
			add(from);

			to = new BootstrapDateTimePicker("to","dd-MM-yyyy")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.to",MessageFilterForm.this));
				}
			};
			to.setType(BootstrapDateTimePicker.Type.DATE);
			add(to);

			add(new Button("search",new ResourceModel("cmd.search"))
			{
				private static final long serialVersionUID = 1L;
	
				@Override
				public void onSubmit()
				{
					setResponsePage(MessageFilterPanel.this.getPage(MessageFilterForm.this.getModelObject()));
				}
			});
			
			add(new Button("reset",new ResourceModel("cmd.reset"))
			{
				private static final long serialVersionUID = 1L;
	
				@Override
				public void onSubmit()
				{
					setResponsePage(getPage().getClass());
				}
			});

		}
	}
	
	public abstract BasePage getPage(MessageFilterFormModel filter);
	
	private Component getFeedbackComponent()
	{
		return this.get("feedback");
	}
	
	private Component getForm()
	{
		return this.get("form");
	}
	
	public static MessageFilterFormModel createMessageFilter()
	{
		return new MessageFilterFormModel();
	}

	public static class MessageFilterFormModel extends EbMSMessageFilter
	{
		private static final long serialVersionUID = 1L;
		private List<String> fromPartyIds = new ArrayList<String>();
		private List<String> fromRoles = new ArrayList<String>();
		private List<String> toPartyIds = new ArrayList<String>();
		private List<String> toRoles = new ArrayList<String>();
		private List<String> services = new ArrayList<String>();
		private List<String> actions = new ArrayList<String>();
		
		public List<String> getFromPartyIds()
		{
			return fromPartyIds;
		}
		public void resetFromPartyIds()
		{
			getFromPartyIds().clear();
			setFromRole(null);
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
			if (getFromRole() != null)
				getFromRole().setRole(null);
		}
		public void resetFromRoles(ArrayList<String> roleNames)
		{
			resetFromRoles();
			getFromRoles().addAll(roleNames);
		}
		public List<String> getToPartyIds()
		{
			return toPartyIds;
		}
		public void resetToPartyIds()
		{
			getToPartyIds().clear();
			setToRole(null);
		}
		public void resetToPartyIds(List<String> partyIds)
		{
			resetToPartyIds();
			getToPartyIds().addAll(partyIds);
		}
		public List<String> getToRoles()
		{
			return toRoles;
		}
		public void resetToRoles()
		{
			getToRoles().clear();
			if (getToRole() != null)
				getToRole().setRole(null);
		}
		public void resetToRoles(ArrayList<String> roleNames)
		{
			resetToRoles();
			getToRoles().addAll(roleNames);
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
	}
}
