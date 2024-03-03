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
package nl.clockwork.ebms.admin;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import lombok.AccessLevel;
import lombok.val;
import lombok.experimental.FieldDefaults;

@Configuration
@PropertySource(
		value = {"classpath:nl/clockwork/ebms/default.properties", "classpath:nl/clockwork/ebms/admin/default.properties",
				"classpath:nl/clockwork/ebms/cache/ignite/default.properties", "file:${ebms.configDir}ebms-admin.embedded.advanced.properties",
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
		val resources = new Resource[]{new ClassPathResource("nl/clockwork/ebms/default.properties"),
				new ClassPathResource("nl/clockwork/ebms/admin/default.properties"), new ClassPathResource("nl/clockwork/ebms/cache/ignite/default.properties"),
				new FileSystemResource(configDir + "ebms-admin.embedded.advanced.properties"), new FileSystemResource(configDir + "ebms-admin.embedded.properties")};
		result.setLocations(resources);
		result.setIgnoreResourceNotFound(true);
		return result;
	}
}
