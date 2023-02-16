---
sort: 7
---

# Release Notes

### [ebms-core-2.19.0.jar](https://repo1.maven.org/maven2/nl/clockwork/ebms/ebms-core/2.19.0/ebms-core-2.19.0.jar)

- upgrade to Java 11
- updated dependecies
- removed storing duplicate messages
- cleanup database (complying to SQL 2008 specifications)
- removed MySQL support
- removed Bitronix
- removed Apache HttpClient
- replaced URLConnection by Java 11 HttpClient
- updated REST API

### [ebms-core-2.18.7.jar](https://repo1.maven.org/maven2/nl/clockwork/ebms/ebms-core/2.18.7/ebms-core-2.18.7.jar)

- updated dependencies
- added deleteCache methods for CPA, URL and Certificate services

### [ebms-core-2.18.6.jar](https://repo1.maven.org/maven2/nl/clockwork/ebms/ebms-core/2.18.6/ebms-core-2.18.6.jar)

- updated dependencies for spring, postgresql, jackson
- bugfix: NullPointerException on delivery failure in best effort mode
- bugfix: serverId not null if not (explicitly) overridden
- bugfix: mysql/mssql deleteAttachments override to include correct db-column
- improvement: rest responsetype for CPA service implementation

### [ebms-core-2.18.5.jar](https://repo1.maven.org/maven2/nl/clockwork/ebms/ebms-core/2.18.5/ebms-core-2.18.5.jar)

- updated various dependencies
- fix Azure keyvault integration config 

### [ebms-core-2.18.4.jar](https://repo1.maven.org/maven2/nl/clockwork/ebms/ebms-core/2.18.4/ebms-core-2.18.4.jar)

