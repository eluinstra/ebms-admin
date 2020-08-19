ebms-admin-2.16.x.jar and up needs jdk 8 and is compiled and tested with openjdk 8
It now supports https for the web and soap interfaces as well as basic and client certificate authentication
It supports now also (and defaults to) PKCS12 keystores.
If you want to use a database other then hsqldb, you have to provide the jdbc driver yourself

The ebms-admin app is a fully functional EbMS adapter that uses the ebms-core jar for this.
The ebms-admin app consists of an ebms-adapter and a web and a soap interface to manage the ebms-adapter.
The ebms-adapter is configured through the ebms-admin properties file that can be generated in the EbMSAdminPropertiesPage.
The web and soap interfaces are configured through the application's command line properties (see below for examples)
The database master scripts can be found in https://repo.maven.apache.org/maven2/nl/clockwork/ebms/ebms-core/2.17.0/ebms-core-2.17.0-sources.jar/resources/scripts/database/
The database update scripts can be found in https://repo.maven.apache.org/maven2/nl/clockwork/ebms/ebms-core/2.17.0/ebms-core-2.17.0-sources.jar/src/main/resources/nl/clockwork/ebms/db/migration/

See 'Start EbMS Admin Console with embedded EbMS adapter' below for usage
See 'Configure 2 EbMS Admin Consoles with embedded EbMS adapter to communicate with each other' below for a test scenario with 2 EbMS Admin Consoles

IMPORTANT:
set property https.clientCertificateAuthentication to false, unless you know what you are doing!!!

Version 2.16.x supports SSL clientCertificate validation. This means that the SSL clientCertificate of the sending party will be validated against the matching CPA. This option works fine as long as the sending party uses the clientCertificate defined in the matching CPA when sending messages and the clientCertificate is trusted. This option can be enabled by setting property https.clientCertificateAuthentication to true.
If the admin console does not handle incoming SSL itself, then the clientCertificate can be forwarded as a Base64 DER-encoded HTTP header to the admin console. The header name can be set from version 2.17.x in https.clientCertificateHeader.
Version 2.17.x will also will also have the option to use SSL clientCertificate from the matching CPA when sending a message. This option works fine as long as the receiving party will trust the clientCertificate. This option can be enabled by setting property https.useClientCertificate to true.
These properties van be edited in ebms-admin.embedded.properties if available and otherwise added to ebms-admin.embedded.advanced.properties.

===============
= Release Notes
===============
ebms-admin-2.17.3.jar:
- upgrade to ebms-core-2.17.3.jar:
	- fixed URL Mapping
	- renamed status SENDING to CREATED
	- updated libraries

ebms-admin-2.17.2.jar:
- improved properties configuration
- moved  flyway db migration to ebms-core
- changed commandline arguments:
	- removed db migration arguments updateDb and updateStrict
	- renamed rate limiter argument requestsPerSecond to queriesPerSecond
	- added user rate limiter argument userQueriesPerSecond
- upgrade to ebms-core-2.17.2.jar:
	- added UserRateLimiterFilter
	- added EventListenerFilter
		- added default property eventListener.filter=
	- added h2 database support
	- added flyway db migration
		- added default properties
			- ebms.jdbc.update=false
			- ebms.jdbc.strict=false

ebms-admin-2.17.1.1.jar:
- fixed updateDb option
- changed commandline arguments:

ebms-admin-2.17.1.jar:
- improved logging
- removed Server response header
- added flyway migration
- added rate limiter
- changed commandline arguments:
	- added db migration arguments updateDb and updateStrict
	- added rate limiter argument requestsPerSecond
- upgrade to ebms-core-2.17.1.jar:
	- improved logging
	- improved error responses
	- fixed auto retry responses only if best effort
	- fixed configuration issue, that causes send events to be rejected
	- fixed server, client and api mode
	- added rate limiter
		- added default property http.requestsPerSecond=

ebms-admin-2.17.0.1.jar:
- changed default settings
- changed configuration of the test scenario
- updated CPA endpoints

