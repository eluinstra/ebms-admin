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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.clockwork.ebms.admin.web.WebMarkupContainer;
import nl.clockwork.ebms.admin.web.configuration.JavaKeyStorePropertiesFormPanel.JavaKeyStorePropertiesFormModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.io.IClusterable;

public class SslPropertiesFormPanel extends Panel
{
	private static final long serialVersionUID = 1L;
	protected transient Log logger = LogFactory.getLog(this.getClass());

	public SslPropertiesFormPanel(String id, final IModel<SslPropertiesFormModel> model, boolean enableSslOverridePropeties)
	{
		super(id,model);
		add(new SslPropertiesForm("form",model,enableSslOverridePropeties));
	}

	public class SslPropertiesForm extends Form<SslPropertiesFormModel>
	{
		private static final long serialVersionUID = 1L;

		public SslPropertiesForm(String id, final IModel<SslPropertiesFormModel> model, boolean enableSslOverridePropeties)
		{
			super(id,new CompoundPropertyModel<>(model));
			add(createOverrideDefaultProtocolsContainer("overrideDefaultProtocolsContainer",enableSslOverridePropeties));
			add(createEnabledProtocolsContainer("enabledProtocolsContainer",enableSslOverridePropeties));
			add(createOverrideDefaultCipherSuitesContainer("overrideDefaultCipherSuitesContainer",enableSslOverridePropeties));
			add(createEnabledCipherSuitesContainer("enabledCipherSuitesContainer",enableSslOverridePropeties));
			add(createClientAuthenticationRequiredCheckBox("requireClientAuthentication"));
			add(new CheckBox("verifyHostnames").setLabel(new ResourceModel("lbl.verifyHostnames")));
			add(new CheckBox("validate").setLabel(new ResourceModel("lbl.validate")));
			add(new KeystorePropertiesFormPanel("keystoreProperties",new PropertyModel<>(getModelObject(),"keystoreProperties")));
			add(createClientKeystorePropertiesFormPanel("clientKeystoreProperties"));
			add(new TruststorePropertiesFormPanel("truststoreProperties",new PropertyModel<>(getModelObject(),"truststoreProperties")));
		}

