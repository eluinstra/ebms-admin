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
package nl.clockwork.ebms.admin.web.message;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nl.clockwork.ebms.EbMSMessageStatus;
import nl.clockwork.ebms.model.EbMSMessageContext;

public class EbMSMessageFilter extends EbMSMessageContext
{
	private static final long serialVersionUID = 1L;
	private Integer messageNr;
	private Boolean serviceMessage;
	private List<EbMSMessageStatus> statuses = new ArrayList<>();
	private Date from;
	private Date to;

	public Integer getMessageNr()
	{
		return messageNr;
	}
	public void setMessageNr(Integer messageNr)
	{
		this.messageNr = messageNr;
	}
	public Boolean getServiceMessage()
	{
		return serviceMessage;
	}
	public void setServiceMessage(Boolean serviceMessage)
	{
		this.serviceMessage = serviceMessage;
	}
	public List<EbMSMessageStatus> getStatuses()
	{
		return statuses;
	}
	public void setStatuses(List<EbMSMessageStatus> statuses)
	{
		this.statuses = statuses;
	}
	public Date getFrom()
	{
		return from;
	}
	public void setFrom(Date from)
	{
		this.from = from;
	}
	public Date getTo()
	{
		return to;
	}
	public void setTo(Date to)
	{
		this.to = to;
	}
}
