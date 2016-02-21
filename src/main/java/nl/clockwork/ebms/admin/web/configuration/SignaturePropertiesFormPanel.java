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

import nl.clockwork.ebms.admin.web.CheckBox;
import nl.clockwork.ebms.admin.web.LocalizedStringResource;
import nl.clockwork.ebms.admin.web.configuration.JavaKeyStorePropertiesFormPanel.JavaKeyStorePropertiesFormModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.io.IClusterable;

public class SignaturePropertiesFormPanel extends Panel
{
	private static final long serialVersionUID = 1L;
	protected transient Log logger = LogFactory.getLog(this.getClass());

	public SignaturePropertiesFormPanel(String id, final IModel<SignaturePropertiesFormModel> model)
	{
		super(id,model);
		add(new SignaturePropertiesForm("form",model));
	}

	public class SignaturePropertiesForm extends Form<SignaturePropertiesFormModel>
	{
		private static final long serialVersionUID = 1L;

		public SignaturePropertiesForm(String id, final IModel<SignaturePropertiesFormModel> model)
		{
			super(id,new CompoundPropertyModel<SignaturePropertiesFormModel>(model));
			add(createSigningCheckBox("signing"));
			add(createKeystorePropertiesPanel("keystoreProperties"));
		}

		private CheckBox createSigningCheckBox(String id)
		{
			CheckBox result = new CheckBox(id,new LocalizedStringResource("lbl.signing",SignaturePropertiesForm.this));
			result.add(new AjaxFormComponentUpdatingBehavior("onchange")
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					target.add(SignaturePropertiesForm.this);
				}
			});
			return result;
		}

		private JavaKeyStorePropertiesFormPanel createKeystorePropertiesPanel(String id)
		{
			return new JavaKeyStorePropertiesFormPanel(id,new PropertyModel<JavaKeyStorePropertiesFormModel>(getModelObject(),"keystoreProperties"),false)
			{
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isVisible()
				{
					return getModelObject().getSigning();
				}
			};
		}

	}

	public static class SignaturePropertiesFormModel implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		private boolean signing = true;
		private JavaKeyStorePropertiesFormModel keystoreProperties = new JavaKeyStorePropertiesFormModel();

		public boolean getSigning()
		{
			return signing;
		}
		public void setSigning(boolean signing)
		{
			this.signing = signing;
		}
		public JavaKeyStorePropertiesFormModel getKeystoreProperties()
		{
			return keystoreProperties;
		}
		public void setKeystoreProperties(JavaKeyStorePropertiesFormModel keystoreProperties)
		{
			this.keystoreProperties = keystoreProperties;
		}
	}
}
