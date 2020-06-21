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
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.io.CachedOutputStream;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.support.TransactionTemplate;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.NoArgsConstructor;
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
import nl.clockwork.ebms.event.processor.EbMSEventStatus;
import nl.clockwork.ebms.querydsl.InstantType;
import nl.clockwork.ebms.querydsl.model.QCpa;
import nl.clockwork.ebms.querydsl.model.QEbmsAttachment;
import nl.clockwork.ebms.querydsl.model.QEbmsEvent;
import nl.clockwork.ebms.querydsl.model.QEbmsEventLog;
import nl.clockwork.ebms.querydsl.model.QEbmsMessage;

@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@AllArgsConstructor
public abstract class AbstractEbMSDAO implements EbMSDAO
{
	@Builder
	@NoArgsConstructor(force = true)
	@AllArgsConstructor(staticName = "of")
	@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
	public static class EbMSMessageRowMapper implements RowMapper<EbMSMessage>
	{
		@NonNull
		SQLQueryFactory queryFactory;
		@NonNull
		@Default
		Supplier<List<EbMSAttachment>> getAttachments = () -> Collections.emptyList();
		@NonNull
		@Default
		Supplier<EbMSEvent> getEvent = () -> null;
		@NonNull
		@Default
		Supplier<List<EbMSEventLog>> getEvents = () -> Collections.emptyList();
		boolean detail;
		QEbmsMessage messageTable = QEbmsMessage.ebmsMessage;

		public SQLQuery<Tuple> getBaseQuery()
		{
			val paths = new ArrayList<Expression<?>>(Arrays.asList(
					messageTable.timeStamp,
					messageTable.cpaId,
					messageTable.conversationId,
					messageTable.messageId,
					messageTable.messageNr,
					messageTable.refToMessageId,
					messageTable.timeToLive,
					messageTable.fromPartyId,
					messageTable.fromRole,
					messageTable.toPartyId,
					messageTable.toRole,
					messageTable.service,
					messageTable.action,
					messageTable.status,
					messageTable.statusTime));
			if (detail)
				paths.add(messageTable.content);
			return queryFactory.select(paths.toArray(new Expression<?>[]{}))
					.from(messageTable);
		}
		
		@Override
		public EbMSMessage mapRow(ResultSet rs, int rowNum) throws SQLException
		{
			val attachments = getAttachments.get();
			val events = getEvents.get();
			val builder = EbMSMessage.builder()
					.timestamp(InstantType.toInstant(rs.getTimestamp("time_stamp")))
					.cpaId(rs.getString("cpa_id"))
					.conversationId(rs.getString("conversation_id"))
					.messageId(rs.getString("message_id"))
					.messageNr(rs.getInt("message_nr"))
					.refToMessageId(rs.getString("ref_to_message_id"))
					.timeToLive(InstantType.toInstant(rs.getTimestamp("time_to_live")))
					.fromPartyId(rs.getString("from_party_id"))
					.fromRole(rs.getString("from_role"))
					.toPartyId(rs.getString("to_party_id"))
					.toRole(rs.getString("to_role"))
					.service(rs.getString("service"))
					.action(rs.getString("action"))
					.status(rs.getObject("status") == null ? null : EbMSMessageStatus.get(rs.getInt("status")).orElse(null))
					.statusTime(InstantType.toInstant(rs.getTimestamp("status_time")))
					.attachments(attachments)
					.event(getEvent.get())
					.events(events);
			if (detail)
				builder.content(rs.getString("content"));
			EbMSMessage result = builder.build();
			attachments.forEach(a -> a.setMessage(result));
			events.forEach(e -> e.setMessage(result));
			return result;
		}
	}

