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

import java.util.Arrays;
import java.util.List;

import nl.clockwork.ebms.admin.web.BootstrapFormComponentFeedbackBorder;
import nl.clockwork.ebms.admin.web.LocalizedStringResource;
import nl.clockwork.ebms.admin.web.PasswordTextField;
import nl.clockwork.ebms.admin.web.TextField;
import nl.clockwork.ebms.admin.web.configuration.Constants.JdbcDriver;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.util.io.IClusterable;

public class JdbcPropertiesFormPanel extends Panel
{
	private static final long serialVersionUID = 1L;
	protected transient Log logger = LogFactory.getLog(this.getClass());

	public JdbcPropertiesFormPanel(String id, final IModel<JdbcPropertiesFormModel> model)
	{
		super(id,model);
		add(new JdbcPropertiesForm("form",model));
	}

	public class JdbcPropertiesForm extends Form<JdbcPropertiesFormModel>
	{
		private static final long serialVersionUID = 1L;

		public JdbcPropertiesForm(String id, final IModel<JdbcPropertiesFormModel> model)
		{
			super(id,new CompoundPropertyModel<JdbcPropertiesFormModel>(model));
			add(new BootstrapFormComponentFeedbackBorder("driverFeedback",createDriverChoice("driver",model)));
			add(new BootstrapFormComponentFeedbackBorder("hostFeedback",createHostsField("host")));
			add(new BootstrapFormComponentFeedbackBorder("portFeedback",createPortField("port")));
			add(new BootstrapFormComponentFeedbackBorder("databaseFeedback",createDatabaseField("database")));
			add(new TextField<String>("url",new LocalizedStringResource("lbl.url",JdbcPropertiesFormPanel.this)).setOutputMarkupId(true).setEnabled(false));
			add(createTestButton("test",model));
			add(new BootstrapFormComponentFeedbackBorder("usernameFeedback",new TextField<String>("username",new LocalizedStringResource("lbl.username",JdbcPropertiesFormPanel.this)).setRequired(true)));
			add(new BootstrapFormComponentFeedbackBorder("passwordFeedback",new PasswordTextField("password",new LocalizedStringResource("lbl.password",JdbcPropertiesFormPanel.this)).setResetPassword(false).setRequired(false)));
		}

		private DropDownChoice<JdbcDriver> createDriverChoice(String id, final IModel<JdbcPropertiesFormModel> model)
		{
			DropDownChoice<JdbcDriver> result = new DropDownChoice<JdbcDriver>(id,new PropertyModel<List<JdbcDriver>>(model.getObject(),"drivers"))
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.driver",JdbcPropertiesFormPanel.this));
				}
			};
			result.setRequired(true);
			result.add(new OnChangeAjaxBehavior()
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					target.add(getUrlComponent());
				}
			});
			return result;
		}

		private TextField<String> createHostsField(String id)
		{
			TextField<String> result = new TextField<String>(id,new LocalizedStringResource("lbl.host",JdbcPropertiesFormPanel.this));
			result.setRequired(true);
			result.add(new OnChangeAjaxBehavior()
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					target.add(getUrlComponent());
				}
			});
			return result;
		}

		private TextField<Integer> createPortField(String id)
		{
			TextField<Integer> result = new TextField<Integer>(id,new LocalizedStringResource("lbl.port",JdbcPropertiesFormPanel.this));
			result.add(new OnChangeAjaxBehavior()
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					target.add(getUrlComponent());
				}
			});
			return result;
		}

		private TextField<String> createDatabaseField(String id)
		{
			TextField<String> result = new TextField<String>(id,new LocalizedStringResource("lbl.database",JdbcPropertiesFormPanel.this));
			result.setRequired(true);
			result.add(new OnChangeAjaxBehavior()
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					target.add(getUrlComponent());
				}
			});
			return result;
		}

		private Button createTestButton(String id, final IModel<JdbcPropertiesFormModel> model)
		{
			Button result = new Button(id,new ResourceModel("cmd.test"))
			{
				private static final long serialVersionUID = 1L;

				@Override
				public void onSubmit()
				{
					try
					{
						JdbcPropertiesFormModel m = model.getObject();
						Utils.testJdbcConnection(m.getDriver().getDriverClassName(),m.getUrl(),m.getUsername(),m.getPassword());
						info(JdbcPropertiesForm.this.getString("test.ok"));
					}
					catch (Exception e)
					{
						logger .error("",e);
						error(new StringResourceModel("test.nok",JdbcPropertiesForm.this,Model.of(e)).getString());
					}
				}
			};
			return result;
		}

		private Component getUrlComponent()
		{
			return this.get("url");
		}
	}
	
	public static class JdbcPropertiesFormModel extends JdbcURL implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		private JdbcDriver driver = JdbcDriver.HSQLDB;
		private String username = "sa";
		private String password = null;

		public List<JdbcDriver> getDrivers()
		{
			return Arrays.asList(JdbcDriver.values());
		}
		public JdbcDriver getDriver()
		{
			return driver;
		}
		public void setDriver(JdbcDriver driver)
		{
			this.driver = driver;
		}
		public String getUrl()
		{
			//return driver.createJdbcURL(getHost(),getPort(),getDatabase());
			return JdbcDriver.createJdbcURL(driver.getUrlExpr(),getHost(),getPort(),getDatabase());
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
	}
}
