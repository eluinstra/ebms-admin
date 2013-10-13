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
package nl.clockwork.ebms.admin.web.service.cpa;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import nl.clockwork.ebms.service.CPAService;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.resource.AbstractResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;

public class DownloadCPALink extends Link<String>
{
	private static final long serialVersionUID = 1L;
	private CPAService cpaClient;

	public DownloadCPALink(String id, CPAService cpaClient, String cpaId)
	{
		super(id,Model.of(cpaId));
		this.cpaClient = cpaClient;
	}

	@Override
	public void onClick()
	{
		String cpaId = getModelObject();
		final String cpa = cpaClient.getCPA(cpaId);
		IResourceStream resourceStream = new AbstractResourceStream()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public String getContentType()
			{
				return "text/xml";
			}
			
			@Override
			public Bytes length()
			{
				return Bytes.bytes(cpa.length());
			}
			
			@Override
			public InputStream getInputStream() throws ResourceStreamNotFoundException
			{
				return new ByteArrayInputStream(cpa.getBytes());
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
			.setFileName("cpa." + cpaId + ".xml")
			.setContentDisposition(ContentDisposition.ATTACHMENT)
		);
	}

}
