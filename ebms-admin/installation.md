---
sort: 3
---

# Installation and Configuration

ebms-admin-2.16.x.jar and up needs jdk 8 and is compiled and tested with openjdk 8  
If you want to use a database other then hsqldb, you have to provide the jdbc driver yourself  

# IMPORTANT:
set property https.clientCertificateAuthentication to false, unless you know what you are doing!!!

Version 2.16.x supports SSL clientCertificate validation. This means that the SSL clientCertificate of the sending party will be validated against the matching CPA. This option works fine as long as the sending party uses the clientCertificate defined in the matching CPA when sending messages and the clientCertificate is trusted. This option can be enabled by setting property https.clientCertificateAuthentication to true.  
If the admin console does not handle incoming SSL itself, then the clientCertificate can be forwarded as a Base64 DER-encoded HTTP header to the admin console. The header name can be set from version 2.17.x in https.clientCertificateHeader.  
Version 2.17.x will also will also have the option to use SSL clientCertificate from the matching CPA when sending a message. This option works fine as long as the receiving party will trust the clientCertificate. This option can be enabled by setting property https.useClientCertificate to true.  
These properties van be edited in ebms-admin.embedded.properties if available and otherwise added to ebms-admin.embedded.advanced.properties.
