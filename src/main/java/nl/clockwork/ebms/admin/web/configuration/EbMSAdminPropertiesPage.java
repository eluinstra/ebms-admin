package nl.clockwork.ebms.admin.web.configuration;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import nl.clockwork.ebms.admin.web.BasePage;
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
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.FormComponentFeedbackBorder;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.io.IClusterable;

public class EbMSAdminPropertiesPage extends BasePage
{
	private static final long serialVersionUID = 1L;
	protected transient Log logger = LogFactory.getLog(this.getClass());

	public EbMSAdminPropertiesPage() throws IOException
	{
		add(new FeedbackPanel("feedback"));
		add(new EbMSAdminPropertiesForm("ebMSAdminPropertiesForm"));
	}
	
	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("ebMSAdminProperties",this);
	}
	
	public class EbMSAdminPropertiesForm extends Form<EbMSAdminPropertiesFormModel>
	{
		private static final long serialVersionUID = 1L;

		public EbMSAdminPropertiesForm(String id)
		{
			super(id,new CompoundPropertyModel<EbMSAdminPropertiesFormModel>(new EbMSAdminPropertiesFormModel()));
			
			TextField<Integer> maxItemsPerPage = new TextField<Integer>("maxItemsPerPage")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.maxItemsPerPage",EbMSAdminPropertiesForm.this));
				}
			};
			maxItemsPerPage.setRequired(true);
			MarkupContainer maxItemsPerPageFeedback = new FormComponentFeedbackBorder("maxItemsPerPageFeedback");
			add(maxItemsPerPageFeedback);
			maxItemsPerPageFeedback.add(maxItemsPerPage);
			add(maxItemsPerPageFeedback);

			TextField<String> ebMSURL = new TextField<String>("ebMSURL")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.ebMSURL",EbMSAdminPropertiesForm.this));
				}
			};
			ebMSURL.setRequired(true);
			MarkupContainer ebMSURLFeedback = new FormComponentFeedbackBorder("ebMSURLFeedback");
			add(ebMSURLFeedback);
			ebMSURLFeedback.add(ebMSURL);
			add(ebMSURLFeedback);

			DropDownChoice<JdbcDriver> jdbcDrivers = new DropDownChoice<JdbcDriver>("jdbcDrivers",new PropertyModel<JdbcDriver>(this.getModelObject(),"jdbcDriver"),new PropertyModel<List<JdbcDriver>>(this.getModelObject(),"jdbcDrivers"))
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.jdbcDriver",EbMSAdminPropertiesForm.this));
				}
			};
			jdbcDrivers.setRequired(true);
			MarkupContainer jdbcDriverFeedback = new FormComponentFeedbackBorder("jdbcDriverFeedback");
			add(jdbcDriverFeedback);
			jdbcDriverFeedback.add(jdbcDrivers);

			TextField<String> dbHost = new TextField<String>("dbHost")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.dbHost",EbMSAdminPropertiesForm.this));
				}
			};
			dbHost.setRequired(true);
			MarkupContainer dbHostFeedback = new FormComponentFeedbackBorder("dbHostFeedback");
			add(dbHostFeedback);
			dbHostFeedback.add(dbHost);

			TextField<Integer> dbPort = new TextField<Integer>("dbPort")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.dbPort",EbMSAdminPropertiesForm.this));
				}
			};
			dbPort.setRequired(true);
			MarkupContainer dbPortFeedback = new FormComponentFeedbackBorder("dbPortFeedback");
			add(dbPortFeedback);
			dbPortFeedback.add(dbPort);

			TextField<String> dbName = new TextField<String>("dbName")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.dbName",EbMSAdminPropertiesForm.this));
				}
			};
			dbName.setRequired(true);
			MarkupContainer dbNameFeedback = new FormComponentFeedbackBorder("dbNameFeedback");
			add(dbNameFeedback);
			dbNameFeedback.add(dbName);

			final TextField<String> jdbcURL = new TextField<String>("jdbcURL");
			jdbcURL.setOutputMarkupId(true);
			jdbcURL.setEnabled(false);
			add(jdbcURL);
			
			Button testJdbcURL = new Button("testJdbcURL",new ResourceModel("cmd.test"))
			{
				private static final long serialVersionUID = 1L;
	
				@Override
				public void onSubmit()
				{
					try
					{
						EbMSAdminPropertiesFormModel model = EbMSAdminPropertiesForm.this.getModelObject();
						Utils.testDatabaseConnection(model.getJdbcDriver().getDriverClassName(),model.getJdbcURL(),model.getDbUsername(),model.getDbPassword());
						info("Database connection succesful.");
					}
					catch (Exception e)
					{
						logger.error("",e);
						error("Database connection not succesful! " + e.getMessage());
					}
				}
			};
			add(testJdbcURL);
			
			jdbcDrivers.add(new AjaxFormComponentUpdatingBehavior("onchange")
      {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					target.add(jdbcURL);
				}
      });

			dbHost.add(new OnChangeAjaxBehavior()
      {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					target.add(jdbcURL);
				}
      });

			dbPort.add(new OnChangeAjaxBehavior()
      {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					target.add(jdbcURL);
				}
      });

			dbName.add(new OnChangeAjaxBehavior()
      {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					target.add(jdbcURL);
				}
      });

			TextField<String> dbUsername = new TextField<String>("dbUsername")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.dbUsername",EbMSAdminPropertiesForm.this));
				}
			};
			dbUsername.setRequired(true);
			MarkupContainer dbUsernameFeedback = new FormComponentFeedbackBorder("dbUsernameFeedback");
			add(dbUsernameFeedback);
			dbUsernameFeedback.add(dbUsername);

			TextField<String> dbPassword = new TextField<String>("dbPassword")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.dbPassword",EbMSAdminPropertiesForm.this));
				}
			};
			dbPassword.setRequired(true);
			MarkupContainer dbPasswordFeedback = new FormComponentFeedbackBorder("dbPasswordFeedback");
			add(dbPasswordFeedback);
			dbPasswordFeedback.add(dbPassword);

			add(new GenerateEbMSAdminPropertiesLink("generate",getModelObject()));
		}
	}

	public class EbMSAdminPropertiesFormModel implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		private int maxItemsPerPage = 10;
		private String ebMSURL = "http://localhost:8089/adapter";
		//private String serviceHost = "localhost";
		//private int servicePort = 8888;
		//private String serviceURL = "/digipoortStub";
		private JdbcDriver jdbcDriver = JdbcDriver.ORACLE;
		private String dbHost = "localhost";
		private int dbPort = 1521;
		private String dbName = "xe";
		private String dbUsername = "system";
		private String dbPassword = "oraclexe";
		
		public int getMaxItemsPerPage()
		{
			return maxItemsPerPage;
		}
		public void setMaxItemsPerPage(int maxItemsPerPage)
		{
			this.maxItemsPerPage = maxItemsPerPage;
		}
		public String getEbMSURL()
		{
			return ebMSURL;
		}
		public void setEbMSURL(String ebMSURL)
		{
			this.ebMSURL = ebMSURL;
		}
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
		public String getDbHost()
		{
			return dbHost;
		}
		public void setDbHost(String dbHost)
		{
			this.dbHost = dbHost;
		}
		public int getDbPort()
		{
			return dbPort;
		}
		public void setDbPort(int dbPort)
		{
			this.dbPort = dbPort;
		}
		public String getJdbcURL()
		{
			//return jdbcDriver.createJdbcURL(dbHost,dbPort,dbName);
			return JdbcDriver.createJdbcURL(jdbcDriver.getUrlExpr(),dbHost,dbPort,dbName);
		}
		public String getDbName()
		{
			return dbName;
		}
		public void setDbName(String dbName)
		{
			this.dbName = dbName;
		}
		public String getDbUsername()
		{
			return dbUsername;
		}
		public void setDbUsername(String dbUsername)
		{
			this.dbUsername = dbUsername;
		}
		public String getDbPassword()
		{
			return dbPassword;
		}
		public void setDbPassword(String dbPassword)
		{
			this.dbPassword = dbPassword;
		}
	}
}
