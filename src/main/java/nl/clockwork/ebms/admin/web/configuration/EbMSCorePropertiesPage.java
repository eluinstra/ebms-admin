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
import java.util.List;

import nl.clockwork.ebms.admin.PropertyPlaceholderConfigurer;
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.BootstrapFeedbackPanel;
import nl.clockwork.ebms.admin.web.BootstrapPanelBorder;
import nl.clockwork.ebms.admin.web.ResetButton;
import nl.clockwork.ebms.admin.web.configuration.CorePropertiesFormPanel.CorePropertiesFormModel;
import nl.clockwork.ebms.admin.web.configuration.HttpPropertiesFormPanel.HttpPropertiesFormModel;
import nl.clockwork.ebms.admin.web.configuration.JdbcPropertiesFormPanel.JdbcPropertiesFormModel;
import nl.clockwork.ebms.admin.web.configuration.SignaturePropertiesFormPanel.SignaturePropertiesFormModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.io.IClusterable;

public class EbMSCorePropertiesPage extends BasePage
{
	private static final long serialVersionUID = 1L;
	protected transient Log logger = LogFactory.getLog(this.getClass());
	@SpringBean(name="propertyConfigurer")
	private PropertyPlaceholderConfigurer propertyPlaceholderConfigurer;

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
			super(id,new CompoundPropertyModel<EbMSCorePropertiesFormModel>(model));
			
			List<BootstrapPanelBorder> components = new ArrayList<BootstrapPanelBorder>();
			components.add(new BootstrapPanelBorder("panelBorder",EbMSCorePropertiesPage.this.getString("coreProperties"),new CorePropertiesFormPanel("component",new PropertyModel<CorePropertiesFormModel>(getModelObject(),"coreProperties"))));
			components.add(new BootstrapPanelBorder("panelBorder",EbMSCorePropertiesPage.this.getString("httpProperties"),new HttpPropertiesFormPanel("component",new PropertyModel<HttpPropertiesFormModel>(getModelObject(),"httpProperties"))));
			components.add(new BootstrapPanelBorder("panelBorder",EbMSCorePropertiesPage.this.getString("signatureProperties"),new SignaturePropertiesFormPanel("component",new PropertyModel<SignaturePropertiesFormModel>(getModelObject(),"signatureProperties"))));
			components.add(new BootstrapPanelBorder("panelBorder",EbMSCorePropertiesPage.this.getString("jdbcProperties"),new JdbcPropertiesFormPanel("component",new PropertyModel<JdbcPropertiesFormModel>(getModelObject(),"jdbcProperties"))));
			add(new ListView<BootstrapPanelBorder>("components",components)
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void populateItem(ListItem<BootstrapPanelBorder> item)
				{
					item.add((BootstrapPanelBorder)item.getModelObject()); 
				}
			}.setReuseItems(true));
			
			
			add(new Button("validate")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public void onSubmit()
				{
					info(EbMSCorePropertiesPage.this.getString("validate.ok"));
				}
			});
			add(new DownloadEbMSCorePropertiesButton("download",new ResourceModel("cmd.download"),getModelObject()));
			add(new ResetButton("reset",new ResourceModel("cmd.reset"),EbMSCorePropertiesPage.class));
		}
	}

	public static class EbMSCorePropertiesFormModel implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		private CorePropertiesFormModel coreProperties = new CorePropertiesFormModel();
		private HttpPropertiesFormModel httpProperties = new HttpPropertiesFormModel();
		private SignaturePropertiesFormModel signatureProperties = new SignaturePropertiesFormModel();
		private JdbcPropertiesFormModel jdbcProperties = new JdbcPropertiesFormModel();

		public CorePropertiesFormModel getCoreProperties()
		{
			return coreProperties;
		}
		public void setCoreProperties(CorePropertiesFormModel coreProperties)
		{
			this.coreProperties = coreProperties;
		}
		public HttpPropertiesFormModel getHttpProperties()
		{
			return httpProperties;
		}
		public void setHttpProperties(HttpPropertiesFormModel httpProperties)
		{
			this.httpProperties = httpProperties;
		}
		public SignaturePropertiesFormModel getSignatureProperties()
		{
			return signatureProperties;
		}
		public void setSignatureProperties(SignaturePropertiesFormModel signatureProperties)
		{
			this.signatureProperties = signatureProperties;
		}
		public JdbcPropertiesFormModel getJdbcProperties()
		{
			return jdbcProperties;
		}
		public void setJdbcProperties(JdbcPropertiesFormModel jdbcProperties)
		{
			this.jdbcProperties = jdbcProperties;
		}
	}
}
