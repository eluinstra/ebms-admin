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
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Scanner;

import nl.clockwork.ebms.admin.web.configuration.Constants.JdbcDriver;
import nl.clockwork.ebms.admin.web.configuration.EbMSAdminPropertiesPage.EbMSAdminPropertiesFormModel;

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
	
	public static void loadProperties(EbMSAdminPropertiesFormModel model, FileReader reader) throws IOException
	{
		Properties properties = new Properties();
		properties.load(reader);
		model.setMaxItemsPerPage(Integer.parseInt(properties.getProperty("maxItemsPerPage")));
		model.setEbMSURL(properties.getProperty("service.ebms.url"));
		model.setJdbcDriver(JdbcDriver.getJdbcDriver(properties.getProperty("ebms.jdbc.driverClassName")));
		//model.setJdbcURL(properties.getProperty("ebms.jdbc.url"));
		parseJdbcURL(model,model.getJdbcDriver(),properties.getProperty("ebms.jdbc.url"));
		model.setJdbcUsername(properties.getProperty("ebms.jdbc.username"));
		model.setJdbcPassword(properties.getProperty("ebms.jdbc.password"));
		//model.setPreferredTestQuery(properties.getProperty("ebms.pool.preferredTestQuery"));
	}

	private static void parseJdbcURL(EbMSAdminPropertiesFormModel model, JdbcDriver jdbcDriver, String jdbcURL) throws MalformedURLException
	{
		Scanner scanner = new Scanner(jdbcURL);
		String protocol = scanner.findInLine("(://|@|:@//)");
		if (protocol != null)
		{
			//String urlString = scanner.findInLine(".*:\\d+");
			String urlString = scanner.findInLine("[^/:]+(:\\d+){0,1}");
			scanner.findInLine("(/|:|;databaseName=)");
			String database = scanner.findInLine("[^;]*");
			if (urlString != null)
			{
				URL url = new URL("http://" + urlString);
				model.setJdbcHost(url.getHost());
				model.setJdbcPort(url.getPort() == -1 ? null : url.getPort());
				model.setJdbcDatabase(database);
			}
		}
	}

  public static void storeProperties(EbMSAdminPropertiesFormModel model, Writer writer) throws IOException
	{
		Properties properties = new Properties();
		properties.setProperty("maxItemsPerPage",Integer.toString(model.getMaxItemsPerPage()));
		properties.setProperty("service.ebms.url",model.getEbMSURL());
		properties.setProperty("ebms.jdbc.driverClassName",model.getJdbcDriver().getDriverClassName());
		properties.setProperty("ebms.jdbc.url",model.getJdbcURL());
		properties.setProperty("ebms.jdbc.username",model.getJdbcUsername());
		properties.setProperty("ebms.jdbc.password",model.getJdbcPassword() == null ? "" : model.getJdbcPassword());
		properties.setProperty("ebms.pool.preferredTestQuery",model.getJdbcDriver().getPreferredTestQuery());
		properties.store(writer,"EbMS Admin Console properties.");
	}

  public static void testEbMSUrl(String url)
	{
		
	}
	
	public static void testDatabaseConnection(String driverClassName, String jdbcUrl, String username, String password) throws PropertyVetoException, SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException
	{
    ClassLoader loader = Utils.class.getClassLoader();
    Class<?> driverClass = loader.loadClass(driverClassName);
    Driver driver = (Driver)driverClass.newInstance();
    if (!driver.acceptsURL(jdbcUrl))
    	throw new IllegalArgumentException("Jdbc Url '" + jdbcUrl + "' not valid.");
    Properties info = new Properties();
    info.setProperty("user",username);
    if (password != null)
    	info.setProperty("password",password);
    Connection connection = driver.connect(jdbcUrl,info);
		//TODO execute preferredTestQuery
		connection.close();
	}

}
