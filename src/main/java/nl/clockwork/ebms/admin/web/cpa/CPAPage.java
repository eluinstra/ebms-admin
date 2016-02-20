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
package nl.clockwork.ebms.admin.web.cpa;

import nl.clockwork.ebms.admin.model.CPA;
import nl.clockwork.ebms.admin.web.PageLink;
import nl.clockwork.ebms.admin.web.BasePage;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.Model;

public class CPAPage extends BasePage
{
	private static final long serialVersionUID = 1L;

	public CPAPage(final CPA cpa, final WebPage responsePage)
	{
		add(new Label("cpaId",cpa.getCpaId()));
		add(new TextArea<String>("cpa",Model.of(cpa.getCpa())).setEnabled(false));
		add(new PageLink("back",responsePage));
	}

	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("cpa",this);
	}

}
