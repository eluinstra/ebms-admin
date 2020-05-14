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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;
import lombok.extern.apachecommons.CommonsLog;
import nl.clockwork.ebms.admin.PropertyPlaceholderConfigurer;
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.BootstrapFeedbackPanel;
import nl.clockwork.ebms.admin.web.BootstrapPanelBorder;
import nl.clockwork.ebms.admin.web.Button;
import nl.clockwork.ebms.admin.web.ResetButton;
import nl.clockwork.ebms.admin.web.configuration.ConsolePropertiesFormPanel.ConsolePropertiesFormData;
import nl.clockwork.ebms.admin.web.configuration.CorePropertiesFormPanel.CorePropertiesFormData;
import nl.clockwork.ebms.admin.web.configuration.EbMSCorePropertiesPage.EbMSCorePropertiesFormData;
import nl.clockwork.ebms.admin.web.configuration.ServicePropertiesFormPanel.ServicePropertiesFormData;

@CommonsLog
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EbMSAdminPropertiesPage extends BasePage
{
	private static final long serialVersionUID = 1L;
	@SpringBean(name="propertyConfigurer")
	PropertyPlaceholderConfigurer propertyPlaceholderConfigurer;
	PropertiesType propertiesType;

	public EbMSAdminPropertiesPage() throws IOException
	{
		this(null);
	}
	public EbMSAdminPropertiesPage(IModel<EbMSAdminPropertiesFormData> model) throws IOException
	{
		propertiesType = PropertiesType.getPropertiesType(propertyPlaceholderConfigurer.getOverridePropertiesFile().getFilename());
		add(new BootstrapFeedbackPanel("feedback"));
		if (model == null)
		{
			try
			{
				val file = new File(propertiesType.getPropertiesFile());
				val reader = new FileReader(file);
				model = Model.of(new EbMSAdminPropertiesReader(reader).read(propertiesType));
				this.info(new StringResourceModel("properties.loaded",this,Model.of(file)).getString());
			}
			catch (IOException e)
			{
				log.error("",e);
				error(e.getMessage());
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
	@EqualsAndHashCode(callSuper = true)
	public static class EbMSAdminPropertiesFormData extends EbMSCorePropertiesFormData
	{
		private static final long serialVersionUID = 1L;
		@NonNull
		ConsolePropertiesFormData consoleProperties = new ConsolePropertiesFormData();
		@NonNull
		CorePropertiesFormData coreProperties = new CorePropertiesFormData();
		@NonNull
		ServicePropertiesFormData serviceProperties = new ServicePropertiesFormData();
	}
}
