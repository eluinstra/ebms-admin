package nl.clockwork.ebms.admin.web.message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.resource.AbstractResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;

public class ZipResourceStream extends AbstractResourceStream
{
	private static final long serialVersionUID = 1L;
	private ByteArrayOutputStream zipStream;
	
	public ZipResourceStream(ByteArrayOutputStream zipStream)
	{
		this.zipStream = zipStream;
	}

	@Override
	public String getContentType()
	{
		return "application/zip";
	}
	
	@Override
	public Bytes length()
	{
		return Bytes.bytes(zipStream.size());
	}
	
	@Override
	public InputStream getInputStream() throws ResourceStreamNotFoundException
	{
		return new ByteArrayInputStream(zipStream.toByteArray());
	}
	
	@Override
	public void close() throws IOException
	{
	}
}
