package nl.clockwork.ebms.admin.dao;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import lombok.AccessLevel;
import lombok.val;
import lombok.experimental.FieldDefaults;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class AdminDAOConfig
{
	@Autowired
	DataSource dataSource;

	@Bean("ebMSAdminDAO")
	public EbMSDAO ebMSDAO() throws Exception
	{
		val transactionManager = new DataSourceTransactionManager(dataSource);
		val transactionTemplate = new TransactionTemplate(transactionManager);
		val jdbcTemplate = new JdbcTemplate(dataSource);
		return new EbMSDAOFactory(dataSource,transactionTemplate,jdbcTemplate).getObject();
	}
}
