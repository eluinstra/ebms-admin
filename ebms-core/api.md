---
sort: 6
---

# Adapter APIs

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

##### ping(cpaId, fromParty, toParty)
performs an EbMS ping action for CPA cpaId, from party fromParty and to party toParty

##### sendMessage(messageContent)
sends the message content messageContent as an EbMS message  
returns the messageId of the generated EbMS message

##### getMessageIds(filter, maxNr)
{: #getMessageIds }
returns a list of messageIds of messages with the status `RECEIVED` that satisfy the messageContext filter.  
If maxNr is given, then maxNr messageIds are returned

##### getMessage(messageId, process)
{: #getMessage }
returns the message content of the message identified by messageId.  
If process is true, the message is given the status `PROCESSED`, which means that it is no longer returned to the list of getMessageIds

##### processMessage(messageId)
{: #processMessage }
sets the status of the message identified by messageId to `PROCESSED`, so that it is no longer returned to the list of getMessageIds

##### getMessageEvents(messageContext, eventTypes, maxNr)
{: #getMessageEvents }
returns the events that satisfy the messageContext filter and the eventTypes eventTypes.  
If maxNr is given, then maxNr events are returned. The possible event types are
- `RECEIVED` - when a message is received
- `DELIVERED` - when a message has been sent successfully
- `FAILED` - when a message returns an error while sending
- `EXPIRED` - when a message could not be sent within the number of attempts and time agreed in the CPA

##### processMessageEvent(messageId)
set processed to true for all currently known events for the message identified by `messageId`, so that it is no longer returned in the list of [getMessageEvents](#getMessageEvents) (and [getMessageIds](#getMessageIds))
