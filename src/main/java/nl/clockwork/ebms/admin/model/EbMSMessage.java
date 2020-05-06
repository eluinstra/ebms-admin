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

import org.apache.wicket.util.io.IClusterable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import nl.clockwork.ebms.EbMSMessageStatus;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "of")
public class EbMSMessage implements IClusterable
{
	private static final long serialVersionUID = 1L;
	@NonNull
	Date timestamp;
	@NonNull
	String cpaId;
	@NonNull
	String conversationId;
	@NonNull
	String messageId;
	int messageNr;
	String refToMessageId;
	Date timeToLive;
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
	Date statusTime;
	@NonNull
	@Default
	List<EbMSAttachment> attachments = new ArrayList<>();
	EbMSEvent event;
	@NonNull
	@Default
	List<EbMSEventLog> events = new ArrayList<>();
}