	@NonNull
	TransactionTemplate transactionTemplate;
	@NonNull
	JdbcTemplate jdbcTemplate;
	@NonNull
	SQLQueryFactory queryFactory;
	QCpa cpaTable = QCpa.cpa1;
	QEbmsMessage messageTable = QEbmsMessage.ebmsMessage;
	QEbmsAttachment attachmentTable = QEbmsAttachment.ebmsAttachment;
	QEbmsEvent eventTable = QEbmsEvent.ebmsEvent;
	QEbmsEventLog eventLogTable = QEbmsEventLog.ebmsEventLog;
	RowMapper<CPA> cpaRowMapper = (rs,rowNum) ->
	{
		return CPA.of(rs.getString("cpa_id"),rs.getString("cpa"));
	};
	
	@Override
	public CPA findCPA(String cpaId)
	{
		try
		{
			val query = queryFactory.select(cpaTable.all())
					.from(cpaTable)
					.where(cpaTable.cpaId.eq(cpaId))
					.getSQL();
			return jdbcTemplate.queryForObject(
					query.getSQL(),
					query.getNullFriendlyBindings().toArray(),
					cpaRowMapper);
		}
		catch (EmptyResultDataAccessException e)
		{
			return null;
		}
	}

	@Override
	public int countCPAs()
	{
		val query = queryFactory.select(cpaTable.cpaId.count())
				.from(cpaTable)
				.getSQL();
		return jdbcTemplate.queryForObject(
				query.getSQL(),
				query.getNullFriendlyBindings().toArray(),
				Integer.class);
	}
	
	@Override
	public List<String> selectCPAIds()
	{
		val query = queryFactory.select(cpaTable.cpaId)
				.from(cpaTable)
				.orderBy(cpaTable.cpaId.asc())
				.getSQL();
		return jdbcTemplate.queryForList(
				query.getSQL(),
				query.getNullFriendlyBindings().toArray(),
				String.class);
	}
	
	@Override
	public List<CPA> selectCPAs(long first, long count)
	{
		val query = queryFactory.select(cpaTable.all())
				.from(cpaTable)
				.orderBy(cpaTable.cpaId.asc())
				.limit(count)
				.offset(first)
				.getSQL();
		return jdbcTemplate.query(
				query.getSQL(),
				query.getNullFriendlyBindings().toArray(),
				cpaRowMapper);
	}

	@Override
	public EbMSMessage findMessage(String messageId)
	{
		return findMessage(messageId,0);
	}

	@Override
	public EbMSMessage findMessage(String messageId, int messageNr)
	{
		val rowMapper = EbMSMessageRowMapper.builder()
				.queryFactory(queryFactory)
				.getAttachments(() -> getAttachments(messageId,messageNr))
				.getEvent(() -> getEvent(messageId))
				.getEvents(() -> getEvents(messageId))
				.detail(true)
				.build();
		val query = rowMapper.getBaseQuery()
				.where(messageTable.messageId.eq(messageId)
						.and(messageTable.messageNr.eq(messageNr)))
				.getSQL();
		return jdbcTemplate.queryForObject(
				query.getSQL(),
				query.getNullFriendlyBindings().toArray(),
				rowMapper);
	}

	@Override
	public boolean existsResponseMessage(String messageId)
	{
		val query = queryFactory.select(messageTable.messageId.count())
				.from(messageTable)
				.where(messageTable.refToMessageId.eq(messageId)
						.and(messageTable.messageNr.eq(0))
						.and(messageTable.service.eq(EbMSAction.EBMS_SERVICE_URI)))
				.getSQL();
		return jdbcTemplate.queryForObject(
				query.getSQL(),
				query.getNullFriendlyBindings().toArray(),
				Integer.class) > 0;
	}

	@Override
	public EbMSMessage findResponseMessage(String messageId)
	{
		val rowMapper = EbMSMessageRowMapper.builder()
				.getAttachments(() -> Collections.emptyList())
				.getEvents(() -> getEvents(messageId))
				.detail(true)
				.build();
		val query = rowMapper.getBaseQuery()
				.where(messageTable.refToMessageId.eq(messageId)
						.and(messageTable.messageNr.eq(0))
						.and(messageTable.service.eq(EbMSAction.EBMS_SERVICE_URI)))
				.getSQL();
		return jdbcTemplate.queryForObject(
				query.getSQL(),
				query.getNullFriendlyBindings().toArray(),
				rowMapper);
	}

