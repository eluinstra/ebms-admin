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
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.io.IClusterable;
import org.oasis_open.committees.ebxml_cppa.schema.cpp_cpa_2_0.CollaborationProtocolAgreement;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;
import lombok.experimental.FieldDefaults;
import lombok.extern.apachecommons.CommonsLog;
import nl.clockwork.ebms.EbMSMessageStatus;
import nl.clockwork.ebms.admin.CPAUtils;
import nl.clockwork.ebms.admin.Utils;
import nl.clockwork.ebms.admin.dao.EbMSDAO;
import nl.clockwork.ebms.admin.web.Action;
import nl.clockwork.ebms.admin.web.AjaxFormComponentUpdatingBehavior;
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.BootstrapFeedbackPanel;
import nl.clockwork.ebms.admin.web.BootstrapFormComponentFeedbackBorder;
import nl.clockwork.ebms.admin.web.Button;
import nl.clockwork.ebms.admin.web.Consumer;
import nl.clockwork.ebms.admin.web.DropDownChoice;
import nl.clockwork.ebms.admin.web.ResetButton;
import nl.clockwork.ebms.admin.web.TextField;
import nl.clockwork.ebms.common.JAXBParser;
import nl.clockwork.ebms.service.CPAService;
import nl.clockwork.ebms.service.EbMSMessageService;
import nl.clockwork.ebms.service.model.Party;

