package nl.clockwork.ebms.admin.web.configuration;

public class JdbcURL
{
	private String host = "localhost";
	private Integer port = 9001;
	private String database = "ebms";

	public String getHost()
	{
		return host;
	}
	public void setHost(String host)
	{
		this.host = host;
	}
	public Integer getPort()
	{
		return port;
	}
	public void setPort(Integer port)
	{
		this.port = port;
	}
	public String getDatabase()
	{
		return database;
	}
	public void setDatabase(String database)
	{
		this.database = database;
	}
}
