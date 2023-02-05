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
package nl.clockwork.ebms.admin.web.message;


import java.io.IOException;
import java.io.OutputStreamWriter;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import nl.clockwork.ebms.admin.dao.EbMSDAO;
import nl.clockwork.ebms.admin.web.message.MessageFilterPanel.MessageFilterFormData;
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
public class DownloadEbMSMessagesCSVLink extends Link<MessageFilterFormData>
{
	private static final long serialVersionUID = 1L;
	@NonNull
	EbMSDAO ebMSDAO;

	public DownloadEbMSMessagesCSVLink(String id, EbMSDAO ebMSDAO, IModel<MessageFilterFormData> model)
	{
		super(id, model);
		this.ebMSDAO = ebMSDAO;
	}

	@Override
	public void onClick()
	{
		try (val output = new CachedOutputStream(); val printer = new CSVPrinter(new OutputStreamWriter(output), CSVFormat.DEFAULT))
		{
			ebMSDAO.printMessagesToCSV(printer, getModelObject());
			val resourceStream = CachedOutputResourceStream.of(output, "text/csv");
			getRequestCycle().scheduleRequestHandlerAfterCurrent(createRequestHandler(resourceStream));
		}
		catch (IOException e)
		{
			log.error("", e);
			error(e.getMessage());
		}
	}

	private ResourceStreamRequestHandler createRequestHandler(IResourceStream resourceStream)
	{
		return new ResourceStreamRequestHandler(resourceStream).setFileName("messages.csv").setContentDisposition(ContentDisposition.ATTACHMENT);
	}

}
