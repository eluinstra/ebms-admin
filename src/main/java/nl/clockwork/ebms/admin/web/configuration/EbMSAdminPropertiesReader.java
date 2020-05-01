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
import java.io.Reader;
import java.net.MalformedURLException;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import lombok.val;
import nl.clockwork.ebms.admin.web.configuration.ConsolePropertiesFormPanel.ConsolePropertiesFormModel;
import nl.clockwork.ebms.admin.web.configuration.EbMSAdminPropertiesPage.EbMSAdminPropertiesFormModel;
import nl.clockwork.ebms.admin.web.configuration.ServicePropertiesFormPanel.ServicePropertiesFormModel;

public class EbMSAdminPropertiesReader extends EbMSCorePropertiesReader
{
	public EbMSAdminPropertiesReader(Reader reader)
	{
		super(reader);
	}

	public void read(EbMSAdminPropertiesFormModel ebMSAdminProperties, PropertiesType propertiesType) throws IOException
	{
		val properties = new Properties();
		switch (propertiesType)
		{
			case EBMS_ADMIN:
				properties.load(reader);
				read(properties,ebMSAdminProperties.getConsoleProperties());
				read(properties,ebMSAdminProperties.getCoreProperties());
				read(properties,ebMSAdminProperties.getServiceProperties());
				read(properties,ebMSAdminProperties.getJdbcProperties());
				break;
			case EBMS_ADMIN_EMBEDDED:
				properties.load(reader);
				read(properties,ebMSAdminProperties.getConsoleProperties());
				read(properties,ebMSAdminProperties.getCoreProperties());
				read(properties,ebMSAdminProperties.getHttpProperties());
				read(properties,ebMSAdminProperties.getSignatureProperties());
				read(properties,ebMSAdminProperties.getEncryptionProperties());
				read(properties,ebMSAdminProperties.getJdbcProperties());
				break;
			case EBMS_CORE:
				read(ebMSAdminProperties);
				break;
		}
	}
	
	protected void read(Properties properties, ConsolePropertiesFormModel consoleProperties) throws MalformedURLException
	{
		consoleProperties.setMaxItemsPerPage(Integer.parseInt(properties.getProperty("maxItemsPerPage")));
		consoleProperties.setLog4jPropertiesFile(StringUtils.defaultString(properties.getProperty("log4j.file")).replaceFirst("file:",""));
	}

	protected void read(Properties properties, ServicePropertiesFormModel serviceProperties) throws MalformedURLException
	{
		serviceProperties.setUrl(properties.getProperty("service.ebms.url"));
	}

}
