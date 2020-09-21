---
sort: 2
---

# Deployment Scenarios

To communicate with another application over EbMS:
- an application communicates with the EbMS adapter through the [SOAP API]({{ site.baseurl }}{% link ebms-admin/soap.md %}) over HTTP(S)
- the EbMS adapter communicates with another EbMS adapter through the EbMS Interface over HTTP(S).  

![image]({{ site.baseurl }}/assets/images/ebms-overview.svg)  

See [here]({{ site.baseurl }}{% link ebms-admin/examples.md %}) for different deployment scenario examples

## Basic
![image]({{ site.baseurl }}/assets/images/ebms-basic.svg)

### Behind a reverse proxy
{: #reverse-proxy}

See the [URLMapping Service]({{ site.baseurl }}/ebms-core/api.html#urlmappingservice) for overriding the CPA's endpoint URL to point to the Reverse Proxy

#### Scenario 1
![image]({{ site.baseurl }}/assets/images/ebms-rproxy1.svg)  
The reverse proxy is only used for incoming traffic

#### Scenario 2
![image]({{ site.baseurl }}/assets/images/ebms-rproxy2.svg)  
The reverse proxy is used for both incoming and outgoing traffic

### Using a forwarding proxy
![image]({{ site.baseurl }}/assets/images/ebms-fproxy.svg)  
The forwarding proxy is used for outgoing traffic

## Scaling

### Scaling through CPAs
{: #scaling-cpas}
![image]({{ site.baseurl }}/assets/images/ebms-scaling-cpa.svg)  
If your application connects to different parties using different CPAs, then you can load the different CPAs into different EbMS adapters, so that each EbMS adapter only handles one or several CPAs. Each EbMS adapter will have its own (unique) endpoint that also needs to be defined in the CPA(s) it handles.

### Scaling with serverId
{: #scaling-serverid}
![image]({{ site.baseurl }}/assets/images/ebms-scaling-serverid.svg)  
You can deploy different EbMS adapters [configured each with a unique serverId]({{ site.baseurl }}/ebms-core/properties.html#core). Each EbMS adapter will then handle its own requests. For this all EbMS adapters have to use the same database. The EbMS adapters need to be placed behind a load balancer so all the EbMS adapters are exposed at the same endpoint. You can also place a load balancer between your application(s) and the EbMS adapters. In this way the load will spread amoungst the different EbMS adapters.

### Advanced scaling
{: #scaling-advanced}
