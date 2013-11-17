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

import nl.clockwork.ebms.admin.dao.AbstractDAOFactory.DefaultDAOFactory;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class EbMSDAOFactory extends DefaultDAOFactory<EbMSDAO>
{
	protected TransactionTemplate transactionTemplate;
	protected JdbcTemplate jdbcTemplate;

	@Override
	public Class<EbMSDAO> getObjectType()
	{
		return nl.clockwork.ebms.admin.dao.EbMSDAO.class;
	}

	@Override
	public EbMSDAO createHSqlDbDAO()
	{
		return new nl.clockwork.ebms.admin.dao.hsqldb.EbMSDAOImpl(transactionTemplate,jdbcTemplate);
	}

	@Override
	public EbMSDAO createMySqlDAO()
	{
		return new nl.clockwork.ebms.admin.dao.mysql.EbMSDAOImpl(transactionTemplate,jdbcTemplate);
	}

	@Override
	public EbMSDAO createPostgresDAO()
	{
		return new nl.clockwork.ebms.admin.dao.postgresql.EbMSDAOImpl(transactionTemplate,jdbcTemplate);
	}

	@Override
	public EbMSDAO createOracleDAO()
	{
		return new nl.clockwork.ebms.admin.dao.oracle.EbMSDAOImpl(transactionTemplate,jdbcTemplate);
	}

	@Override
	public EbMSDAO createMsSqlDAO()
	{
		return new nl.clockwork.ebms.admin.dao.mssql.EbMSDAOImpl(transactionTemplate,jdbcTemplate);
	}

	public void setTransactionTemplate(TransactionTemplate transactionTemplate)
	{
		this.transactionTemplate = transactionTemplate;
	}
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate)
	{
		this.jdbcTemplate = jdbcTemplate;
	}

}
