package nl.clockwork.ebms.admin.web.configuration;

import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.util.Properties;

import nl.clockwork.ebms.admin.web.configuration.ConsolePropertiesFormPanel.ConsolePropertiesFormModel;
import nl.clockwork.ebms.admin.web.configuration.Constants.PropertiesType;
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
		Properties properties = new Properties();
		switch (propertiesType)
		{
			case EBMS_ADMIN:
				properties.load(reader);
				read(properties,ebMSAdminProperties.getConsoleProperties());
				read(properties,ebMSAdminProperties.getServiceProperties());
				read(properties,ebMSAdminProperties.getJdbcProperties());
				break;
			case EBMS_ADMIN_EMBEDDED:
				properties.load(reader);
				read(properties,ebMSAdminProperties.getConsoleProperties());
				read(properties,ebMSAdminProperties.getHttpProperties());
				read(properties,ebMSAdminProperties.getSignatureProperties());
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
	}

	protected void read(Properties properties, ServicePropertiesFormModel serviceProperties) throws MalformedURLException
	{
		serviceProperties.setUrl(properties.getProperty("service.ebms.url"));
	}

}
