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
package nl.clockwork.ebms.admin.web.service.message;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import nl.clockwork.ebms.admin.web.Utils;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.encoding.UrlEncoder;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.resource.AbstractResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;

public class DownloadEbMSDataSourceLink extends Link<Void>
{
	private static final long serialVersionUID = 1L;
	private nl.clockwork.ebms.model.EbMSDataSource ebMSDataSource;

	public DownloadEbMSDataSourceLink(String id, nl.clockwork.ebms.model.EbMSDataSource ebMSDataSource)
	{
		super(id);//,Model.of(Args.notNull(new EbMSDataSource(ebMSDataSource),"ebMSDataSource"))
		this.ebMSDataSource = ebMSDataSource;
	}

	@Override
	public void onClick()
	{
		//final EbMSDataSource ebMSDataSource = getModelObject();
		String fileName = UrlEncoder.QUERY_INSTANCE.encode(ebMSDataSource.getName() == null ? "ebMSDataSource" + Utils.getFileExtension(ebMSDataSource.getContentType()) : ebMSDataSource.getName(),getRequest().getCharset());
		IResourceStream resourceStream = new AbstractResourceStream()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public String getContentType()
			{
				return ebMSDataSource.getContentType();
			}
			
			@Override
			public Bytes length()
			{
				return Bytes.bytes(ebMSDataSource.getContent().length);
			}
			
			@Override
			public InputStream getInputStream() throws ResourceStreamNotFoundException
			{
				return new ByteArrayInputStream(ebMSDataSource.getContent());
			}
			
			@Override
			public void close() throws IOException
			{
			}
		}; 

		getRequestCycle().scheduleRequestHandlerAfterCurrent(
			new ResourceStreamRequestHandler(resourceStream)
			{
				@Override
				public void respond(IRequestCycle requestCycle)
				{
					super.respond(requestCycle);
				}
			}
			.setFileName(fileName)
			.setContentDisposition(ContentDisposition.ATTACHMENT)
		);
	}

}
