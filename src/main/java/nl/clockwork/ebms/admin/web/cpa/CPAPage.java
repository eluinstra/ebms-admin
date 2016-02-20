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
import nl.clockwork.ebms.admin.web.BasePage;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;

public class CPAPage extends BasePage
{
	private static final long serialVersionUID = 1L;

	public CPAPage(final CPA cpa, final WebPage responsePage)
	{
		add(createCPAId("cpaId",cpa));
		add(createCPA("cpa",cpa));
		add(createBack("back",responsePage));
	}

	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("cpa",this);
	}

	private Label createCPAId(String id, final CPA cpa)
	{
		return new Label(id,cpa.getCpaId());
	}

	private TextArea<String> createCPA(String id, CPA cpa)
	{
		TextArea<String> result = new TextArea<String>(id,Model.of(cpa.getCpa()));
		result.setEnabled(false);
		return result;
	}
	
	private Link<Void> createBack(String id, final WebPage responsePage)
	{
		return new Link<Void>(id)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick()
			{
				setResponsePage(responsePage);
			}
		};
	}

}
