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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.apache.cxf.common.util.StringUtils;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.resource.IResourceStream;

import lombok.val;
import lombok.extern.apachecommons.CommonsLog;
import nl.clockwork.ebms.admin.web.Utils;
import nl.clockwork.ebms.admin.web.message.ByteArrayResourceStream;
import nl.clockwork.ebms.common.JAXBParser;
import nl.clockwork.ebms.service.model.EbMSMessageContent;
import nl.clockwork.ebms.service.model.EbMSMessageContext;

@CommonsLog
public class DownloadEbMSMessageContentLink extends Link<EbMSMessageContent>
{
	private static final long serialVersionUID = 1L;

	public DownloadEbMSMessageContentLink(String id, nl.clockwork.ebms.service.model.EbMSMessageContent messageContent)
	{
		super(id,Model.of(Args.notNull(messageContent,"messageContent")));
	}

	@Override
	public void onClick()
	{
		try
		{
			val messageContent = getModelObject();
			val output = new ByteArrayOutputStream();
			try (val zip = new ZipOutputStream(output))
			{
				writeMessageToZip(messageContent,zip);
			}
			val resourceStream = new ByteArrayResourceStream(output,"application/zip");
			getRequestCycle().scheduleRequestHandlerAfterCurrent(createRequestHandler(messageContent,resourceStream));
		}
		catch (IOException e)
		{
			log.error("",e);
			error(e.getMessage());
		}
		catch (JAXBException e)
		{
			log.error("",e);
			error(e.getMessage());
		}
	}

	private void writeMessageToZip(EbMSMessageContent messageContent, ZipOutputStream zip) throws IOException, JAXBException
	{
		val entry = new ZipEntry("messageContext.xml");
		zip.putNextEntry(entry);
		zip.write(JAXBParser.getInstance(EbMSMessageContext.class).handle(new JAXBElement<>(new QName("http://www.clockwork.nl/ebms/2.0","messageContext"),EbMSMessageContext.class,messageContent.getContext())).getBytes());
		zip.closeEntry();
		for (val dataSource: messageContent.getDataSources())
		{
			val e = new ZipEntry("datasources/" + (StringUtils.isEmpty(dataSource.getName()) ? UUID.randomUUID() + Utils.getFileExtension(dataSource.getContentType()) : dataSource.getName()));
			entry.setComment("Content-Type: " + dataSource.getContentType());
			zip.putNextEntry(e);
			zip.write(dataSource.getContent());
			zip.closeEntry();
		}
	}

	private ResourceStreamRequestHandler createRequestHandler(EbMSMessageContent messageContent, IResourceStream resourceStream)
	{
		return new ResourceStreamRequestHandler(resourceStream)
				.setFileName("messageContent." + messageContent.getContext().getMessageId() + ".zip")
				.setContentDisposition(ContentDisposition.ATTACHMENT);
	}

}
