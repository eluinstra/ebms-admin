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

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Properties;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.springframework.core.io.Resource;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class PropertySourcesPlaceholderConfigurer extends org.springframework.context.support.PropertySourcesPlaceholderConfigurer
{
	Resource overridePropertiesFile;

	@Override
	public void setLocations(Resource...locations)
	{
		overridePropertiesFile = locations[locations.length - 1];
		super.setLocations(locations);
	}

	public Resource getOverridePropertiesFile()
	{
		return overridePropertiesFile;
	}

	public Properties getProperties() throws IOException
	{
		val properties = mergeProperties();
		val result = new Properties();
		result.putAll(
				properties.entrySet()
						.stream()
						.map(
								e -> System.getProperty((String)e.getKey()) == null
										? e
										: new AbstractMap.SimpleEntry<String, String>((String)e.getKey(), System.getProperty((String)e.getKey())))
						.map(
								e -> System.getenv((String)e.getKey()) == null
										? e
										: new AbstractMap.SimpleEntry<String, String>((String)e.getKey(), System.getenv((String)e.getKey())))
						.map(
								e -> System.getenv(((String)e.getKey()).replaceAll("\\.", "_")) == null
										? e
										: new AbstractMap.SimpleEntry<String, String>((String)e.getKey(), System.getenv(((String)e.getKey()).replaceAll("\\.", "_"))))
						.collect(Collectors.toMap(e -> (String)e.getKey(), e -> (String)e.getValue())));
		return result;
	}
}
