package nl.clockwork.ebms.admin.web;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import nl.clockwork.ebms.admin.dao.EbMSDAO;
import nl.clockwork.ebms.admin.web.message.EbMSMessageFilter;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.resource.AbstractResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;

public class DownloadEbMSMessagesCSVLink extends Link<Void>
{
	private static final long serialVersionUID = 1L;
	private EbMSDAO ebMSDAO;
	private EbMSMessageFilter filter;

	public DownloadEbMSMessagesCSVLink(String id, EbMSDAO ebMSDAO, EbMSMessageFilter filter)
	{
		super(id,null);
		this.ebMSDAO = ebMSDAO;
		this.filter = filter;
	}

	@Override
	public void onClick()
	{
		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		CSVPrinter printer = new CSVPrinter(new OutputStreamWriter(output),CSVFormat.DEFAULT);
		ebMSDAO.printMessagesToCSV(printer,filter);

		IResourceStream resourceStream = new AbstractResourceStream()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public String getContentType()
			{
				return "text/csv";
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
			.setFileName("EbMSMessages.csv")
			.setContentDisposition(ContentDisposition.ATTACHMENT)
		);
	}

}
