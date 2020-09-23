---
sort: 4
---

# Command Line Options

## Show help

```sh
java -cp ebms-admin-{{ site.data.ebms.core.version }}.jar nl.clockwork.ebms.admin.StartEmbedded -h
usage: Start [-authentication] [-cipherSuites <arg>]
       [-clientAuthentication] [-clientCertificateHeader <arg>]
       [-clientTrustStorePassword <arg>] [-clientTrustStorePath <arg>]
       [-clientTrustStoreType <arg>] [-configDir <arg>] [-connectionLimit
       <arg>] [-disableEbMSClient] [-disableEbMSServer] [-h] [-headless]
       [-host <arg>] [-hsqldb] [-hsqldbDir <arg>] [-jmx] [-jmxAccessFile
       <arg>] [-jmxPasswordFile <arg>] [-jmxPort <arg>] [-keyStorePassword
       <arg>] [-keyStorePath <arg>] [-keyStoreType <arg>] [-path <arg>]
       [-port <arg>] [-protocols <arg>] [-queriesPerSecond <arg>] [-soap]
       [-ssl] [-trustStorePassword <arg>] [-trustStorePath <arg>]
       [-trustStoreType <arg>] [-userQueriesPerSecond <arg>]
 -authentication                   use basic / client certificate authentication
 -cipherSuites <arg>               set ssl cipherSuites
 -clientAuthentication             require ssl client authentication
 -clientCertificateHeader <arg>    set client certificate header
 -clientTrustStorePassword <arg>   set client truststore password
 -clientTrustStorePath <arg>       set client truststore path
 -clientTrustStoreType <arg>       set client truststore type (deault=PKCS12)
 -configDir <arg>                  set config directory (default=current dir)
 -connectionLimit <arg>            set connection limit (default: none)
 -disableEbMSClient                disable ebms client
 -disableEbMSServer                disable ebms server
 -h                                print this message
 -headless                         start without web interface
 -host <arg>                       set host
 -hsqldb                           start hsqldb server
 -hsqldbDir <arg>                  set hsqldb location (default: hsqldb)
 -jmx                              start jmx server (default: false)
 -jmxAccessFile <arg>              set jmx access file
 -jmxPasswordFile <arg>            set jmx password file
 -jmxPort <arg>                    set jmx port
 -keyStorePassword <arg>           set keystore password
 -keyStorePath <arg>               set keystore path
 -keyStoreType <arg>               set keystore type (deault=PKCS12)
 -path <arg>                       set path
 -port <arg>                       set port
 -protocols <arg>                  set ssl protocols
 -queriesPerSecond <arg>           set requests per second limit (default: none)
 -soap                             start soap service
 -ssl                              use ssl
 -trustStorePassword <arg>         set truststore password
 -trustStorePath <arg>             set truststore path
 -trustStoreType <arg>             set truststore type (deault=PKCS12)
 -userQueriesPerSecond <arg>       set requests per user per secondlimit (default: none)
```

## Start with the embedded HSQLDB server

```sh
java -cp ebms-admin-{{ site.data.ebms.core.version }}.jar nl.clockwork.ebms.admin.StartEmbedded -hsqldb
```

## Start using a PostgreSQL JDBC driver

```sh
java -cp postgresql-42.2.14.jar:ebms-admin-{{ site.data.ebms.core.version }}.jar nl.clockwork.ebms.admin.StartEmbedded
```

## Start on port 8000 (instead of 8080)

Start SOAP/Web interface on port `8000` (instead of `8080`)

```sh
java -cp ebms-admin-{{ site.data.ebms.core.version }}.jar nl.clockwork.ebms.admin.StartEmbedded -port 8000
```

## Start with SOAP interface

```sh
java -cp ebms-admin-{{ site.data.ebms.core.version }}.jar nl.clockwork.ebms.admin.StartEmbedded -soap
```

## Start with SOAP interface and without a Web interface

```sh
java -cp ebms-admin-{{ site.data.ebms.core.version }}.jar nl.clockwork.ebms.admin.StartEmbedded -soap -headless
```

