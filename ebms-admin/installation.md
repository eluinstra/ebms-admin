---
sort: 3
---

# Installation and Configuration

## Requisites

- download and install Java 8 (or later)
- provide a [supported database]({{ site.baseurl }}{% link ebms-core/database.md %}) and download the appropriate JDBC driver
- download [ebms-admin-{{ site.data.ebms.core.version }}.jar](https://github.com/eluinstra/ebms-admin/releases/download/ebms-admin-{{ site.data.ebms.core.version }}/ebms-admin-{{ site.data.ebms.core.version }}.jar)

## Installation

- create directory `ebms-admin`
- copy ebms-admin-{{ site.data.ebms.core.version }}.jar to `ebms-admin`

## Configuration

- Create the file `ebms-admin.embedded.properties` in `ebms-admin` and [configure the properties]({{ site.baseurl }}{% link ebms-admin/properties.md %})
- [Configure Flyway]({{ site.baseurl }}/ebms-admin/database.html#initialize-flyway) to load the database scripts automatically or load the [database scripts]({{ site.baseurl }}/ebms-core/database.html#database-scripts) manually

The EbMS interface is configured through [properties]({{ site.baseurl }}/ebms-admin/properties.html#ebms-server). The Web and SOAP interfaces are configured through [command line options]({{ site.baseurl }}/ebms-admin/command.html#start-on-port-8000-instead-of-8080).

See [here]({{ site.baseurl }}{% link ebms-core/overview.md %}) for a functional overview of the EbMS adapter.

## Start

- start ebms-admin on default port `8080` with the SOAP interface enabled and using JDBC driver `<jdbc-driver>.jar`

```
java -cp <jdbc-driver>.jar:ebms-admin-{{ site.data.ebms.core.version }}.jar nl.clockwork.ebms.admin.StartEmbedded -soap
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

It shows how the Web interface, SOAP interface en EbMS Server endpoints are configured. You can find the different [SOAP interface]({{ site.baseurl }}{% link ebms-admin/soap.md %}) endpoints when you open your browser at [http://localhost:8080/service](http://localhost:8080/service).


See [here]({{ site.baseurl }}{% link ebms-admin/command.md %}) for all command line options. See [here]({{ site.baseurl }}{% link ebms-admin/examples.md %}) for more examples.

You can install ebms-admin as a Java service on Windows or Linux using a *Java Service Wrapper*.
