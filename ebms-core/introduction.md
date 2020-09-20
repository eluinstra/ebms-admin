---
sort: 1
---

# Introduction

This library implements the [EbMS 2.0 specification](http://www.ebxml.org/specs/ebMS2.pdf).

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
-	Additional Features:
	-	Message Order Module
	-	Multi-Hop Module

### Remarks
-	Duplicate messages will always be eliminated
-	Only standalone MSH level messages are supported
-	Only acts as ToPartyMSH, not as nextMSH
-	Only 1 (=allPurpose) Channel per Action is supported
-	Manifest can only refer to payload data included as part of the message
-	Extendible to support other communication protocols
