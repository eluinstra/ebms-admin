---
sidebar_position: 12
---

# EbMS Admin 2.19 Migration Guide

## Before you start

We assume that you have installed EbMS Admin 2.18.x.

### Update connected applications

If your application uses the EbMS Admin REST API, then you should update your application to use the new version of the REST API.

:::danger
MySQL is not supported anymore
:::

:::danger
Apache HttpClient is removed
:::

:::danger
Bitronix TransactionManager is removed
:::

:::info
Duplicate messages are not stored anymore
:::

:::info
The default test database has changed from HSQLDB to H2.
:::

## Upgrade to EbMS Admin 2.19.x

### Upgrade Java

Install the latest JRE or JDK 11.

### Shutdown EbMS Admin 2.18.x

Shutdown your current EbMS Admin application.

### Migrate MySQL Database

If you are using a MySQL database, you have to migrate your data to another database. After that you have to [configure](/ebms-core/database.md#database-configuration) the new database.
We removed MySQL support, because it does not comply to the ANSI SQL 2008 specifications. 

### Update Database

If you are **not** using [Flyway](database#initialize-flyway), you have to update the database manually by applying the V2.19.0__Update.sql script from this [directory](https://github.com/eluinstra/ebms-core/tree/ebms-core-2.19.x/src/main/resources/nl/clockwork/ebms/db/migration) to your database.

### Reconfigure HttpClient

If you configured Apache HttpClient, you have to switch to the default HttpClient. See [here](/ebms-core/properties.md#httpclient) for the configuration.

### Reconfigure TransactionManager

If you configured Bitronix TransactionManager, you have to switch to the Atomikos. See [here](/ebms-core/properties.md#transactionmanager) for the configuration.

### Upgrade EbMS Admin

Replace the ebms-admin-2.18.x.jar by the latest ebms-admin-2.19.x.jar and start the EbMS Admin application.
