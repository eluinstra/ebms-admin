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

import nl.clockwork.ebms.Constants.EbMSEventStatus;

import org.apache.wicket.util.io.IClusterable;

public class EbMSEventLog implements IClusterable
{
	private static final long serialVersionUID = 1L;
	private EbMSMessage message;
	private Date timestamp;
	private String uri;
	private EbMSEventStatus status;
	private String errorMessage;

	public EbMSEventLog()
	{
	}

	public EbMSEventLog(Date timestamp, String uri, EbMSEventStatus status, String errorMessage)
	{
		this.timestamp = timestamp;
		this.uri = uri;
		this.status = status;
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

	public Date getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp(Date timestamp)
	{
		this.timestamp = timestamp;
	}

	public String getUri()
	{
		return uri;
	}

	public void setUri(String uri)
	{
		this.uri = uri;
	}

	public EbMSEventStatus getStatus()
	{
		return status;
	}

	public void setStatus(EbMSEventStatus status)
	{
		this.status = status;
	}

	public String getErrorMessage()
	{
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage)
	{
		this.errorMessage = errorMessage;
	}

	public static long getSerialversionuid()
	{
		return serialVersionUID;
	}

}