ebms-admin-2.17.0.jar:
- added options to enable high availability and horizontal scaling (and throttling)
- changed command line arguments:
	- renamed propertiesFilesDir to configDir
	- removed property log4j.file (use -Dlog4j.configurationFile=log4j2.xml instead)
	- added ssl arguments protocols and cipherSuites
	- added jmx arguments
	- added arguments disableEbMSClient and disableEbMSServer
- split up CPAService into CPAService and URLMapper
- added new SOAP service CertificateMapper
- updated EbMS Admin Properties Page
- removed EbMS Core Properties Page
- added database java command line tools:
	- DBMigrate (java -cp ebms-admin-2.17.0.jar nl.clockwork.ebms.admin.DBMigrate -h)
	- DBClean (java -cp ebms-admin-2.17.0.jar nl.clockwork.ebms.admin.DBClean -h)
- upgrade to ebms-core-2.17.0.jar:
	- added options to enable availability and horizontal scaling (and throttling)
	- added option to use SSL clientCerttificate defined in the CPA to send messages (https.useClientCertificate)
		- added CertificateMapper SOAP service to override defined SSL clientCertificate
	- cleaned up and split up SOAP interfaces
	- changed SOAP Services:
		- renamed operations from EbMSMessageService:
			- GetMessageIds to GetUnprocessedMessageIds
			- GetMessageEvents to GetUnprocessedMessageEvents
		- removed operations from EbMSMessageService:
			- SendMessageWithAttachments (use SendMessage from ebmsMTOM instead)
			- GetMessageStatus is replaced by GetMessageStatusByMessageId, old GetMessageStatus is removed
			- ProcessMessages (use ProcessMessage instead)
			- ProcessMessageEvents (use ProcessMessageEvent instead)
		- split up CPAService into CPAService and URLMapper
	- changed default properties
		- removed properties:
			- ebms.allowMultipleServers (leave property ebms.serverId empty to set allowMultipleServers to false)
			- patch.digipoort.enable (not necessary anymore)
			- patch.oracle.enable (not necessary anymore)
			- patch.cleo.enable (not necessary anymore)
			- cache.disabled (use cache.type instead)
			- eventProcessor.enabled=true (use eventProcessor.type=NONE instead)
		- changed default value of property
			- http.base64Writer to false (writer is disabled anyway because of an issue)
			- https.clientCertificateAuthentication to false
		- added properties:
			- https.useClientCertificate=false
			- client.keystore.keyPassword=${client.keystore.password}
			- client.keystore.defaultAlias=
			- signature.keystore.keyPassword=${signature.keystore.password}
			- encryption.keystore.keyPassword=${encryption.keystore.password}
			- cache.type=DEFAULT (allowed values: DEFAULT(=SPRING) | EHCACHE | IGNITE)
			- eventProcessor.type=DEFAULT (allowed values: NONE | DEFAULT(=DAO) | JMS)
			- deliveryManager.type=DEFAULT (allowed types: DEFAULT(=DAO) | JMS)
			- eventListener.type=DEFAULT (allowed values: DEFAULT(=LOGGING) | DAO | SIMPLE_JMS | JMS | JMS_TEXT)
			- transactionManager.type=DEFAULT (allowed values: DEFAULT | ATOMIKOS)
		* see src/main/resources/nl/clockwork/ebms/default.properties for all available properties
	- implemented JMS components (for scaling)
	- added Atomikos transaction manager (for JMS)
	- added Apache Ignite cache manager (for scaling)
	- added Flyway to install and upgrade database 
	- code improvements
		- added lombok and vavr
		- made objects immutable where possible
		- moved spring bean configuration from xml to code
		- restructured classes and packages
		- reconfigured caching and transactions
		- split up DAO
		- replaced jdbcTemplate by querydsl
		- replace commons-logging by slf4j
		- lots of other improvements
	- updated libraries
	- database updates and improved indices
- updated libraries
- updated keystores and CPAs

