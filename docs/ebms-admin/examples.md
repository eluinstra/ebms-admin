---
sidebar_position: 6
---

# Examples

[Below](#example-with-2-ebms-adapters) you'll find and simple example of how to configure 2 EbMS Admin Consoles with embedded EbMS adapter to communicate with each other.
You can also find this and other examples configured in [Docker](#docker), [Kubernetes](#kubernetes) and [Ansible](#ansible).

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

In this example you will configure 2 ebms adapters that will communicate with each other. One represents party `Overheid`, the other one represents party `Digipoort`. They will use the `aanleveren` and `afleveren` services as defined in the CPA.

### Prerequisites

- download and install Java 8 (or later)
- download [ebms-admin-@ebms.core.version@.jar](https://github.com/eluinstra/ebms-admin/releases/download/ebms-admin-@ebms.core.version@/ebms-admin-@ebms.core.version@.jar)
- download [cpaStubEBF.rm.https.signed.xml](https://raw.githubusercontent.com/eluinstra/ebms-admin/ebms-admin-2.17.3/resources/CPAs/cpaStubEBF.rm.https.signed.xml)

### Create and configure party Overheid

- create directory `overheid`
- copy ebms-admin-@ebms.core.version@.jar to `overheid`
- start ebms-admin on port `8000` with the SOAP Interface using a HSQLDB server
```
java -cp ebms-admin-@ebms.core.version@.jar nl.clockwork.ebms.admin.StartEmbedded -port 8000 -soap -hsqldb
```
- open web browser at [http://localhost:8000](http://localhost:8000)
- configure properties at the [EbMSAdmin Properties Page](https://localhost:8000/wicket/bookmarkable/nl.clockwork.ebms.admin.web.configuration.EbMSAdminPropertiesPage)
	- set port `8088`
	- set database port `9000`
	- save
	- restart ebms-admin
- upload CPA `cpaStubEBF.rm.https.signed.xml` at the [CPA Upload Page](http://localhost:8000/wicket/bookmarkable/nl.clockwork.ebms.admin.web.service.cpa.CPAUploadPage)

### Create and configure party Digipoort

- create directory `digipoort`
- copy ebms-admin-@ebms.core.version@.jar to `digipoort`
- start ebms-admin on default port `8080` with the SOAP Interface using a HSQLDB server
```
java -cp ebms-admin-@ebms.core.version@.jar nl.clockwork.ebms.admin.StartEmbedded -soap -hsqldb
```
- open web browser at [http://localhost:8080](http://localhost:8080)
- configure properties at the [EbMSAdmin Properties Page](http://localhost:8080/wicket/bookmarkable/nl.clockwork.ebms.admin.web.configuration.EbMSAdminPropertiesPage) [Optional]
	- use default properties, so no changes
	- save
	- restart ebms-admin
- upload CPA cpaStubEBF.rm.https.signed.xml at the [CPA Upload Page](http://localhost:8080/wicket/bookmarkable/nl.clockwork.ebms.admin.web.service.cpa.CPAUploadPage)

### Send messages from party DigiPoort to Overheid

- send a ping to party OVerheid at the [Ping Page](http://localhost:8080/wicket/bookmarkable/nl.clockwork.ebms.admin.web.service.message.PingPage)
	- CPA Id `cpaStubEBF.rm.https.signed`
	- From Party Id `urn:osb:oin:00000000000000000000`
	- To Party Id `urn:osb:oin:000000000000000000001`
- send a message to party OVerheid at the [SendMessage Page](http://localhost:8080/wicket/bookmarkable/nl.clockwork.ebms.admin.web.service.message.SendMessagePageX)
	- CPA Id `cpaStubEBF.rm.https.signed`
	- From Party Id `urn:osb:oin:00000000000000000000`
	- From Role `DIGIPOORT`
	- To Party Id `urn:osb:oin:00000000000000000001`
	- To Role `OVERHEID`
	- Service `urn:osb:services:osb:afleveren:1.1$1.0`
	- Action `afleveren`
	- Add a Data Source [Optional]
- view traffic at the [Traffic Page](http://localhost:8080/wicket/bookmarkable/nl.clockwork.ebms.admin.web.message.TrafficPage)
- find the different endpoints of the [SOAP Interface](soap) [here](http://localhost:8080/service)

### Send messages from party Overheid to Digipoort

- send a ping to party Digipoort at the [Ping Page](http://localhost:8000/wicket/bookmarkable/nl.clockwork.ebms.admin.web.service.message.PingPage)
	- CPA Id `cpaStubEBF.rm.https.signed.xml`
	- From Party Id `urn:osb:oin:00000000000000000001`
	- To Party Id `urn:osb:oin:00000000000000000000`
- send a message to party Digipoort at the [SendMessage Page](http://localhost:8000/wicket/bookmarkable/nl.clockwork.ebms.admin.web.service.message.SendMessagePageX)
	- CPA Id `cpaStubEBF.rm.https.signed.xml`
	- From Party Id `urn:osb:oin:00000000000000000001`
	- From Role `OVERHEID`
	- To Party Id `urn:osb:oin:00000000000000000000`
	- To Role `DIGIPOORT`
	- Service `urn:osb:services:osb:afleveren:1.1$1.0`
	- Action `bevestigAfleveren`
	- Add a Data Source [Optional]
- view traffic at the [Traffic Page](http://localhost:8000/wicket/bookmarkable/nl.clockwork.ebms.admin.web.message.TrafficPage)
- find the different endpoints of the [SOAP Interface](soap) [here](http://localhost:8000/service)
