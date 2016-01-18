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

import java.io.IOException;
import java.io.Writer;
import java.util.Properties;

import nl.clockwork.ebms.admin.web.configuration.ConsolePropertiesFormPanel.ConsolePropertiesFormModel;
import nl.clockwork.ebms.admin.web.configuration.Constants.PropertiesType;
import nl.clockwork.ebms.admin.web.configuration.EbMSAdminPropertiesPage.EbMSAdminPropertiesFormModel;
import nl.clockwork.ebms.admin.web.configuration.ServicePropertiesFormPanel.ServicePropertiesFormModel;

public class EbMSAdminPropertiesWriter extends EbMSCorePropertiesWriter
{
  public EbMSAdminPropertiesWriter(Writer writer)
	{
		super(writer);
	}

	public void write(EbMSAdminPropertiesFormModel ebMSAdminProperties, PropertiesType propertiesType) throws IOException
	{
		Properties p = new Properties();
		switch (propertiesType)
		{
			case EBMS_ADMIN:
				write(p,ebMSAdminProperties.getConsoleProperties());
				write(p,ebMSAdminProperties.getCoreProperties());
				write(p,ebMSAdminProperties.getServiceProperties());
				write(p,ebMSAdminProperties.getJdbcProperties());
				p.store(writer,"EbMS Admin properties");
				break;
			case EBMS_ADMIN_EMBEDDED:
				write(p,ebMSAdminProperties.getConsoleProperties());
				write(p,ebMSAdminProperties.getCoreProperties());
				write(p,ebMSAdminProperties.getHttpProperties());
				write(p,ebMSAdminProperties.getSignatureProperties());
				write(p,ebMSAdminProperties.getJdbcProperties());
				p.store(writer,"EbMS Admin Embedded properties");
				break;
			case EBMS_CORE:
				write(ebMSAdminProperties);
				break;
		}
	}

  protected void write(Properties properties, ConsolePropertiesFormModel consoleProperties)
  {
		properties.setProperty("maxItemsPerPage",Integer.toString(consoleProperties.getMaxItemsPerPage()));
  }

  protected void write(Properties properties, ServicePropertiesFormModel serviceProperties)
  {
		properties.setProperty("service.ebms.url",serviceProperties.getUrl());
  }

}
