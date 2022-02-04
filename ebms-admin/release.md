---
sort: 10
---

# Release Notes

```note
Due to the CVE's found in log4j these versions 2.16.8.2, 2.17.10.2 and 2.18.4.2 have been updated to include log4j 2.17.0.
```

```note
ebms-admin-2.18.5, ebms-admin-2.17.10.2 and ebms-admin-2.16.8.2 are the latest versions.
```

### [ebms-admin-2.18.5.jar](https://github.com/eluinstra/ebms-admin/releases/download/ebms-admin.2.18.5/ebms-admin-2.18.5.jar)

- updated dependencies
- added allow CORS all
- fixed issue: not hiding password In about screen
- upgrade to [ebms-core-2.18.5.jar]({{ site.baseurl }}/ebms-core/release.html#ebms-core-2185jar)

### [ebms-admin-2.18.4.2.jar](https://github.com/eluinstra/ebms-admin/releases/download/ebms-admin.2.18.4.2/ebms-admin-2.18.4.2.jar)

- updated log4j dependency to 2.17.0
- updated spring, cxf dependencies

### [ebms-admin-2.18.4.1.jar](https://github.com/eluinstra/ebms-admin/releases/download/ebms-admin.2.18.4.1/ebms-admin-2.18.4.1.jar)

- updated log4j dependency to 2.16.0
- modified default log4j2.xml to format the message as %m{nolookups}

### [ebms-admin-2.18.4.jar](https://github.com/eluinstra/ebms-admin/releases/download/ebms-admin.2.18.4/ebms-admin-2.18.4.jar)

- DBClean implementation changed based on pull request by Lost Lemon
- updated various dependencies [83d30c9](https://github.com/eluinstra/ebms-admin/commit/83d30c9116f6608d77c0ee1f60606712807615fb) 
- upgrade to [ebms-core-2.18.4.jar]({{ site.baseurl }}/ebms-core/release.html#ebms-core-2184jar)

### [ebms-admin-2.18.3.jar](https://github.com/eluinstra/ebms-admin/releases/download/ebms-admin.2.18.3/ebms-admin-2.18.3.jar)

- DBClean implementation changed based on pull request by Lost Lemon
- REST service implementation added, more information [here]({{ site.baseurl }}/ebms-admin/rest.html)
- upgrade to [ebms-core-2.18.3.jar]({{ site.baseurl }}/ebms-core/release.html#ebms-core-2183jar)

### [ebms-admin-2.18.2.jar](https://github.com/eluinstra/ebms-admin/releases/download/ebms-admin.2.18.2/ebms-admin-2.18.2.jar)

- fixed DBClean for MSSQL and MySQL, deleting attachments
- added Apache Kafka integration for delivery events
- added Microsoft Azure Application Insights and Microsoft Azure Key Vault integration
- upgrade to [ebms-core-2.18.2.jar]({{ site.baseurl }}/ebms-core/release.html#ebms-core-2182jar)

### [ebms-admin-2.18.1.jar](https://github.com/eluinstra/ebms-admin/releases/download/ebms-admin.2.18.1/ebms-admin-2.18.1.jar)

- dependency and plugin version upgrades
- various improvements and bugfixes
- upgrade to [ebms-core-2.18.1.jar]({{ site.baseurl }}/ebms-core/release.html#ebms-core-2181jar)

### [ebms-admin-2.18.0.jar](https://github.com/eluinstra/ebms-admin/releases/download/ebms-admin-2.18.0/ebms-admin-2.18.0.jar)

- upgrade to [ebms-core-2.18.0.jar]({{ site.baseurl }}/ebms-core/release.html#ebms-core-2180jar)

### [ebms-admin-2.17.10.2.jar](https://github.com/eluinstra/ebms-admin/releases/download/ebms-admin.2.17.10.2/ebms-admin-2.17.10.2.jar)

- updated log4j dependency to 2.17.0
- updated spring, cxf dependencies

### [ebms-admin-2.17.10.1.jar](https://github.com/eluinstra/ebms-admin/releases/download/ebms-admin.2.17.10.1/ebms-admin-2.17.10.1.jar)

- updated log4j dependency to 2.16.0
- modified default log4j2.xml to format the message as %m{nolookups}

### [ebms-admin-2.17.10.jar](https://github.com/eluinstra/ebms-admin/releases/download/ebms-admin.2.17.10/ebms-admin-2.17.10.jar)

- updated log4j dependency to 2.15.0
- updated various dependencies
- upgrade to [ebms-core-2.17.10.jar]({{ site.baseurl }}/ebms-core/release.html#ebms-core-21710jar)

### [ebms-admin-2.17.9.jar](https://github.com/eluinstra/ebms-admin/releases/download/ebms-admin.2.17.9/ebms-admin-2.17.9.jar)

- removed @NonNull for name in EbMSDataSource service model
- upgrade to [ebms-core-2.17.9.jar]({{ site.baseurl }}/ebms-core/release.html#ebms-core-2179jar)

### [ebms-admin-2.17.8.jar](https://github.com/eluinstra/ebms-admin/releases/download/ebms-admin.2.17.8/ebms-admin-2.17.8.jar)

- fixed DBClean for MSSQL and MySQL, deleting attachments
- upgrade to [ebms-core-2.17.8.jar]({{ site.baseurl }}/ebms-core/release.html#ebms-core-2178jar)

### [ebms-admin-2.17.7.jar](https://github.com/eluinstra/ebms-admin/releases/download/ebms-admin-2.17.7/ebms-admin-2.17.7.jar)

- upgrade to [ebms-core-2.17.7.jar]({{ site.baseurl }}/ebms-core/release.html#ebms-core-2177jar)
- changed the starting of the embedded hsqldb
- maven plugin version upgrades
- dependency version upgrades, almost all dependencies updated to latest version which still runs on java 1.8
- bumped the end-dates of the test CPA's to 2022
- added Microsoft Azure Application Insights and Microsoft Azure Key Vault integration
- fixed zip issues
- fixed swallowing errors at server start (server thread)
- upgraded jetty version

### [ebms-admin-2.17.6.jar](https://github.com/eluinstra/ebms-admin/releases/download/ebms-admin-2.17.6/ebms-admin-2.17.6.jar)

- upgrade to [ebms-core-2.17.6.jar]({{ site.baseurl }}/ebms-core/release.html#ebms-core-2176jar)
- added `RemoteAddressMDCFilter`

### [ebms-admin-2.17.5.jar](https://github.com/eluinstra/ebms-admin/releases/download/ebms-admin-2.17.5/ebms-admin-2.17.5.jar)

- upgrade to [ebms-core-2.17.5.jar]({{ site.baseurl }}/ebms-core/release.html#ebms-core-2175jar)
- added custom error handler

### ebms-admin-2.17.4.jar

- upgrade to [ebms-core-2.17.4.jar]({{ site.baseurl }}/ebms-core/release.html#ebms-core-2174jar)
- fixed `DBClean`

### ebms-admin-2.17.3.jar

- upgrade to [ebms-core-2.17.3.jar]({{ site.baseurl }}/ebms-core/release.html#ebms-core-2173jar)

### ebms-admin-2.17.2.jar

- upgrade to [ebms-core-2.17.2.jar]({{ site.baseurl }}/ebms-core/release.html#ebms-core-2172jar)
- improved properties configuration
- moved Flyway database migration to ebms-core
- changed commandline arguments
	- removed db migration arguments `updateDb` and `updateStrict`
	- renamed rate limiter argument `requestsPerSecond` to `queriesPerSecond`
	- added user rate limiter argument `userQueriesPerSecond`

### ebms-admin-2.17.1.1.jar

- fixed `updateDb` and `updateStrict` options	

### ebms-admin-2.17.1.jar

- upgrade to [ebms-core-2.17.1.jar]({{ site.baseurl }}/ebms-core/release.html#ebms-core-2171jar)
- improved logging
- removed Jetty Server response header
- added Flyway database migration
- added rate limiter
- changed commandline arguments
	- added db migration arguments `updateDb` and `updateStrict`
	- added rate limiter argument `requestsPerSecond`

### ebms-admin-2.17.0.1.jar

- changed default settings
- changed configuration of the test scenario
- updated CPA endpoints

### ebms-admin-2.17.0.jar

- upgrade to [ebms-core-2.17.0.jar]({{ site.baseurl }}/ebms-core/release.html#ebms-core-2170jar)
- added options to enable high availability and horizontal scaling (and throttling)
- changed command line arguments
	- renamed `propertiesFilesDir` to `configDir`
	- removed property `log4j.file` (use `-Dlog4j.configurationFile=log4j2.xml` instead)
	- added SSL arguments `protocols` and `cipherSuites`
	- added JMX arguments
	- added arguments `disableEbMSClient` and `disableEbMSServer`
- split up `CPAService` into `CPAService` and `URLMapper`
- added new SOAP service `CertificateMapper`
- updated EbMS Admin Properties Page
- removed EbMS Core Properties Page
- added database java command line tools
	- `DBMigrate` (`java -cp ebms-admin-2.17.0.jar nl.clockwork.ebms.admin.DBMigrate -h`)
	- `DBClean` (`java -cp ebms-admin-2.17.0.jar nl.clockwork.ebms.admin.DBClean -h`)


### [ebms-admin-2.16.8.2.jar](https://github.com/eluinstra/ebms-admin/releases/download/ebms-admin.2.16.8.2/ebms-admin-2.16.8.2.jar)

- updated log4j dependency to 2.17.0
- updated spring, cxf, activemq dependencies

### [ebms-admin-2.16.8.1.jar](https://github.com/eluinstra/ebms-admin/releases/download/ebms-admin.2.16.8.1/ebms-admin-2.16.8.1.jar)

- updated log4j dependency to 2.16.0
- modified default log4j2.xml to format the message as %m{nolookups}

### [ebms-admin-2.16.8.jar](https://github.com/eluinstra/ebms-admin/releases/download/ebms-admin-2.16.8/ebms-admin-2.16.8.jar)

- upgrade to [ebms-core-2.16.8.jar]({{ site.baseurl }}/ebms-core/release.html#ebms-core-2168jar)
- added custom error handler

### ebms-admin-2.16.7.jar

- upgrade to [ebms-core-2.16.7.jar]({{ site.baseurl }}/ebms-core/release.html#ebms-core-2167jar)

### ebms-admin-2.16.6.jar

- upgrade to [ebms-core-2.16.6.jar]({{ site.baseurl }}/ebms-core/release.html#ebms-core-2166jar)
- fixed CXF logging

### ebms-admin-2.16.5.jar

- upgrade to [ebms-core-2.16.5.jar]({{ site.baseurl }}/ebms-core/release.html#ebms-core-2165jar)

### ebms-admin-2.16.4.jar

- upgrade to [ebms-core-2.16.4.jar]({{ site.baseurl }}/ebms-core/release.html#ebms-core-2164jar)
- added new MTOM EbMS SOAP service endpoint on /service/ebmsMTOM

### ebms-admin-2.16.3.jar

- upgrade to [ebms-core-2.16.3.jar]({{ site.baseurl }}/ebms-core/release.html#ebms-core-2163jar)
- minor configuration improvements

### ebms-admin-2.16.2a.jar

- fixed reading the encryption keystore property which caused an error opening the Configuration Page after the encryption keystore was set
- minor layout improvements

### ebms-admin-2.16.2.jar

- upgrade to [ebms-core-2.16.2.jar]({{ site.baseurl }}/ebms-core/release.html#ebms-core-2162jar)
- fixed default value of `log4j.file` property which caused a startup error when ebms-admin-2.16.1.jar was fresh installed 

### ebms-admin-2.16.1.jar

- upgrade to [ebms-core-2.16.1.jar]({{ site.baseurl }}/ebms-core/release.html#ebms-core-2161jar)
- upgraded to Java 8
- HTTPS support for Web and SOAP interfaces (including client certificate authentication)
- upgraded a lot of libraries (included wicked-charts-wicket8)
- added the option to use PKCS12 keystores and truststores
- removed all previously included JDBC drivers from the release
- minor improvements
