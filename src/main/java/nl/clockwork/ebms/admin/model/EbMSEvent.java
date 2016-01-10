package nl.clockwork.ebms.admin.model;

import java.util.Date;

import org.apache.wicket.util.io.IClusterable;

public class EbMSEvent  implements IClusterable
{
	private static final long serialVersionUID = 1L;
	private Date timeToLive;
	private Date timestamp;
	private int retries;

	public EbMSEvent(Date timeToLive, Date timestamp, int retries)
	{
		this.timeToLive = timeToLive;
		this.timestamp = timestamp;
		this.retries = retries;
	}

	public Date getTimeToLive()
	{
		return timeToLive;
	}

	public void setTimeToLive(Date timeToLive)
	{
		this.timeToLive = timeToLive;
	}

	public Date getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp(Date timestamp)
	{
		this.timestamp = timestamp;
	}

	public int getRetries()
	{
		return retries;
	}

	public void setRetries(int retries)
	{
		this.retries = retries;
	}

	public static long getSerialversionuid()
	{
		return serialVersionUID;
	}

}
