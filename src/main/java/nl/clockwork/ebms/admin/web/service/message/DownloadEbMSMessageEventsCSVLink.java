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
import java.io.OutputStreamWriter;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import nl.clockwork.ebms.admin.Utils;
import nl.clockwork.ebms.admin.web.message.CachedOutputResourceStream;
import nl.clockwork.ebms.event.MessageEventType;
import nl.clockwork.ebms.service.EbMSMessageService;
import nl.clockwork.ebms.service.model.MessageEvent;
import nl.clockwork.ebms.service.model.MessageFilter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.resource.IResourceStream;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DownloadEbMSMessageEventsCSVLink extends Link<Void>
{
	private static final long serialVersionUID = 1L;
	@NonNull
	EbMSMessageService ebMSMessageService;
	@NonNull
	IModel<MessageFilter> filter;
	@NonNull
	MessageEventType[] eventTypes;

	@Builder
	public DownloadEbMSMessageEventsCSVLink(
			String id,
			@NonNull EbMSMessageService ebMSMessageService,
			@NonNull IModel<MessageFilter> filter,
			@NonNull MessageEventType...eventTypes)
	{
		super(id);
		this.ebMSMessageService = ebMSMessageService;
		this.filter = filter;
		this.eventTypes = eventTypes;
	}

	@Override
	public void onClick()
	{
		try (val output = new CachedOutputStream(); val printer = new CSVPrinter(new OutputStreamWriter(output), CSVFormat.DEFAULT))
		{
			val messageEvents = Utils.toList(ebMSMessageService.getUnprocessedMessageEvents(filter.getObject(), eventTypes, null));
			if (messageEvents != null)
				printMessagesToCSV(printer, messageEvents);
			val resourceStream = CachedOutputResourceStream.of(output, "text/csv");
			getRequestCycle().scheduleRequestHandlerAfterCurrent(createRequestHandler(resourceStream));
		}
		catch (IOException e)
		{
			log.error("", e);
			error(e.getMessage());
		}
	}

	private void printMessagesToCSV(CSVPrinter printer, List<MessageEvent> messageEvents) throws IOException
	{
		for (val event : messageEvents)
			printer.printRecord(event.getMessageId(), event.getType().name());
	}

	private ResourceStreamRequestHandler createRequestHandler(IResourceStream resourceStream)
	{
		return new ResourceStreamRequestHandler(resourceStream).setFileName("messages.csv").setContentDisposition(ContentDisposition.ATTACHMENT);
	}

}
