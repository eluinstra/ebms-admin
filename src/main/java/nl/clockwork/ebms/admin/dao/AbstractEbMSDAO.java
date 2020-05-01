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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import nl.clockwork.ebms.Constants;
import nl.clockwork.ebms.EbMSMessageStatus;
import nl.clockwork.ebms.admin.Constants.TimeUnit;
import nl.clockwork.ebms.admin.model.CPA;
import nl.clockwork.ebms.admin.model.EbMSAttachment;
import nl.clockwork.ebms.admin.model.EbMSEvent;
import nl.clockwork.ebms.admin.model.EbMSEventLog;
import nl.clockwork.ebms.admin.model.EbMSMessage;
import nl.clockwork.ebms.admin.web.Utils;
import nl.clockwork.ebms.admin.web.message.EbMSMessageFilter;
import nl.clockwork.ebms.dao.DAOException;
import nl.clockwork.ebms.event.processor.EbMSEventStatus;

import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.support.TransactionTemplate;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@AllArgsConstructor
public abstract class AbstractEbMSDAO implements EbMSDAO
{
	public static class CPARowMapper implements RowMapper<CPA>
	{
		public static String getBaseQuery()
		{
			return "select cpa_id, cpa from cpa";
		}

		@Override
		public CPA mapRow(ResultSet rs, int rowNum) throws SQLException
		{
			return new CPA(rs.getString("cpa_id"),rs.getString("cpa"));
		}
	}
	
	public static class EbMSMessageRowMapper implements RowMapper<EbMSMessage>
	{
		private boolean detail;

		public EbMSMessageRowMapper()
		{
			this(false);
		}
		
