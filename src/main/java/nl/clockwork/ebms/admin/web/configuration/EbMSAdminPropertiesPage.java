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

			TextField<String> jdbcHost = new TextField<String>("jdbcHost")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.dbHost",EbMSAdminPropertiesForm.this));
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
					return Model.of(getLocalizer().getString("lbl.jdbcPort",EbMSAdminPropertiesForm.this));
				}
			};
			jdbcPort.setRequired(true);
			MarkupContainer jdbcPortFeedback = new FormComponentFeedbackBorder("jdbcPortFeedback");
			add(jdbcPortFeedback);
			jdbcPortFeedback.add(jdbcPort);

			TextField<String> jdbcDatabase = new TextField<String>("jdbcDatabase")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.jdbcDatabase",EbMSAdminPropertiesForm.this));
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
			
			Button testJdbcURL = new Button("testJdbcURL",new ResourceModel("cmd.test"))
			{
				private static final long serialVersionUID = 1L;
	
				@Override
				public void onSubmit()
				{
					try
					{
						EbMSAdminPropertiesFormModel model = EbMSAdminPropertiesForm.this.getModelObject();
						Utils.testDatabaseConnection(model.getJdbcDriver().getDriverClassName(),model.getJdbcURL(),model.getJdbcUsername(),model.getJdbcPassword());
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
					return Model.of(getLocalizer().getString("lbl.jdbcUsername",EbMSAdminPropertiesForm.this));
				}
			};
			jdbcUsername.setRequired(true);
			MarkupContainer jdbcUsernameFeedback = new FormComponentFeedbackBorder("jdbcUsernameFeedback");
			add(jdbcUsernameFeedback);
			jdbcUsernameFeedback.add(jdbcUsername);

			TextField<String> jdbcPassword = new TextField<String>("jdbcPassword")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.jdbcPassword",EbMSAdminPropertiesForm.this));
				}
			};
			jdbcPassword.setRequired(true);
			MarkupContainer jdbcPasswordFeedback = new FormComponentFeedbackBorder("jdbcPasswordFeedback");
			add(jdbcPasswordFeedback);
			jdbcPasswordFeedback.add(jdbcPassword);

			add(new DownloadEbMSAdminPropertiesButton("download",new ResourceModel("cmd.download"),getModelObject()));
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
		private String jdbcHost = "localhost";
		private int jdbcPort = 1521;
		private String jdbcDatabase = "xe";
		private String jdbcUsername = "system";
		private String jdbcPassword = "oraclexe";
		
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
		public String getJdbcHost()
		{
			return jdbcHost;
		}
		public void setJdbcHost(String jdbcHost)
		{
			this.jdbcHost = jdbcHost;
		}
		public int getJdbcPort()
		{
			return jdbcPort;
		}
		public void setJdbcPort(int jdbcPort)
		{
			this.jdbcPort = jdbcPort;
		}
		public String getJdbcURL()
		{
			//return jdbcDriver.createJdbcURL(jdbcHost,jdbcPort,jdbcDatabase);
			return JdbcDriver.createJdbcURL(jdbcDriver.getUrlExpr(),jdbcHost,jdbcPort,jdbcDatabase);
		}
		public String getJdbcDatabase()
		{
			return jdbcDatabase;
		}
		public void setJdbcDatabase(String jdbcDatabase)
		{
			this.jdbcDatabase = jdbcDatabase;
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
