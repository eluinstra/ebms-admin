package nl.clockwork.ebms.admin.web;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import nl.clockwork.ebms.admin.model.EbMSAttachment;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.encoding.UrlEncoder;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.resource.AbstractResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;

public class DownloadEbMSAttachmentLink extends Link<EbMSAttachment>
{
	private static final long serialVersionUID = 1L;

	public DownloadEbMSAttachmentLink(String id, EbMSAttachment attachment)
	{
		this(id,Model.of(Args.notNull(attachment,"attachment")));
	}

	public DownloadEbMSAttachmentLink(String id, IModel<EbMSAttachment> attachmentModel)
	{
		super(id,attachmentModel);
	}

	@Override
	public void onClick()
	{
		final EbMSAttachment attachment = getModelObject();
		String fileName = UrlEncoder.QUERY_INSTANCE.encode(attachment.getName() == null ? "attachment" + Utils.getFileExtension(attachment.getContentType()) : attachment.getName(),getRequest().getCharset());
		IResourceStream resourceStream = new AbstractResourceStream()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public String getContentType()
			{
				return attachment.getContentType();
			}
			
			@Override
			public Bytes length()
			{
				return Bytes.bytes(attachment.getContent().length);
			}
			
			@Override
			public InputStream getInputStream() throws ResourceStreamNotFoundException
			{
				//TODO remove content from object and get it from database instead
				return new ByteArrayInputStream(attachment.getContent());
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
			.setFileName(fileName)
			.setContentDisposition(ContentDisposition.ATTACHMENT)
		);
	}

}
