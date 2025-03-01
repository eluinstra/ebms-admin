---
sidebar_position: 5
---

# Properties

Below the [default properties](#default-properties) of ebms-admin. For the default properties of ebms-core see [here](/ebms-core/properties.md).

## Override Properties

To override the default properties create the file `ebms-admin.embedded.properties` in the [configDir](command#start-with-config-directory-conf) and add the properties to that file.

You can also configure the basic properties at [EbMSAdminPropertiesPage](https://localhost:8080/wicket/bookmarkable/nl.clockwork.ebms.admin.web.configuration.EbMSAdminPropertiesPage) after you started ebms-admin. If you want to override 'advanced' properties that are not included in the `ebms-admin.embedded.properties` file that is created, create the file `ebms-admin.embedded.advanced.properties` in the [configDir](command#start-with-config-directory-conf) and add the 'advanced' properties to that file.

You can also override properties by setting them as environment variables. You can for example override property `ebms.port` with value `80` as follows

```sh
export ebms_port=80
```

This is especially useful when configuring containers

## Basic Properties

- [Database](#database)
- [EbMS Server](#ebms-server)
- [SSL](#ssl)
- [SSL Server keystore](#ssl-server-keystore)
- [SSL Client Keystore](/ebms-core/properties.md#ssl-client-keystore)
- [Truststore](/ebms-core/properties.md#truststore)
- [Forward Proxy](/ebms-core/properties.md#forward-proxy)
- [Overflow attachments to disk](/ebms-core/properties.md#overflow-attachments-to-disk)
- [EbMS Signature keystore](/ebms-core/properties.md#signature-keystore)
- [EbMS Encryption keystore](/ebms-core/properties.md#encryption-keystore)
- [EbMS Message Storage](/ebms-core/properties.md#ebms-message-storage)
- [EbMS EventListener](/ebms-core/properties.md#eventlistener)

## Default Properties

Below the contents of ebms-admin's [default.properties](https://github.com/eluinstrablob/ebms-admin-@ebms.branch.version@/src/main/resources/nl/clockwork/ebms/admin/default.properties) file. These are the default settings for ebms-admin.

### Database

These properties override [these](/ebms-core/properties.md#database) default ebms-core properties.

```properties
ebms.jdbc.driverClassName=org.hsqldb.jdbcDriver
ebms.jdbc.url=jdbc:hsqldb:hsql://localhost:9001/ebms
ebms.jdbc.username=sa
ebms.jdbc.password=
```

### EbMS Server

Properties for the EbMS Server endpoint used to connect to another EbMS adapter.

```properties
ebms.host=0.0.0.0
ebms.port=8888
ebms.path=/ebms
ebms.ssl=true
ebms.connectionLimit=
ebms.queriesPerSecond=
ebms.userQueriesPerSecond=
```

### SSL Server keystore

Holds the SSL key (and related certificates) for the [EbMS Server](#ebms-server) endpoint.

```properties
# KeystoreType: JCEKS | JKS | DKS | PKCS11 | PKCS12
keystore.type=PKCS12
keystore.path=nl/clockwork/ebms/keystore.p12
keystore.password=password
keystore.defaultAlias=
```

### SSL

`https.protocols` and `https.cipherSuites` override [these](/ebms-core/properties.md#ssl) default ebms-core properties. If `https.requireClientAuthentication=true` then the [EbMS Server](#ebms-server) endpoint requires SSL client authentication.

When SSL offloading is used and the EbMS adapter does not handle incoming SSL itself (see [reverse proxy example](deployment#behind-a-reverse-proxy)) and the EbMS adapter is using [SSL client certificate validation](/ebms-core/properties.md#ssl), then the SSL client certificate can be forwarded as a Base64 DER-encoded HTTP header to the EbMS adapter. The header name can be set in `https.clientCertificateHeader`.

```properties
https.protocols=TLSv1.2
https.cipherSuites=TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384,TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384
https.requireClientAuthentication=false
https.clientCertificateHeader=
```

### User Interface

```properties
maxItemsPerPage=20
```
