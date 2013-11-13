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

import java.io.IOException;

import nl.clockwork.ebms.admin.PropertyPlaceholderConfigurer;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.core.io.Resource;

public class HomePage extends BasePage
{
	private static final long serialVersionUID = 1L;
	@SpringBean(name="propertyConfigurer")
	private PropertyPlaceholderConfigurer propertyPlaceholderConfigurer;

	public HomePage(final PageParameters parameters) throws IOException
	{
		super(parameters);
		Resource file = propertyPlaceholderConfigurer.getOverridePropertiesFile();
		add(new WebMarkupContainer("configurationFile.found").add(new Label("configuration_file",file.getFile().getAbsolutePath())).setVisible(file.exists()));
		add(new WebMarkupContainer("configurationFile.notFound").add(new Label("configuration_file",file.getFile().getAbsolutePath()),new BookmarkablePageLink<Void>("configuration_link",WicketApplication.get().getHomePage())).setVisible(!file.exists()));
	}

	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("home",this);
	}

}
