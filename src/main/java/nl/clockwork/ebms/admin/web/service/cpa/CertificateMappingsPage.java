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

import java.security.cert.X509Certificate;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nl.clockwork.ebms.admin.web.Action;
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.BootstrapFeedbackPanel;
import nl.clockwork.ebms.admin.web.Button;
import nl.clockwork.ebms.admin.web.OddOrEvenIndexStringModel;
import nl.clockwork.ebms.admin.web.PageLink;
import nl.clockwork.ebms.admin.web.WebMarkupContainer;
import nl.clockwork.ebms.service.cpa.certificate.CertificateMapping;
import nl.clockwork.ebms.service.cpa.certificate.CertificateMappingService;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CertificateMappingsPage extends BasePage
{
	private class CertificateMappingsDataView extends DataView<CertificateMapping>
	{
		protected CertificateMappingsDataView(String id, IDataProvider<CertificateMapping> dataProvider)
		{
			super(id,dataProvider);
			setOutputMarkupId(true);
		}

		private static final long serialVersionUID = 1L;

		@Override
		protected void populateItem(final Item<CertificateMapping> item)
		{
			val o = item.getModelObject();
			item.add(new Label("source",createLabel(o.getSource())));
			item.add(new Label("destination",createLabel(o.getDestination())));
			item.add(new Label("cpaId",o.getCpaId()));
			item.add(createEditButton("editCertificate",item.getModel()));
			item.add(createDeleteButton("delete",o));
			item.add(AttributeModifier.replace("class",OddOrEvenIndexStringModel.of(item.getIndex())));
		}

		private String createLabel(@NonNull X509Certificate certificate)
		{
			return certificate.getSubjectDN().getName() + " (" + certificate.getSerialNumber().toString() + ")";
		}

		private Button createEditButton(String id, final IModel<CertificateMapping> model)
		{
			Action onSubmit = () ->
			{
				try
				{
					setResponsePage(new CertificateMappingPage());
				}
				catch (Exception e)
				{
					log.error("",e);
					error(e.getMessage());
				}
			};
			return new Button(id,new ResourceModel("cmd.edit"),onSubmit);
		}

		private Button createDeleteButton(String id, final CertificateMapping certificateMapping)
		{
			Action onSubmit = () ->
			{
				try
				{
					certificateMappingService.deleteCertificateMapping(certificateMapping.getSource(),certificateMapping.getCpaId());
					setResponsePage(new CertificateMappingsPage());
				}
				catch (Exception e)
				{
					log.error("",e);
					error(e.getMessage());
				}
			};
			val result = new Button(id,new ResourceModel("cmd.delete"),onSubmit);
			result.add(AttributeModifier.replace("onclick","return confirm('" + getLocalizer().getString("confirm",this) + "');"));
			return result;
		}

	}

	private static final long serialVersionUID = 1L;
	@SpringBean(name="certificateMappingService")
	CertificateMappingService certificateMappingService;

	public CertificateMappingsPage()
	{
		add(new BootstrapFeedbackPanel("feedback"));
		add(new EditCertificateMappingsForm("form"));
	}

	public class EditCertificateMappingsForm extends Form<Void>
	{
		private static final long serialVersionUID = 1L;

		public EditCertificateMappingsForm(String id)
		{
			super(id);
			val container = new WebMarkupContainer("container");
			add(container);
			container.add(new CertificateMappingsDataView("certificateMappings",CertificateMappingDataProvider.of(certificateMappingService)));
			add(new PageLink("new",new CertificateMappingPage()));
		}
	}
	
	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("certificateMappings",this);
	}
}
