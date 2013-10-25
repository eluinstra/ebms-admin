package nl.clockwork.ebms.admin.web.configuration;

public class Constants
{
	private static final String dbHostProperty = "#dbHost";
	private static final String dbPortProperty = "#dbPort";
	private static final String dbNameProperty = "#dbName";
	
	public enum JdbcDriver
	{
		HSQLDB("org.hsqldb.jdbcDriver","jdbc:hsqldb:hsql://" + dbHostProperty + ":" + dbPortProperty + "/" + dbNameProperty,"select 1 from information_schema.system_tables"),
		MYSQL("com.mysql.jdbc.Driver","jdbc:mysql://" + dbHostProperty + ":" + dbPortProperty + "/" + dbNameProperty,"select 1"),
		POSTGRESQL("org.postgresql.Driver","jdbc:postgresql://" + dbHostProperty + ":" + dbPortProperty + "/" + dbNameProperty,"select 1"),
		MSSQL("net.sourceforge.jtds.jdbc.Driver","jdbc:jtds:sqlserver://" + dbHostProperty + ":" + dbPortProperty + "/" + dbNameProperty,"select 1"),
		//MSSQL("com.microsoft.sqlserver.jdbc.SQLServerDriver","jdbc:sqlserver://" + dbHostProperty + ":" + dbPortProperty + ";databaseName=" + dbNameProperty + ";","select 1"),
		ORACLE("oracle.jdbc.OracleDriver","jdbc:oracle:thin:@" + dbHostProperty + ":" + dbPortProperty + ":" + dbNameProperty,"select 1 from dual");
		
		private String driverClassName;
		private String urlExpr;
		private String preferredTestQuery;

		private JdbcDriver(String driverClassName, String urlExpr, String preferredTestQuery)
		{
			this.driverClassName = driverClassName;
			this.urlExpr = urlExpr;
			this.preferredTestQuery = preferredTestQuery;
		}
		public String getDriverClassName()
		{
			return driverClassName;
		}
		public String getUrlExpr()
		{
			return urlExpr;
		}
		public String getPreferredTestQuery()
		{
			return preferredTestQuery;
		}
		public String createJdbcURL(String hostname, int port, String database)
		{
			urlExpr = urlExpr.replaceAll(dbHostProperty,hostname);
			urlExpr = urlExpr.replaceAll(dbPortProperty,Integer.toString(port));
			urlExpr = urlExpr.replaceAll(dbNameProperty,database);
			return urlExpr;
		}
		public static String createJdbcURL(String urlExpr, String hostname, int port, String database)
		{
			urlExpr = urlExpr.replaceAll(dbHostProperty,hostname);
			urlExpr = urlExpr.replaceAll(dbPortProperty,Integer.toString(port));
			urlExpr = urlExpr.replaceAll(dbNameProperty,database);
			return urlExpr;
		}
	}
}