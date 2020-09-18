---
sort: 9
---

# Release Notes

### ebms-admin-2.17.3.jar
- upgrade to ebms-core-2.17.3.jar
	- fixed URL Mapping
	- renamed status SENDING to CREATED
	- updated libraries

### ebms-admin-2.17.2.jar
- improved properties configuration
- moved  flyway db migration to ebms-core
- changed commandline arguments:
	- removed db migration arguments updateDb and updateStrict
	- renamed rate limiter argument requestsPerSecond to queriesPerSecond
	- added user rate limiter argument userQueriesPerSecond
- upgrade to ebms-core-2.17.2.jar:
	- added UserRateLimiterFilter
	- added EventListenerFilter
		- added default property eventListener.filter=
	- added h2 database support
	- added flyway db migration
		- added default properties
			- ebms.jdbc.update=false
			- ebms.jdbc.strict=false

### ebms-admin-2.17.1.1.jar
- fixed updateDb option
- changed commandline arguments:

### ebms-admin-2.17.1.jar
- improved logging
- removed Server response header
- added flyway migration
- added rate limiter
- changed commandline arguments:
	- added db migration arguments updateDb and updateStrict
	- added rate limiter argument requestsPerSecond
- upgrade to ebms-core-2.17.1.jar:
	- improved logging
	- improved error responses
	- fixed auto retry responses only if best effort
	- fixed configuration issue, that causes send events to be rejected
	- fixed server, client and api mode
	- added rate limiter
		- added default property http.requestsPerSecond=

### ebms-admin-2.17.0.1.jar
- changed default settings
- changed configuration of the test scenario
- updated CPA endpoints

### ebms-admin-2.17.0.jar
- added options to enable high availability and horizontal scaling (and throttling)
- changed command line arguments:
	- renamed propertiesFilesDir to configDir
	- removed property log4j.file (use -Dlog4j.configurationFile=log4j2.xml instead)
	- added ssl arguments protocols and cipherSuites
	- added jmx arguments
	- added arguments disableEbMSClient and disableEbMSServer
- split up CPAService into CPAService and URLMapper
- added new SOAP service CertificateMapper
- updated EbMS Admin Properties Page
- removed EbMS Core Properties Page
- added database java command line tools:
	- DBMigrate (java -cp ebms-admin-2.17.0.jar nl.clockwork.ebms.admin.DBMigrate -h)
	- DBClean (java -cp ebms-admin-2.17.0.jar nl.clockwork.ebms.admin.DBClean -h)
- upgrade to ebms-core-2.17.0.jar:
	- added options to enable availability and horizontal scaling (and throttling)
	- added option to use SSL clientCerttificate defined in the CPA to send messages (https.useClientCertificate)
		- added CertificateMapper SOAP service to override defined SSL clientCertificate
	- cleaned up and split up SOAP interfaces
	- changed SOAP Services:
		- renamed operations from EbMSMessageService:
			- GetMessageIds to GetUnprocessedMessageIds
			- GetMessageEvents to GetUnprocessedMessageEvents
		- removed operations from EbMSMessageService:
			- SendMessageWithAttachments (use SendMessage from ebmsMTOM instead)
			- GetMessageStatus is replaced by GetMessageStatusByMessageId, old GetMessageStatus is removed
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
		- added properties:
			- https.useClientCertificate=false
			- client.keystore.keyPassword=${client.keystore.password}
			- client.keystore.defaultAlias=
			- signature.keystore.keyPassword=${signature.keystore.password}
			- encryption.keystore.keyPassword=${encryption.keystore.password}
			- cache.type=DEFAULT (allowed values: DEFAULT(=SPRING) | EHCACHE | IGNITE)
			- eventProcessor.type=DEFAULT (allowed values: NONE | DEFAULT(=DAO) | JMS)
			- deliveryManager.type=DEFAULT (allowed types: DEFAULT(=DAO) | JMS)
			- eventListener.type=DEFAULT (allowed values: DEFAULT(=LOGGING) | DAO | SIMPLE_JMS | JMS | JMS_TEXT)
			- transactionManager.type=DEFAULT (allowed values: DEFAULT | ATOMIKOS)
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
- updated libraries
- updated keystores and CPAs

### ebms-admin-2.16.7.jar
- upgrade to ebms-core-2.16.7.jar:
	- disabled base64Writer because of a bug when sending base64 encoded content
	- fixed using header defined in property x509CertificateHeader

### ebms-admin-2.16.6.jar
- fixed CXF logging
- upgrade to ebms-core-2.16.6.jar:
  - fixed bug: the references in a signed acknowledgment is not validated correctly, which will not set the status of the message to DELIVERED but eventually to EXPIRED instead
  - fixed issue using asynchronous messaging and no receive deliveryChannel can be found. The message will be stored and returned synchronously as an error now
  - fixed deliveryChannel validation not handled correctly, causing a SOAP fault being returned instead of a EbMS MessageError in case of an error

### ebms-admin-2.16.5.jar
- upgrade to ebms-core-2.16.5.jar:
	- optimized memory usage by using CachedOutputStream for attachments that overflows to disk:
		- added property ebmsMessage.attachment.memoryTreshold - default: 128KB 
		- added property ebmsMessage.attachment.outputDirectory - default: <tempDir>
		- added property ebmsMessage.attachment.cipherTransformation - default: none

### ebms-admin-2.16.4.jar
- added new MTOM EbMS soap service endpoint on /service/ebmsMTOM
- upgrade to ebms-core-2.16.4.jar:
	- fixed EbMSEventProcessor: the processor sometimes stops processing after an error occurs, so the ebms adapter stops sending messages
	- fixed query in deleteEbMSAttachmentsOnMessageProcessed
	- fixed messageId: the hostname is not prepended anymore when the messageId is given
	- added new MTOM EbMS soap service

### ebms-admin-2.16.3.jar
- minor configuration improvements
- upgrade to ebms-core-2.16.3.jar:
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

### ebms-admin-2.16.2a.jar
- fixed reading the encryption keystore property which caused an error opening the Configuration Page after the encryption keystore was set
- minor layout improvements

### ebms-admin-2.16.2.jar
- fixed default value of log4j.file property which caused a startup error when ebms-admin-2.16.1.jar was fresh installed 
- upgrade to ebms-core-2.16.2.jar:
	- removed MIME-Version header

### ebms-admin-2.16.1.jar
- upgraded to java 8
- https support for web and soap interfaces (including client certificate authentication)
- upgraded a lot of libraries (included wicked-charts-wicket8)
- added the option to use PKCS12 keystores and truststores
- removed all previously included jdbc drivers from the release
- minor improvements
- upgrade to ebms-core-2.16.1.jar:
	- upgraded to java 8
	- minor improvements