ebms-admin-2.16.7.jar:
- upgrade to ebms-core-2.16.7.jar:
	- disabled base64Writer because of a bug when sending base64 encoded content
	- fixed using header defined in property x509CertificateHeader

ebms-admin-2.16.6.jar:
- fixed CXF logging
- upgrade to ebms-core-2.16.6.jar:
  - fixed bug: the references in a signed acknowledgment is not validated correctly, which will not set the status of the message to DELIVERED but eventually to EXPIRED instead
  - fixed issue using asynchronous messaging and no receive deliveryChannel can be found. The message will be stored and returned synchronously as an error now
  - fixed deliveryChannel validation not handled correctly, causing a SOAP fault being returned instead of a EbMS MessageError in case of an error

ebms-admin-2.16.5.jar:
- upgrade to ebms-core-2.16.5.jar:
	- optimized memory usage by using CachedOutputStream for attachments that overflows to disk:
		- added property ebmsMessage.attachment.memoryTreshold - default: 128KB 
		- added property ebmsMessage.attachment.outputDirectory - default: <tempDir>
		- added property ebmsMessage.attachment.cipherTransformation - default: none

ebms-admin-2.16.4.jar
- added new MTOM EbMS soap service endpoint on /service/ebmsMTOM
- upgrade to ebms-core-2.16.4.jar:
	- fixed EbMSEventProcessor: the processor sometimes stops processing after an error occurs, so the ebms adapter stops sending messages
	- fixed query in deleteEbMSAttachmentsOnMessageProcessed
	- fixed messageId: the hostname is not prepended anymore when the messageId is given
	- added new MTOM EbMS soap service

ebms-admin-2.16.3.jar
- minor configuration improvements
- upgrade to ebms-core-2.16.3.jar:
	- fixed bug: messages are sometimes sent more than once at (almost) the same time
	- improved EbMSEventProcessor
	- renamed property jobScheduler.enabled to eventProcessor.enabled
	- renamed property jobScheduler.delay to eventProcessor.delay
	- renamed property jobScheduler.period to eventProcessor.period
	- renamed property job.maxTreads to eventProcessor.maxTreads
	- renamed property job.processorsScaleFactor to eventProcessor.processorsScaleFactor
	- renamed property job.queueScaleFactor to eventProcessor.queueScaleFactor
	- improved EbMSResponseHandler
	- renamed property http.errors.server.irrecoverable to http.errors.server.unrecoverable

ebms-admin-2.16.2a.jar
- fixed reading the encryption keystore property which caused an error opening the Configuration Page after the encryption keystore was set
- minor layout improvements

ebms-admin-2.16.2.jar
- fixed default value of log4j.file property which caused a startup error when ebms-admin-2.16.1.jar was fresh installed 
- upgrade to ebms-core-2.16.2.jar:
	- removed MIME-Version header

ebms-admin-2.16.1.jar
- upgraded to java 8
- https support for web and soap interfaces (including client certificate authentication)
- upgraded a lot of libraries (included wicked-charts-wicket8)
- added the option to use PKCS12 keystores and truststores
- removed all previously included jdbc drivers from the release
- minor improvements
- upgrade to ebms-core-2.16.1.jar:
	- upgraded to java 8
	- minor improvements

=====================================================
= Start EbMS Admin Console with embedded EbMS adapter
=====================================================
show help:
> java -cp ebms-admin-2.17.3.jar nl.clockwork.ebms.admin.StartEmbedded -h
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

start with the embedded hsqldb server:
> java -cp ebms-admin-2.17.3.jar nl.clockwork.ebms.admin.StartEmbedded -hsqldb

start using a postgresql jdbc driver:
> java -cp postgresql-42.2.14.jar:ebms-admin-2.17.3.jar nl.clockwork.ebms.admin.StartEmbedded

start on port 8000 (instead of 8080):
> java -cp ebms-admin-2.17.3.jar nl.clockwork.ebms.admin.StartEmbedded -port 8000

start with soap interface:
> java -cp ebms-admin-2.17.3.jar nl.clockwork.ebms.admin.StartEmbedded -soap

