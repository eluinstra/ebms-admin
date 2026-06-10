---
sidebar_position: 6
---

# EbMS API

The EbMS API consists of the following services

- [CPAService](#cpaservice)
- [URLMappingService](#urlmappingservice)
- [CertificateMappingService](#certificatemappingservice)
- [EbMSMessageService](#ebmsmessageservice)
- [Types](#types) — shared parameter / return objects and enums

These services are implemented as a SOAP and a REST interface.

Machine-readable specifications and ready-to-run sample requests:

- REST: [OpenAPI 3.0 spec (`ebms.yml`)](https://github.com/eluinstra/ebms-core/blob/ebms-core-@ebms.branch.version@/core/resources/test/ebms.yml)
- SOAP: [WSDL (`ebms.wsdl`)](https://github.com/eluinstra/ebms-core/blob/ebms-core-@ebms.branch.version@/core/resources/test/ebms.wsdl)
- Sample REST calls: [`ebms.rest`](https://github.com/eluinstra/ebms-core/blob/ebms-core-@ebms.branch.version@/core/resources/test/ebms.rest) (JetBrains HTTP Client / VS Code REST Client format)
- Sample SOAP calls: [SoapUI project (`EbMS-soapui-project.xml`)](https://github.com/eluinstra/ebms-core/blob/ebms-core-@ebms.branch.version@/core/resources/test/EbMS-soapui-project.xml), [MTOM variant](https://github.com/eluinstra/ebms-core/blob/ebms-core-@ebms.branch.version@/core/resources/test/EbMSMTOM-soapui-project.xml)

## Transports: REST vs SOAP

All services are exposed both as REST and SOAP. Both are served by the same embedded CXF servlet mounted at `/service/*` (default ports: `8080` HTTP, `8443` HTTPS). You can also find and overview of the available endpoints at `https://<host>:8443/service`.

| Aspect | REST | SOAP |
|---|---|---|
| Base URL | `https://<host>:8443/service/rest/v19` | `https://<host>:8443/service` |
| Per-service path | `/cpas`, `/urlMappings`, `/certificateMappings`, `/ebms` | `/cpa`, `/urlMapping`, `/certificateMapping`, `/ebms` |
| Body format | JSON (CPA: `text/xml`; `deleteCertificateMapping`: `text/plain`) | SOAP 1.1 envelope (`text/xml`) |
| Binary payloads | Inline base64 in JSON, or multipart MTOM at `/ebms/messages/mtom` | Inline base64, or MTOM via `EbMSMessageServiceMTOM` |
| Filter parameters | `MessageFilter` fields flattened into query parameters (e.g. `?cpaId=…&fromPartyId=…`) | `MessageFilter` sent as a structured object inside the SOAP body |
| Certificates | Base64-encoded `String` in JSON | XML-marshalled `X509Certificate` (`xs:base64Binary`) per WSDL schema |
| Spec | [OpenAPI 3.0](https://github.com/eluinstra/ebms-core/blob/ebms-core-@ebms.branch.version@/core/resources/test/ebms.yml) | [WSDL](https://github.com/eluinstra/ebms-core/blob/ebms-core-@ebms.branch.version@/core/resources/test/ebms.wsdl) |

The remainder of this page describes the services and their operations at the logical level; concrete REST URLs and SOAP envelopes are in the spec files linked above.

## Errors

All service exceptions are unchecked (`RuntimeException`) and, on SOAP, are marshalled as a `<service>Exception` SOAP fault (e.g. `CPAServiceException`, `EbMSMessageServiceException`). On REST they are mapped to HTTP status codes as follows:

| Condition | Exception (Java) | HTTP status | Body |
|---|---|---|---|
| CPA not found | `CPANotFoundException` | `404 Not Found` | empty |
| URL mapping not found | `URLNotFoundException` | `404 Not Found` | empty |
| Certificate mapping not found | `CertificateNotFoundException` | `404 Not Found` | empty |
| Message not found | `NotFoundException` | `404 Not Found` | empty |
| Invalid CPA XML (`validateCPA`, `insertCPA`) | `BadRequestException` | `400 Bad Request` | error message |
| Any other failure | `*ControllerException` (per service) | `500 Internal Server Error` | error message |

There is currently no dedicated status for duplicate `messageId` on `sendMessage`; deduplication is handled internally by the message processor and is not surfaced as a 409.

## CPAService

The [CPAService](https://github.com/eluinstra/ebms-core/blob/ebms-core-@ebms.branch.version@/src/main/java/nl/clockwork/ebms/cpa/CPAService.java) contains functionality to manage CPAs. See also [Overview](/ebms-core/overview.md) for the role of the CPA in EbMS, and the [Cache property](/ebms-core/properties.md#cache) for CPA cache configuration.

Example (REST) — insert a CPA:

```bash
curl -X POST 'https://localhost:8443/service/rest/v19/cpas?overwrite=true' \
  -H 'Content-Type: text/xml' \
  --data-binary @cpa.xml
```

### validateCPA(cpa)

Validates CPA `cpa`. Throws a fault if the CPA is invalid.  
Returns: void.

### insertCPA(cpa, overwrite)

Stores CPA `cpa`. If `overwrite` is true and the CPA exists, it will be overwritten.  
Returns the cpaId of the CPA.

**Parameters**

- `cpa` (String, required) — an XML document containing a `CollaborationProtocolAgreement` as defined by the ebXML CPP/CPA 2.0 schema (namespace `http://www.oasis-open.org/committees/ebxml-cppa/schema/cpp-cpa-2_0.xsd`). The cpaId is read from the root element's `cpaid` attribute, for example:

  ```xml
  <tns:CollaborationProtocolAgreement
      xmlns:tns="http://www.oasis-open.org/committees/ebxml-cppa/schema/cpp-cpa-2_0.xsd"
      tns:cpaid="cpaStubEBF.rm.https.signed"
      tns:version="1.0">
    ...
  </tns:CollaborationProtocolAgreement>
  ```

- `overwrite` (Boolean, optional) — if `true`, replaces an existing CPA with the same id.

### deleteCPA(cpaId)

Removes CPA identified by `cpaId`.  
Returns: void.

### getCPAIds()

Returns a list of all cpaIds.  
Returns: List\<String\>.

### getCPA(cpaId)

Returns the CPA identified by `cpaId`.  
Returns: String — the CPA XML document, or empty if not found.

## URLMappingService

The [URLMappingService](https://github.com/eluinstra/ebms-core/blob/ebms-core-@ebms.branch.version@/src/main/java/nl/clockwork/ebms/cpa/url/URLMappingService.java) contains functionality to override CPA's urls.

Example (REST) — add a URL mapping:

```bash
curl -X POST 'https://localhost:8443/service/rest/v19/urlMappings' \
  -H 'Content-Type: application/json' \
  -d '{
    "source": "http://www.a.com",
    "destination": "http://www.b.com"
  }'
```

### setURLMapping(urlMapping)

Stores URL mapping `urlMapping` (see [URLMapping](#urlmapping)).  
Returns: void.

### deleteURLMapping(source)

Removes URL mapping identified by source URL `source`.  
Returns: void.

### getURLMappings()

Returns a list of all URL mappings.  
Returns: List\<URLMapping\>.

## CertificateMappingService

The [CertificateMappingService](https://github.com/eluinstra/ebms-core/blob/ebms-core-@ebms.branch.version@/src/main/java/nl/clockwork/ebms/cpa/certificate/CertificateMappingService.java) contains functionality to override CPA's certificates.

Example (REST) — add a certificate mapping (base64-encoded X.509 certificates, truncated below):

```bash
curl -X POST 'https://localhost:8443/service/rest/v19/certificateMappings' \
  -H 'Content-Type: application/json' \
  -d '{
    "source":      "MIICYTCCAcqgAwIBAgIETsDxqzANBgkqhkiG9w0BAQUFADB1...",
    "destination": "MIICYTCCAcqgAwIBAgIETsDxqzANBgkqhkiG9w0BAQUFADB1..."
  }'
```

### setCertificateMapping(certificateMapping)

Stores Certificate mapping `certificateMapping` (see [CertificateMapping](#certificatemapping)).  
Returns: void.

### deleteCertificateMapping(source)

Removes Certificate mapping identified by source Certificate `source`.  
Returns: void.

### getCertificateMappings()

Returns a list of all Certificate mappings.  
Returns: List\<CertificateMapping\>.

## EbMSMessageService

The [EbMSMessageService](https://github.com/eluinstra/ebms-core/blob/ebms-core-@ebms.branch.version@/src/main/java/nl/clockwork/ebms/service/EbMSMessageService.java) contains functionality for sending and receiving EbMS messages. There is also an MTOM variant, [EbMSMessageServiceMTOM](https://github.com/eluinstra/ebms-core/blob/ebms-core-@ebms.branch.version@/src/main/java/nl/clockwork/ebms/service/EbMSMessageServiceMTOM.java).

### Message flow

Outbound — `sendMessage` hands the message to the asynchronous delivery pipeline; the client observes the result by polling events (or via JMS).

![EbMS API send flow](/assets/images/ebms-api-send-flow.svg)

Inbound — the MSH stores received messages and emits a `RECEIVED` event; the client polls, fetches the payload, and marks the message processed.

![EbMS API receive flow](/assets/images/ebms-api-receive-flow.svg)

Operations:

- [ping](#pingcpaid-frompartyid-topartyid)
- [sendMessage](#sendmessagemessage) / [resendMessage](#resendmessagemessageid)
- [getUnprocessedMessageIds](#getunprocessedmessageidsmessagefilter-maxnr), [getMessage](#getmessagemessageid-process), [processMessage](#processmessagemessageid)
- [getMessageStatus](#getmessagestatusmessageid)
- [getUnprocessedMessageEvents](#getunprocessedmessageeventsmessagefilter-eventtypes-maxnr), [processMessageEvent](#processmessageeventmessageid)

### Choosing between the regular and MTOM service

| | Regular `EbMSMessageService` | `EbMSMessageServiceMTOM` |
|---|---|---|
| Payload encoding | Inline as `DataSource.content` (base64 in JSON / `xs:base64Binary` in SOAP) | Streamed binary parts (MTOM/XOP for SOAP, `multipart/form-data` for REST) |
| Memory & bandwidth | Entire payload held in memory; ~33% base64 overhead | Streamed; no base64 overhead |
| Recommended for | Small payloads, simple clients | Large or binary payloads (PDFs, ZIPs, EDI/XML \> a few MB) |
| REST endpoints | `POST /ebms/messages`, `GET /ebms/messages/{id}` | `POST /ebms/messages/mtom`, `GET /ebms/messages/mtom/{id}` |
| SOAP request shape | `sendMessage(MessageRequest)` with inline payloads | `sendMessageMTOM(MessageRequestProperties properties, List<DataSource> attachments)` \u2014 properties and attachments are separate MTOM parts |
| Other operations | Same set (`ping`, `resendMessage`, filters, events, \u2026) | Same set; only the message-content operations differ |

The non-content operations (`ping`, `resendMessage`, `getUnprocessedMessageIds`, `processMessage`, `getMessageStatus`, `getUnprocessedMessageEvents`, `processMessageEvent`) behave identically in both services.

Related configuration:

- [DeliveryManager](/ebms-core/properties.md#deliverymanager), [DeliveryTaskHandler](/ebms-core/properties.md#deliverytaskhandler), [DeliveryTaskManager](/ebms-core/properties.md#deliverytaskmanager) — outbound send / retry behaviour.
- [Signature keystore](/ebms-core/properties.md#signature-keystore), [Encryption keystore](/ebms-core/properties.md#encryption-keystore), [SSL](/ebms-core/properties.md#ssl) — signing, encryption and transport security.
- [EbMS Message Storage](/ebms-core/properties.md#ebms-message-storage), [Overflow attachments to disk](/ebms-core/properties.md#overflow-attachments-to-disk) — payload storage.
- [EventListener](/ebms-core/properties.md#eventlistener), [JMS](/ebms-core/properties.md#jms) — event delivery (required for `getUnprocessedMessageEvents`).

Example (REST) — send a message with one attachment:

```bash
curl -X POST 'https://localhost:8443/service/rest/v19/ebms/messages' \
  -H 'Content-Type: application/json' \
  -d '{
    "properties": {
      "cpaId": "cpaStubEBF.rm.https.signed",
      "fromPartyId": "urn:osb:oin:00000000000000000000",
      "fromRole": "DIGIPOORT",
      "toPartyId": "urn:osb:oin:00000000000000000001",
      "toRole": "OVERHEID",
      "service": "urn:osb:services:osb:afleveren:1.1$1.0",
      "action": "afleveren"
    },
    "dataSources": [{
      "name": "test.txt",
      "contentType": "text/plain",
      "content": "U2FtcGxlIG1lc3NhZ2Uu"
    }]
  }'
```

For large or binary payloads, prefer the MTOM endpoint `POST /service/rest/v19/ebms/messages/mtom`, which takes a multipart request with a `requestProperties` JSON part and one or more `attachment` parts (see `ebms-core/core/resources/test/ebms.rest` for a full example).

Note: in the REST API, `getUnprocessedMessageIds` and `getUnprocessedMessageEvents` flatten the [MessageFilter](#messagefilter) into query parameters (`cpaId`, `fromPartyId`, `fromRole`, `toPartyId`, `toRole`, `service`, `action`, `conversationId`, `messageId`, `refToMessageId`) instead of taking a JSON body. The SOAP API uses the structured `MessageFilter` object.

### ping(cpaId, fromPartyId, toPartyId)

Performs an EbMS ping action for CPA `cpaId`, from party `fromPartyId` to party `toPartyId`.  
Returns: void. Throws a fault if no Pong is received.

### sendMessage(message)

Sends message `message` (see [MessageRequest](#messagerequest)) as an EbMS message.  
Returns the messageId of the generated EbMS message.

Notes on identifiers:

- If `messageId` is omitted, a new EbMS-conformant id is generated and returned. If you supply your own `messageId`, it must be globally unique; the receiving MSH will reject (or, on a reliable-messaging channel, deduplicate) a second message with the same id.
- `conversationId` ties related messages together. If omitted, a new conversation is started; supply the same value to continue an existing conversation.
- `refToMessageId` is used for response / acknowledgement correlation: set it to the `messageId` of the message you are replying to.

### resendMessage(messageId)

Resends message identified by `messageId` as an EbMS message.  
Returns the messageId of the new EbMS message.

Resending is not idempotent at the EbMS level: each call produces a new EbMS message (with a new `messageId`) that carries the same payload and `refToMessageId` chain. For reliable-messaging channels, prefer letting the built-in retry mechanism handle transient failures (see [DeliveryTaskHandler](/ebms-core/properties.md#deliverytaskhandler)); use `resendMessage` only after a message has reached `FAILED` or `EXPIRED`.

### getUnprocessedMessageIds(messageFilter, maxNr)

Returns all messageIds of messages with status `RECEIVED` that satisfy filter `messageFilter` (see [MessageFilter](#messagefilter)). If `maxNr` is given, then maxNr messageIds are returned.  
Returns: List\<String\>.

### getMessage(messageId, process)

Returns the message identified by `messageId`. If `process` is true, the message is given the status `PROCESSED`, which means that it is no longer returned in the list of [getUnprocessedMessageIds](#getunprocessedmessageidsmessagefilter-maxnr).  
Returns: [Message](#message).

### processMessage(messageId)

Marks the message identified by `messageId` as `PROCESSED`, so that it is no longer returned by [getUnprocessedMessageIds](#getunprocessedmessageidsmessagefilter-maxnr).  
Returns: void.

### getMessageStatus(messageId)

Returns the message status of the message identified by `messageId`.  
Returns: [EbMSMessageStatus](#ebmsmessagestatus).

### getUnprocessedMessageEvents(messageFilter, eventTypes, maxNr)

Returns the events that satisfy filter `messageFilter` (see [MessageFilter](#messagefilter)) and event types `eventTypes` (a list of [MessageEventType](#messageeventtype) values). If `maxNr` is given, then maxNr events are returned.  
Returns: List\<[MessageEvent](#messageevent)\>.

Events can only be retrieved with this method when [EventListener property](/ebms-core/properties.md#eventlistener) `eventListener.type` is set to `DAO`.

### processMessageEvent(messageId)

Marks the event of the message identified by `messageId` as processed, so that it is no longer returned by [getUnprocessedMessageEvents](#getunprocessedmessageeventsmessagefilter-eventtypes-maxnr) (and, for a `RECEIVED` event, also no longer by [getUnprocessedMessageIds](#getunprocessedmessageidsmessagefilter-maxnr)).  
Returns: void.

## Types

Shared object and enum types referenced by the service operations above. In the REST API these correspond to JSON objects; in the SOAP API they are XML elements defined in the [WSDL](https://github.com/eluinstra/ebms-core/blob/ebms-core-@ebms.branch.version@/core/resources/test/ebms.wsdl).

### URLMapping

- `source` (String, required) — the source URL (as it appears in the CPA) to be rewritten.
- `destination` (String, required) — the URL that requests to `source` are redirected to.

### CertificateMapping

- `source` (String, required) — base64-encoded X.509 certificate (as it appears in the CPA) to be replaced.
- `destination` (String, required) — base64-encoded X.509 certificate to use instead.
- `cpaId` (String, optional) — if set, restricts the mapping to the CPA with this id; otherwise the mapping applies to all CPAs.

### MessageRequest

- `properties` ([MessageRequestProperties](#messagerequestproperties), required) — addressing and routing properties.
- `dataSources` (List\<[DataSource](#datasource)\>, optional) — message payloads / attachments.

### MessageRequestProperties

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

### DataSource

- `name` (String, optional) — payload name.
- `contentId` (String, optional) — MIME `Content-Id`; generated if omitted.
- `contentType` (String, required) — MIME content type of the payload.
- `content` (byte[], required) — raw payload bytes (base64-encoded in the JSON representation).

### Message

Returned by [getMessage](#getmessagemessageid-process).

- `properties` ([MessageProperties](#messageproperties)) — metadata of the received message.
- `dataSources` (List\<[DataSource](#datasource)\>) — message payloads.

### MessageProperties

All fields of [MessageRequestProperties](#messagerequestproperties), plus:

- `fromParty` ([Party](#party)) — resolved sending party.
- `toParty` ([Party](#party)) — resolved receiving party.
- `timestamp` (Instant) — when the message was received.
- `messageStatus` ([EbMSMessageStatus](#ebmsmessagestatus)) — current status of the message.

### MessageFilter

Used by [getUnprocessedMessageIds](#getunprocessedmessageidsmessagefilter-maxnr) and [getUnprocessedMessageEvents](#getunprocessedmessageeventsmessagefilter-eventtypes-maxnr). All fields are optional; only set fields are used to narrow the result.

- `cpaId` (String)
- `fromParty` ([Party](#party))
- `toParty` ([Party](#party))
- `service` (String)
- `action` (String)
- `conversationId` (String)
- `messageId` (String)
- `refToMessageId` (String)

In the REST API these are flattened into query parameters (`cpaId`, `fromPartyId`, `fromRole`, `toPartyId`, `toRole`, `service`, `action`, `conversationId`, `messageId`, `refToMessageId`).

### Party

- `partyId` (String, required)
- `role` (String, optional)

### MessageEvent

Returned by [getUnprocessedMessageEvents](#getunprocessedmessageeventsmessagefilter-eventtypes-maxnr).

- `messageId` (String, required) — id of the message the event refers to.
- `type` ([MessageEventType](#messageeventtype), required).

### EbMSMessageStatus (enum) {#ebmsmessagestatus}

Lifecycle status of a message. Common values include:

- `RECEIVED` — message received and stored, not yet processed by the client.
- `PROCESSED` — message marked as processed by the client.
- `DELIVERED` — outbound message acknowledged by the receiving MSH.
- `FAILED` — outbound message returned an error.
- `EXPIRED` — outbound message could not be delivered within the configured retries / persist duration.

### MessageEventType (enum) {#messageeventtype}

- `RECEIVED` — when a message is received.
- `DELIVERED` — when a message has been sent successfully.
- `FAILED` — when a message returns an error while sending.
- `EXPIRED` — when a message could not be sent within the number of attempts and time defined in the CPA.
