package nl.clockwork.ebms.admin.web.service.message;

import java.util.List;

import nl.clockwork.ebms.admin.web.service.message.SendMessagePage.MessageForm;
import nl.clockwork.ebms.model.EbMSDataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

public class DataSourcePanel extends Panel
{
	private static final long serialVersionUID = 1L;
	protected Log logger = LogFactory.getLog(this.getClass());

	public DataSourcePanel(String id, final DataSourceModel dataSource, final MessageForm messageForm, final List<DataSourceModel> dataSources, final int index)
	{
		super(id,new CompoundPropertyModel<DataSourceModel>(dataSource));
		add(new FileUploadField("file"));
		add(new TextField<String>("name"));
		add(new TextField<String>("contentType"));
		AjaxLink<Void> remove = new AjaxLink<Void>("remove")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target)
			{
				dataSources.remove(index);
				target.add(messageForm);
			}
		};
//		Button remove = new Button("remove")
//		{
//			private static final long serialVersionUID = 1L;
//			
//			@Override
//			public void onSubmit()
//			{
//				dataSources.remove(index);
//			}
//		};
//		remove.setDefaultFormProcessing(false);
		add(remove);

		/*file.add(new AjaxFormComponentUpdatingBehavior("onchange")
    {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target)
			{
				DataSourceModel dataSourceModel = (DataSourceModel)DataSourcePanel.this.getDefaultModel().getObject();
				for (FileUpload file : dataSourceModel.getFile())
					dataSource.setContentType(URLConnection.guessContentTypeFromName(file.getClientFileName()));
					//dataSource.setContentType(new MimetypesFileTypeMap().getContentType(file.getClientFileName()));
				target.add(contentType);
			}
    });*/
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