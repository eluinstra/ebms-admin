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

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import nl.clockwork.ebms.admin.web.Action;
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.BootstrapFeedbackPanel;
import nl.clockwork.ebms.admin.web.BootstrapFormComponentFeedbackBorder;
import nl.clockwork.ebms.admin.web.Button;
import nl.clockwork.ebms.admin.web.ResetButton;
import nl.clockwork.ebms.cpa.url.URLMapping;
import nl.clockwork.ebms.cpa.url.URLMappingService;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class URLMappingPage extends BasePage
{
	private static final long serialVersionUID = 1L;
	@SpringBean(name = "urlMappingService")
	URLMappingService urlMappingService;

	public URLMappingPage()
	{
		this(Model.of(new URLMapping()));
	}

	public URLMappingPage(IModel<URLMapping> model)
	{
		add(new BootstrapFeedbackPanel("feedback"));
		add(new EditURLMappingForm("form", model));
	}

	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("urlMapping", this);
	}

	public class EditURLMappingForm extends Form<URLMapping>
	{
		private static final long serialVersionUID = 1L;

		public EditURLMappingForm(String id, IModel<URLMapping> model)
		{
			super(id, new CompoundPropertyModel<>(model));
			add(
					new BootstrapFormComponentFeedbackBorder(
							"sourceFeedback",
							new TextField<String>("source").setRequired(true).setLabel(new ResourceModel("lbl.source"))));
			add(
					new BootstrapFormComponentFeedbackBorder(
							"destinationFeedback",
							new TextField<String>("destination").setRequired(true).setLabel(new ResourceModel("lbl.destination"))));
			add(createSetButton("set"));
			add(new ResetButton("reset", new ResourceModel("cmd.reset"), URLMappingPage.class));
		}

		private Button createSetButton(String id)
		{
			Action onSubmit = () ->
			{
				try
				{
					val o = getModelObject();
					urlMappingService.setURLMapping(o);
					setResponsePage(URLMappingsPage.class);
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
}
