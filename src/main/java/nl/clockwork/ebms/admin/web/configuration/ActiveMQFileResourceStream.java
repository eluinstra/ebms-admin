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
package nl.clockwork.ebms.admin.web.configuration;

import java.net.URISyntaxException;
import org.apache.wicket.core.util.resource.PackageResourceStream;

public class ActiveMQFileResourceStream extends PackageResourceStream
{
	private static final long serialVersionUID = 1L;

	public ActiveMQFileResourceStream() throws URISyntaxException
	{
		super(ActiveMQFileResourceStream.class, "/nl/clockwork/ebms/activemq.xml");
	}

	@Override
	public String getContentType()
	{
		return "text/xml";
	}
}