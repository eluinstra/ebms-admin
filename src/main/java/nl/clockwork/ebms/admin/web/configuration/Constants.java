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

public class Constants
{
	public enum JdbcDriver
	{
		HSQLDB("org.hsqldb.jdbcDriver","jdbc:hsqldb:hsql://%s/%s","select 1 from information_schema.system_tables"),
		MYSQL("com.mysql.jdbc.Driver","jdbc:mysql://%s/%s","select 1"),
		POSTGRESQL("org.postgresql.Driver","jdbc:postgresql://%s/%s","select 1"),
		MSSQL("com.microsoft.sqlserver.jdbc.SQLServerDriver","jdbc:sqlserver://%s;databaseName=%s;","select 1"),
		MSSQL_JTDS("net.sourceforge.jtds.jdbc.Driver","jdbc:jtds:sqlserver://%s/%s","select 1"),
		ORACLE("oracle.jdbc.OracleDriver","jdbc:oracle:thin:@//%s/%s","select 1 from dual"),
		ORACLE_("oracle.jdbc.OracleDriver","jdbc:oracle:thin:@%s:%s","select 1 from dual");
		
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
		public static JdbcDriver getJdbcDriver(String driverClassName)
		{
			for (JdbcDriver jdbcDriver : JdbcDriver.values())
				if (jdbcDriver.driverClassName.equals(driverClassName))
					return jdbcDriver;
			return null;
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
}
