---
sidebar_position: 4
---

# Properties

Below the [default properties](#default-properties) of ebms-core.


## Default Properties

Below the contents of ebms-core's [default.properties](https://github.com/eluinstra/ebms-core/blob/ebms-core-@ebms.branch.version@/src/main/resources/nl/clockwork/ebms/default.properties) file. These are the default settings for ebms-core.

## Override Properties

### Cache

The default cache type is `DEFAULT` which is a simple in-memory cache. When you are [scaling](/ebms-admin/deployment.md#scaling) the EbMS Adapter, you should set the cache type to `EHCACHE` or `HAZELCAST`. You cannot disable caching anymore. When you are [scaling](/ebms-admin/deployment.md#scaling) the EbMS Adapter, you should configure the [Ehcache](cache.md#ehcache) or the [Hazelcast](cache.md#hazelcast) plugin.

```properties
# CacheType: DEFAULT
cache.type=DEFAULT
```

### Database

See [here](database) for the supported databases.

```properties
ebms.jdbc.driverClassName=org.hsqldb.jdbcDriver
ebms.jdbc.url=jdbc:hsqldb:mem:ebms
ebms.jdbc.username=sa
ebms.jdbc.password=
ebms.jdbc.update=false
ebms.jdbc.strict=false
```

### Database Connection Pool

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

### DeliveryManager

The DeliveryManager is used to handle EbMS [Ping](api#pingcpaid-frompartyid-topartyid) and [getMessageStatus](api#getmessagestatusmessageid) calls. Set `deliveryManager.type` to `JMS` when you are [scaling](/ebms-admin/deployment.md#scaling) the EbMS Adapter, otherwise leave it set to `DEFAULT`. When `deliveryManager.type=JMS` add the [JMS Messaging Plugin](#jms-messaging-plugin) to the classpath and configure [JMS](#jms).

```properties
# DeliveryManagerType: DEFAULT (DAO) | JMS (requires the JMS messaging plugin)
deliveryManager.type=DEFAULT
deliveryManager.minThreads=2
deliveryManager.maxThreads=8
messageQueue.maxEntries=64
messageQueue.timeout=30000
```

### DeliveryTaskHandler

The DeliveryTaskHandler is used to [send EbMS Messages](api#sendmessagemessage) asynchronously. By default the `DEFAULT` (DAO) executor is used; it is gated by [Raft leader election](#raft) so a single elected leader processes delivery tasks at any given moment, allowing horizontal scaling without external coordinators. The `JMS` variant is also available when the [JMS Messaging Plugin](#jms-messaging-plugin) is on the classpath.

```properties
# DeliveryTaskHandlerType: DEFAULT (DAO, Raft-leader-gated) | JMS (requires the JMS messaging plugin)
deliveryTaskHandler.start=true
deliveryTaskHandler.type=DEFAULT
deliveryTaskHandler.minThreads=16
deliveryTaskHandler.maxThreads=16
deliveryTaskHandler.default.maxTasks=100
deliveryTaskHandler.default.executionInterval=1000
deliveryTaskHandler.default.leaderCheckIntervalMillis=1000
deliveryTaskHandler.default.taskAwaitTimeoutMillis=60000
deliveryTaskHandler.task.executionInterval=0
```

### Raft

Delivery-task execution is coordinated through [jgroups-raft](https://github.com/belaban/jgroups-raft). Each node joins the cluster identified by `raft.clusterName` using the JGroups stack defined in `raft.configLocation` (the bundled `ebms-raft.xml` uses TCP+TCPPING). The default stack runs a single-node cluster that self-elects as leader, so stand-alone deployments work out of the box; override the JGroups system properties (e.g. `jgroups.raft.id`, `jgroups.raft.members`, `jgroups.tcpping.initial_hosts`) to scale to multiple nodes.

```properties
raft.configLocation=ebms-raft.xml
raft.clusterName=ebms-cluster
```

### DeliveryTaskManager

It is possible to retry sending best-effort messages after a technical error (like a connection error). `ebmsMessage.nrAutoRetries` sets the maximum number of retries. `ebmsMessage.autoRetryInterval` sets the retry interval in minutes.

:::note
This is not according to the EbMS Specifications, but will not violate them either.
:::

```properties
deliveryTaskManager.nrAutoRetries=0
deliveryTaskManager.autoRetryInterval=5
```

### EbMS Core

You have to set `ebms.serverId` with a unique serverId per server when you are [scaling with serverId](/ebms-admin/deployment.md#scaling-with-serverid).

```properties
ebms.serverId=
```

### EbMS Message Storage

If `deleteContentOnProcessed=true` then the attachments of a received message are deleted right after it has been processed and the attachments of a sent message are deleted right after it has been acknowledged (failed or expired).  
If `ebmsMessage.storeDuplicateContent=false` then the attachments of a duplicate message are not stored. If `ebmsMessage.storeDuplicate=false` then the whole duplicate message is not stored.

```properties
ebmsMessage.deleteContentOnProcessed=false
ebmsMessage.storeDuplicate=true
ebmsMessage.storeDuplicateContent=true
```

### Encryption keystore

Holds the different encryption keys (and related certificates) of the encryption certificates defined in the different CPAs the EbMS adapter is using.

```properties
# KeystoreType: JCEKS | JKS | DKS | PKCS11 | PKCS12
encryption.keystore.type=PKCS12
encryption.keystore.path=nl/clockwork/ebms/keystore.p12
encryption.keystore.password=password
encryption.keystore.keyPassword=${encryption.keystore.password}
```

### EventListener

When receiving a message a `RECEIVE` event is generated. After a message is sent, a `DELIVERED`, `FAILED` or `EXPIRED` event is generated. By `DEFAULT` these events are logged to file, but it is also possible to persist and consume these events. For that you can choose from the EventListenerTypes

- `DAO` which stores it to database
- `SIMPLE_JMS` which stores the messageId to JMS
- `JMS` which stores all message properties to JMS
- `JMS_TEXT` which stores all message properties to JMS as a text message

When `DAO` is selected, you can get the events by calling [getUnProcessedEvents](api#getunprocessedmessageeventsmessagefilter-eventtypes-maxnr). When one of the JMS listeners is selected, you can get the events by listening to a `QUEUE` or `TOPIC` depending on the `destinationType` — these listeners require the [JMS Messaging Plugin](#jms-messaging-plugin) on the classpath and you then also have to configure [JMS](#jms). Events can be filtered by providing a comma separated list of events to be filtered out in `eventListener.filter`.

```properties
# EventListenerType: DEFAULT (LOGGING) | DAO | SIMPLE_JMS | JMS | JMS_TEXT (SIMPLE_JMS, JMS and JMS_TEXT require the JMS messaging plugin)
eventListener.type=DEFAULT
eventListener.filter=
```

### Forward Proxy

```properties
http.proxy.host=
http.proxy.port=0
http.proxy.nonProxyHosts=127.0.0.1,localhost
http.proxy.username=
http.proxy.password=
```

### HTTPClient

`http.uuid.headerName` is used to specify the name of the header in which a generated UUID is added to the outgoing EbMS HTTP request. This can be used for logging, debugging and tracing purposes.

```properties
http.connectTimeout=30000
http.readTimeout=30000
http.uuid.headerName=
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

### JMS Messaging Plugin

JMS support (broker, `ConnectionFactory`, `JMS` delivery manager, `JMS` delivery-task dispatcher, JMS message-event listeners) ships as a separate Maven artifact `nl.clockwork.ebms.plugin.messaging:ebms-jms-messaging-plugin`. Add the plugin jar to the classpath — it is auto-discovered through `META-INF/services/nl.clockwork.ebms.PluginProvider` — when you need any `*.type=JMS` (or `SIMPLE_JMS`/`JMS_TEXT`) configuration. The webapp distribution already bundles it.

The plugin contributes the properties below. By default the `jms.brokerURL=vm://localhost` setting starts a persistent ActiveMQ broker that stores its data in the folder `activemq-data` and is reachable through `vm://localhost`. To use a different (external) ActiveMQ broker configure `jms.brokerURL`. You can also start an internal ActiveMQ broker by setting `jms.broker.start=true` and set the path to the broker's configuration file in `jms.broker.config`. The default ActiveMQ configuration ships at `nl/clockwork/ebms/plugin/messaging/jms/activemq.xml` in the plugin jar; if you are using this configuration file, the broker's data is stored in the folder `data`.

:::info
When [`eventListener.type`](#eventlistener) is set to `SIMPLE_JMS`, `JMS`, or `JMS_TEXT` use (the default) persistent delivery.
:::

```properties
# Broker / ConnectionFactory
jms.brokerURL=vm://localhost
jms.broker.start=false
jms.broker.config=classpath:nl/clockwork/ebms/plugin/messaging/jms/activemq.xml
jms.broker.username=
jms.broker.password=
jms.pool.minPoolSize=32
jms.pool.maxPoolSize=32

# DeliveryTaskHandler JMS variant
deliveryTaskHandler.jms.destinationName=DELIVERY_TASK
deliveryTaskHandler.jms.receiveTimeout=3000
deliveryTaskHandler.jms.concurrentConsumers=1
deliveryTaskHandler.jms.maxConcurrentConsumers=8

# EventListener JMS variants
# DestinationType: QUEUE | TOPIC
eventListener.jms.destinationType=QUEUE
```

### Overflow attachments to disk

Large EbMS attachments will be cached in temporary files if they exceed the `ebmsMessage.attachment.memoryTreshold` which by default is `128Kb`. The temporary files are written to `ebmsMessage.attachment.outputDirectory` if set, otherwise to the default temp directory. To enable file encryption set `ebmsMessage.attachment.cipherTransformation` to a stream or 8-bit block cipher transformation (like RC4, AES/CTR/NoPadding, etc).

:::caution
Enabling file encryption will result in an increased processing time.
:::

```properties
ebmsMessage.attachment.memoryTreshold=131072
ebmsMessage.attachment.outputDirectory=
ebmsMessage.attachment.cipherTransformation=
```

### Signature keystore

Holds the different signature keys (and related certificates) of the signature certificates defined in the different CPAs the EbMS adapter is using.

```properties
# KeystoreType: JCEKS | JKS | DKS | PKCS11 | PKCS12
signature.keystore.type=PKCS12
signature.keystore.path=nl/clockwork/ebms/keystore.p12
signature.keystore.password=password
signature.keystore.keyPassword=${signature.keystore.password}
```

### SSL

The EbMS HTTP client has the option to use SSL client certificate from the matching CPA when sending a message. You can **override** a certificate defined in a CPA by creating a [Certificate Mapping](api#certificatemappingservice). This option can be enabled by setting property `https.useClientCertificate` to true.  

:::caution
This option works **ONLY** as long as the other (receiving) party will trust the configured SSL client certificate.
:::

The EbMS adapter supports SSL client certificate validation. This means that the SSL clientCertificate of the incoming request will be validated against the matching CPA. This option can be enabled by setting property `https.clientCertificateAuthentication` to true.

:::caution
This option **ONLY** works as long as the other (sending) party uses the SSL client certificates defined in the CPA and the client certificates are trusted in the [truststore](properties#truststore).
:::

```properties
https.protocols=
https.cipherSuites=
https.verifyHostnames=true
https.clientCertificateAuthentication=false
https.useClientCertificate=false
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

### TransactionManager

The EbMS adapter uses a single non-XA `DataSourceTransactionManager`. Configure the JDBC isolation level when required by your database.

```properties
# IsolationLevel: <EMPTY> | TRANSACTION_NONE | TRANSACTION_READ_UNCOMMITTED | TRANSACTION_READ_COMMITTED | TRANSACTION_REPEATABLE_READ | TRANSACTION_SERIALIZABLE | TRANSACTION_SQL_SERVER_SNAPSHOT_ISOLATION_LEVEL
transactionManager.isolationLevel=
```

### Truststore

Holds all trusted SSL, Signature and Encryption certificates.

```properties
# TruststoreType: JCEKS | JKS | DKS | PKCS11 | PKCS12
truststore.type=PKCS12
truststore.path=nl/clockwork/ebms/truststore.p12
truststore.password=password
```
