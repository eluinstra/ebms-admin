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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.bind.JAXBException;

import nl.clockwork.ebms.admin.web.Utils;
import nl.clockwork.ebms.common.XMLMessageBuilder;
import nl.clockwork.ebms.model.EbMSMessageContent;
import nl.clockwork.ebms.model.EbMSMessageContext;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.resource.AbstractResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;

public class DownloadEbMSMessageContentLink extends Link<Void>
{
	private static final long serialVersionUID = 1L;
	private nl.clockwork.ebms.model.EbMSMessageContent messageContent;

	public DownloadEbMSMessageContentLink(String id, nl.clockwork.ebms.model.EbMSMessageContent messageContent)
	{
		super(id);//,Model.of(Args.notNull(messageContent,"messageContent"))
		this.messageContent = messageContent;
	}

	@Override
	public void onClick()
	{
		try
		{
			//EbMSMessageContent messageContent = getModelObject();
			final ByteArrayOutputStream output = new ByteArrayOutputStream();
			ZipOutputStream zip = new ZipOutputStream(output);
			writeMessageToZip(messageContent,zip);
			zip.close();

			IResourceStream resourceStream = new AbstractResourceStream()
			{
				private static final long serialVersionUID = 1L;

				@Override
				public String getContentType()
				{
					return "application/zip";
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
				.setFileName("messageContent." + messageContent.getContext().getMessageId() + ".zip")
				.setContentDisposition(ContentDisposition.ATTACHMENT)
			);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		catch (JAXBException e)
		{
			throw new RuntimeException(e);
		}
	}

	private void writeMessageToZip(EbMSMessageContent messageContent, ZipOutputStream zip) throws IOException, JAXBException
	{
		ZipEntry entry = new ZipEntry("messageContext.xml");
		zip.putNextEntry(entry);
		zip.write(XMLMessageBuilder.getInstance(EbMSMessageContext.class).handle(messageContent.getContext()).getBytes());
		zip.closeEntry();
		for (nl.clockwork.ebms.model.EbMSDataSource dataSource : messageContent.getDataSources())
		{
			entry = new ZipEntry("datasources/" + (dataSource.getName() == null ? UUID.randomUUID() + Utils.getFileExtension(dataSource.getContentType()) : dataSource.getName()));
			entry.setComment("Content-Type: " + dataSource.getContentType());
			zip.putNextEntry(entry);
			zip.write(dataSource.getContent());
			zip.closeEntry();
		}
	}

}
