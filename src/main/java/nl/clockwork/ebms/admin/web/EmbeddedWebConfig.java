/**
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

import javax.xml.namespace.QName;
import javax.xml.ws.Endpoint;
import javax.xml.ws.soap.SOAPBinding;

import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.AccessLevel;
import lombok.val;
import lombok.experimental.FieldDefaults;
import nl.clockwork.ebms.event.listener.EventListenerConfig.EventListenerType;
import nl.clockwork.ebms.service.EbMSMessageService;
import nl.clockwork.ebms.service.EbMSMessageServiceMTOM;
import nl.clockwork.ebms.service.cpa.CPAService;
import nl.clockwork.ebms.service.cpa.certificate.CertificateMappingService;
import nl.clockwork.ebms.service.cpa.url.URLMappingService;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmbeddedWebConfig
{
	@Value("${maxItemsPerPage}")
	Integer maxItemsPerPage;
	@Value("${eventListener.type}")
	EventListenerType eventListenerType;
	@Autowired
	CPAService cpaService;
	@Autowired
	URLMappingService urlMappingService;
	@Autowired
	CertificateMappingService certificateMappingService;
	@Autowired
	EbMSMessageService ebMSMessageService;
	@Autowired
	EbMSMessageServiceMTOM ebMSMessageServiceMTOM;

	@Bean
	public WicketApplication wicketApplication()
	{
		return new WicketApplication(maxItemsPerPage,eventListenerType);
	}

	@Bean
	public Endpoint cpaServiceEndpoint()
	{
		return publishEndpoint(cpaService,"/cpa");
	}

	@Bean
	public Endpoint urlMappingServiceEndpoint()
	{
		return publishEndpoint(urlMappingService,"/urlMapping");
	}

	@Bean
	public Endpoint certificateMappingServiceEndpoint()
	{
		return publishEndpoint(certificateMappingService,"/certificateMapping");
	}

	@Bean
	public Endpoint ebMSMessageServiceEndpoint()
	{
		return publishEndpoint(ebMSMessageService,"/ebms");
	}

	@Bean
	public Endpoint ebMSMessageServiceMTOMEndpoint()
	{
		val endpoint = new EndpointImpl(cxf(),ebMSMessageServiceMTOM);
		endpoint.setAddress("/ebmsMTOM");
		endpoint.setServiceName(new QName("http://service.ebms.clockwork.nl/","EbMSMessageServiceImplService"));
		endpoint.setEndpointName(new QName("http://service.ebms.clockwork.nl/","EbMSMessageServiceImplPort"));
		endpoint.publish();
		val binding = (SOAPBinding)endpoint.getBinding();
		binding.setMTOMEnabled(true);
		return endpoint;
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

	private Endpoint publishEndpoint(Object service, String address)
	{
		val endpoint = new EndpointImpl(cxf(),service);
		endpoint.setAddress(address);
		endpoint.publish();
		return endpoint;
	}
}
