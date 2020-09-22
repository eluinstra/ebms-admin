---
sort: 2
---

# Overview
You can use ebms-core by integrating it into your own Java application, or you can use it as a standalone SOAP Service through [ebms-admin console]({{ site.baseurl }}{% link ebms-admin/introduction.md %})

![image]({{ site.baseurl }}/assets/images/ebms-overview.svg)

## Basic
It offers the following interfaces
- EbMS Interface to connect to other EbMS adapters
- [EbMS/SOAP API]({{ site.baseurl }}{% link ebms-core/api.md %}) to connect to applications to manage the EbMS adapter  

If you want to use ebms-core in your own application you have to add the [jar]({{ site.baseurl }}/ebms-core/development.html#maven) to your project and configure the adapter through [Spring Properties]({{ site.baseurl }}{% link ebms-core/properties.md %}).  
You also have to add the Spring configuration class [MainCondig.java](https://github.com/eluinstra/ebms-core/blob/ebms-core-{{ site.data.ebms.branch.version }}/src/main/java/nl/clockwork/ebms/MainConfig.java) to your project or replace it by your custom implementation.  
Finally you have to add the [EbMSServlet](https://github.com/eluinstra/ebms-core/blob/ebms-core-{{ site.data.ebms.branch.version }}/src/main/java/nl/clockwork/ebms/server/servlet/EbMSServlet.java) class to your web configuration expose the EbMS Interface.  
Your application can manage the adapter through the [EbMS API]({{ site.baseurl }}{% link ebms-core/api.md %}). This API can also be exposed as SOAP Services.  
The adapter stores its data in the [EbMS database]({{ site.baseurl }}{% link ebms-core/database.md %}).  

## Advanced
### EventListener
You can track sent and received messages if you enable the [EventListener]({{ site.baseurl}}/ebms-core/properties.html#eventlistener).

### Scaling
