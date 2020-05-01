package nl.clockwork.ebms.admin.web.configuration;

import java.util.Arrays;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Getter
public enum JdbcDriver
{
	HSQLDB("org.hsqldb.jdbcDriver","jdbc:hsqldb:hsql://%s/%s","select 1 from information_schema.system_tables"),
	MYSQL("com.mysql.jdbc.Driver","jdbc:mysql://%s/%s","select 1"),
	MARIADB("org.mariadb.jdbc.Driver","jdbc:mysql://%s/%s","select 1"),
	POSTGRESQL("org.postgresql.Driver","jdbc:postgresql://%s/%s","select 1"),
	MSSQL("com.microsoft.sqlserver.jdbc.SQLServerDriver","jdbc:sqlserver://%s;databaseName=%s;","select 1"),
	ORACLE("oracle.jdbc.OracleDriver","jdbc:oracle:thin:@//%s/%s","select 1 from dual"),
	ORACLE_("oracle.jdbc.OracleDriver","jdbc:oracle:thin:@%s:%s","select 1 from dual");
	
	String driverClassName;
	String urlExpr;
	String preferredTestQuery;

	public static JdbcDriver getJdbcDriver(String driverClassName)
	{
		return Arrays.stream(JdbcDriver.values()).filter(j -> j.driverClassName.equals(driverClassName)).findFirst().orElse(null);
	}
	public String createJdbcURL(String hostname, Integer port, String database)
	{
		return createJdbcURL(urlExpr,hostname,port,database);
	}
	public static String createJdbcURL(String urlExpr, String hostname, Integer port, String database)
	{
		return String.format(urlExpr,Utils.createURL(hostname,port),database);
	}
}