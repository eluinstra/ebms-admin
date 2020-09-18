---
sort: 5
---

# Database support

The database master scripts can be found [here](https://github.com/eluinstra/ebms-core/tree/ebms-core-2.17.x/resources/scripts/database/master/)  
The database update scripts can be found [here](https://github.com/eluinstra/ebms-core/tree/ebms-core-2.17.x/src/main/resources/nl/clockwork/ebms/db/migration)  

The EbMS Adapter supports the following databases:
- [DB2](#db2)
- [H2](#h2)
- [HSQLDB](#hsqldb)
- [MariaDB](#mariadb)
- [MS SQL Server](#ms-sql-server)
- [MySQL](#mysql)
- [Oracle](#oracle)
- [PostgreSQL](#postgresql)

## JDBC Common
```
ebms.jdbc.username=<username>
ebms.jdbc.password=<password>
```
## DB2
```
# JDBC driver
ebms.jdbc.driverClassName=com.ibm.db2.jcc.DB2Driver
# or XA driver
ebms.jdbc.driverClassName=com.ibm.db2.jcc.DB2XADataSource
ebms.jdbc.url=jdbc:db2://<host>:<port>/<dbname>
```
## H2
```
# JDBC and XA driver
ebms.jdbc.driverClassName=org.h2.Driver
# In memory
ebms.jdbc.url=jdbc:h2:mem:<dbname>
# or file
ebms.jdbc.url=jdbc:h2:<path>
# or server
ebms.jdbc.url=jdbc:h2:tcp://<host>:<port>/<path>
```
## HSQLDB
```
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
## MariaDB
```
# JDBC driver
ebms.jdbc.driverClassName=org.mariadb.jdbc.Driver
# or XA driver
ebms.jdbc.driverClassName=
ebms.jdbc.url=jdbc:mysql://<host>:<port>/<dbname>
```
## MS SQL Server
```
# JDBC driver
ebms.jdbc.driverClassName=com.microsoft.sqlserver.jdbc.SQLServerDriver
# or XA driver
ebms.jdbc.driverClassName=com.microsoft.sqlserver.jdbc.SQLServerXADataSource
ebms.jdbc.url=jdbc:sqlserver://<host>:<port>;[instanceName=<instanceName>;]databaseName=<dbname>;
```
## MySQL
```
# JDBC driver
ebms.jdbc.driverClassName=com.mysql.cj.jdbc.Driver
# or XA driver
ebms.jdbc.driverClassName=com.mysql.cj.jdbc.MysqlXADataSource
ebms.jdbc.url=jdbc:mysql://<host>:<port>/<dbname>
```
## Oracle
```
# JDBC driver
ebms.jdbc.driverClassName=oracle.jdbc.OracleDriver
# or XA driver
ebms.jdbc.driverClassName=oracle.jdbc.xa.client.OracleXADataSource
ebms.jdbc.url=jdbc:oracle:thin:@<host>:<port>:<dbname>
```
## PostgreSQL
```
# JDBC driver
ebms.jdbc.driverClassName=org.postgresql.Driver
# or XA driver
ebms.jdbc.driverClassName=org.postgresql.xa.PGXADataSource
ebms.jdbc.url=jdbc:postgresql://<host>:<port>/<dbname>
```
