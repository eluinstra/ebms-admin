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

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
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

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nl.clockwork.ebms.admin.CPAUtils;
import nl.clockwork.ebms.admin.Utils;
import nl.clockwork.ebms.admin.web.Action;
import nl.clockwork.ebms.admin.web.AjaxButton;
import nl.clockwork.ebms.admin.web.AjaxFormComponentUpdatingBehavior;
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.BootstrapFeedbackPanel;
import nl.clockwork.ebms.admin.web.BootstrapFormComponentFeedbackBorder;
import nl.clockwork.ebms.admin.web.Button;
import nl.clockwork.ebms.admin.web.Consumer;
import nl.clockwork.ebms.admin.web.DropDownChoice;
import nl.clockwork.ebms.admin.web.ResetButton;
import nl.clockwork.ebms.jaxb.JAXBParser;
import nl.clockwork.ebms.service.EbMSMessageService;
import nl.clockwork.ebms.service.cpa.CPAService;
import nl.clockwork.ebms.service.model.DataSource;
import nl.clockwork.ebms.service.model.MessageRequest;
import nl.clockwork.ebms.service.model.MessageRequestProperties;
import nl.clockwork.ebms.service.model.Party;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SendMessagePage extends BasePage
{
	private static final long serialVersionUID = 1L;
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

	public class MessageForm extends Form<EbMSMessagePropertiesData>
	{
		private static final long serialVersionUID = 1L;

		public MessageForm(String id)
		{
			super(id,new CompoundPropertyModel<>(new EbMSMessagePropertiesData()));
			setMultiPart(true);
			add(new BootstrapFormComponentFeedbackBorder("cpaIdFeedback",createCPAIdChoice("cpaId")));
			add(new BootstrapFormComponentFeedbackBorder("fromPartyIdFeedback",createFromPartyIdChoice("fromParty.partyId")));
			add(new BootstrapFormComponentFeedbackBorder("fromRoleFeedback",createFromRoleChoice("fromParty.role")));
			add(new BootstrapFormComponentFeedbackBorder("serviceFeedback",createServiceChoice("service")));
			add(new BootstrapFormComponentFeedbackBorder("actionFeedback",createActionChoice("action")));
			add(new TextField<String>("conversationId").setLabel(new ResourceModel("lbl.conversationId")));
			add(new TextField<String>("messageId").setLabel(new ResourceModel("lbl.messageId")));
			add(new TextField<String>("refToMessageId").setLabel(new ResourceModel("lbl.refToMessageId")));
			add(new DataSourcesForm("form",Model.ofList(getModelObject().getDataSources())));
			val send = createSendButton("send");
			setDefaultButton(send);
			add(send);
			add(new ResetButton("reset",new ResourceModel("cmd.reset"),SendMessagePage.class));
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
					o.resetFromRoles(CPAUtils.getRoleNames(cpa));
					o.resetServices();
					o.resetActions();
					o.resetDataSources();
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
			val result = new DropDownChoice<>(id,new PropertyModel<List<String>>(getModel(),"fromPartyIds"));
			result.setLabel(new ResourceModel("lbl.fromPartyId"));
			result.setRequired(false).setOutputMarkupId(true);
			Consumer<AjaxRequestTarget> onUpdate = t ->
			{
				try
				{
					val o = getModelObject();
					val cpa = JAXBParser.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(o.getCpaId()));
					o.resetFromRoles(CPAUtils.getRoleNames(cpa,o.getFromParty().getPartyId()));
					o.resetServices(CPAUtils.getServiceNames(cpa,o.getFromParty().getRole()));
					o.resetActions();
					o.resetDataSources();
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
			val result = new DropDownChoice<>(id,new PropertyModel<List<String>>(getModel(),"fromRoles"));
			result.setLabel(new ResourceModel("lbl.fromRole"));
			result.setRequired(true).setOutputMarkupId(true);
			Consumer<AjaxRequestTarget> onUpdate = t ->
			{
				try
				{
					val o = getModelObject();
					val cpa = JAXBParser.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(o.getCpaId()));
					if (o.getFromParty().getPartyId() == null)
						o.resetFromPartyIds(CPAUtils.getPartyIdsByRoleName(cpa,o.getFromParty().getRole()));
					o.resetServices(CPAUtils.getServiceNames(cpa,o.getFromParty().getRole()));
					o.resetActions();
					o.resetDataSources();
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
			val result = new DropDownChoice<>(id,new PropertyModel<List<String>>(getModel(),"services"));
			result.setLabel(new ResourceModel("lbl.service"));
			result.setRequired(true);
			result.setOutputMarkupId(true);
			Consumer<AjaxRequestTarget> onUpdate = t ->
			{
				try
				{
					val o = getModelObject();
					val cpa = JAXBParser.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(o.getCpaId()));
					o.resetActions(CPAUtils.getFromActionNames(cpa,o.getFromParty().getRole(),o.getService()));
					o.resetDataSources();
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
			val result = new DropDownChoice<String>(id,new PropertyModel<List<String>>(getModel(),"actions"));
			result.setLabel(new ResourceModel("lbl.action"));
			result.setRequired(true);
			result.setOutputMarkupId(true);
			return result;
		}

		private Button createSendButton(String id)
		{
			Action onSubmit = () ->
			{
				try
				{
					val o = getModelObject();
					val message = new MessageRequest(o,o.getDataSources());
					val messageId = ebMSMessageService.sendMessage(message);
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
	public class EbMSMessagePropertiesData extends MessageRequestProperties
	{
		private static final long serialVersionUID = 1L;
		final List<String> fromPartyIds = new ArrayList<>();
		final List<String> fromRoles = new ArrayList<>();
		final List<String> services = new ArrayList<>();
		final List<String> actions = new ArrayList<>();
		final List<DataSource> dataSources = new ArrayList<>();

		public EbMSMessagePropertiesData()
		{
			setFromParty(new Party());
		}
		public void resetFromPartyIds()
		{
			getFromRoles().clear();
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
		public void resetDataSources()
		{
			getDataSources().clear();
		}
	}

	@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
	public static class EbMSDataSourceListView extends ListView<DataSource>
	{
		private static final long serialVersionUID = 1L;
		@NonNull
		DataSourcesForm dataSourcesForm;

		public EbMSDataSourceListView(String id, List<DataSource> dataSources, @NonNull DataSourcesForm dataSourcesForm)
		{
			super(id,dataSources);
			this.dataSourcesForm = dataSourcesForm;
		}

		@Override
		protected void populateItem(final ListItem<DataSource> item)
		{
			item.setModel(new CompoundPropertyModel<>(item.getModel()));
			item.add(new Label("name"));
			item.add(new Label("contentType"));
			Consumer<AjaxRequestTarget> onSubmit = t ->
			{
				dataSourcesForm.getModelObject().remove(item.getModelObject());
				t.add(dataSourcesForm);
			};
			item.add(AjaxButton.builder()
					.id("remove")
					.model(new ResourceModel("cmd.remove"))
					.form(dataSourcesForm)
					.onSubmit(onSubmit)
					.build());
		}
	}

	public class DataSourcesForm extends Form<List<DataSource>>
	{
		private static final long serialVersionUID = 1L;

		public DataSourcesForm(String id, IModel<List<DataSource>> model)
		{
			super(id,model);
			val dataSources_ = new EbMSDataSourceListView("dataSources",model.getObject(),this);
			dataSources_.setOutputMarkupId(true);
			add(dataSources_);
			val dataSourceModalWindow = new DataSourceModalWindow("dataSourceModelWindow",model.getObject(),this);
			add(dataSourceModalWindow);
			val add = AjaxButton.builder()
					.id("add")
					.onSubmit(t -> dataSourceModalWindow.show(t))
					.build();
			add(add);
		}
	}
}
