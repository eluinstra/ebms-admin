---
sidebar_position: 13
---

# Release Notes

### [ebms-admin-2.20.10.jar](https://github.com/eluinstra/ebms-admin/releases/download/ebms-admin-2.20.10/ebms-admin-2.20.10.jar)

- hardened XML (JAXB) parsing of the web UI against billion-laughs & XXE
- TLS 1.2 + 1.3 by default, with recommended TLS 1.3 cipher suites
- updated dependencies

### [ebms-admin-2.20.9.jar](https://github.com/eluinstra/ebms-admin/releases/download/ebms-admin-2.20.9/ebms-admin-2.20.9.jar)

- updated license info

### [ebms-admin-2.20.8.jar](https://github.com/eluinstra/ebms-admin/releases/download/ebms-admin-2.20.8/ebms-admin-2.20.8.jar)

- re-release aligned to ebms-core 2.20.8

### [ebms-admin-2.20.7.jar](https://github.com/eluinstra/ebms-admin/releases/download/ebms-admin-2.20.7/ebms-admin-2.20.7.jar)

- improved the embedded Jetty implementation
- replaced text-io by jline
- updated dependencies

### [ebms-admin-2.20.6.jar](https://github.com/eluinstra/ebms-admin/releases/download/ebms-admin-2.20.6/ebms-admin-2.20.6.jar)

- updated dependencies
- fixed empty header value issue
- fixed missing ehcache dependency

### [ebms-admin-2.20.5.jar](https://github.com/eluinstra/ebms-admin/releases/download/ebms-admin-2.20.5/ebms-admin-2.20.5.jar)

- put database libraries in separate plugins
- removed kafka support
- added EchoHeaderServlet
- added client UUID header
- improvements

>> Note: Download the right database plugin and add it to the classpath. The configuration of the database stays the same.

### [ebms-admin-2.20.4.jar](https://github.com/eluinstra/ebms-admin/releases/download/ebms-admin-2.20.4/ebms-admin-2.20.4.jar)

- updated dependencies
- added MDCServletFilter
- improved caching key generation
- added Hazelcast cache plugin

To enable EhCache or Hazelcast:

- add the library to the classpath
- set environment variable cache_type="PLUGIN"
- you can override the default config by setting the environment variable cache_configLocation

### [ebms-admin-2.20.3.jar](https://github.com/eluinstra/ebms-admin/releases/download/ebms-admin-2.20.3/ebms-admin-2.20.3.jar)

- fixed database pool properties
- fixed: org.quartz.SchedulerConfigException: Non-ManagedTX DataSource name not set!
- updated dependencies

### [ebms-admin-2.20.2.jar](https://github.com/eluinstra/ebms-admin/releases/download/ebms-admin-2.20.2/ebms-admin-2.20.2.jar)

- removed Azure keyvault support

### [ebms-admin-2.20.1.jar](https://github.com/eluinstra/ebms-admin/releases/download/ebms-admin-2.20.1/ebms-admin-2.20.1.jar)

- reverted namespace prefixes
- updated dependencies

### [ebms-admin-2.20.0.jar](https://github.com/eluinstra/ebms-admin/releases/download/ebms-admin-2.20.0/ebms-admin-2.20.0.jar)

- upgrade to Java 17
- upgrade to Spring 6
- upgrade libraries from javax to jakarta
- split core in multiple modules
- removed Ignite caching support
- ehcache is provided as a separate library
