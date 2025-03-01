---
sidebar_position: 1
---

# Introduction

ebms-admin is a standalone EbMS adapter that uses an embedded Jetty server. ebms-admin exposes a Web and a SOAP interface over HTTP(S) to manage the EbMS adapter and it uses a database to store the data. ebms-admin is configured through properties and command line options. It also supports basic and client certificate authentication for the Web, REST and SOAP interfaces.

- See [here](installation.md) for installation and configuration
- See [here](migration.md) for the migration guide
- See [here](deployment.md) for different deployment scenarios
- See [here](/ebms-core/introduction.md) for the embs-core library

## Short introduction to EbMS

EbMS supports secure and reliable messaging. Secure messaging can be established by communicating over SSL and by signing and encrypting messages. Reliable messaging can be established by configuring the retry mechanism. All of this is defined in the CPA.

A CPA is the contract between 2 parties and it defines the cpaId, actions, endpoint URLs and SSL, Signing and Encryption certificates between usually 2 endpoints of the 2 parties. Each CPA is uniquelly identified by a cpaId. The CPA defines the actions that both parties can send and receive. Normally the EbMS adapter communicates over SSL. EbMS Messages are usually signed to verify the originator. Sometimes they are also encrypted. The SSL, Signing and Encryption certificates are also defined in the CPA. Also the endpoint URLs which are used to communicate between the 2 parties are defined in the CPA. So you need different CPAs for your Development, Acceptance and Production environments. In practice the same certificate is used for SSL, Signing and Encryption in a single CPA/environment. Also the retry period and number of retries are defined in the CPA.

See [here](/ebms-core/introduction.md) for more information