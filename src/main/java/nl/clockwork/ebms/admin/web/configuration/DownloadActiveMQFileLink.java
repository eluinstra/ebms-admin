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
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.encoding.UrlEncoder;
import org.apache.wicket.util.resource.IResourceStream;

@Slf4j
public class DownloadActiveMQFileLink extends Link<Void>
{
	private static final long serialVersionUID = 1L;

	public DownloadActiveMQFileLink(String id)
	{
		super(id,null);
	}

	@Override
	public void onClick()
	{
		try
		{
			val fileName = UrlEncoder.QUERY_INSTANCE.encode("activemq.xml",getRequest().getCharset());
			val resourceStream = new ActiveMQFileResourceStream();
			getRequestCycle().scheduleRequestHandlerAfterCurrent(createRequestHandler(fileName,resourceStream));
		}
		catch (URISyntaxException e)
		{
			log.error("",e);
			error(e);
		}
	}

	private ResourceStreamRequestHandler createRequestHandler(String fileName, IResourceStream resourceStream)
	{
		return new ResourceStreamRequestHandler(resourceStream).setFileName(fileName).setContentDisposition(ContentDisposition.ATTACHMENT);
	}
}
