---
sort: 4
---

# Default Properties

Below the contents of the [default.properties](https://github.com/eluinstra/ebms-core/blob/ebms-core-{{ site.data.ebms.branch.version }}/src/main/resources/nl/clockwork/ebms/default.properties) file of ebms-core v{{ site.data.ebms.core.version }}.

### Core
```
ebms.serverId=
```
### Cache
```
# CacheType = NONE | DEFAULT(=SPRING) | EHCACHE | IGNITE
cache.type=DEFAULT
cache.configLocation=
```
### DeliveryTaskHandler
```
# DeliveryTaskHandlerType = DEFAULT(=DAO) | JMS | QUARTZ | QUARTZ_JMS
deliveryTaskHandler.start=true
deliveryTaskHandler.type=DEFAULT
deliveryTaskHandler.minThreads=16
deliveryTaskHandler.maxThreads=16
deliveryTaskHandler.default.maxTasks=100
deliveryTaskHandler.default.executionInterval=1000
deliveryTaskHandler.jms.destinationName=
deliveryTaskHandler.jms.receiveTimeout=3000
deliveryTaskHandler.quartz.driverDelegateClass=
deliveryTaskHandler.quartz.isClustered=false
deliveryTaskHandler.quartz.jdbc.driverClassName=
deliveryTaskHandler.quartz.jdbc.selectWithLockSQL=
deliveryTaskHandler.task.executionInterval=0
```
### DeliveryManager
```
# DeliveryManagerType = DEFAULT(=DAO) | JMS
deliveryManager.type=DEFAULT
deliveryManager.minThreads=2
deliveryManager.maxThreads=8
messageQueue.maxEntries=64
messageQueue.timeout=30000
```
### EventListener
When receiving a message a `RECEIVE` event is generated. After a message is sent, a `DELIVERED`, `FAILED` or `EXPIRED` event is generated.
By `DEFAULT` these events are logged to file, but it is also possible to persist and consume these events.
For that you can choose from the `type`s
- `DAO` which stores it to database
- `SIMPLE_JMS` which stores the messageId to JMS
- `JMS` which stores all message properties to JMS
- `JMS_TEXT` which stores all message properties to JMS as a text message

When `DAO` is selected, you can get the events by calling [getUnProcessedEvents]({{ site.baseurl }}/ebms-core/api.html#getUnprocessedMessageEvents).
When one of the JMS listeners is selected, you can get the events by listening to a `QUEUE` of `TOPIC` depending on the `destinationType`.
```
# EventListenerType = DEFAULT(=LOGGING) | DAO | SIMPLE_JMS | JMS | JMS_TEXT
eventListener.type=DEFAULT
eventListener.filter=
# DestinationType=QUEUE | TOPIC
eventListener.jms.destinationType=QUEUE
```
### TransactionManager
```
# TransactionManagerType = DEFAULT | BITRONIX | ATOMIKOS
transactionManager.type=DEFAULT
# IsolationLevel = | TRANSACTION_NONE | TRANSACTION_READ_UNCOMMITTED | TRANSACTION_READ_COMMITTED | TRANSACTION_REPEATABLE_READ | TRANSACTION_SERIALIZABLE | TRANSACTION_SQL_SERVER_SNAPSHOT_ISOLATION_LEVEL
transactionManager.isolationLevel=
transactionManager.transactionTimeout=300
```
### HTTPClient 
```
# EbMSHttpClientType = DEFAULT | APACHE
http.client=DEFAULT
http.connectTimeout=30000
http.chunkedStreamingMode=true
http.base64Writer=false
```
### HTTP Errors
```
http.errors.informational.recoverable=
http.errors.redirection.recoverable=
http.errors.client.recoverable=408,429
http.errors.server.unrecoverable=501,505,510
```
### SSL
```
https.protocols=
https.cipherSuites=
https.verifyHostnames=true
https.clientCertificateAuthentication=false
https.useClientCertificate=false
```
### Forward Proxy
```
http.proxy.host=
http.proxy.port=0
http.proxy.nonProxyHosts=127.0.0.1,localhost
http.proxy.username=
http.proxy.password=
```
### EbMS Message Storage
If `deleteContentOnProcessed=true` then the attachments of a received message are deleted right after it has been processed and the attachments of a sent message are deleted right after it has been acknowledged (, failed or expired).  
If `ebmsMessage.storeDuplicateContent=false` then the attachments of a duplicate message are not stored. If `ebmsMessage.storeDuplicate=false` then the whole duplicate message is not stored.
```
ebmsMessage.deleteContentOnProcessed=false
ebmsMessage.storeDuplicate=true
ebmsMessage.storeDuplicateContent=true
```
### Overflow attachments to disk
A large attachment will be cached in a temporary file if it exceeds the `memoryTreshold`. The temporary files are written to `outputDirectory` if set, otherwise to the default temp directory. To enable file encryption set `cipherTransformation` to a stream or 8-bit block cipher transformation (like RC4, AES/CTR/NoPadding, etc). Note: This will result in an increased processing time.
```
ebmsMessage.attachment.memoryTreshold=131072
ebmsMessage.attachment.outputDirectory=
ebmsMessage.attachment.cipherTransformation=
```
### Auto retry acknowledgements, incl. max and interval (in minutes)
It is possible to retry sending best-effort messages after a technical error (like a connection error).
`ebmsMessage.nrAutoRetries` sets the maximum number of retries.
`autoRetryInterval` sets the retry interval in 
Note: This is not according to the EbMS Specifications, but will not violate them either
```
ebmsMessage.nrAutoRetries=0
ebmsMessage.autoRetryInterval=5
```
### Truststore
```
truststore.type=PKCS12
truststore.path=nl/clockwork/ebms/truststore.p12
truststore.password=password
```
### SSL Client keystore
```
client.keystore.type=PKCS12
client.keystore.path=nl/clockwork/ebms/keystore.p12
client.keystore.password=password
client.keystore.keyPassword=${client.keystore.password}
client.keystore.defaultAlias=
```
### EbMS Signature keystore
```
signature.keystore.type=PKCS12
signature.keystore.path=nl/clockwork/ebms/keystore.p12
signature.keystore.password=password
signature.keystore.keyPassword=${signature.keystore.password}
```
### EbMS Encryption keystore
```
encryption.keystore.type=PKCS12
encryption.keystore.path=nl/clockwork/ebms/keystore.p12
encryption.keystore.password=password
encryption.keystore.keyPassword=${encryption.keystore.password}
```
### JMS
```
jms.broker.config=classpath:nl/clockwork/ebms/activemq.xml
jms.broker.username=
jms.broker.password=
jms.broker.start=false
jms.brokerURL=vm://localhost
jms.pool.minPoolSize=32
jms.pool.maxPoolSize=32
```
### Datastore
```
ebms.jdbc.driverClassName=org.hsqldb.jdbcDriver
ebms.jdbc.url=jdbc:hsqldb:mem:ebms
ebms.jdbc.username=sa
ebms.jdbc.password=
ebms.jdbc.update=false
ebms.jdbc.strict=false
```
### Datastore connection pool
```
ebms.pool.autoCommit=true
ebms.pool.connectionTimeout=30000
ebms.pool.maxIdleTime=600000
ebms.pool.maxLifetime=1800000
ebms.pool.testQuery=
ebms.pool.minPoolSize=16
ebms.pool.maxPoolSize=32
```
