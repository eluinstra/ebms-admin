---
sort: 3
---

# Installation and Configuration

## Requisites

- download and install Java 8 (or later)
- provide a database and download the JDBC driver. See [here]({{ site.baseurl }}{% link ebms-core/database.md %}) for the supported databases.
- download [ebms-admin-{{ site.data.ebms.core.version }}.jar](https://github.com/eluinstra/ebms-admin/releases/download/ebms-admin-{{ site.data.ebms.core.version }}/ebms-admin-{{ site.data.ebms.core.version }}.jar)

## Install

- create directory `ebms-admin`
- copy ebms-admin-{{ site.data.ebms.core.version }}.jar to `ebms-admin`

## Configure

- Create the file `ebms-admin.embedded.properties` in `ebms-admin` and [configure]({{ site.baseurl }}{% link ebms-admin/properties.md %}) the properties
- [Configure Flyway]({{ site.baseurl }}/ebms-admin/database.html#initialize-flyway) if you want to migrate your database automatically

See [here]({{ site.baseurl }}{% link ebms-core/overview.md %}) for a functional overview of the EbMS adapter.

## Start

- start ebms-admin on default port `8080` with the SOAP interface enabled and using a JDBC driver `<jdbc-driver>.jar`

```
java -cp <jdbc-driver>.jar:ebms-admin-{{ site.data.ebms.core.version }}.jar nl.clockwork.ebms.admin.StartEmbedded -soap
```

See [here]({{ site.baseurl }}{% link ebms-admin/command.md %}) for all command line options.

You can install ebms-admin as a Java service on Windows or Linux using a *Java Service Wrapper*.