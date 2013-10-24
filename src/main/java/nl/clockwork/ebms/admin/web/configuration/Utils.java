package nl.clockwork.ebms.admin.web.configuration;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class Utils
{
	public static void testEbMSUrl(String url)
	{
		
	}
	
	public static void testDatabaseConnection(String driverClass, String jdbcUrl, String username, String password) throws PropertyVetoException, SQLException
	{
		ComboPooledDataSource ds = new ComboPooledDataSource();
		ds.setDriverClass(driverClass);
		ds.setJdbcUrl(jdbcUrl);
		ds.setUser(username);
		ds.setPassword(password);
		ds.setCheckoutTimeout(10000);
		Connection connection = ds.getConnection();
		connection.close();
	}
}
