/*
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

import java.io.IOException;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import nl.clockwork.ebms.admin.web.Utils;
import nl.clockwork.ebms.admin.web.message.CachedOutputResourceStream;
import nl.clockwork.ebms.jaxb.JAXBParser;
import nl.clockwork.ebms.service.model.Message;
import nl.clockwork.ebms.service.model.MessageProperties;
import org.apache.cxf.common.util.StringUtils;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.resource.IResourceStream;

@Slf4j
public class DownloadEbMSMessageLink extends Link<Message>
{
	private static final long serialVersionUID = 1L;

	public DownloadEbMSMessageLink(String id, IModel<Message> model)
	{
		super(id, Args.notNull(model, "message"));
	}

	@Override
	public void onClick()
	{
		val o = getModelObject();
		try (val output = new CachedOutputStream(); val zip = new ZipOutputStream(output))
		{
			writeMessageToZip(o, zip);
			val resourceStream = CachedOutputResourceStream.of(output, "application/zip");
			getRequestCycle().scheduleRequestHandlerAfterCurrent(createRequestHandler(o, resourceStream));
		}
		catch (IOException e)
		{
			log.error("", e);
			error(e.getMessage());
		}
		catch (JAXBException e)
		{
			log.error("", e);
			error(e.getMessage());
		}
	}

	private void writeMessageToZip(Message message, ZipOutputStream zip) throws IOException, JAXBException
	{
		val entry = new ZipEntry("messageProperties.xml");
		zip.putNextEntry(entry);
		zip.write(
				JAXBParser.getInstance(MessageProperties.class)
						.handle(new JAXBElement<>(new QName("http://www.ordina.nl/ebms/2.18", "messageProperties"), MessageProperties.class, message.getProperties()))
						.getBytes());
		zip.closeEntry();
		for (val dataSource : message.getDataSources())
		{
			val e = new ZipEntry(
					"datasources/"
							+ (StringUtils.isEmpty(dataSource.getName()) ? UUID.randomUUID() + Utils.getFileExtension(dataSource.getContentType()) : dataSource.getName()));
			entry.setComment("Content-Type: " + dataSource.getContentType());
			zip.putNextEntry(e);
			zip.write(dataSource.getContent());
			zip.closeEntry();
		}
		zip.finish();
	}

	private ResourceStreamRequestHandler createRequestHandler(Message messageContent, IResourceStream resourceStream)
	{
		return new ResourceStreamRequestHandler(resourceStream).setFileName("message." + messageContent.getProperties().getMessageId() + ".zip")
				.setContentDisposition(ContentDisposition.ATTACHMENT);
	}

}