@CommonsLog
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageStatusPage extends BasePage
{
	private static final long serialVersionUID = 1L;
	@SpringBean(name="ebMSAdminDAO")
	EbMSDAO ebMSDAO;
	@SpringBean(name="cpaService")
	CPAService cpaService;
	@SpringBean(name="ebMSMessageService")
	EbMSMessageService ebMSMessageService;
	@SpringBean(name="cleoPatch")
	Boolean cleoPatch;

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
			super(id,new CompoundPropertyModel<>(new MessageStatusFormModel()));
			add(new BootstrapFormComponentFeedbackBorder("cpaIdFeedback",createCPAIdChoice("cpaId")));
			add(new BootstrapFormComponentFeedbackBorder("fromPartyIdFeedback",createFromPartyIdChoice("fromPartyId")).setVisible(cleoPatch));
			add(new BootstrapFormComponentFeedbackBorder("fromRoleFeedback",createFromRoleChoice("fromRole")));
			add(new BootstrapFormComponentFeedbackBorder("toPartyIdFeedback",createToPartyIdChoice("toPartyId")).setVisible(cleoPatch));
			add(new BootstrapFormComponentFeedbackBorder("toRoleFeedback",createToRoleChoice("toRole")));
			val messageIds = createMessageIdsChoice("messageIds");
			add(new BootstrapFormComponentFeedbackBorder("messageIdFeedback",messageIds,createMessageIdField("messageId")));
			add(createManualCheckBox("manual",messageIds));
			val check = createCheckButton("check");
			setDefaultButton(check);
			add(check);
			add(new ResetButton("reset",new ResourceModel("cmd.reset"),MessageStatusPage.class));
		}

		private DropDownChoice<String> createCPAIdChoice(String id)
		{
			val result = DropDownChoice.<String>builder()
					.id(id)
					.choices(Model.ofList(Utils.toList(cpaService.getCPAIds())))
					.isEnabled(() -> !MessageStatusForm.this.getModelObject().isManual())
					.build();
			result.setLabel(new ResourceModel("lbl.cpaId"));
			//result.setRequired(true);
			Consumer<AjaxRequestTarget> onUpdate = t ->
			{
				try
				{
					val model = MessageStatusForm.this.getModelObject();
					val cpa = JAXBParser.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
					model.resetFromPartyIds(CPAUtils.getPartyIds(cpa));
					model.resetFromRoles(CPAUtils.getRoleNames(cpa));
					model.resetToPartyIds();
					model.resetToRoles();
					t.add(getPage().get("feedback"));
					t.add(getPage().get("form"));
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
					.isEnabled(() -> !MessageStatusForm.this.getModelObject().isManual())
					.isRequired(() -> !MessageStatusForm.this.getModelObject().isManual())
					.build();
			result.setLabel(new ResourceModel("lbl.fromPartyId"));
			result.setOutputMarkupId(true);
			Consumer<AjaxRequestTarget> onUpdate = t ->
			{
				try
				{
					val model = MessageStatusForm.this.getModelObject();
					val cpa = JAXBParser.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
					model.resetFromRoles(CPAUtils.getRoleNames(cpa,model.getFromPartyId()));
					model.resetToPartyIds(CPAUtils.getOtherPartyIds(cpa,model.getFromPartyId()));
					if (model.getFromRole() != null)
						model.resetToRoles(CPAUtils.getRoleNames(cpa,model.getToPartyId()));
					model.resetMessageIds(ebMSDAO.selectMessageIds(model.getCpaId(),model.getFromRole(),model.getToRole(),EbMSMessageStatus.getSendStatus()));
					if (model.getMessageIds().size() == 0)
						info("No messages found");
					t.add(getPage().get("feedback"));
					t.add(getPage().get("form"));
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
					.isEnabled(() -> !MessageStatusForm.this.getModelObject().isManual())
					.build();
			result.setLabel(new ResourceModel("lbl.fromRole"));
			result.setRequired(false).setOutputMarkupId(true);
			Consumer<AjaxRequestTarget> onUpdate = t ->
			{
				try
				{
					val model = MessageStatusForm.this.getModelObject();
					val cpa = JAXBParser.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
					model.resetFromPartyIds(CPAUtils.getPartyIdsByRoleName(cpa,model.getFromRole()));
					model.resetToPartyIds(CPAUtils.getOtherPartyIds(cpa,model.getFromPartyId()));
					model.resetToRoles(CPAUtils.getRoleNames(cpa,model.getToPartyId()));
					model.resetMessageIds(ebMSDAO.selectMessageIds(model.getCpaId(),model.getFromRole(),model.getToRole(),EbMSMessageStatus.getSendStatus()));
					if (model.getMessageIds().size() == 0)
						info("No messages found");
					t.add(getPage().get("feedback"));
					t.add(getPage().get("form"));
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
					.isEnabled(() -> !MessageStatusForm.this.getModelObject().isManual())
					.isRequired(() -> !MessageStatusForm.this.getModelObject().isManual())
					.build();
			result.setLabel(new ResourceModel("lbl.toPartyId"));
			result.setOutputMarkupId(true);
			Consumer<AjaxRequestTarget> onUpdate = t ->
			{
				try
				{
					val model = MessageStatusForm.this.getModelObject();
					val cpa = JAXBParser.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
					model.resetToRoles(CPAUtils.getRoleNames(cpa,model.getToPartyId()));
					model.resetMessageIds(ebMSDAO.selectMessageIds(model.getCpaId(),model.getFromRole(),model.getToRole(),EbMSMessageStatus.getSendStatus()));
					if (model.getMessageIds().size() == 0)
						info("No messages found");
					t.add(getPage().get("feedback"));
					t.add(getPage().get("form"));
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
					.isEnabled(() -> !MessageStatusForm.this.getModelObject().isManual())
					.build();
			result.setLabel(new ResourceModel("lbl.toRole"));
			result.setRequired(false).setOutputMarkupId(true);
			Consumer<AjaxRequestTarget> onUpdate = t ->
			{
				try
				{
					val model = MessageStatusForm.this.getModelObject();
					val cpa = JAXBParser.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
					if (model.getToPartyId() == null)
						model.resetToPartyIds(CPAUtils.getPartyIdsByRoleName(cpa,model.getToRole()));
					model.resetMessageIds(ebMSDAO.selectMessageIds(model.getCpaId(),model.getFromRole(),model.getToRole(),EbMSMessageStatus.getSendStatus()));
					if (model.getMessageIds().size() == 0)
						info("No messages found");
					t.add(getPage().get("feedback"));
					t.add(getPage().get("form"));
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

		private DropDownChoice<String> createMessageIdsChoice(String id)
		{
			val result = DropDownChoice.<String>builder()
					.id(id)
					.model(new PropertyModel<String>(this.getModelObject(),"messageId"))
					.choices(new PropertyModel<List<String>>(this.getModelObject(),"messageIds"))
					.isVisible(() -> !MessageStatusForm.this.getModelObject().isManual())
					.build();
			result.setLabel(new ResourceModel("lbl.messageId"));
			result.setOutputMarkupPlaceholderTag(true);
			result.setRequired(true);
			return result;
		}

		private TextField<String> createMessageIdField(String id)
		{
			val result = TextField.<String>builder()
					.id(id)
					.isVisible(() -> MessageStatusForm.this.getModelObject().isManual())
					.build();
			result.setLabel(new ResourceModel("lbl.messageId"));
			result.setRequired(true).setOutputMarkupPlaceholderTag(true);
			return result;
		}

		private CheckBox createManualCheckBox(String id, final DropDownChoice<String> messageIds)
		{
			val result = new CheckBox(id);
			result.setLabel(new ResourceModel("lbl.manual"));
			Consumer<AjaxRequestTarget> onUpdate = t ->
			{
				if (messageIds.isVisible())
				{
					val model = MessageStatusForm.this.getModelObject();
					model.resetMessageIds(ebMSDAO.selectMessageIds(model.getCpaId(),model.getFromRole(),model.getToRole(),EbMSMessageStatus.getSendStatus()));
					if (model.getMessageIds().size() == 0)
						info("No messages found");
				}
				t.add(getPage().get("feedback"));
				t.add(getPage().get("form"));
			};
			result.add(new AjaxFormComponentUpdatingBehavior("change",onUpdate));
			return result;
		}

		private Button createCheckButton(String id)
		{
			Action onSubmit = () ->
			{
				try
				{
					val model = MessageStatusForm.this.getModelObject();
					if (!model.isManual() || (model.getCpaId() != null && model.getFromPartyId() != null && model.getToPartyId() != null))
					{
						val messageStatus = ebMSMessageService.getMessageStatus(model.getCpaId(),new Party(model.getFromPartyId(),model.getFromRole()),new Party(model.getToPartyId(),model.getToRole()),model.getMessageId());
						info(new StringResourceModel("getMessageStatus.ok",Model.of(messageStatus.getStatus())).getString());
					}
					else
					{
						val messageStatus = ebMSMessageService.getMessageStatus(model.getMessageId());
						info(new StringResourceModel("getMessageStatus.ok",Model.of(messageStatus.getStatus())).getString());
					}
				}
				catch (Exception e)
				{
					log.error("",e);
					error(e.getMessage());
				}
			};
			return new Button(id,new ResourceModel("cmd.check"),onSubmit);
		}

	}

	@Data
	@FieldDefaults(level = AccessLevel.PRIVATE)
	@NoArgsConstructor
	public class MessageStatusFormModel implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		String cpaId;
		final List<String> fromPartyIds = new ArrayList<>();
		String fromPartyId;
		final List<String> fromRoles = new ArrayList<>();
		String fromRole;
		final List<String> toPartyIds = new ArrayList<>();
		String toPartyId;
		final List<String> toRoles = new ArrayList<>();
		String toRole;
		final List<String> messageIds = new ArrayList<>();
		String messageId;
		boolean manual = true;
		
		public void resetFromPartyIds()
		{
			getFromPartyIds().clear();
			setFromPartyId(null);
		}
		public void resetFromPartyIds(List<String> partyIds)
		{
			resetFromPartyIds();
			getFromPartyIds().addAll(partyIds);
			setFromPartyId(getFromPartyIds().size() == 1 ? getFromPartyIds().get(0) : null);
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
		public void resetToRoles()
		{
			getToRoles().clear();
			setToRole(null);
		}
		public void resetToRoles(List<String> roleNames)
		{
			resetToRoles();
			getToRoles().addAll(roleNames);
			setToRole(getFromRole() != null && getToRoles().size() == 1 ? getToRoles().get(0) : null);
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
	}		
}
