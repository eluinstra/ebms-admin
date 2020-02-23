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

import nl.clockwork.ebms.admin.Utils;
import nl.clockwork.ebms.admin.web.BootstrapFormComponentFeedbackBorder;
import nl.clockwork.ebms.admin.web.configuration.ProxyPropertiesFormPanel.ProxyPropertiesFormModel;
import nl.clockwork.ebms.admin.web.configuration.SslPropertiesFormPanel.SslPropertiesFormModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.convert.converter.AbstractConverter;
import org.apache.wicket.util.io.IClusterable;

public class HttpPropertiesFormPanel extends Panel
{
	private static final long serialVersionUID = 1L;
	protected transient Log logger = LogFactory.getLog(this.getClass());

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
			TextField<String> result = new TextField<>(id);
			result.setLabel(new ResourceModel("lbl.host"));
			result.add(new OnChangeAjaxBehavior()
	    {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					target.add(HttpPropertiesForm.this.get("url"));
				}
	    });
			result.setRequired(true);
			return result;
		}

		private TextField<Integer> createPortField(String id)
		{
			TextField<Integer> result = new TextField<>(id);
			result.setLabel(new ResourceModel("lbl.port"));
			result.add(new OnChangeAjaxBehavior()
	    {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					target.add(HttpPropertiesForm.this.get("url"));
				}
	    });
			return result;
		}

		private TextField<String> createPathField(String id)
		{
			TextField<String> result = new TextField<String>(id)
			{
				private static final long serialVersionUID = 1L;

				@SuppressWarnings("unchecked")
				@Override
				public <C> IConverter<C> getConverter(Class<C> type)
				{
					return (IConverter<C>)new PathConverter();
				}
			};
			result.setLabel(new ResourceModel("lbl.path"));
			result.setRequired(true);
			result.add(new OnChangeAjaxBehavior()
	    {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					target.add(HttpPropertiesForm.this.get("url"));
				}
	    });
			return result;
		}

		private CheckBox CreateSslCheckBox(String id)
		{
			CheckBox result = new CheckBox(id);
			result.setLabel(new ResourceModel("lbl.ssl"));
			result.add(new AjaxFormComponentUpdatingBehavior("change")
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					target.add(HttpPropertiesForm.this);
				}
			});
			return result;
		}

		private SslPropertiesFormPanel createSslPropertiesPanel(String id, boolean enableSslOverridePropeties)
		{
			SslPropertiesFormPanel result = new SslPropertiesFormPanel(id,new PropertyModel<>(getModelObject(),"sslProperties"),enableSslOverridePropeties)
			{
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isVisible()
				{
					return getModelObject().getSsl();
				}
			};
			return result;
		}

		private CheckBox createProxyCheckBox(String id)
		{
			CheckBox result = new CheckBox(id);
			result.setLabel(new ResourceModel("lbl.proxy"));
			result.add(new AjaxFormComponentUpdatingBehavior("change")
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					target.add(HttpPropertiesForm.this);
				}
			});
			return result;
		}

		private ProxyPropertiesFormPanel createProxyPropertiesPanel(String id)
		{
			ProxyPropertiesFormPanel result = new ProxyPropertiesFormPanel(id,new PropertyModel<>(getModelObject(),"proxyProperties"))
			{
				private static final long serialVersionUID = 1L;
				
				@Override
				public boolean isVisible()
				{
					return getModelObject().getProxy();
				}
			};
			return result;
		}

	}

	public static class HttpPropertiesFormModel implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		private String host = "0.0.0.0";
		private Integer port = 8888;
		private String path = "/ebms";
		private boolean chunkedStreamingMode = true;
		private boolean base64Writer = false;
		private boolean ssl = true;
		private SslPropertiesFormModel sslProperties = new SslPropertiesFormModel();
		private boolean proxy;
		private ProxyPropertiesFormModel proxyProperties = new ProxyPropertiesFormModel();

		public String getProtocol()
		{
			return ssl ? "https://" : "http://";
		}
		public String getHost()
		{
			return host;
		}
		public void setHost(String host)
		{
			this.host = host;
		}
		public Integer getPort()
		{
			return port;
		}
		public void setPort(Integer port)
		{
			this.port = port;
		}
		public String getPath()
		{
			return path;
		}
		public void setPath(String path)
		{
			this.path = path;
		}
		public String getUrl()
		{
			return getProtocol() + Utils.getHost(host) + (port == null ? "" : ":" + port.toString()) + path;
		}
		public boolean isChunkedStreamingMode()
		{
			return chunkedStreamingMode;
		}
		public void setChunkedStreamingMode(boolean chunkedStreamingMode)
		{
			this.chunkedStreamingMode = chunkedStreamingMode;
		}
		public boolean isBase64Writer()
		{
			return base64Writer;
		}
		public void setBase64Writer(boolean base64Writer)
		{
			this.base64Writer = base64Writer;
		}
		public boolean getSsl()
		{
			return ssl;
		}
		public void setSsl(boolean ssl)
		{
			this.ssl = ssl;
		}
		public SslPropertiesFormModel getSslProperties()
		{
			return sslProperties;
		}
		public void setSslProperties(SslPropertiesFormModel sslProperties)
		{
			this.sslProperties = sslProperties;
		}
		public boolean getProxy()
		{
			return proxy;
		}
		public void setProxy(boolean proxy)
		{
			this.proxy = proxy;
		}
		public ProxyPropertiesFormModel getProxyProperties()
		{
			return proxyProperties;
		}
		public void setProxyProperties(ProxyPropertiesFormModel proxyProperties)
		{
			this.proxyProperties = proxyProperties;
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
