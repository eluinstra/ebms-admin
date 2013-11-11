package nl.clockwork.ebms.admin.web.configuration;

import java.util.Arrays;
import java.util.List;

import nl.clockwork.ebms.admin.web.BootstrapFormComponentFeedbackBorder;
import nl.clockwork.ebms.admin.web.configuration.Constants.JdbcDriver;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
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
			DropDownChoice<JdbcDriver> drivers = new DropDownChoice<JdbcDriver>("drivers",new PropertyModel<JdbcDriver>(model.getObject(),"driver"),new PropertyModel<List<JdbcDriver>>(model.getObject(),"drivers"))
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.driver",JdbcPropertiesFormPanel.this));
				}
			};
			drivers.setRequired(true);
			MarkupContainer driverFeedback = new BootstrapFormComponentFeedbackBorder("driverFeedback");
			add(driverFeedback);
			driverFeedback.add(drivers);

			TextField<String> host = new TextField<String>("host")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.host",JdbcPropertiesFormPanel.this));
				}
			};
			host.setRequired(true);
			MarkupContainer hostFeedback = new BootstrapFormComponentFeedbackBorder("hostFeedback");
			add(hostFeedback);
			hostFeedback.add(host);

			TextField<Integer> port = new TextField<Integer>("port")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.port",JdbcPropertiesFormPanel.this));
				}
			};
			MarkupContainer portFeedback = new BootstrapFormComponentFeedbackBorder("portFeedback");
			add(portFeedback);
			portFeedback.add(port);

			TextField<String> database = new TextField<String>("database")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.database",JdbcPropertiesFormPanel.this));
				}
			};
			database.setRequired(true);
			MarkupContainer databaseFeedback = new BootstrapFormComponentFeedbackBorder("databaseFeedback");
			add(databaseFeedback);
			databaseFeedback.add(database);

			final TextField<String> url = new TextField<String>("url");
			url.setOutputMarkupId(true);
			url.setEnabled(false);
			add(url);
			
			Button test = new Button("test",new ResourceModel("cmd.test"))
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
			add(test);
			
			drivers.add(new AjaxFormComponentUpdatingBehavior("onchange")
	    {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					target.add(url);
				}
	    });

			host.add(new OnChangeAjaxBehavior()
	    {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					target.add(url);
				}
	    });

			port.add(new OnChangeAjaxBehavior()
	    {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					target.add(url);
				}
	    });

			database.add(new OnChangeAjaxBehavior()
	    {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					target.add(url);
				}
	    });

			TextField<String> username = new TextField<String>("username")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.username",JdbcPropertiesFormPanel.this));
				}
			};
			username.setRequired(true);
			MarkupContainer usernameFeedback = new BootstrapFormComponentFeedbackBorder("usernameFeedback");
			add(usernameFeedback);
			usernameFeedback.add(username);

			PasswordTextField password = new PasswordTextField("password")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.password",JdbcPropertiesFormPanel.this));
				}
			};
			password.setResetPassword(false);
			password.setRequired(false);
			MarkupContainer passwordFeedback = new BootstrapFormComponentFeedbackBorder("passwordFeedback");
			add(passwordFeedback);
			passwordFeedback.add(password);
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
