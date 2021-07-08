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
package nl.clockwork.ebms.admin.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import org.apache.commons.csv.CSVPrinter;

import com.querydsl.core.BooleanBuilder;

import nl.clockwork.ebms.EbMSMessageStatus;
import nl.clockwork.ebms.admin.model.CPA;
import nl.clockwork.ebms.admin.model.EbMSAttachment;
import nl.clockwork.ebms.admin.model.EbMSMessage;
import nl.clockwork.ebms.admin.web.message.EbMSMessageFilter;
import nl.clockwork.ebms.admin.web.message.TimeUnit;
import nl.clockwork.ebms.querydsl.model.QEbmsMessage;
import nl.clockwork.ebms.service.model.EbMSMessageContext;

public interface EbMSDAO
{
	CPA findCPA(String cpaId);
	long countCPAs();
	List<String> selectCPAIds();
	List<CPA> selectCPAs(long first, long count);

	EbMSMessage findMessage(String messageId);
	EbMSMessage findMessage(String messageId, int messageNr);
	boolean existsResponseMessage(String messageId);
	EbMSMessage findResponseMessage(String messageId);
	long countMessages(EbMSMessageFilter filter);
	List<EbMSMessage> selectMessages(EbMSMessageFilter filter, long first, long count);

	EbMSAttachment findAttachment(String messageId, int messageNr, String contentId);
	
	List<String> selectMessageIds(String cpaId, String fromRole, String toRole, EbMSMessageStatus...status);

	Map<Integer,Integer> selectMessageTraffic(LocalDateTime from, LocalDateTime to, TimeUnit timeUnit, EbMSMessageStatus...status);
	
	void writeMessageToZip(String messageId, int messageNr, ZipOutputStream stream);
	void printMessagesToCSV(CSVPrinter printer, EbMSMessageFilter filter);

	static BooleanBuilder applyFilter(QEbmsMessage table, EbMSMessageContext messageContext, BooleanBuilder builder)
	{
		if (messageContext != null)
		{
			if (messageContext.getCpaId() != null)
				builder.and(table.cpaId.eq(messageContext.getCpaId()));
			if (messageContext.getFromParty() != null)
			{
				if (messageContext.getFromParty().getPartyId() != null)
					builder.and(table.fromPartyId.eq(messageContext.getFromParty().getPartyId()));
				if (messageContext.getFromParty().getRole() != null)
					builder.and(table.fromRole.eq(messageContext.getFromParty().getRole()));
			}
			if (messageContext.getToParty() != null)
			{
				if (messageContext.getToParty().getPartyId() != null)
					builder.and(table.toPartyId.eq(messageContext.getToParty().getPartyId()));
				if (messageContext.getToParty().getRole() != null)
					builder.and(table.toRole.eq(messageContext.getToParty().getRole()));
			}
			if (messageContext.getService() != null)
				builder.and(table.service.eq(messageContext.getService()));
			if (messageContext.getAction() != null)
				builder.and(table.action.eq(messageContext.getAction()));
			if (messageContext.getConversationId() != null)
				builder.and(table.conversationId.eq(messageContext.getConversationId()));
			if (messageContext.getMessageId() != null)
				builder.and(table.messageId.eq(messageContext.getMessageId()));
			if (messageContext.getRefToMessageId() != null)
				builder.and(table.refToMessageId.eq(messageContext.getRefToMessageId()));
			if (messageContext.getMessageStatus() != null)
				builder.and(table.status.eq(messageContext.getMessageStatus()));
		}
		return builder;
	}
}
