---
sidebar_position: 6
---

# EbMS API

The EbMS API consists of the following services

- [CPAService](#cpaservice)
- [UrlMappingService](#urlmappingservice)
- [CertificateMappingService](#certificatemappingservice)
- [EbMSMessageService](#ebmsmessageservice)

These services are implemented as a SOAP and a REST interface.

## CPAService

The [CPAService](https://github.com/eluinstra/ebms-core/blob/ebms-core-@ebms.branch.version@/src/main/java/nl/clockwork/ebms/cpa/CPAService.java) contains functionality to manage CPAs.

### validateCPA(cpa)

Validates CPA `cpa`.

### insertCPA(cpa, overwrite)

Stores CPA `cpa`. If `overwrite` is true and the CPA exists, it will be overwritten.  
Returns the cpaId of the CPA.

**Parameters**

- `cpa` (String, required) — an XML document containing a `CollaborationProtocolAgreement` as defined by the ebXML CPP/CPA 2.0 schema. The `cpaId` is extracted from the document's `cpaid` attribute.
- `overwrite` (Boolean, optional) — if `true`, replaces an existing CPA with the same id.

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

**URLMapping**

- `source` (String, required) — the source URL (as it appears in the CPA) to be rewritten.
- `destination` (String, required) — the URL that requests to `source` are redirected to.

### deleteURLMapping(source)

Removes URL mapping identified by source URL `source`.

### getURLMappings()

Returns a list of all URL mappings.

## CertificateMappingService

The [CertificateMappingService](https://github.com/eluinstra/ebms-core/blob/ebms-core-@ebms.branch.version@/src/main/java/nl/clockwork/ebms/cpa/certificate/CertificateMappingService.java) contains functionality to override CPA's certificates.

### setCertificateMapping(certificateMapping)

Stores Certificate mapping `certificateMapping`.

**CertificateMapping**

- `source` (String, required) — base64-encoded X.509 certificate (as it appears in the CPA) to be replaced.
- `destination` (String, required) — base64-encoded X.509 certificate to use instead.
- `cpaId` (String, optional) — if set, restricts the mapping to the CPA with this id; otherwise the mapping applies to all CPAs.

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

**MessageRequest**

- `properties` (MessageRequestProperties, required) — addressing and routing properties.
- `dataSources` (List\<DataSource\>, optional) — message payloads / attachments.

**MessageRequestProperties**

- `cpaId` (String, required) — id of the CPA that governs this message.
- `fromPartyId` (String, required) — id of the sending party (as listed in the CPA).
- `fromRole` (String, required) — role of the sending party.
- `toPartyId` (String, optional) — id of the receiving party; derived from the CPA if omitted.
- `toRole` (String, optional) — role of the receiving party; derived from the CPA if omitted.
- `service` (String, required) — ebXML service name.
- `action` (String, required) — ebXML action name.
- `conversationId` (String, optional) — generated if omitted.
- `messageId` (String, optional) — generated if omitted.
- `refToMessageId` (String, optional) — id of a previous message this message refers to.

**DataSource**

- `name` (String, optional) — payload name.
- `contentId` (String, optional) — MIME `Content-Id`; generated if omitted.
- `contentType` (String, required) — MIME content type of the payload.
- `content` (byte[], required) — raw payload bytes.

### resendMessage(messageId)

Resends message identified by `messageId` as an EbMS message.  
Returns the messageId of the new EbMS message.

### getUnprocessedMessageIds(messageFilter, maxNr)

Returns all messageIds of messages with status `RECEIVED` that satisfy filter `messageFilter`. If `maxNr` is given, then maxNr messageIds are returned.

**MessageFilter** (all fields optional; only set fields are used to narrow the result)

- `cpaId` (String)
- `fromParty` (Party)
- `toParty` (Party)
- `service` (String)
- `action` (String)
- `conversationId` (String)
- `messageId` (String)
- `refToMessageId` (String)

**Party**

- `partyId` (String, required)
- `role` (String, optional)

### getMessage(messageId, process)

Returns the message identified by `messageId`. If `process` is true, the message is given the status `PROCESSED`, which means that it is no longer returned in the list of [getUnprocessedMessageIds](#getunprocessedmessageidsmessagefilter-maxnr).

**Returns: Message**

- `properties` (MessageProperties) — all fields of [MessageRequestProperties](#sendmessagemessage) plus:
  - `fromParty` (Party)
  - `toParty` (Party)
  - `timestamp` (Instant) — when the message was received.
  - `messageStatus` (EbMSMessageStatus) — e.g. `RECEIVED`, `PROCESSED`, `DELIVERED`, `FAILED`, `EXPIRED`.
- `dataSources` (List\<DataSource\>) — message payloads.

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

**MessageEvent**

- `messageId` (String, required) — id of the message the event refers to.
- `type` (MessageEventType, required) — one of `RECEIVED`, `DELIVERED`, `FAILED`, `EXPIRED`.

The `messageFilter` parameter uses the same [MessageFilter](#getunprocessedmessageidsmessagefilter-maxnr) object as `getUnprocessedMessageIds`. `eventTypes` is a list of `MessageEventType` values to include.

### processMessageEvent(messageId)

Sets processed to true for the event of the message identified by `messageId`, so that it is no longer returned in the list of [getUnprocessedMessageEvents](#getunprocessedmessageeventsmessagefilter-eventtypes-maxnr) (and [getUnprocessedMessageIds](#getunprocessedmessageidsmessagefilter-maxnr) in case of a `RECEIVED` event).
