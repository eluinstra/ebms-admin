---
sort: 5
---

# Default Properties

Below the [default properties](https://github.com/eluinstra/ebms-admin/blob/ebms-admin-2.17.x/src/main/resources/nl/clockwork/ebms/admin/default.properties) of ebms-admin. These properties can be overriden by ebms-admin.properties.
For the default properties of ebms-core see [Properties]({{ site.baseurl }}{% link ebms-core/properties.md %})

### User Interface
```
maxItemsPerPage=20
```

### Host
```
ebms.host=0.0.0.0
ebms.port=8888
ebms.path=/ebms
ebms.ssl=true
ebms.connectionLimit=
ebms.queriesPerSecond=
ebms.userQueriesPerSecond=
```
### SSL
```
https.protocols=TLSv1.2
https.cipherSuites=TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384,TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384
https.requireClientAuthentication=false
https.clientCertificateHeader=
```
### Server SSL keystore
```
keystore.type=PKCS12
keystore.path=nl/clockwork/ebms/keystore.p12
keystore.password=password
keystore.defaultAlias=
```
### Datastore
```
ebms.jdbc.driverClassName=org.hsqldb.jdbcDriver
ebms.jdbc.url=jdbc:hsqldb:hsql://localhost:9001/ebms
ebms.jdbc.username=sa
ebms.jdbc.password=
```
