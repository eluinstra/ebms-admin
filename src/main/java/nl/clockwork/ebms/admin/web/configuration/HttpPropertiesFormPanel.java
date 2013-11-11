package nl.clockwork.ebms.admin.web.configuration;

import nl.clockwork.ebms.admin.web.configuration.SslPropertiesFormPanel.SslPropertiesFormModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.FormComponentFeedbackBorder;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.io.IClusterable;

public class HttpPropertiesFormPanel extends Panel
{
	private static final long serialVersionUID = 1L;
	protected transient Log logger = LogFactory.getLog(this.getClass());

	public HttpPropertiesFormPanel(String id, final IModel<HttpPropertiesFormModel> model)
	{
		super(id,model);
		add(new HttpPropertiesForm("form",model));
	}

	public class HttpPropertiesForm extends Form<HttpPropertiesFormModel>
	{
		private static final long serialVersionUID = 1L;

		public HttpPropertiesForm(String id, final IModel<HttpPropertiesFormModel> model)
		{
			super(id,new CompoundPropertyModel<HttpPropertiesFormModel>(model));
			setOutputMarkupId(true);

			TextField<String> host = new TextField<String>("host")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.host",HttpPropertiesForm.this));
				}
			};
			host.setRequired(true);
			host.setEnabled(false);
			add(host);

			TextField<Integer> port = new TextField<Integer>("port")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.port",HttpPropertiesForm.this));
				}
			};
			MarkupContainer portFeedback = new FormComponentFeedbackBorder("portFeedback");
			add(portFeedback);
			portFeedback.add(port);

			TextField<String> url = new TextField<String>("url")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.url",HttpPropertiesForm.this));
				}
			};
			url.setRequired(true);
			MarkupContainer urlFeedback = new FormComponentFeedbackBorder("urlFeedback");
			add(urlFeedback);
			urlFeedback.add(url);

			CheckBox chunkedStreamingMode = new CheckBox("chunkedStreamingMode")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.chunkedStreamingMode",HttpPropertiesForm.this));
				}
			};
			add(chunkedStreamingMode);

			CheckBox ssl = new CheckBox("ssl")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.ssl",HttpPropertiesForm.this));
				}
			};
			add(ssl);

			final SslPropertiesFormPanel sslProperties = new SslPropertiesFormPanel("sslProperties",new PropertyModel<SslPropertiesFormModel>(getModelObject(),"sslProperties"))
			{
				private static final long serialVersionUID = 1L;

				public boolean isVisible()
				{
					return getModelObject().getSsl();
				}
			};
			sslProperties.setOutputMarkupId(true);
			add(sslProperties);

			ssl.add(new AjaxFormComponentUpdatingBehavior("onchange")
      {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					//FIXME
					//target.add(sslProperties);
					target.add(HttpPropertiesForm.this);
				}
      });
		}
	}

	public static class HttpPropertiesFormModel implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		private String host = "localhost";
		private Integer port = 8888;
		private String url = "/digipoortStub";
		private boolean chunkedStreamingMode = true;
		private boolean ssl = true;
		private SslPropertiesFormModel sslProperties = new SslPropertiesFormModel();

		public String getHost()
		{
			return host;
		}
		public Integer getPort()
		{
			return port;
		}
		public void setPort(Integer port)
		{
			this.port = port;
		}
		public String getUrl()
		{
			return url;
		}
		public void setUrl(String url)
		{
			this.url = url;
		}
		public boolean isChunkedStreamingMode()
		{
			return chunkedStreamingMode;
		}
		public void setChunkedStreamingMode(boolean chunkedStreamingMode)
		{
			this.chunkedStreamingMode = chunkedStreamingMode;
		}
		public boolean getSsl()
		{
			return ssl;
		}
		public void setSsl(boolean ssl)
		{
			this.ssl = ssl;
		}
		public SslPropertiesFormModel getSslProperties()
		{
			return sslProperties;
		}
		public void setSslProperties(SslPropertiesFormModel sslProperties)
		{
			this.sslProperties = sslProperties;
		}
	}
}
