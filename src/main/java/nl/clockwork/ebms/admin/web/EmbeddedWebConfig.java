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

import java.security.cert.X509Certificate;
import java.util.Collections;

import javax.xml.namespace.QName;
import javax.xml.ws.Endpoint;
import javax.xml.ws.soap.SOAPBinding;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import org.apache.cxf.binding.BindingFactoryManager;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.jaxrs.JAXRSBindingFactory;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.vavr.Tuple;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import lombok.AccessLevel;
import lombok.val;
import lombok.experimental.FieldDefaults;
import nl.clockwork.ebms.cpa.CPAService;
import nl.clockwork.ebms.cpa.CPAServiceImpl;
import nl.clockwork.ebms.cpa.certificate.CertificateMappingService;
import nl.clockwork.ebms.cpa.certificate.CertificateMappingServiceImpl;
import nl.clockwork.ebms.cpa.url.URLMappingService;
import nl.clockwork.ebms.cpa.url.URLMappingServiceImpl;
import nl.clockwork.ebms.event.MessageEventListenerConfig.EventListenerType;
import nl.clockwork.ebms.jaxrs.X509CertificateDeserializer;
import nl.clockwork.ebms.jaxrs.X509CertificateSerializer;
import nl.clockwork.ebms.service.EbMSMessageService;
import nl.clockwork.ebms.service.EbMSMessageServiceMTOM;

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
		return publishEndpoint(cpaService,"/cpa","http://www.ordina.nl/cpa/2.18","CPAService","CPAPort");
	}

	@Bean
	public Endpoint urlMappingServiceEndpoint()
	{
		return publishEndpoint(urlMappingService,"/urlMapping","http://www.ordina.nl/cpa/urlMapping/2.18","URLMappingService","URLMappingPort");
	}

	@Bean
	public Endpoint certificateMappingServiceEndpoint()
	{
		return publishEndpoint(certificateMappingService,"/certificateMapping","http://www.ordina.nl/cpa/certificateMapping/2.18","CertificateMappingService","CertificateMappingPort");
	}

	@Bean
	public Endpoint ebMSMessageServiceEndpoint()
	{
		return publishEndpoint(ebMSMessageService,"/ebms","http://www.ordina.nl/ebms/2.18","EbMSMessageService","EbMSMessagePort");
	}

	@Bean
	public Endpoint ebMSMessageServiceMTOMEndpoint()
	{
		val result = new EndpointImpl(cxf(),ebMSMessageServiceMTOM);
		result.setAddress("/ebmsMTOM");
		result.setServiceName(new QName("http://www.ordina.nl/ebms/2.18","EbMSMessageService"));
		result.setEndpointName(new QName("http://www.ordina.nl/ebms/2.18","EbMSMessagePort"));
		result.publish();
		val binding = (SOAPBinding)result.getBinding();
		binding.setMTOMEnabled(true);
		return result;
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

	private Endpoint publishEndpoint(Object service, String address, String namespaceUri, String serviceName, String endpointName)
	{
		val result = new EndpointImpl(cxf(),service);
		result.setAddress(address);
		result.setServiceName(new QName(namespaceUri,serviceName));
		result.setEndpointName(new QName(namespaceUri,endpointName));
		result.publish();
		return result;
	}

	@Bean
	public Server createRestServer()
	{
		val sf = new JAXRSServerFactoryBean();
		sf.setBus(cxf());
		sf.setAddress("/rest");
		sf.setProvider(createJacksonJsonProvider());
		sf.setResourceClasses(getResourceClasses().keySet().toJavaList());
		getResourceClasses().forEach((resourceClass,resourceObject) -> {
			createResourceProvider(sf, resourceClass, resourceObject);
		});
		val manager = sf.getBus().getExtension(BindingFactoryManager.class);
		val factory = new JAXRSBindingFactory();
		factory.setBus(sf.getBus());
		manager.registerBindingFactory(JAXRSBindingFactory.JAXRS_BINDING_ID,factory);
		return sf.create();
	}

	Map<Class<?>,Object> getResourceClasses()
	{
		val result = HashMap.<Class<?>,Object>ofEntries(
			Tuple.of(CPAServiceImpl.class, cpaService),
			Tuple.of(URLMappingServiceImpl.class, urlMappingService),
			Tuple.of(CertificateMappingServiceImpl.class, certificateMappingService));
		return result;
	}

	private JacksonJsonProvider createJacksonJsonProvider()
	{
		val result = new JacksonJsonProvider();
		result.setMapper(createObjectMapper());
		return result;
	}

	private ObjectMapper createObjectMapper() {
		val result = new ObjectMapper();
		result.registerModule(createSimpleModule());
		return result;
	}

	private SimpleModule createSimpleModule() {
		val result = new SimpleModule();
		result.addSerializer(X509Certificate.class, new X509CertificateSerializer());
		result.addDeserializer(X509Certificate.class, new X509CertificateDeserializer());
		return result;
	}

	private void createResourceProvider(JAXRSServerFactoryBean sf, Class<?> resourceClass, Object resourceObject) {
		sf.setResourceProvider(resourceClass, new SingletonResourceProvider(resourceObject));
	}
}
