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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.common.util.StringUtils;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.resource.IResourceStream;

import nl.clockwork.ebms.admin.model.EbMSAttachment;
import nl.clockwork.ebms.admin.model.EbMSMessage;
import nl.clockwork.ebms.admin.web.Utils;

public class DownloadEbMSMessageLinkX extends Link<EbMSMessage>
{
	private static final long serialVersionUID = 1L;
	protected transient Log logger = LogFactory.getLog(this.getClass());

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
			try (ZipOutputStream zip = new ZipOutputStream(output))
			{
				writeMessageToZip(message,zip);
			}
			IResourceStream resourceStream = new ByteArrayResourceStream(output,"application/zip");
			getRequestCycle().scheduleRequestHandlerAfterCurrent(createRequestHandler(message,resourceStream));
		}
		catch (IOException e)
		{
			logger.error("",e);
			error(e.getMessage());
		}
	}

	private void writeMessageToZip(EbMSMessage message, ZipOutputStream zip) throws IOException
	{
		ZipEntry entry = new ZipEntry("message.xml");
		zip.putNextEntry(entry);
		zip.write(message.getContent().getBytes());
		zip.closeEntry();
		for (EbMSAttachment a: message.getAttachments())
		{
			ZipEntry e = new ZipEntry("attachments/" + (StringUtils.isEmpty(a.getName()) ? a.getContentId() + Utils.getFileExtension(a.getContentType()) : a.getName()));
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
