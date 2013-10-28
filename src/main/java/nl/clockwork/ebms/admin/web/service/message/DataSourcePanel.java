package nl.clockwork.ebms.admin.web.service.message;

import java.net.URLConnection;
import java.util.List;

import nl.clockwork.ebms.admin.web.BootstrapFeedbackPanel;
import nl.clockwork.ebms.model.EbMSDataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.form.validation.FormComponentFeedbackBorder;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.ResourceModel;

public abstract class DataSourcePanel extends Panel
{
	private static final long serialVersionUID = 1L;
	protected Log logger = LogFactory.getLog(this.getClass());

	public DataSourcePanel(final ModalWindow window, String id)
	{
		super(id,new CompoundPropertyModel<DataSourceModel>(new DataSourceModel()));
		add(new DataSourceForm("dataSourceForm"));
		
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

			FileUploadField file = new FileUploadField("file");
			file.setRequired(true);
			MarkupContainer fileFeedback = new FormComponentFeedbackBorder("fileFeedback");
			add(fileFeedback);
			fileFeedback.add(file);

			add(new TextField<String>("name"));

			add(new TextField<String>("contentType"));

			final AjaxButton add =
				new AjaxButton("add",new ResourceModel("cmd.add"))
				{
					private static final long serialVersionUID = 1L;

					@Override
					protected void onSubmit(AjaxRequestTarget target, Form<?> form)
					{
						DataSourceModel model = DataSourceForm.this.getModelObject();
						for (FileUpload file : model.getFile())
							addDataSource(new EbMSDataSource(StringUtils.isBlank(model.getName()) ? file.getClientFileName() : model.getName(),StringUtils.isBlank(model.getContentType()) ? URLConnection.guessContentTypeFromName(file.getClientFileName()) : model.getContentType(),file.getBytes()));
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
				}
			;
			add(add);
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