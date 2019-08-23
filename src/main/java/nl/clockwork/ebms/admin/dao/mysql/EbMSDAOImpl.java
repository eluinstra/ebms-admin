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
package nl.clockwork.ebms.admin.dao.mysql;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import nl.clockwork.ebms.Constants.EbMSMessageStatus;
import nl.clockwork.ebms.admin.Constants.TimeUnit;
import nl.clockwork.ebms.admin.dao.AbstractEbMSDAO;
import nl.clockwork.ebms.admin.model.EbMSAttachment;
import nl.clockwork.ebms.admin.web.Utils;
import nl.clockwork.ebms.admin.web.message.EbMSMessageFilter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.support.TransactionTemplate;

public class EbMSDAOImpl extends AbstractEbMSDAO
{

	public EbMSDAOImpl(TransactionTemplate transactionTemplate, JdbcTemplate jdbcTemplate)
	{
		super(transactionTemplate,jdbcTemplate);
	}

	@Override
	public String selectCPAsQuery(long first, long count)
	{
		return CPARowMapper.getBaseQuery() +
			" order by cpa_id" +
			" limit " + count + " offset " + first
		;
	}
	
	@Override
	public String selectMessagesQuery(EbMSMessageFilter filter, long first, long count, List<Object> parameters)
	{
		return new EbMSMessageRowMapper().getBaseQuery() +
			" where 1 = 1" +
			getMessageFilter(filter,parameters) +
			" order by time_stamp desc" +
			" limit " + count + " offset " + first
		;
	}

	@Override
	public EbMSAttachment findAttachment(String messageId, int messageNr, String contentId)
	{
		return jdbcTemplate.queryForObject(
			"select a.name, a.content_id, a.content_type, a.content" + 
			" from ebms_message m, ebms_attachment a" + 
			" where m.message_id = ?" +
			" and m.message_nr = ?" +
			" and m.id = a.ebms_message_id" +
			" and a.content_id = ?",
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
			"select a.name, a.content_id, a.content_type" + 
			" from ebms_message m, ebms_attachment a" + 
			" where m.message_id = ?" +
			" and m.message_nr = ?" +
			" and m.id = a.ebms_message_id",
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

	@Override
	public HashMap<Date,Number> selectMessageTraffic(Date from, Date to, TimeUnit timeUnit, EbMSMessageStatus...status)
	{
		final HashMap<Date,Number> result = new HashMap<>();
		jdbcTemplate.query(
			//"select date_format(time_stamp,'" + getDateFormat(timeUnit.getTimeUnitDateFormat()) + "') time, count(*) nr" + 
			"select str_to_date(date_format(time_stamp,'" + getDateFormat(timeUnit.getTimeUnitDateFormat()) + "'),'%Y-%m-%d %k:%i:%s') time, count(*) nr" + 
			" from ebms_message" + 
			" where time_stamp >= ? " +
			" and time_stamp < ?" +
			(status.length == 0 ? " and status is not null" : " and status in (" + join(status,",") + ")") +
			" group by date_format(time_stamp,'" + getDateFormat(timeUnit.getTimeUnitDateFormat()) + "')",
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

	protected void writeAttachmentsToZip(String messageId, int messageNr, final ZipOutputStream zip)
	{
		jdbcTemplate.query(
			"select a.name, a.content_id, a.content_type, a.content" + 
			" from ebms_message m, ebms_attachment a" + 
			" where m.message_id = ?" +
			" and m.message_nr = ?" +
			" and m.id = a.ebms_message_id",
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
	
	protected String getDateFormat(String timeUnitDateFormat)
	{
		if ("mm".equals(timeUnitDateFormat))
			return "%Y-%m-%d %k:%i:00";
		else if ("HH".equals(timeUnitDateFormat))
			return "%Y-%m-%d %k:00:00";
		else if ("dd".equals(timeUnitDateFormat))
			return "%Y-%m-%d 00:00:00";
		else if ("MM".equals(timeUnitDateFormat))
			return "%Y-%m-01 00:00:00";
		else
			return timeUnitDateFormat;
	}

}