	@Override
	public int countMessages(EbMSMessageFilter filter)
	{
		val query = queryFactory.select(messageTable.messageId.count())
				.from(messageTable)
				.where(getMessageFilter(messageTable,filter,new BooleanBuilder()))
				.getSQL();
		return jdbcTemplate.queryForObject(
				query.getSQL(),
				query.getNullFriendlyBindings().toArray(),
				Integer.class);
	}
	
	@Override
	public List<EbMSMessage> selectMessages(EbMSMessageFilter filter, long first, long count)
	{
		val rowMapper = EbMSMessageRowMapper.builder().queryFactory(queryFactory).build();
		val query = rowMapper.getBaseQuery()
				.where(getMessageFilter(messageTable,filter,new BooleanBuilder()))
				.orderBy(messageTable.timeStamp.desc())
				.limit(count)
				.offset(first)
				.getSQL();
		return jdbcTemplate.query(
				query.getSQL(),
				query.getNullFriendlyBindings().toArray(),
				rowMapper);
	}
	
	@Override
	public EbMSAttachment findAttachment(String messageId, int messageNr, String contentId)
	{
		val query = queryFactory.select(attachmentTable.name,attachmentTable.contentId,attachmentTable.contentType,attachmentTable.content)
				.from(attachmentTable)
				.where(attachmentTable.messageId.eq(messageId)
						.and(attachmentTable.messageNr.eq(messageNr))
						.and(attachmentTable.contentId.eq(contentId)))
				.orderBy(attachmentTable.orderNr.asc())
				.getSQL();
		return jdbcTemplate.queryForObject(
				query.getSQL(),
				query.getNullFriendlyBindings().toArray(),
				(rs,rowNum) ->
				{
					try
					{
						return EbMSAttachment.builder()
								.name(rs.getString("name"))
								.contentId(rs.getString("content_id"))
								.contentType(rs.getString("content_type"))
								.content(createCachedOutputStream(rs.getBinaryStream("content")))
								.build();
					}
					catch (IOException e)
					{
						throw new SQLException(e);
					}
				});
	}

	protected CachedOutputStream createCachedOutputStream(InputStream in) throws IOException
	{
		val result = new CachedOutputStream();
		CachedOutputStream.copyStream(in,result,4096);
		result.lockOutputStream();
		return result;
	}

	protected List<EbMSAttachment> getAttachments(String messageId, int messageNr)
	{
		val query = queryFactory.select(attachmentTable.name,attachmentTable.contentId,attachmentTable.contentType)
				.from(attachmentTable)
				.where(attachmentTable.messageId.eq(messageId)
						.and(attachmentTable.messageNr.eq(messageNr)))
				.getSQL();
		return jdbcTemplate.query(
				query.getSQL(),
				query.getNullFriendlyBindings().toArray(),
				(rs,rowNum) ->
				{
					return EbMSAttachment.builder()
							.name(rs.getString("name"))
							.contentId(rs.getString("content_id"))
							.contentType(rs.getString("content_type"))
							.build();
				});
	}

	private EbMSEvent getEvent(String messageId)
	{
		try
		{
			val query = queryFactory.select(eventTable.timeToLive,eventTable.timeStamp,eventTable.retries)
					.from(eventTable)
					.where(eventTable.messageId.eq(messageId))
					.getSQL();
			return jdbcTemplate.queryForObject(
					query.getSQL(),
					query.getNullFriendlyBindings().toArray(),
					(rs,rowNum) ->
					{
						return EbMSEvent.of(InstantType.toInstant(rs.getTimestamp("time_to_live")),InstantType.toInstant(rs.getTimestamp("time_stamp")),rs.getInt("retries"));
					});
		}
		catch (EmptyResultDataAccessException e)
		{
			return null;
		}
	}

