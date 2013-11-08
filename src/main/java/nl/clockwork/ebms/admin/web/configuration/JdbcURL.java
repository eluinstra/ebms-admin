package nl.clockwork.ebms.admin.web.configuration;

public class JdbcURL
{
	private String jdbcHost;
	private Integer jdbcPort;
	private String jdbcDatabase;

	public String getJdbcHost()
	{
		return jdbcHost;
	}
	public void setJdbcHost(String jdbcHost)
	{
		this.jdbcHost = jdbcHost;
	}
	public Integer getJdbcPort()
	{
		return jdbcPort;
	}
	public void setJdbcPort(Integer jdbcPort)
	{
		this.jdbcPort = jdbcPort;
	}
	public String getJdbcDatabase()
	{
		return jdbcDatabase;
	}
	public void setJdbcDatabase(String jdbcDatabase)
	{
		this.jdbcDatabase = jdbcDatabase;
	}
}
