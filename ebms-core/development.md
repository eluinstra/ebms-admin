---
sort: 3
---

# Development

If you want to use ebms-core in your own application you have to add the [JAR](#maven) to your project. You also have to add the Spring configuration class [MainCondig.java](https://github.com/eluinstra/ebms-core/blob/ebms-core-{{ site.ebms.branch.version }}/src/main/java/nl/clockwork/ebms/MainConfig.java) to your project or replace it by your custom implementation. Finally you have to add the [EbMSServlet](https://github.com/eluinstra/ebms-core/blob/ebms-core-{{ site.ebms.branch.version }}/src/main/java/nl/clockwork/ebms/server/servlet/EbMSServlet.java) class to your web configuration to expose the EbMS Interface. Your application can manage the adapter through the [EbMS API]({{ site.baseurl }}{% link ebms-core/api.md %}). This API can also be exposed as SOAP Services.  

## Java

ebms-core is compiled and tested with OpenJDK 8

## Maven

ebms-core is released in the [Central Maven repository](https://mvnrepository.com/artifact/nl.clockwork.ebms/ebms-core/{{ site.ebms.core.version }}):

```xml
<dependency>
  <groupId>nl.clockwork.ebms</groupId>
  <artifactId>ebms-core</artifactId>
  <version>{{ site.ebms.core.version }}</version>
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

```sh
mvn clean verify
```

### Generate reports

```sh
mvn site
# or to generate individual reports:
mvn jacoco:report
mvn cobertura:cobertura
mvn org.owasp:dependency-check-maven:check
mvn checkstyle:checkstyle
mvn jdepend:generate
mvn jxr:jxr
mvn jxr:test-jxr
mvn pmd:pmd
mvn surefire:test
mvn com.github.spotbugs:spotbugs-maven-plugin:check
mvn com.github.spotbugs:spotbugs-maven-plugin:spotbugs
mvn taglist:taglist
```
