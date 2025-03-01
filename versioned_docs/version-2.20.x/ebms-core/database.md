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

You can find the JDBC settings for the supported databases as well as links to the JDBC drivers below.

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

Download drivers [here](https://www.ibm.com/support/pages/db2-jdbc-driver-versions-and-downloads)

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

### MariaDB

```properties
# JDBC driver
ebms.jdbc.driverClassName=org.mariadb.jdbc.Driver
# or XA driver
ebms.jdbc.driverClassName=org.mariadb.jdbc.MySQLDataSource
ebms.jdbc.url=jdbc:mariadb://<host>:<port>/<dbname>
```

Download drivers [here](https://downloads.mariadb.org/connector-java/)

Download the right flyway-mysql driver [here](https://mvnrepository.com/artifact/org.flywaydb/flyway-mysql) and add it to the classpath next to the database driver
Check the pom.xml of ebms-admin for the right version of the flyway-mysql library

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

Download drivers [here](https://docs.microsoft.com/en-us/sql/connect/jdbc/download-microsoft-jdbc-driver-for-sql-server)

Download the right flyway-sqlserver driver [here](https://mvnrepository.com/artifact/org.flywaydb/flyway-sqlserver) and add it to the classpath next to the database driver
Check the pom.xml of ebms-admin for the right version of the flyway-sqlserver library

#### XA Driver

When using the XA driver execute the following script

```sql
EXEC sp_sqljdbc_xa_install
EXEC sp_addrolemember [SqlJDBCXAUser], '<username>'
```

#### Quartz

When [`deliveryTaskHandler.type`](properties#deliverytaskhandler) is set to `QUARTZ`or `QUARTZ_JMS` then set

```properties
deliveryTaskHandler.quartz.jdbc.selectWithLockSQL=SELECT * FROM {0}LOCKS UPDLOCK WHERE LOCK_NAME = ?
```

### Oracle

```properties
# JDBC driver
ebms.jdbc.driverClassName=oracle.jdbc.OracleDriver
# or XA driver
ebms.jdbc.driverClassName=oracle.jdbc.xa.client.OracleXADataSource
ebms.jdbc.url=jdbc:oracle:thin:@<host>:<port>:<dbname>
```

Download drivers [here](https://www.oracle.com/database/technologies/appdev/jdbc-downloads.html)

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

Download drivers [here](https://jdbc.postgresql.org/download.html)

#### XA Driver

If you get the following error when using the XA driver

```properties
org.postgresql.util.PSQLException: ERROR: prepared transactions are disabled Hint: Set max_prepared_transactions to a nonzero value.
```

then enable the `max_prepared_transactions` attribute in `postgresql.conf`
