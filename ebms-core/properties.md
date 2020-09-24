---
sort: 4
---

# Default Properties

Below the contents of the [default.properties](https://github.com/eluinstra/ebms-core/blob/ebms-core-{{ site.data.ebms.branch.version }}/src/main/resources/nl/clockwork/ebms/default.properties) file of ebms-core v{{ site.data.ebms.core.version }}. These are the default settings for ebms-core.

### Core

You have to set `ebms.serverId` with a unique serverId per server when you are [scaling with serverId]({{ site.baseurl }}/ebms-admin/deployment.html#scaling-serverid).

```properties
ebms.serverId=
```

### Cache

Set `deliveryTaskHandler.type` to `IGNITE` or `EHCACHE` when you are [scaling]({{ site.baseurl }}/ebms-admin/deployment.html#scaling) the EbMS Adapter, otherwise leave it set to `DEFAULT`. You can also disable caching by setting `deliveryTaskHandler.type` to `NONE`, but this is not adviced. The scaling configuration for `IGNITE` works out of the box, the scaling configuration for `EHCACHE` you have to configure yourself. You can find the default configuration file for `IGNITE` [here](https://github.com/eluinstra/ebms-core/blob/ebms-core-{{ site.data.ebms.branch.version }}/src/main/resources/nl/clockwork/ebms/ignite.xml) and you can find the default configuration file for `EHCACHE` [here](https://github.com/eluinstra/ebms-core/blob/ebms-core-{{ site.data.ebms.branch.version }}/src/main/resources/nl/clockwork/ebms/ehcache.xml).

```properties
# CacheType: NONE | DEFAULT (SPRING) | EHCACHE | IGNITE
cache.type=DEFAULT
cache.configLocation=
```

### DeliveryTaskHandler

The DeliveryTaskHandler is used to [send EbMS Messages]({{ site.baseurl }}/ebms-core/api.html#sendMessage) asynchronously. Set `deliveryTaskHandler.type` to `JMS`, `QUARTZ` or `QUARTZ_JMS` when you are using [advanced scaling]({{ site.baseurl }}/ebms-admin/deployment.html#scaling-advanced), otherwise leave it set to `DEFAULT`or set it to `QUARTZ`. When `deliveryTaskHandler.type` is set to `JMS`or `QUARTZ_JMS` configure [JMS](#jms) and the [TransactionManager](#transactionmanager).

```properties
# DeliveryTaskHandlerType: DEFAULT (DAO) | JMS | QUARTZ | QUARTZ_JMS
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

### DeliveryTaskManager

It is possible to retry sending best-effort messages after a technical error (like a connection error). `ebmsMessage.nrAutoRetries` sets the maximum number of retries. `ebmsMessage.autoRetryInterval` sets the retry interval in minutes. Note: This is not according to the EbMS Specifications, but will not violate them either.

```properties
deliveryTaskManager.nrAutoRetries=0
deliveryTaskManager.autoRetryInterval=5
```

### DeliveryManager

The DeliveryManager is used to handle EbMS [Ping]({{ site.baseurl }}/ebms-core/api.html#ping) and [getMessageStatus]({{ site.baseurl }}/ebms-core/api.html#getMessageStatus) calls. Set `deliveryManager.type` to `JMS` when you are [scaling]({{ site.baseurl }}/ebms-admin/deployment.html#scaling) the EbMS Adapter, otherwise leave it set to `DEFAULT`. When `deliveryManager.type` is set to `JMS` configure [JMS](#jms).

```properties
# DeliveryManagerType: DEFAULT (DAO) | JMS
deliveryManager.type=DEFAULT
deliveryManager.minThreads=2
deliveryManager.maxThreads=8
messageQueue.maxEntries=64
messageQueue.timeout=30000
```

### EventListener

When receiving a message a `RECEIVE` event is generated. After a message is sent, a `DELIVERED`, `FAILED` or `EXPIRED` event is generated. By `DEFAULT` these events are logged to file, but it is also possible to persist and consume these events. For that you can choose from the EventListenerTypes

- `DAO` which stores it to database
- `SIMPLE_JMS` which stores the messageId to JMS
- `JMS` which stores all message properties to JMS
- `JMS_TEXT` which stores all message properties to JMS as a text message

When `DAO` is selected, you can get the events by calling [getUnProcessedEvents]({{ site.baseurl }}/ebms-core/api.html#getUnprocessedMessageEvents). When one of the JMS listeners is selected, you can get the events by listening to a `QUEUE` or `TOPIC` depending on the `destinationType`. You then also have to configure [JMS](#jms). Events can be filtered by providing a comma separated list of events to be filtered out in `eventListener.filter`.

```properties
# EventListenerType: DEFAULT (LOGGING) | DAO | SIMPLE_JMS | JMS | JMS_TEXT
eventListener.type=DEFAULT
eventListener.filter=
# DestinationType: QUEUE | TOPIC
eventListener.jms.destinationType=QUEUE
```

### TransactionManager

When `deliveryTaskHandler.type` is `DEFAULT` or `QUARTZ` then set `transactionManager.type=DEFAULT`.
When `deliveryTaskHandler.type` is `JMS` or `QUARTZ_JMS` then set `transactionManager.type=ATOMIKOS` and select an XA driver for your [database]({{ site.baseurl }}{% link ebms-core/database.md %}).

```properties
# TransactionManagerType: DEFAULT | BITRONIX | ATOMIKOS
transactionManager.type=DEFAULT
# IsolationLevel: <EMPTY> | TRANSACTION_NONE | TRANSACTION_READ_UNCOMMITTED | TRANSACTION_READ_COMMITTED | TRANSACTION_REPEATABLE_READ | TRANSACTION_SERIALIZABLE | TRANSACTION_SQL_SERVER_SNAPSHOT_ISOLATION_LEVEL
transactionManager.isolationLevel=
transactionManager.transactionTimeout=300
```

### HTTPClient

Setting `http.client` to `APACHE` is not tested lately, so leave it to `DEFAULT`.  
Note: The `http.base64Writer` property is disabled for now because of problems sending Base64 content.

```properties
# EbMSHttpClientType: DEFAULT | APACHE
http.client=DEFAULT
http.connectTimeout=30000
http.chunkedStreamingMode=true
http.base64Writer=false
```

### HTTP Errors

Defines the recoverable and unrecoverable errors to determine the unrecoverable errors on which the EbMS adapter will let a sent message fail.  
By default the EbM adapter will fail on all recoverable errors except for `408` and `429` and will only fail on unrecoverable errors `501`, `505` and `510`. Recoverable errors are `1xx`, `3xx` and `4xx` errors. Unrecoverable errors are `5xx` errors.

```properties
http.errors.informational.recoverable=
http.errors.redirection.recoverable=
http.errors.client.recoverable=408,429
http.errors.server.unrecoverable=501,505,510
```

### SSL

The EbMS HTTP client has the option to use SSL client certificate from the matching CPA when sending a message. This option works **ONLY** as long as the receiving party will trust the SSL client certificate. You can override a certificate by creating a [Certificate Mapping]({{ site.baseurl }}/ebms-core/api.html#certificatemappingservice). This option can be enabled by setting property `https.useClientCertificate` to true.  

The EbMS adapter supports SSL client certificate validation. This means that the SSL clientCertificate of the incoming request will be validated against the matching CPA. This option **ONLY** works as long as the other parties use the SSL client certificates defined in the CPAs and the client certificates are trusted in the [truststore]({{ site.baseurl }}/ebms-core/properties.html#truststore). This option can be enabled by setting property `https.clientCertificateAuthentication` to true.

```properties
https.protocols=
https.cipherSuites=
https.verifyHostnames=true
https.clientCertificateAuthentication=false
https.useClientCertificate=false
```

### Forward Proxy

```properties
http.proxy.host=
http.proxy.port=0
http.proxy.nonProxyHosts=127.0.0.1,localhost
http.proxy.username=
http.proxy.password=
```

### EbMS Message Storage

If `deleteContentOnProcessed=true` then the attachments of a received message are deleted right after it has been processed and the attachments of a sent message are deleted right after it has been acknowledged (, failed or expired).  
If `ebmsMessage.storeDuplicateContent=false` then the attachments of a duplicate message are not stored. If `ebmsMessage.storeDuplicate=false` then the whole duplicate message is not stored.

```properties
ebmsMessage.deleteContentOnProcessed=false
ebmsMessage.storeDuplicate=true
ebmsMessage.storeDuplicateContent=true
```

### Overflow attachments to disk

Large EbMS attachments will be cached in a temporary files if they exceed the `ebmsMessage.attachment.memoryTreshold` which by default is `128Kb`. The temporary files are written to `ebmsMessage.attachment.outputDirectory` if set, otherwise to the default temp directory. To enable file encryption set `ebmsMessage.attachment.cipherTransformation` to a stream or 8-bit block cipher transformation (like RC4, AES/CTR/NoPadding, etc). Note: This will result in an increased processing time.

```properties
ebmsMessage.attachment.memoryTreshold=131072
ebmsMessage.attachment.outputDirectory=
ebmsMessage.attachment.cipherTransformation=
```

### Truststore

Holds all SSL, Signature and Encryption trust certificates.

```properties
# TruststoreType: JCEKS | JKS | DKS | PKCS11 | PKCS12
truststore.type=PKCS12
truststore.path=nl/clockwork/ebms/truststore.p12
truststore.password=password
```

### SSL Client keystore

Holds the SSL client keys (and related certificates) used by the EbMS HTTP client.

```properties
# KeystoreType: JCEKS | JKS | DKS | PKCS11 | PKCS12
client.keystore.type=PKCS12
client.keystore.path=nl/clockwork/ebms/keystore.p12
client.keystore.password=password
client.keystore.keyPassword=${client.keystore.password}
client.keystore.defaultAlias=
```

### EbMS Signature keystore

Holds the different signature keys (and related certificates) of the signature certificates defined in the different CPAs the EbMS adapter is using.

```properties
# KeystoreType: JCEKS | JKS | DKS | PKCS11 | PKCS12
signature.keystore.type=PKCS12
signature.keystore.path=nl/clockwork/ebms/keystore.p12
signature.keystore.password=password
signature.keystore.keyPassword=${signature.keystore.password}
```

### EbMS Encryption keystore

Holds the different encryption keys (and related certificates) of the encryption certificates defined in the different CPAs the EbMS adapter is using.

```properties
# KeystoreType: JCEKS | JKS | DKS | PKCS11 | PKCS12
encryption.keystore.type=PKCS12
encryption.keystore.path=nl/clockwork/ebms/keystore.p12
encryption.keystore.password=password
encryption.keystore.keyPassword=${encryption.keystore.password}
```

### JMS

You can find the default ActiveMQ configuration file [here](https://github.com/eluinstra/ebms-core/blob/ebms-core-{{ site.data.ebms.branch.version }}/src/main/resources/nl/clockwork/ebms/activemq.xml).
JMS can be used in the [DeliveryTaskHandler](#deliverytaskhandler), the [DeliveryManager](#deliverymanager) and the [EventListener](#eventlistener)

```properties
jms.broker.config=classpath:nl/clockwork/ebms/activemq.xml
jms.broker.username=
jms.broker.password=
jms.broker.start=false
jms.brokerURL=vm://localhost
jms.pool.minPoolSize=32
jms.pool.maxPoolSize=32
```

### Database

See [here]({{ site.baseurl }}{% link ebms-core/database.md %}) for the supported databases.

```properties
ebms.jdbc.driverClassName=org.hsqldb.jdbcDriver
ebms.jdbc.url=jdbc:hsqldb:mem:ebms
ebms.jdbc.username=sa
ebms.jdbc.password=
ebms.jdbc.update=false
ebms.jdbc.strict=false
```

### Database connection pool

Set `ebms.pool.minPoolSize` and `ebms.pool.maxPoolSize` to your needs. Do not set `ebms.pool.testQuery` if you are using a JDBC 4 compliant driver. The rest of the defaults should be fine.

```properties
ebms.pool.autoCommit=true
ebms.pool.connectionTimeout=30000
ebms.pool.maxIdleTime=600000
ebms.pool.maxLifetime=1800000
ebms.pool.testQuery=
ebms.pool.minPoolSize=16
ebms.pool.maxPoolSize=32
```
