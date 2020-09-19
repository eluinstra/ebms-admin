---
sort: 2
---

# Usage

You can use ebms-core by adding it to your own Java project, or you can use it as a standalone SOAP Service through [ebms-admin console]({{ site.baseurl }}{% link ebms-admin/introduction.md %})

![image]({{ site.baseurl }}/assets/images/ebms-overview.svg)

It offers the following APIs
- EbMS API to connect to other EbMS adapters
- [Adapter/SOAP APIs]({{ site.baseurl }}{% link ebms-core/api.md %}) to connect to applications to manage the EbMS adapter  

If you want to use ebms-core in your own application you have to add the [jar]({{ site.baseurl }}/ebms-core/build.html#maven) to your project and configure the adapter through [Spring Properties]({{ site.baseurl }}{% link ebms-core/properties.md %}).  
You also have to add the Spring configuration class [MainCondig.java](https://github.com/eluinstra/ebms-core/blob/ebms-core-{{ site.data.ebms.branch.version }}/src/main/java/nl/clockwork/ebms/MainConfig.java) to your project or replace it by your custom implementation.  
Finally you have to add the [EbMSServlet](https://github.com/eluinstra/ebms-core/blob/ebms-core-{{ site.data.ebms.branch.version }}/src/main/java/nl/clockwork/ebms/server/servlet/EbMSServlet.java) class to your web configuration expose the EbMS API.  
Your application can manage the adapter through the [Adapter APIs]({{ site.baseurl }}{% link ebms-core/api.md %}). These APIs can also be exposed as SOAP Services.  
The adapter stores its data in a [database]({{ site.baseurl }}{% link ebms-core/database.md %}).  

