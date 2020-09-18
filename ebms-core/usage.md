---
sort: 2
---

# Usage

You can use the ebms-core by integrating it into your own java application, or you can use it as a standalone SOAP service through ebms-admin console
 
If you want to use the ebms-core in your own application you can include the jar into your application and configure the adapter through spring properties.  
You can include the spring configuration class [MainCondig.java](https://github.com/eluinstra/ebms-core/blob/ebms-core-2.17.x/src/main/java/nl/clockwork/ebms/MainConfig.java) into your application or modify it to your needs.  
For properties configuration see [Properties]({% link ebms-core/properties.md %})  
An application should call the ebms-core through the [EbMS API]({% link ebms-core/api.md %})  
To receive ebms messages configure the servlet nl.clockwork.ebms.servlet.EbMSServlet in your application  
