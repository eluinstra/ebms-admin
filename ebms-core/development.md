---
sort: 3
---

# Development

## Java

ebms-core is compiled and tested with OpenJDK 8

## Maven

ebms-core is released in the [Central Maven repository](https://mvnrepository.com/artifact/nl.clockwork.ebms/ebms-core/{{ site.data.ebms.core.version }}):

```xml
<dependency>
  <groupId>nl.clockwork.ebms</groupId>
  <artifactId>ebms-core</artifactId>
  <version>{{ site.data.ebms.core.version }}</version>
</dependency>
```

### Configure

ebms-core includes Oracle ojdbc8 driver. If you don't use an Oracle database then you can exclude the driver. Otherwise add the following `<server>` element to the `<servers>` section of the Maven ~/.m2/settings.xml to add the Oracle maven repository:

```xml
 <server>
    <id>maven.oracle.com</id>
    <username>username</username>
    <password>password</password>
    <configuration>
      <basicAuthScope>
        <host>ANY</host>
        <port>ANY</port>
        <realm>OAM 11g</realm>
      </basicAuthScope>
      <httpConfiguration>
        <all>
          <params>
            <property>
              <name>http.protocol.allow-circular-redirects</name>
              <value>%b,true</value>
            </property>
          </params>
        </all>
      </httpConfiguration>
    </configuration>
  </server>
```

Replace the `<username>` and `<password>` entries with your OTN username and password.

### Sources

You can find the sources [here](https://sourceforge.net/p/muleebmsadapter/code/ci/master/tree/) and [here](https://github.com/eluinstra/ebms-core)

### Build

```sh
mvn clean package
```

### Testing

The ebms-core project contains a couple of tests for signing, encryption and the `DAOFactory`. You can test the EbMS functionality through a using ebms-admin as follows

- download and install Java 8
- download and install SoapUI 5.x.x
- download [ebms-admin-{{ site.data.ebms.core.version }}.jar](https://github.com/eluinstra/ebms-admin/releases/download/ebms-admin-{{ site.data.ebms.core.version }}/ebms-admin-{{ site.data.ebms.core.version }}.jar)
- download the override properties file [ebms-admin.embedded.properties](https://github.com/eluinstra/ebms-core/blob/ebms-core-2.17.x/resources/test/ebms-admin.embedded.properties)
- download the SoapUI project [EbMS-soapui-project.xml](https://github.com/eluinstra/ebms-core/blob/ebms-core-2.17.x/resources/test/EbMS-soapui-project.xml)
- create directory `test` and copy `ebms-admin-{{ site.data.ebms.core.version }}.jar` and `ebms-admin.embedded.properties` to `test`
- move to directory `test`
- start ebms-admin
```sh
java -Djavax.net.ssl.trustStore= -Debms.jdbc.update=true -cp ebms-admin-2.17.3.jar nl.clockwork.ebms.admin.StartEmbedded -hsqldb -soap
```
- open SoapUI and load `EbMS-soapui-project.xml`
- run the testsuite

### Generate reports

```sh
mvn site
# or to generate individual reports:
mvn surefire:test
mvn jxr:jxr
mvn jxr:test-jxr
mvn checkstyle:checkstyle
mvn com.github.spotbugs:spotbugs-maven-plugin:gui
mvn pmd:pmd
mvn jdepend:generate
mvn cobertura:cobertura
mvn org.owasp:dependency-check-maven:check
```

## Eclipse

- install https://marketplace.eclipse.org/content/m2e-apt
- install lombok (since [v2.17.0]({{ site.baseurl }}/ebms-core/release.html#ebms-core-2170jar))
