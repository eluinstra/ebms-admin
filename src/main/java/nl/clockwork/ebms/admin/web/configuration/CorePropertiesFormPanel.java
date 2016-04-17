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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.io.IClusterable;

public class CorePropertiesFormPanel extends Panel
{
	private static final long serialVersionUID = 1L;
	protected transient Log logger = LogFactory.getLog(this.getClass());

	public CorePropertiesFormPanel(String id, final IModel<CorePropertiesFormModel> model)
	{
		super(id,model);
		add(new CorePropertiesForm("form",model));
	}

	public class CorePropertiesForm extends Form<CorePropertiesFormModel>
	{
		private static final long serialVersionUID = 1L;

		public CorePropertiesForm(String id, final IModel<CorePropertiesFormModel> model)
		{
			super(id,new CompoundPropertyModel<CorePropertiesFormModel>(model));
			add(new CheckBox("digipoortPatch",new LocalizedStringResource("lbl.digipoortPatch",CorePropertiesForm.this)));
			add(new CheckBox("oraclePatch",new LocalizedStringResource("lbl.oraclePatch",CorePropertiesForm.this)));
			add(new CheckBox("cleoPatch",new LocalizedStringResource("lbl.cleoPatch",CorePropertiesForm.this)));
		}
	}

	public static class CorePropertiesFormModel implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		private boolean digipoortPatch = true;
		private boolean oraclePatch = true;
		private boolean cleoPatch = false;

		public boolean isDigipoortPatch()
		{
			return digipoortPatch;
		}
		public void setDigipoortPatch(boolean digipoortPatch)
		{
			this.digipoortPatch = digipoortPatch;
		}
		public boolean isOraclePatch()
		{
			return oraclePatch;
		}
		public void setOraclePatch(boolean oraclePatch)
		{
			this.oraclePatch = oraclePatch;
		}
		public boolean isCleoPatch()
		{
			return cleoPatch;
		}
		public void setCleoPatch(boolean cleoPatch)
		{
			this.cleoPatch = cleoPatch;
		}
	}
}
