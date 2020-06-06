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

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.support.TransactionTemplate;

import lombok.val;
import nl.clockwork.ebms.EbMSMessageStatus;
import nl.clockwork.ebms.admin.dao.AbstractEbMSDAO;
import nl.clockwork.ebms.admin.model.EbMSAttachment;
import nl.clockwork.ebms.admin.web.Utils;
import nl.clockwork.ebms.admin.web.message.EbMSMessageFilter;
import nl.clockwork.ebms.admin.web.message.TimeUnit;

public class EbMSDAOImpl extends AbstractEbMSDAO
{
	public EbMSDAOImpl(TransactionTemplate transactionTemplate, JdbcTemplate jdbcTemplate)
	{
		super(transactionTemplate,jdbcTemplate);
	}

	@Override
	public String selectCPAsQuery(long first, long count)
	{
		return "select * from cpa" +
			" order by cpa_id" +
			" limit " + count + " offset " + first
		;
	}
	
	@Override
	public String selectMessagesQuery(EbMSMessageFilter filter, long first, long count, List<Object> parameters)
	{
		return EbMSMessageRowMapper.builder().build().getBaseQuery() +
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

	protected List<EbMSAttachment> getAttachments(String messageId, int messageNr)
	{
		return jdbcTemplate.query(
			"select a.name, a.content_id, a.content_type" + 
			" from ebms_message m, ebms_attachment a" + 
			" where m.message_id = ?" +
			" and m.message_nr = ?" +
			" and m.id = a.ebms_message_id",
			(RowMapper<EbMSAttachment>)(rs,rownNum) ->
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

	@Override
	public HashMap<LocalDateTime,Integer> selectMessageTraffic(LocalDateTime from, LocalDateTime to, TimeUnit timeUnit, EbMSMessageStatus...status)
	{
		val result = new HashMap<LocalDateTime,Integer>();
		jdbcTemplate.query(
			//"select date_format(time_stamp,'" + getDateFormat(timeUnit.getTimeUnitDateFormat()) + "') time, count(*) nr" + 
			"select str_to_date(date_format(time_stamp,'" + getDateFormat(timeUnit.getSqlDateFormat()) + "'),'%Y-%m-%d %k:%i:%s') time, count(*) nr" + 
			" from ebms_message" + 
			" where time_stamp >= ? " +
			" and time_stamp < ?" +
			(status.length == 0 ? " and status is not null" : " and status in (" + join(status,",") + ")") +
			" group by date_format(time_stamp,'" + getDateFormat(timeUnit.getSqlDateFormat()) + "')",
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

	protected void writeAttachmentsToZip(String messageId, int messageNr, final ZipOutputStream zip)
	{
		jdbcTemplate.query(
			"select a.name, a.content_id, a.content_type, a.content" + 
			" from ebms_message m, ebms_attachment a" + 
			" where m.message_id = ?" +
			" and m.message_nr = ?" +
			" and m.id = a.ebms_message_id",
			(RowMapper<Object>)(rs,rowNum) ->
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
			},
			messageId,
			messageNr
		);
	}
	
	protected String getDateFormat(String timeUnitDateFormat)
	{
		return Match(timeUnitDateFormat).of(
				Case($("mm"),"%Y-%m-%d %k:%i:00"),
				Case($("HH"),"%Y-%m-%d %k:00:00"),
				Case($("dd"),"%Y-%m-%d 00:00:00"),
				Case($("MM"),"%Y-%m-01 00:00:00"),
				Case($(),timeUnitDateFormat));
	}
}
