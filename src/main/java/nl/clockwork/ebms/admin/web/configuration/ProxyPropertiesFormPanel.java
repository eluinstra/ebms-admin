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

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.io.IClusterable;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import nl.clockwork.ebms.admin.web.BootstrapFormComponentFeedbackBorder;
import nl.clockwork.ebms.admin.web.Supplier;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProxyPropertiesFormPanel extends Panel
{
	private static final long serialVersionUID = 1L;
	Supplier<Boolean> isVisible;

	@Builder
	public ProxyPropertiesFormPanel(String id, final IModel<ProxyPropertiesFormData> model, Supplier<Boolean> isVisible)
	{
		super(id,model);
		this.isVisible = isVisible == null ? () -> super.isVisible() : isVisible;
		add(new ProxyPropertiesForm("form",model));
	}

	@Override
	public boolean isVisible()
	{
		return isVisible.get();
	}

	public class ProxyPropertiesForm extends Form<ProxyPropertiesFormData>
	{
		private static final long serialVersionUID = 1L;

		public ProxyPropertiesForm(String id, final IModel<ProxyPropertiesFormData> model)
		{
			super(id,new CompoundPropertyModel<>(model));
			add(new BootstrapFormComponentFeedbackBorder("hostFeedback",new TextField<String>("host").setLabel(new ResourceModel("lbl.host")).setRequired(true)));
			add(new BootstrapFormComponentFeedbackBorder("portFeedback",new TextField<Integer>("port").setLabel(new ResourceModel("lbl.port"))));
			add(new TextField<String>("nonProxyHosts").setLabel(new ResourceModel("lbl.nonProxyHosts")));
			add(new TextField<String>("username").setLabel(new ResourceModel("lbl.username")));
			add(new PasswordTextField("password").setResetPassword(false).setLabel(new ResourceModel("lbl.password")).setRequired(false));
		}
	}

	@Data
	@FieldDefaults(level = AccessLevel.PRIVATE)
	@NoArgsConstructor
	public static class ProxyPropertiesFormData implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		@NonNull
		String host;
		@NonNull
		Integer port;
		String nonProxyHosts;
		String username;
		String password;
	}
}
