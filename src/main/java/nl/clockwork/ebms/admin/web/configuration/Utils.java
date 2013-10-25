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
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

public class Utils
{
	public static void testEbMSUrl(String url)
	{
		
	}
	
	public static void testDatabaseConnection(String driverClass, String jdbcUrl, String username, String password) throws PropertyVetoException, SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException
	{
    ClassLoader loader = Utils.class.getClassLoader();
    Class<?> myClass = loader.loadClass(driverClass);
    Driver driver = (Driver)myClass.newInstance();
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
