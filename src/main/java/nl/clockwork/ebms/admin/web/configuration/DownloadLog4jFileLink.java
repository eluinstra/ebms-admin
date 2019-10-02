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
package nl.clockwork.ebms.admin.web.configuration;

import java.net.URISyntaxException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.encoding.UrlEncoder;
import org.apache.wicket.util.resource.IResourceStream;

public class DownloadLog4jFileLink extends Link<Void>
{
	private static final long serialVersionUID = 1L;
	protected transient Log logger = LogFactory.getLog(getClass());

	public DownloadLog4jFileLink(String id)
	{
		super(id,null);
	}

	@Override
	public void onClick()
	{
		try
		{
			String fileName = UrlEncoder.QUERY_INSTANCE.encode("log4j2.xml",getRequest().getCharset());
			IResourceStream resourceStream = new XMLFileResourceStream("/log4j2.xml");
			getRequestCycle().scheduleRequestHandlerAfterCurrent(createRequestHandler(fileName,resourceStream));
		}
		catch (URISyntaxException e)
		{
			logger.error("",e);
			error(e);
		}
	}

	private ResourceStreamRequestHandler createRequestHandler(String fileName, IResourceStream resourceStream)
	{
		return new ResourceStreamRequestHandler(resourceStream)
		.setFileName(fileName)
		.setContentDisposition(ContentDisposition.ATTACHMENT);
	}

}
