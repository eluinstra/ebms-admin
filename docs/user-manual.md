---
sidebar_position: 4
---

# User Manual

This page describes how to use the EbMS Adapter through its REST and SOAP interfaces. See [EbMS Admin](/ebms-admin/introduction.md) for installation and [EbMS API](/ebms-core/api.md) for a method-level reference. The OpenAPI spec is available [here](https://github.com/eluinstra/ebms-core/blob/ebms-core-@ebms.branch.version@/resources/test/ebms.yml).

Before you can exchange EbMS messages with another party you have to register a [CPA](/ebms-core/api.md#cpaservice) that defines the contract between both parties (endpoints, certificates, retry policy). Optionally you can override CPA URLs and certificates per environment using [URL Mappings](/ebms-core/api.md#urlmappingservice) and [Certificate Mappings](/ebms-core/api.md#certificatemappingservice). After that you can [exchange messages](#exchange-messages) and track them through [message events](#message-events).

## Manage CPAs

You can manage CPAs using the REST and SOAP [CPAService](/ebms-core/api.md#cpaservice).

![Manage CPAs](/assets/images/user-manual-manage-cpas.svg)

1. [`validateCPA(cpa)`](/ebms-core/api.md#validatecpacpa) — validate the CPA document before storing it
2. [`insertCPA(cpa, overwrite)`](/ebms-core/api.md#insertcpacpa-overwrite) — store the CPA; if `overwrite` is `true` an existing CPA with the same `cpaId` is replaced
3. [`getCPAIds()`](/ebms-core/api.md#getcpaids) — list all registered `cpaId`s
4. [`getCPA(cpaId)`](/ebms-core/api.md#getcpacpaid) — retrieve a stored CPA
5. [`deleteCPA(cpaId)`](/ebms-core/api.md#deletecpacpaid) — remove a CPA

## Manage URL Mappings

You can override the endpoint URLs from a CPA per environment using the REST and SOAP [URLMappingService](/ebms-core/api.md#urlmappingservice). This is useful for routing the same CPA to a remote EbMS adapter through a Proxy Server or for testing purposes.

![Manage URL Mappings](/assets/images/user-manual-manage-url-mappings.svg)

1. [`setURLMapping(urlMapping)`](/ebms-core/api.md#seturlmappingurlmapping) — map a `source` URL (as defined in the CPA) to a destination URL
2. [`getURLMappings()`](/ebms-core/api.md#geturlmappings) — list all URL mappings
3. [`deleteURLMapping(source)`](/ebms-core/api.md#deleteurlmappingsource) — remove the mapping for `source`

## Manage Certificate Mappings

You can override the SSL certificates from a CPA per environment using the REST and SOAP [CertificateMappingService](/ebms-core/api.md#certificatemappingservice).

![Manage Certificate Mappings](/assets/images/user-manual-manage-certificate-mappings.svg)

1. [`setCertificateMapping(certificateMapping)`](/ebms-core/api.md#setcertificatemappingcertificatemapping) — map a `source` SSL certificate (as defined in the CPA) to a destination SSL certificate
2. [`getCertificateMappings()`](/ebms-core/api.md#getcertificatemappings) — list all certificate mappings
3. [`deleteCertificateMapping(source)`](/ebms-core/api.md#deletecertificatemappingsource) — remove the mapping for `source`

## Exchange Messages

The EbMS Adapter exchanges messages with remote EbMS adapters defined in a CPA, using the REST and SOAP [EbMSMessageService](/ebms-core/api.md#ebmsmessageservice). For large or binary payloads use the MTOM-enabled variant (see [EbMS API](/ebms-core/api.md#ebmsmessageservice)).

### Send a message

![Send a message](/assets/images/user-manual-send-message.svg)

1. [`sendMessage(message)`](/ebms-core/api.md#sendmessagemessage) — submit the message to the EbMS Adapter; the adapter returns a generated `messageId` and asynchronously delivers it to the remote adapter according to the CPA's retry policy
2. [`getMessageStatus(messageId)`](/ebms-core/api.md#getmessagestatusmessageid) — check the delivery status
3. Optional: [`resendMessage(messageId)`](/ebms-core/api.md#resendmessagemessageid) — resend a message; a new `messageId` is returned

To be notified of delivery results without polling, configure an [EventListener](/ebms-core/properties.md#eventlistener) and consume [Message Events](#message-events).

### Receive messages

![Receive messages](/assets/images/user-manual-receive-message.svg)

When the remote adapter sends an EbMS message, the EbMS Adapter stores it with status `RECEIVED`. The application polls and processes it:

1. [`getUnprocessedMessageIds(messageFilter, maxNr)`](/ebms-core/api.md#getunprocessedmessageidsmessagefilter-maxnr) — list message IDs that match `messageFilter` and have status `RECEIVED`
2. [`getMessage(messageId, process)`](/ebms-core/api.md#getmessagemessageid-process) — retrieve the message; when `process` is `true` the message is marked `PROCESSED` and no longer returned by `getUnprocessedMessageIds`
3. Alternative: retrieve the message with `process=false` and call [`processMessage(messageId)`](/ebms-core/api.md#processmessagemessageid) after successful processing

### Ping

You can verify connectivity and the CPA configuration with a remote adapter without sending business data:

![Ping](/assets/images/user-manual-ping.svg)

1. [`ping(cpaId, fromPartyId, toPartyId)`](/ebms-core/api.md#pingcpaid-frompartyid-topartyid) — performs an EbMS ping for the given CPA and party IDs

## Message Events

Message Events let an application track the lifecycle of sent and received messages. They require [`eventListener.type = DAO`](/ebms-core/properties.md#eventlistener).

![Message Events](/assets/images/user-manual-message-events.svg)

1. [`getUnprocessedMessageEvents(messageFilter, eventTypes, maxNr)`](/ebms-core/api.md#getunprocessedmessageeventsmessagefilter-eventtypes-maxnr) — list events matching `messageFilter` and `eventTypes`. Event types:
   - `RECEIVED` — a message was received
   - `DELIVERED` — a message was sent successfully
   - `FAILED` — a message returned an error while sending
   - `EXPIRED` — a message could not be sent within the attempts and time defined in the CPA
2. [`getMessage(messageId, process)`](/ebms-core/api.md#getmessagemessageid-process) — retrieve the message referenced by a `RECEIVED` event
3. [`processMessageEvent(messageId)`](/ebms-core/api.md#processmessageeventmessageid) — mark the event processed so it is no longer returned by `getUnprocessedMessageEvents` (and, for `RECEIVED` events, by `getUnprocessedMessageIds`)
