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

import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.sql.SQLQueryFactory;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;
import nl.clockwork.ebms.EbMSAction;
import nl.clockwork.ebms.EbMSMessageStatus;
import nl.clockwork.ebms.admin.model.CPA;
import nl.clockwork.ebms.admin.model.EbMSAttachment;
import nl.clockwork.ebms.admin.model.EbMSEvent;
import nl.clockwork.ebms.admin.model.EbMSEventLog;
import nl.clockwork.ebms.admin.model.EbMSMessage;
import nl.clockwork.ebms.admin.web.Utils;
import nl.clockwork.ebms.admin.web.message.EbMSMessageFilter;
import nl.clockwork.ebms.admin.web.message.TimeUnit;
import nl.clockwork.ebms.querydsl.model.QCpa;
import nl.clockwork.ebms.querydsl.model.QEbmsAttachment;
import nl.clockwork.ebms.querydsl.model.QEbmsEvent;
import nl.clockwork.ebms.querydsl.model.QEbmsEventLog;
import nl.clockwork.ebms.querydsl.model.QEbmsMessage;

@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@AllArgsConstructor
public abstract class AbstractEbMSDAO implements EbMSDAO
{
	@NonNull
	JdbcTemplate jdbcTemplate;
	@NonNull
	SQLQueryFactory queryFactory;
	QCpa cpaTable = QCpa.cpa1;
	QEbmsMessage messageTable = QEbmsMessage.ebmsMessage;
	QEbmsAttachment attachmentTable = QEbmsAttachment.ebmsAttachment;
	QEbmsEvent eventTable = QEbmsEvent.ebmsEvent;
	QEbmsEventLog eventLogTable = QEbmsEventLog.ebmsEventLog;
	Expression<?>[] ebMSMessageColumns = {messageTable.timeStamp,messageTable.cpaId,messageTable.conversationId,messageTable.messageId,messageTable.messageNr,messageTable.refToMessageId,messageTable.timeToLive,messageTable.fromPartyId,messageTable.fromRole,messageTable.toPartyId,messageTable.toRole,messageTable.service,messageTable.action,messageTable.status,messageTable.statusTime};
	Expression<?>[] ebMSMessageDetailColumns = {messageTable.timeStamp,messageTable.cpaId,messageTable.conversationId,messageTable.messageId,messageTable.messageNr,messageTable.refToMessageId,messageTable.timeToLive,messageTable.fromPartyId,messageTable.fromRole,messageTable.toPartyId,messageTable.toRole,messageTable.service,messageTable.action,messageTable.content,messageTable.status,messageTable.statusTime};
	ConstructorExpression<EbMSMessage> ebMSMessageProjection = Projections.constructor(EbMSMessage.class,ebMSMessageColumns);
	ConstructorExpression<EbMSMessage> ebMSMessageDetailProjection = Projections.constructor(EbMSMessage.class,ebMSMessageDetailColumns);
	
	@Override
	@Transactional(transactionManager = "dataSourceTransactionManager")
	public CPA findCPA(String cpaId)
	{
		return queryFactory.select(Projections.constructor(CPA.class,cpaTable.cpaId,cpaTable.cpa))
				.from(cpaTable)
				.where(cpaTable.cpaId.eq(cpaId))
				.fetchOne();
	}

	@Override
	@Transactional(transactionManager = "dataSourceTransactionManager")
	public long countCPAs()
	{
		return queryFactory.select(cpaTable.cpaId.count())
				.from(cpaTable)
				.fetchOne();
	}
	
	@Override
	@Transactional(transactionManager = "dataSourceTransactionManager")
	public List<String> selectCPAIds()
	{
		return queryFactory.select(cpaTable.cpaId)
				.from(cpaTable)
				.orderBy(cpaTable.cpaId.asc())
				.fetch();
	}
	
	@Override
	@Transactional(transactionManager = "dataSourceTransactionManager")
	public List<CPA> selectCPAs(long first, long count)
	{
		return queryFactory.select(Projections.constructor(CPA.class,cpaTable.cpaId,cpaTable.cpa))
				.from(cpaTable)
				.orderBy(cpaTable.cpaId.asc())
				.limit(count)
				.offset(first)
				.fetch();
	}

	@Override
	@Transactional(transactionManager = "dataSourceTransactionManager")
	public EbMSMessage findMessage(String messageId)
	{
		return findMessage(messageId,0);
	}

	@Override
	@Transactional(transactionManager = "dataSourceTransactionManager")
	public EbMSMessage findMessage(String messageId, int messageNr)
	{
		val result = queryFactory.select(ebMSMessageDetailProjection)
				.from(messageTable)
				.where(messageTable.messageId.eq(messageId)
						.and(messageTable.messageNr.eq(messageNr)))
				.fetchOne();
		result.setAttachments(getAttachments(messageId,messageNr));
		result.setEvent(getEvent(messageId));
		result.setEvents(getEvents(messageId));
		return result;
	}

