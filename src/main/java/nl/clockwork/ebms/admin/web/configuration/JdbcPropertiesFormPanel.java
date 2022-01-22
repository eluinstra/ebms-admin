/*
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

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
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

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nl.clockwork.ebms.admin.web.Action;
import nl.clockwork.ebms.admin.web.BootstrapFeedbackPanel;
import nl.clockwork.ebms.admin.web.BootstrapFormComponentFeedbackBorder;
import nl.clockwork.ebms.admin.web.Button;
import nl.clockwork.ebms.admin.web.Consumer;
import nl.clockwork.ebms.admin.web.OnChangeAjaxBehavior;

@Slf4j
public class JdbcPropertiesFormPanel extends Panel
{
	private static final long serialVersionUID = 1L;

	public JdbcPropertiesFormPanel(String id, final IModel<JdbcPropertiesFormData> model)
	{
		super(id,model);
		val jdbcPropertiesForm = new JdbcPropertiesForm("form",model);
		add(new BootstrapFeedbackPanel("feedback",new ContainerFeedbackMessageFilter(jdbcPropertiesForm)).setOutputMarkupId(true));
		add(jdbcPropertiesForm);
	}

	public class JdbcPropertiesForm extends Form<JdbcPropertiesFormData>
	{
		private static final long serialVersionUID = 1L;

		public JdbcPropertiesForm(String id, final IModel<JdbcPropertiesFormData> model)
		{
			super(id,new CompoundPropertyModel<>(model));
			add(new BootstrapFormComponentFeedbackBorder("driverFeedback",createDriverChoice("driver")));
			add(new BootstrapFormComponentFeedbackBorder("hostFeedback",createHostsField("host")));
			add(new BootstrapFormComponentFeedbackBorder("portFeedback",createPortField("port")));
			add(new BootstrapFormComponentFeedbackBorder("databaseFeedback",createDatabaseField("database")));
			add(new TextField<String>("url").setLabel(new ResourceModel("lbl.url")).setOutputMarkupId(true).setEnabled(false));
			add(createTestButton("test"));
			add(new BootstrapFormComponentFeedbackBorder(
					"usernameFeedback",
					new TextField<String>("username")
							.setLabel(new ResourceModel("lbl.username"))
							.setRequired(true)));
			add(new BootstrapFormComponentFeedbackBorder(
					"passwordFeedback",
					new PasswordTextField("password")
							.setResetPassword(false)
							.setLabel(new ResourceModel("lbl.password"))
							.setRequired(false)));
		}

		private DropDownChoice<JdbcDriver> createDriverChoice(String id)
		{
			val o = getModelObject();
			val result = new DropDownChoice<JdbcDriver>(id,new PropertyModel<List<JdbcDriver>>(o,"drivers"));
			result.setLabel(new ResourceModel("lbl.driver"));
			result.setRequired(true);
			Consumer<AjaxRequestTarget> action = t ->
			{
				if (!o.getDriver().getDriverClassName().equals(JdbcDriver.HSQLDB.getDriverClassName()) && !classExists(o.getDriver().getDriverClassName()))
					error(getString("driver.jdbc.missing",getModel()));
				t.add(JdbcPropertiesFormPanel.this.get("feedback"));
				t.add(getURLComponent());
			};
			result.add(OnChangeAjaxBehavior.builder()
					.onUpdate(action)
					.build());
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
			result.add(OnChangeAjaxBehavior.builder()
					.onUpdate(t -> t.add(getURLComponent()))
					.build());
			return result;
		}

		private TextField<Integer> createPortField(String id)
		{
			TextField<Integer> result = new TextField<>(id);
			result.setLabel(new ResourceModel("lbl.port"));
			result.add(OnChangeAjaxBehavior.builder()
					.onUpdate(t -> t.add(getURLComponent()))
					.build());
			return result;
		}

		private TextField<String> createDatabaseField(String id)
		{
			val result = new TextField<String>(id);
			result.setLabel(new ResourceModel("lbl.database"));
			result.setRequired(true);
			result.add(OnChangeAjaxBehavior.builder()
					.onUpdate(t -> t.add(getURLComponent()))
					.build());
			return result;
		}

		private Button createTestButton(String id)
		{
			Action onSubmit = () ->
			{
				try
				{
					val o = getModelObject();
					Utils.testJdbcConnection(o.getDriver().getDriverClassName(),o.getUrl(),o.getUsername(),o.getPassword());
					info(getString("test.ok"));
				}
				catch (Exception e)
				{
					log.error("",e);
					error(new StringResourceModel("test.nok",this,Model.of(e)).getString());
				}
			};
			return new Button(id,new ResourceModel("cmd.test"),onSubmit);
		}

		private Component getURLComponent()
		{
			return this.get("url");
		}
	}

	@Data
	@FieldDefaults(level = AccessLevel.PRIVATE)
	@NoArgsConstructor
	@EqualsAndHashCode(callSuper = true)
	public static class JdbcPropertiesFormData extends JdbcURL implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		JdbcDriver driver;
		@NonNull
		String username;
		String password;

		public List<JdbcDriver> getDrivers()
		{
			return Arrays.asList(JdbcDriver.values());
		}
		public String getUrl()
		{
			//return driver.createJdbcURL(getHost(),getPort(),getDatabase());
			return JdbcDriver.createJdbcURL(driver.getUrlExpr(),getHost(),getPort(),getDatabase());
		}
	}
}
