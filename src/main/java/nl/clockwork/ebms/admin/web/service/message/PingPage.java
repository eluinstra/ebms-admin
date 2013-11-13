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
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.BootstrapFeedbackPanel;
import nl.clockwork.ebms.admin.web.BootstrapFormComponentFeedbackBorder;
import nl.clockwork.ebms.admin.web.ResetButton;
import nl.clockwork.ebms.common.XMLMessageBuilder;
import nl.clockwork.ebms.service.CPAService;
import nl.clockwork.ebms.service.EbMSMessageService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.io.IClusterable;
import org.oasis_open.committees.ebxml_cppa.schema.cpp_cpa_2_0.CollaborationProtocolAgreement;

public class PingPage extends BasePage
{
	private static final long serialVersionUID = 1L;
	protected transient Log logger = LogFactory.getLog(getClass());
	@SpringBean(name="cpaService")
	private CPAService cpaService;
	@SpringBean(name="ebMSMessageService")
	private EbMSMessageService ebMSMessageService;

	public PingPage()
	{
		add(new BootstrapFeedbackPanel("feedback").setOutputMarkupId(true));
		add(new PingForm("form"));
	}
	
	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("ping",this);
	}

	public class PingForm extends Form<PingFormModel>
	{
		private static final long serialVersionUID = 1L;

		public PingForm(String id)
		{
			super(id,new CompoundPropertyModel<PingFormModel>(new PingFormModel()));

			DropDownChoice<String> cpaIds = new DropDownChoice<String>("cpaIds",new PropertyModel<String>(this.getModelObject(),"cpaId"),Model.ofList(cpaService.getCPAIds()))
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.cpaId",PingForm.this));
				}
			};
			cpaIds.setRequired(true);
			add(new BootstrapFormComponentFeedbackBorder("cpaIdFeedback",cpaIds));

			DropDownChoice<String> fromParties = new DropDownChoice<String>("fromParties",new PropertyModel<String>(this.getModelObject(),"fromParty"),new PropertyModel<List<String>>(this.getModelObject(),"fromParties"))
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.fromParty",PingForm.this));
				}
			};
			fromParties.setRequired(true).setOutputMarkupId(true);
			add(new BootstrapFormComponentFeedbackBorder("fromPartyFeedback",fromParties));
			
			cpaIds.add(new AjaxFormComponentUpdatingBehavior("onchange")
      {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					try
					{
						PingFormModel model = PingForm.this.getModelObject();
						CollaborationProtocolAgreement cpa = XMLMessageBuilder.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
						model.getFromParties().clear();
						model.getFromParties().addAll(CPAUtils.getPartyNames(cpa));
						model.setFromParty(null);
						model.getToParties().clear();
						model.setToParty(null);
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

			DropDownChoice<String> toParties = new DropDownChoice<String>("toParties",new PropertyModel<String>(this.getModelObject(),"toParty"),new PropertyModel<List<String>>(this.getModelObject(),"toParties"))
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.toParty",PingForm.this));
				}
			};
			toParties.setRequired(true).setOutputMarkupId(true);
			add(new BootstrapFormComponentFeedbackBorder("toPartyFeedback",toParties));
			
			fromParties.add(new AjaxFormComponentUpdatingBehavior("onchange")
      {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					try
					{
						PingFormModel model = PingForm.this.getModelObject();
						CollaborationProtocolAgreement cpa = XMLMessageBuilder.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
						String otherPartyName = CPAUtils.getOtherPartyName(cpa,model.getFromParty());
						model.getToParties().clear();
						model.getToParties().addAll(Arrays.asList(otherPartyName));
						model.setToParty(otherPartyName);
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

			Button ping = new Button("ping",new ResourceModel("cmd.ping"))
			{
				private static final long serialVersionUID = 1L;
	
				@Override
				public void onSubmit()
				{
					try
					{
						PingFormModel model = PingForm.this.getModelObject();
						CollaborationProtocolAgreement cpa = XMLMessageBuilder.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
						ebMSMessageService.ping(model.getCpaId(),CPAUtils.getPartyIdbyPartyName(cpa,model.getFromParty()),CPAUtils.getPartyIdbyPartyName(cpa,model.getToParty()));
						info(PingPage.this.getString("ping.ok"));
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

			add(new ResetButton("reset",new ResourceModel("cmd.reset"),PingPage.class));
		}
	}

	public class PingFormModel implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		private String cpaId;
		private List<String> fromParties = new ArrayList<String>();
		private String fromParty;
		private List<String> toParties = new ArrayList<String>();
		private String toParty;
		
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
		public void setToParty(String toParty)
		{
			this.toParty = toParty;
		}
		public List<String> getToParties()
		{
			return toParties;
		}
	}		

}
