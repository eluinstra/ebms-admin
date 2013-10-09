package nl.clockwork.ebms.admin.web;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import nl.clockwork.ebms.admin.model.EbMSAttachment;
import nl.clockwork.ebms.admin.model.EbMSMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.resource.AbstractResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;

public class DownloadEbMSMessageLink extends Link<Void>
{
	protected transient Log logger = LogFactory.getLog(this.getClass());
	private static final long serialVersionUID = 1L;
	private EbMSMessage message;

	public DownloadEbMSMessageLink(String id, EbMSMessage message)
	{
		super(id,null);
		this.message = message;
	}

	@Override
	public void onClick()
	{
		try
		{
			final ByteArrayOutputStream output = new ByteArrayOutputStream();
			ZipOutputStream zip = new ZipOutputStream(output);
			writeMessageToZip(message,zip);

			IResourceStream resourceStream = new AbstractResourceStream()
			{
				private static final long serialVersionUID = 1L;

				@Override
				public String getContentType()
				{
					return "application/zip";
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
				.setFileName("EbMSMessage.zip")
				.setContentDisposition(ContentDisposition.ATTACHMENT)
			);
		}
		catch (IOException e)
		{
			logger.error("",e);
		}
	}

	private void writeMessageToZip(EbMSMessage message, ZipOutputStream zip) throws IOException
	{
		ZipEntry entry = new ZipEntry("message.xml");
		zip.putNextEntry(entry);
		zip.write(message.getContent().getBytes());
		zip.closeEntry();
		for (EbMSAttachment attachment : message.getAttachments())
		{
			entry = new ZipEntry("attachments/" + (attachment.getName() == null ? attachment.getContentId() + Utils.getFileExtension(attachment.getContentType()) : attachment.getName()));
			entry.setComment("Content-Type: " + attachment.getContentType());
			zip.putNextEntry(entry);
			zip.write(attachment.getContent());
			zip.closeEntry();
		}
		zip.close();
	}

}
