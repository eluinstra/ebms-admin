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
package nl.clockwork.ebms.admin.web.configuration;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.util.io.IClusterable;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.BootstrapFeedbackPanel;
import nl.clockwork.ebms.admin.web.BootstrapPanelBorder;
import nl.clockwork.ebms.admin.web.Button;
import nl.clockwork.ebms.admin.web.ResetButton;
import nl.clockwork.ebms.admin.web.WicketApplication;
import nl.clockwork.ebms.admin.web.configuration.ConsolePropertiesFormPanel.ConsolePropertiesFormData;
import nl.clockwork.ebms.admin.web.configuration.CorePropertiesFormPanel.CorePropertiesFormData;
import nl.clockwork.ebms.admin.web.configuration.EncryptionPropertiesFormPanel.EncryptionPropertiesFormData;
import nl.clockwork.ebms.admin.web.configuration.HttpPropertiesFormPanel.HttpPropertiesFormData;
import nl.clockwork.ebms.admin.web.configuration.JdbcPropertiesFormPanel.JdbcPropertiesFormData;
import nl.clockwork.ebms.admin.web.configuration.ServicePropertiesFormPanel.ServicePropertiesFormData;
import nl.clockwork.ebms.admin.web.configuration.SignaturePropertiesFormPanel.SignaturePropertiesFormData;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EbMSAdminPropertiesPage extends BasePage
{
	private static final long serialVersionUID = 1L;
	PropertiesType propertiesType;

	public EbMSAdminPropertiesPage() throws IOException, IllegalStateException
	{
		this(null);
	}
	public EbMSAdminPropertiesPage(IModel<EbMSAdminPropertiesFormData> model) throws IllegalStateException
	{
		val propertySourcesPlaceholderConfigurer = WicketApplication.get().getPropertySourcesPlaceholderConfigurer();
		val f = propertySourcesPlaceholderConfigurer .getOverridePropertiesFile().getFilename();
		propertiesType = PropertiesType.getPropertiesType(f).orElseThrow(() -> new IllegalStateException("No PropertiesType found for " + propertySourcesPlaceholderConfigurer.getOverridePropertiesFile().getFilename()));
		add(new BootstrapFeedbackPanel("feedback"));
		if (model == null)
		{
			try
			{
				val properties = WicketApplication.get().getPropertySourcesPlaceholderConfigurer().getProperties();
				model = Model.of(new EbMSAdminPropertiesReader(properties).read(propertiesType));
				this.info(new StringResourceModel("properties.loaded",this).getString());
			}
			catch (IOException e)
			{
				model = Model.of(new EbMSAdminPropertiesFormData());
				log.warn("",e);
				warn(e.getMessage());
			}
		}
		add(new EbMSAdminPropertiesForm("form",model));
	}
	
	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("ebMSAdminProperties",this);
	}
	
	public class EbMSAdminPropertiesForm extends Form<EbMSAdminPropertiesFormData>
	{
		private static final long serialVersionUID = 1L;

		public EbMSAdminPropertiesForm(String id, IModel<EbMSAdminPropertiesFormData> model)
		{
			super(id,new CompoundPropertyModel<>(model));
			
			val components = new ArrayList<BootstrapPanelBorder>();
			components.add(new BootstrapPanelBorder(
					"panelBorder",
					EbMSAdminPropertiesPage.this.getString("consoleProperties"),
					new ConsolePropertiesFormPanel("component",new PropertyModel<>(getModel(),"consoleProperties"))));
			components.add(new BootstrapPanelBorder(
					"panelBorder",
					EbMSAdminPropertiesPage.this.getString("coreProperties"),
					new CorePropertiesFormPanel("component",new PropertyModel<>(getModel(),"coreProperties"),PropertiesType.EBMS_ADMIN.equals(propertiesType))));
			if (PropertiesType.EBMS_ADMIN.equals(propertiesType))
				components.add(new BootstrapPanelBorder(
						"panelBorder",
						EbMSAdminPropertiesPage.this.getString("serviceProperties"),
						new ServicePropertiesFormPanel("component",new PropertyModel<>(getModel(),"serviceProperties"))));
			if (PropertiesType.EBMS_ADMIN_EMBEDDED.equals(propertiesType))
			{
				components.add(new BootstrapPanelBorder(
						"panelBorder",
						EbMSAdminPropertiesPage.this.getString("httpProperties"),
						new HttpPropertiesFormPanel("component",new PropertyModel<>(getModel(),"httpProperties"),true)));
				components.add(new BootstrapPanelBorder(
						"panelBorder",
						EbMSAdminPropertiesPage.this.getString("signatureProperties"),
						new SignaturePropertiesFormPanel("component",new PropertyModel<>(getModel(),"signatureProperties"))));
				components.add(new BootstrapPanelBorder(
						"panelBorder",
						EbMSAdminPropertiesPage.this.getString("encryptionProperties"),
						new EncryptionPropertiesFormPanel("component",new PropertyModel<>(getModel(),"encryptionProperties"))));
			}
			components.add(new BootstrapPanelBorder(
					"panelBorder",
					EbMSAdminPropertiesPage.this.getString("jdbcProperties"),
					new JdbcPropertiesFormPanel("component",new PropertyModel<>(getModel(),"jdbcProperties"))));
			add(new ComponentsListView("components",components));
			add(createValidateButton("validate"));
			add(new DownloadEbMSAdminPropertiesButton("download",new ResourceModel("cmd.download"),getModel(),propertiesType));
			add(new SaveEbMSAdminPropertiesButton("save",new ResourceModel("cmd.save"),getModel(),propertiesType));
			add(new ResetButton("reset",new ResourceModel("cmd.reset"),EbMSAdminPropertiesPage.class));
		}

		private Button createValidateButton(String id)
		{
			return Button.builder()
					.id(id)
					.onSubmit(() -> info(EbMSAdminPropertiesPage.this.getString("validate.ok")))
					.build();
		}
	}

	@Data
	@FieldDefaults(level = AccessLevel.PRIVATE)
	@NoArgsConstructor
	public static class EbMSAdminPropertiesFormData implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		@NonNull
		ConsolePropertiesFormData consoleProperties = new ConsolePropertiesFormData();
		@NonNull
		CorePropertiesFormData coreProperties = new CorePropertiesFormData();
		@NonNull
		ServicePropertiesFormData serviceProperties = new ServicePropertiesFormData();
		@NonNull
		HttpPropertiesFormData httpProperties = new HttpPropertiesFormData();
		@NonNull
		SignaturePropertiesFormData signatureProperties = new SignaturePropertiesFormData();
		@NonNull
		EncryptionPropertiesFormData encryptionProperties = new EncryptionPropertiesFormData();
		@NonNull
		JdbcPropertiesFormData jdbcProperties = new JdbcPropertiesFormData();
	}
}
