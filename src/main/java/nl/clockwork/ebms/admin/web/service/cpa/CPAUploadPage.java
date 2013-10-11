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
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class CPAUploadPage extends BasePage
{
	private static final long serialVersionUID = 1L;
	protected transient Log logger = LogFactory.getLog(getClass());
	@SpringBean(name="cpaClient")
	private CPAService cpaClient;

	public CPAUploadPage()
	{
		add(new FeedbackPanel("feedback"));
		add(new EditUploadForm("uploadCPAForm"));
	}
	
	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("cpaEdit",this);
	}

	public class EditUploadForm extends Form<Void>
	{
		private static final long serialVersionUID = 1L;
		private boolean overwrite;

		public EditUploadForm(String id)
		{
			super(id);
			setMultiPart(true);

			final FileUploadField cpaFile = new FileUploadField("cpaFile");
			cpaFile.setLabel(Model.of(getLocalizer().getString("lbl.cpa",this)));
			cpaFile.setRequired(true);
			MarkupContainer cpaFeedback = new FormComponentFeedbackBorder("cpaFeedback");
			add(cpaFeedback);
			cpaFeedback.add(cpaFile);
			
			final CheckBox overwrite = new CheckBox("overwrite",Model.of(this.overwrite));
			overwrite.setLabel(Model.of(getLocalizer().getString("lbl.overwrite",this)));
			add(overwrite);

			Button validate = new Button("validate",new ResourceModel("cmd.validate"))
			{
				private static final long serialVersionUID = 1L;
	
				@Override
				public void onSubmit()
				{
					try
					{
						final List<FileUpload> files = cpaFile.getFileUploads();
						if (files != null && files.size() == 1)
						{
							FileUpload file = files.get(0);
							//String contentType = file.getContentType();
							//FIXME char encoding
							cpaClient.validateCPA(new String(file.getBytes()));
						}
						info("CPA is valid");
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
						final List<FileUpload> files = cpaFile.getFileUploads();
						if (files != null && files.size() == 1)
						{
							FileUpload file = files.get(0);
							//String contentType = file.getContentType();
							//FIXME char encoding
							cpaClient.insertCPA(new String(file.getBytes()),overwrite.getModelObject());
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

}
