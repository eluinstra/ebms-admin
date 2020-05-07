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

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.oasis_open.committees.ebxml_cppa.schema.cpp_cpa_2_0.CollaborationProtocolAgreement;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;
import lombok.extern.apachecommons.CommonsLog;
import nl.clockwork.ebms.EbMSMessageStatus;
import nl.clockwork.ebms.admin.CPAUtils;
import nl.clockwork.ebms.admin.Utils;
import nl.clockwork.ebms.admin.web.AjaxFormComponentUpdatingBehavior;
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.BootstrapDateTimePicker;
import nl.clockwork.ebms.admin.web.BootstrapFeedbackPanel;
import nl.clockwork.ebms.admin.web.Button;
import nl.clockwork.ebms.admin.web.Consumer;
import nl.clockwork.ebms.admin.web.DropDownChoice;
import nl.clockwork.ebms.admin.web.Function;
import nl.clockwork.ebms.admin.web.Link;
import nl.clockwork.ebms.common.JAXBParser;
import nl.clockwork.ebms.service.CPAService;
import nl.clockwork.ebms.service.EbMSMessageService;

@CommonsLog
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageFilterPanel extends Panel
{
	private static final long serialVersionUID = 1L;
	@SpringBean(name="cpaService")
	CPAService cpaService;
	@SpringBean(name="ebMSMessageService")
	EbMSMessageService ebMSMessageService;
	@NonNull
	final Function<MessageFilterFormModel,BasePage> getPage;
	BootstrapDateTimePicker from;
	BootstrapDateTimePicker to;

	@Builder
	public MessageFilterPanel(String id, MessageFilterFormModel filter, @NonNull Function<MessageFilterFormModel,BasePage> getPage)
	{
		super(id);
		this.getPage = getPage;
		add(new BootstrapFeedbackPanel("feedback").setOutputMarkupId(true));
		add(new MessageFilterForm("form",filter));
		add(createClearLink("clear"));
	}

	private Link<Void> createClearLink(String id)
	{
		return new Link<Void>(id,() -> setResponsePage(getPage().getClass()));
	}
	
	@Override
	public void renderHead(IHeaderResponse response)
	{
		response.render(OnDomReadyHeaderItem.forScript(BootstrapDateTimePicker.getLinkBootstrapDateTimePickersJavaScript(from,to)));
		super.renderHead(response);
	}

	public BasePage getPage(MessageFilterFormModel filter)
	{
		return getPage.apply(filter);
	}

	@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
	private static class ListMultipleChoice<T> extends org.apache.wicket.markup.html.form.ListMultipleChoice<T>
	{
		private static final long serialVersionUID = 1L;
		boolean localizeDisplayValues;

		@Builder
		public ListMultipleChoice(String id, IModel<? extends List<? extends T>> choices, boolean localizeDisplayValues)
		{
			super(id,choices);
			this.localizeDisplayValues = localizeDisplayValues;
		}
		
		@Override
		protected boolean localizeDisplayValues()
		{
			return localizeDisplayValues;
		}
	}

	public class MessageFilterForm extends Form<MessageFilterFormModel>
	{
		private static final long serialVersionUID = 1L;

		public MessageFilterForm(String id, MessageFilterFormModel model)
		{
			super(id,new CompoundPropertyModel<>(model));
			add(createCPAIdChoice("cpaId"));
			add(createFromPartyIdChoice("fromParty.partyId"));
			add(createFromRoleChoice("fromParty.role"));
			add(createToPartyIdChoice("toParty.partyId"));
			add(createToRoleChoice("toParty.role"));
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
			val result = new DropDownChoice<String>(id,Model.ofList(Utils.toList(cpaService.getCPAIds())));
			result.setLabel(new ResourceModel("lbl.cpaId"));
			Consumer<AjaxRequestTarget> onUpdate = t ->
			{
				try
				{
					val model = MessageFilterForm.this.getModelObject();
					val cpa = JAXBParser.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
					model.resetFromPartyIds(CPAUtils.getPartyIds(cpa));
					model.resetFromRoles();
					model.resetToPartyIds(CPAUtils.getPartyIds(cpa));
					model.resetToRoles();
					model.resetServices();
					model.resetActions();
					t.add(getFeedbackComponent());
					t.add(getForm());
				}
				catch (JAXBException e)
				{
					log.error("",e);
					error(e.getMessage());
				}
			};
			result.add(new AjaxFormComponentUpdatingBehavior("change",onUpdate));
			return result;
		}

		private DropDownChoice<String> createFromPartyIdChoice(String id)
		{
			val result = DropDownChoice.<String>builder()
					.id(id)
					.choices(new PropertyModel<List<String>>(this.getModelObject(),"fromPartyIds"))
					.isEnabled(() -> MessageFilterForm.this.getModelObject().getToParty() == null)
					.build();
			result.setLabel(new ResourceModel("lbl.fromPartyId"));
			result.setOutputMarkupId(true);
			Consumer<AjaxRequestTarget> onUpdate = t ->
			{
				try
				{
					val model = MessageFilterForm.this.getModelObject();
					val cpa = JAXBParser.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
					model.resetFromRoles(CPAUtils.getRoleNames(cpa,model.getFromParty().getPartyId()));
					model.resetServices();
					model.resetActions();
					t.add(getFeedbackComponent());
					t.add(getForm());
				}
				catch (JAXBException e)
				{
					log.error("",e);
					error(e.getMessage());
				}
			};
			result.add(new AjaxFormComponentUpdatingBehavior("change",onUpdate));
			return result;
		}

		private DropDownChoice<String> createFromRoleChoice(String id)
		{
			val result = DropDownChoice.<String>builder()
					.id(id)
					.choices(new PropertyModel<List<String>>(this.getModelObject(),"fromRoles"))
					.isEnabled(() -> MessageFilterForm.this.getModelObject().getToParty() == null)
					.build();
			result.setLabel(new ResourceModel("lbl.fromRole"));
			result.setOutputMarkupId(true);
			Consumer<AjaxRequestTarget> onUpdate = t ->
			{
				try
				{
					val model = MessageFilterForm.this.getModelObject();
					val cpa = JAXBParser.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
					model.resetServices(CPAUtils.getServiceNames(cpa,model.getFromParty().getRole()));
					model.resetActions();
					t.add(getFeedbackComponent());
					t.add(getForm());
				}
				catch (JAXBException e)
				{
					log.error("",e);
					error(e.getMessage());
				}
			};
			result.add(new AjaxFormComponentUpdatingBehavior("change",onUpdate));
			return result;
		}

		private DropDownChoice<String> createToPartyIdChoice(String id)
		{
			val result = DropDownChoice.<String>builder()
					.id(id)
					.choices(new PropertyModel<List<String>>(this.getModelObject(),"toPartyIds"))
					.isEnabled(() -> MessageFilterForm.this.getModelObject().getFromParty() == null)
					.build();
			result.setLabel(new ResourceModel("lbl.toPartyId"));
			result.setOutputMarkupId(true);
			Consumer<AjaxRequestTarget> onUpdate = t ->
			{
				try
				{
					val model = MessageFilterForm.this.getModelObject();
					val cpa = JAXBParser.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
					model.resetToRoles(CPAUtils.getRoleNames(cpa,model.getToParty().getPartyId()));
					model.resetServices();
					model.resetActions();
					t.add(getFeedbackComponent());
					t.add(getForm());
				}
				catch (JAXBException e)
				{
					log.error("",e);
					error(e.getMessage());
				}
			};
			result.add(new AjaxFormComponentUpdatingBehavior("change",onUpdate));
			return result;
		}

		private DropDownChoice<String> createToRoleChoice(String id)
		{
			val result = DropDownChoice.<String>builder()
					.id(id)
					.choices(new PropertyModel<List<String>>(this.getModelObject(),"toRoles"))
					.isEnabled(() -> MessageFilterForm.this.getModelObject().getFromParty() == null)
					.build();
			result.setLabel(new ResourceModel("lbl.toRole"));
			result.setOutputMarkupId(true);
			Consumer<AjaxRequestTarget> onUpdate = t ->
			{
				try
				{
					val model = MessageFilterForm.this.getModelObject();
					val cpa = JAXBParser.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
					model.resetServices(CPAUtils.getServiceNames(cpa,model.getToParty().getRole()));
					model.resetActions();
					t.add(getFeedbackComponent());
					t.add(getForm());
				}
				catch (JAXBException e)
				{
					log.error("",e);
					error(e.getMessage());
				}
			};
			result.add(new AjaxFormComponentUpdatingBehavior("change",onUpdate));
			return result;
		}

		private DropDownChoice<String> createServiceChoice(String id)
		{
			val result = new DropDownChoice<>(id,new PropertyModel<List<String>>(this.getModelObject(),"services"));
			result.setLabel(new ResourceModel("lbl.service"));
			result.setOutputMarkupId(true);
			Consumer<AjaxRequestTarget> onUpdate = t ->
			{
				try
				{
					val model = MessageFilterForm.this.getModelObject();
					val cpa = JAXBParser.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
					model.resetActions(model.getFromParty() == null ? CPAUtils.getToActionNames(cpa,model.getToParty().getRole(),model.getService()) : CPAUtils.getFromActionNames(cpa,model.getFromParty().getRole(),model.getService()));
					t.add(getFeedbackComponent());
					t.add(getForm());
				}
				catch (JAXBException e)
				{
					log.error("",e);
					error(e.getMessage());
				}
			};
			result.add(new AjaxFormComponentUpdatingBehavior("change",onUpdate));
			return result;
		}

		private DropDownChoice<String> createActionChoice(String id)
		{
			val result = new DropDownChoice<String>(id,new PropertyModel<List<String>>(this.getModelObject(),"actions"));
			result.setLabel(new ResourceModel("lbl.action"));
			result.setOutputMarkupId(true);
			return result;
		}

		private ListMultipleChoice<EbMSMessageStatus> createStatusesChoice(String id)
		{
			val result = ListMultipleChoice.<EbMSMessageStatus>builder()
					.id(id)
					.choices(Model.ofList(Arrays.asList(EbMSMessageStatus.values())))
					.localizeDisplayValues(true)
					.build();
			result.setLabel(new ResourceModel("lbl.status"));
			result.setMaxRows(4);
			return result;
		}

		private Button createSearchButton(String id)
		{
			return Button.builder()
					.id(id)
					.model(new ResourceModel("cmd.search"))
					.onSubmit(() -> setResponsePage(MessageFilterPanel.this.getPage(MessageFilterForm.this.getModelObject())))
					.build();
		}

		private Button createResetButton(String id)
		{
			return Button.builder()
					.id(id)
					.model(new ResourceModel("cmd.reset"))
					.onSubmit(() -> setResponsePage(getPage().getClass()))
					.build();
		}

	}
	
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

	@Data
	@FieldDefaults(level = AccessLevel.PRIVATE)
	@NoArgsConstructor
	@EqualsAndHashCode(callSuper = true)
	public static class MessageFilterFormModel extends EbMSMessageFilter
	{
		private static final long serialVersionUID = 1L;
		final List<String> fromPartyIds = new ArrayList<>();
		final List<String> fromRoles = new ArrayList<>();
		final List<String> toPartyIds = new ArrayList<>();
		final List<String> toRoles = new ArrayList<>();
		final List<String> services = new ArrayList<>();
		final List<String> actions = new ArrayList<>();
		
		public void resetFromPartyIds()
		{
			getFromPartyIds().clear();
			setFromParty(null);
		}
		public void resetFromPartyIds(List<String> partyIds)
		{
			resetFromPartyIds();
			getFromPartyIds().addAll(partyIds);
		}
		public void resetFromRoles()
		{
			getFromRoles().clear();
			if (getFromParty() != null)
				getFromParty().setRole(null);
		}
		public void resetFromRoles(List<String> roleNames)
		{
			resetFromRoles();
			getFromRoles().addAll(roleNames);
		}
		public void resetToPartyIds()
		{
			getToPartyIds().clear();
			setToParty(null);
		}
		public void resetToPartyIds(List<String> partyIds)
		{
			resetToPartyIds();
			getToPartyIds().addAll(partyIds);
		}
		public void resetToRoles()
		{
			getToRoles().clear();
			if (getToParty() != null)
				getToParty().setRole(null);
		}
		public void resetToRoles(List<String> roleNames)
		{
			resetToRoles();
			getToRoles().addAll(roleNames);
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
