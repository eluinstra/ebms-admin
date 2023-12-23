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
package nl.clockwork.ebms.admin.web.configuration;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import nl.clockwork.ebms.admin.web.Action;
import nl.clockwork.ebms.admin.web.BootstrapFormComponentFeedbackBorder;
import nl.clockwork.ebms.admin.web.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.util.io.IClusterable;

@Slf4j
public class ServicePropertiesFormPanel extends Panel
{
	private static final long serialVersionUID = 1L;

	public ServicePropertiesFormPanel(String id, final IModel<ServicePropertiesFormData> model)
	{
		super(id, model);
		add(new ServicePropertiesForm("form", model));
	}

	public class ServicePropertiesForm extends Form<ServicePropertiesFormData>
	{
		private static final long serialVersionUID = 1L;

		public ServicePropertiesForm(String id, final IModel<ServicePropertiesFormData> model)
		{
			super(id, new CompoundPropertyModel<>(model));
			add(new BootstrapFormComponentFeedbackBorder("urlFeedback", new TextField<String>("url").setLabel(new ResourceModel("lbl.url")).setRequired(true)));
			add(createTestButton("test"));
		}

		private Button createTestButton(String id)
		{
			Action action = () ->
			{
				try
				{
					val o = getModelObject();
					Utils.testEbMSUrl(o.getUrl());
					info(getString("test.ok"));
				}
				catch (Exception e)
				{
					log.error("", e);
					error(new StringResourceModel("test.nok", this, Model.of(e)).getString());
				}
			};
			return new Button(id, new ResourceModel("cmd.test"), action);
		}
	}

	@Data
	@FieldDefaults(level = AccessLevel.PRIVATE)
	@NoArgsConstructor
	public static class ServicePropertiesFormData implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		@NonNull
		String url;
	}
}
