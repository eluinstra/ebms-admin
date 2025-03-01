---
sidebar_position: 12
---

# EbMS Admin 2.20 Migration Guide

## Before you start

We assume that you have installed EbMS Admin 2.19.x.

:::danger
Ignite cache is not supported anymore
:::

:::danger
Azure keyvault and insights are not supported anymore
:::

## Upgrade to EbMS Admin 2.20.x

### Upgrade Java

Install the latest JRE or JDK 17.

### Shutdown EbMS Admin 2.19.x

Shutdown your current EbMS Admin application.

### Reconfigure Caching

If you configured Ignite caching, you have to switch to EhCache. See [here](properties.md#cache) for the configuration.
We had to remove Ignite, because it was not upgraded to the newer version Spring that we upgraded to.

### Azure support

We removed Azure keyvault and insights because we were experiencing problems due to errors in de azure libraries. You can create and inject a JKS or P12 keystore in your deployment instead.

### Upgrade EbMS Admin

Replace the ebms-admin-2.19.x.jar by the latest ebms-admin-2.20.x.jar and start the EbMS Admin application.
