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

import nl.clockwork.ebms.admin.web.BootstrapFormComponentFeedbackBorder;
import nl.clockwork.ebms.admin.web.LocalizedStringResource;
import nl.clockwork.ebms.admin.web.TextField;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.io.IClusterable;

public class ConsolePropertiesFormPanel extends Panel
{
	private static final long serialVersionUID = 1L;
	protected transient Log logger = LogFactory.getLog(this.getClass());

	public ConsolePropertiesFormPanel(String id, final IModel<ConsolePropertiesFormModel> model)
	{
		super(id,model);
		add(new ConsolePropertiesForm("form",model));
	}

	public class ConsolePropertiesForm extends Form<ConsolePropertiesFormModel>
	{
		private static final long serialVersionUID = 1L;

		public ConsolePropertiesForm(String id, final IModel<ConsolePropertiesFormModel> model)
		{
			super(id,new CompoundPropertyModel<ConsolePropertiesFormModel>(model));
			add(new BootstrapFormComponentFeedbackBorder("maxItemsPerPageFeedback",new TextField<Integer>("maxItemsPerPage",new LocalizedStringResource("lbl.maxItemsPerPage",ConsolePropertiesForm.this)).setRequired(true)));
		}
	}

	public static class ConsolePropertiesFormModel implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		private int maxItemsPerPage = 20;

		public int getMaxItemsPerPage()
		{
			return maxItemsPerPage;
		}
		public void setMaxItemsPerPage(int maxItemsPerPage)
		{
			this.maxItemsPerPage = maxItemsPerPage;
		}
	}
}
