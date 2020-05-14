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
import nl.clockwork.ebms.admin.web.configuration.ConsolePropertiesFormPanel.ConsolePropertiesFormData;
import nl.clockwork.ebms.admin.web.configuration.EbMSAdminPropertiesPage.EbMSAdminPropertiesFormData;
import nl.clockwork.ebms.admin.web.configuration.ServicePropertiesFormPanel.ServicePropertiesFormData;

public class EbMSAdminPropertiesReader extends EbMSCorePropertiesReader
{
	public EbMSAdminPropertiesReader(Reader reader)
	{
		super(reader);
	}

	public EbMSAdminPropertiesFormData read(PropertiesType propertiesType) throws IOException
	{
		val result = new EbMSAdminPropertiesFormData();
		val properties = new Properties();
		switch (propertiesType)
		{
			case EBMS_ADMIN:
				properties.load(reader);
				read(properties,result.getConsoleProperties());
				read(properties,result.getCoreProperties());
				read(properties,result.getServiceProperties());
				read(properties,result.getJdbcProperties());
				break;
			case EBMS_ADMIN_EMBEDDED:
				properties.load(reader);
				read(properties,result.getConsoleProperties());
				read(properties,result.getCoreProperties());
				read(properties,result.getHttpProperties());
				read(properties,result.getSignatureProperties());
				read(properties,result.getEncryptionProperties());
				read(properties,result.getJdbcProperties());
				break;
			case EBMS_CORE:
				read(result);
				break;
		}
		return result;
	}
	
	protected void read(Properties properties, ConsolePropertiesFormData consoleProperties) throws MalformedURLException
	{
		consoleProperties.setMaxItemsPerPage(Integer.parseInt(properties.getProperty("maxItemsPerPage")));
		consoleProperties.setLog4jPropertiesFile(StringUtils.defaultString(properties.getProperty("log4j.file")).replaceFirst("file:",""));
	}

	protected void read(Properties properties, ServicePropertiesFormData serviceProperties) throws MalformedURLException
	{
		serviceProperties.setUrl(properties.getProperty("service.ebms.url"));
	}

}
