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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import lombok.AccessLevel;
import lombok.val;
import lombok.experimental.FieldDefaults;
import nl.clockwork.ebms.admin.Utils;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class AboutPage extends BasePage
{
	private static final long serialVersionUID = 1L;

	public AboutPage(final PageParameters parameters) throws FileNotFoundException, IOException
	{
		super(parameters);
		add(new WebMarkupContainer("ebms-admin.version").add(new Label("version",Utils.readVersion("/META-INF/maven/nl.clockwork.ebms.admin/ebms-admin/pom.properties"))));
		add(new WebMarkupContainer("ebms-core.version").add(new Label("version",Utils.readVersion("/META-INF/maven/nl.clockwork.ebms/ebms-core/pom.properties"))));
		val properties = WicketApplication.get().getPropertySourcesPlaceholderConfigurer().getProperties();
		val writer = new StringWriter();
		Utils.writeProperties(properties,writer);
		add(new MultiLineLabel("properties",writer.toString()));
	}

	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("about",this);
	}

}
