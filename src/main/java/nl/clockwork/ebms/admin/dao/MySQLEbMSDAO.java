/*
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

import com.querydsl.sql.SQLQueryFactory;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import nl.clockwork.ebms.admin.model.EbMSAttachment;
import nl.clockwork.ebms.admin.web.Utils;
import nl.clockwork.ebms.querydsl.CachedOutputStreamType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;

public class MySQLEbMSDAO extends AbstractEbMSDAO
{
	public MySQLEbMSDAO(JdbcTemplate jdbcTemplate, SQLQueryFactory queryFactory)
	{
		super(jdbcTemplate, queryFactory);
	}

	@Override
	public EbMSAttachment findAttachment(String messageId, int messageNr, String contentId)
	{

		return jdbcTemplate.queryForObject(
				"select a.name, a.content_id, a.content_type, a.content"
						+ " from ebms_message m, ebms_attachment a"
						+ " where m.message_id = ?"
						+ " and m.message_nr = ?"
						+ " and m.id = a.ebms_message_id"
						+ " and a.content_id = ?",
				(rs, rowNum) ->
				{
					try
					{
						return new EbMSAttachment(
								rs.getString("name"),
								rs.getString("content_id"),
								rs.getString("content_type"),
								CachedOutputStreamType.createCachedOutputStream(rs.getBinaryStream("content")));
					}
					catch (IOException e)
					{
						throw new SQLException(e);
					}
				},
				messageId,
				messageNr,
				contentId);
	}

	protected List<EbMSAttachment> getAttachments(String messageId, int messageNr)
	{
		return jdbcTemplate.query(
				"select a.name, a.content_id, a.content_type"
						+ " from ebms_message m, ebms_attachment a"
						+ " where m.message_id = ?"
						+ " and m.message_nr = ?"
						+ " and m.id = a.ebms_message_id",
				(rs, rownNum) -> new EbMSAttachment(rs.getString("name"), rs.getString("content_id"), rs.getString("content_type")),
				messageId,
				messageNr);
	}

	@Override
	protected void writeAttachmentsToZip(String messageId, int messageNr, final ZipOutputStream zip)
	{
		jdbcTemplate.query(
				"select a.name, a.content_id, a.content_type, a.content"
						+ " from ebms_message m, ebms_attachment a"
						+ " where m.message_id = ?"
						+ " and m.message_nr = ?"
						+ " and m.id = a.ebms_message_id",
				(rs, rowNum) ->
				{
					try
					{
						ZipEntry entry = new ZipEntry(
								"attachments/"
										+ (StringUtils.isEmpty(rs.getString("name"))
												? rs.getString("content_id") + Utils.getFileExtension(rs.getString("content_type"))
												: rs.getString("name")));
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
				messageNr);
	}
}
