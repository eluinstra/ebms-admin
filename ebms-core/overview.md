---
parent: EbMS Core
nav_order: 2
---

# Overview
{: .no_toc }

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}
---

You can use ebms-core by [integrating]({{ site.baseurl }}{% link ebms-core/development.md %}) it into your own Java application, or you can use it as a standalone SOAP Service through [ebms-admin]({{ site.baseurl }}{% link ebms-admin/introduction.md %}).

![EbMS Overview]({{ site.baseurl }}/assets/images/ebms-overview.svg)

## Basic Functionality

ebms-core offers the following interfaces

- EbMS Interface to connect to other EbMS adapters
- [EbMS/SOAP API]({{ site.baseurl }}{% link ebms-core/api.md %}) to connect to applications to manage the EbMS adapter  

 ebms-core is configured through [properties]({{ site.baseurl }}{% link ebms-core/properties.md %}). Data is stored in the [database]({{ site.baseurl }}{% link ebms-core/database.md %}).

You can configure [EbMS Message Storage]({{ site.baseurl }}/ebms-core/properties.html#ebms-message-storage) and [EbMS Attachments overflow to disk]({{ site.baseurl }}/ebms-core/properties.html#overflow-attachments-to-disk).

## Advanced

### EventListener

You can track sent and received messages if you configure the [EventListener]({{ site.baseurl }}/ebms-core/properties.html#eventlistener).

### Scaling

To support [scaling with serverId]({{ site.baseurl }}/ebms-admin/deployment.html#scaling-serverid) and [advanced scaling]({{ site.baseurl }}/ebms-admin/deployment.html#scaling) the different components have to use the same [database]({{ site.baseurl }}{% link ebms-core/database.md %}). Also the internal [cache]({{ site.baseurl }}/ebms-core/properties.html#cache) has to be synchronized between the components. Furthermore you have to configure the [DeliveryManager]({{ site.baseurl }}/ebms-core/properties.html#deliverymanager). When using [advanced scaling]({{ site.baseurl }}/ebms-admin/deployment.html#scaling) you also have to configure the [DeliveryTaskHandler]({{ site.baseurl }}/ebms-core/properties.html#deliverytaskhandler).


## Data Model

![EbMS Data Model]({{ site.baseurl }}/assets/images/ebms-data-model.svg)