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
package nl.clockwork.ebms.admin.web;

import java.util.Locale;
import nl.clockwork.ebms.admin.web.menu.MenuPanel;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public abstract class BasePage extends WebPage
{
	private static final long serialVersionUID = 1L;

	public BasePage()
	{
		this(new PageParameters());
	}

	protected BasePage(final PageParameters parameters)
	{
		super(parameters);
		getSession().setLocale(Locale.US);
		add(new MenuPanel("menu", WicketApplication.get().getMenuItems()));
		add(new BookmarkablePageLink<Void>("home", WicketApplication.get().getHomePage()));
		add(new Label("pageTitle", new PropertyModel<String>(this, "pageTitle")));
	}

	public abstract String getPageTitle();

}
