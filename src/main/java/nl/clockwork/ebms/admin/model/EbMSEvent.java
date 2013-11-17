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

import nl.clockwork.ebms.Constants.EbMSEventStatus;
import nl.clockwork.ebms.Constants.EbMSEventType;

public class EbMSEvent implements IClusterable
{
	private static final long serialVersionUID = 1L;
	private EbMSMessage message;
	private Date time;
	private EbMSEventType type;
	private EbMSEventStatus status;
	private Date statusTime;
	private String errorMessage;

	public EbMSEvent()
	{
	}
	
	public EbMSEvent(Date time, EbMSEventType type, EbMSEventStatus status, Date statusTime, String errorMessage)
	{
		this.time = time;
		this.type = type;
		this.status = status;
		this.statusTime = statusTime;
		this.errorMessage = errorMessage;
	}

	public EbMSMessage getMessage()
	{
		return message;
	}
	public void setMessage(EbMSMessage message)
	{
		this.message = message;
	}
	public Date getTime()
	{
		return time;
	}
	public void setTime(Date time)
	{
		this.time = time;
	}
	public EbMSEventType getType()
	{
		return type;
	}
	public void setType(EbMSEventType type)
	{
		this.type = type;
	}
	public EbMSEventStatus getStatus()
	{
		return status;
	}
	public void setStatus(EbMSEventStatus status)
	{
		this.status = status;
	}
	public Date getStatusTime()
	{
		return statusTime;
	}
	public void setStatusTime(Date statusTime)
	{
		this.statusTime = statusTime;
	}
	public String getErrorMessage()
	{
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage)
	{
		this.errorMessage = errorMessage;
	}

}
