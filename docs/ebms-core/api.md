---
sidebar_position: 6
---

# EbMS API

The EbMS API consists of the following services

- [CPAService](#cpaservice)
- [UrlMappingService](#urlmappingservice)
- [CertificateMappingService](#certificatemappingservice)
- [EbMSMessageService](#ebmsmessageservice)

These services are implemented as a SOAP interface and since EbMS 2.18.3 also as a REST interface.

## CPAService

The [CPAService](https://github.com/eluinstra/ebms-core/blob/ebms-core-@ebms.branch.version@/src/main/java/nl/clockwork/ebms/cpa/CPAService.java) contains functionality to manage CPAs.

### validateCPA(cpa)

Validates CPA `cpa`.

### insertCPA(cpa, overwrite)

Stores CPA `cpa`. If `overwrite` is true and the CPA exists, it will be overwritten.  
Returns the cpaId of the CPA.

### deleteCPA(cpaId)

Removes CPA identified by `cpaId`.

### getCPAIds()

Returns a list of all cpaIds.

### getCPA(cpaId)

Returns the CPA identified by `cpaId`.

## UrlMappingService

The [UrlMappingService](https://github.com/eluinstra/ebms-core/blob/ebms-core-@ebms.branch.version@/src/main/java/nl/clockwork/ebms/cpa/url/URLMappingService.java) contains functionality to override CPA's urls.

### setURLMapping(urlMapping)

Stores URL mapping `urlMapping`.

### deleteURLMapping(source)

Removes URL mapping identified by source URL `source`.

### getURLMappings()

Returns a list of all URL mappings.

## CertificateMappingService

The [CertificateMappingService](https://github.com/eluinstra/ebms-core/blob/ebms-core-@ebms.branch.version@/src/main/java/nl/clockwork/ebms/cpa/certificate/CertificateMappingService.java) contains functionality to override CPA's certificates.

### setCertificateMapping(certificateMapping)

Stores Certificate mapping `certificateMapping`.

### deleteCertificateMapping(source)

Removes Certificate mapping identified by source Certificate `source`.

### getCertificateMappings()

Returns a list of all Certificate mappings.

## EbMSMessageService

The [EbMSMessageService](https://github.com/eluinstra/ebms-core/blob/ebms-core-@ebms.branch.version@/src/main/java/nl/clockwork/ebms/service/EbMSMessageService.java) contains functionality for sending and receiving EbMS messages. There is also an MTOM [EbMSMessageService](https://github.com/eluinstra/ebms-core/blob/ebms-core-@ebms.branch.version@/src/main/java/nl/clockwork/ebms/service/EbMSMessageServiceMTOM.java) available, which is more efficient.

### ping(cpaId, fromPartyId, toPartyId)

Performs an EbMS ping action for CPA `cpaId`, from party `fromPartyId` to party `toPartyId`

### sendMessage(message)

Sends message `message` as an EbMS message.  
Returns the messageId of the generated EbMS message.

### resendMessage(messageId)

Resends message identified by `messageId` as an EbMS message.  
Returns the messageId of the new EbMS message.

### getUnprocessedMessageIds(messageFilter, maxNr)

Returns all messageIds of messages with status `RECEIVED` that satisfy filter `messageFilter`. If `maxNr` is given, then maxNr messageIds are returned.

### getMessage(messageId, process)

Returns the message identified by `messageId`. If `process` is true, the message is given the status `PROCESSED`, which means that it is no longer returned in the list of [getUnprocessedMessageIds](#getunprocessedmessageidsmessagefilter-maxnr).

### processMessage(messageId)

Sets the status of the message identified by `messageId` to `PROCESSED`, so that it is no longer returned in the list of [getUnprocessedMessageIds](#getunprocessedmessageidsmessagefilter-maxnr).

### getMessageStatus(messageId)

Returns the message status of the message identified by `messageId`.

### getUnprocessedMessageEvents(messageFilter, eventTypes, maxNr)

Returns the events that satisfy filter `messageFilter` and event types `eventTypes`. If `maxNr` is given, then maxNr events are returned. The possible event types are

- `RECEIVED` - when a message is received
- `DELIVERED` - when a message has been sent successfully
- `FAILED` - when a message returns an error while sending
- `EXPIRED` - when a message could not be sent within the number of attempts and time defined in the CPA

Events can only be retrieved with this method when [EventListener property](/ebms-core/properties.md#eventlistener ) `eventListener.type` is set to `DAO`.

### processMessageEvent(messageId)

Sets processed to true for the event of the message identified by `messageId`, so that it is no longer returned in the list of [getUnprocessedMessageEvents](#getunprocessedmessageeventsmessagefilter-eventtypes-maxnr) (and [getUnprocessedMessageIds](#getunprocessedmessageidsmessagefilter-maxnr) in case of a `RECEIVED` event).
