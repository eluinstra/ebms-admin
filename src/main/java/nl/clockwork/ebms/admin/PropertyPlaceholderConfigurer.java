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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.io.Resource;

public class PropertyPlaceholderConfigurer extends org.springframework.beans.factory.config.PropertyPlaceholderConfigurer
{
	private Resource overridePropertiesFile;
	private Map<String,String> properties;

	@Override
	public void setLocations(Resource[] locations)
	{
		overridePropertiesFile = locations[locations.length - 1];
		super.setLocations(locations);
	}
	
	@Override
	protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties properties) throws BeansException
	{
		super.processProperties(beanFactoryToProcess,properties);
		this.properties = new HashMap<String,String>();
		for (Object key: properties.keySet())
			this.properties.put(key.toString(),resolvePlaceholder(key.toString(),properties));
	}
	
	@Override
	protected void convertProperties(Properties properties)
	{
		super.convertProperties(properties);
	}

	public Resource getOverridePropertiesFile()
	{
		return overridePropertiesFile;
	}
	
	public Map<String,String> getProperties()
	{
		return Collections.unmodifiableMap(properties);
	}
	
	public String getProperty(String name)
	{
		return properties.get(name);
	}
}