		public EbMSMessageRowMapper(boolean detail)
		{
			this.detail = detail;
		}

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
			EbMSMessage result = new EbMSMessage();
			result.setTimestamp(rs.getTimestamp("time_stamp"));
			result.setCpaId(rs.getString("cpa_id"));
			result.setConversationId(rs.getString("conversation_id"));
			result.setMessageId(rs.getString("message_id"));
			result.setMessageNr(rs.getInt("message_nr"));
			result.setRefToMessageId(rs.getString("ref_to_message_id"));
			result.setTimeToLive(rs.getTimestamp("time_to_live"));
			result.setFromPartyId(rs.getString("from_party_id"));
			result.setFromRole(rs.getString("from_role"));
			result.setToPartyId(rs.getString("to_party_id"));
			result.setToRole(rs.getString("to_role"));
			result.setService(rs.getString("service"));
			result.setAction(rs.getString("action"));
			if (detail)
				result.setContent(rs.getString("content"));
			result.setStatus(rs.getObject("status") == null ? null : EbMSMessageStatus.get(rs.getInt("status")));
			result.setStatusTime(rs.getTimestamp("status_time"));
			return result;
		}
	}

	@NonNull
	TransactionTemplate transactionTemplate;
	@NonNull
	JdbcTemplate jdbcTemplate;

	@Override
	public CPA findCPA(String cpaId)
	{
		try
		{
			return jdbcTemplate.queryForObject(
				CPARowMapper.getBaseQuery() +
				" where cpa_id = ?",
				new CPARowMapper(),
				cpaId
			);
		}
		catch (EmptyResultDataAccessException e)
		{
			return null;
		}
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
			new CPARowMapper()
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
		EbMSMessage result = jdbcTemplate.queryForObject(
			new EbMSMessageRowMapper(true).getBaseQuery() + 
			" where message_id = ?" +
			" and message_nr = ?",
			new EbMSMessageRowMapper(true),
			messageId,
			messageNr
		);
		result.setAttachments(getAttachments(messageId,messageNr));
		result.getAttachments().forEach(a -> a.setMessage(result));
		result.setEvent(getEvent(messageId));
		result.setEvents(getEvents(messageId));
		result.getEvents().forEach(e -> e.setMessage(result));
		return result;
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
			Constants.EBMS_SERVICE_URI
		) > 0;
	}

	@Override
	public EbMSMessage findResponseMessage(String messageId)
	{
		EbMSMessage result = jdbcTemplate.queryForObject(
			new EbMSMessageRowMapper(true).getBaseQuery() + 
			" where ref_to_message_id = ?" +
			" and message_nr = ?" +
			" and service = ?",
			new EbMSMessageRowMapper(true),
			messageId,
			0,
			Constants.EBMS_SERVICE_URI
		);
		result.setAttachments(new ArrayList<>());
		result.setEvents(getEvents(messageId));
		result.getEvents().forEach(e -> e.setMessage(result));
		return result;
	}

	@Override
	public int countMessages(EbMSMessageFilter filter)
	{
		List<Object> parameters = new ArrayList<>();
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
		List<Object> parameters = new ArrayList<>();
		return jdbcTemplate.query(
			selectMessagesQuery(filter,first,count,parameters),
			parameters.toArray(new Object[0]),
			new EbMSMessageRowMapper()
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
			new RowMapper<EbMSAttachment>()
			{
				@Override
				public EbMSAttachment mapRow(ResultSet rs, int rowNum) throws SQLException
				{
					return new EbMSAttachment(rs.getString("name"),rs.getString("content_id"),rs.getString("content_type"),rs.getBytes("content"));
				}
			},
			messageId,
			messageNr,
			contentId
		);
	}

	protected List<EbMSAttachment> getAttachments(String messageId, int messageNr)
	{
		return jdbcTemplate.query(
			"select name, content_id, content_type" + 
			" from ebms_attachment" + 
			" where message_id = ?" +
			" and message_nr = ?",
			new RowMapper<EbMSAttachment>()
			{
				@Override
				public EbMSAttachment mapRow(ResultSet rs, int rowNum) throws SQLException
				{
					return new EbMSAttachment(rs.getString("name"),rs.getString("content_id"),rs.getString("content_type"),null);
				}
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
				new RowMapper<EbMSEvent>()
				{
					@Override
					public EbMSEvent mapRow(ResultSet rs, int rowNum) throws SQLException
					{
						return new EbMSEvent(rs.getTimestamp("time_to_live"),rs.getTimestamp("time_stamp"),rs.getInt("retries"));
					}
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
			new RowMapper<EbMSEventLog>()
			{
				@Override
				public EbMSEventLog mapRow(ResultSet rs, int rowNum) throws SQLException
				{
					return new EbMSEventLog(rs.getTimestamp("time_stamp"),rs.getString("uri"),EbMSEventStatus.get(rs.getInt("status")),rs.getString("error_message"));
				}
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
	public HashMap<Date,Integer> selectMessageTraffic(Date from, Date to, TimeUnit timeUnit, EbMSMessageStatus...status)
	{
		final HashMap<Date,Integer> result = new HashMap<>();
		jdbcTemplate.query(
			"select trunc(time_stamp,'" + getDateFormat(timeUnit.getTimeUnitDateFormat()) + "') time, count(*) nr" + 
			" from ebms_message" + 
			" where time_stamp >= ? " +
			" and time_stamp < ?" +
			(status.length == 0 ? " and status is not null" : " and status in (" + join(status,",") + ")") +
			" group by trunc(time_stamp,'" + getDateFormat(timeUnit.getTimeUnitDateFormat()) + "')",
			new RowMapper<Object>()
			{
				@Override
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException
				{
					result.put(rs.getTimestamp("time"),rs.getInt("nr"));
					return null;
				}
			},
			from,
			to
		);
		return result;
	}
	
	protected String getDateFormat(String timeUnitDateFormat)
	{
		if ("mm".equals(timeUnitDateFormat))
			return "mi";
		else
			return timeUnitDateFormat;
	}

	protected String join(EbMSMessageStatus[] array, String delimiter)
	{
		return Arrays.stream(array).map(s -> Integer.toString(s.getId())).collect(Collectors.joining(delimiter));
	}
	
	@Override
	public void printMessagesToCSV(final CSVPrinter printer, EbMSMessageFilter filter)
	{
		List<Object> parameters = new ArrayList<>();
		jdbcTemplate.query(
			new EbMSMessageRowMapper().getBaseQuery() +
			" where 1 = 1" +
			getMessageFilter(filter,parameters) +
			" order by time_stamp desc",
			parameters.toArray(new Object[0]),
			new RowMapper<Object>()
			{
				@Override
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException
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
				new RowMapper<Object>()
				{
					@Override
					public Object mapRow(ResultSet rs, int rowNum) throws SQLException
					{
						try
						{
							ZipEntry entry = new ZipEntry("message.xml");
							zip.putNextEntry(entry);
							zip.write(rs.getString("content").getBytes());
							zip.closeEntry();
							return null;
						}
						catch (IOException e)
						{
							throw new SQLException(e);
						}
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
			new RowMapper<Object>()
			{
				@Override
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException
				{
					try
					{
						ZipEntry entry = new ZipEntry("attachments/" + (StringUtils.isEmpty(rs.getString("name")) ? rs.getString("content_id") + Utils.getFileExtension(rs.getString("content_type")) : rs.getString("name")));
						entry.setComment("Content-Type: " + rs.getString("content_type"));
						zip.putNextEntry(entry);
						zip.write(rs.getBytes("content"));
						zip.closeEntry();
						return null;
					}
					catch (IOException e)
					{
						throw new SQLException(e);
					}
				}
			},
			messageId,
			messageNr
		);
	}
	
	protected String getMessageFilter(EbMSMessageFilter messageFilter, List<Object> parameters)
	{
		StringBuffer result = new StringBuffer();
		if (messageFilter != null)
		{
			if (messageFilter.getCpaId() != null)
			{
				parameters.add(messageFilter.getCpaId());
				result.append(" and cpa_id = ?");
			}
			if (messageFilter.getFromRole() != null)
			{
				if (messageFilter.getFromRole().getPartyId() != null)
				{
					parameters.add(messageFilter.getFromRole().getPartyId());
					result.append(" and from_party_id = ?");
				}
				if (messageFilter.getFromRole().getRole() != null)
				{
					parameters.add(messageFilter.getFromRole().getRole());
					result.append(" and from_role = ?");
				}
			}
			if (messageFilter.getToRole() != null)
			{
				if (messageFilter.getToRole().getPartyId() != null)
				{
					parameters.add(messageFilter.getToRole().getPartyId());
					result.append(" and to_party_id = ?");
				}
				if (messageFilter.getToRole().getRole() != null)
				{
					parameters.add(messageFilter.getToRole().getRole());
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
				parameters.add(Constants.EBMS_SERVICE_URI);
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