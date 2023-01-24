/*
 * Copyright 2013 Clockwork
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.clockwork.ebms.admin.web;


import java.util.Collections;
import java.util.HashMap;
import javax.xml.namespace.QName;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.val;
import nl.clockwork.ebms.cpa.CPAService;
import nl.clockwork.ebms.event.MessageEventListenerConfig.EventListenerType;
import nl.clockwork.ebms.service.EbMSMessageServiceMTOM;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transport.http.HTTPConduitConfigurer;
import org.apache.cxf.transports.http.configuration.ConnectionType;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WebConfig
{
	@Value("${maxItemsPerPage}")
	Integer maxItemsPerPage;
	@Value("${eventListener.type}")
	EventListenerType eventListenerType;
	@Value("${service.ebms.url}")
	String serviceUrl;

	@Bean
	public WicketApplication wicketApplication()
	{
		return new WicketApplication(maxItemsPerPage, eventListenerType);
	}

	@Bean(name = "cpaService")
	public Object cpaClientProxy()
	{
		return proxyFactoryBean(CPAService.class, serviceUrl + "/cpa", "http://www.ordina.nl/cpa/2.18", "CPAService", "CPAPort").create();
	}

	@Bean(name = "urlMappingService")
	public Object urlMappingClientProxy()
	{
		return proxyFactoryBean(CPAService.class, serviceUrl + "/urlMapping", "http://www.ordina.nl/cpa/urlMapping/2.18", "URLMappingService", "URLMappingPort")
				.create();
	}

	@Bean(name = "certificateMappingService")
	public Object certificateMappingClientProxy()
	{
		return proxyFactoryBean(
				CPAService.class,
				serviceUrl + "/certificateMapping",
				"http://www.ordina.nl/cpa/certificateMapping/2.18",
				"CertificateMappingService",
				"CertificateMappingPort").create();
	}

	@Bean(name = "ebMSMessageService")
	public Object ebMSMessageClientProxy()
	{
		return proxyFactoryBean(CPAService.class, serviceUrl + "/ebms", "http://www.ordina.nl/ebms/2.18", "EbMSMessageService", "EbMSMessagePort").create();
	}

	@Bean(name = "ebMSMessageServiceMTOM")
	public Object ebMSMessageMTOMClientProxy()
	{
		val proxyFactory = new JaxWsProxyFactoryBean();
		setMTOM(proxyFactory);
		proxyFactory.setServiceClass(EbMSMessageServiceMTOM.class);
		proxyFactory.setAddress(serviceUrl + "/ebmsMTOM");
		proxyFactory.setServiceName(new QName("http://www.ordina.nl/ebms/2.18", "EbMSMessageService"));
		proxyFactory.setEndpointName(new QName("http://www.ordina.nl/ebms/2.18", "EbMSMessagePort"));
		return proxyFactory.create();
	}

	@Bean
	public void something()
	{
		val httpConduitConfigurer = new HTTPConduitConfigurer()
		{
			public void configure(String name, String address, HTTPConduit c)
			{
				val httpClientPolicy = new HTTPClientPolicy();
				httpClientPolicy.setConnection(ConnectionType.KEEP_ALIVE);
				httpClientPolicy.setMaxRetransmits(1);
				httpClientPolicy.setAllowChunking(false);
				c.setClient(httpClientPolicy);
			}
		};
		cxf().setExtension(httpConduitConfigurer, HTTPConduitConfigurer.class);
	}

	@Bean
	public SpringBus cxf()
	{
		val result = new SpringBus();
		val f = new LoggingFeature();
		f.setPrettyLogging(true);
		result.setFeatures(Collections.singletonList(f));
		return result;
	}

	private void setMTOM(final org.apache.cxf.jaxws.JaxWsProxyFactoryBean proxyFactory)
	{
		val props = new HashMap<String, Object>();
		props.put("mtom-enabled", true);
		proxyFactory.setProperties(props);
	}

	private JaxWsProxyFactoryBean proxyFactoryBean(Class<?> clazz, String url, String namespaceUri, String serviceName, String endpointName)
	{
		val result = new JaxWsProxyFactoryBean();
		result.setServiceClass(clazz);
		result.setAddress(serviceUrl + url);
		result.setServiceName(new QName(namespaceUri, serviceName));
		result.setEndpointName(new QName(namespaceUri, endpointName));
		return result;
	}
}
