package nl.clockwork.ebms.admin.web.configuration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.FormComponentFeedbackBorder;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
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

			TextField<String> url = new TextField<String>("url")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.url",ServicePropertiesForm.this));
				}
			};
			url.setRequired(true);
			MarkupContainer urlFeedback = new FormComponentFeedbackBorder("urlFeedback");
			add(urlFeedback);
			urlFeedback.add(url);
			add(urlFeedback);

			Button test = new Button("test",new ResourceModel("cmd.test"))
			{
				private static final long serialVersionUID = 1L;

				@Override
				public void onSubmit()
				{
					try
					{
						ServicePropertiesFormModel m = model.getObject();
						Utils.testEbMSUrl(m.getUrl());
						info(ServicePropertiesForm.this.getString("test.ok"));
					}
					catch (Exception e)
					{
						logger .error("",e);
						error(new StringResourceModel("test.nok",ServicePropertiesForm.this,Model.of(e)).getString());
					}
				}
			};
			add(test);
		}
	}

	public static class ServicePropertiesFormModel implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		private String url = "http://localhost:8089/adapter";

		public String getUrl()
		{
			return url;
		}
		public void setUrl(String url)
		{
			this.url = url;
		}
	}
}
