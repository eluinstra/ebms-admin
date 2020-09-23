---
sort: 6
---

# EbMS API

ebms-core v{{ site.data.ebms.core.version }}

The Adapter API consists of the following services
- [CPAService](#cpaservice)
- [UrlMappingService](#urlmappingservice)
- [CertificateMappingService](#certificatemappingservice)
- [EbMSMessageService](#ebmsmessageservice)

## CPAService

The [CPAService](https://github.com/eluinstra/ebms-core/blob/ebms-core-{{ site.data.ebms.branch.version }}/src/main/java/nl/clockwork/ebms/cpa/CPAService.java) contains functionality to manage CPAs.

### validateCPA(cpa)
{: #validateCPA}

Validates CPA `cpa`.

### insertCPA(cpa, overwrite)
{: #insertCPA}

Stores CPA `cpa`. If `overwrite` is true and the CPA exists, it will be overwritten.  
Returns the cpaId of the CPA.

### deleteCPA(cpaId)
{: #deleteCPA}

Removes CPA identified by `cpaId`.

### getCPAIds()
{: #getCPAIds}

Returns a list of all cpaIds.

### getCPA(cpaId)
{: #getCPA}

Returns the CPA identified by `cpaId`.

## UrlMappingService

The [UrlMappingService](https://github.com/eluinstra/ebms-core/blob/ebms-core-{{ site.data.ebms.branch.version }}/src/main/java/nl/clockwork/ebms/cpa/url/URLMappingService.java) contains functionality to override CPA's urls.

### setURLMapping(urlMapping)
{: #setURLMapping}

Stores URL mapping `urlMapping`.

### deleteURLMapping(source)
{: #deleteURLMapping}

Removes URL mapping identified by source URL `source`.

### getURLMappings()
{: #getURLMappings}

Returns a list of all URL mappings.

## CertificateMappingService

The [CertificateMappingService](https://github.com/eluinstra/ebms-core/blob/ebms-core-{{ site.data.ebms.branch.version }}/src/main/java/nl/clockwork/ebms/cpa/certificate/CertificateMappingService.java) contains functionality to override CPA's certificates.

### setCertificateMapping(certificateMapping)
{: #setCertificateMapping}

Stores Certificate mapping `certificateMapping`.

### deleteCertificateMapping(source)
{: #deleteCertificateMapping}

Removes Certificate mapping identified by source Certificate `source`.

### getCertificateMappings()
{: #getCertificateMappings}

Returns a list of all Certificate mappings.

## EbMSMessageService

The [EbMSMessageService](https://github.com/eluinstra/ebms-core/blob/ebms-core-{{ site.data.ebms.branch.version }}/src/main/java/nl/clockwork/ebms/service/EbMSMessageService.java) contains functionality for sending and receiving EbMS messages. There is also an MTOM [EbMSMessageService](https://github.com/eluinstra/ebms-core/blob/ebms-core-{{ site.data.ebms.branch.version }}/src/main/java/nl/clockwork/ebms/service/EbMSMessageServiceMTOM.java) available, which is more efficient.

### ping(cpaId, fromPartyId, toPartyId)
{: #ping}

Performs an EbMS ping action for CPA `cpaId`, from party `fromPartyId` to party `toPartyId`

### sendMessage(message)
{: #sendMessage}

Sends message `message` as an EbMS message.  
Returns the messageId of the generated EbMS message.

### resendMessage(messageId)
{: #resendMessage }

Resends message identified by `messageId` as an EbMS message.  
Returns the messageId of the new EbMS message.

### getUnprocessedMessageIds(messageFilter, maxNr)
{: #getUnprocessedMessageIds }

Returns all messageIds of messages with status `RECEIVED` that satisfy filter `messageFilter`. If `maxNr` is given, then maxNr messageIds are returned.

### getMessage(messageId, process)
{: #getMessage }

Returns the message identified by `messageId`. If `process` is true, the message is given the status `PROCESSED`, which means that it is no longer returned in the list of [getUnprocessedMessageIds](#getUnprocessedMessageIds).

### processMessage(messageId)
{: #processMessage }

Sets the status of the message identified by `messageId` to `PROCESSED`, so that it is no longer returned in the list of [getUnprocessedMessageIds](#getUnprocessedMessageIds).

### getMessageStatus(messageId)
{: #getMessageStatus}

Returns the message status of the message identified by `messageId`.

### getUnprocessedMessageEvents(messageFilter, eventTypes, maxNr)
{: #getUnprocessedMessageEvents }

Returns the events that satisfy filter `messageFilter` and event types `eventTypes`. If `maxNr` is given, then maxNr events are returned. The possible event types are

- `RECEIVED` - when a message is received
- `DELIVERED` - when a message has been sent successfully
- `FAILED` - when a message returns an error while sending
- `EXPIRED` - when a message could not be sent within the number of attempts and time defined in the CPA

Events can only be retrieved with this method when [EventListener property]({{ site.baseurl }}/ebms-core/properties.html#eventlistener ) `eventListener.type` is set to `DAO`.

### processMessageEvent(messageId)

Sets processed to true for the event of the message identified by `messageId`, so that it is no longer returned in the list of [getUnprocessedMessageEvents](#getUnprocessedMessageEvents) (and [getUnprocessedMessageIds](#getUnprocessedMessageIds) in case of a `RECEIVED` event).
