===========================
= Start EbMS Admin Console
===========================
> java -jar ebms-admin-1.0.0.jar
> java -cp ebms-admin-1.0.0.jar nl.clockwork.ebms.admin.Main

> java -cp ebms-admin-1.0.0.jar nl.clockwork.ebms.admin.Start
> java -cp ebms-admin-1.0.0.jar nl.clockwork.ebms.admin.Start -h

> java -cp ebms-admin-1.0.0.jar nl.clockwork.ebms.admin.StartEmbedded
> java -cp ebms-admin-1.0.0.jar nl.clockwork.ebms.admin.StartEmbedded -hsqldb
> java -cp ebms-admin-1.0.0.jar nl.clockwork.ebms.admin.StartEmbedded -hsqldb -ebmsSsl

> java -cp ebms-admin-1.0.0.jar nl.clockwork.ebms.admin.StartEmbeddedX -hsqldb -ebmsSsl -ebmsSslCipherSuites TLS_DHE_RSA_WITH_AES_128_CBC_SHA,TLS_RSA_WITH_AES_128_CBC_SHA -ebmsClientAuth -ebmsKeystore classpath:keystore.jks -ebmsKeystorePassword password -ebmsTruststore classpath:keystore.jks -ebmsTruststorePassword password
> java -cp ebms-admin-1.0.0.jar nl.clockwork.ebms.admin.StartEmbeddedX -hsqldb -ebmsSsl -ebmsSslCipherSuites TLS_DHE_RSA_WITH_AES_128_CBC_SHA,TLS_RSA_WITH_AES_128_CBC_SHA -ebmsClientAuth -ebmsKeystore keystore.jks -ebmsKeystorePassword password -ebmsTruststore keystore.jks -ebmsTruststorePassword password
> java -cp ebms-admin-1.0.0.jar nl.clockwork.ebms.admin.StartEmbeddedX -hsqldb -ebmsSsl -ebmsSslCipherSuites TLS_DHE_RSA_WITH_AES_128_CBC_SHA,TLS_RSA_WITH_AES_128_CBC_SHA -ebmsClientAuth -ebmsKeystore f:/keystore.jks -ebmsKeystorePassword password -ebmsTruststore f:/keystore.jks -ebmsTruststorePassword password

================
= Build project
================
mvn package

==========
= Eclipse
==========
Import -> Existing Maven Projects

resolve js validation errors:
- Properties -> JavaScript -> Include Path -> Source
- Edit ebms-admin/src/main/webapp -> Excluded
- Add Exclusion Pattern: js/jquery-min.js
- Remove file js/jquery-min.js from project and add it again
OR
- Properties -> Builders
- Uncheck JavaScript Validator
- Remove file js/jquery-min.js from project and add it again
