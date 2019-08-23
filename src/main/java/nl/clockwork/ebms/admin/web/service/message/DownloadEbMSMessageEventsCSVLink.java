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
import java.io.OutputStreamWriter;
import java.util.List;

import nl.clockwork.ebms.Constants.EbMSMessageEventType;
import nl.clockwork.ebms.ThrowingConsumer;
import nl.clockwork.ebms.admin.Utils;
import nl.clockwork.ebms.admin.web.message.ByteArrayResourceStream;
import nl.clockwork.ebms.model.EbMSMessageContext;
import nl.clockwork.ebms.model.EbMSMessageEvent;
import nl.clockwork.ebms.service.EbMSMessageService;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.resource.IResourceStream;

public class DownloadEbMSMessageEventsCSVLink extends Link<Void>
{
	private static final long serialVersionUID = 1L;
	protected transient Log logger = LogFactory.getLog(this.getClass());
	private EbMSMessageService ebMSMessageService;
	private EbMSMessageContext filter;
	private EbMSMessageEventType[] eventTypes;

	public DownloadEbMSMessageEventsCSVLink(String id, EbMSMessageService ebMSMessageService, EbMSMessageContext filter, EbMSMessageEventType...eventTypes)
	{
		super(id);
		this.ebMSMessageService = ebMSMessageService;
		this.filter = filter;
		this.eventTypes = eventTypes;
	}

	@Override
	public void onClick()
	{
		try
		{
			final ByteArrayOutputStream output = new ByteArrayOutputStream();
			try (CSVPrinter printer = new CSVPrinter(new OutputStreamWriter(output),CSVFormat.DEFAULT))
			{
				List<EbMSMessageEvent> messageEvents = Utils.toList(ebMSMessageService.getMessageEvents(filter,eventTypes,null));
				if (messageEvents != null)
					printMessagesToCSV(printer,messageEvents);
			}
			IResourceStream resourceStream = new ByteArrayResourceStream(output,"text/csv");
			getRequestCycle().scheduleRequestHandlerAfterCurrent(createRequestHandler(resourceStream));
		}
		catch (IOException e)
		{
			logger.error("",e);
			error(e.getMessage());
		}
	}

	private void printMessagesToCSV(CSVPrinter printer, List<EbMSMessageEvent> messageEvents) throws IOException
	{
		messageEvents.forEach(ThrowingConsumer.throwingConsumerWrapper(e -> printer.printRecord(e.getMessageId(),e.getType().name())));
	}

	private ResourceStreamRequestHandler createRequestHandler(IResourceStream resourceStream)
	{
		return new ResourceStreamRequestHandler(resourceStream)
		.setFileName("messages.csv")
		.setContentDisposition(ContentDisposition.ATTACHMENT);
	}

}
