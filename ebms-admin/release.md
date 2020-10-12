---
sort: 9
---

# Release Notes

### [ebms-admin-2.18.0.jar](https://github.com/eluinstra/ebms-admin/releases/download/ebms-admin-2.18.0/ebms-admin-2.18.0.jar)

- upgrade to [ebms-core-2.18.0.jar]({{ site.baseurl }}/ebms-core/release.html#ebms-core-2180jar)
- added RemoteAddressMDCFilter

### [ebms-admin-2.17.5.jar](https://github.com/eluinstra/ebms-admin/releases/download/ebms-admin-2.17.5/ebms-admin-2.17.5.jar)

- upgrade to [ebms-core-2.17.5.jar]({{ site.baseurl }}/ebms-core/release.html#ebms-core-2175jar)
- added custom error handler

### ebms-admin-2.17.4.jar

- upgrade to [ebms-core-2.17.4.jar]({{ site.baseurl }}/ebms-core/release.html#ebms-core-2174jar)
- fixed DBClean

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
