package nl.clockwork.ebms.admin.web.message;

import java.io.Serializable;
import java.util.Date;

import nl.clockwork.ebms.model.EbMSMessageContext;

public class EbMSMessageFilter extends EbMSMessageContext implements Serializable
{
	private static final long serialVersionUID = 1L;
	private Boolean mshMessage;
	private Date from;
	private Date to;
	
	public Boolean getMshMessage()
	{
		return mshMessage;
	}
	
	public Date getFrom()
	{
		return from;
	}
	
	public Date getTo()
	{
		return to;
	}
}
