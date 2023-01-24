/*
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
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import nl.clockwork.ebms.admin.web.Action;
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.BootstrapFeedbackPanel;
import nl.clockwork.ebms.admin.web.BootstrapFormComponentFeedbackBorder;
import nl.clockwork.ebms.admin.web.Button;
import nl.clockwork.ebms.admin.web.ResetButton;
import nl.clockwork.ebms.cpa.CPAService;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.io.IClusterable;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CPAUploadPage extends BasePage
{
	private static final long serialVersionUID = 1L;
	@SpringBean(name = "cpaService")
	CPAService cpaService;

	public CPAUploadPage()
	{
		add(new BootstrapFeedbackPanel("feedback"));
		add(new EditUploadForm("form"));
	}

	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("cpaUpload", this);
	}

	public class EditUploadForm extends Form<EditUploadFormData>
	{
		private static final long serialVersionUID = 1L;

		public EditUploadForm(String id)
		{
			super(id, new CompoundPropertyModel<>(new EditUploadFormData()));
			setMultiPart(true);
			add(new BootstrapFormComponentFeedbackBorder("cpaFeedback", createCPAFileField("cpaFile")));
			add(new CheckBox("overwrite").setLabel(new ResourceModel("lbl.overwrite")));
			add(createValidateButton("validate"));
			add(createUploadButton("upload"));
			add(new ResetButton("reset", new ResourceModel("cmd.reset"), CPAUploadPage.class));
		}

		private FileUploadField createCPAFileField(String id)
		{
			val result = new FileUploadField(id);
			result.setLabel(new ResourceModel("lbl.cpa"));
			result.setRequired(true);
			return result;
		}

		private Button createValidateButton(String id)
		{
			Action onSubmit = () ->
			{
				try
				{
					val files = getModelObject().cpaFile;
					if (files != null && files.size() == 1)
					{
						FileUpload file = files.get(0);
						// String contentType = file.getContentType();
						// FIXME char encoding
						cpaService.validateCPA(new String(file.getBytes()));
					}
					info(getString("cpa.valid"));
				}
				catch (Exception e)
				{
					log.error("", e);
					error(e.getMessage());
				}
			};
			return new Button(id, new ResourceModel("cmd.validate"), onSubmit);
		}

		private Button createUploadButton(String id)
		{
			Action onSubmit = () ->
			{
				try
				{
					val files = getModelObject().cpaFile;
					if (files != null && files.size() == 1)
					{
						val file = files.get(0);
						// val contentType = file.getContentType();
						// FIXME char encoding
						cpaService.insertCPA(new String(file.getBytes()), getModelObject().isOverwrite());
					}
					setResponsePage(new CPAsPage());
				}
				catch (Exception e)
				{
					log.error("", e);
					error(e.getMessage());
				}
			};
			val result = new Button(id, new ResourceModel("cmd.upload"), onSubmit);
			setDefaultButton(result);
			return result;
		}
	}

	@Data
	@FieldDefaults(level = AccessLevel.PRIVATE)
	@NoArgsConstructor
	public class EditUploadFormData implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		List<FileUpload> cpaFile;
		boolean overwrite;
	}
}
