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

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.io.IClusterable;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nl.clockwork.ebms.admin.Utils;
import nl.clockwork.ebms.admin.web.Action;
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.BootstrapFeedbackPanel;
import nl.clockwork.ebms.admin.web.BootstrapFormComponentFeedbackBorder;
import nl.clockwork.ebms.admin.web.Button;
import nl.clockwork.ebms.admin.web.DropDownChoice;
import nl.clockwork.ebms.admin.web.ResetButton;
import nl.clockwork.ebms.service.cpa.CPAService;
import nl.clockwork.ebms.service.cpa.certificate.CertificateMapping;
import nl.clockwork.ebms.service.cpa.certificate.CertificateMappingService;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CertificateMappingPage extends BasePage
{
	private static final long serialVersionUID = 1L;
	@SpringBean(name="cpaService")
	CPAService cpaService;
	@SpringBean(name="certificateMappingService")
	CertificateMappingService certificateMappingService;

	public CertificateMappingPage()
	{
		this(Model.of(new CertificateMappingFormData()));
	}
	
	public CertificateMappingPage(IModel<CertificateMappingFormData> model)
	{
		add(new BootstrapFeedbackPanel("feedback"));
		add(new EditCertificateMappingForm("form",model));
	}

	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("certificateMapping",this);
	}

	public class EditCertificateMappingForm extends Form<CertificateMappingFormData>
	{
		private static final long serialVersionUID = 1L;

		public EditCertificateMappingForm(String id, IModel<CertificateMappingFormData> model)
		{
			super(id,new CompoundPropertyModel<>(model));
			add(new BootstrapFormComponentFeedbackBorder("sourceFeedback",new FileUploadField("source").setRequired(true).setLabel(new ResourceModel("lbl.source"))));
			add(new BootstrapFormComponentFeedbackBorder("destinationFeedback",new FileUploadField("destination").setRequired(true).setLabel(new ResourceModel("lbl.destination"))));
			add(new BootstrapFormComponentFeedbackBorder("cpaIdFeedback",new DropDownChoice<String>("cpaId",Model.ofList(Utils.toList(cpaService.getCPAIds()))).setLabel(new ResourceModel("lbl.cpaId"))));
			add(createSetButton("set"));
			add(new ResetButton("reset",new ResourceModel("cmd.reset"),CertificateMappingPage.class));
		}

		private Button createSetButton(String id)
		{
			Action onSubmit = () ->
			{
				try
				{
					val o = getModelObject();
					certificateMappingService.setCertificateMapping(createCertificateMapping(o));
					setResponsePage(CertificateMappingsPage.class);
				}
				catch (Exception e)
				{
					log.error("",e);
					error(e.getMessage());
				}
			};
			val result = new Button(id,new ResourceModel("cmd.upload"),onSubmit);
			setDefaultButton(result);
			return result;
		}

		private CertificateMapping createCertificateMapping(CertificateMappingFormData o) throws CertificateException, IOException
		{
			X509Certificate source = getX509Certificate(o.source);
			X509Certificate destination = getX509Certificate(o.destination);
			String cpaId = o.cpaId;
			return new CertificateMapping(source,destination,cpaId);
		}

		private X509Certificate getX509Certificate(List<FileUpload> files) throws CertificateException, IOException
		{
			return files != null && files.size() == 1 ? (X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(files.get(0).getInputStream()) : null;
		}
	}

	@Data
	@FieldDefaults(level = AccessLevel.PRIVATE)
	@NoArgsConstructor
	public static class CertificateMappingFormData implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		List<FileUpload> source;
		List<FileUpload> destination;
		String cpaId;
	}

}
