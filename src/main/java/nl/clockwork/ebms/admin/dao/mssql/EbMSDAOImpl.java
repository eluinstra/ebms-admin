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

import java.util.List;

import nl.clockwork.ebms.admin.dao.AbstractEbMSDAO;
import nl.clockwork.ebms.admin.web.message.EbMSMessageFilter;

import org.springframework.jdbc.core.JdbcTemplate;
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
		//return CPARowMapper.getBaseQuery() +
		//	" order by cpa_id" +
		//	" offset " + first + " rows fetch " + count + " rows only"
		//;
		return "select * from (" +
			CPARowMapper.getBaseQuery().replaceFirst("select ","select row_number() over (order by cpa_id) as rn, ") +
			//") where rn >= " + (first + 1) + " and rn < " + (first + 1 + count)
			") where rn between " + first + " and " + (first + count)
		;
	}
	
	@Override
	public String selectMessagesQuery(EbMSMessageFilter filter, long first, long count, List<Object> parameters)
	{
		//return new EbMSMessageRowMapper().getBaseQuery() +
		//	" where 1 = 1" +
		//	getMessageFilter(filter,parameters) +
		//	" order by time_stamp desc" +
		//	" offset " + first + " rows fetch " + count + " rows only"
		//;
		return "select * from (" +
			new EbMSMessageRowMapper().getBaseQuery().replaceFirst("select ","select row_number() over (order by time_stamp desc) as rn, ") +
			" where 1 = 1" +
			getMessageFilter(filter,parameters) +
			//") where rn >= " + (first + 1) + " and rn < " + (first + 1 + count)
			") where rn between " + first + " and " + (first + count)
		;
	}
}
