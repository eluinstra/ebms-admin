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
package nl.clockwork.ebms.admin.web.service.cpa;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.io.IClusterable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;
import lombok.extern.apachecommons.CommonsLog;
import nl.clockwork.ebms.admin.web.Action;
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.BootstrapFeedbackPanel;
import nl.clockwork.ebms.admin.web.BootstrapFormComponentFeedbackBorder;
import nl.clockwork.ebms.admin.web.Button;
import nl.clockwork.ebms.admin.web.ResetButton;
import nl.clockwork.ebms.service.CPAService;
import nl.clockwork.ebms.service.model.CertificateMapping;

@CommonsLog
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CertificateMappingPage extends BasePage
{
	private static final long serialVersionUID = 1L;
	@SpringBean(name="cpaService")
	CPAService cpaService;

	public CertificateMappingPage()
	{
		this(new CertificateMapping());
	}
	
	public CertificateMappingPage(CertificateMapping certificateMapping)
	{
		add(new BootstrapFeedbackPanel("feedback"));
		add(new EditCertificateMappingForm("form",certificateMapping));
	}

	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("certificateMapping",this);
	}

	public class EditCertificateMappingForm extends Form<CertificateMappingFormModel>
	{
		private static final long serialVersionUID = 1L;

		public EditCertificateMappingForm(String id, CertificateMapping certificateMapping)
		{
			super(id,new CompoundPropertyModel<>(new CertificateMappingFormModel(certificateMapping)));
			add(new BootstrapFormComponentFeedbackBorder("sourceFeedback",new TextField<String>("certificateMapping.source").setRequired(true).setLabel(new ResourceModel("lbl.source"))));
			add(new BootstrapFormComponentFeedbackBorder("destinationFeedback",new TextField<String>("certificateMapping.destination").setRequired(true).setLabel(new ResourceModel("lbl.destination"))));
			add(createSetButton("set"));
			add(new ResetButton("reset",new ResourceModel("cmd.reset"),CertificateMappingPage.class));
		}

		private Button createSetButton(String id)
		{
			Action onSubmit = () ->
			{
				try
				{
					val certificateMapping = EditCertificateMappingForm.this.getModelObject().certificateMapping;
					cpaService.setCertificateMapping(certificateMapping);
					setResponsePage(CertificateMappingsPage.class);
				}
				catch (Exception e)
				{
					log.error("",e);
					error(e.getMessage());
				}
			};
			val result = new Button(id,new ResourceModel("cmd.upload"),onSubmit );
			setDefaultButton(result);
			return result;
		}
	}

	@Data
	@FieldDefaults(level = AccessLevel.PRIVATE)
	@NoArgsConstructor
	@AllArgsConstructor
	public class CertificateMappingFormModel implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		@NonNull
		CertificateMapping certificateMapping;
	}

}
