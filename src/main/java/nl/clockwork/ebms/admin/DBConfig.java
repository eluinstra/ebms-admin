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


import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import nl.clockwork.ebms.dao.DAOConfig;
import nl.clockwork.ebms.datasource.DataSourceConfig;
import nl.clockwork.ebms.jms.JMSConfig;
import nl.clockwork.ebms.kafka.KafkaConfig;
import nl.clockwork.ebms.querydsl.model.QueryDSLConfig;
import nl.clockwork.ebms.transaction.TransactionManagerConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Import({DAOConfig.class, DataSourceConfig.class, JMSConfig.class, QueryDSLConfig.class, KafkaConfig.class, TransactionManagerConfig.class})
@PropertySource(
		value = {"classpath:nl/clockwork/ebms/default.properties", "classpath:nl/clockwork/ebms/admin/default.properties",
				"file:${ebms.configDir}ebms-admin.embedded.advanced.properties", "file:${ebms.configDir}ebms-admin.embedded.properties"},
		ignoreResourceNotFound = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DBConfig
{
}
