---
sort: 7
---

# Database support

If you want to use a database other then H2 or HSQLDB, you have to provide the JDBC driver yourself.

For more on database support see [here]({{ site.baseurl }}{% link ebms-core/database.md %}).

## Initialize Flyway
To initialize Flyway for the first time run [DBMigrate](#dbmigrate).
## DBMigrate
With DBMigrate you can migrate your database using Flyway. ebms-core supports [automatic update through Flyway]({{ site.baseurl }}/ebms-core/database.html#flyway) now.
```
java -cp ebms-admin-{{ site.data.ebms.core.version }}.jar nl.clockwork.ebms.admin.DBMigrate -h
usage: DBMigrate [-ebmsVersion <arg>] [-h] [-jdbcUrl <arg>] [-password <arg>]
       [-strict] [-username <arg>]
 -ebmsVersion <arg>   set current ebmsVersion (default: none)
 -h                   print this message
 -jdbcUrl <arg>       set jdbcUrl
 -password <arg>      set password
 -strict              use strict db scripts (default: false)
 -username <arg>      set username

Valid ebmsVersions:
2.10
2.11
2.12
2.13
2.14
2.15
2.16
2.17
```
See [here]({{ site.baseurl }}{% link ebms-core/database.md %}) for the right settings for your database.  
Note: If you run Flyway for the first time on an existing database, then you have to use the argument `-ebmsVersion` with your current EbMS database version to [initialize the database](#initialize).

### Migrate PostgreSQL database
{: #migrate}
Migrate PostgreSQL database `ebms` on `localhost:5432` with credentials `username/password`
```
java -cp postgresql-42.2.16.jar:ebms-admin-{{ site.data.ebms.core.version }}.jar nl.clockwork.ebms.admin.DBMigrate \
-jdbcUrl jdbc:postgresql://localhost:5432/ebms \
-username=username -password=password
```
### Initialize existing PostgreSQL database
{: #initialize}
Initialize and migrate existing PostgreSQL database `ebms` on `localhost:5432` with credentials `username/password` on EbMS database v2.15.x
```
java -cp postgresql-42.2.16.jar:ebms-admin-{{ site.data.ebms.core.version }}.jar nl.clockwork.ebms.admin.DBMigrate \
-ebmsVersion 2.15
-jdbcUrl jdbc:postgresql://localhost:5432/ebms \
-username username -password password
```
## DBClean
With DBClean you can cleanup your database.
```
java -cp ebms-admin-{{ site.data.ebms.core.version }}.jar nl.clockwork.ebms.admin.DBClean -h
usage: DBClean [-cmd <arg>] [-cpaId <arg>] [-dateFrom <arg>] [-h]
 -cmd <arg>        objects to clean [vales: cpa|messages]
 -cpaId <arg>      the cpaId of the CPA to delete
 -dateFrom <arg>   the date from which objects will be deleted [format:
                   YYYYMMDD][default: now - 30 days]
 -h                print this message
```
### Cleanup messages from PostgreSQL database
Cleanup EbMS messages (and related objects) with persistence date before 30 days ago from PostgreSQL database
```
java -cp postgresql-42.2.16.jar:ebms-admin-{{ site.data.ebms.core.version }}.jar nl.clockwork.ebms.admin.DBMigrate -cmd messages
```
### Cleanup CPA from PostgreSQL database
Cleanup CPA with id `1` (and all related EbMS messages and other objects) from PostgreSQL database
```
java -cp postgresql-42.2.16.jar:ebms-admin-{{ site.data.ebms.core.version }}.jar nl.clockwork.ebms.admin.DBMigrate -cmd cpa -cpaId=1
```
