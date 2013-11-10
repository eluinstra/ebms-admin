package nl.clockwork.ebms.admin.web.configuration;

public class JdbcURL
{
	private String jdbcHost = "localhost";
	private Integer jdbcPort = 9001;
	private String jdbcDatabase = "ebms";

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
