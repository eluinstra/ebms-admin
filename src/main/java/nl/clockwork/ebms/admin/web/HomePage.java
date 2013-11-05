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

import java.io.File;

import nl.clockwork.ebms.admin.web.configuration.Constants;
import nl.clockwork.ebms.admin.web.configuration.EbMSAdminPropertiesPage;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class HomePage extends BasePage
{
	private static final long serialVersionUID = 1L;

	public HomePage(final PageParameters parameters)
	{
		super(parameters);
		File file = new File(Constants.PROPERTIES_FILE);
		add(new WebMarkupContainer("configurationFile.found").add(new Label("configuration_file",file.getAbsolutePath())).setVisible(file.exists()));
		add(new WebMarkupContainer("configurationFile.notFound").add(new Label("configuration_file",file.getAbsolutePath()),new ConfigurationLink("configuration_link")).setVisible(!file.exists()));
	}

	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("home",this);
	}

	private final class ConfigurationLink extends Link<Void>
	{
		private static final long serialVersionUID = 1L;

		private ConfigurationLink(String id)
		{
			super(id);
		}

		public void onClick()
		{
			setResponsePage(EbMSAdminPropertiesPage.class);
		}
	}

}
