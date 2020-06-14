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

import java.util.Arrays;
import java.util.Optional;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Getter
public enum JdbcDriver
{
	HSQLDB("org.hsqldb.jdbcDriver","jdbc:hsqldb:hsql://%s/%s"),
	MYSQL("com.mysql.jdbc.Driver","jdbc:mysql://%s/%s"),
	MARIADB("org.mariadb.jdbc.Driver","jdbc:mysql://%s/%s"),
	POSTGRESQL("org.postgresql.Driver","jdbc:postgresql://%s/%s"),
	MSSQL("com.microsoft.sqlserver.jdbc.SQLServerDriver","jdbc:sqlserver://%s;databaseName=%s;"),
	ORACLE("oracle.jdbc.OracleDriver","jdbc:oracle:thin:@//%s/%s"),
	ORACLE_("oracle.jdbc.OracleDriver","jdbc:oracle:thin:@%s:%s");
	
	String driverClassName;
	String urlExpr;

	public static Optional<JdbcDriver> getJdbcDriver(String driverClassName)
	{
		return Arrays.stream(JdbcDriver.values()).filter(j -> j.driverClassName.equals(driverClassName)).findFirst();
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