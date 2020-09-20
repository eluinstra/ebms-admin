---
sort: 6
---

# Adapter API

ebms-core v{{ site.data.ebms.core.version }}

## CPA
The CPA API contains functionality to manage CPAs 

##### validateCPA(cpa)
validates CPA cpa

##### insertCPA(cpa, overwrite)
stores CPA cpa in the EbMS database. If overwrite is true and the CPA exists, it will be overwritten. The function returns the cpaId of the CPA

##### deleteCPA(cpaId)
removes CPA identified by cpaId from the EbMS database

##### getCPAIds()
returns a list of all cpaIds from the EbMS database

##### getCPA(cpaId)
returns the CPA identified by cpaId from the EbMS database

## Url Mapping
The EbMSMessageService contains functionality to override CPA's urls. The URL mapping maps the source URL to the destination URL.  

##### setURLMapping(urlMapping)
stores URL mapping urlMapping in the EbMS database

##### deleteURLMapping(source)
removes URL mapping identified by source URL source from the EbMS database

##### getURLMappings()
returns a list of all URL mappings from the EbMS database

## Certificate Mapping
The EbMSMessageService contains functionality to override CPA's certificates

## EbMS Message
The EbMSMessageService contains functionality for sending and receiving EbMS messages

##### ping(cpaId, fromPartyId, toPartyId)
{: #ping}
Performs an EbMS ping action for CPA `cpaId`, from party `fromPartyId` to party `toPartyId`

##### sendMessage(message)
{: #sendMessage}
Sends message `message` as an EbMS message 
returns the messageId of the generated EbMS message

##### resendMessage(messageId)
{: #resendMessage }
Resends message `messageId` as an EbMS message 
returns the messageId of the new EbMS message

##### getUnprocessedMessageIds(messageFilter, maxNr)
{: #getUnprocessedMessageIds }
Gets all messageIds of messages with status `RECEIVED` that satisfy filter `messageFilter`.  
If `maxNr` is given, then maxNr messageIds are returned

##### getMessage(messageId, process)
{: #getMessage }
Returns the message identified by `messageId`.  
If `process` is true, the message is given the status `PROCESSED`, which means that it is no longer returned in the list of [getUnprocessedMessageIds](#getUnprocessedMessageIds)

##### processMessage(messageId)
{: #processMessage }
Sets the status of the message identified by `messageId` to `PROCESSED`, so that it is no longer returned in the list of [getUnprocessedMessageIds](#getUnprocessedMessageIds)

##### getMessageStatus(messageId)
{: #getMessageStatus}
Returns the message status of the message identified by `messageId`

##### getUnprocessedMessageEvents(messageFilter, eventTypes, maxNr)
{: #getUnprocessedMessageEvents }
Returns the events that satisfy filter `messageFilter` and event types `eventTypes`.  
If maxNr is given, then maxNr events are returned. The possible event types are
- `RECEIVED` - when a message is received
- `DELIVERED` - when a message has been sent successfully
- `FAILED` - when a message returns an error while sending
- `EXPIRED` - when a message could not be sent within the number of attempts and time defined in the CPA
Events can only be retreived with this method when [EventListener property]({{ site.baseurl }}/ebms-core/properties.html#eventlistener ) `eventListener.type` is set to `DAO`

##### processMessageEvent(messageId)
Sets processed to true for the event of the message identified by `messageId`, so that it is no longer returned in the list of [getUnprocessedMessageEvents](#getUnprocessedMessageEvents) (and [getUnprocessedMessageIds](#getUnprocessedMessageIds) in case of a `RECEIVED` event)
