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

import java.util.Arrays;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.util.io.IClusterable;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;
import lombok.extern.apachecommons.CommonsLog;
import nl.clockwork.ebms.admin.web.Action;
import nl.clockwork.ebms.admin.web.BootstrapFormComponentFeedbackBorder;
import nl.clockwork.ebms.admin.web.Button;
import nl.clockwork.ebms.admin.web.Supplier;
import nl.clockwork.ebms.security.KeyStoreType;

@CommonsLog
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JavaKeyStorePropertiesFormPanel extends Panel
{
	private static final long serialVersionUID = 1L;
	boolean required;
	Supplier<Boolean> isVisible;

	public JavaKeyStorePropertiesFormPanel(String id, IModel<JavaKeyStorePropertiesFormModel> model)
	{
		this(id,model,true,null);
	}

	@Builder
	public JavaKeyStorePropertiesFormPanel(String id, IModel<JavaKeyStorePropertiesFormModel> model, boolean required, Supplier<Boolean> isVisible)
	{
		super(id,model);
		this.required = required;
		this.isVisible = isVisible == null ? () -> super.isVisible() : isVisible;
		add(new JavaKeyStorePropertiesForm("form",model));
	}

	@Override
	public boolean isVisible()
	{
		return isVisible.get();
	}

	public class JavaKeyStorePropertiesForm extends Form<JavaKeyStorePropertiesFormModel>
	{
		private static final long serialVersionUID = 1L;

		public JavaKeyStorePropertiesForm(String id, IModel<JavaKeyStorePropertiesFormModel> model)
		{
			super(id,new CompoundPropertyModel<>(model));
			add(new BootstrapFormComponentFeedbackBorder("typeFeedback",new DropDownChoice<KeyStoreType>("type",Arrays.asList(KeyStoreType.values())).setLabel(new ResourceModel("lbl.type")).setRequired(required)));
			add(new BootstrapFormComponentFeedbackBorder("uriFeedback",new TextField<String>("uri").setLabel(new ResourceModel("lbl.uri")).setRequired(required)));
			add(new BootstrapFormComponentFeedbackBorder("passwordFeedback",new PasswordTextField("password").setResetPassword(false).setLabel(new ResourceModel("lbl.password")).setRequired(required)));
			add(createTestButton("test",model));
		}

		private Button createTestButton(String id, final IModel<JavaKeyStorePropertiesFormModel> model)
		{
			Action action = () ->
			{
				try
				{
					val m = model.getObject();
					Utils.testKeystore(m.getType(),m.getUri(),m.getPassword());
					info(JavaKeyStorePropertiesForm.this.getString("test.ok"));
				}
				catch (Exception e)
				{
					log.error("",e);
					error(new StringResourceModel("test.nok",JavaKeyStorePropertiesForm.this,Model.of(e)).getString());
				}
			};
			return Button.builder()
					.id(id)
					.model(new ResourceModel("cmd.test"))
					.onSubmit(action)
					.build();
		}
	}

	@Data
	@FieldDefaults(level = AccessLevel.PRIVATE)
	@NoArgsConstructor
	public static class JavaKeyStorePropertiesFormModel implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		KeyStoreType type = KeyStoreType.PKCS12;
		@NonNull
		String uri = "keystore.p12";
		@NonNull
		String password = "password";
	}
}
