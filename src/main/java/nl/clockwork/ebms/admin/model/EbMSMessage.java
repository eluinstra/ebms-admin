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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nl.clockwork.ebms.Constants.EbMSMessageStatus;

import org.apache.wicket.util.io.IClusterable;

public class EbMSMessage implements IClusterable
{
	private static final long serialVersionUID = 1L;
	private Date timestamp;
	private String cpaId;
	private String conversationId;
	private String messageId;
	private int messageNr;
	private String refToMessageId;
	private Date timeToLive;
	private String fromPartyId;
	private String fromRole;
	private String toPartyId;
	private String toRole;
	private String service;
	private String action;
	private String content;
	private EbMSMessageStatus status;
	private Date statusTime;
	private List<EbMSAttachment> attachments = new ArrayList<>();
	private EbMSEvent event;
	private List<EbMSEventLog> events = new ArrayList<>();

	public Date getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp(Date timestamp)
	{
		this.timestamp = timestamp;
	}

	public String getCpaId()
	{
		return cpaId;
	}

	public void setCpaId(String cpaId)
	{
		this.cpaId = cpaId;
	}

	public String getConversationId()
	{
		return conversationId;
	}

	public void setConversationId(String conversationId)
	{
		this.conversationId = conversationId;
	}

	public String getMessageId()
	{
		return messageId;
	}

	public void setMessageId(String messageId)
	{
		this.messageId = messageId;
	}

	public int getMessageNr()
	{
		return messageNr;
	}

	public void setMessageNr(int messageNr)
	{
		this.messageNr = messageNr;
	}

	public String getRefToMessageId()
	{
		return refToMessageId;
	}

	public void setRefToMessageId(String refToMessageId)
	{
		this.refToMessageId = refToMessageId;
	}

	public Date getTimeToLive()
	{
		return timeToLive;
	}

	public void setTimeToLive(Date timeToLive)
	{
		this.timeToLive = timeToLive;
	}

	public String getFromPartyId()
	{
		return fromPartyId;
	}

	public void setFromPartyId(String fromPartyId)
	{
		this.fromPartyId = fromPartyId;
	}

	public String getFromRole()
	{
		return fromRole;
	}

	public void setFromRole(String fromRole)
	{
		this.fromRole = fromRole;
	}

	public String getToPartyId()
	{
		return toPartyId;
	}

	public void setToPartyId(String toPartyId)
	{
		this.toPartyId = toPartyId;
	}

	public String getToRole()
	{
		return toRole;
	}

	public void setToRole(String toRole)
	{
		this.toRole = toRole;
	}

	public String getService()
	{
		return service;
	}

	public void setService(String service)
	{
		this.service = service;
	}

	public String getAction()
	{
		return action;
	}

	public void setAction(String action)
	{
		this.action = action;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}

	public EbMSMessageStatus getStatus()
	{
		return status;
	}

	public void setStatus(EbMSMessageStatus status)
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
	
	public List<EbMSAttachment> getAttachments()
	{
		return attachments;
	}
	
	public void setAttachments(List<EbMSAttachment> attachments)
	{
		this.attachments = attachments;
	}

	public EbMSEvent getEvent()
	{
		return event;
	}

	public void setEvent(EbMSEvent event)
	{
		this.event = event;
	}

	public List<EbMSEventLog> getEvents()
	{
		return events;
	}
	
	public void setEvents(List<EbMSEventLog> events)
	{
		this.events = events;
	}
}
