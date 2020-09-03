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
package nl.clockwork.ebms.admin;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import lombok.AccessLevel;
import lombok.val;
import lombok.experimental.FieldDefaults;
import nl.clockwork.ebms.CommonConfig;
import nl.clockwork.ebms.admin.dao.AdminDAOConfig;
import nl.clockwork.ebms.admin.web.EmbeddedWebConfig;
import nl.clockwork.ebms.cache.CacheConfig;
import nl.clockwork.ebms.client.ClientConfig;
import nl.clockwork.ebms.client.DeliveryManagerConfig;
import nl.clockwork.ebms.cpa.CPAManagerConfig;
import nl.clockwork.ebms.dao.DAOConfig;
import nl.clockwork.ebms.datasource.DataSourceConfig;
import nl.clockwork.ebms.encryption.EncryptionConfig;
import nl.clockwork.ebms.event.listener.EventListenerConfig;
import nl.clockwork.ebms.jms.JMSConfig;
import nl.clockwork.ebms.processor.EbMSProcessorConfig;
import nl.clockwork.ebms.querydsl.QueryDSLConfig;
import nl.clockwork.ebms.security.KeyStoreConfig;
import nl.clockwork.ebms.send.SendTaskHandlerConfig;
import nl.clockwork.ebms.send.SendTaskManagerConfig;
import nl.clockwork.ebms.server.ServerConfig;
import nl.clockwork.ebms.service.ServiceConfig;
import nl.clockwork.ebms.signing.SigningConfig;
import nl.clockwork.ebms.transaction.TransactionManagerConfig;
import nl.clockwork.ebms.validation.ValidationConfig;

@Configuration
@Import({
		AdminDAOConfig.class,
		CacheConfig.class,
		ClientConfig.class,
		CommonConfig.class,
		CPAManagerConfig.class,
		DAOConfig.class,
		DataSourceConfig.class,
		DeliveryManagerConfig.class,
		EbMSProcessorConfig.class,
		EmbeddedWebConfig.class,
		EncryptionConfig.class,
		EventListenerConfig.class,
		SendTaskManagerConfig.class,
		SendTaskHandlerConfig.class,
		JMSConfig.class,
		KeyStoreConfig.class,
		QueryDSLConfig.class,
		ServerConfig.class,
		ServiceConfig.class,
		SigningConfig.class,
		TransactionManagerConfig.class,
		ValidationConfig.class})
@PropertySource(value = {
		"classpath:nl/clockwork/ebms/default.properties",
		"classpath:nl/clockwork/ebms/admin/default.properties",
		"file:${ebms.configDir}ebms-admin.embedded.advanced.properties",
		"file:${ebms.configDir}ebms-admin.embedded.properties"},
		ignoreResourceNotFound = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmbeddedAppConfig
{
	public static PropertySourcesPlaceholderConfigurer PROPERTY_SOURCE = propertySourcesPlaceholderConfigurer();
	
	private static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer()
	{
		val result = new PropertySourcesPlaceholderConfigurer();
		val configDir = System.getProperty("ebms.configDir");
		val resources = new Resource[]{
				new ClassPathResource("nl/clockwork/ebms/default.properties"),
				new ClassPathResource("nl/clockwork/ebms/admin/default.properties"),
				new FileSystemResource(configDir + "ebms-admin.embedded.advanced.properties"),
				new FileSystemResource(configDir + "ebms-admin.embedded.properties")};
		result.setLocations(resources);
		result.setIgnoreResourceNotFound(true);
		return result;
	}
}
