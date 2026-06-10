---
sidebar_position: 5
---

# Caching

The default cache type is `DEFAULT` which is a simple in-memory cache. You cannot disable caching anymore. To enable caching with overflow to disk, configure the [EhCache](#ehcache) plugin. To enable distributed caching, configure the [Hazelcast](#hazelcast) plugin. When you are [scaling](/ebms-admin/deployment.md#scaling) the EbMS Adapter, you should set enable [Hazelcast](#hazelcast).

## EhCache

To enable EhCache:

- add the [library](https://github.com/eluinstra/ebms-admin/releases/download/ebms-admin-@ebms.core.version@/ebms-ehcache-cache-plugin-@ebms.core.version@.jar) to the classpath
- set environment variable `cache_type="PLUGIN"`
- you can override the default config by setting the environment variable `cache_configLocation`
  you can find the default EhCache configuration file [here](https://github.com/eluinstra/ebms-core/blob/ebms-core-@ebms.core.version@/plugin/cache/ehcache/src/main/resources/nl/clockwork/ebms/plugin/cache/ehcache/ehcache.xml)

## Hazelcast

To enable Hazelcast:

- add the [library](https://github.com/eluinstra/ebms-admin/releases/download/ebms-admin-@ebms.core.version@/ebms-hazelcast-cache-plugin-@ebms.core.version@.jar) to the classpath
- set environment variable `cache_type="PLUGIN"`
- you can override the default config by setting the environment variable `cache_configLocation`
  you can find the default Hazelcast configuration file [here](https://github.com/eluinstra/ebms-core/blob/ebms-core-@ebms.core.version@/plugin/cache/hazelcast/src/main/resources/hazelcast.yaml)
