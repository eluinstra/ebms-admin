/*
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
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
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
import nl.clockwork.ebms.admin.web.ResetButton;
import nl.clockwork.ebms.cpa.CPAService;
import nl.clockwork.ebms.jaxb.JAXBParser;
import nl.clockwork.ebms.service.EbMSMessageService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.io.IClusterable;
import org.oasis_open.committees.ebxml_cppa.schema.cpp_cpa_2_0.CollaborationProtocolAgreement;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PingPage extends BasePage
{
	private static final long serialVersionUID = 1L;
	@SpringBean(name = "cpaService")
	CPAService cpaService;
	@SpringBean(name = "ebMSMessageService")
	EbMSMessageService ebMSMessageService;

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

	public class PingForm extends Form<PingFormData>
	{
		private static final long serialVersionUID = 1L;

		public PingForm(String id)
		{
			super(id,new CompoundPropertyModel<>(new PingFormData()));
			add(new BootstrapFormComponentFeedbackBorder("cpaIdFeedback",createCPAIdChoice("cpaId")));
			add(new BootstrapFormComponentFeedbackBorder("fromPartyIdFeedback",createFromPartyIdChoice("fromPartyId")));
			add(new BootstrapFormComponentFeedbackBorder("toPartyIdFeedback",createToPartyIdChoice("toPartyId")));
			val ping = createPingButton("ping");
			setDefaultButton(ping);
			add(ping);
			add(new ResetButton("reset",new ResourceModel("cmd.reset"),PingPage.class));
		}

		private DropDownChoice<String> createCPAIdChoice(String id)
		{
			val result = new DropDownChoice<>(id,Model.ofList(Utils.toList(cpaService.getCPAIds())));
			result.setLabel(new ResourceModel("lbl.cpaId"));
			result.setRequired(true);
			Consumer<AjaxRequestTarget> onUpdate = t ->
			{
				try
				{
					val o = getModelObject();
					val cpa = JAXBParser.getInstance(CollaborationProtocolAgreement.class).handleUnsafe(cpaService.getCPA(o.getCpaId()));
					o.resetFromPartyIds(CPAUtils.getPartyIds(cpa));
					o.resetToPartyIds();
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
			DropDownChoice<String> result = new DropDownChoice<>(id,new PropertyModel<List<String>>(getModel(),"fromPartyIds"));
			result.setLabel(new ResourceModel("lbl.fromPartyId"));
			result.setRequired(true).setOutputMarkupId(true);
			Consumer<AjaxRequestTarget> onUpdate = t ->
			{
				try
				{
					val o = getModelObject();
					val cpa = JAXBParser.getInstance(CollaborationProtocolAgreement.class).handleUnsafe(cpaService.getCPA(o.getCpaId()));
					o.resetToPartyIds(CPAUtils.getOtherPartyIds(cpa,o.getFromPartyId()));
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
			val result = new DropDownChoice<>(id,new PropertyModel<List<String>>(getModel(),"toPartyIds"));
			result.setLabel(new ResourceModel("lbl.toPartyId"));
			result.setRequired(true).setOutputMarkupId(true);
			Consumer<AjaxRequestTarget> onUpdate = t ->
			{
				t.add(getPage().get("feedback"));
				t.add(getPage().get("form"));
			};
			result.add(new AjaxFormComponentUpdatingBehavior("change",onUpdate));
			return result;
		}

		private Button createPingButton(String id)
		{
			Action onSubmit = () ->
			{
				try
				{
					val o = getModelObject();
					ebMSMessageService.ping(o.getCpaId(),o.getFromPartyId(),o.getToPartyId());
					info(PingPage.this.getString("ping.ok"));
				}
				catch (Exception e)
				{
					log.error("",e);
					error(e.getMessage());
				}
			};
			return new Button(id,new ResourceModel("cmd.ping"),onSubmit);
		}
	}

	@Data
	@FieldDefaults(level = AccessLevel.PRIVATE)
	@NoArgsConstructor
	public class PingFormData implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		String cpaId;
		final List<String> fromPartyIds = new ArrayList<>();
		String fromPartyId;
		final List<String> toPartyIds = new ArrayList<>();
		String toPartyId;

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
	}
}
