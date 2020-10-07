---
sort: 4
---

# Command Line Options

### Show help

```sh
java -cp ebms-admin-{{ site.ebms.core.version }}.jar nl.clockwork.ebms.admin.StartEmbedded -h
usage: StartEmbedded [-authentication] [-cipherSuites <arg>]
       [-clientAuthentication] [-clientCertificateHeader <arg>]
       [-clientTrustStorePassword <arg>] [-clientTrustStorePath <arg>]
       [-clientTrustStoreType <arg>] [-configDir <arg>] [-connectionLimit
       <arg>] [-disableEbMSClient] [-disableEbMSServer] [-h] [-headless]
       [-health] [-healthPort <arg>] [-host <arg>] [-hsqldb] [-hsqldbDir
       <arg>] [-jmx] [-jmxAccessFile <arg>] [-jmxPasswordFile <arg>]
       [-jmxPort <arg>] [-keyStorePassword <arg>] [-keyStorePath <arg>]
       [-keyStoreType <arg>] [-path <arg>] [-port <arg>] [-protocols
       <arg>] [-queriesPerSecond <arg>] [-soap] [-ssl]
       [-trustStorePassword <arg>] [-trustStorePath <arg>]
       [-trustStoreType <arg>] [-userQueriesPerSecond <arg>]
 -authentication                   use basic | client certificate authentication
 -cipherSuites <arg>               set ssl cipherSuites [default: <none>]
 -clientAuthentication             require ssl client authentication
 -clientCertificateHeader <arg>    set client certificate header [default: <none>]
 -clientTrustStorePassword <arg>   set client truststore password [default: <none>]
 -clientTrustStorePath <arg>       set client truststore path [default: <none>]
 -clientTrustStoreType <arg>       set client truststore type [default: PKCS12]
 -configDir <arg>                  set config directory [default: <startup_directory>]
 -connectionLimit <arg>            set connection limit [default: <none>]
 -disableEbMSClient                disable ebms client
 -disableEbMSServer                disable ebms server
 -h                                print this message
 -headless                         start without web interface
 -health                           start health service
 -healthPort <arg>                 set health service port [default: 8008]
 -host <arg>                       set host [default: 0.0.0.0]
 -hsqldb                           start hsqldb server
 -hsqldbDir <arg>                  set hsqldb location [default: hsqldb]
 -jmx                              start jmx server
 -jmxAccessFile <arg>              set jmx access file [default: none]
 -jmxPasswordFile <arg>            set jmx password file [default: none]
 -jmxPort <arg>                    set jmx port [default: 1999]
 -keyStorePassword <arg>           set keystore password [default: password]
 -keyStorePath <arg>               set keystore path [default: nl/clockwork/ebms/keystore.p12]
 -keyStoreType <arg>               set keystore type [default: PKCS12]
 -path <arg>                       set path [default: /]
 -port <arg>                       set port [default: <8080|8443>]
 -protocols <arg>                  set ssl protocols [default: <none>]
 -queriesPerSecond <arg>           set requests per second limit [default: <none>]
 -soap                             start soap service
 -ssl                              use ssl
 -trustStorePassword <arg>         set truststore password [default: <none>]
 -trustStorePath <arg>             set truststore path [default: <none>]
 -trustStoreType <arg>             set truststore type [default: PKCS12]
 -userQueriesPerSecond <arg>       set requests per user per secondlimit [default: <none>]
```

### Start with the embedded HSQLDB server

```sh
java -cp ebms-admin-{{ site.ebms.core.version }}.jar nl.clockwork.ebms.admin.StartEmbedded -hsqldb
```

### Start using a PostgreSQL JDBC driver

```sh
java -cp postgresql-42.2.14.jar:ebms-admin-{{ site.ebms.core.version }}.jar nl.clockwork.ebms.admin.StartEmbedded
```

### Start on port 8000

Start on port 8000 (instead of default port 8080)

Start Web/SOAP interface on port `8000` (instead of `8080`)

```sh
java -cp ebms-admin-{{ site.ebms.core.version }}.jar nl.clockwork.ebms.admin.StartEmbedded -port 8000
```

### Start with SOAP interface

```sh
java -cp ebms-admin-{{ site.ebms.core.version }}.jar nl.clockwork.ebms.admin.StartEmbedded -soap
```

### Start with SOAP interface and without a Web interface

```sh
java -cp ebms-admin-{{ site.ebms.core.version }}.jar nl.clockwork.ebms.admin.StartEmbedded -soap -headless
```

