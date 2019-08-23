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
package nl.clockwork.ebms.admin.dao.postgresql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import nl.clockwork.ebms.Constants.EbMSMessageStatus;
import nl.clockwork.ebms.admin.Constants.TimeUnit;
import nl.clockwork.ebms.admin.dao.AbstractEbMSDAO;
import nl.clockwork.ebms.admin.web.message.EbMSMessageFilter;

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
	public HashMap<Date,Number> selectMessageTraffic(Date from, Date to, TimeUnit timeUnit, EbMSMessageStatus...status)
	{
		final HashMap<Date,Number> result = new HashMap<>();
		jdbcTemplate.query(
			"select date_trunc('" + getDateFormat(timeUnit.getTimeUnitDateFormat()) + "',time_stamp) as time, count(*) as nr" + 
			" from ebms_message" + 
			" where time_stamp >= ? " +
			" and time_stamp < ?" +
			(status.length == 0 ? " and status is not null" : " and status in (" + join(status,",") + ")") +
			" group by date_trunc('" + getDateFormat(timeUnit.getTimeUnitDateFormat()) + "',time_stamp)",
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
			return "minute";
		else if ("HH".equals(timeUnitDateFormat))
			return "hour";
		else if ("dd".equals(timeUnitDateFormat))
			return "day";
		else if ("MM".equals(timeUnitDateFormat))
			return "month";
		else
			return timeUnitDateFormat;
	}

}
