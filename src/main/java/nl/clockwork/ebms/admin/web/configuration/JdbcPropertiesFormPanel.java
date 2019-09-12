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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.util.io.IClusterable;

import nl.clockwork.ebms.admin.web.BootstrapFeedbackPanel;
import nl.clockwork.ebms.admin.web.BootstrapFormComponentFeedbackBorder;
import nl.clockwork.ebms.admin.web.configuration.Constants.JdbcDriver;

public class JdbcPropertiesFormPanel extends Panel
{
	private static final long serialVersionUID = 1L;
	protected transient Log logger = LogFactory.getLog(this.getClass());

	public JdbcPropertiesFormPanel(String id, final IModel<JdbcPropertiesFormModel> model)
	{
		super(id,model);
		JdbcPropertiesForm jdbcPropertiesForm = new JdbcPropertiesForm("form",model);
		add(new BootstrapFeedbackPanel("feedback",new ContainerFeedbackMessageFilter(jdbcPropertiesForm)).setOutputMarkupId(true));
		add(jdbcPropertiesForm);
	}

	public class JdbcPropertiesForm extends Form<JdbcPropertiesFormModel>
	{
		private static final long serialVersionUID = 1L;

		public JdbcPropertiesForm(String id, final IModel<JdbcPropertiesFormModel> model)
		{
			super(id,new CompoundPropertyModel<>(model));
			add(new BootstrapFormComponentFeedbackBorder("driverFeedback",createDriverChoice("driver",model)));
			add(new BootstrapFormComponentFeedbackBorder("hostFeedback",createHostsField("host")));
			add(new BootstrapFormComponentFeedbackBorder("portFeedback",createPortField("port")));
			add(new BootstrapFormComponentFeedbackBorder("databaseFeedback",createDatabaseField("database")));
			add(new TextField<String>("url").setLabel(new ResourceModel("lbl.url")).setOutputMarkupId(true).setEnabled(false));
			add(createTestButton("test",model));
			add(new BootstrapFormComponentFeedbackBorder("usernameFeedback",new TextField<String>("username").setLabel(new ResourceModel("lbl.username")).setRequired(true)));
			add(new BootstrapFormComponentFeedbackBorder("passwordFeedback",new PasswordTextField("password").setResetPassword(false).setLabel(new ResourceModel("lbl.password")).setRequired(false)));
		}

		private DropDownChoice<JdbcDriver> createDriverChoice(String id, final IModel<JdbcPropertiesFormModel> model)
		{
			DropDownChoice<JdbcDriver> result = new DropDownChoice<>(id,new PropertyModel<List<JdbcDriver>>(model.getObject(),"drivers"));
			result.setLabel(new ResourceModel("lbl.driver"));
			result.setRequired(true);
			result.add(new OnChangeAjaxBehavior()
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					if (model.getObject().getDriver().getDriverClassName().equals(Constants.JdbcDriver.ORACLE.getDriverClassName()) && !classExists(Constants.JdbcDriver.ORACLE.getDriverClassName()))
						error(JdbcPropertiesForm.this.getString("driver.oracle.missing"));
					target.add(JdbcPropertiesFormPanel.this.get("feedback"));
					target.add(getURLComponent());
				}
			});
			return result;
		}

		private boolean classExists(String className)
		{
			try
			{
				Class.forName(className);
				return true;
			}
			catch (ClassNotFoundException e)
			{
				return false;
			}
		}

		private TextField<String> createHostsField(String id)
		{
			TextField<String> result = new TextField<>(id);
			result.setLabel(new ResourceModel("lbl.host"));
			result.setRequired(true);
			result.add(new OnChangeAjaxBehavior()
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					target.add(getURLComponent());
				}
			});
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
					target.add(getURLComponent());
				}
			});
			return result;
		}

		private TextField<String> createDatabaseField(String id)
		{
			TextField<String> result = new TextField<>(id);
			result.setLabel(new ResourceModel("lbl.database"));
			result.setRequired(true);
			result.add(new OnChangeAjaxBehavior()
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					target.add(getURLComponent());
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

		private Component getURLComponent()
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
			return JdbcDriver.createJdbcURL(driver.getURLExpr(),getHost(),getPort(),getDatabase());
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
