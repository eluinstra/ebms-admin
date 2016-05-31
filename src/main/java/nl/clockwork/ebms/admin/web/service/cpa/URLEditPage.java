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

import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.BootstrapFeedbackPanel;
import nl.clockwork.ebms.admin.web.BootstrapFormComponentFeedbackBorder;
import nl.clockwork.ebms.service.CPAService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.io.IClusterable;

public class URLEditPage extends BasePage
{
	private static final long serialVersionUID = 1L;
	protected transient Log logger = LogFactory.getLog(getClass());
	@SpringBean(name="cpaService")
	private CPAService cpaService;

	public URLEditPage(WebPage responsePage)
	{
		this("","",responsePage);
	}

	public URLEditPage(final String source, final String destination, final WebPage responsePage)
	{
		add(new BootstrapFeedbackPanel("feedback"));
		add(new EditURLForm("form",new EditURLFormModel(source,destination)));
	}
	
	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("url",this);
	}

	public class EditURLForm extends Form<EditURLFormModel>
	{
		private static final long serialVersionUID = 1L;

		public EditURLForm(String id, EditURLFormModel model)
		{
			super(id,new CompoundPropertyModel<EditURLFormModel>(model));
			add(new BootstrapFormComponentFeedbackBorder("sourceFeedback",createSourceTextField("source")));
			add(new BootstrapFormComponentFeedbackBorder("destinationFeedback",createDestinationFileField("destination")));
			add(createSaveButton("save"));
		}

		private TextField<String> createSourceTextField(String id)
		{
			TextField<String> result = new TextField<String>(id);
			result.setLabel(new ResourceModel("lbl.sourceUrl"));
			result.setRequired(true);
			return result;
		}

		private TextField<String> createDestinationFileField(String id)
		{
			TextField<String> result = new TextField<String>(id);
			result.setLabel(new ResourceModel("lbl.destinationUrl"));
			result.setRequired(true);
			return result;
		}

		private Button createSaveButton(String id)
		{
			Button result = new Button(id,new ResourceModel("cmd.save"))
			{
				private static final long serialVersionUID = 1L;
	
				@Override
				public void onSubmit()
				{
					try
					{
						cpaService.setURL(EditURLForm.this.getModelObject().source,EditURLForm.this.getModelObject().destination);
						setResponsePage(new URLsPage());
					}
					catch (Exception e)
					{
						logger.error("",e);
						error(e.getMessage());
					}
				}
			};
			setDefaultButton(result);
			return result;
		}
	}
	
	public class EditURLFormModel implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		private String source;
		private String destination;

		public EditURLFormModel()
		{
		}
		public EditURLFormModel(String source, String destination)
		{
			this.source = source;
			this.destination = destination;
		}
		public String getSource()
		{
			return source;
		}
		public void setSource(String source)
		{
			this.source = source;
		}
		public String getDestination()
		{
			return destination;
		}
		public void setDestination(String destination)
		{
			this.destination = destination;
		}
	}

}