### Start with config directory conf/
{: #configDir}

By default the config directory is the directory from which you start the ebms-admin. You can change the config directory by setting `configDir`

```sh
java -cp ebms-admin-{{ site.ebms.core.version }}.jar nl.clockwork.ebms.admin.StartEmbedded -configDir conf/
```

### Start with a custom log4j2 file log4j2.xml

```sh
java -Dlog4j.configurationFile=log4j2.xml -cp ebms-admin-{{ site.ebms.core.version }}.jar nl.clockwork.ebms.admin.StartEmbedded
```

### Start without using the default Java truststore

```sh
java -Djavax.net.ssl.trustStore= -cp ebms-admin-{{ site.ebms.core.version }}.jar nl.clockwork.ebms.admin.StartEmbedded
```

### Start with HTTPS

Start with HTTPS Web/SOAP interface using keystore `keystore.p12`

```sh
java -Djavax.net.ssl.trustStore= -cp ebms-admin-{{ site.ebms.core.version }}.jar nl.clockwork.ebms.admin.StartEmbedded \
-ssl -keyStoreType PKCS12 -keyStorePath keystore.p12 -keyStorePassword password
```

### Start with HTTPS and client authentication

Start with HTTPS Web/SOAP interface using keystore `keystore.p12`  
and require SSL client authentication using truststore `truststore.p12` (which holds the client's certificate chain)

```sh
java -Djavax.net.ssl.trustStore= -cp ebms-admin-{{ site.ebms.core.version }}.jar nl.clockwork.ebms.admin.StartEmbedded \
-ssl -keyStoreType PKCS12 -keyStorePath keystore.p12 -keyStorePassword password \
-clientAuthentication -trustStoreType PKCS12 -trustStorePath truststore.p12 -trustStorePassword password
```

### Start with HTTPS, client authentication and client certifiate authentication

Start with HTTPS Web/SOAP interface using keystore `keystore.p12`  
and require SSL client authentication using truststore `truststore.p12` (which holds the client's certificate chain)  
and authenticate client SSL certificate using `clientTruststore.p12` (which holds the client's certificate)

```sh
java -Djavax.net.ssl.trustStore= -cp ebms-admin-{{ site.ebms.core.version }}.jar nl.clockwork.ebms.admin.StartEmbedded \
-ssl -keyStoreType PKCS12 -keyStorePath keystore.p12 -keyStorePassword password \
-clientAuthentication -trustStoreType PKCS12 -trustStorePath truststore.p12 -trustStorePassword password \
-authentication -clientTrustStoreType PKCS12 -clientTrustStorePath clientTruststore.p12 -clientTrustStorePassword password
```

### Start in EbMS Server mode

```sh
java -Djavax.net.ssl.trustStore= -cp ebms-admin-{{ site.ebms.core.version }}.jar nl.clockwork.ebms.admin.StartEmbedded -headless -disableEbMSClient
```

### Start in EbMS Client mode

```sh
java -Djavax.net.ssl.trustStore= -cp ebms-admin-{{ site.ebms.core.version }}.jar nl.clockwork.ebms.admin.StartEmbedded -headless -disableEbMSServer
```

### Start in SOAP API mode

```sh
java -Djavax.net.ssl.trustStore= -cp ebms-admin-{{ site.ebms.core.version }}.jar nl.clockwork.ebms.admin.StartEmbedded -soap -headless -disableEbMSServer -disableEbMSClient
```

### Start in Web Interface mode

```sh
java -Djavax.net.ssl.trustStore= -cp ebms-admin-{{ site.ebms.core.version }}.jar nl.clockwork.ebms.admin.StartEmbedded -disableEbMSServer -disableEbMSClient
```

### Start Health service on port 8089

Start Health service on port 8089 (instead of default port 8008)

```sh
java -Djavax.net.ssl.trustStore= -cp ebms-admin-{{ site.ebms.core.version }}.jar nl.clockwork.ebms.admin.StartEmbedded -health -healthPort 8089
```

### Start using basic authentication

Start using basic authentication on Web/SOAP interface

```sh
java -cp ebms-admin-{{ site.ebms.core.version }}.jar nl.clockwork.ebms.admin.StartEmbedded -authentication
```

### Start hsqldb and ebms-admin as 2 separate applications

```sh
java -cp ebms-admin-{{ site.ebms.core.version }}.jar org.hsqldb.server.Server --database.0 file:hsqldb/ebms --dbname.0 ebms -port 9001
java -Djavax.net.ssl.trustStore= -cp ebms-admin-{{ site.ebms.core.version }}.jar nl.clockwork.ebms.admin.StartEmbedded -soap
```
