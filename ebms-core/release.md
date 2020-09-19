---
sort: 7
---

# Release Notes

### ebms-core-2.17.3.jar
- fixed URL Mapping
- renamed status SENDING to CREATED
- updated libraries

### ebms-core-2.17.2.jar
- added UserRateLimiterFilter
- added EventListenerFilter
	- added default property eventListener.filter=
- added h2 database support
- added flyway db migration
	- added default properties
		- ebms.jdbc.update=false
		- ebms.jdbc.strict=false

### ebms-core-2.17.1.jar
- improved logging
- improved error responses
- fixed auto retry responses only if best effort
- fixed configuration issue, that causes send events to be rejected
- fixed server, client and api mode
- added rate limiter
	- added default property http.requestsPerSecond=

### ebms-core-2.17.0.jar
- added options to enable high availability and horizontal scaling (and throttling)
- added option to use SSL clientCerttificate defined in the CPA to send messages (https.useClientCertificate)
	- added CertificateMapper SOAP service to override defined SSL clientCertificate
- cleaned up and split up SOAP interfaces
- changed SOAP Services:
	- renamed operations from EbMSMessageService:
		- GetMessageIds to GetUnprocessedMessageIds
		- GetMessageEvents to GetUnprocessedMessageEvents
	- removed operations from EbMSMessageService:
		- SendMessageWithAttachments (use SendMessage from ebmsMTOM instead)
		- GetMassageStatus is replaced by GetMessageStatusByMessageId, old GetMessageStatus is removed
		- ProcessMessages (use ProcessMessage instead)
		- ProcessMessageEvents (use ProcessMessageEvent instead)
	- split up CPAService into CPAService and URLMapper
- changed default properties
	- removed properties:
		- ebms.allowMultipleServers (leave property ebms.serverId empty to set allowMultipleServers to false)
		- patch.digipoort.enable (not necessary anymore)
		- patch.oracle.enable (not necessary anymore)
		- patch.cleo.enable (not necessary anymore)
		- cache.disabled (use cache.type instead)
		- eventProcessor.enabled=true (use eventProcessor.type=NONE instead)
	- changed default value of property
		- http.base64Writer to false (writer is disabled anyway because of an issue)
		- https.clientCertificateAuthentication to false
	- added properties:\
		- https.useClientCertificate=false
		- client.keystore.keyPassword=${client.keystore.password}
		- client.keystore.defaultAlias=
		- signature.keystore.keyPassword=${signature.keystore.password}
		- encryption.keystore.keyPassword=${encryption.keystore.password}
		- cache.type=DEFAULT (allowed values: DEFAULT(=	SPRING) \| EHCACHE \| IGNITE)
		- eventProcessor.type=DEFAULT (allowed values: NONE \| DEFAULT(=DAO) \| JMS)
		- deliveryManager.type=DEFAULT (allowed types: DEFAULT(=DAO) \| JMS)
		- eventListener.type=DEFAULT (allowed values: DEFAULT(=LOGGING) \| DAO \| SIMPLE_JMS \| JMS \| JMS_TEXT)
		- transactionManager.type=DEFAULT (allowed values: DEFAULT \| ATOMIKOS)
	* see src/main/resources/nl/clockwork/ebms/default.properties for all available properties
- implemented JMS components (for scaling)
- added Atomikos transaction manager (for JMS)
- added Apache Ignite cache manager (for scaling)
- added Flyway to install and upgrade database
- code improvements
	- added lombok and vavr
	- made objects immutable where possible
	- moved spring bean configuration from xml to code
	- restructured classes and packages
	- reconfigured caching and transactions
	- split up DAO
	- replaced jdbcTemplate by querydsl
	- replace commons-logging by slf4j
	- lots of other improvements
- updated libraries
- database updates and improved indices

### ebms-core-2.16.7.jar
- disabled base64Writer because of a bug when sending base64 encoded content
- fixed using header defined in property x509CertificateHeader

### ebms-core-2.16.6.jar
- fixed bug: the references in a signed acknowledgment is not validated correctly, which will not set the status of the message to DELIVERED but eventually to EXPIRED instead
- fixed issue using asynchronous messaging and no receive deliveryChannel can be found. The message will be stored and returned synchronously as an error now
- fixed deliveryChannel validation not handled correctly, causing a SOAP fault being returned instead of a EbMS MessageError in case of an error

### ebms-core-2.16.5.jar
- optimized memory usage by using CachedOutputStream for attachments that overflows to disk:
	- added property ebmsMessage.attachment.memoryTreshold - default: 128KB 
	- added property ebmsMessage.attachment.outputDirectory - default: <tempDir>
	- added property ebmsMessage.attachment.cipherTransformation - default: none

### ebms-core-2.16.4.jar
- fixed EbMSEventProcessor: the processor sometimes stops processing after an error occurs, so the ebms adapter stops sending messages
- fixed query in deleteEbMSAttachmentsOnMessageProcessed
- fixed messageId: the hostname is not prepended anymore when the messageId is given
- added new MTOM EbMS soap service

### ebms-core-2.16.3.jar
- fixed bug: messages are sometimes sent more than once at (almost) the same time
- improved EbMSEventProcessor
- renamed property jobScheduler.enabled to eventProcessor.enabled
- renamed property jobScheduler.delay to eventProcessor.delay
- renamed property jobScheduler.period to eventProcessor.period
- renamed property job.maxTreads to eventProcessor.maxTreads
- renamed property job.processorsScaleFactor to eventProcessor.processorsScaleFactor
- renamed property job.queueScaleFactor to eventProcessor.queueScaleFactor
- improved EbMSResponseHandler
- renamed property http.errors.server.irrecoverable to http.errors.server.unrecoverable

### ebms-core-2.16.2.jar
- removed MIME-Version header

### ebms-core-2.16.1.jar
- improved client ssl behaviour
- added keystore type support
- minor improvements

### ebms-core-2.16.0.jar
- upgraded to java 8
- minor improvements

