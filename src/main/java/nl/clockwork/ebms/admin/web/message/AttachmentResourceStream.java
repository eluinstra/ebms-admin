package nl.clockwork.ebms.admin.web.message;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import nl.clockwork.ebms.admin.model.EbMSAttachment;

import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.resource.AbstractResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;

public class AttachmentResourceStream extends AbstractResourceStream
{
	private static final long serialVersionUID = 1L;
	private EbMSAttachment attachment;

	public AttachmentResourceStream(EbMSAttachment attachment)
	{
		this.attachment = attachment;
	}

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
		return new ByteArrayInputStream(attachment.getContent());
	}
	
	@Override
	public void close() throws IOException
	{
	}
}