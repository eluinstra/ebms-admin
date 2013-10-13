package nl.clockwork.ebms.admin.web.service.message;

import java.io.Serializable;

public class EbMSDataSource extends nl.clockwork.ebms.model.EbMSDataSource implements Serializable
{ 
	private static final long serialVersionUID = 1L;
	public EbMSDataSource(nl.clockwork.ebms.model.EbMSDataSource ebMSDataSource)
	{
		super(ebMSDataSource.getName(),ebMSDataSource.getContentType(),ebMSDataSource.getContent());
	}
}
