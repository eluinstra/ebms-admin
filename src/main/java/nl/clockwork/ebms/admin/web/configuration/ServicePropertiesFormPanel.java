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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.util.io.IClusterable;

public class ServicePropertiesFormPanel extends Panel
{
	private static final long serialVersionUID = 1L;
	protected transient Log logger = LogFactory.getLog(this.getClass());

	public ServicePropertiesFormPanel(String id, final IModel<ServicePropertiesFormModel> model)
	{
		super(id,model);
		add(new ServicePropertiesForm("form",model));
	}

	public class ServicePropertiesForm extends Form<ServicePropertiesFormModel>
	{
		private static final long serialVersionUID = 1L;

		public ServicePropertiesForm(String id, final IModel<ServicePropertiesFormModel> model)
		{
			super(id,new CompoundPropertyModel<ServicePropertiesFormModel>(model));
			add(new BootstrapFormComponentFeedbackBorder("urlFeedback",new TextField<String>("url").setLabel(new ResourceModel("lbl.url")).setRequired(true)));
			add(createTestButton("test",model));
		}

		private Button createTestButton(String id, final IModel<ServicePropertiesFormModel> model)
		{
			Button result = new Button(id,new ResourceModel("cmd.test"))
			{
				private static final long serialVersionUID = 1L;

				@Override
				public void onSubmit()
				{
					try
					{
						ServicePropertiesFormModel m = model.getObject();
						Utils.testEbMSUrl(m.getURL());
						info(ServicePropertiesForm.this.getString("test.ok"));
					}
					catch (Exception e)
					{
						logger .error("",e);
						error(new StringResourceModel("test.nok",ServicePropertiesForm.this,Model.of(e)).getString());
					}
				}
			};
			return result;
		}
	}

	public static class ServicePropertiesFormModel implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		private String url = "http://localhost:8089/adapter";

		public String getURL()
		{
			return url;
		}
		public void setUrl(String url)
		{
			this.url = url;
		}
	}
}
