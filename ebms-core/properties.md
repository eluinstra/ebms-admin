---
sort: 4
---

# Default Properties

Below the [default properties](https://github.com/eluinstra/ebms-core/blob/ebms-core-2.17.x/src/main/resources/nl/clockwork/ebms/default.properties) of ebms-core.

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
```
# EventListenerType = DEFAULT(=LOGGING) | DAO | SIMPLE_JMS | JMS | JMS_TEXT
eventListener.type=DEFAULT
eventListener.filter=
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
### EbMS Storage
```
ebmsMessage.deleteContentOnProcessed=false
ebmsMessage.storeDuplicate=true
ebmsMessage.storeDuplicateContent=true
```
### Overflow attachments to disk
```
ebmsMessage.attachment.memoryTreshold=131072
ebmsMessage.attachment.outputDirectory=
ebmsMessage.attachment.cipherTransformation=
```
### Auto retry acknowledgements, incl. max and interval (in minutes)
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
jms.destinationType=QUEUE
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
