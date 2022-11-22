package nl.clockwork.ebms.admin.dao;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.StringPath;

import nl.clockwork.ebms.admin.web.message.EbMSMessageFilter;
import nl.clockwork.ebms.querydsl.model.QEbmsMessage;
import nl.clockwork.ebms.service.model.Party;

public interface WithMessageFilter {
	default BooleanBuilder applyFilter(QEbmsMessage table, EbMSMessageFilter messageContext, BooleanBuilder builder)
	{
		if (messageContext != null)
		{
			if (messageContext.getCpaId() != null)
				builder.and(table.cpaId.eq(messageContext.getCpaId()));
			applyPathFilter(table.fromPartyId,table.fromRole,messageContext.getFromParty(),builder);
			applyPathFilter(table.toPartyId,table.toRole,messageContext.getToParty(),builder);
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
			if (messageContext.getStatuses() != null && !messageContext.getStatuses().isEmpty())
				builder.and(table.status.in(messageContext.getStatuses()));
		}
		return builder;
	}

	private void applyPathFilter(StringPath partyId, StringPath role, Party party, BooleanBuilder builder)
	{
		if (party != null)
		{
			builder.and(partyId.eq(party.getPartyId()));
			if (party.getRole() != null)
				builder.and(role.eq(party.getRole()));
		}
	}
}
