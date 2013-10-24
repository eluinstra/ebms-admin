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
package nl.clockwork.ebms.admin.web.message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import nl.clockwork.ebms.admin.dao.EbMSDAO;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.resource.AbstractResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;

public class DownloadEbMSMessagesCSVLink extends Link<Void>
{
	private static final long serialVersionUID = 1L;
	protected transient Log logger = LogFactory.getLog(this.getClass());
	private EbMSDAO ebMSDAO;
	private EbMSMessageFilter filter;

	public DownloadEbMSMessagesCSVLink(String id, EbMSDAO ebMSDAO, EbMSMessageFilter filter)
	{
		super(id);
		this.ebMSDAO = ebMSDAO;
		this.filter = filter;
	}

	@Override
	public void onClick()
	{
		try
		{
			final ByteArrayOutputStream output = new ByteArrayOutputStream();
			CSVPrinter printer = new CSVPrinter(new OutputStreamWriter(output),CSVFormat.DEFAULT);
			ebMSDAO.printMessagesToCSV(printer,filter);
			printer.close();

			IResourceStream resourceStream = new AbstractResourceStream()
			{
				private static final long serialVersionUID = 1L;

				@Override
				public String getContentType()
				{
					return "text/csv";
				}
				
				@Override
				public Bytes length()
				{
					return Bytes.bytes(output.size());
				}
				
				@Override
				public InputStream getInputStream() throws ResourceStreamNotFoundException
				{
					return new ByteArrayInputStream(output.toByteArray());
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
				.setFileName("messages.csv")
				.setContentDisposition(ContentDisposition.ATTACHMENT)
			);
		}
		catch (IOException e)
		{
			logger.error("",e);
			error(e.getMessage());
		}
	}

}
