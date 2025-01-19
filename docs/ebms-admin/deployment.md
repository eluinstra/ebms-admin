---
sidebar_position: 2
---

# Deployment Scenarios

To communicate with another application over EbMS

- an application communicates with the EbMS adapter through the [SOAP API](soap) over HTTP(S)
- the EbMS adapter communicates with another EbMS adapter through the EbMS Interface over HTTP(S).

It is also possible to connect multiple applications to one EbMS adapter.

![EbMS Overview](/assets/images/ebms-overview.svg)

See [here](examples) for different deployment scenario examples.

## Basic

![EbMS Basic Deployment Scenario](/assets/images/ebms-basic.svg)

### Behind a reverse proxy

#### Scenario 1

![EbMS Basic Deployment Scenario - Behind a reverse proxy - Scenario 1](/assets/images/ebms-rproxy1.svg)

The reverse proxy is only used for incoming traffic.

#### Scenario 2

![EbMS Basic Deployment Scenario - Behind a reverse proxy - Scenario 2](/assets/images/ebms-rproxy2.svg)

The reverse proxy is used for both incoming and outgoing traffic.

See the [URLMapping Service](/ebms-core/api.md#urlmappingservice) for overriding the CPA's endpoint URL to point to the Reverse Proxy.

### Using a forward proxy

See [here](/ebms-core/properties.md#forward-proxy) for the forward proxy settings.

![EbMS Basic Deployment Scenario - Using a forward proxy](/assets/images/ebms-fproxy.svg)

The forward proxy is used for outgoing traffic.

## Scaling

### Scaling through CPAs

![EbMS Scaling Deployment Scenario - Scaling through CPAs](/assets/images/ebms-scaling-cpa.svg)

If your application connects to different parties using different CPAs, then you can load the different CPAs into different EbMS adapters, so that each EbMS adapter only handles one or several CPAs. Each EbMS adapter will have its own (unique) endpoint that also needs to be defined in the CPA(s) it handles.

### Scaling with serverId

![EbMS Scaling Deployment Scenario - Scaling with serverId](/assets/images/ebms-scaling-serverid.svg)

You can deploy different EbMS adapters [each configured with a unique serverId](/ebms-core/properties.md#ebms-core). Each EbMS adapter will then handle its own requests. For this all EbMS adapters have to use the same [database](properties#database) ([cache](/ebms-core/properties.md#cache) and [queues](/ebms-core/properties.md#jms)). The EbMS adapters need to be placed behind a load balancer so all the EbMS adapters are exposed at the same endpoint. In this way the load will spread amoungst the different EbMS adapters. You can also place a load balancer between your application(s) and the EbMS adapters.

### Advanced scaling

You can start the EbMS adapter in [EbMS Server](command#start-in-ebms-server-mode), [EbMS Client](command#start-in-ebms-client-mode), [SOAP API](command#start-in-soap-api-mode) or [Web Interface](command#start-in-web-interface-mode) mode. You can also start multiple EbMS Servers, EbMS Clients, SOAP APIs and Web Interfaces simultaniously. All EbMS Servers, EbMS Clients, SOAP APIs and Web Interfaces have to use the same [database](properties#database) ([cache](/ebms-core/properties.md#cache) and [queues](/ebms-core/properties.md#jms)). In this way you can divide the load over different components.

![EbMS Scaling Deployment Scenario - Advanced scaling](/assets/images/ebms-scaling-advanced.svg)
