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

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;

import lombok.val;

public class Log4j2Configurer
{
	public static void initLogging(String uri) throws URISyntaxException
	{
		if (StringUtils.isNotEmpty(uri))
		{
			val context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(true);
			context.setConfigLocation(new URI(uri));
		}
	}

}
