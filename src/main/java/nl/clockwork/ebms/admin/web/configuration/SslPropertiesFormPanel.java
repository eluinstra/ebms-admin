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

import javax.net.ssl.SSLServerSocketFactory;

import nl.clockwork.ebms.admin.web.configuration.JavaKeyStorePropertiesFormPanel.JavaKeyStorePropertiesFormModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.io.IClusterable;

public class SslPropertiesFormPanel extends Panel
{
	private static final long serialVersionUID = 1L;
	protected transient Log logger = LogFactory.getLog(this.getClass());

	public SslPropertiesFormPanel(String id, final IModel<SslPropertiesFormModel> model)
	{
		super(id,model);
		add(new SslPropertiesForm("form",model));
	}

	public class SslPropertiesForm extends Form<SslPropertiesFormModel>
	{
		private static final long serialVersionUID = 1L;

		public SslPropertiesForm(String id, final IModel<SslPropertiesFormModel> model)
		{
			super(id,new CompoundPropertyModel<SslPropertiesFormModel>(model));

			CheckBoxMultipleChoice<String> allowedCipherSuites = new CheckBoxMultipleChoice<String>("allowedCipherSuites",getModelObject().getDefaultCipherSuites())
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.allowedCipherSuites",SslPropertiesForm.this));
				}
			};
			add(allowedCipherSuites);

			CheckBox requireClientAuthentication = new CheckBox("requireClientAuthentication")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.requireClientAuthentication",SslPropertiesForm.this));
				}
			};
			add(requireClientAuthentication);

			CheckBox verifyHostnames = new CheckBox("verifyHostnames")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.verifyHostnames",SslPropertiesForm.this));
				}
			};
			add(verifyHostnames);

			add(new KeystorePropertiesFormPanel("keystoreProperties",new PropertyModel<JavaKeyStorePropertiesFormModel>(getModelObject(),"keystoreProperties")));
			add(new TruststorePropertiesFormPanel("truststoreProperties",new PropertyModel<JavaKeyStorePropertiesFormModel>(getModelObject(),"truststoreProperties")));
		}
	}

	public static class SslPropertiesFormModel implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		private List<String> defaultCipherSuites = Arrays.asList(((SSLServerSocketFactory)SSLServerSocketFactory.getDefault()).getDefaultCipherSuites());
		private List<String> allowedCipherSuites = new ArrayList<String>(Arrays.asList(new String[]{"TLS_DHE_RSA_WITH_AES_128_CBC_SHA","TLS_RSA_WITH_AES_128_CBC_SHA"}));
		private boolean requireClientAuthentication = true;
		private boolean verifyHostnames = false;
		private JavaKeyStorePropertiesFormModel keystoreProperties = new JavaKeyStorePropertiesFormModel();
		private JavaKeyStorePropertiesFormModel truststoreProperties = new JavaKeyStorePropertiesFormModel();

		public List<String> getDefaultCipherSuites()
		{
			return defaultCipherSuites;
		}
		public void setAllowedCipherSuites(List<String> allowedCipherSuites)
		{
			this.allowedCipherSuites = allowedCipherSuites;
		}
		public List<String> getAllowedCipherSuites()
		{
			return allowedCipherSuites;
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
	}
}
