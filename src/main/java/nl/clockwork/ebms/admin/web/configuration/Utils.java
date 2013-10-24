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
    info.setProperty("password",password);
    Connection connection = driver.connect(jdbcUrl,info);
		connection.close();
	}

}
