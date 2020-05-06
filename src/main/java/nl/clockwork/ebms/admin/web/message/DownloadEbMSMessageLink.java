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
import java.util.zip.ZipOutputStream;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.resource.IResourceStream;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;
import lombok.extern.apachecommons.CommonsLog;
import nl.clockwork.ebms.admin.dao.EbMSDAO;
import nl.clockwork.ebms.admin.model.EbMSMessage;

@CommonsLog
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DownloadEbMSMessageLink extends Link<Void>
{
	private static final long serialVersionUID = 1L;
	@NonNull
	EbMSDAO ebMSDAO;
	@NonNull
	String messageId;
	int messageNr;

	public DownloadEbMSMessageLink(String id, EbMSDAO ebMSDAO, EbMSMessage message)
	{
		this(id,ebMSDAO,message.getMessageId(),message.getMessageNr());
	}

	public DownloadEbMSMessageLink(String id, EbMSDAO ebMSDAO, String messageId, int messageNr)
	{
		super(id,null);
		this.ebMSDAO = ebMSDAO;
		this.messageId = messageId;
		this.messageNr = messageNr;
	}

	@Override
	public void onClick()
	{
		try
		{
			val output = new ByteArrayOutputStream();
			try (val zip = new ZipOutputStream(output))
			{
				ebMSDAO.writeMessageToZip(messageId, messageNr,zip);
			}
			val resourceStream = ByteArrayResourceStream.of(output,"application/zip");
			getRequestCycle().scheduleRequestHandlerAfterCurrent(createRequestHandler(resourceStream));
		}
		catch (IOException e)
		{
			log.error("",e);
			error(e.getMessage());
		}
	}

	private ResourceStreamRequestHandler createRequestHandler(IResourceStream resourceStream)
	{
		return new ResourceStreamRequestHandler(resourceStream)
				.setFileName("message." + messageId + "." + messageNr + ".zip")
				.setContentDisposition(ContentDisposition.ATTACHMENT);
	}

}
