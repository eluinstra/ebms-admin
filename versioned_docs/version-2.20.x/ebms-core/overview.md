---
sidebar_position: 2
---

# Overview

You can use ebms-core by [integrating](development) it into your own Java application, or you can use it as a standalone SOAP Service through [ebms-admin](/ebms-admin/introduction.md).

![EbMS Overview](/assets/images/ebms-overview.svg)

## Basic Functionality

ebms-core offers the following interfaces

- EbMS Interface to connect to other EbMS adapters
- [EbMS/SOAP API](api) to connect to applications to manage the EbMS adapter  

 ebms-core is configured through [properties](properties). Data is stored in the [database](database).

You can configure [EbMS Message Storage](properties#ebms-message-storage) and [EbMS Attachments overflow to disk](properties#overflow-attachments-to-disk).

## Advanced

### EventListener

You can track sent and received messages if you configure the [EventListener](properties#eventlistener).

### Scaling

To support [scaling with serverId](/ebms-admin/deployment.md#scaling-with-serverid) and [advanced scaling](/ebms-admin/deployment.md#scaling) the different components have to use the same [database](database). Also the internal [cache](properties#cache) has to be synchronized between the components. Furthermore you have to configure the [DeliveryManager](properties#deliverymanager). When using [advanced scaling](/ebms-admin/deployment.md#scaling) you also have to configure the [DeliveryTaskHandler](properties#deliverytaskhandler).


## Data Model

![EbMS Data Model](/assets/images/ebms-data-model.svg)