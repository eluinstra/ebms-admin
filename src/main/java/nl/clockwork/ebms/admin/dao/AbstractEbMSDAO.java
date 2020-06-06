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
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.io.CachedOutputStream;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.support.TransactionTemplate;

import static io.vavr.API.*;
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
import nl.clockwork.ebms.dao.DAOException;
import nl.clockwork.ebms.event.processor.EbMSEventStatus;

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
		@Default
		Supplier<List<EbMSAttachment>> getAttachments = () -> Collections.emptyList();
		@NonNull
		@Default
		Supplier<EbMSEvent> getEvent = () -> null;
		@NonNull
		@Default
		Supplier<List<EbMSEventLog>> getEvents = () -> Collections.emptyList();
		boolean detail;

		public String getBaseQuery()
		{
			return "select" +
				" time_stamp," +
				" cpa_id," +
				" conversation_id," +
				" message_id," +
				" message_nr," +
				" ref_to_message_id," +
				" time_to_live," +
				" from_party_id," +
				" from_role," +
				" to_party_id," +
				" to_role," +
				" service," +
				" action," +
				(detail ? " content," : "") +
				" status," +
				" status_time" +
				" from ebms_message";
		}
		
		@Override
		public EbMSMessage mapRow(ResultSet rs, int rowNum) throws SQLException
		{
			val attachments = getAttachments.get();
			val events = getEvents.get();
			val builder = EbMSMessage.builder()
					.timestamp(toInstant(rs.getTimestamp("time_stamp")))
					.cpaId(rs.getString("cpa_id"))
					.conversationId(rs.getString("conversation_id"))
					.messageId(rs.getString("message_id"))
					.messageNr(rs.getInt("message_nr"))
					.refToMessageId(rs.getString("ref_to_message_id"))
					.timeToLive(toInstant(rs.getTimestamp("time_to_live")))
					.fromPartyId(rs.getString("from_party_id"))
					.fromRole(rs.getString("from_role"))
					.toPartyId(rs.getString("to_party_id"))
					.toRole(rs.getString("to_role"))
					.service(rs.getString("service"))
					.action(rs.getString("action"))
					.status(rs.getObject("status") == null ? null : EbMSMessageStatus.get(rs.getInt("status")))
					.statusTime(toInstant(rs.getTimestamp("status_time")))
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
	RowMapper<CPA> cpaRowMapper = (RowMapper<CPA>)(rs,rowNum) ->
	{
		return CPA.of(rs.getString("cpa_id"),rs.getString("cpa"));
	};
	
	@Override
	public CPA findCPA(String cpaId)
	{
		try
		{
			return jdbcTemplate.queryForObject(
				"select * from cpa" +
				" where cpa_id = ?",
				cpaRowMapper,
				cpaId
			);
		}
		catch (EmptyResultDataAccessException e)
		{
			return null;
		}
	}

	protected static Instant toInstant(Timestamp timestamp)
	{
		return timestamp == null ? null : timestamp.toInstant();
	}

	@Override
	public int countCPAs()
	{
		return jdbcTemplate.queryForObject("select count(cpa_id) from cpa",Integer.class);
	}
	
	@Override
	public List<String> selectCPAIds()
	{
		return jdbcTemplate.queryForList(
			"select cpa_id" +
			" from cpa" +
			" order by cpa_id",
			String.class
		);
	}
	
	public abstract String selectCPAsQuery(long first, long count);
	
	@Override
	public List<CPA> selectCPAs(long first, long count)
	{
		return jdbcTemplate.query(
			selectCPAsQuery(first,count),
			cpaRowMapper
		);
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
				.getAttachments(() -> getAttachments(messageId,messageNr))
				.getEvent(() -> getEvent(messageId))
				.getEvents(() -> getEvents(messageId))
				.detail(true)
				.build();
		return jdbcTemplate.queryForObject(
				rowMapper.getBaseQuery() + 
			" where message_id = ?" +
			" and message_nr = ?",
			rowMapper,
			messageId,
			messageNr
		);
	}

	@Override
	public boolean existsResponseMessage(String messageId)
	{
		return jdbcTemplate.queryForObject(
			"select count(*) from ebms_message" + 
			" where ref_to_message_id = ?" +
			" and message_nr = ?" +
			" and service = ?",
			Integer.class,
			messageId,
			0,
			EbMSAction.EBMS_SERVICE_URI
		) > 0;
	}

	@Override
	public EbMSMessage findResponseMessage(String messageId)
	{
		val rowMapper = EbMSMessageRowMapper.builder()
				.getAttachments(() -> Collections.emptyList())
				.getEvents(() -> getEvents(messageId))
				.detail(true)
				.build();
		return jdbcTemplate.queryForObject(
				rowMapper.getBaseQuery() + 
				" where ref_to_message_id = ?" +
				" and message_nr = ?" +
				" and service = ?",
				rowMapper,
				messageId,
				0,
				EbMSAction.EBMS_SERVICE_URI
		);
	}

	@Override
	public int countMessages(EbMSMessageFilter filter)
	{
		val parameters = new ArrayList<Object>();
		return jdbcTemplate.queryForObject(
			"select count(message_id)" +
			" from ebms_message" +
			" where 1 = 1" +
			getMessageFilter(filter,parameters),
			Integer.class,
			parameters.toArray(new Object[0])
		);
	}
	
	public abstract String selectMessagesQuery(EbMSMessageFilter filter, long first, long count, List<Object> parameters);
	
	@Override
	public List<EbMSMessage> selectMessages(EbMSMessageFilter filter, long first, long count)
	{
		val parameters = new ArrayList<Object>();
		val rowMapper = new EbMSMessageRowMapper();
		return jdbcTemplate.query(
			selectMessagesQuery(filter,first,count,parameters),
			parameters.toArray(new Object[0]),
			rowMapper
		);
	}
	
	@Override
	public EbMSAttachment findAttachment(String messageId, int messageNr, String contentId)
	{
		return jdbcTemplate.queryForObject(
			"select name, content_id, content_type, content" + 
			" from ebms_attachment" + 
			" where message_id = ?" +
			" and message_nr = ?" +
			" and content_id = ?",
			(RowMapper<EbMSAttachment>)(rs,rowNum) ->
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
			},
			messageId,
			messageNr,
			contentId
		);
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
		return jdbcTemplate.query(
			"select name, content_id, content_type" + 
			" from ebms_attachment" + 
			" where message_id = ?" +
			" and message_nr = ?",
			(RowMapper<EbMSAttachment>)(rs,rowNum) ->
			{
				return EbMSAttachment.builder()
						.name(rs.getString("name"))
						.contentId(rs.getString("content_id"))
						.contentType(rs.getString("content_type"))
						.build();
			},
			messageId,
			messageNr
		);
	}

	private EbMSEvent getEvent(String messageId)
	{
		try
		{
			return jdbcTemplate.queryForObject(
				"select time_to_live, time_stamp, retries" +
				" from ebms_event" +
				" where message_id = ?",
				(RowMapper<EbMSEvent>)(rs,rowNum) ->
				{
					return EbMSEvent.of(toInstant(rs.getTimestamp("time_to_live")),toInstant(rs.getTimestamp("time_stamp")),rs.getInt("retries"));
				},
				messageId
			);
		}
		catch (EmptyResultDataAccessException e)
		{
			return null;
		}
	}

	private List<EbMSEventLog> getEvents(String messageId)
	{
		return jdbcTemplate.query(
			"select message_id, time_stamp, uri, status, error_message" + 
			" from ebms_event_log" + 
			" where message_id = ?",
			(RowMapper<EbMSEventLog>)(rs,rowNum) ->
			{
				return EbMSEventLog.builder()
						.timestamp(toInstant(rs.getTimestamp("time_stamp")))
						.uri(rs.getString("uri"))
						.status(EbMSEventStatus.get(rs.getInt("status")))
						.errorMessage(rs.getString("error_message"))
						.build();
			},
			messageId
		);
	}
	
	@Override
	public List<String> selectMessageIds(String cpaId, String fromRole, String toRole, EbMSMessageStatus...status)
	{
		return jdbcTemplate.queryForList(
			"select message_id" + 
			" from ebms_message" + 
			" where cpa_id = ?" +
			" and from_role = ?" +
			" and to_role = ?" +
			" and status in (" + join(status,",") + ")" +
			" order by time_stamp desc",
			String.class,
			cpaId,
			fromRole,
			toRole
		);
	}
	
	@Override
	public HashMap<LocalDateTime,Integer> selectMessageTraffic(LocalDateTime from, LocalDateTime to, TimeUnit timeUnit, EbMSMessageStatus...status)
	{
		val result = new HashMap<LocalDateTime,Integer>();
		jdbcTemplate.query(
			"select trunc(time_stamp,'" + getDateFormat(timeUnit.getSqlDateFormat()) + "') time, count(*) nr" + 
			" from ebms_message" + 
			" where time_stamp >= ? " +
			" and time_stamp < ?" +
			(status.length == 0 ? " and status is not null" : " and status in (" + join(status,",") + ")") +
			" group by trunc(time_stamp,'" + getDateFormat(timeUnit.getSqlDateFormat()) + "')",
			(RowMapper<Object>)(rs,rowNum) ->
			{
				result.put(rs.getTimestamp("time").toLocalDateTime(),rs.getInt("nr"));
				return null;
			},
			Timestamp.valueOf(from),
			Timestamp.valueOf(to)
		);
		return result;
	}
	
	protected String getDateFormat(String timeUnitDateFormat)
	{
		return Match(timeUnitDateFormat).of(
				Case($("mm"),"mi"),
				Case($(),timeUnitDateFormat));
	}

	protected String join(EbMSMessageStatus[] array, String delimiter)
	{
		return Arrays.stream(array).map(s -> Integer.toString(s.getId())).collect(Collectors.joining(delimiter));
	}
	
	@Override
	public void printMessagesToCSV(final CSVPrinter printer, EbMSMessageFilter filter)
	{
		val parameters = new ArrayList<Object>();
		val rowMapper = EbMSMessageRowMapper.builder().build();
		jdbcTemplate.query(
			rowMapper.getBaseQuery() +
			" where 1 = 1" +
			getMessageFilter(filter,parameters) +
			" order by time_stamp desc",
			parameters.toArray(new Object[0]),
			(RowMapper<Object>)(rs,rowNum) ->
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
			}
		);
	}

	@Override
	public void writeMessageToZip(String messageId, int messageNr, final ZipOutputStream zip)
	{
		try
		{
			jdbcTemplate.queryForObject(
				"select content" + 
				" from ebms_message" + 
				" where message_id = ?" +
				" and message_nr = ?",
				(RowMapper<Object>)(rs,rowNum) ->
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
				},
				messageId,
				messageNr
			);
			writeAttachmentsToZip(messageId, messageNr, zip);
		}
		catch (DataAccessException e)
		{
			throw new DAOException(e);
		}
	}

	protected void writeAttachmentsToZip(String messageId, int messageNr, final ZipOutputStream zip)
	{
		jdbcTemplate.query(
			"select name, content_id, content_type, content" + 
			" from ebms_attachment" + 
			" where message_id = ?" +
			" and message_nr = ?",
			(RowMapper<Object>)(rs,rowNum) ->
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
			},
			messageId,
			messageNr
		);
	}
	
	protected String getMessageFilter(EbMSMessageFilter messageFilter, List<Object> parameters)
	{
		val result = new StringBuffer();
		if (messageFilter != null)
		{
			if (messageFilter.getCpaId() != null)
			{
				parameters.add(messageFilter.getCpaId());
				result.append(" and cpa_id = ?");
			}
			if (messageFilter.getFromParty() != null)
			{
				if (messageFilter.getFromParty().getPartyId() != null)
				{
					parameters.add(messageFilter.getFromParty().getPartyId());
					result.append(" and from_party_id = ?");
				}
				if (messageFilter.getFromParty().getRole() != null)
				{
					parameters.add(messageFilter.getFromParty().getRole());
					result.append(" and from_role = ?");
				}
			}
			if (messageFilter.getToParty() != null)
			{
				if (messageFilter.getToParty().getPartyId() != null)
				{
					parameters.add(messageFilter.getToParty().getPartyId());
					result.append(" and to_party_id = ?");
				}
				if (messageFilter.getToParty().getRole() != null)
				{
					parameters.add(messageFilter.getToParty().getRole());
					result.append(" and to_role = ?");
				}
			}
			if (messageFilter.getService() != null)
			{
				parameters.add(messageFilter.getService());
				result.append(" and service = ?");
			}
			if (messageFilter.getAction() != null)
			{
				parameters.add(messageFilter.getAction());
				result.append(" and action = ?");
			}
			if (messageFilter.getConversationId() != null)
			{
				parameters.add(messageFilter.getConversationId());
				result.append(" and conversation_id = ?");
			}
			if (messageFilter.getMessageId() != null)
			{
				parameters.add(messageFilter.getMessageId());
				result.append(" and message_id = ?");
			}
			if (messageFilter.getMessageNr() != null)
			{
				parameters.add(messageFilter.getMessageNr());
				result.append(" and message_nr = ?");
			}
			if (messageFilter.getRefToMessageId() != null)
			{
				parameters.add(messageFilter.getRefToMessageId());
				result.append(" and ref_to_message_id = ?");
			}
			if (messageFilter.getStatuses().size() > 0)
			{
				String ids = messageFilter.getStatuses().stream().map(s -> Integer.toString(s.getId())).collect(Collectors.joining(","));
				result.append(" and status in (" + ids + ")");
			}
			if (messageFilter.getServiceMessage() != null)
			{
				parameters.add(EbMSAction.EBMS_SERVICE_URI);
				if (messageFilter.getServiceMessage())
					result.append(" and service = ?");
				else
					result.append(" and service <> ?");
			}
			if (messageFilter.getFrom() != null)
			{
				parameters.add(messageFilter.getFrom());
				result.append(" and time_stamp >= ?");
			}
			if (messageFilter.getTo() != null)
			{
				parameters.add(messageFilter.getTo());
				result.append(" and time_stamp < ?");
			}
		}
		return result.toString();
	}
	
}