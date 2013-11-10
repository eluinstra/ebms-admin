package nl.clockwork.ebms.admin.web.configuration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.FormComponentFeedbackBorder;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.io.IClusterable;

public class ServicePropertiesFormPanel extends Panel
{
	private static final long serialVersionUID = 1L;
	protected transient Log logger = LogFactory.getLog(this.getClass());

	public ServicePropertiesFormPanel(String id, final IModel<ServicePropertiesFormModel> model)
	{
		super(id,model);
		add(new ServicePropertiesForm("form",model));
	}

	public class ServicePropertiesForm extends Form<ServicePropertiesFormModel>
	{
		private static final long serialVersionUID = 1L;

		public ServicePropertiesForm(String id, final IModel<ServicePropertiesFormModel> model)
		{
			super(id,new CompoundPropertyModel<ServicePropertiesFormModel>(model));

			TextField<String> ebMSURL = new TextField<String>("ebMSURL")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.ebMSURL",ServicePropertiesForm.this));
				}
			};
			ebMSURL.setRequired(true);
			MarkupContainer ebMSURLFeedback = new FormComponentFeedbackBorder("ebMSURLFeedback");
			add(ebMSURLFeedback);
			ebMSURLFeedback.add(ebMSURL);
			add(ebMSURLFeedback);

		}
	}

	public static class ServicePropertiesFormModel extends JdbcURL implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		private String ebMSURL = "http://localhost:8089/adapter";

		public String getEbMSURL()
		{
			return ebMSURL;
		}
		public void setEbMSURL(String ebMSURL)
		{
			this.ebMSURL = ebMSURL;
		}
	}
}
