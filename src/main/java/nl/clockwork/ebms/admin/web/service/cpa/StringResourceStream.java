package nl.clockwork.ebms.admin.web.service.cpa;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.resource.AbstractResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;

public class StringResourceStream extends AbstractResourceStream
{
	private static final long serialVersionUID = 1L;
	private String s;
	private String contentType;

	public StringResourceStream(String s, String contentType)
	{
		this.s = s;
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
		return Bytes.bytes(s.length());
	}
	
	@Override
	public InputStream getInputStream() throws ResourceStreamNotFoundException
	{
		return new ByteArrayInputStream(s.getBytes());
	}
	
	@Override
	public void close() throws IOException
	{
	}
}
