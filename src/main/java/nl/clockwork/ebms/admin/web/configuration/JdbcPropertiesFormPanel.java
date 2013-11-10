package nl.clockwork.ebms.admin.web.configuration;

import java.util.Arrays;
import java.util.List;

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
import org.apache.wicket.markup.html.form.validation.FormComponentFeedbackBorder;
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
			DropDownChoice<JdbcDriver> jdbcDrivers = new DropDownChoice<JdbcDriver>("jdbcDrivers",new PropertyModel<JdbcDriver>(model.getObject(),"jdbcDriver"),new PropertyModel<List<JdbcDriver>>(model.getObject(),"jdbcDrivers"))
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.jdbcDriver",JdbcPropertiesFormPanel.this));
				}
			};
			jdbcDrivers.setRequired(true);
			MarkupContainer jdbcDriverFeedback = new FormComponentFeedbackBorder("jdbcDriverFeedback");
			add(jdbcDriverFeedback);
			jdbcDriverFeedback.add(jdbcDrivers);

			TextField<String> jdbcHost = new TextField<String>("jdbcHost")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.jdbcHost",JdbcPropertiesFormPanel.this));
				}
			};
			jdbcHost.setRequired(true);
			MarkupContainer jdbcHostFeedback = new FormComponentFeedbackBorder("jdbcHostFeedback");
			add(jdbcHostFeedback);
			jdbcHostFeedback.add(jdbcHost);

			TextField<Integer> jdbcPort = new TextField<Integer>("jdbcPort")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.jdbcPort",JdbcPropertiesFormPanel.this));
				}
			};
			MarkupContainer jdbcPortFeedback = new FormComponentFeedbackBorder("jdbcPortFeedback");
			add(jdbcPortFeedback);
			jdbcPortFeedback.add(jdbcPort);

			TextField<String> jdbcDatabase = new TextField<String>("jdbcDatabase")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.jdbcDatabase",JdbcPropertiesFormPanel.this));
				}
			};
			jdbcDatabase.setRequired(true);
			MarkupContainer jdbcDatabaseFeedback = new FormComponentFeedbackBorder("jdbcDatabaseFeedback");
			add(jdbcDatabaseFeedback);
			jdbcDatabaseFeedback.add(jdbcDatabase);

			final TextField<String> jdbcURL = new TextField<String>("jdbcURL");
			jdbcURL.setOutputMarkupId(true);
			jdbcURL.setEnabled(false);
			add(jdbcURL);
			
			Button test = new Button("test",new ResourceModel("cmd.test"))
			{
				private static final long serialVersionUID = 1L;

				@Override
				public void onSubmit()
				{
					try
					{
						JdbcPropertiesFormModel m = model.getObject();
						Utils.testDatabaseConnection(m.getJdbcDriver().getDriverClassName(),m.getJdbcURL(),m.getJdbcUsername(),m.getJdbcPassword());
						info(getPage().getString("db.connection.ok"));
					}
					catch (Exception e)
					{
						logger .error("",e);
						error(new StringResourceModel("db.connection.nok",getPage(),Model.of(e)).getString());
					}
				}
			};
			add(test);
			
			jdbcDrivers.add(new AjaxFormComponentUpdatingBehavior("onchange")
	    {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					target.add(jdbcURL);
				}
	    });

			jdbcHost.add(new OnChangeAjaxBehavior()
	    {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					target.add(jdbcURL);
				}
	    });

			jdbcPort.add(new OnChangeAjaxBehavior()
	    {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					target.add(jdbcURL);
				}
	    });

			jdbcDatabase.add(new OnChangeAjaxBehavior()
	    {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					target.add(jdbcURL);
				}
	    });

			TextField<String> jdbcUsername = new TextField<String>("jdbcUsername")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.jdbcUsername",JdbcPropertiesFormPanel.this));
				}
			};
			jdbcUsername.setRequired(true);
			MarkupContainer jdbcUsernameFeedback = new FormComponentFeedbackBorder("jdbcUsernameFeedback");
			add(jdbcUsernameFeedback);
			jdbcUsernameFeedback.add(jdbcUsername);

			PasswordTextField jdbcPassword = new PasswordTextField("jdbcPassword")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.jdbcPassword",JdbcPropertiesFormPanel.this));
				}
			};
			jdbcPassword.setResetPassword(false);
			jdbcPassword.setRequired(false);
			MarkupContainer jdbcPasswordFeedback = new FormComponentFeedbackBorder("jdbcPasswordFeedback");
			add(jdbcPasswordFeedback);
			jdbcPasswordFeedback.add(jdbcPassword);
		}
	}

	public static class JdbcPropertiesFormModel extends JdbcURL implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		private JdbcDriver jdbcDriver = JdbcDriver.HSQLDB;
		private String jdbcUsername = "sa";
		private String jdbcPassword = null;

		public List<JdbcDriver> getJdbcDrivers()
		{
			return Arrays.asList(JdbcDriver.values());
		}
		public JdbcDriver getJdbcDriver()
		{
			return jdbcDriver;
		}
		public void setJdbcDriver(JdbcDriver jdbcDriver)
		{
			this.jdbcDriver = jdbcDriver;
		}
		public String getJdbcURL()
		{
			//return jdbcDriver.createJdbcURL(getJdbcHost(),getJdbcPort(),getJdbcDatabase());
			return JdbcDriver.createJdbcURL(jdbcDriver.getUrlExpr(),getJdbcHost(),getJdbcPort(),getJdbcDatabase());
		}
		public String getJdbcUsername()
		{
			return jdbcUsername;
		}
		public void setJdbcUsername(String jdbcUsername)
		{
			this.jdbcUsername = jdbcUsername;
		}
		public String getJdbcPassword()
		{
			return jdbcPassword;
		}
		public void setJdbcPassword(String jdbcPassword)
		{
			this.jdbcPassword = jdbcPassword;
		}
	}
}