## Start with config directory conf/
{: #configDir}

By default the config directory is the directory from which you start the ebms-admin. You can change the config directory by setting `configDir`

```sh
java -cp ebms-admin-{{ site.data.ebms.core.version }}.jar nl.clockwork.ebms.admin.StartEmbedded -configDir conf/
```

## Start with a custom log4j2 file conf/log4j2.xml

```sh
java -Dlog4j.configurationFile=conf/log4j2.xml -cp ebms-admin-{{ site.data.ebms.core.version }}.jar nl.clockwork.ebms.admin.StartEmbedded
```

## Start without using the default Java truststore

```sh
java -Djavax.net.ssl.trustStore= -cp ebms-admin-{{ site.data.ebms.core.version }}.jar nl.clockwork.ebms.admin.StartEmbedded
```

## Start with HTTPS

Start with HTTPS SOAP/Web interface using keystore `keystore.p12`

```sh
java -Djavax.net.ssl.trustStore= -cp ebms-admin-{{ site.data.ebms.core.version }}.jar nl.clockwork.ebms.admin.StartEmbedded \
-ssl -keyStoreType PKCS12 -keyStorePath keystore.p12 -keyStorePassword password
```

## Start with HTTPS and client authentication

Start with HTTPS SOAP/Web interface using keystore `keystore.p12`  
and require SSL client authentication using truststore `truststore.p12` (which holds the client's certificate chain)

```sh
java -Djavax.net.ssl.trustStore= -cp ebms-admin-{{ site.data.ebms.core.version }}.jar nl.clockwork.ebms.admin.StartEmbedded \
-ssl -keyStoreType PKCS12 -keyStorePath keystore.p12 -keyStorePassword password \
-clientAuthentication -trustStoreType PKCS12 -trustStorePath truststore.p12 -trustStorePassword password
```

## Start with HTTPS, client authentication and client certifiate authentication

Start with HTTPS SOAP/Web interface using keystore `keystore.p12`  
and require SSL client authentication using truststore `truststore.p12` (which holds the client's certificate chain)  
and authenticate client SSL certificate using `clientTruststore.p12` (which holds the client's certificate)

```sh
java -Djavax.net.ssl.trustStore= -cp ebms-admin-{{ site.data.ebms.core.version }}.jar nl.clockwork.ebms.admin.StartEmbedded \
-ssl -keyStoreType PKCS12 -keyStorePath keystore.p12 -keyStorePassword password \
-clientAuthentication -trustStoreType PKCS12 -trustStorePath truststore.p12 -trustStorePassword password \
-authentication -clientTrustStoreType PKCS12 -clientTrustStorePath clientTruststore.p12 -clientTrustStorePassword password
```

## Start in EbMS Server mode

```sh
java -Djavax.net.ssl.trustStore= -cp ebms-admin-{{ site.data.ebms.core.version }}.jar nl.clockwork.ebms.admin.StartEmbedded -headless -disableEbMSClient
```

## Start in EbMS Client mode

```sh
java -Djavax.net.ssl.trustStore= -cp ebms-admin-{{ site.data.ebms.core.version }}.jar nl.clockwork.ebms.admin.StartEmbedded -headless -disableEbMSServer
```

## Start in SOAP API mode

```sh
java -Djavax.net.ssl.trustStore= -cp ebms-admin-{{ site.data.ebms.core.version }}.jar nl.clockwork.ebms.admin.StartEmbedded -soap -disableEbMSServer -disableEbMSClient
```

## Start using basic authentication

Start using basic authentication on SOAP/Web interface

```sh
java -cp ebms-admin-{{ site.data.ebms.core.version }}.jar nl.clockwork.ebms.admin.StartEmbedded -authentication
```

## Start hsqldb and ebms-admin as 2 separate applications

```sh
java -cp ebms-admin-{{ site.data.ebms.core.version }}.jar org.hsqldb.server.Server --database.0 file:hsqldb/ebms --dbname.0 ebms -port 9001
java -Djavax.net.ssl.trustStore= -cp ebms-admin-{{ site.data.ebms.core.version }}.jar nl.clockwork.ebms.admin.StartEmbedded -soap
```

When you start you can see the following information in the console

```sh
Using config directory:
[Server@f0da945]: Database [index=0, id=0, db=file:hsqldb/ebms, alias=ebms] opened successfully in 420 ms.
[Server@f0da945]: Startup sequence completed in 424 ms.
[Server@f0da945]: 2019-12-27 15:07:57.319 HSQLDB server 2.5.0 is online on port 9001
[Server@f0da945]: To close normally, connect and execute SHUTDOWN SQL
[Server@f0da945]: From command line, use [Ctrl]+[C] to abort abruptly
EbMS tables already exist
Using keyStore jar:file:/home/digipoort/ebms-admin-{{ site.data.ebms.core.version }}.jar!/keystore.p12
Using trustStore jar:file:/home/digipoort/ebms-admin-{{ site.data.ebms.core.version }}.jar!/truststore.p12
Web server configured on https://localhost:8443/
SOAP service configured on https://localhost:8443/service
EbMS service configured on https://0.0.0.0:8888/digipoortStub
Configuring web server client certificate authentication:
Using clientTrustStore jar:file:/home/digipoort/ebms-admin-{{ site.data.ebms.core.version }}.jar!/clientTruststore.p12
Starting web server...
```