		private WebMarkupContainer createOverrideDefaultProtocolsContainer(String id, boolean enableSslOverridePropeties)
		{
			WebMarkupContainer result = new WebMarkupContainer(id);
			result.setVisible(enableSslOverridePropeties);
			CheckBox checkBox = new CheckBox("overrideDefaultProtocols");
			checkBox.setLabel(new ResourceModel("lbl.overrideDefaultProtocols"));
			checkBox.add(new AjaxFormComponentUpdatingBehavior("change")
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					target.add(SslPropertiesForm.this);
				}
			});
			result.add(checkBox);
			return result;
		}

		private WebMarkupContainer createEnabledProtocolsContainer(String id, final boolean enableSslOverridePropeties)
		{
			WebMarkupContainer result = new WebMarkupContainer(id)
			{
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isVisible()
				{
					return enableSslOverridePropeties && getModelObject().isOverrideDefaultProtocols();
				}
			};
			result.add(
				new CheckBoxMultipleChoice<String>("enabledProtocols",getModelObject().getSupportedProtocols()).setLabel(new ResourceModel("lbl.enabledProtocols"))
			);
			return result;
		}

		private WebMarkupContainer createOverrideDefaultCipherSuitesContainer(String id, boolean enableSslOverridePropeties)
		{
			WebMarkupContainer result = new WebMarkupContainer(id);
			result.setVisible(enableSslOverridePropeties);
			CheckBox checkBox = new CheckBox("overrideDefaultCipherSuites");
			checkBox.setLabel(new ResourceModel("lbl.overrideDefaultCipherSuites"));
			checkBox.add(new AjaxFormComponentUpdatingBehavior("change")
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					target.add(SslPropertiesForm.this);
				}
			});
			result.add(checkBox);
			return result;
		}

		private WebMarkupContainer createEnabledCipherSuitesContainer(String id, final boolean enableSslOverridePropeties)
		{
			WebMarkupContainer result = new WebMarkupContainer(id)
			{
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isVisible()
				{
					return enableSslOverridePropeties && getModelObject().isOverrideDefaultCipherSuites();
				}
			};
			result.add(
				new CheckBoxMultipleChoice<String>("enabledCipherSuites",getModelObject().getSupportedCipherSuites()).setLabel(new ResourceModel("lbl.enabledCipherSuites"))
			);
			return result;
		}

		private FormComponent<Boolean> createClientAuthenticationRequiredCheckBox(String id)
		{
			CheckBox result = new CheckBox(id);
			result.setLabel(new ResourceModel("lbl.requireClientAuthentication"));
			result.add(new AjaxFormComponentUpdatingBehavior("change")
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					target.add(SslPropertiesForm.this);
				}
			});
			return result;
		}

		private ClientKeystorePropertiesFormPanel createClientKeystorePropertiesFormPanel(String id)
		{
			return new ClientKeystorePropertiesFormPanel(id,new PropertyModel<>(getModelObject(),"clientKeystoreProperties"))
			{
				private static final long serialVersionUID = 1L;
				
				@Override
				public boolean isVisible()
				{
					return getModelObject().getRequireClientAuthentication();
				}
			};
		}
	}

	public static class SslPropertiesFormModel implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		private boolean overrideDefaultProtocols = false;
		private List<String> supportedProtocols = Arrays.asList(Utils.getSupportedSSLProtocols());
		private List<String> enabledProtocols = new ArrayList<>();
		private boolean overrideDefaultCipherSuites = false;
		private List<String> supportedCipherSuites = Arrays.asList(Utils.getSupportedSSLCipherSuites());
		private List<String> enabledCipherSuites = new ArrayList<>();
		private boolean requireClientAuthentication = true;
		private boolean verifyHostnames = false;
		private boolean validate = true;
		private JavaKeyStorePropertiesFormModel keystoreProperties = new JavaKeyStorePropertiesFormModel();
		private JavaKeyStorePropertiesFormModel truststoreProperties = new JavaKeyStorePropertiesFormModel();
		private JavaKeyStorePropertiesFormModel clientKeystoreProperties = new JavaKeyStorePropertiesFormModel();

		public boolean isOverrideDefaultProtocols()
		{
			return overrideDefaultProtocols;
		}
		public void setOverrideDefaultProtocols(boolean overrideDefaultProtocols)
		{
			this.overrideDefaultProtocols = overrideDefaultProtocols;
		}
		public List<String> getSupportedProtocols()
		{
			return supportedProtocols;
		}
		public List<String> getEnabledProtocols()
		{
			return enabledProtocols;
		}
		public void setEnabledProtocols(List<String> enabledProtocols)
		{
			this.enabledProtocols = enabledProtocols;
		}
		public boolean isOverrideDefaultCipherSuites()
		{
			return overrideDefaultCipherSuites;
		}
		public void setOverrideDefaultCipherSuites(boolean overrideDefaultCipherSuites)
		{
			this.overrideDefaultCipherSuites = overrideDefaultCipherSuites;
		}
		public List<String> getSupportedCipherSuites()
		{
			return supportedCipherSuites;
		}
		public void setEnabledCipherSuites(List<String> enabledCipherSuites)
		{
			this.enabledCipherSuites = enabledCipherSuites;
		}
		public List<String> getEnabledCipherSuites()
		{
			return enabledCipherSuites;
		}
		public boolean getRequireClientAuthentication()
		{
			return requireClientAuthentication;
		}
		public void setRequireClientAuthentication(boolean requireClientAuthentication)
		{
			this.requireClientAuthentication = requireClientAuthentication;
		}
		public boolean getVerifyHostnames()
		{
			return verifyHostnames;
		}
		public void setVerifyHostnames(boolean verifyHostnames)
		{
			this.verifyHostnames = verifyHostnames;
		}
		public boolean getValidate()
		{
			return validate;
		}
		public void setValidate(boolean validate)
		{
			this.validate = validate;
		}
		public JavaKeyStorePropertiesFormModel getKeystoreProperties()
		{
			return keystoreProperties;
		}
		public void setKeystoreProperties(JavaKeyStorePropertiesFormModel keystoreProperties)
		{
			this.keystoreProperties = keystoreProperties;
		}
		public JavaKeyStorePropertiesFormModel getTruststoreProperties()
		{
			return truststoreProperties;
		}
		public void setTruststoreProperties(JavaKeyStorePropertiesFormModel truststoreProperties)
		{
			this.truststoreProperties = truststoreProperties;
		}
		public JavaKeyStorePropertiesFormModel getClientKeystoreProperties()
		{
			return clientKeystoreProperties;
		}
		public void setClientKeystoreProperties(JavaKeyStorePropertiesFormModel clientKeystoreProperties)
		{
			this.clientKeystoreProperties = clientKeystoreProperties;
		}
	}
}
