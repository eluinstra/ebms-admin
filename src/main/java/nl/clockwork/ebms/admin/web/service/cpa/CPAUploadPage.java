/**
 * Copyright 2013 Clockwork
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.clockwork.ebms.admin.web.service.cpa;

import java.util.List;

import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.BootstrapFeedbackPanel;
import nl.clockwork.ebms.service.CPAService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.form.validation.FormComponentFeedbackBorder;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.io.IClusterable;

public class CPAUploadPage extends BasePage
{
	private static final long serialVersionUID = 1L;
	protected transient Log logger = LogFactory.getLog(getClass());
	@SpringBean(name="cpaService")
	private CPAService cpaService;

	public CPAUploadPage()
	{
		add(new BootstrapFeedbackPanel("feedback"));
		add(new EditUploadForm("uploadCPAForm"));
	}
	
	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("cpaUpload",this);
	}

	public class EditUploadForm extends Form<EditUploadFormModel>
	{
		private static final long serialVersionUID = 1L;

		public EditUploadForm(String id)
		{
			super(id,new CompoundPropertyModel<EditUploadFormModel>(new EditUploadFormModel()));
			setMultiPart(true);

			FileUploadField cpaFile = new FileUploadField("cpaFile")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.cpa",EditUploadForm.this));
				}
			};
			cpaFile.setRequired(true);
			MarkupContainer cpaFeedback = new FormComponentFeedbackBorder("cpaFeedback");
			add(cpaFeedback);
			cpaFeedback.add(cpaFile);
			
			CheckBox overwrite = new CheckBox("overwrite")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.overwrite",EditUploadForm.this));
				}
			};
			add(overwrite);

			Button validate = new Button("validate",new ResourceModel("cmd.validate"))
			{
				private static final long serialVersionUID = 1L;
	
				@Override
				public void onSubmit()
				{
					try
					{
						final List<FileUpload> files = EditUploadForm.this.getModelObject().cpaFile;
						if (files != null && files.size() == 1)
						{
							FileUpload file = files.get(0);
							//String contentType = file.getContentType();
							//FIXME char encoding
							cpaService.validateCPA(new String(file.getBytes()));
						}
						info(getString("cpa.valid"));
					}
					catch (Exception e)
					{
						logger.error("",e);
						error(e.getMessage());
					}
				}
			};
			add(validate);

			Button upload = new Button("upload",new ResourceModel("cmd.upload"))
			{
				private static final long serialVersionUID = 1L;
	
				@Override
				public void onSubmit()
				{
					try
					{
						final List<FileUpload> files = EditUploadForm.this.getModelObject().cpaFile;
						if (files != null && files.size() == 1)
						{
							FileUpload file = files.get(0);
							//String contentType = file.getContentType();
							//FIXME char encoding
							cpaService.insertCPA(new String(file.getBytes()),EditUploadForm.this.getModelObject().isOverwrite());
						}
						setResponsePage(new CPAsPage());
					}
					catch (Exception e)
					{
						logger.error("",e);
						error(e.getMessage());
					}
				}
			};
			setDefaultButton(upload);
			add(upload);
		}
	}
	
	public class EditUploadFormModel implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		private List<FileUpload> cpaFile;
		private boolean overwrite;
		
		public List<FileUpload> getCpaFile()
		{
			return cpaFile;
		}
		
		public void setCpaFile(List<FileUpload> cpaFile)
		{
			this.cpaFile = cpaFile;
		}
		
		public boolean isOverwrite()
		{
			return overwrite;
		}
		
		public void setOverwrite(boolean overwrite)
		{
			this.overwrite = overwrite;
		}
	}

}
