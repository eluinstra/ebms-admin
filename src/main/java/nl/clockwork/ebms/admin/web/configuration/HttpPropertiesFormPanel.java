package nl.clockwork.ebms.admin.web.configuration;

import nl.clockwork.ebms.admin.web.BootstrapFormComponentFeedbackBorder;
import nl.clockwork.ebms.admin.web.configuration.SslPropertiesFormPanel.SslPropertiesFormModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
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
			
			add(new Label("protocol")); 

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
			MarkupContainer portFeedback = new BootstrapFormComponentFeedbackBorder("portFeedback");
			add(portFeedback);
			portFeedback.add(port);

			TextField<String> path = new TextField<String>("path")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.path",HttpPropertiesForm.this));
				}
			};
			path.setRequired(true);
			MarkupContainer pathFeedback = new BootstrapFormComponentFeedbackBorder("pathFeedback");
			add(pathFeedback);
			pathFeedback.add(path);

			final TextField<String> url = new TextField<String>("url")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.url",HttpPropertiesForm.this));
				}
			};
			url.setOutputMarkupId(true);
			url.setEnabled(false);
			add(url);

			port.add(new OnChangeAjaxBehavior()
	    {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					target.add(url);
				}
	    });

			path.add(new OnChangeAjaxBehavior()
	    {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					target.add(url);
				}
	    });

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
		private String path = "/digipoortStub";
		private boolean chunkedStreamingMode = true;
		private boolean ssl = true;
		private SslPropertiesFormModel sslProperties = new SslPropertiesFormModel();

		public String getProtocol()
		{
			return ssl ? "https://" : "http://";
		}
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
		public String getPath()
		{
			return path;
		}
		public void setPath(String path)
		{
			this.path = path;
		}
		public String getUrl()
		{
			return getProtocol() + host + (port == null ? "" : ":" + port.toString()) + path;
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
