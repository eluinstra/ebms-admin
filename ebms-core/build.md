---
sort: 3
---

# Build

ebms-core-2.16.0.jar and up needs jdk 8 and is compiled and tested with openjdk 8  
ebms-core version 2.16.0 and up are released in the Central Maven repository:
```
<dependency>
  <groupId>nl.clockwork.ebms</groupId>
  <artifactId>ebms-core</artifactId>
  <version>2.17.3</version>
</dependency>
```
The Maven settings.xml requires additional settings to support the Oracle Maven Repository. Add the following \<server> element to the \<servers> section of the Maven settings.xml:
```
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
Replace the \<username> and \<password> entries with your OTN user name and password.

mvn package

Eclipse:
- install https://marketplace.eclipse.org/content/m2e-apt
- install lombok
