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

import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.io.IClusterable;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;
import nl.clockwork.ebms.admin.web.AjaxFormComponentUpdatingBehavior;
import nl.clockwork.ebms.admin.web.configuration.JavaKeyStorePropertiesFormPanel.JavaKeyStorePropertiesFormData;

public class SignaturePropertiesFormPanel extends Panel
{
	private static final long serialVersionUID = 1L;

	public SignaturePropertiesFormPanel(String id, final IModel<SignaturePropertiesFormData> model)
	{
		super(id,model);
		add(new SignaturePropertiesForm("form",model));
	}

	public class SignaturePropertiesForm extends Form<SignaturePropertiesFormData>
	{
		private static final long serialVersionUID = 1L;

		public SignaturePropertiesForm(String id, final IModel<SignaturePropertiesFormData> model)
		{
			super(id,new CompoundPropertyModel<>(model));
			add(createSigningCheckBox("signing"));
			add(createKeystorePropertiesPanel("keystoreProperties"));
		}

		private CheckBox createSigningCheckBox(String id)
		{
			val result = new CheckBox(id);
			result.setLabel(new ResourceModel("lbl.signing"));
			result.add(AjaxFormComponentUpdatingBehavior.builder()
					.event("change")
					.onUpdate(t -> t.add(this))
					.build());
			return result;
		}

		private JavaKeyStorePropertiesFormPanel createKeystorePropertiesPanel(String id)
		{
			return JavaKeyStorePropertiesFormPanel.builder()
					.id(id)
					.model(new PropertyModel<>(getModel(),"keystoreProperties"))
					.required(false)
					.showDefaultAlias(false)
					.isVisible(() -> getModelObject().isSigning())
					.build();
		}

	}

	@Data
	@FieldDefaults(level = AccessLevel.PRIVATE)
	@NoArgsConstructor
	public static class SignaturePropertiesFormData implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		boolean signing;
		@NonNull
		JavaKeyStorePropertiesFormData keystoreProperties = new JavaKeyStorePropertiesFormData();
	}
}
