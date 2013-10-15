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
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import nl.clockwork.ebms.admin.model.EbMSAttachment;
import nl.clockwork.ebms.admin.model.EbMSMessage;
import nl.clockwork.ebms.admin.web.Utils;

import org.apache.cxf.common.util.StringUtils;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.resource.AbstractResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;

public class DownloadEbMSMessageLinkX extends Link<EbMSMessage>
{
	private static final long serialVersionUID = 1L;

	public DownloadEbMSMessageLinkX(String id, EbMSMessage message)
	{
		super(id,Model.of(Args.notNull(message,"message")));
	}

	@Override
	public void onClick()
	{
		try
		{
			EbMSMessage message = getModelObject();
			final ByteArrayOutputStream output = new ByteArrayOutputStream();
			ZipOutputStream zip = new ZipOutputStream(output);
			writeMessageToZip(message,zip);
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
				.setFileName("message." + message.getMessageId() + "." + message.getMessageNr() + ".zip")
				.setContentDisposition(ContentDisposition.ATTACHMENT)
			);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	private void writeMessageToZip(EbMSMessage message, ZipOutputStream zip) throws IOException
	{
		ZipEntry entry = new ZipEntry("message.xml");
		zip.putNextEntry(entry);
		zip.write(message.getContent().getBytes());
		zip.closeEntry();
		for (EbMSAttachment attachment : message.getAttachments())
		{
			entry = new ZipEntry("attachments/" + (StringUtils.isEmpty(attachment.getName()) ? attachment.getContentId() + Utils.getFileExtension(attachment.getContentType()) : attachment.getName()));
			entry.setComment("Content-Type: " + attachment.getContentType());
			zip.putNextEntry(entry);
			zip.write(attachment.getContent());
			zip.closeEntry();
		}
	}

}
