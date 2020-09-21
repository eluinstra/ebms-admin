---
sort: 5
---

# Default Properties

Below the [default properties](https://github.com/eluinstra/ebms-admin/blob/ebms-admin-2.17.x/src/main/resources/nl/clockwork/ebms/admin/default.properties) of ebms-admin.
For the default properties of ebms-core see [here]({{ site.baseurl }}{% link ebms-core/properties.md %}).

If you want to override properties create the file `ebms-admin.embedded.properties` in the [configDir]({{ site.baseurl }}/ebms-admin/commands.html#configdir) and add the properties to that file.  

You can also configure the basic properties at [EbMSAdminPropertiesPage](https://localhost:8080/wicket/bookmarkable/nl.clockwork.ebms.admin.web.configuration.EbMSAdminPropertiesPage).
If you want to override 'advanced' properties from the default.properties file that are not included in the `ebms-admin.embedded.properties` file, then create the file `ebms-admin.embedded.advanced.properties` in the [configDir]({{ site.baseurl }}/ebms-admin/commands.html#configdir) and add the advanced properties to that file.


### User Interface
```
maxItemsPerPage=20
```

### EbMS Server
Properties for the EbMS server endpoint used to connect to another EbMS adapter.
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
`https.protocols` and `https.cipherSuites` override [these]({{ site.baseurl }}/ebms-core/properties.html#ssl) default ebms-core properties.
If `https.requireClientAuthentication=true` then the [EbMS Server](#ebms-server) endpoint requires SSL client authentication.
```
https.protocols=TLSv1.2
https.cipherSuites=TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384,TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384
https.requireClientAuthentication=false
https.clientCertificateHeader=
```
### Server SSL keystore
Keystore for the [EbMS Server](#ebms-server) endpoint.
```
keystore.type=PKCS12
keystore.path=nl/clockwork/ebms/keystore.p12
keystore.password=password
keystore.defaultAlias=
```
### Datastore
These properties override [these]({{ site.baseurl }}/ebms-core/properties.html#datastore) default ebms-core properties.
```
ebms.jdbc.driverClassName=org.hsqldb.jdbcDriver
ebms.jdbc.url=jdbc:hsqldb:hsql://localhost:9001/ebms
ebms.jdbc.username=sa
ebms.jdbc.password=
```
