package nl.clockwork.ebms.admin;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
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
import nl.clockwork.ebms.cpa.CPAManagerConfig;
import nl.clockwork.ebms.dao.DAOConfig;
import nl.clockwork.ebms.dao.DataSourceConfig;
import nl.clockwork.ebms.encryption.EncryptionConfig;
import nl.clockwork.ebms.event.listener.EventListenerConfig;
import nl.clockwork.ebms.event.processor.EventManagerConfig;
import nl.clockwork.ebms.event.processor.EventProcessorConfig;
import nl.clockwork.ebms.jms.JMSConfig;
import nl.clockwork.ebms.processor.EbMSProcessorConfig;
import nl.clockwork.ebms.security.KeyStoreConfig;
import nl.clockwork.ebms.server.ServerConfig;
import nl.clockwork.ebms.service.ServiceConfig;
import nl.clockwork.ebms.signing.SigningConfig;
import nl.clockwork.ebms.validation.ValidationConfig;

@Configuration(proxyBeanMethods = false)
@Import({
	AdminDAOConfig.class,
	CacheConfig.class,
	ClientConfig.class,
	CommonConfig.class,
	CPAManagerConfig.class,
	DAOConfig.class,
	DataSourceConfig.class,
	EbMSProcessorConfig.class,
	EmbeddedWebConfig.class,
	EncryptionConfig.class,
	EventListenerConfig.class,
	EventManagerConfig.class,
	EventProcessorConfig.class,
	JMSConfig.class,
	KeyStoreConfig.class,
	ServerConfig.class,
	ServiceConfig.class,
	SigningConfig.class,
	ValidationConfig.class})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmbeddedAppConfig
{
	@Bean("propertyConfigurer")
	public static PropertySourcesPlaceholderConfigurer properties()
	{
		val configDir = System.getProperty("ebms.configDir");
		PropertySourcesPlaceholderConfigurer c = new PropertySourcesPlaceholderConfigurer();
		Resource[] resources = new Resource[]{
				new ClassPathResource("nl/clockwork/ebms/default.properties"),
				new ClassPathResource("nl/clockwork/ebms/admin/default.properties"),
				new FileSystemResource(configDir + "ebms-admin.embedded.advanced.properties"),
				new FileSystemResource(configDir + "ebms-admin.embedded.properties")};
		c.setLocations(resources);
		c.setIgnoreResourceNotFound(true);
		return c;
	}
}
