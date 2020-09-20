---
sort: 6
---

# Examples

Below you'll find and simple example of how to configure 2 EbMS Admin Consoles with embedded EbMS adapter to communicate with each other.
You can also find this and other examples configured in Docker, Kubernetes and Ansible.

## Docker

For Docker examples see
- [https://github.com/eluinstra/ebms-docker](https://github.com/eluinstra/ebms-docker)
- [https://gitlab.com/peterzandbergen/ebms-admin-docker](https://gitlab.com/peterzandbergen/ebms-admin-docker)

## Kubernetes

For Kubernetes examples see
- [https://github.com/eluinstra/ebms-kubernetes](https://github.com/eluinstra/ebms-kubernetes)

## Ansible

For Ansible examples see
- [https://github.com/eluinstra/ebms-ansible](https://github.com/eluinstra/ebms-ansible)

## Example with 2 EbMS adapters

In this example you will configure 2 ebms adapters that will communicate with each other. One represents the party Overheid, the other one represents the party Digipoort. They will use the AanleverService and AfleverService as defined in the CPA.

#### Create and configure party Overheid

- create directory overheid
- copy ebms-admin-{{ site.data.ebms.core.version }}.jar to overheid
- start admin console on port 8000 with a hsqldb server
```java -cp ebms-admin-{{ site.data.ebms.core.version }}.jar nl.clockwork.ebms.admin.StartEmbedded -port 8000 -hsqldb```

- open web browser at https://localhost:8000
- configure properties at [EbMSAdminPropertiesPage](https://localhost:8000/wicket/bookmarkable/nl.clockwork.ebms.admin.web.configuration.EbMSAdminPropertiesPage)
	- set port: 8088
	- set database port: 9000
	- save
	- restart admin console
- upload CPA cpaStubEBF.rm.https.signed.xml at [CPAUploadPage](https://localhost:8000/wicket/bookmarkable/nl.clockwork.ebms.admin.web.service.cpa.CPAUploadPage)

#### Create and configure party Digipoort

- create directory digipoort
- copy ebms-admin-{{ site.data.ebms.core.version }}.jar to digipoort
- start admin console on default port 8080 with a hsqldb server
```java -cp ebms-admin-{{ site.data.ebms.core.version }}.jar nl.clockwork.ebms.admin.StartEmbedded -hsqldb```
- open web browser at [localhost](https://localhost:8080)
- configure properties at [EbMSAdminPropertiesPage](https://localhost:8080/wicket/bookmarkable/nl.clockwork.ebms.admin.web.configuration.EbMSAdminPropertiesPage)
	- use default properties, so no changes
	- save
	- restart admin console
- upload CPA cpaStubEBF.rm.https.signed.xml at [CPAUploadPage](https://localhost:8080/wicket/bookmarkable/nl.clockwork.ebms.admin.web.service.cpa.CPAUploadPage)

#### Send messages from party Overheid to Digipoort

- execute a ping the other adapter at [PingPage](https://localhost:8000/wicket/bookmarkable/nl.clockwork.ebms.admin.web.service.message.PingPage)
	- CPA Id: CPA_EBFStub
	- From Party: Overheid
	- To Party: Logius
- send a message to the other adapter at [SendMessagePage](https://localhost:8000/wicket/bookmarkable/nl.clockwork.ebms.admin.web.service.message.SendMessagePageX)
	- CPA Id: CPA_EBFStub
	- From Role: OVERHEID
	- Service: urn:osb:services:osb:afleveren:1.1$1.0 urn:osb:services:osb:aanleveren:1.1$1.0
	- Action: bevestigAfleveren
	- Add a Data Source [Optional]
- view traffic at [TrafficPage](https://localhost:8000/wicket/bookmarkable/nl.clockwork.ebms.admin.web.message.TrafficPage)
		
#### Send messages from party DigiPoort to Overheid

- execute a ping the other adapter at [PingPage](https://localhost:8080/wicket/bookmarkable/nl.clockwork.ebms.admin.web.service.message.PingPage)
	- CPA Id: CPA_EBFStub
	- From Party: Logius
	- To Party: Overheid
- send a message to the other adapter at [SendMessagePage](https://localhost:8080/wicket/bookmarkable/nl.clockwork.ebms.admin.web.service.message.SendMessagePageX)
	- CPA Id: CPA_EBFStub
	- From Role: LOGIUS
	- Service: urn:osb:services:osb:afleveren:1.1$1.0 urn:osb:services:osb:aanleveren:1.1$1.0
	- Action: afleveren
	- Add a Data Source [Optional]
- view traffic at [TrafficPage](https://localhost:8080/wicket/bookmarkable/nl.clockwork.ebms.admin.web.message.TrafficPage)
