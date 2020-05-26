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

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.resource.IResourceStream;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nl.clockwork.ebms.admin.Utils;
import nl.clockwork.ebms.admin.web.message.ByteArrayResourceStream;
import nl.clockwork.ebms.service.EbMSMessageService;
import nl.clockwork.ebms.service.model.EbMSMessageContext;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DownloadEbMSMessageIdsCSVLink extends Link<Void>
{
	private static final long serialVersionUID = 1L;
	@NonNull
	private EbMSMessageService ebMSMessageService;
	private IModel<EbMSMessageContext> filter;

	public DownloadEbMSMessageIdsCSVLink(String id, @NonNull EbMSMessageService ebMSMessageService, @NonNull IModel<EbMSMessageContext> filter)
	{
		super(id);
		this.ebMSMessageService = ebMSMessageService;
		this.filter = filter;
	}

	@Override
	public void onClick()
	{
		try
		{
			val output = new ByteArrayOutputStream();
			try (val printer = new CSVPrinter(new OutputStreamWriter(output),CSVFormat.DEFAULT))
			{
				val messageIds = Utils.toList(ebMSMessageService.getMessageIds(filter.getObject(),null));
				if (messageIds != null)
					printMessagesToCSV(printer,messageIds);
			}
			val resourceStream = ByteArrayResourceStream.of(output,"text/csv");
			getRequestCycle().scheduleRequestHandlerAfterCurrent(createRequestHandler(resourceStream));
		}
		catch (IOException e)
		{
			log.error("",e);
			error(e.getMessage());
		}
	}

	private void printMessagesToCSV(CSVPrinter printer, List<String> messageIds) throws IOException
	{
		for (val id: messageIds)
			printer.printRecord(id);
	}

	private ResourceStreamRequestHandler createRequestHandler(IResourceStream resourceStream)
	{
		return new ResourceStreamRequestHandler(resourceStream)
				.setFileName("messages.csv")
				.setContentDisposition(ContentDisposition.ATTACHMENT);
	}

}