- updated various dependencies [0918189](https://github.com/eluinstra/ebms-core/commit/09181894cc100cac83d6a621885dc2c6cc937eb5)

### [ebms-core-2.18.3.jar](https://repo1.maven.org/maven2/nl/clockwork/ebms/ebms-core/2.18.3/ebms-core-2.18.3.jar)

- update CPAManager: changed streams, naming and extended tests
- REST service interface added
- (initial) Apache JMeter tests added for performance testing the SOAP and REST interface

### [ebms-core-2.18.2.jar](https://repo1.maven.org/maven2/nl/clockwork/ebms/ebms-core/2.18.2/ebms-core-2.18.2.jar)

- removed Querydsl (+ issue fixes)
- added Apache Kafka integration for delivery events
- added Microsoft Azure Key Vault integration

### [ebms-core-2.18.1.jar](https://repo1.maven.org/maven2/nl/clockwork/ebms/ebms-core/2.18.1/ebms-core-2.18.1.jar)

- fix, notnull datasource
- dependency and plugin version upgrades
- fixed issue with conversationId used as messageId
- jax instant and duration converter 2 adapters
- added HTTP read timeout as requested by Capgemini
- fixed bug: NullPointerException occurs when an attachment has no name
- implemented a fix proposed by Lost Lemon, a too strict xsd validation causes the adapter to break at startup. added XXE vuln test taken from the 2.17 branch

### [ebms-core-2.18.0.jar](https://repo1.maven.org/maven2/nl/clockwork/ebms/ebms-core/2.18.0/ebms-core-2.18.0.jar)

- improved SOAP interface
- added QuartzDeliveryTaskManager and QuartzJMSDeliveryTaskManager
- renamed EbMSEvent to DeliveryTask
- renamed EbMSEventLog to DeliveryLog
- renamed EbMSMessageEvent to MessageEvent
- renamed JMS SendTask queue from SEND to DELIVERY_TASK
- renamed properties eventProcessor.* to deliveryTaskHandler.*
- renamed table ebms_event to send_task
- renamed table ebms_event_log to send_log
- renamed table ebms_message_event to ebms_event

### [ebms-core-2.17.11.jar](https://repo1.maven.org/maven2/nl/clockwork/ebms/ebms-core/2.17.10/ebms-core-2.17.10.jar)

- updated dependencies for spring, mysql, postgresql, ignite, h2
- bugfix: serverId not null if not (explicitly) overridden
- bugfix: mysql/mssql deleteAttachments override to include correct db-column

### [ebms-core-2.17.10.jar](https://repo1.maven.org/maven2/nl/clockwork/ebms/ebms-core/2.17.10/ebms-core-2.17.10.jar)

- updated various dependencies [70b51a4](https://github.com/eluinstra/ebms-core/commit/70b51a47a1a8ebefb8c6f0a132aaa269b8af3327)

### [ebms-core-2.17.9.jar](https://repo1.maven.org/maven2/nl/clockwork/ebms/ebms-core/2.17.9/ebms-core-2.17.9.jar)

- update xmlsec dependency version
- removed @NonNull for name in EbMSDataSource service model

### [ebms-core-2.17.8.jar](https://repo1.maven.org/maven2/nl/clockwork/ebms/ebms-core/2.17.8/ebms-core-2.17.8.jar)

- removed querydsl (+ issue fixes)

### [ebms-core-2.17.7.jar](https://repo1.maven.org/maven2/nl/clockwork/ebms/ebms-core/2.17.7/ebms-core-2.17.7.jar)

- maven plugin version upgrades
- dependency version upgrades, almost all dependencies updated to latest version which still runs on java 1.8
- changed jaxb instant and duration converters to adapters
- added Microsoft Azure Key Vault integration
- fixed issue with conversationId used as messageId
- fixed NullPointerException when sending message in Best Effort mode

### [ebms-core-2.17.6.jar](https://repo1.maven.org/maven2/nl/clockwork/ebms/ebms-core/2.17.6/ebms-core-2.17.6.jar)

- bugfix: `NullPointerException` occurs when an attachment has no name
- added property `http.readTimeout`
- added `RemoteAddressMDCFilter`
- improved XSD validation
- improved error handling

### [ebms-core-2.17.5.jar](https://repo1.maven.org/maven2/nl/clockwork/ebms/ebms-core/2.17.5/ebms-core-2.17.5.jar)

- added MDC logging
- improved XML parsing
- improved error handling

### ebms-core-2.17.4.jar

- implemented `HealthServlet`
- fixed DB2 and Oracle XA configuration
- fixed H2, MSSQL DAOs
- fixed DB2, H2, MSSQL, MySQL/MariaDB and Oracle flyway scripts

### ebms-core-2.17.3.jar

- fixed `URLMapper`
- renamed status `SENDING` to `CREATED`
- updated libraries

### ebms-core-2.17.2.jar

- added `UserRateLimiterFilter`
- added `EventListenerFilter`
	- added default property `eventListener.filter=`
- added H2 database support
- added Flyway database migration
	- added default properties
		- `ebms.jdbc.update=false`
		- `ebms.jdbc.strict=false`

### ebms-core-2.17.1.jar

- improved logging
- improved error responses
- fixed auto retry responses only if best effort
- fixed configuration issue, that causes send events to be rejected
- fixed EbMS Server, EbMS Client and SOAP API mode
- added rate limiter
	- added default property `http.requestsPerSecond=`

### ebms-core-2.17.0.jar

- added options to enable high availability and horizontal scaling (and throttling)
- added option to use SSL client certtificate defined in the CPA to send messages (`https.useClientCertificate`)
	- added `CertificateMapper` SOAP service to override defined SSL client certificate
- cleaned up and split up SOAP interfaces
- changed SOAP Services
	- renamed operations from `EbMSMessageService`
		- `GetMessageIds` to `GetUnprocessedMessageIds`
		- `GetMessageEvents` to `GetUnprocessedMessageEvents`
	- removed operations from EbMSMessageService
		- `SendMessageWithAttachments` (use `SendMessage` from ebmsMTOM instead)
		- `GetMassageStatus` is replaced by `GetMessageStatusByMessageId`, old `GetMessageStatus` is removed
		- `ProcessMessages` (use `ProcessMessage` instead)
		- `ProcessMessageEvents` (use `ProcessMessageEvent` instead)
	- split up `CPAService` into `CPAService` and `URLMapper`
- changed default properties
	- removed properties
		- `ebms.allowMultipleServers` (leave property `ebms.serverId` empty to set `allowMultipleServers` to `false`)
		- `patch.digipoort.enable` (not necessary anymore)
		- `patch.oracle.enable` (not necessary anymore)
		- `patch.cleo.enable` (not necessary anymore)
		- `cache.disabled` (use cache.type instead)
		- `eventProcessor.enabled=true` (use `eventProcessor.type=NONE` instead)
	- changed default value of property
		- `http.base64Writer` to `false` (writer is disabled anyway because of an issue)
		- `https.clientCertificateAuthentication` to `false`
	- added properties
		- `https.useClientCertificate=false`
		- `client.keystore.keyPassword=${client.keystore.password}`
		- `client.keystore.defaultAlias=`
		- `signature.keystore.keyPassword=${signature.keystore.password}`
		- `encryption.keystore.keyPassword=${encryption.keystore.password}`
		- `cache.type=DEFAULT` (allowed values: `DEFAULT` (SPRING) \| `EHCACHE` \| `IGNITE`)
		- `eventProcessor.type=DEFAULT` (allowed values: `NONE` \| `DEFAULT` (DAO) \| `JMS`)
		- `deliveryManager.type=DEFAULT` (allowed types: `DEFAULT` (DAO) \| `JMS`)
		- `eventListener.type=DEFAULT` (allowed values: `DEFAULT` (LOGGING) \| `DAO` \| `SIMPLE_JMS` \| `JMS` \| `JMS_TEXT`)
		- `transactionManager.type=DEFAULT` (allowed values: `DEFAULT` \| `ATOMIKOS`)
	* see `src/main/resources/nl/clockwork/ebms/default.properties` for all available properties
- implemented JMS components (for scaling)
- added Atomikos transaction manager (for JMS)
- added Apache Ignite cache manager (for scaling)
- added Flyway to install and upgrade database
- code improvements
	- added Lombok and VAVR
	- made objects immutable where possible
	- moved Spring bean configuration from XML to code
	- restructured classes and packages
	- reconfigured caching and transactions
	- split up DAO
	- replaced String JDBC by QueryDSL
	- replace Commons-Logging by SLF4J
	- lots of other improvements
- updated libraries
- database updates and improved indices

### [ebms-core-2.16.8.jar](https://repo1.maven.org/maven2/nl/clockwork/ebms/ebms-core/2.16.8/ebms-core-2.16.8.jar)

- improved XML parsing
- improved error handling

### ebms-core-2.16.7.jar

- disabled `base64Writer` because of a bug when sending base64 encoded content
- fixed using header defined in property `x509CertificateHeader`

### ebms-core-2.16.6.jar

- fixed bug: the references in a signed acknowledgment is not validated correctly, which will not set the status of the message to `DELIVERED` but eventually to `EXPIRED` instead
- fixed issue using asynchronous messaging and no receive `deliveryChannel` can be found. The message will be stored and returned synchronously as an error now
- fixed `deliveryChannel` validation not handled correctly, causing a SOAP fault being returned instead of a EbMS `MessageError` in case of an error

### ebms-core-2.16.5.jar

- optimized memory usage by using `CachedOutputStream` for attachments that overflows to disk
	- added property `ebmsMessage.attachment.memoryTreshold` - default: 128KB 
	- added property `ebmsMessage.attachment.outputDirectory` - default: \<tempDir>
	- added property `ebmsMessage.attachment.cipherTransformation` - default: none

### ebms-core-2.16.4.jar

- fixed `EbMSEventProcessor`: the processor sometimes stops processing after an error occurs, so the ebms adapter stops sending messages
- fixed query in `deleteEbMSAttachmentsOnMessageProcessed`
- fixed `messageId`: the `hostname` is not prepended anymore when the `messageId` is given
- added new MTOM EbMS SOAP service

### ebms-core-2.16.3.jar

- fixed bug: messages are sometimes sent more than once at (almost) the same time
- improved `EbMSEventProcessor`
- renamed property `jobScheduler.enabled` to `eventProcessor.enabled`
- renamed property `jobScheduler.delay` to `eventProcessor.delay`
- renamed property `jobScheduler.period` to `eventProcessor.period`
- renamed property `job.maxTreads` to `eventProcessor.maxTreads`
- renamed property `job.processorsScaleFactor` to `eventProcessor.processorsScaleFactor`
- renamed property `job.queueScaleFactor` to `eventProcessor.queueScaleFactor`
- improved `EbMSResponseHandler`
- renamed property `http.errors.server.irrecoverable` to `http.errors.server.unrecoverable`

### ebms-core-2.16.2.jar

- removed MIME-Version header

### ebms-core-2.16.1.jar

- improved client SSL behaviour
- added keystore type support
- minor improvements

### ebms-core-2.16.0.jar

- upgraded to Java 8
- minor improvements
