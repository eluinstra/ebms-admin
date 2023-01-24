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


import lombok.val;
import org.apache.activemq.xbean.BrokerFactoryBean;
import org.springframework.core.io.ClassPathResource;

public class JmsBroker
{
	public static void main(String[] args) throws Exception
	{
		// System.setProperty("activemq.base",System.getProperty("user.dir"));
		val result = new BrokerFactoryBean(new ClassPathResource("nl/clockwork/ebms/activemq.xml"));
		result.setStart(true);
		result.afterPropertiesSet();
		System.out.println("Broker started");
		Thread.currentThread().join();
	}
}
