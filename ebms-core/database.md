---
sort: 5
---

# Database support

The EbMS Adapter supports the following databases

- [DB2](#db2)
- [H2](#h2)
- [HSQLDB](#hsqldb)
- [MariaDB](#mariadb)
- [MS SQL Server](#ms-sql-server)
- [MySQL](#mysql)
- [Oracle](#oracle)
- [PostgreSQL](#postgresql)

## Database Scripts

The database master scripts can be found [here](https://github.com/eluinstra/ebms-core/tree/ebms-core-{{ site.ebms.branch.version }}/resources/scripts/database/master/)  
The database update scripts can be found [here](https://github.com/eluinstra/ebms-core/tree/ebms-core-{{ site.ebms.branch.version }}/src/main/resources/nl/clockwork/ebms/db/migration)  
ebms-core also supports automatic database migration through [Flyway](#flyway)

## JDBC Settings

You can find the JDBC settings for the supported databases as well as links to the JDBC drivers below.

### JDBC Common

```properties
ebms.jdbc.username=<username>
ebms.jdbc.password=<password>
```

### DB2

since v2.14.0

```properties
# JDBC driver
ebms.jdbc.driverClassName=com.ibm.db2.jcc.DB2Driver
# or XA driver
ebms.jdbc.driverClassName=com.ibm.db2.jcc.DB2XADataSource
ebms.jdbc.url=jdbc:db2://<host>:<port>/<dbname>
```

Tested with DB2 11.5.4.0

Download drivers [here](https://www.ibm.com/support/pages/db2-jdbc-driver-versions-and-downloads)

### H2

since [v2.17.2]({{ site.baseurl }}/ebms-core/release.html#ebms-core-2172jar)

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

Tested with H2 1.4.200

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

Tested with HSQLDB 2.5.1

### MariaDB

```properties
# JDBC driver
ebms.jdbc.driverClassName=org.mariadb.jdbc.Driver
# or XA driver
ebms.jdbc.driverClassName=org.mariadb.jdbc.MySQLDataSource
ebms.jdbc.url=jdbc:mariadb://<host>:<port>/<dbname>
```

Tested with MariaDB 10.3.22

Download drivers [here](https://downloads.mariadb.org/connector-java/)

Download the right flyway-mysql driver [here](https://mvnrepository.com/artifact/org.flywaydb/flyway-mysql) and add it to the classpath next to the database driver
Check the pom.xml of ebms-admin for the right version of the flyway-mysql library

### MS SQL Server

```properties
# JDBC driver
ebms.jdbc.driverClassName=com.microsoft.sqlserver.jdbc.SQLServerDriver
# or XA driver
ebms.jdbc.driverClassName=com.microsoft.sqlserver.jdbc.SQLServerXADataSource
ebms.jdbc.url=jdbc:sqlserver://<host>:<port>;[instanceName=<instanceName>;]databaseName=<dbname>;
```

Tested with MS SQL Server 2019

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

When [`deliveryTaskHandler.type`]({{ site.baseurl }}/ebms-core/properties.html#deliverytaskhandler) is set to `QUARTZ`or `QUARTZ_JMS` then set

```properties
deliveryTaskHandler.quartz.jdbc.selectWithLockSQL=SELECT * FROM {0}LOCKS UPDLOCK WHERE LOCK_NAME = ?
```

### MySQL

```note
MySQL support is removed in version 2.19.0
```

```properties
# JDBC driver
ebms.jdbc.driverClassName=com.mysql.cj.jdbc.Driver
# or XA driver
ebms.jdbc.driverClassName=com.mysql.cj.jdbc.MysqlXADataSource
ebms.jdbc.url=jdbc:mysql://<host>:<port>/<dbname>
```

Tested with MySQL 8.0.21

Download drivers [here](https://dev.mysql.com/downloads/connector/j/)

#### XA Driver

When using the XA driver add line the following line to `my.ini` or `my.cnf`

```properties
default-time-zone='+02:00'
```

### Oracle

```properties
# JDBC driver
ebms.jdbc.driverClassName=oracle.jdbc.OracleDriver
# or XA driver
ebms.jdbc.driverClassName=oracle.jdbc.xa.client.OracleXADataSource
ebms.jdbc.url=jdbc:oracle:thin:@<host>:<port>:<dbname>
```

Tested with Oracle XE 18c

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

Tested with PostgreSQL 12.4

Download drivers [here](https://jdbc.postgresql.org/download.html)

#### XA Driver

If you get the following error when using the XA driver

```properties
org.postgresql.util.PSQLException: ERROR: prepared transactions are disabled Hint: Set max_prepared_transactions to a nonzero value.
```

then enable the `max_prepared_transactions` attribute in `postgresql.conf`


## Flyway

Database migration through Flyway is enabled through the following [EbMS property]({{ site.baseurl }}/ebms-core/properties.html#database) (since [v2.17.2]({{ site.baseurl }}/ebms-core/release.html#ebms-core-2172jar))

```properties
ebms.jdbc.update=true
```

If you already have an existing database and want to use Flyway, then you first have to [initialize Flyway]({{ site.baseurl }}/ebms-admin/database.html#initialize-flyway). Otherwise you can just enable the property.
