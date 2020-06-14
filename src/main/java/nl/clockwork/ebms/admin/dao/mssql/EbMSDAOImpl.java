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
package nl.clockwork.ebms.admin.dao.mssql;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.support.TransactionTemplate;

import lombok.val;
import nl.clockwork.ebms.EbMSMessageStatus;
import nl.clockwork.ebms.admin.web.message.EbMSMessageFilter;
import nl.clockwork.ebms.admin.web.message.TimeUnit;

public class EbMSDAOImpl extends nl.clockwork.ebms.admin.dao.mysql.EbMSDAOImpl
{

	public EbMSDAOImpl(TransactionTemplate transactionTemplate, JdbcTemplate jdbcTemplate)
	{
		super(transactionTemplate,jdbcTemplate);
	}

	@Override
	public String selectCPAsQuery(long first, long count)
	{
		return "select a.* from (" +
				"select row_number() over (order by cpa_id) as rn, * from cpa" +
			") a where rn between " + first + " and " + (first + count)
		;
	}
	
	@Override
	public String selectMessagesQuery(EbMSMessageFilter filter, long first, long count, List<Object> parameters)
	{
		return "select a.* from (" +
			EbMSMessageRowMapper.builder().build().getBaseQuery().replaceFirst("select ","select row_number() over (order by time_stamp desc) as rn, ") +
			" where 1 = 1" +
			getMessageFilter(filter,parameters) +
			") a where rn between " + first + " and " + (first + count)
		;
	}

	@Override
	public HashMap<LocalDateTime,Integer> selectMessageTraffic(LocalDateTime from, LocalDateTime to, TimeUnit timeUnit, EbMSMessageStatus...status)
	{
		val result = new HashMap<LocalDateTime,Integer>();
		jdbcTemplate.query(
			"select cast(" + getDateFormat(timeUnit.getSqlDateFormat()) + "as datetime) time, count(*) nr" + 
			" from ebms_message" + 
			" where time_stamp >= ? " +
			" and time_stamp < ?" +
			(status.length == 0 ? " and status is not null" : " and status in (" + join(status,",") + ")") +
			" group by " + getDateFormat(timeUnit.getSqlDateFormat()),
			(rs,rowNum) ->
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
				Case($("mm"),"cast(datepart(yyyy,time_stamp) as varchar(4)) + '-' + cast(datepart(mm,time_stamp) as varchar(2)) + '-' + cast(datepart(dd,time_stamp) as varchar(2)) + ' ' + cast(datepart(hh,time_stamp) as varchar(2)) + ':' + cast(datepart(mi,time_stamp) as varchar(2)) + ':' + '00'"), //return "yyyy-MM-dd HH:mm:00";
				Case($("HH"),"cast(datepart(yyyy,time_stamp) as varchar(4)) + '-' + cast(datepart(mm,time_stamp) as varchar(2)) + '-' + cast(datepart(dd,time_stamp) as varchar(2)) + ' ' + cast(datepart(hh,time_stamp) as varchar(2)) + ':' + '00' + ':' + '00'"), //return "yyyy-MM-dd HH:00:00";
				Case($("dd"),"cast(datepart(yyyy,time_stamp) as varchar(4)) + '-' + cast(datepart(mm,time_stamp) as varchar(2)) + '-' + cast(datepart(dd,time_stamp) as varchar(2)) + ' ' + '00' + ':' + '00' + ':' + '00'"), //return "yyyy-MM-dd 00:00:00";
				Case($("MM"),"cast(datepart(yyyy,time_stamp) as varchar(4)) + '-' + cast(datepart(mm,time_stamp) as varchar(2)) + '-' + '01' + ' ' + '00' + ':' + '00' + ':' + '00'"), //return "yyyy-MM-01 00:00:00";
				Case($(),timeUnitDateFormat));
	}
}
