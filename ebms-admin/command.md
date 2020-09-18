---
sort: 4
---

# Command Line Options

#### show help
```
java -cp ebms-admin-2.17.3.jar nl.clockwork.ebms.admin.StartEmbedded -h  
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

#### start with the embedded hsqldb server
```
java -cp ebms-admin-2.17.3.jar nl.clockwork.ebms.admin.StartEmbedded -hsqldb
```
#### start using a postgresql jdbc driver
```
java -cp postgresql-42.2.14.jar:ebms-admin-2.17.3.jar nl.clockwork.ebms.admin.StartEmbedded
```
#### start on port 8000 (instead of 8080)
```
java -cp ebms-admin-2.17.3.jar nl.clockwork.ebms.admin.StartEmbedded -port 8000
```
#### start with soap interface
```
java -cp ebms-admin-2.17.3.jar nl.clockwork.ebms.admin.StartEmbedded -soap
```
#### start with soap interface and without a web interface
```
java -cp ebms-admin-2.17.3.jar nl.clockwork.ebms.admin.StartEmbedded -soap -headless
```
#### start with config directory conf/
```
java -cp ebms-admin-2.17.3.jar nl.clockwork.ebms.admin.StartEmbedded -configDir conf/
```
#### start with a custom log4j2 file conf/log4j2.xml
```
java -Dlog4j.configurationFile=conf/log4j2.xml -cp ebms-admin-2.17.3.jar nl.clockwork.ebms.admin.StartEmbedded
```
#### start without using the default java truststore
```
java -Djavax.net.ssl.trustStore= -cp ebms-admin-2.17.3.jar nl.clockwork.ebms.admin.StartEmbedded
```
#### start with https
start with https using keystore keystore.p12  
```
java -Djavax.net.ssl.trustStore= -cp ebms-admin-2.17.3.jar nl.clockwork.ebms.admin.StartEmbedded \
-ssl -keyStoreType PKCS12 -keyStorePath keystore.p12 -keyStorePassword password
```
#### start with https and client authentication
start with https using keystore keystore.p12  
and require client authentication using truststore truststore.p12 (which holds the client's certificate chain)  
```
java -Djavax.net.ssl.trustStore= -cp ebms-admin-2.17.3.jar nl.clockwork.ebms.admin.StartEmbedded \
-ssl -keyStoreType PKCS12 -keyStorePath keystore.p12 -keyStorePassword password \
-clientAuthentication -trustStoreType PKCS12 -trustStorePath truststore.p12 -trustStorePassword password
```
#### start with https, client authentication and client certifiate authentication
start with https using keystore keystore.p12  
and require ssl client authentication using truststore truststore.p12 (which holds the client's certificate chain)  
and authenticate client ssl certificate using clientTruststore.p12 (which holds the client's certificate)  

```
java -Djavax.net.ssl.trustStore= -cp ebms-admin-2.17.3.jar nl.clockwork.ebms.admin.StartEmbedded \
-ssl -keyStoreType PKCS12 -keyStorePath keystore.p12 -keyStorePassword password \
-clientAuthentication -trustStoreType PKCS12 -trustStorePath truststore.p12 -trustStorePassword password \
-authentication -clientTrustStoreType PKCS12 -clientTrustStorePath clientTruststore.p12 -clientTrustStorePassword password
```
#### start using basic authentication
```
java -cp ebms-admin-2.17.3.jar nl.clockwork.ebms.admin.StartEmbedded -authentication
```
#### start hsqldb and ebms-admin as 2 separate applications:
```
java -cp ebms-admin-2.17.0-SNAPSHOT.jar org.hsqldb.server.Server --database.0 file:hsqldb/ebms --dbname.0 ebms -port 9001
java -Djavax.net.ssl.trustStore= -cp ebms-admin-2.17.3.jar nl.clockwork.ebms.admin.StartEmbedded -soap
```
When you start you can see the following information in the console
```
Using config directory: 
[Server@f0da945]: Database [index=0, id=0, db=file:hsqldb/ebms, alias=ebms] opened successfully in 420 ms.
[Server@f0da945]: Startup sequence completed in 424 ms.
[Server@f0da945]: 2019-12-27 15:07:57.319 HSQLDB server 2.5.0 is online on port 9001
[Server@f0da945]: To close normally, connect and execute SHUTDOWN SQL
[Server@f0da945]: From command line, use [Ctrl]+[C] to abort abruptly
EbMS tables already exist
Using keyStore jar:file:/home/digipoort/ebms-admin-2.17.3.jar!/keystore.p12
Using trustStore jar:file:/home/digipoort/ebms-admin-2.17.3.jar!/truststore.p12
Web server configured on https://localhost:8443/
SOAP service configured on https://localhost:8443/service
EbMS service configured on https://0.0.0.0:8888/digipoortStub
Configuring web server client certificate authentication:
Using clientTrustStore jar:file:/home/digipoort/ebms-admin-2.17.3.jar!/clientTruststore.p12
Starting web server...
```
Next configure the remote EbMS service in [EbMSAdminPropertiesPage](https://localhost:8080/wicket/bookmarkable/nl.clockwork.ebms.admin.web.configuration.EbMSAdminPropertiesPage)

If you want to override 'advanced' properties from the default.properties file that are not included in the ebms-admin.embedded.properties file, then create the file ebms-admin.embedded.advanced.properties in the configDir and add the properties to that file.