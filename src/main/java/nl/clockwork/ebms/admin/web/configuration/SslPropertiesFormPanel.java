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

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;
import nl.clockwork.ebms.admin.web.AjaxFormComponentUpdatingBehavior;
import nl.clockwork.ebms.admin.web.Supplier;
import nl.clockwork.ebms.admin.web.WebMarkupContainer;
import nl.clockwork.ebms.admin.web.configuration.JavaKeyStorePropertiesFormPanel.JavaKeyStorePropertiesFormModel;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SslPropertiesFormPanel extends Panel
{
	private static final long serialVersionUID = 1L;
	Supplier<Boolean> isVisible;

	@Builder
	public SslPropertiesFormPanel(String id, final IModel<SslPropertiesFormModel> model, boolean enableSslOverridePropeties, Supplier<Boolean> isVisible)
	{
		super(id,model);
		this.isVisible = isVisible == null ? () -> super.isVisible() : isVisible;
		add(new SslPropertiesForm("form",model,enableSslOverridePropeties));
	}

	@Override
	public boolean isVisible()
	{
		return isVisible.get();
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
			add(createClientCertificateAuthenticationContainer("clientCertificateAuthenticationContainer"));
			add(new KeystorePropertiesFormPanel("keystoreProperties",new PropertyModel<>(getModelObject(),"keystoreProperties")));
			add(new ClientKeystorePropertiesFormPanel("clientKeystoreProperties",new PropertyModel<>(getModelObject(),"clientKeystoreProperties")));
			add(new TruststorePropertiesFormPanel("truststoreProperties",new PropertyModel<>(getModelObject(),"truststoreProperties")));
			add(new CheckBox("verifyHostnames").setLabel(new ResourceModel("lbl.verifyHostnames")));
		}

		private WebMarkupContainer createOverrideDefaultProtocolsContainer(String id, boolean enableSslOverridePropeties)
		{
			val result = new WebMarkupContainer(id);
			result.setVisible(enableSslOverridePropeties);
			val checkBox = new CheckBox("overrideDefaultProtocols");
			checkBox.setLabel(new ResourceModel("lbl.overrideDefaultProtocols"));
			checkBox.add(AjaxFormComponentUpdatingBehavior.builder()
					.event("change")
					.onUpdate(t -> t.add(SslPropertiesForm.this))
					.build());
			result.add(checkBox);
			return result;
		}

		private WebMarkupContainer createEnabledProtocolsContainer(String id, final boolean enableSslOverridePropeties)
		{
			val result = WebMarkupContainer.builder()
					.id(id)
					.isVisible(() -> enableSslOverridePropeties && getModelObject().isOverrideDefaultProtocols())
					.build();
			result.add(
					new CheckBoxMultipleChoice<String>("enabledProtocols",getModelObject().getSupportedProtocols())
					.setSuffix("<br/>")
					.setLabel(new ResourceModel("lbl.enabledProtocols"))
			);
			return result;
		}

		private WebMarkupContainer createOverrideDefaultCipherSuitesContainer(String id, boolean enableSslOverridePropeties)
		{
			val result = new WebMarkupContainer(id);
			result.setVisible(enableSslOverridePropeties);
			val checkBox = new CheckBox("overrideDefaultCipherSuites");
			checkBox.setLabel(new ResourceModel("lbl.overrideDefaultCipherSuites"));
			checkBox.add(AjaxFormComponentUpdatingBehavior.builder()
					.event("change")
					.onUpdate(t -> t.add(SslPropertiesForm.this))
					.build());
			result.add(checkBox);
			return result;
		}

		private WebMarkupContainer createEnabledCipherSuitesContainer(String id, final boolean enableSslOverridePropeties)
		{
			val result = WebMarkupContainer.builder()
					.id(id)
					.isVisible(() -> enableSslOverridePropeties && getModelObject().isOverrideDefaultCipherSuites())
					.build();
			result.add(new CheckBoxMultipleChoice<String>("enabledCipherSuites",getModelObject().getSupportedCipherSuites())
					.setSuffix("<br/>")
					.setLabel(new ResourceModel("lbl.enabledCipherSuites"))
			);
			return result;
		}

		private FormComponent<Boolean> createClientAuthenticationRequiredCheckBox(String id)
		{
			val result = new CheckBox(id);
			result.setLabel(new ResourceModel("lbl.requireClientAuthentication"));
			result.add(AjaxFormComponentUpdatingBehavior.builder()
					.event("change")
					.onUpdate(t -> t.add(SslPropertiesForm.this))
					.build());
			return result;
		}

		private WebMarkupContainer createClientCertificateAuthenticationContainer(String id)
		{
			val result = WebMarkupContainer.builder()
					.id(id)
					.isVisible(() -> getModelObject().isRequireClientAuthentication())
					.build();
			result.add(new CheckBox("clientCertificateAuthentication").setLabel(new ResourceModel("lbl.clientCertificateAuthentication")));
			return result;
		}
	}

	@Data
	@FieldDefaults(level = AccessLevel.PRIVATE)
	@NoArgsConstructor
	public static class SslPropertiesFormModel implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		boolean overrideDefaultProtocols = true;
		final List<String> supportedProtocols = Arrays.asList(Utils.getSupportedSSLProtocols());
		@NonNull
		List<String> enabledProtocols = Arrays.asList("TLSv1.2");
		boolean overrideDefaultCipherSuites = false;
		final List<String> supportedCipherSuites = Arrays.asList(Utils.getSupportedSSLCipherSuites());
		@NonNull
		List<String> enabledCipherSuites = new ArrayList<>();
		boolean requireClientAuthentication = true;
		boolean verifyHostnames = false;
		boolean clientCertificateAuthentication = true;
		@NonNull
		JavaKeyStorePropertiesFormModel keystoreProperties = new JavaKeyStorePropertiesFormModel();
		@NonNull
		JavaKeyStorePropertiesFormModel truststoreProperties = new JavaKeyStorePropertiesFormModel();
		@NonNull
		JavaKeyStorePropertiesFormModel clientKeystoreProperties = new JavaKeyStorePropertiesFormModel();
	}
}
