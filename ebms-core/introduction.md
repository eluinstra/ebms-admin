---
parent: EbMS Core
nav_order: 1
---

# Introduction
{: .no_toc }

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}
---

ebms-core is a Java implementation of the [EbMS 2.0 specification](http://www.ebxml.org/specs/ebMS2.pdf).

### Implemented

-	Core Functionality
	-	Security Module
		-	Signature
		-	Encryption
	-	Error Handling Module
	-	SyncReply Module
-	Additional Features:
	-	Reliable Messaging Module
	-	Message Status Service
	-	Message Service Handler Ping Service
-	HTTP(S) Protocol

### Not implemented

-	Core Functionality
	-	Packaging
-	Additional Features
	-	Message Order Module
	-	Multi-Hop Module

### Remarks

-	Duplicate messages will always be eliminated and will not be stored
-	Only standalone MSH level messages are supported
-	Only acts as ToPartyMSH, not as nextMSH
-	Only 1 (=allPurpose) Channel per Action is supported
-	Manifest can only refer to payload data included as part of the message
-	Extendible to support other communication protocols

### Message Statuses

EbMS messages can have the following statuses

![EbMS Message Statuses]({{ site.baseurl }}/assets/images/ebms-message-states.svg)