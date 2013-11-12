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
import nl.clockwork.ebms.admin.web.ResetButton;
import nl.clockwork.ebms.admin.web.configuration.ConsolePropertiesFormPanel.ConsolePropertiesFormModel;
import nl.clockwork.ebms.admin.web.configuration.Constants.PropertiesType;
import nl.clockwork.ebms.admin.web.configuration.HttpPropertiesFormPanel.HttpPropertiesFormModel;
import nl.clockwork.ebms.admin.web.configuration.JdbcPropertiesFormPanel.JdbcPropertiesFormModel;
import nl.clockwork.ebms.admin.web.configuration.ServicePropertiesFormPanel.ServicePropertiesFormModel;
import nl.clockwork.ebms.admin.web.configuration.SignaturePropertiesFormPanel.SignaturePropertiesFormModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.io.IClusterable;

public class EbMSAdminPropertiesPage extends BasePage
{
	private static final long serialVersionUID = 1L;
	protected transient Log logger = LogFactory.getLog(this.getClass());
	@SpringBean(name="propertyConfigurer")
	private PropertyPlaceholderConfigurer propertyPlaceholderConfigurer;
	private PropertiesType propertiesType;

	public EbMSAdminPropertiesPage() throws IOException
	{
		this(new EbMSAdminPropertiesFormModel());
	}
	public EbMSAdminPropertiesPage(EbMSAdminPropertiesFormModel ebMSAdminPropertiesFormModel) throws IOException
	{
		propertiesType = PropertiesType.getPropertiesType(propertyPlaceholderConfigurer.getOverridePropertiesFile().getFilename());
		add(new BootstrapFeedbackPanel("feedback"));
		add(new EbMSAdminPropertiesForm("form",ebMSAdminPropertiesFormModel));
	}
	
	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("ebMSAdminProperties",this);
	}
	
	public class EbMSAdminPropertiesForm extends Form<EbMSAdminPropertiesFormModel>
	{
		private static final long serialVersionUID = 1L;

		public EbMSAdminPropertiesForm(String id, EbMSAdminPropertiesFormModel model)
		{
			super(id,new CompoundPropertyModel<EbMSAdminPropertiesFormModel>(model));
			setOutputMarkupId(true);
			
			List<Panel> components = new ArrayList<Panel>();
			components.add(new ConsolePropertiesFormPanel("component",new PropertyModel<ConsolePropertiesFormModel>(getModelObject(),"consoleProperties")));
			if (PropertiesType.EBMS_ADMIN.equals(propertiesType))
				components.add(new ServicePropertiesFormPanel("component",new PropertyModel<ServicePropertiesFormModel>(getModelObject(),"serviceProperties")));
			if (PropertiesType.EBMS_ADMIN_EMBEDDED.equals(propertiesType))
			{
				components.add(new HttpPropertiesFormPanel("component",new PropertyModel<HttpPropertiesFormModel>(getModelObject(),"httpProperties")));
				components.add(new SignaturePropertiesFormPanel("component",new PropertyModel<SignaturePropertiesFormModel>(getModelObject(),"signatureProperties")));
			}
			components.add(new JdbcPropertiesFormPanel("component",new PropertyModel<JdbcPropertiesFormModel>(getModelObject(),"jdbcProperties")));
			add(new ListView<Panel>("components",components)
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void populateItem(ListItem<Panel> item)
				{
					item.add((Panel)item.getModelObject()); 
				}
			}.setReuseItems(true));
			
			
			add(new Button("validate")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public void onSubmit()
				{
					info(EbMSAdminPropertiesPage.this.getString("validate.ok"));
				}
			});
			add(new DownloadEbMSAdminPropertiesButton("download",new ResourceModel("cmd.download"),getModelObject(),propertiesType));
			add(new LoadEbMSAdminPropertiesButton("load",new ResourceModel("cmd.load"),getModelObject(),propertiesType));
			add(new SaveEbMSAdminPropertiesButton("save",new ResourceModel("cmd.save"),getModelObject(),propertiesType));
			add(new ResetButton("reset",new ResourceModel("cmd.reset"),EbMSAdminPropertiesPage.class));
		}
	}

	public static class EbMSAdminPropertiesFormModel implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		private ConsolePropertiesFormModel consoleProperties = new ConsolePropertiesFormModel();
		private ServicePropertiesFormModel serviceProperties = new ServicePropertiesFormModel();
		private HttpPropertiesFormModel httpProperties = new HttpPropertiesFormModel();
		private SignaturePropertiesFormModel signatureProperties = new SignaturePropertiesFormModel();
		private JdbcPropertiesFormModel jdbcProperties = new JdbcPropertiesFormModel();
		
		public ConsolePropertiesFormModel getConsoleProperties()
		{
			return consoleProperties;
		}
		public void setConsoleProperties(ConsolePropertiesFormModel consoleProperties)
		{
			this.consoleProperties = consoleProperties;
		}
		public ServicePropertiesFormModel getServiceProperties()
		{
			return serviceProperties;
		}
		public void setServiceProperties(ServicePropertiesFormModel serviceProperties)
		{
			this.serviceProperties = serviceProperties;
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