	@Override
	@Transactional(transactionManager = "dataSourceTransactionManager")
	public boolean existsResponseMessage(String messageId)
	{
		return queryFactory.select(messageTable.messageId.count())
				.from(messageTable)
				.where(messageTable.refToMessageId.eq(messageId)
						.and(messageTable.messageNr.eq(0))
						.and(messageTable.service.eq(EbMSAction.EBMS_SERVICE_URI)))
				.fetchOne() > 0;
	}

	@Override
	public EbMSMessage findResponseMessage(String messageId)
	{
		val result = queryFactory.select(ebMSMessageDetailProjection)
				.from(messageTable)
				.where(messageTable.refToMessageId.eq(messageId)
						.and(messageTable.messageNr.eq(0))
						.and(messageTable.service.eq(EbMSAction.EBMS_SERVICE_URI)))
				.fetchOne();
		result.setEvents(getEvents(messageId));
		return result;
	}

	@Override
	@Transactional(transactionManager = "dataSourceTransactionManager")
	public long countMessages(EbMSMessageFilter filter)
	{
		return queryFactory.select(messageTable.messageId.count())
				.from(messageTable)
				.where(getMessageFilter(messageTable,filter,new BooleanBuilder()))
				.fetchOne();
	}
	
	@Override
	@Transactional(transactionManager = "dataSourceTransactionManager")
	public List<EbMSMessage> selectMessages(EbMSMessageFilter filter, long first, long count)
	{
		return queryFactory.select(ebMSMessageProjection)
				.from(messageTable)
				.where(getMessageFilter(messageTable,filter,new BooleanBuilder()))
				.orderBy(messageTable.timeStamp.desc())
				.limit(count)
				.offset(first)
				.fetch();
	}
	
	@Override
	@Transactional(transactionManager = "dataSourceTransactionManager")
	public EbMSAttachment findAttachment(String messageId, int messageNr, String contentId)
	{
		return queryFactory.select(Projections.constructor(EbMSAttachment.class,attachmentTable.name,attachmentTable.contentId,attachmentTable.contentType,attachmentTable.content))
				.from(attachmentTable)
				.where(attachmentTable.messageId.eq(messageId)
						.and(attachmentTable.messageNr.eq(messageNr))
						.and(attachmentTable.contentId.eq(contentId)))
				.orderBy(attachmentTable.orderNr.asc())
				.fetchOne();
	}

	@Transactional(transactionManager = "dataSourceTransactionManager")
	protected List<EbMSAttachment> getAttachments(String messageId, int messageNr)
	{
		return queryFactory.select(Projections.constructor(EbMSAttachment.class,attachmentTable.name,attachmentTable.contentId,attachmentTable.contentType))
				.from(attachmentTable)
				.where(attachmentTable.messageId.eq(messageId)
						.and(attachmentTable.messageNr.eq(messageNr)))
				.fetch();
	}

	@Transactional(transactionManager = "dataSourceTransactionManager")
	private EbMSEvent getEvent(String messageId)
	{
		return queryFactory.select(Projections.constructor(EbMSEvent.class,eventTable.timeToLive,eventTable.timeStamp,eventTable.retries))
				.from(eventTable)
				.where(eventTable.messageId.eq(messageId))
				.fetchOne();
	}

	@Transactional(transactionManager = "dataSourceTransactionManager")
	private List<EbMSEventLog> getEvents(String messageId)
	{
		return queryFactory.select(Projections.constructor(EbMSEventLog.class,eventLogTable.timeStamp,eventLogTable.uri,eventLogTable.status,eventLogTable.errorMessage))
				.from(eventLogTable)
				.where(eventLogTable.messageId.eq(messageId))
				.fetch();
	}
	
	@Override
	@Transactional(transactionManager = "dataSourceTransactionManager")
	public List<String> selectMessageIds(String cpaId, String fromRole, String toRole, EbMSMessageStatus...statuses)
	{
		return queryFactory.select(messageTable.messageId)
				.from(messageTable)
				.where(messageTable.cpaId.eq(cpaId)
						.and(messageTable.fromRole.eq(fromRole))
						.and(messageTable.toRole.eq(toRole))
						.and(messageTable.status.in(statuses)))
				.orderBy(messageTable.timeStamp.desc())
				.fetch();
	}
	
	@Override
	@Transactional(transactionManager = "dataSourceTransactionManager")
	public Map<String,Integer> selectMessageTraffic(LocalDateTime from, LocalDateTime to, TimeUnit timeUnit, EbMSMessageStatus...statuses)
	{
		val result = queryFactory.select(getTimestamp(messageTable.timeStamp,timeUnit).as("time"), messageTable.messageId.count().as("nr"))
				.from(messageTable)
				.where(messageTable.timeStamp.goe(from.atZone(ZoneId.systemDefault()).toInstant())
						.and(messageTable.timeStamp.lt(to.atZone(ZoneId.systemDefault()).toInstant()))
						.and(statuses.length == 0 ? messageTable.status.isNotNull() : messageTable.status.in(statuses)))
				.groupBy(getTimestamp(messageTable.timeStamp,timeUnit))
				.fetch();
		return result.stream().collect(Collectors.toMap(t -> t.get(0,Integer.class).toString(),t -> t.get(1,Long.class).intValue()));
	}

