package nl.clockwork.ebms.admin.web.service.message;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import nl.clockwork.ebms.model.EbMSDataSource;

import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.resource.AbstractResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;

public class EbMSDataSourceResourceStream extends AbstractResourceStream
{
	private static final long serialVersionUID = 1L;
	private EbMSDataSource ebMSDataSource;

	public EbMSDataSourceResourceStream(EbMSDataSource ebMSDataSource)
	{
		this.ebMSDataSource = ebMSDataSource;
	}

	@Override
	public String getContentType()
	{
		return ebMSDataSource.getContentType();
	}
	
	@Override
	public Bytes length()
	{
		return Bytes.bytes(ebMSDataSource.getContent().length);
	}
	
	@Override
	public InputStream getInputStream() throws ResourceStreamNotFoundException
	{
		return new ByteArrayInputStream(ebMSDataSource.getContent());
	}
	
	@Override
	public void close() throws IOException
	{
	}
}
