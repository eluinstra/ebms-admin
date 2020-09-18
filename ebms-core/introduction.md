---
sort: 1
---

# Introduction

This library contains the core functionality of the EbMS adapter including:
- a servlet to use the adapter in a servlet container
- CPA, URL, Certificate and EbMSMessage (SOAP) Interfaces to control the EbMS adapter

Implemented:
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

Not implemented:
-	Core Functionality
	o	Packaging
-	Additional Features:
	o	Message Order Module
	o	Multi-Hop Module

Remarks:
-	Duplicate messages will always be eliminated
-	Only standalone MSH level messages are supported
-	Only acts as ToPartyMSH, not as nextMSH
-	Only 1 (=allPurpose) Channel per Action is supported
-	Manifest can only refer to payload data included as part of the message
-	Extendible to support other communication protocols
