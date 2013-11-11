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

import java.beans.PropertyVetoException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Scanner;

import nl.clockwork.ebms.admin.web.configuration.ConsolePropertiesFormPanel.ConsolePropertiesFormModel;
import nl.clockwork.ebms.admin.web.configuration.Constants.JdbcDriver;
import nl.clockwork.ebms.admin.web.configuration.Constants.PropertiesType;
import nl.clockwork.ebms.admin.web.configuration.EbMSAdminPropertiesPage.EbMSAdminPropertiesFormModel;
import nl.clockwork.ebms.admin.web.configuration.JdbcPropertiesFormPanel.JdbcPropertiesFormModel;
import nl.clockwork.ebms.admin.web.configuration.ServicePropertiesFormPanel.ServicePropertiesFormModel;

public class Utils
{
	public static String createURL(String hostname, int port)
	{
		return hostname + (port == -1 ? "" : ":" + port); 
	}
	
	public static String createURL(String hostname, Integer port)
	{
		return hostname + (port == null ? "" : ":" + port); 
	}
	
	public static void loadProperties(EbMSAdminPropertiesFormModel ebMSAdminProperties, PropertiesType propertiesType, FileReader reader) throws IOException
	{
		Properties properties = new Properties();
		properties.load(reader);
		switch (propertiesType)
		{
			case EBMS_ADMIN:
				loadProperties(properties,ebMSAdminProperties.getConsoleProperties());
				loadProperties(properties,ebMSAdminProperties.getServiceProperties());
				loadProperties(properties,ebMSAdminProperties.getJdbcProperties());
				break;
			case EBMS_ADMIN_EMBEDDED:
				loadProperties(properties,ebMSAdminProperties.getConsoleProperties());
				loadProperties(properties,ebMSAdminProperties.getJdbcProperties());
				break;
			case EBMS_CORE:
				loadProperties(properties,ebMSAdminProperties.getServiceProperties());
				loadProperties(properties,ebMSAdminProperties.getJdbcProperties());
				break;
		}
	}
	
	public static void loadProperties(Properties properties, ConsolePropertiesFormModel consoleProperties) throws MalformedURLException
	{
		consoleProperties.setMaxItemsPerPage(Integer.parseInt(properties.getProperty("maxItemsPerPage")));
	}

	public static void loadProperties(Properties properties, ServicePropertiesFormModel serviceProperties) throws MalformedURLException
	{
		serviceProperties.setUrl(properties.getProperty("service.ebms.url"));
	}

	public static void loadProperties(Properties properties, JdbcPropertiesFormModel jdbcProperties) throws MalformedURLException
	{
		jdbcProperties.setDriver(JdbcDriver.getJdbcDriver(properties.getProperty("ebms.jdbc.driverClassName")));
		//jdbcProperties.setJdbcURL(properties.getProperty("ebms.jdbc.url"));
		parseJdbcURL(properties.getProperty("ebms.jdbc.url"),jdbcProperties);
		jdbcProperties.setUsername(properties.getProperty("ebms.jdbc.username"));
		jdbcProperties.setPassword(properties.getProperty("ebms.jdbc.password"));
		//jdbcProperties.setPreferredTestQuery(properties.getProperty("ebms.pool.preferredTestQuery"));
	}

  public static void storeProperties(EbMSAdminPropertiesFormModel ebMSAdminProperties, PropertiesType propertiesType, Writer writer) throws IOException
	{
		Properties p = new Properties();
		switch (propertiesType)
		{
			case EBMS_ADMIN:
				storeProperties(p,ebMSAdminProperties.getConsoleProperties());
				storeProperties(p,ebMSAdminProperties.getServiceProperties());
				storeProperties(p,ebMSAdminProperties.getJdbcProperties());
				break;
			case EBMS_ADMIN_EMBEDDED:
				storeProperties(p,ebMSAdminProperties.getConsoleProperties());
				storeProperties(p,ebMSAdminProperties.getJdbcProperties());
				break;
			case EBMS_CORE:
				storeProperties(p,ebMSAdminProperties.getServiceProperties());
				storeProperties(p,ebMSAdminProperties.getJdbcProperties());
				break;
		}
		p.store(writer,"EbMS Admin Console properties");
	}

  public static void storeProperties(Properties properties, ConsolePropertiesFormModel consoleProperties)
  {
		properties.setProperty("maxItemsPerPage",Integer.toString(consoleProperties.getMaxItemsPerPage()));
  }

  public static void storeProperties(Properties properties, ServicePropertiesFormModel serviceProperties)
  {
		properties.setProperty("service.ebms.url",serviceProperties.getUrl());
  }

  public static void storeProperties(Properties properties, JdbcPropertiesFormModel jdbcProperties)
  {
		properties.setProperty("ebms.jdbc.driverClassName",jdbcProperties.getDriver().getDriverClassName());
		properties.setProperty("ebms.jdbc.url",jdbcProperties.getUrl());
		properties.setProperty("ebms.jdbc.username",jdbcProperties.getUsername());
		properties.setProperty("ebms.jdbc.password",jdbcProperties.getPassword() == null ? "" : jdbcProperties.getPassword());
		properties.setProperty("ebms.pool.preferredTestQuery",jdbcProperties.getDriver().getPreferredTestQuery());
  }
  
	public static JdbcURL parseJdbcURL(String jdbcURL, JdbcURL model) throws MalformedURLException
	{
		Scanner scanner = new Scanner(jdbcURL);
		String protocol = scanner.findInLine("(://|@|:@//)");
		if (protocol != null)
		{
			String urlString = scanner.findInLine("[^/:]+(:\\d+){0,1}");
			scanner.findInLine("(/|:|;databaseName=)");
			String database = scanner.findInLine("[^;]*");
			if (urlString != null)
			{
				URL url = new URL("http://" + urlString);
				model.setHost(url.getHost());
				model.setPort(url.getPort() == -1 ? null : url.getPort());
				model.setDatabase(database);
			}
		}
		return model;
	}

  public static void testEbMSUrl(String uri) throws IOException
	{
		URL url = new URL(uri + "/cpa?wsdl");
		//URL url = new URL(uri + "/message?wsdl");
		URLConnection connection = url.openConnection();
		if (connection instanceof HttpURLConnection)
		{
			connection.setDoOutput(true);
			((HttpURLConnection)connection).setRequestMethod("GET");
			connection.connect();
			if (((HttpURLConnection)connection).getResponseCode() != 200)
				throw new RuntimeException("Status code " + ((HttpURLConnection)connection).getResponseCode());
		}
		else
			throw new IllegalArgumentException("Unknown protocol: " + uri);
	}
	
	public static void testDatabaseConnection(String driverClassName, String jdbcUrl, String username, String password) throws PropertyVetoException, SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException
	{
    ClassLoader loader = Utils.class.getClassLoader();
    Class<?> driverClass = loader.loadClass(driverClassName);
    Driver driver = (Driver)driverClass.newInstance();
    if (!driver.acceptsURL(jdbcUrl))
    	throw new IllegalArgumentException("Jdbc Url '" + jdbcUrl + "' not valid!");
    Properties info = new Properties();
    info.setProperty("user",username);
    if (password != null)
    	info.setProperty("password",password);
    Connection connection = driver.connect(jdbcUrl,info);
		connection.close();
	}

}
