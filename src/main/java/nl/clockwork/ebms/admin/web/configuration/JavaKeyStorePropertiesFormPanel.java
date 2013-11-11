package nl.clockwork.ebms.admin.web.configuration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.FormComponentFeedbackBorder;
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
			MarkupContainer uriFeedback = new FormComponentFeedbackBorder("uriFeedback");
			add(uriFeedback);
			uriFeedback.add(uri);
			add(uriFeedback);

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
			MarkupContainer passwordFeedback = new FormComponentFeedbackBorder("passwordFeedback");
			add(passwordFeedback);
			passwordFeedback.add(password);

			Button test = new Button("test",new ResourceModel("cmd.test"))
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
			};
			add(test);
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
