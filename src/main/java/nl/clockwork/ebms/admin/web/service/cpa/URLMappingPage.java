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
import nl.clockwork.ebms.admin.web.ResetButton;
import nl.clockwork.ebms.model.URLMapping;
import nl.clockwork.ebms.service.CPAService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.io.IClusterable;

public class URLMappingPage extends BasePage
{
	private static final long serialVersionUID = 1L;
	protected transient Log logger = LogFactory.getLog(getClass());
	@SpringBean(name="cpaService")
	private CPAService cpaService;

	public URLMappingPage()
	{
		this(new URLMapping());
	}
	
	public URLMappingPage(URLMapping urlMapping)
	{
		add(new BootstrapFeedbackPanel("feedback"));
		add(new EditURLMappingForm("form",urlMapping));
	}

	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("urlMapping",this);
	}

	public class EditURLMappingForm extends Form<URLMappingFormModel>
	{
		private static final long serialVersionUID = 1L;

		public EditURLMappingForm(String id, URLMapping urlMapping)
		{
			super(id,new CompoundPropertyModel<URLMappingFormModel>(new URLMappingFormModel(urlMapping)));
			add(new BootstrapFormComponentFeedbackBorder("sourceFeedback",new TextField<String>("urlMapping.source").setRequired(true).setLabel(new ResourceModel("lbl.source"))));
			add(new BootstrapFormComponentFeedbackBorder("destinationFeedback",new TextField<String>("urlMapping.destination").setRequired(true).setLabel(new ResourceModel("lbl.destination"))));
			add(createSetButton("set"));
			add(new ResetButton("reset",new ResourceModel("cmd.reset"),URLMappingPage.class));
		}

		private Button createSetButton(String id)
		{
			Button result = new Button(id,new ResourceModel("cmd.upload"))
			{
				private static final long serialVersionUID = 1L;
	
				@Override
				public void onSubmit()
				{
					try
					{
						final URLMapping urlMapping = EditURLMappingForm.this.getModelObject().urlMapping;
						cpaService.setURLMapping(urlMapping);
						setResponsePage(URLMappingsPage.class);
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
	
	public class URLMappingFormModel implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		private URLMapping urlMapping;
		
		public URLMappingFormModel(URLMapping urlMapping)
		{
			this.urlMapping = urlMapping;
		}
		public URLMapping getUrlMapping()
		{
			return urlMapping;
		}
		public void setUrlMapping(URLMapping urlMapping)
		{
			this.urlMapping = urlMapping;
		}
	}

}
