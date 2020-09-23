---
sort: 5
---

# Properties

Below the [default properties](#default-properties) of ebms-admin. For the default properties of ebms-core see [here]({{ site.baseurl }}{% link ebms-core/properties.md %}).

## Override Properties

If you want to override properties create the file `ebms-admin.embedded.properties` in the [configDir]({{ site.baseurl }}/ebms-admin/command.html#configDir) and add the properties to that file.

You can also configure the basic properties at [EbMSAdminPropertiesPage](https://localhost:8080/wicket/bookmarkable/nl.clockwork.ebms.admin.web.configuration.EbMSAdminPropertiesPage) after you started ebms-admin. If you want to override advanced properties that are not included in the `ebms-admin.embedded.properties` file that is created, create the file `ebms-admin.embedded.advanced.properties` in the [configDir]({{ site.baseurl }}/ebms-admin/command.html#configDir) and add the advanced properties to that file.

You can also override properties by setting them as environment variables. You can for example override property `ebms.port` with value `80` as follows

```sh
export ebms_port=80
```

This is especially useful when configuring containers

## Default Properties

Below the contents of the [default.properties](https://github.com/eluinstra/ebms-core/blob/ebms-admin-{{ site.data.ebms.branch.version }}/src/main/resources/nl/clockwork/ebms/admin/default.properties) file of ebms-admin v{{ site.data.ebms.core.version }}. These are the default settings for ebms-admin.

### User Interface

```properties
maxItemsPerPage=20
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

### SSL

`https.protocols` and `https.cipherSuites` override [these]({{ site.baseurl }}/ebms-core/properties.html#ssl) default ebms-core properties. If `https.requireClientAuthentication=true` then the [EbMS Server](#ebms-server) endpoint requires SSL client authentication.

When SSL offloading is used and the EbMS adapter does not handle incoming SSL itself (see [reverse proxy example]({{ site.baseurl }}/ebms-admin/deployment.html#reverse-proxy)) and the EbMS adapter is using [SSL client certificate validation]({{ site.baseurl }}/ebms-core/properties.html#ssl), then the SSL client certificate can be forwarded as a Base64 DER-encoded HTTP header to the EbMS adapter. The header name can be set in `https.clientCertificateHeader` (since [v2.16.7]({{ site.baseurl }}/ebms-core/release.html#ebms-core-2167jar)).

```properties
https.protocols=TLSv1.2
https.cipherSuites=TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384,TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384
https.requireClientAuthentication=false
https.clientCertificateHeader=
```

### Server SSL keystore

Holds the SSL key (and related certificates) for the [EbMS Server](#ebms-server) endpoint.

```properties
# KeystoreType: JCEKS | JKS | DKS | PKCS11 | PKCS12
keystore.type=PKCS12
keystore.path=nl/clockwork/ebms/keystore.p12
keystore.password=password
keystore.defaultAlias=
```

### Database

These properties override [these]({{ site.baseurl }}/ebms-core/properties.html#database) default ebms-core properties.

```properties
ebms.jdbc.driverClassName=org.hsqldb.jdbcDriver
ebms.jdbc.url=jdbc:hsqldb:hsql://localhost:9001/ebms
ebms.jdbc.username=sa
ebms.jdbc.password=
```
