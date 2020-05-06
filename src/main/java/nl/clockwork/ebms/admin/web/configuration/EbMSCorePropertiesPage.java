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
package nl.clockwork.ebms.admin.web.configuration;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.io.IClusterable;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;
import nl.clockwork.ebms.admin.PropertyPlaceholderConfigurer;
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.BootstrapFeedbackPanel;
import nl.clockwork.ebms.admin.web.BootstrapPanelBorder;
import nl.clockwork.ebms.admin.web.Button;
import nl.clockwork.ebms.admin.web.ResetButton;
import nl.clockwork.ebms.admin.web.configuration.CorePropertiesFormPanel.CorePropertiesFormModel;
import nl.clockwork.ebms.admin.web.configuration.EncryptionPropertiesFormPanel.EncryptionPropertiesFormModel;
import nl.clockwork.ebms.admin.web.configuration.HttpPropertiesFormPanel.HttpPropertiesFormModel;
import nl.clockwork.ebms.admin.web.configuration.JdbcPropertiesFormPanel.JdbcPropertiesFormModel;
import nl.clockwork.ebms.admin.web.configuration.SignaturePropertiesFormPanel.SignaturePropertiesFormModel;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class EbMSCorePropertiesPage extends BasePage
{
	private static final long serialVersionUID = 1L;
	@SpringBean(name="propertyConfigurer")
	PropertyPlaceholderConfigurer propertyPlaceholderConfigurer;

	public EbMSCorePropertiesPage() throws IOException
	{
		this(new EbMSCorePropertiesFormModel());
	}
	public EbMSCorePropertiesPage(EbMSCorePropertiesFormModel ebMSCorePropertiesFormModel) throws IOException
	{
		add(new BootstrapFeedbackPanel("feedback"));
		add(new EbMSCorePropertiesForm("form",ebMSCorePropertiesFormModel));
	}
	
	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("ebMSCoreProperties",this);
	}
	
	public class EbMSCorePropertiesForm extends Form<EbMSCorePropertiesFormModel>
	{
		private static final long serialVersionUID = 1L;

		public EbMSCorePropertiesForm(String id, EbMSCorePropertiesFormModel model)
		{
			super(id,new CompoundPropertyModel<>(model));
			
			val components = new ArrayList<BootstrapPanelBorder>();
			components.add(new BootstrapPanelBorder("panelBorder",EbMSCorePropertiesPage.this.getString("coreProperties"),new CorePropertiesFormPanel("component",new PropertyModel<>(getModelObject(),"coreProperties"),false)));
			components.add(new BootstrapPanelBorder("panelBorder",EbMSCorePropertiesPage.this.getString("httpProperties"),new HttpPropertiesFormPanel("component",new PropertyModel<>(getModelObject(),"httpProperties"),false)));
			components.add(new BootstrapPanelBorder("panelBorder",EbMSCorePropertiesPage.this.getString("signatureProperties"),new SignaturePropertiesFormPanel("component",new PropertyModel<>(getModelObject(),"signatureProperties"))));
			components.add(new BootstrapPanelBorder("panelBorder",EbMSCorePropertiesPage.this.getString("encryptionProperties"),new EncryptionPropertiesFormPanel("component",new PropertyModel<>(getModelObject(),"encryptionProperties"))));
			components.add(new BootstrapPanelBorder("panelBorder",EbMSCorePropertiesPage.this.getString("jdbcProperties"),new JdbcPropertiesFormPanel("component",new PropertyModel<>(getModelObject(),"jdbcProperties"))));
			add(new ComponentsListView("components",components));
			add(createValidateButton("validate"));
			add(new DownloadEbMSCorePropertiesButton("download",new ResourceModel("cmd.download"),getModelObject()));
			add(new ResetButton("reset",new ResourceModel("cmd.reset"),EbMSCorePropertiesPage.class));
		}

		private Button createValidateButton(String id)
		{
			return Button.builder()
					.id(id)
					.onSubmit(() -> info(EbMSCorePropertiesPage.this.getString("validate.ok")))
					.build();
		}
	}

	@Data
	@FieldDefaults(level = AccessLevel.PRIVATE)
	@NoArgsConstructor
	public static class EbMSCorePropertiesFormModel implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		@NonNull
		CorePropertiesFormModel coreProperties = new CorePropertiesFormModel();
		@NonNull
		HttpPropertiesFormModel httpProperties = new HttpPropertiesFormModel();
		@NonNull
		SignaturePropertiesFormModel signatureProperties = new SignaturePropertiesFormModel();
		@NonNull
		EncryptionPropertiesFormModel encryptionProperties = new EncryptionPropertiesFormModel();
		@NonNull
		JdbcPropertiesFormModel jdbcProperties = new JdbcPropertiesFormModel();
	}
}
