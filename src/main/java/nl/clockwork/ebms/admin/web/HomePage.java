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
package nl.clockwork.ebms.admin.web;


import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class HomePage extends BasePage
{
	private static final long serialVersionUID = 1L;

	public HomePage(final PageParameters parameters)
	{
		super(parameters);
		add(new FeedbackPanel("feedback"));
		add(new Label("message",getLocalizer().getString("intro",this)));
	}

	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("home",this);
	}
}
