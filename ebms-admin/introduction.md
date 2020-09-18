---
sort: 1
---

# Introduction

The ebms-admin app is a fully functional EbMS adapter that uses the ebms-core jar for this.  
The ebms-admin app consists of an ebms-adapter and a web and a soap interface to manage the ebms-adapter.  
The ebms-adapter is configured through the ebms-admin properties file that can be generated in the EbMSAdminPropertiesPage.  
The web and soap interfaces are configured through the application's command line properties (see [Command Line Options]({{ site.baseurl }}{% link ebms-admin/command.md %}) for examples)  

It now supports https for the web and soap interfaces as well as basic and client certificate authentication  
It supports now also (and defaults to) PKCS12 keystores.

See [Command Line Options]({{ site.baseurl }}{% link ebms-admin/command.md %}) for usage  
See [Properties]({{ site.baseurl }}{% link ebms-admin/properties.md %}) for configuration  
See [Examples]({{ site.baseurl }}{% link ebms-admin/examples.md %}) for some runnable example scenarios  
