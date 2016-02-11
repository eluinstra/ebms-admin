=====================================
= Start EbMS Admin Console standalone
=====================================
show help:
> java -cp ebms-admin-2.x.x.jar nl.clockwork.ebms.admin.Start -h

start:
> java -cp ebms-admin-2.x.x.jar nl.clockwork.ebms.admin.Start

=====================================================
= Start EbMS Admin Console with embedded EbMS adapter
=====================================================
show help:
> java -cp ebms-admin-2.x.x.jar nl.clockwork.ebms.admin.StartEmbedded -h

start with hsqldb server:
> java -cp ebms-admin-2.x.x.jar nl.clockwork.ebms.admin.StartEmbedded -hsqldb

========================================================
= Start 2 EbMS Admin Consoles with embedded EbMS adapter
========================================================
Example using 2 ebms adapters:

- create directory overheid
- copy ebms-admin-2.x.x.jar to overheid
- start admin console on port 8000 with a hsqldb server:
> java -cp ebms-admin-2.x.x.jar nl.clockwork.ebms.admin.StartEmbedded -port 8000 -hsqldb

- open web browser at http://localhost:8000
- configure properties at http://localhost:8000/wicket/bookmarkable/nl.clockwork.ebms.admin.web.configuration.EbMSAdminPropertiesPage
	- set port: 8088
	- set path: overheidStub
	- set database port: 9000
	- save
	- restart admin console
- upload CPA cpaStubEBF.rm.https.signed.xml at http://localhost:8000/wicket/bookmarkable/nl.clockwork.ebms.admin.web.service.cpa.CPAUploadPage

- create directory digipoort
- copy ebms-admin-2.x.x.jar to digipoort
- start admin console on default port 8080 with a hsqldb server:
> java -cp ebms-admin-2.x.x.jar nl.clockwork.ebms.admin.StartEmbedded -hsqldb
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

================
= Build project
================
mvn package

==========
= Eclipse
==========
Import -> Existing Maven Projects

resolve js validation errors:
- Properties -> JavaScript -> Include Path -> Source
- Edit ebms-admin/src/main/webapp -> Excluded
- Add Exclusion Pattern: js/jquery-min.js
- Remove file js/jquery-min.js from project and add it again
OR
- Properties -> Builders
- Uncheck JavaScript Validator
- Remove file js/jquery-min.js from project and add it again
