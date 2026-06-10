---
sidebar_position: 5
---

# Database support

The EbMS Adapter supports the following databases:

[DB2](#db2)

[H2](#h2) for test purposes

[HSQLDB](#hsqldb) for test purposes

[MariaDB](#mariadb)

[MS SQL Server](#ms-sql-server) (not recommended)

[Oracle](#oracle)

[PostgreSQL](#postgresql)


## Database Scripts

The database master scripts can be found [here](https://github.com/eluinstra/ebms-core/tree/ebms-core-@ebms.branch.version@/resources/scripts/database/master/)  
The database update scripts can be found [here](https://github.com/eluinstra/ebms-core/tree/ebms-core-@ebms.branch.version@/src/main/resources/nl/clockwork/ebms/db/migration)  
ebms-core also supports automatic database migration through [Flyway](#flyway)

## Flyway

Database migration through Flyway is enabled through the following [EbMS property](properties#database)

```properties
ebms.jdbc.update=true
```

If you already have an existing database and want to use Flyway, then you first have to [initialize Flyway](/ebms-admin/database.md#initialize-flyway). Otherwise you can just enable the property.

## Database Configuration

You can find the JDBC properties for the supported databases as well as links to the EbMS database plugins, JDBC drivers and Flyway database drivers below. You should add the properties to the [EbMS override properties](properties#override-properties) and add either add the EbMS database plugin or the JDBC driver and Flyway database driver to the classpath of ebms-admin when you start it. For example if you want to use PostgreSQL then either add [`ebms-postgres-db-plugin-2.20.6.jar`](https://github.com/eluinstra/ebms-admin/releases/download/ebms-admin-2.20.6/ebms-postgres-db-plugin-2.20.6.jar) to the classpath or add `postgresql-42.7.3.jar` and `flyway-postgresql-12.0.4.jar` to the classpath when you start ebms-admin.

### Common Properties

```properties
ebms.jdbc.username=<username>
ebms.jdbc.password=<password>
```

### DB2

```properties
# JDBC driver
ebms.jdbc.driverClassName=com.ibm.db2.jcc.DB2Driver
# or XA driver
ebms.jdbc.driverClassName=com.ibm.db2.jcc.DB2XADataSource
ebms.jdbc.url=jdbc:db2://<host>:<port>/<dbname>
```

Download the EbMS DB2 plugin [here](https://github.com/eluinstra/ebms-admin/releases/download/ebms-admin-2.20.6/ebms-db2-db-plugin-2.20.6.jar) or download the JDBC drivers [here](https://www.ibm.com/support/pages/db2-jdbc-driver-versions-and-downloads) and the Flyway DB2 driver [here](https://repo1.maven.org/maven2/org/flywaydb/flyway-database-db2/12.4.0/flyway-database-db2-12.4.0.jar) and add them to the classpath next to each other when you start ebms-admin.

### H2

```properties
# JDBC and XA driver
ebms.jdbc.driverClassName=org.h2.Driver
# or XA driver
ebms.jdbc.driverClassName=org.h2.jdbcx.JdbcDataSource
# In memory
ebms.jdbc.url=jdbc:h2:mem:<dbname>
# or file
ebms.jdbc.url=jdbc:h2:<path>
# or server
ebms.jdbc.url=jdbc:h2:tcp://<host>:<port>/<path>
```

Download the EbMS H2 plugin [here](https://github.com/eluinstra/ebms-admin/releases/download/ebms-admin-2.20.6/ebms-h2-db-plugin-2.20.6.jar) or download the JDBC drivers [here](https://www.h2database.com/html/download.html) and add it to the classpath next to each other when you start ebms-admin.

### HSQLDB

```properties
# JDBC driver
ebms.jdbc.driverClassName=org.hsqldb.jdbcDriver
# or XA driver
ebms.jdbc.driverClassName=org.hsqldb.jdbc.pool.JDBCXADataSource
# In memory
ebms.jdbc.url=jdbc:hsqldb:mem:<dbname>
# or file
ebms.jdbc.url=jdbc:hsqldb:file:<path>
# or server
ebms.jdbc.url=jdbc:hsqldb:hsql://<host>:<port>/<dbname>
```

Download the EbMS HSQLDB plugin [here](https://github.com/eluinstra/ebms-admin/releases/download/ebms-admin-2.20.6/ebms-hsqldb-db-plugin-2.20.6.jar) or download the JDBC drivers [here](http://hsqldb.org/) and the Flyway HSQLDB driver [here](https://repo1.maven.org/maven2/org/flywaydb/flyway-database-hsqldb/12.4.0/flyway-database-hsqldb-12.4.0.jar) and add them to the classpath next to each other when you start ebms-admin.

### MariaDB

```properties
# JDBC driver
ebms.jdbc.driverClassName=org.mariadb.jdbc.Driver
# or XA driver
ebms.jdbc.driverClassName=org.mariadb.jdbc.MySQLDataSource
ebms.jdbc.url=jdbc:mariadb://<host>:<port>/<dbname>
```

Download the EbMS MariaDB plugin [here](https://github.com/eluinstra/ebms-admin/releases/download/ebms-admin-2.20.6/ebms-mariadb-db-plugin-2.20.6.jar) or download the JDBC drivers [here](https://downloads.mariadb.org/connector-java/) and the Flyway MariaDB driver [here](https://repo1.maven.org/maven2/org/flywaydb/flyway-mysql/12.4.0/flyway-mysql-12.4.0.jar) and add them to the classpath next to each other when you start ebms-admin.

### MS SQL Server

:::danger
We strongly advise to **not** use a MSSQL Database with the EbMS Adapter if you expect a moderate to high message load, because MSSQL cannot handle that because of Page Locking.
:::

```properties
# JDBC driver
ebms.jdbc.driverClassName=com.microsoft.sqlserver.jdbc.SQLServerDriver
# or XA driver
ebms.jdbc.driverClassName=com.microsoft.sqlserver.jdbc.SQLServerXADataSource
ebms.jdbc.url=jdbc:sqlserver://<host>:<port>;[instanceName=<instanceName>;]databaseName=<dbname>;
```

Download the EbMS MS SQL Server plugin [here](https://github.com/eluinstra/ebms-admin/releases/download/ebms-admin-2.20.6/ebms-mssql-db-plugin-2.20.6.jar) or download the JDBC drivers [here](https://docs.microsoft.com/en-us/sql/connect/jdbc/download-microsoft-jdbc-driver-for-sql-server) and the Flyway MS SQL Server driver [here](https://repo1.maven.org/maven2/org/flywaydb/flyway-sqlserver/12.4.0/flyway-sqlserver-12.4.0.jar) and add them to the classpath next to each other when you start ebms-admin.

#### XA Driver

When using the XA driver execute the following script

```sql
EXEC sp_sqljdbc_xa_install
EXEC sp_addrolemember [SqlJDBCXAUser], '<username>'
```

### Oracle

```properties
# JDBC driver
ebms.jdbc.driverClassName=oracle.jdbc.OracleDriver
# or XA driver
ebms.jdbc.driverClassName=oracle.jdbc.xa.client.OracleXADataSource
ebms.jdbc.url=jdbc:oracle:thin:@<host>:<port>:<dbname>
```

Download the EbMS Oracle plugin [here](https://github.com/eluinstra/ebms-admin/releases/download/ebms-admin-2.20.6/ebms-oracle-db-plugin-2.20.6.jar) or download the JDBC drivers [here](https://www.oracle.com/database/technologies/appdev/jdbc-downloads.html) and the Flyway Oracle driver [here](https://repo1.maven.org/maven2/org/flywaydb/flyway-database-oracle/12.4.0/flyway-database-oracle-12.4.0.jar) and add them to the classpath next to each other when you start ebms-admin.

#### XA Driver

When using the XA driver execute the following script

```sql
grant select on sys.dba_pending_transactions to <username>;
grant select on sys.pending_trans$ to <username>;
grant select on sys.dba_2pc_pending to <username>;
```

### PostgreSQL

```properties
# JDBC driver
ebms.jdbc.driverClassName=org.postgresql.Driver
# or XA driver
ebms.jdbc.driverClassName=org.postgresql.xa.PGXADataSource
ebms.jdbc.url=jdbc:postgresql://<host>:<port>/<dbname>
```

Download the EbMS PostgreSQL plugin [here](https://github.com/eluinstra/ebms-admin/releases/download/ebms-admin-2.20.6/ebms-postgresql-db-plugin-2.20.6.jar) or download the JDBC drivers [here](https://jdbc.postgresql.org/download.html) and the Flyway PostgreSQL driver [here](https://repo1.maven.org/maven2/org/flywaydb/flyway-database-oracle/12.4.0/flyway-database-oracle-12.4.0.jar) and add them to the classpath next to each other when you start ebms-admin.

#### XA Driver

If you get the following error when using the XA driver

```properties
org.postgresql.util.PSQLException: ERROR: prepared transactions are disabled Hint: Set max_prepared_transactions to a nonzero value.
```

then enable the `max_prepared_transactions` attribute in `postgresql.conf`
