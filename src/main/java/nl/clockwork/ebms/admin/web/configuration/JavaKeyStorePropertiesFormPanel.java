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
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.util.io.IClusterable;

public class JavaKeyStorePropertiesFormPanel extends Panel
{
	private static final long serialVersionUID = 1L;
	protected transient Log logger = LogFactory.getLog(this.getClass());
	private boolean required;

	public JavaKeyStorePropertiesFormPanel(String id, final IModel<JavaKeyStorePropertiesFormModel> model)
	{
		this(id,model,true);
	}

	public JavaKeyStorePropertiesFormPanel(String id, final IModel<JavaKeyStorePropertiesFormModel> model, boolean required)
	{
		super(id,model);
		this.required = required;
		add(new JavaKeyStorePropertiesForm("form",model));
	}

	public class JavaKeyStorePropertiesForm extends Form<JavaKeyStorePropertiesFormModel>
	{
		private static final long serialVersionUID = 1L;

		public JavaKeyStorePropertiesForm(String id, final IModel<JavaKeyStorePropertiesFormModel> model)
		{
			super(id,new CompoundPropertyModel<JavaKeyStorePropertiesFormModel>(model));

			TextField<String> uri = new TextField<String>("uri")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.uri",JavaKeyStorePropertiesForm.this));
				}
			};
			uri.setRequired(required);
			add(new BootstrapFormComponentFeedbackBorder("uriFeedback",uri));

			PasswordTextField password = new PasswordTextField("password")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.password",JavaKeyStorePropertiesForm.this));
				}
			};
			password.setRequired(required);
			password.setResetPassword(false);
			add(new BootstrapFormComponentFeedbackBorder("passwordFeedback",password));

			add(new Button("test",new ResourceModel("cmd.test"))
			{
				private static final long serialVersionUID = 1L;

				@Override
				public void onSubmit()
				{
					try
					{
						JavaKeyStorePropertiesFormModel m = model.getObject();
						Utils.testKeystore(m.getUri(),m.getPassword());
						info(JavaKeyStorePropertiesForm.this.getString("test.ok"));
					}
					catch (Exception e)
					{
						logger .error("",e);
						error(new StringResourceModel("test.nok",JavaKeyStorePropertiesForm.this,Model.of(e)).getString());
					}
				}
			});
		}
	}

	public static class JavaKeyStorePropertiesFormModel implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		private String uri = "keystore.jks";
		private String password = "password";

		public String getUri()
		{
			return uri;
		}
		public void setUri(String uri)
		{
			this.uri = uri;
		}
		public String getPassword()
		{
			return password;
		}
		public void setPassword(String password)
		{
			this.password = password;
		}
	}
}
