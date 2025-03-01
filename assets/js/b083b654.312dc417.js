"use strict";(self.webpackChunkdocumentation=self.webpackChunkdocumentation||[]).push([[433],{5450:e=>{e.exports=JSON.parse('{"version":{"pluginId":"default","version":"current","label":"Next","banner":null,"badge":false,"noIndex":false,"className":"docs-version-current","isLast":true,"docsSidebars":{"tutorialSidebar":[{"type":"link","label":"Home","href":"/ebms-admin/docs/intro","docId":"intro","unlisted":false},{"type":"category","label":"EbMS Admin","collapsible":true,"collapsed":true,"items":[{"type":"link","label":"Introduction","href":"/ebms-admin/docs/ebms-admin/introduction","docId":"ebms-admin/introduction","unlisted":false},{"type":"link","label":"Deployment Scenarios","href":"/ebms-admin/docs/ebms-admin/deployment","docId":"ebms-admin/deployment","unlisted":false},{"type":"link","label":"Installation and Configuration","href":"/ebms-admin/docs/ebms-admin/installation","docId":"ebms-admin/installation","unlisted":false},{"type":"link","label":"Command Line Options","href":"/ebms-admin/docs/ebms-admin/command","docId":"ebms-admin/command","unlisted":false},{"type":"link","label":"Properties","href":"/ebms-admin/docs/ebms-admin/properties","docId":"ebms-admin/properties","unlisted":false},{"type":"link","label":"SSL Configuration","href":"/ebms-admin/docs/ebms-admin/ssl","docId":"ebms-admin/ssl","unlisted":false},{"type":"link","label":"Database support","href":"/ebms-admin/docs/ebms-admin/database","docId":"ebms-admin/database","unlisted":false},{"type":"link","label":"Examples","href":"/ebms-admin/docs/ebms-admin/examples","docId":"ebms-admin/examples","unlisted":false},{"type":"link","label":"SOAP Interface","href":"/ebms-admin/docs/ebms-admin/soap","docId":"ebms-admin/soap","unlisted":false},{"type":"link","label":"REST Interface","href":"/ebms-admin/docs/ebms-admin/rest","docId":"ebms-admin/rest","unlisted":false},{"type":"link","label":"EbMS Admin 2.19 Migration Guide","href":"/ebms-admin/docs/ebms-admin/migration","docId":"ebms-admin/migration","unlisted":false},{"type":"link","label":"Release Notes","href":"/ebms-admin/docs/ebms-admin/release","docId":"ebms-admin/release","unlisted":false}],"href":"/ebms-admin/docs/category/ebms-admin"},{"type":"category","label":"EbMS Core","collapsible":true,"collapsed":true,"items":[{"type":"link","label":"Introduction","href":"/ebms-admin/docs/ebms-core/introduction","docId":"ebms-core/introduction","unlisted":false},{"type":"link","label":"EbMS Core","href":"/ebms-admin/docs/ebms-core/","docId":"ebms-core/README","unlisted":false},{"type":"link","label":"Overview","href":"/ebms-admin/docs/ebms-core/overview","docId":"ebms-core/overview","unlisted":false},{"type":"link","label":"Development","href":"/ebms-admin/docs/ebms-core/development","docId":"ebms-core/development","unlisted":false},{"type":"link","label":"Default Properties","href":"/ebms-admin/docs/ebms-core/properties","docId":"ebms-core/properties","unlisted":false},{"type":"link","label":"Database support","href":"/ebms-admin/docs/ebms-core/database","docId":"ebms-core/database","unlisted":false},{"type":"link","label":"EbMS API","href":"/ebms-admin/docs/ebms-core/api","docId":"ebms-core/api","unlisted":false},{"type":"link","label":"Release Notes","href":"/ebms-admin/docs/ebms-core/release","docId":"ebms-core/release","unlisted":false},{"type":"link","label":"Roadmap","href":"/ebms-admin/docs/ebms-core/roadmap","docId":"ebms-core/roadmap","unlisted":false}],"href":"/ebms-admin/docs/category/ebms-core"}]},"docs":{"ebms-admin/command":{"id":"ebms-admin/command","title":"Command Line Options","description":"Show help","sidebar":"tutorialSidebar"},"ebms-admin/database":{"id":"ebms-admin/database","title":"Database support","description":"If you want to use a database other then H2 or HSQLDB, you have to provide the JDBC driver yourself.","sidebar":"tutorialSidebar"},"ebms-admin/deployment":{"id":"ebms-admin/deployment","title":"Deployment Scenarios","description":"To communicate with another application over EbMS","sidebar":"tutorialSidebar"},"ebms-admin/examples":{"id":"ebms-admin/examples","title":"Examples","description":"Below you\'ll find and simple example of how to configure 2 EbMS Admin Consoles with embedded EbMS adapter to communicate with each other.","sidebar":"tutorialSidebar"},"ebms-admin/installation":{"id":"ebms-admin/installation","title":"Installation and Configuration","description":"Prerequisites","sidebar":"tutorialSidebar"},"ebms-admin/introduction":{"id":"ebms-admin/introduction","title":"Introduction","description":"ebms-admin is a standalone EbMS adapter that uses an embedded Jetty server. ebms-admin exposes a Web and a SOAP interface over HTTP(S) to manage the EbMS adapter and it uses a database to store the data. ebms-admin is configured through properties and command line options. It also supports basic and client certificate authentication for the Web, REST and SOAP interfaces.","sidebar":"tutorialSidebar"},"ebms-admin/migration":{"id":"ebms-admin/migration","title":"EbMS Admin 2.19 Migration Guide","description":"Before you start","sidebar":"tutorialSidebar"},"ebms-admin/properties":{"id":"ebms-admin/properties","title":"Properties","description":"Below the default properties of ebms-admin. For the default properties of ebms-core see here.","sidebar":"tutorialSidebar"},"ebms-admin/release":{"id":"ebms-admin/release","title":"Release Notes","description":"@ebms.core.version@ and ebms-admin-2.18.10 are the latest versions.","sidebar":"tutorialSidebar"},"ebms-admin/rest":{"id":"ebms-admin/rest","title":"REST Interface","description":"The REST Interface is not (yet) enabled seperately but together with the SOAP interface through a command line option.","sidebar":"tutorialSidebar"},"ebms-admin/soap":{"id":"ebms-admin/soap","title":"SOAP Interface","description":"The SOAP Interface can be enabled through a command line option.","sidebar":"tutorialSidebar"},"ebms-admin/ssl":{"id":"ebms-admin/ssl","title":"SSL Configuration","description":"To configure SSL for the EbMS interface you have to create and configure a keystore and a truststore. You can and should also configure SSL for the Web/SOAP/REST interface.","sidebar":"tutorialSidebar"},"ebms-core/api":{"id":"ebms-core/api","title":"EbMS API","description":"The EbMS API consists of the following services","sidebar":"tutorialSidebar"},"ebms-core/database":{"id":"ebms-core/database","title":"Database support","description":"The EbMS Adapter supports the following databases","sidebar":"tutorialSidebar"},"ebms-core/development":{"id":"ebms-core/development","title":"Development","description":"If you want to use ebms-core in your own application you have to add the JAR to your project. You also have to add the Spring configuration class MainCondig.java to your project or replace it by your custom implementation. Finally you have to add the EbMSServlet class to your web configuration to expose the EbMS Interface. Your application can manage the adapter through the EbMS API. This API can also be exposed as SOAP Services.","sidebar":"tutorialSidebar"},"ebms-core/introduction":{"id":"ebms-core/introduction","title":"Introduction","description":"ebms-core is a Java implementation of the EbMS 2.0 specification.","sidebar":"tutorialSidebar"},"ebms-core/overview":{"id":"ebms-core/overview","title":"Overview","description":"You can use ebms-core by integrating it into your own Java application, or you can use it as a standalone SOAP Service through ebms-admin.","sidebar":"tutorialSidebar"},"ebms-core/properties":{"id":"ebms-core/properties","title":"Default Properties","description":"Below the contents of ebms-core\'s default.properties file. These are the default settings for ebms-core.","sidebar":"tutorialSidebar"},"ebms-core/README":{"id":"ebms-core/README","title":"EbMS Core","description":"","sidebar":"tutorialSidebar"},"ebms-core/release":{"id":"ebms-core/release","title":"Release Notes","description":"ebms-core-2.19.3.jar","sidebar":"tutorialSidebar"},"ebms-core/roadmap":{"id":"ebms-core/roadmap","title":"Roadmap","description":"release 2.19.x","sidebar":"tutorialSidebar"},"intro":{"id":"intro","title":"Home","description":"EbMS adapter for Java. Supports EbMS 2.0 specification. Was initially developed to support the Digipoort EbMS specification, as defined by Logius for the Dutch government.","sidebar":"tutorialSidebar"}}}}')}}]);