ebms-admin-2.16.0.jar is tested with openjdk 8
It now supports https for the web and soap interfaces as well basic or client certificate authentication
If you want to use a database other then hsqldb, you have to provide the jdbc driver yourself

=====================================
= Start EbMS Admin Console standalone
=====================================
show help:
> java -cp ebms-admin-2.16.0.jar nl.clockwork.ebms.admin.Start -h

start:
> java -cp ebms-admin-2.16.0.jar nl.clockwork.ebms.admin.Start

start on port 8000 (instead of 8080):
> java -cp ebms-admin-2.16.0.jar nl.clockwork.ebms.admin.Start -port 8000

When you start you can see the following information in the console:
Using keyStore jar:file:/home/digipoort/ebms-admin-2.16.0.jar!/keystore.p12
Using trustStore jar:file:/home/digipoort/ebms-admin-2.16.0.jar!/truststore.p12
Web server configured on https://localhost:8443/
Configuring web server client certificate authentication:
Using clientTrustStore jar:file:/home/digipoort/ebms-admin-2.16.0.jar!/clientTruststore.p12
Starting web server...

Next configure the remote EbMS service in http://localhost:8080/wicket/bookmarkable/nl.clockwork.ebms.admin.web.configuration.EbMSAdminPropertiesPage

=====================================================
= Start EbMS Admin Console with embedded EbMS adapter
=====================================================
show help:
> java -cp ebms-admin-2.16.0.jar nl.clockwork.ebms.admin.StartEmbedded -h

start with the embedded hsqldb server:
> java -cp ebms-admin-2.16.0.jar nl.clockwork.ebms.admin.StartEmbedded -hsqldb

start using a postgresql jdbc driver:
> java -cp postgresql-9.3-1102-jdbc41.jar:ebms-admin-2.16.0.jar nl.clockwork.ebms.admin.StartEmbedded

start on port 8000 (instead of 8080):
> java -cp ebms-admin-2.16.0.jar nl.clockwork.ebms.admin.StartEmbedded -port 8000

start with soap interface:
> java -cp ebms-admin-2.16.0.jar nl.clockwork.ebms.admin.StartEmbedded -soap

start with soap interface and without a web interface:
> java -cp ebms-admin-2.16.0.jar nl.clockwork.ebms.admin.StartEmbedded -soap -headless

start with a different log4j2 file:
> java -Dlog4j.configurationFile=path/to/log4j2.xml -cp ebms-admin-2.16.0.jar nl.clockwork.ebms.admin.StartEmbedded

start without using the default java truststore:
> java -Djavax.net.ssl.trustStore= -cp ebms-admin-2.16.0.jar nl.clockwork.ebms.admin.StartEmbedded

start with https using keystore keystore.p12
> java -Djavax.net.ssl.trustStore= -cp ebms-admin-2.16.0.jar nl.clockwork.ebms.admin.StartEmbedded \
	-ssl -keyStoreType PKCS12 -keyStorePath keystore.p12 -keyStorePassword password

start with https using keystore keystore.p12
	and require client authentication using truststore truststore.p12 (which holds the client's certificate chain)
> java -Djavax.net.ssl.trustStore= -cp ebms-admin-2.16.0.jar nl.clockwork.ebms.admin.StartEmbedded \
	-ssl -keyStoreType PKCS12 -keyStorePath keystore.p12 -keyStorePassword password \
	-clientAuthentication -trustStoreType PKCS12 -trustStorePath truststore.p12 -trustStorePassword password

start with https using keystore keystore.p12
	and require ssl client authentication using truststore truststore.p12 (which holds the client's certificate chain)
	and authenticate client ssl certificate using clientTruststore.p12 (which holds the client's certificate)
> java -Djavax.net.ssl.trustStore= -cp ebms-admin-2.16.0.jar nl.clockwork.ebms.admin.StartEmbedded \
	-ssl -keyStoreType PKCS12 -keyStorePath keystore.p12 -keyStorePassword password \
	-clientAuthentication -trustStoreType PKCS12 -trustStorePath truststore.p12 -trustStorePassword password \
	-authentication -clientTrustStoreType PKCS12 -clientTrustStorePath clientTruststore.p12 -clientTrustStorePassword password

start using basic authentication
> java -cp ebms-admin-2.16.0.jar nl.clockwork.ebms.admin.StartEmbedded -authentication

When you start you can see the following information in the console:
[Server@f0da945]: Database [index=0, id=0, db=file:hsqldb/ebms, alias=ebms] opened successfully in 420 ms.
[Server@f0da945]: Startup sequence completed in 424 ms.
[Server@f0da945]: 2019-12-27 15:07:57.319 HSQLDB server 2.5.0 is online on port 9001
[Server@f0da945]: To close normally, connect and execute SHUTDOWN SQL
[Server@f0da945]: From command line, use [Ctrl]+[C] to abort abruptly
EbMS tables already exist
Using keyStore jar:file:/home/digipoort/ebms-admin-2.16.0.jar!/keystore.p12
Using trustStore jar:file:/home/digipoort/ebms-admin-2.16.0.jar!/truststore.p12
Web server configured on https://localhost:8443/
SOAP service configured on https://localhost:8443/service
EbMS service configured on https://0.0.0.0:8888/digipoortStub
Configuring web server client certificate authentication:
Using clientTrustStore jar:file:/home/digipoort/ebms-admin-2.16.0.jar!/clientTruststore.p12
Starting web server...

Next configure the remote EbMS service in http://localhost:8080/wicket/bookmarkable/nl.clockwork.ebms.admin.web.configuration.EbMSAdminPropertiesPage

========================================================
= Start 2 EbMS Admin Consoles with embedded EbMS adapter
========================================================
Example using 2 ebms adapters:

- create directory overheid
- copy ebms-admin-2.16.0.jar to overheid
- start admin console on port 8000 with a hsqldb server:
> java -cp ebms-admin-2.16.0.jar nl.clockwork.ebms.admin.StartEmbedded -port 8000 -hsqldb

- open web browser at http://localhost:8000
- configure properties at http://localhost:8000/wicket/bookmarkable/nl.clockwork.ebms.admin.web.configuration.EbMSAdminPropertiesPage
	- set port: 8088
	- set path: overheidStub
	- set database port: 9000
	- save
	- restart admin console
- upload CPA cpaStubEBF.rm.https.signed.xml at http://localhost:8000/wicket/bookmarkable/nl.clockwork.ebms.admin.web.service.cpa.CPAUploadPage

- create directory digipoort
- copy ebms-admin-2.16.0.jar to digipoort
- start admin console on default port 8080 with a hsqldb server:
> java -cp ebms-admin-2.16.0.jar nl.clockwork.ebms.admin.StartEmbedded -hsqldb
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
