package nl.clockwork.ebms.admin.web.service.message;

import java.util.List;

import nl.clockwork.ebms.admin.web.BootstrapFeedbackPanel;
import nl.clockwork.ebms.admin.web.BootstrapFormComponentFeedbackBorder;
import nl.clockwork.ebms.admin.web.Utils;
import nl.clockwork.ebms.model.EbMSDataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

public class DataSourceModalWindow extends ModalWindow
{
	private static final long serialVersionUID = 1L;

	public DataSourceModalWindow(String id, final List<EbMSDataSource> dataSources, final Component...components)
	{
		super(id);
		//setTitle(getLocalizer().getString("dataSource",this));
		setCssClassName(ModalWindow.CSS_CLASS_GRAY);
		setContent(new DataSourcePanel(getContentId())
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void addDataSource(EbMSDataSource dataSource)
			{
				dataSources.add(dataSource);
			}
			
			@Override
			public Component[] getComponents()
			{
				return components;
			}
			
			@Override
			public ModalWindow getWindow()
			{
				return DataSourceModalWindow.this;
			}
		});
		setCookieName("eventError");
		setCloseButtonCallback(new ModalWindow.CloseButtonCallback()
		{
			private static final long serialVersionUID = 1L;

			public boolean onCloseButtonClicked(AjaxRequestTarget target)
			{
				return true;
			}
		});
	}
	
	@Override
	public IModel<String> getTitle()
	{
		return Model.of(getLocalizer().getString("dataSource",this));
	}

	public abstract class DataSourcePanel extends Panel
	{
		private static final long serialVersionUID = 1L;
		protected Log logger = LogFactory.getLog(this.getClass());

		public DataSourcePanel(String id)
		{
			super(id,new CompoundPropertyModel<DataSourceModel>(new DataSourceModel()));
			add(new DataSourceForm("form"));
		}
		
		public abstract void addDataSource(EbMSDataSource dataSource);
		public abstract Component[] getComponents();
		public abstract ModalWindow getWindow();

		public class DataSourceForm extends Form<DataSourceModel>
		{
			private static final long serialVersionUID = 1L;

			public DataSourceForm(String id)
			{
				super(id,new CompoundPropertyModel<DataSourceModel>(new DataSourceModel()));

				add(new BootstrapFeedbackPanel("feedback"));

				FileUploadField file = new FileUploadField("file")
				{
					private static final long serialVersionUID = 1L;

					@Override
					public IModel<String> getLabel()
					{
						return Model.of(getLocalizer().getString("lbl.file",DataSourceForm.this));
					}
				};
				file.setRequired(true);
				add(new BootstrapFormComponentFeedbackBorder("fileFeedback",file));

				add(new TextField<String>("name")
				{
					private static final long serialVersionUID = 1L;

					@Override
					public IModel<String> getLabel()
					{
						return Model.of(getLocalizer().getString("lbl.name",DataSourceForm.this));
					}
				});

				add(new TextField<String>("contentType")
				{
					private static final long serialVersionUID = 1L;

					@Override
					public IModel<String> getLabel()
					{
						return Model.of(getLocalizer().getString("lbl.contentType",DataSourceForm.this));
					}
				});

				final AjaxButton add = new AjaxButton("add",new ResourceModel("cmd.add"))
				{
					private static final long serialVersionUID = 1L;

					@Override
					protected void onSubmit(AjaxRequestTarget target, Form<?> form)
					{
						DataSourceModel model = DataSourceForm.this.getModelObject();
						for (FileUpload file : model.getFile())
							addDataSource(new EbMSDataSource(StringUtils.isBlank(model.getName()) ? file.getClientFileName() : model.getName(),StringUtils.isBlank(model.getContentType()) ? Utils.getContentType(file.getClientFileName()) : model.getContentType(),file.getBytes()));
						if (target != null)
						{
							target.add(getComponents());
							getWindow().close(target);
						}
					}

					@Override
					protected void onError(AjaxRequestTarget target, Form<?> form)
					{
						super.onError(target,form);
						if (target != null)
						{
							target.add(form);
						}
					}
				};
				add(add);

				AjaxButton cancel = new AjaxButton("cancel",new ResourceModel("cmd.cancel"))
				{
					private static final long serialVersionUID = 1L;

					@Override
					protected void onSubmit(AjaxRequestTarget target, Form<?> form)
					{
						getWindow().close(target);
					}
				};
				cancel.setDefaultFormProcessing(false);
				add(cancel);
			}

		}
	}

	public static class DataSourceModel extends EbMSDataSource
	{
		private static final long serialVersionUID = 1L;
		private List<FileUpload> file;
		
		public List<FileUpload> getFile()
		{
			return file;
		}
		public void setFile(List<FileUpload> file)
		{
			this.file = file;
		}
	}
}
