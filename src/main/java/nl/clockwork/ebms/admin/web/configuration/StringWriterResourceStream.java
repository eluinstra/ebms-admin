package nl.clockwork.ebms.admin.web.configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.resource.AbstractResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;

public class StringWriterResourceStream extends AbstractResourceStream
{
	private static final long serialVersionUID = 1L;
	private StringWriter writer;
	private String contentType;

	public StringWriterResourceStream(StringWriter writer, String contentType)
	{
		this.writer = writer;
		this.contentType = contentType;
	}

	@Override
	public String getContentType()
	{
		return contentType;
	}
	
	@Override
	public Bytes length()
	{
		return Bytes.bytes(writer.getBuffer().length());
	}
	
	@Override
	public InputStream getInputStream() throws ResourceStreamNotFoundException
	{
		return new ByteArrayInputStream(writer.toString().getBytes());
	}
	
	@Override
	public void close() throws IOException
	{
	}
}