start with soap interface and without a web interface:
> java -cp ebms-admin-2.17.3.jar nl.clockwork.ebms.admin.StartEmbedded -soap -headless

start with config directory conf/
> java -cp ebms-admin-2.17.3.jar nl.clockwork.ebms.admin.StartEmbedded -configDir conf/

start with a log4j2 file conf/log4j2.xml:
> java -Dlog4j.configurationFile=conf/log4j2.xml -cp ebms-admin-2.17.3.jar nl.clockwork.ebms.admin.StartEmbedded

start without using the default java truststore:
> java -Djavax.net.ssl.trustStore= -cp ebms-admin-2.17.3.jar nl.clockwork.ebms.admin.StartEmbedded

start with https using keystore keystore.p12
> java -Djavax.net.ssl.trustStore= -cp ebms-admin-2.17.3.jar nl.clockwork.ebms.admin.StartEmbedded \
	-ssl -keyStoreType PKCS12 -keyStorePath keystore.p12 -keyStorePassword password

start with https using keystore keystore.p12
	and require client authentication using truststore truststore.p12 (which holds the client's certificate chain)
> java -Djavax.net.ssl.trustStore= -cp ebms-admin-2.17.3.jar nl.clockwork.ebms.admin.StartEmbedded \
	-ssl -keyStoreType PKCS12 -keyStorePath keystore.p12 -keyStorePassword password \
	-clientAuthentication -trustStoreType PKCS12 -trustStorePath truststore.p12 -trustStorePassword password

start with https using keystore keystore.p12
	and require ssl client authentication using truststore truststore.p12 (which holds the client's certificate chain)
	and authenticate client ssl certificate using clientTruststore.p12 (which holds the client's certificate)
> java -Djavax.net.ssl.trustStore= -cp ebms-admin-2.17.3.jar nl.clockwork.ebms.admin.StartEmbedded \
	-ssl -keyStoreType PKCS12 -keyStorePath keystore.p12 -keyStorePassword password \
	-clientAuthentication -trustStoreType PKCS12 -trustStorePath truststore.p12 -trustStorePassword password \
	-authentication -clientTrustStoreType PKCS12 -clientTrustStorePath clientTruststore.p12 -clientTrustStorePassword password

start using basic authentication
> java -cp ebms-admin-2.17.3.jar nl.clockwork.ebms.admin.StartEmbedded -authentication

start hsqldb and ebms-admin as 2 separate applications:
> java -cp ebms-admin-2.17.0-SNAPSHOT.jar org.hsqldb.server.Server --database.0 file:hsqldb/ebms --dbname.0 ebms -port 9001
> java -Djavax.net.ssl.trustStore= -cp ebms-admin-2.17.3.jar nl.clockwork.ebms.admin.StartEmbedded -soap

When you start you can see the following information in the console:
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

Next configure the remote EbMS service in http://localhost:8080/wicket/bookmarkable/nl.clockwork.ebms.admin.web.configuration.EbMSAdminPropertiesPage

If you want to override 'advanced' properties from the default.properties file that are not included in the ebms-admin.embedded.properties file, then create the file ebms-admin.embedded.advanced.properties in the configDir and add the properties to that file.

=====================================
= Start EbMS Admin Console standalone
=====================================
show help:
> java -cp ebms-admin-2.17.3.jar nl.clockwork.ebms.admin.Start -h
usage: Start [-authentication] [-cipherSuites <arg>]
       [-clientAuthentication] [-clientCertificateHeader <arg>]
       [-clientTrustStorePassword <arg>] [-clientTrustStorePath <arg>]
       [-clientTrustStoreType <arg>] [-configDir <arg>] [-connectionLimit
       <arg>] [-h] [-host <arg>] [-jmx] [-jmxAccessFile <arg>]
       [-jmxPasswordFile <arg>] [-jmxPort <arg>] [-keyStorePassword <arg>]
       [-keyStorePath <arg>] [-keyStoreType <arg>] [-path <arg>] [-port
       <arg>] [-protocols <arg>] [-queriesPerSecond <arg>] [-ssl]
       [-trustStorePassword <arg>] [-trustStorePath <arg>]
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
 -h                                print this message
 -host <arg>                       set host
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
 -ssl                              use ssl
 -trustStorePassword <arg>         set truststore password
 -trustStorePath <arg>             set truststore path
 -trustStoreType <arg>             set truststore type (deault=PKCS12)
 -userQueriesPerSecond <arg>       set requests per user per secondlimit (default: none)

start:
> java -cp ebms-admin-2.17.3.jar nl.clockwork.ebms.admin.Start

===========================================================================================
= Configure 2 EbMS Admin Consoles with embedded EbMS adapter to communicate with each other
===========================================================================================
Example using 2 ebms adapters (or see https://github.com/eluinstra/ebms-docker for a docker example):

- create directory overheid
- copy ebms-admin-2.17.3.jar to overheid
- start admin console on port 8000 with a hsqldb server:
> java -cp ebms-admin-2.17.3.jar nl.clockwork.ebms.admin.StartEmbedded -port 8000 -hsqldb

- open web browser at http://localhost:8000
- configure properties at http://localhost:8000/wicket/bookmarkable/nl.clockwork.ebms.admin.web.configuration.EbMSAdminPropertiesPage
	- set port: 8088
	- set database port: 9000
	- save
	- restart admin console
- upload CPA cpaStubEBF.rm.https.signed.xml at http://localhost:8000/wicket/bookmarkable/nl.clockwork.ebms.admin.web.service.cpa.CPAUploadPage

- create directory digipoort
- copy ebms-admin-2.17.3.jar to digipoort
- start admin console on default port 8080 with a hsqldb server:
> java -cp ebms-admin-2.17.3.jar nl.clockwork.ebms.admin.StartEmbedded -hsqldb
- open web browser at http://localhost:8080
- configure properties at http://localhost:8080/wicket/bookmarkable/nl.clockwork.ebms.admin.web.configuration.EbMSAdminPropertiesPage
	- use default properties, so no changes
	- save
	- restart admin console
- upload CPA cpaStubEBF.rm.https.signed.xml at http://localhost:8080/wicket/bookmarkable/nl.clockwork.ebms.admin.web.service.cpa.CPAUploadPage

- next from the overheid console you can:
	- execute a ping the other adapter at http://localhost:8000/wicket/bookmarkable/nl.clockwork.ebms.admin.web.service.message.PingPage
		- CPA Id: CPA_EBFStub
		- From Party: Overheid
		- To Party: Logius
	- send a message to the other adapter at http://localhost:8000/wicket/bookmarkable/nl.clockwork.ebms.admin.web.service.message.SendMessagePageX
		- CPA Id: CPA_EBFStub
		- From Role: OVERHEID
		- Service: urn:osb:services:osb:afleveren:1.1$1.0 urn:osb:services:osb:aanleveren:1.1$1.0
		- Action: bevestigAfleveren
		- Add a Data Source
	- view traffic at http://localhost:8000/wicket/bookmarkable/nl.clockwork.ebms.admin.web.message.TrafficPage
		
- next from the digipoort console you can:
	- execute a ping the other adapter at http://localhost:8080/wicket/bookmarkable/nl.clockwork.ebms.admin.web.service.message.PingPage
		- CPA Id: CPA_EBFStub
		- From Party: Logius
		- To Party: Overheid
	- send a message to the other adapter at http://localhost:8080/wicket/bookmarkable/nl.clockwork.ebms.admin.web.service.message.SendMessagePageX
		- CPA Id: CPA_EBFStub
		- From Role: LOGIUS
		- Service: urn:osb:services:osb:afleveren:1.1$1.0 urn:osb:services:osb:aanleveren:1.1$1.0
		- Action: afleveren
		- Add a Data Source
	- view traffic at http://localhost:8080/wicket/bookmarkable/nl.clockwork.ebms.admin.web.message.TrafficPage
