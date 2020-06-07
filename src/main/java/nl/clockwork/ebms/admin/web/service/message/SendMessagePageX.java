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

import org.apache.commons.collections4.ListUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.oasis_open.committees.ebxml_cppa.schema.cpp_cpa_2_0.CollaborationProtocolAgreement;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.val;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nl.clockwork.ebms.admin.CPAUtils;
import nl.clockwork.ebms.admin.Utils;
import nl.clockwork.ebms.admin.web.Action;
import nl.clockwork.ebms.admin.web.AjaxFormComponentUpdatingBehavior;
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.BootstrapFeedbackPanel;
import nl.clockwork.ebms.admin.web.BootstrapFormComponentFeedbackBorder;
import nl.clockwork.ebms.admin.web.Button;
import nl.clockwork.ebms.admin.web.Consumer;
import nl.clockwork.ebms.admin.web.DropDownChoice;
import nl.clockwork.ebms.admin.web.MessageProvider;
import nl.clockwork.ebms.admin.web.ResetButton;
import nl.clockwork.ebms.admin.web.Supplier;
import nl.clockwork.ebms.admin.web.WebMarkupContainer;
import nl.clockwork.ebms.admin.web.WicketApplication;
import nl.clockwork.ebms.jaxb.JAXBParser;
import nl.clockwork.ebms.service.EbMSMessageService;
import nl.clockwork.ebms.service.cpa.CPAService;
import nl.clockwork.ebms.service.model.EbMSMessageContent;
import nl.clockwork.ebms.service.model.EbMSMessageContext;
import nl.clockwork.ebms.service.model.Party;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SendMessagePageX extends BasePage
{
	private static final long serialVersionUID = 1L;
	@SpringBean(name="cpaService")
	CPAService cpaService;
	@SpringBean(name="ebMSMessageService")
	EbMSMessageService ebMSMessageService;

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

	@FieldDefaults(level = AccessLevel.PRIVATE)
	public class MessageForm extends Form<EbMSMessageContextData>
	{
		private static final long serialVersionUID = 1L;
		DataSourcesPanel dataSources;

		public MessageForm(String id)
		{
			super(id,new CompoundPropertyModel<>(new EbMSMessageContextData()));
			setMultiPart(true);
			add(new BootstrapFormComponentFeedbackBorder("cpaIdFeedback",createCPAIdChoice("cpaId")));
			add(new BootstrapFormComponentFeedbackBorder("fromPartyIdFeedback",createFromPartyIdChoice("fromParty.partyId")));
			add(new BootstrapFormComponentFeedbackBorder("fromRoleFeedback",createFromRoleChoice("fromParty.role")));
			add(new BootstrapFormComponentFeedbackBorder("toPartyIdFeedback",createToPartyIdChoice("toParty.partyId")));
			add(new BootstrapFormComponentFeedbackBorder("toRoleFeedback",createToRoleChoice("toParty.role")));
			add(new BootstrapFormComponentFeedbackBorder("serviceFeedback",createServiceChoice("service")));
			add(new BootstrapFormComponentFeedbackBorder("actionFeedback",createActionChoice("action")));
			add(new TextField<String>("conversationId").setLabel(new ResourceModel("lbl.conversationId")));
			add(new TextField<String>("messageId").setLabel(new ResourceModel("lbl.messageId")));
			add(new TextField<String>("refToMessageId").setLabel(new ResourceModel("lbl.refToMessageId")));
			val rawInputContainer = createRawInputContainer();
			add(rawInputContainer);
			rawInputContainer.add(createRawInputCheckBox("rawInput"));
			add(dataSources = new EmptyDataSourcesPanel("dataSources"));
			val send = createSendButton("send");
			setDefaultButton(send);
			add(send);
			add(new ResetButton("reset",new ResourceModel("cmd.reset"),SendMessagePageX.class));
		}

		private DropDownChoice<String> createCPAIdChoice(String id)
		{
			val result = new DropDownChoice<String>(id,Model.ofList(Utils.toList(cpaService.getCPAIds())));
			result.setLabel(new ResourceModel("lbl.cpaId"));
			result.setRequired(true);
			Consumer<AjaxRequestTarget> onUpdate = t ->
			{
				try
				{
					val o = getModelObject();
					val cpa = JAXBParser.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(o.getCpaId()));
					o.resetFromPartyIds(CPAUtils.getPartyIds(cpa));
					o.resetFromRoles();
					o.resetToPartyIds();
					o.resetToRoles();
					o.resetServices();
					o.resetActions();
					dataSources.replaceWith(dataSources = new EmptyDataSourcesPanel(dataSources.getId()));
				}
				catch (JAXBException e)
				{
					log.error("",e);
					error(e.getMessage());
				}
				t.add(getPage().get("feedback"));
				t.add(getPage().get("form"));
			};
			result.add(new AjaxFormComponentUpdatingBehavior("change",onUpdate));
			return result;
		}

		private DropDownChoice<String> createFromPartyIdChoice(String id)
		{
			val result = new DropDownChoice<String>(id,new PropertyModel<List<String>>(getModel(),"fromPartyIds"));
			result.setLabel(new ResourceModel("lbl.fromPartyId"));
			result.setRequired(false).setOutputMarkupId(true);
			Consumer<AjaxRequestTarget> onUpdate = t ->
			{
				try
				{
					val o = getModelObject();
					val cpa = JAXBParser.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(o.getCpaId()));
					o.resetFromRoles(CPAUtils.getRoleNames(cpa,o.getFromParty().getPartyId()));
					o.resetToPartyIds(CPAUtils.getOtherPartyIds(cpa,o.getFromParty().getPartyId()));
					o.resetToRoles(CPAUtils.getOtherRoleNamesByPartyId(cpa,o.getFromParty().getPartyId()));
					o.resetServices(ListUtils.intersection(CPAUtils.getServiceNamesCanSend(cpa,o.getFromParty().getPartyId(),o.getFromParty().getRole()),CPAUtils.getServiceNamesCanReceive(cpa,o.getToParty().getPartyId(),o.getToParty().getRole())));
					o.resetActions();
					dataSources.replaceWith(dataSources = new EmptyDataSourcesPanel(dataSources.getId()));
				}
				catch (JAXBException e)
				{
					log.error("",e);
					error(e.getMessage());
				}
				t.add(getPage().get("feedback"));
				t.add(getPage().get("form"));
			};
			result.add(new AjaxFormComponentUpdatingBehavior("change",onUpdate));
			return result;
		}

		private DropDownChoice<String> createFromRoleChoice(String id)
		{
			val result = new DropDownChoice<String>(id,new PropertyModel<List<String>>(getModel(),"fromRoles"));
			result.setLabel(new ResourceModel("lbl.fromRole"));
			result.setRequired(true).setOutputMarkupId(true);
			Consumer<AjaxRequestTarget> onUpdate = t ->
			{
				try
				{
					val o = getModelObject();
					val cpa = JAXBParser.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(o.getCpaId()));
					o.resetServices(ListUtils.intersection(CPAUtils.getServiceNamesCanSend(cpa,o.getFromParty().getPartyId(),o.getFromParty().getRole()),CPAUtils.getServiceNamesCanReceive(cpa,o.getToParty().getPartyId(),o.getToParty().getRole())));
					o.resetActions();
					dataSources.replaceWith(dataSources = new EmptyDataSourcesPanel(dataSources.getId()));
				}
				catch (JAXBException e)
				{
					log.error("",e);
					error(e.getMessage());
				}
				t.add(getPage().get("feedback"));
				t.add(getPage().get("form"));
			};
			result.add(new AjaxFormComponentUpdatingBehavior("change",onUpdate));
			return result;
		}

		private DropDownChoice<String> createToPartyIdChoice(String id)
		{
			val result = new DropDownChoice<String>(id,new PropertyModel<List<String>>(getModel(),"toPartyIds"));
			result.setLabel(new ResourceModel("lbl.toPartyId"));
			result.setRequired(true).setOutputMarkupId(true);
			Consumer<AjaxRequestTarget> onUpdate = t ->
			{
				try
				{
					val o = getModelObject();
					val cpa = JAXBParser.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(o.getCpaId()));
					o.resetToRoles(CPAUtils.getRoleNames(cpa,o.getToParty().getPartyId()));
					o.resetServices(ListUtils.intersection(CPAUtils.getServiceNamesCanSend(cpa,o.getFromParty().getPartyId(),o.getFromParty().getRole()),CPAUtils.getServiceNamesCanReceive(cpa,o.getToParty().getPartyId(),o.getToParty().getRole())));
					o.resetActions();
					dataSources.replaceWith(dataSources = new EmptyDataSourcesPanel(dataSources.getId()));
				}
				catch (JAXBException e)
				{
					log.error("",e);
					error(e.getMessage());
				}
				t.add(getPage().get("feedback"));
				t.add(getPage().get("form"));
			};
			result.add(new AjaxFormComponentUpdatingBehavior("change",onUpdate));
			return result;
		}

		private DropDownChoice<String> createToRoleChoice(String id)
		{
			val result = new DropDownChoice<String>(id,new PropertyModel<List<String>>(getModel(),"toRoles"));
			result.setLabel(new ResourceModel("lbl.toRole"));
			result.setRequired(true).setOutputMarkupId(true);
			Consumer<AjaxRequestTarget> onUpdate = t ->
			{
				try
				{
					val o = getModelObject();
					val cpa = JAXBParser.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(o.getCpaId()));
					o.resetServices(ListUtils.intersection(CPAUtils.getServiceNamesCanSend(cpa,o.getFromParty().getPartyId(),o.getFromParty().getRole()),CPAUtils.getServiceNamesCanReceive(cpa,o.getToParty().getPartyId(),o.getToParty().getRole())));
					o.resetActions();
					dataSources.replaceWith(dataSources = new EmptyDataSourcesPanel(dataSources.getId()));
				}
				catch (JAXBException e)
				{
					log.error("",e);
					error(e.getMessage());
				}
				t.add(getPage().get("feedback"));
				t.add(getPage().get("form"));
			};
			result.add(new AjaxFormComponentUpdatingBehavior("change",onUpdate));
			return result;
		}

		private DropDownChoice<String> createServiceChoice(String id)
		{
			val result = new DropDownChoice<String>(id,new PropertyModel<List<String>>(getModel(),"services"));
			result.setLabel(new ResourceModel("lbl.service"));
			result.setRequired(true);
			result.setOutputMarkupId(true);
			Consumer<AjaxRequestTarget> onUpdate = t ->
			{
				try
				{
					val o = getModelObject();
					val cpa = JAXBParser.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(o.getCpaId()));
					o.resetActions(ListUtils.intersection(CPAUtils.getFromActionNamesCanSend(cpa,o.getFromParty().getPartyId(),o.getFromParty().getRole(),o.getService()),CPAUtils.getFromActionNamesCanReceive(cpa,o.getToParty().getPartyId(),o.getToParty().getRole(),o.getService())));
					dataSources.replaceWith(dataSources = new EmptyDataSourcesPanel(dataSources.getId()));
				}
				catch (JAXBException e)
				{
					log.error("",e);
					error(e.getMessage());
				}
				t.add(getPage().get("feedback"));
				t.add(getPage().get("form"));
			};
			result.add(new AjaxFormComponentUpdatingBehavior("change",onUpdate));
			return result;
		}

		private DropDownChoice<String> createActionChoice(String id)
		{
			val actions = new DropDownChoice<String>(id,new PropertyModel<List<String>>(getModel(),"actions"));
			actions.setLabel(new ResourceModel("lbl.action"));
			actions.setRequired(true);
			actions.setOutputMarkupId(true);
			Consumer<AjaxRequestTarget> onUpdate = t->
			{
				val o = getModelObject();
				if (WicketApplication.get().getMessageEditPanels().containsKey(MessageProvider.createId(o.getService(),o.getAction())))
					dataSources.replaceWith(dataSources = WicketApplication.get().getMessageEditPanels().get(MessageProvider.createId(o.getService(),o.getAction())).getPanel(dataSources.getId()));
				else
					dataSources.replaceWith(dataSources = new DefaultDataSourcesPanel(dataSources.getId()));
				o.setRawInput(false);
				t.add(getPage().get("feedback"));
				t.add(getPage().get("form"));
			};
			actions.add(new AjaxFormComponentUpdatingBehavior("change",onUpdate));
			return actions;
		}

		private WebMarkupContainer createRawInputContainer()
		{
			Supplier<Boolean> isVisible = () ->
			{
				val o = getModelObject();
				return o.getAction() != null && (WicketApplication.get().getMessageEditPanels().containsKey(MessageProvider.createId(o.getService(),o.getAction()))) || (dataSources != null && !(dataSources instanceof EmptyDataSourcesPanel || dataSources instanceof DefaultDataSourcesPanel));
			};
			return new WebMarkupContainer("rawInputContainer",isVisible);
		}

		private CheckBox createRawInputCheckBox(String id)
		{
			val result = new CheckBox(id);
			result.setLabel(new ResourceModel("lbl.rawInput"));
			Consumer<AjaxRequestTarget> onUpdate = t ->
			{
				val o = getModelObject();
				if (o.isRawInput())
					dataSources.replaceWith(dataSources = new DefaultDataSourcesPanel(dataSources.getId()));
				else
					dataSources.replaceWith(dataSources = WicketApplication.get().getMessageEditPanels().get(MessageProvider.createId(o.getService(),o.getAction())).getPanel(dataSources.getId()));
				t.add(getPage().get("feedback"));
				t.add(getPage().get("form"));
			};
			result.add(new AjaxFormComponentUpdatingBehavior("change",onUpdate));
			return result;
		}

		private Button createSendButton(String id)
		{
			Action onSubmit = () ->
			{
				try
				{
					val o = getModelObject();
					val messageContent = new EbMSMessageContent(o,dataSources.getDataSources());
					val messageId = ebMSMessageService.sendMessage(messageContent);
					info(new StringResourceModel("sendMessage.ok",Model.of(messageId)).getString());
				}
				catch (Exception e)
				{
					log.error("",e);
					error(e.getMessage());
				}
			};
			return new Button(id,new ResourceModel("cmd.send"),onSubmit);
		}
	}

	@Data
	@FieldDefaults(level = AccessLevel.PRIVATE)
	@EqualsAndHashCode(callSuper = true)
	public class EbMSMessageContextData extends EbMSMessageContext
	{
		private static final long serialVersionUID = 1L;
		final List<String> fromPartyIds = new ArrayList<>();
		final List<String> fromRoles = new ArrayList<>();
		final List<String> toPartyIds = new ArrayList<>();
		final List<String> toRoles = new ArrayList<>();
		final List<String> services = new ArrayList<>();
		final List<String> actions = new ArrayList<>();
		boolean rawInput;

		public EbMSMessageContextData()
		{
			setFromParty(new Party());
			setToParty(new Party());
		}
		public void resetFromPartyIds()
		{
			getFromPartyIds().clear();
			getFromParty().setPartyId(null);
		}
		public void resetFromPartyIds(List<String> partyIds)
		{
			resetFromPartyIds();
			getFromPartyIds().addAll(partyIds);
		}
		public void resetFromRoles()
		{
			getFromRoles().clear();
			getFromParty().setRole(null);
		}
		public void resetFromRoles(List<String> roles)
		{
			resetFromRoles();
			getFromRoles().addAll(roles);
			getFromParty().setRole(getFromRoles().size() == 1 ? getFromRoles().get(0) : null);
		}
		public void resetToPartyIds()
		{
			getToPartyIds().clear();
			getToParty().setPartyId(null);
		}
		public void resetToPartyIds(List<String> partyIds)
		{
			resetToPartyIds();
			getToPartyIds().addAll(partyIds);
			getToParty().setPartyId(getFromParty().getPartyId() != null && getToPartyIds().size() == 1 ? getToPartyIds().get(0) : null);
		}
		public void resetToRoles()
		{
			getToRoles().clear();
			getToParty().setRole(null);
		}
		public void resetToRoles(List<String> roles)
		{
			resetToRoles();
			getToRoles().addAll(roles);
			getToParty().setRole(getToRoles().size() == 1 ? getToRoles().get(0) : null);
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
