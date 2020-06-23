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

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.apache.wicket.util.io.IClusterable;
import org.w3c.dom.Document;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import nl.clockwork.ebms.EbMSMessageStatus;
import nl.clockwork.ebms.util.DOMUtils;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class EbMSMessage implements IClusterable
{
	private static final long serialVersionUID = 1L;
	@NonNull
	Instant timestamp;
	@NonNull
	String cpaId;
	@NonNull
	String conversationId;
	@NonNull
	String messageId;
	int messageNr;
	String refToMessageId;
	Instant timeToLive;
	@NonNull
	String fromPartyId;
	String fromRole;
	@NonNull
	String toPartyId;
	String toRole;
	@NonNull
	String service;
	@NonNull
	String action;
	String content;
	EbMSMessageStatus status;
	Instant statusTime;
	@NonNull
	@Default
	List<EbMSAttachment> attachments = Collections.emptyList();
	EbMSEvent event;
	@NonNull
	@Default
	List<EbMSEventLog> events = Collections.emptyList();

	public EbMSMessage(@NonNull Instant timestamp, @NonNull String cpaId, @NonNull String conversationId, @NonNull String messageId, int messageNr, String refToMessageId, Instant timeToLive, @NonNull String fromPartyId, String fromRole, @NonNull String toPartyId, String toRole, @NonNull String service, @NonNull String action, EbMSMessageStatus status, Instant statusTime)
	{
		this.timestamp = timestamp;
		this.cpaId = cpaId;
		this.conversationId = conversationId;
		this.messageId = messageId;
		this.messageNr = messageNr;
		this.refToMessageId = refToMessageId;
		this.timeToLive = timeToLive;
		this.fromPartyId = fromPartyId;
		this.fromRole = fromRole;
		this.toPartyId = toPartyId;
		this.toRole = toRole;
		this.service = service;
		this.action = action;
		this.status = status;
		this.statusTime = statusTime;
		this.attachments = Collections.emptyList();
		this.events = Collections.emptyList();
	}

	public EbMSMessage(@NonNull Instant timestamp, @NonNull String cpaId, @NonNull String conversationId, @NonNull String messageId, int messageNr, String refToMessageId, Instant timeToLive, @NonNull String fromPartyId, String fromRole, @NonNull String toPartyId, String toRole, @NonNull String service, @NonNull String action, Document content, EbMSMessageStatus status, Instant statusTime) throws TransformerException
	{
		this.timestamp = timestamp;
		this.cpaId = cpaId;
		this.conversationId = conversationId;
		this.messageId = messageId;
		this.messageNr = messageNr;
		this.refToMessageId = refToMessageId;
		this.timeToLive = timeToLive;
		this.fromPartyId = fromPartyId;
		this.fromRole = fromRole;
		this.toPartyId = toPartyId;
		this.toRole = toRole;
		this.service = service;
		this.action = action;
		this.content = DOMUtils.toString(content);
		this.status = status;
		this.statusTime = statusTime;
		this.attachments = Collections.emptyList();
		this.events = Collections.emptyList();
	}
}
