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
package nl.clockwork.ebms.admin.web.configuration;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.io.IClusterable;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import nl.clockwork.ebms.admin.web.BootstrapFormComponentFeedbackBorder;

public class ConsolePropertiesFormPanel extends Panel
{
	private static final long serialVersionUID = 1L;

	public ConsolePropertiesFormPanel(String id, final IModel<ConsolePropertiesFormData> model)
	{
		super(id,model);
		add(new ConsolePropertiesForm("form",model));
	}

	public class ConsolePropertiesForm extends Form<ConsolePropertiesFormData>
	{
		private static final long serialVersionUID = 1L;

		public ConsolePropertiesForm(String id, final IModel<ConsolePropertiesFormData> model)
		{
			super(id,new CompoundPropertyModel<>(model));
			add(new BootstrapFormComponentFeedbackBorder(
					"maxItemsPerPageFeedback",
					new TextField<Integer>("maxItemsPerPage")
							.setLabel(new ResourceModel("lbl.maxItemsPerPage"))
							.setRequired(true)));
		}
	}

	@Data
	@FieldDefaults(level = AccessLevel.PRIVATE)
	@NoArgsConstructor
	public static class ConsolePropertiesFormData implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		int maxItemsPerPage = 20;
	}
}
