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

import nl.clockwork.ebms.admin.CPAUtils;
import nl.clockwork.ebms.admin.dao.EbMSDAO;
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.common.XMLMessageBuilder;
import nl.clockwork.ebms.model.MessageStatus;
import nl.clockwork.ebms.service.CPAService;
import nl.clockwork.ebms.service.EbMSMessageService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.validation.FormComponentFeedbackBorder;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.io.IClusterable;
import org.oasis_open.committees.ebxml_cppa.schema.cpp_cpa_2_0.CollaborationProtocolAgreement;

public class MessageStatusPage extends BasePage
{
	private static final long serialVersionUID = 1L;
	protected transient Log logger = LogFactory.getLog(getClass());
	@SpringBean(name="ebMSDAO")
	public EbMSDAO ebMSDAO;
	@SpringBean(name="cpaClient")
	private CPAService cpaClient;
	@SpringBean(name="ebMSClient")
	private EbMSMessageService ebMSClient;

	public MessageStatusPage()
	{
		add(new FeedbackPanel("feedback"));
		add(new MessageStatusForm("pingForm"));
	}
	
	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("ping",this);
	}

	public class MessageStatusForm extends Form<MessageStatusFormModel>
	{
		private static final long serialVersionUID = 1L;

		public MessageStatusForm(String id)
		{
			super(id,new CompoundPropertyModel<MessageStatusFormModel>(new MessageStatusFormModel()));

			DropDownChoice<String> cpaIds = new DropDownChoice<String>("cpaIds",new PropertyModel<String>(this.getModelObject(),"cpaId"),Model.ofList(cpaClient.getCPAIds()));
			cpaIds.setLabel(Model.of(getLocalizer().getString("lbl.cpaId",this)));
			cpaIds.setRequired(true);
			MarkupContainer cpaIdFeedback = new FormComponentFeedbackBorder("cpaIdFeedback");
			add(cpaIdFeedback);
			cpaIdFeedback.add(cpaIds);

			final DropDownChoice<String> fromParties = new DropDownChoice<String>("fromParties",new PropertyModel<String>(this.getModelObject(),"fromParty"),new PropertyModel<List<String>>(this.getModelObject(),"fromParties"));
			fromParties.setLabel(Model.of(getLocalizer().getString("lbl.fromParty",this)));
			fromParties.setRequired(true);
			fromParties.setOutputMarkupId(true);
			MarkupContainer fromPartyFeedback = new FormComponentFeedbackBorder("fromPartyFeedback");
			add(fromPartyFeedback);
			fromPartyFeedback.add(fromParties);
			
			cpaIds.add(new AjaxFormComponentUpdatingBehavior("onchange")
      {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					try
					{
						MessageStatusFormModel model = MessageStatusForm.this.getModelObject();
						CollaborationProtocolAgreement cpa = XMLMessageBuilder.getInstance(CollaborationProtocolAgreement.class).handle(cpaClient.getCPA(model.getCpaId()));
						ArrayList<String> partyNames = CPAUtils.getPartyNames(cpa);
						model.setFromParties(partyNames);
						target.add(fromParties);
					}
					catch (JAXBException e)
					{
						logger.error("",e);
						error(e.getMessage());
					}
				}
      });

			final DropDownChoice<String> toParties = new DropDownChoice<String>("toParties",new PropertyModel<String>(this.getModelObject(),"toParty"),new PropertyModel<List<String>>(this.getModelObject(),"toParties"));
			toParties.setLabel(Model.of(getLocalizer().getString("lbl.toParty",this)));
			toParties.setRequired(true);
			toParties.setOutputMarkupId(true);
			MarkupContainer toPartyFeedback = new FormComponentFeedbackBorder("toPartyFeedback");
			add(toPartyFeedback);
			toPartyFeedback.add(toParties);
			
			final DropDownChoice<String> messageIds = new DropDownChoice<String>("messageIds",new PropertyModel<String>(this.getModelObject(),"messageId"),new PropertyModel<List<String>>(this.getModelObject(),"messageIds"));
			messageIds.setLabel(Model.of(getLocalizer().getString("lbl.messageId",this)));
			messageIds.setRequired(true);
			messageIds.setOutputMarkupId(true);
			MarkupContainer messageIdFeedback = new FormComponentFeedbackBorder("messageIdFeedback");
			add(messageIdFeedback);
			messageIdFeedback.add(messageIds);
			
			fromParties.add(new AjaxFormComponentUpdatingBehavior("onchange")
      {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					try
					{
						MessageStatusFormModel model = MessageStatusForm.this.getModelObject();
						CollaborationProtocolAgreement cpa = XMLMessageBuilder.getInstance(CollaborationProtocolAgreement.class).handle(cpaClient.getCPA(model.getCpaId()));
						String otherPartyName = CPAUtils.getOtherPartyName(cpa,model.getFromParty());
						model.setToParties(Arrays.asList(otherPartyName));
						model.setToParty(otherPartyName);
						model.setMessageIds(ebMSDAO.getPendingMessageIds(model.getCpaId(),model.getFromParty(),model.getToParty()));
						target.add(toParties);
						target.add(messageIds);
					}
					catch (JAXBException e)
					{
						logger.error("",e);
						error(e.getMessage());
					}
				}
      });

			//TextField<String> messageId = new TextField<String>("messageId");
			//messageId.setLabel(Model.of(getLocalizer().getString("lbl.messageId",this)));
			//messageId.setRequired(true);
			//MarkupContainer messageIdFeedback = new FormComponentFeedbackBorder("messageIdFeedback");
			//add(messageIdFeedback);
			//messageIdFeedback.add(messageId);
			
			Button ping = new Button("check",new ResourceModel("cmd.check"))
			{
				private static final long serialVersionUID = 1L;
	
				@Override
				public void onSubmit()
				{
					try
					{
						MessageStatusFormModel model = MessageStatusForm.this.getModelObject();
						CollaborationProtocolAgreement cpa = XMLMessageBuilder.getInstance(CollaborationProtocolAgreement.class).handle(cpaClient.getCPA(model.getCpaId()));
						MessageStatus messageStatus = ebMSClient.getMessageStatus(model.getCpaId(),CPAUtils.getPartyIdbyPartyName(cpa,model.getFromParty()),CPAUtils.getPartyIdbyPartyName(cpa,model.getToParty()),model.getMessageId());
						info("Get Message Status succesful. Status is " + messageStatus.getStatus().statusCode());
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
		}
	}

	public class MessageStatusFormModel implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		private String cpaId;
		private List<String> fromParties = new ArrayList<String>();
		private String fromParty;
		private List<String> toParties = new ArrayList<String>();
		private String toParty;
		private List<String> messageIds = new ArrayList<String>();
		private String messageId;
		
		public String getCpaId()
		{
			return cpaId;
		}
		public void setCpaId(String cpaId)
		{
			this.cpaId = cpaId;
		}
		public String getFromParty()
		{
			return fromParty;
		}
		public void setFromParties(List<String> fromParties)
		{
			this.fromParties = fromParties;
		}
		public String getToParty()
		{
			return toParty;
		}
		public void setFromParty(String fromParty)
		{
			this.fromParty = fromParty;
		}
		public List<String> getFromParties()
		{
			return fromParties;
		}
		public void setToParties(List<String> toParties)
		{
			this.toParties = toParties;
		}
		public void setToParty(String toParty)
		{
			this.toParty = toParty;
		}
		public List<String> getToParties()
		{
			return toParties;
		}
		public List<String> getMessageIds()
		{
			return messageIds;
		}
		public void setMessageIds(List<String> messageIds)
		{
			this.messageIds = messageIds;
		}
		public String getMessageId()
		{
			return messageId;
		}
		public void setMessageId(String messageId)
		{
			this.messageId = messageId;
		}
	}		

}
