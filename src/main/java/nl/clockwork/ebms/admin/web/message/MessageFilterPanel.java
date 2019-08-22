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
import nl.clockwork.ebms.common.JAXBParser;
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
		add(createClearLink("clear"));
	}

	private Link<Void> createClearLink(String id)
	{
		return new Link<Void>(id)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick()
			{
				setResponsePage(getPage().getClass());
			}
		};
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
			add(createCPAIdChoice("cpaId"));
			add(createFromPartyIdChoice("fromRole.partyId"));
			add(createFromRoleChoice("fromRole.role"));
			add(createToPartyIdChoice("toRole.partyId"));
			add(createToRoleChoice("toRole.role"));
			add(createServiceChoice("service"));
			add(createActionChoice("action"));
			add(new TextField<String>("conversationId").setLabel(new ResourceModel("lbl.conversationId")));
			add(new TextField<String>("messageId").setLabel(new ResourceModel("lbl.messageId")));
			add(new TextField<String>("refToMessageId").setLabel(new ResourceModel("lbl.refToMessageId")));
			add(createStatusesChoice("statuses"));
			from = new BootstrapDateTimePicker("from","dd-MM-yyyy",BootstrapDateTimePicker.Type.DATE);
			from.setLabel(new ResourceModel("lbl.from"));
			add(from);
			to = new BootstrapDateTimePicker("to","dd-MM-yyyy",BootstrapDateTimePicker.Type.DATE);
			to.setLabel(new ResourceModel("lbl.to"));
			add(to);
			add(createSearchButton("search"));
			add(createResetButton("reset"));
		}

		private DropDownChoice<String> createCPAIdChoice(String id)
		{
			DropDownChoice<String> result = new DropDownChoice<String>(id,Model.ofList(Utils.toList(cpaService.getCPAIds())));
			result.setLabel(new ResourceModel("lbl.cpaId"));
			result.add(new AjaxFormComponentUpdatingBehavior("change")
      {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					try
					{
						MessageFilterFormModel model = MessageFilterForm.this.getModelObject();
						CollaborationProtocolAgreement cpa = JAXBParser.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
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
			return result;
		}

		private DropDownChoice<String> createFromPartyIdChoice(String id)
		{
			DropDownChoice<String> result = new DropDownChoice<String>(id,new PropertyModel<List<String>>(this.getModelObject(),"fromPartyIds"))
			{
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isEnabled()
				{
					return MessageFilterForm.this.getModelObject().getToRole() == null;
				}
			};
			result.setLabel(new ResourceModel("lbl.fromPartyId"));
			result.setOutputMarkupId(true);
			result.add(new AjaxFormComponentUpdatingBehavior("change")
      {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					try
					{
						MessageFilterFormModel model = MessageFilterForm.this.getModelObject();
						CollaborationProtocolAgreement cpa = JAXBParser.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
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
			return result;
		}

		private DropDownChoice<String> createFromRoleChoice(String id)
		{
			DropDownChoice<String> result = new DropDownChoice<String>(id,new PropertyModel<List<String>>(this.getModelObject(),"fromRoles"))
			{
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isEnabled()
				{
					return MessageFilterForm.this.getModelObject().getToRole() == null;
				}
			};
			result.setLabel(new ResourceModel("lbl.fromRole"));
			result.setOutputMarkupId(true);
			result.add(new AjaxFormComponentUpdatingBehavior("change")
      {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					try
					{
						MessageFilterFormModel model = MessageFilterForm.this.getModelObject();
						CollaborationProtocolAgreement cpa = JAXBParser.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
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
			return result;
		}

		private DropDownChoice<String> createToPartyIdChoice(String id)
		{
			DropDownChoice<String> result = new DropDownChoice<String>(id,new PropertyModel<List<String>>(this.getModelObject(),"toPartyIds"))
			{
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isEnabled()
				{
					return MessageFilterForm.this.getModelObject().getFromRole() == null;
				}
			};
			result.setLabel(new ResourceModel("lbl.toPartyId"));
			result.setOutputMarkupId(true);
			result.add(new AjaxFormComponentUpdatingBehavior("change")
      {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					try
					{
						MessageFilterFormModel model = MessageFilterForm.this.getModelObject();
						CollaborationProtocolAgreement cpa = JAXBParser.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
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
			return result;
		}

		private DropDownChoice<String> createToRoleChoice(String id)
		{
			DropDownChoice<String> result = new DropDownChoice<String>(id,new PropertyModel<List<String>>(this.getModelObject(),"toRoles"))
			{
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isEnabled()
				{
					return MessageFilterForm.this.getModelObject().getFromRole() == null;
				}
			};
			result.setLabel(new ResourceModel("lbl.toRole"));
			result.setOutputMarkupId(true);
			result.add(new AjaxFormComponentUpdatingBehavior("change")
      {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					try
					{
						MessageFilterFormModel model = MessageFilterForm.this.getModelObject();
						CollaborationProtocolAgreement cpa = JAXBParser.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
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
			return result;
		}

		private DropDownChoice<String> createServiceChoice(String id)
		{
			DropDownChoice<String> result = new DropDownChoice<String>(id,new PropertyModel<List<String>>(this.getModelObject(),"services"));
			result.setLabel(new ResourceModel("lbl.service"));
			result.setOutputMarkupId(true);
			result.add(new AjaxFormComponentUpdatingBehavior("change")
      {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					try
					{
						MessageFilterFormModel model = MessageFilterForm.this.getModelObject();
						CollaborationProtocolAgreement cpa = JAXBParser.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
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
			return result;
		}

		private DropDownChoice<String> createActionChoice(String id)
		{
			DropDownChoice<String> result = new DropDownChoice<String>(id,new PropertyModel<List<String>>(this.getModelObject(),"actions"));
			result.setLabel(new ResourceModel("lbl.action"));
			result.setOutputMarkupId(true);
			return result;
		}

		private ListMultipleChoice<EbMSMessageStatus> createStatusesChoice(String id)
		{
			ListMultipleChoice<EbMSMessageStatus> result = new ListMultipleChoice<EbMSMessageStatus>(id,Model.ofList(Arrays.asList(EbMSMessageStatus.values())))
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected boolean localizeDisplayValues()
				{
					return true;
				}
			};
			result.setLabel(new ResourceModel("lbl.status"));
			result.setMaxRows(4);
			return result;
		}

		private Button createSearchButton(String id)
		{
			return new Button(id,new ResourceModel("cmd.search"))
			{
				private static final long serialVersionUID = 1L;
	
				@Override
				public void onSubmit()
				{
					setResponsePage(MessageFilterPanel.this.getPage(MessageFilterForm.this.getModelObject()));
				}
			};
		}

		private Button createResetButton(String id)
		{
			return new Button(id,new ResourceModel("cmd.reset"))
			{
				private static final long serialVersionUID = 1L;
	
				@Override
				public void onSubmit()
				{
					setResponsePage(getPage().getClass());
				}
			};
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
