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

import java.util.Locale;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.convert.converter.AbstractConverter;
import org.apache.wicket.util.io.IClusterable;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;
import nl.clockwork.ebms.admin.Utils;
import nl.clockwork.ebms.admin.web.AjaxFormComponentUpdatingBehavior;
import nl.clockwork.ebms.admin.web.BootstrapFormComponentFeedbackBorder;
import nl.clockwork.ebms.admin.web.OnChangeAjaxBehavior;
import nl.clockwork.ebms.admin.web.TextField;
import nl.clockwork.ebms.admin.web.configuration.ProxyPropertiesFormPanel.ProxyPropertiesFormModel;
import nl.clockwork.ebms.admin.web.configuration.SslPropertiesFormPanel.SslPropertiesFormModel;

public class HttpPropertiesFormPanel extends Panel
{
	private static final long serialVersionUID = 1L;

	public HttpPropertiesFormPanel(String id, final IModel<HttpPropertiesFormModel> model, boolean enableSslOverridePropeties)
	{
		super(id,model);
		add(new HttpPropertiesForm("form",model,enableSslOverridePropeties));
	}

	public class HttpPropertiesForm extends Form<HttpPropertiesFormModel>
	{
		private static final long serialVersionUID = 1L;

		public HttpPropertiesForm(String id, final IModel<HttpPropertiesFormModel> model, boolean enableSslOverridePropeties)
		{
			super(id,new CompoundPropertyModel<>(model));
			add(new BootstrapFormComponentFeedbackBorder("hostFeedback",createHostField("host")).add(new Label("protocol")));
			add(new BootstrapFormComponentFeedbackBorder("portFeedback",createPortField("port")));
			add(new BootstrapFormComponentFeedbackBorder("pathFeedback",createPathField("path")));
			add(new TextField<String>("url").setLabel(new ResourceModel("lbl.url")).setOutputMarkupId(true).setEnabled(false));
			add(new CheckBox("chunkedStreamingMode").setLabel(new ResourceModel("lbl.chunkedStreamingMode")));
			add(new CheckBox("base64Writer").setLabel(new ResourceModel("lbl.base64Writer")));
			add(CreateSslCheckBox("ssl"));
			add(createSslPropertiesPanel("sslProperties",enableSslOverridePropeties));
			add(createProxyCheckBox("proxy"));
			add(createProxyPropertiesPanel("proxyProperties"));
		}

		private FormComponent<String> createHostField(String id)
		{
			val result = new TextField<String>(id);
			result.setLabel(new ResourceModel("lbl.host"));
			result.add(OnChangeAjaxBehavior.builder()
					.onUpdate(t -> t.add(HttpPropertiesForm.this.get("url")))
					.build());
			result.setRequired(true);
			return result;
		}

		private TextField<Integer> createPortField(String id)
		{
			val result = new TextField<Integer>(id);
			result.setLabel(new ResourceModel("lbl.port"));
			result.add(OnChangeAjaxBehavior.builder()
					.onUpdate(t -> t.add(HttpPropertiesForm.this.get("url")))
					.build());
			return result;
		}

		private TextField<String> createPathField(String id)
		{
			val result = TextField.<String>builder()
					.id(id)
					.getConverter(t -> new PathConverter())
					.build();
			result.setLabel(new ResourceModel("lbl.path"));
			result.setRequired(true);
			result.add(OnChangeAjaxBehavior.builder()
					.onUpdate(t -> t.add(HttpPropertiesForm.this.get("url")))
					.build());
			return result;
		}

		private CheckBox CreateSslCheckBox(String id)
		{
			val result = new CheckBox(id);
			result.setLabel(new ResourceModel("lbl.ssl"));
			result.add(AjaxFormComponentUpdatingBehavior.builder()
					.event("change")
					.onUpdate(t -> t.add(HttpPropertiesForm.this))
					.build());
			return result;
		}

		private SslPropertiesFormPanel createSslPropertiesPanel(String id, boolean enableSslOverridePropeties)
		{
			val result = SslPropertiesFormPanel.builder()
					.id(id)
					.model(new PropertyModel<>(getModelObject(),"sslProperties"))
					.enableSslOverridePropeties(enableSslOverridePropeties)
					.isVisible(() -> getModelObject().isSsl())
					.build();
			return result;
		}

		private CheckBox createProxyCheckBox(String id)
		{
			val result = new CheckBox(id);
			result.setLabel(new ResourceModel("lbl.proxy"));
			result.add(AjaxFormComponentUpdatingBehavior.builder()
					.event("change")
					.onUpdate(t -> t.add(HttpPropertiesForm.this))
					.build());
			return result;
		}

		private ProxyPropertiesFormPanel createProxyPropertiesPanel(String id)
		{
			val result = ProxyPropertiesFormPanel.builder()
					.id(id)
					.model(new PropertyModel<>(getModelObject(),"proxyProperties"))
					.isVisible(() -> getModelObject().isProxy())
					.build();
			return result;
		}

	}

	@Data
	@FieldDefaults(level = AccessLevel.PRIVATE)
	@NoArgsConstructor
	public static class HttpPropertiesFormModel implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		@NonNull
		String host = "0.0.0.0";
		@NonNull
		Integer port = 8888;
		//@NonNull
		String path = "/ebms";
		boolean chunkedStreamingMode = true;
		boolean base64Writer = false;
		boolean ssl = true;
		@NonNull
		SslPropertiesFormModel sslProperties = new SslPropertiesFormModel();
		boolean proxy;
		@NonNull
		ProxyPropertiesFormModel proxyProperties = new ProxyPropertiesFormModel();

		public String getProtocol()
		{
			return ssl ? "https://" : "http://";
		}
		public String getUrl()
		{
			return getProtocol() + Utils.getHost(host) + (port == null ? "" : ":" + port.toString()) + path;
		}
	}
	
	public class PathConverter extends AbstractConverter<String>
	{
		private static final long serialVersionUID = 1L;

		@Override
		public String convertToObject(String value, Locale locale)
		{
			return "/" + value;
		}

		@Override
		public String convertToString(String value, Locale locale)
		{
			return value.substring(1);
		}

		@Override
		protected Class<String> getTargetType()
		{
			return String.class;
		}

	}
}
