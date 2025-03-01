---
sidebar_position: 3
---

# Installation and Configuration

## Prerequisites

- download and install Java 17 (or later)
- provide a [supported database](/ebms-core/database.md) and download the appropriate JDBC driver
- download [ebms-admin-@ebms.core.version@.jar](https://github.com/eluinstra/ebms-admin/releases/download/ebms-admin-@ebms.core.version@/ebms-admin-@ebms.core.version@.jar)

## Installation

- create directory `ebms-admin`
- copy ebms-admin-@ebms.core.version@.jar to `ebms-admin`

## Configuration

- Create the file `ebms-admin.embedded.properties` in `ebms-admin` and [configure the properties](properties)
- [Configure Flyway](database#initialize-flyway) to load the database scripts automatically or load the [database scripts](/ebms-core/database.md#database-scripts) manually

The EbMS interface is configured through [properties](properties#ebms-server). The Web and SOAP interfaces are configured through [command line options](command#start-on-port-8000).

See [here](/ebms-core/overview.md) for a functional overview of the EbMS adapter.

## Start

- start ebms-admin on default port `8080` with the SOAP interface enabled and using JDBC driver `<jdbc-driver>.jar`

```sh
java -cp <jdbc-driver>.jar:ebms-admin-@ebms.core.version@.jar nl.clockwork.ebms.admin.StartEmbedded -soap
```

When you start ebms-admin you can see the following output in your console

```sh
Using config directory: 
Web Server configured on http://localhost:8080/
SOAP Service configured on http://localhost:8080/service
EbMS Service configured on https://localhost:8888/ebms
Starting Server...
Server started.
```

It shows how the Web interface, SOAP interface en EbMS Server endpoints are configured. You can find the different [SOAP interface](soap) endpoints when you open your browser at [http://localhost:8080/service](http://localhost:8080/service).


See [here](command) for all command line options. See [here](examples) for more examples.

You can install ebms-admin as a Java service on Windows or Linux using a *Java Service Wrapper*.
