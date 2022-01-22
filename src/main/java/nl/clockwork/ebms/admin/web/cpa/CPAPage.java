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
package nl.clockwork.ebms.admin.web.cpa;

import org.apache.wicket.IGenericComponent;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import nl.clockwork.ebms.admin.model.CPA;
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.PageLink;

public class CPAPage extends BasePage implements IGenericComponent<CPA,CPAPage>
{
	private static final long serialVersionUID = 1L;

	public CPAPage(final IModel<CPA> model, final WebPage responsePage)
	{
		setModel(new CompoundPropertyModel<>(model));
		add(new Label("cpaId"));
		add(new TextArea<String>("cpa").setEnabled(false));
		add(new PageLink("back",responsePage));
	}

	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("cpa",this);
	}
}