	private List<EbMSEventLog> getEvents(String messageId)
	{
		val query = queryFactory.select(eventLogTable.messageId,eventLogTable.timeStamp,eventLogTable.uri,eventLogTable.status,eventLogTable.errorMessage)
				.from(eventLogTable)
				.where(eventLogTable.messageId.eq(messageId))
				.getSQL();
		return jdbcTemplate.query(
				query.getSQL(),
				query.getNullFriendlyBindings().toArray(),
				(rs,rowNum) ->
				{
					return EbMSEventLog.builder()
							.timestamp(InstantType.toInstant(rs.getTimestamp("time_stamp")))
							.uri(rs.getString("uri"))
							.status(EbMSEventStatus.get(rs.getInt("status")).orElse(null))
							.errorMessage(rs.getString("error_message"))
							.build();
				});
	}
	
	@Override
	public List<String> selectMessageIds(String cpaId, String fromRole, String toRole, EbMSMessageStatus...statuses)
	{
		val query = queryFactory.select(messageTable.messageId)
				.from(messageTable)
				.where(messageTable.cpaId.eq(cpaId)
						.and(messageTable.fromRole.eq(fromRole))
						.and(messageTable.toRole.eq(toRole))
						.and(messageTable.statusRaw.in(EbMSMessageStatus.getIds(statuses))))
				.orderBy(messageTable.timeStamp.desc())
				.getSQL();
		return jdbcTemplate.queryForList(
				query.getSQL(),
				query.getNullFriendlyBindings().toArray(),
				String.class);
	}
	
	@Override
	public HashMap<String,Integer> selectMessageTraffic(LocalDateTime from, LocalDateTime to, TimeUnit timeUnit, EbMSMessageStatus...statuses)
	{
		val query = queryFactory.select(getTimestamp(messageTable.timeStampRaw,timeUnit).as("time"), messageTable.messageId.count().as("nr"))
				.from(messageTable)
				.where(messageTable.timeStampRaw.goe(Timestamp.valueOf(from))
						.and(messageTable.timeStampRaw.lt(Timestamp.valueOf(to)))
						.and(statuses.length == 0 ? messageTable.statusRaw.isNotNull() : messageTable.statusRaw.in(EbMSMessageStatus.getIds(statuses))))
				.groupBy(getTimestamp(messageTable.timeStampRaw,timeUnit))
				.getSQL();
		val result = new HashMap<String,Integer>();
		jdbcTemplate.query(
				query.getSQL(),
				query.getNullFriendlyBindings().toArray(),
				(rs,rowNum) ->
				{
					result.put(rs.getObject("time",Integer.class).toString(),rs.getInt("nr"));
					return null;
				});
		return result;
	}

	@Override
	public void printMessagesToCSV(final CSVPrinter printer, EbMSMessageFilter filter)
	{
		val rowMapper = EbMSMessageRowMapper.builder().queryFactory(queryFactory).build();
		val query = rowMapper.getBaseQuery()
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
				builder.and(table.statusRaw.in(EbMSMessageStatus.getIds(messageFilter.getStatuses())));
			if (messageFilter.getServiceMessage() != null)
			{
				if (messageFilter.getServiceMessage())
					builder.and(table.service.eq(EbMSAction.EBMS_SERVICE_URI));
				else
					builder.and(table.service.ne(EbMSAction.EBMS_SERVICE_URI));
			}
			if (messageFilter.getFrom() != null)
				builder.and(table.timeStampRaw.goe(Timestamp.from(messageFilter.getFrom().atZone(ZoneId.systemDefault()).toInstant())));
			if (messageFilter.getTo() != null)
				builder.and(table.timeStampRaw.lt(Timestamp.from(messageFilter.getTo().atZone(ZoneId.systemDefault()).toInstant())));
		}
		return builder;
	}

	private NumberExpression<Integer> getTimestamp(DateTimePath<Timestamp> timeStampRaw, TimeUnit timeUnit)
	{
		switch (timeUnit)
		{
			case HOUR:
				return timeStampRaw.minute();
			case DAY:
				return timeStampRaw.hour();
			case MONTH:
				return timeStampRaw.dayOfMonth();
			case YEAR:
				return timeStampRaw.month();
			default:
				return null;
		}
	}
}