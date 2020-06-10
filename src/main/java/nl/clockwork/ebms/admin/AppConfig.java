package nl.clockwork.ebms.admin;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import lombok.AccessLevel;
import lombok.val;
import lombok.experimental.FieldDefaults;
import nl.clockwork.ebms.admin.dao.AdminDAOConfig;
import nl.clockwork.ebms.admin.dao.DataSourceConfig;
import nl.clockwork.ebms.admin.web.WebConfig;

@Configuration(proxyBeanMethods = false)
@Import({
	AdminDAOConfig.class,
	DataSourceConfig.class,
	WebConfig.class})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppConfig
{
	@Bean
	public PropertySourcesPlaceholderConfigurer properties()
	{
		val configDir = System.getProperty("ebms.configDir");
		PropertySourcesPlaceholderConfigurer c = new PropertySourcesPlaceholderConfigurer();
		Resource[] resources = new Resource[]{
				new ClassPathResource("nl/clockwork/ebms/admin/default.properties"),
				new FileSystemResource(configDir + "ebms-admin.advanced.properties"),
				new FileSystemResource(configDir + "ebms-admin.properties")};
		c.setLocations(resources);
		c.setIgnoreResourceNotFound(true);
		return c;
	}
}