	@Override
	public void printMessagesToCSV(final CSVPrinter printer, EbMSMessageFilter filter)
	{
		val query = queryFactory.select(ebMSMessageColumns)
				.from(messageTable)
				.where(getMessageFilter(messageTable,filter,new BooleanBuilder()))
				.orderBy(messageTable.timeStamp.desc())
				.getSQL();
		jdbcTemplate.query(
				query.getSQL(),
				query.getNullFriendlyBindings().toArray(),
				(rs,rowNum) ->
				{
					try
					{
						printer.print(rs.getString("message_id"));
						printer.print(rs.getInt("message_nr"));
						printer.print(rs.getString("ref_to_message_id"));
						printer.print(rs.getString("conversation_id"));
						printer.print(rs.getTimestamp("time_stamp"));
						printer.print(rs.getTimestamp("time_to_live"));
						printer.print(rs.getString("cpa_id"));
						printer.print(rs.getString("from_role"));
						printer.print(rs.getString("to_role"));
						printer.print(rs.getString("service"));
						printer.print(rs.getString("action"));
						printer.print(rs.getObject("status") == null ? null : EbMSMessageStatus.get(rs.getInt("status")));
						printer.print(rs.getTimestamp("status_time"));
						printer.println();
						return null;
					}
					catch (IOException e)
					{
						throw new SQLException(e);
					}
				});
	}

	@Override
	public void writeMessageToZip(String messageId, int messageNr, final ZipOutputStream zip)
	{
		val query = queryFactory.select(messageTable.content)
				.from(messageTable)
				.where(messageTable.messageId.eq(messageId)
						.and(messageTable.messageNr.eq(messageNr)))
				.getSQL();
		jdbcTemplate.queryForObject(
				query.getSQL(),
				query.getNullFriendlyBindings().toArray(),
				(rs,rowNum) ->
				{
					try
					{
						val entry = new ZipEntry("message.xml");
						zip.putNextEntry(entry);
						zip.write(rs.getString("content").getBytes());
						zip.closeEntry();
						return null;
					}
					catch (IOException e)
					{
						throw new SQLException(e);
					}
				});
		writeAttachmentsToZip(messageId,messageNr,zip);
	}

	protected void writeAttachmentsToZip(String messageId, int messageNr, final ZipOutputStream zip)
	{
		val query = queryFactory.select(attachmentTable.name,attachmentTable.contentId,attachmentTable.contentType,attachmentTable.content)
				.from(attachmentTable)
				.where(attachmentTable.messageId.eq(messageId)
						.and(attachmentTable.messageNr.eq(messageNr)))
				.getSQL();
		jdbcTemplate.query(
				query.getSQL(),
				query.getNullFriendlyBindings().toArray(),
				(rs,rowNum) ->
				{
					try
					{
						val entry = new ZipEntry("attachments/" + (StringUtils.isEmpty(rs.getString("name")) ? rs.getString("content_id") + Utils.getFileExtension(rs.getString("content_type")) : rs.getString("name")));
						entry.setComment("Content-Type: " + rs.getString("content_type"));
						zip.putNextEntry(entry);
						IOUtils.copy(rs.getBinaryStream("content"),zip);
						zip.closeEntry();
						return null;
					}
					catch (IOException e)
					{
						throw new SQLException(e);
					}
				});
	}
	
	protected BooleanBuilder getMessageFilter(QEbmsMessage table, EbMSMessageFilter messageFilter, BooleanBuilder builder)
	{
		builder = nl.clockwork.ebms.dao.EbMSDAO.applyFilter(table,messageFilter,builder);
		if (messageFilter != null)
		{
			if (messageFilter.getMessageNr() != null)
				builder.and(table.messageNr.eq(messageFilter.getMessageNr()));
			if (messageFilter.getStatuses().size() > 0)
				builder.and(table.status.in(messageFilter.getStatuses()));
			if (messageFilter.getServiceMessage() != null)
			{
				if (messageFilter.getServiceMessage())
					builder.and(table.service.eq(EbMSAction.EBMS_SERVICE_URI));
				else
					builder.and(table.service.ne(EbMSAction.EBMS_SERVICE_URI));
			}
			if (messageFilter.getFrom() != null)
				builder.and(table.timeStamp.goe(messageFilter.getFrom().atZone(ZoneId.systemDefault()).toInstant()));
			if (messageFilter.getTo() != null)
				builder.and(table.timeStamp.lt(messageFilter.getTo().atZone(ZoneId.systemDefault()).toInstant()));
		}
		return builder;
	}

	private NumberExpression<Integer> getTimestamp(DateTimePath<Instant> timeStamp, TimeUnit timeUnit)
	{
		switch (timeUnit)
		{
			case HOUR:
				return timeStamp.minute();
			case DAY:
				return timeStamp.hour();
			case MONTH:
				return timeStamp.dayOfMonth();
			case YEAR:
				return timeStamp.month();
			default:
				return null;
		}
	}
}