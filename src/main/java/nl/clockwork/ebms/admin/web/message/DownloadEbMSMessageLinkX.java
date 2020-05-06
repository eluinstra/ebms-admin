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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.cxf.common.util.StringUtils;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.resource.IResourceStream;

import lombok.val;
import lombok.extern.apachecommons.CommonsLog;
import nl.clockwork.ebms.admin.model.EbMSMessage;
import nl.clockwork.ebms.admin.web.Utils;

@CommonsLog
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
			val message = getModelObject();
			val output = new ByteArrayOutputStream();
			try (val zip = new ZipOutputStream(output))
			{
				writeMessageToZip(message,zip);
			}
			val resourceStream = ByteArrayResourceStream.of(output,"application/zip");
			getRequestCycle().scheduleRequestHandlerAfterCurrent(createRequestHandler(message,resourceStream));
		}
		catch (IOException e)
		{
			log.error("",e);
			error(e.getMessage());
		}
	}

	private void writeMessageToZip(EbMSMessage message, ZipOutputStream zip) throws IOException
	{
		val entry = new ZipEntry("message.xml");
		zip.putNextEntry(entry);
		zip.write(message.getContent().getBytes());
		zip.closeEntry();
		for (val a: message.getAttachments())
		{
			val e = new ZipEntry("attachments/" + (StringUtils.isEmpty(a.getName()) ? a.getContentId() + Utils.getFileExtension(a.getContentType()) : a.getName()));
			entry.setComment("Content-Type: " + a.getContentType());
			zip.putNextEntry(e);
			zip.write(a.getContent());
			zip.closeEntry();
		}
	}

	private ResourceStreamRequestHandler createRequestHandler(EbMSMessage message, IResourceStream resourceStream)
	{
		return new ResourceStreamRequestHandler(resourceStream)
				.setFileName("message." + message.getMessageId() + "." + message.getMessageNr() + ".zip")
				.setContentDisposition(ContentDisposition.ATTACHMENT);
	}

}
