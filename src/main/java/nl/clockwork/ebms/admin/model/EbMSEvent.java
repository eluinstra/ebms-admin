/**
 * Copyright 2013 Clockwork
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
