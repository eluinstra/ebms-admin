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

import nl.clockwork.ebms.admin.web.BootstrapFormComponentFeedbackBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.io.IClusterable;

public class ProxyPropertiesFormPanel extends Panel
{
	private static final long serialVersionUID = 1L;
	protected transient Log logger = LogFactory.getLog(this.getClass());

	public ProxyPropertiesFormPanel(String id, final IModel<ProxyPropertiesFormModel> model)
	{
		super(id,model);
		add(new ProxyPropertiesForm("form",model));
	}

	public class ProxyPropertiesForm extends Form<ProxyPropertiesFormModel>
	{
		private static final long serialVersionUID = 1L;

		public ProxyPropertiesForm(String id, final IModel<ProxyPropertiesFormModel> model)
		{
			super(id,new CompoundPropertyModel<ProxyPropertiesFormModel>(model));
			add(new BootstrapFormComponentFeedbackBorder("hostFeedback",new TextField<String>("host").setLabel(new ResourceModel("lbl.host")).setRequired(true)));
			add(new BootstrapFormComponentFeedbackBorder("portFeedback",new TextField<Integer>("port").setLabel(new ResourceModel("lbl.port"))));
			add(new TextField<String>("nonProxyHosts").setLabel(new ResourceModel("lbl.nonProxyHosts")));
			add(new TextField<String>("username").setLabel(new ResourceModel("lbl.username")));
			add(new PasswordTextField("password").setResetPassword(false).setLabel(new ResourceModel("lbl.password")).setRequired(false));
		}
	}

	public static class ProxyPropertiesFormModel implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		private String host;
		private Integer port;
		private String nonProxyHosts;
		private String username;
		private String password;

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
		public String getNonProxyHosts()
		{
			return nonProxyHosts;
		}
		public void setNonProxyHosts(String nonProxyHosts)
		{
			this.nonProxyHosts = nonProxyHosts;
		}
		public String getUsername()
		{
			return username;
		}
		public void setUsername(String username)
		{
			this.username = username;
		}
		public String getPassword()
		{
			return password;
		}
		public void setPassword(String password)
		{
			this.password = password;
		}
		public static long getSerialversionuid()
		{
			return serialVersionUID;
		}
	}
}
