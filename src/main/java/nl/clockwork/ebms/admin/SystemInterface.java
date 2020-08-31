package nl.clockwork.ebms.admin;

public interface SystemInterface
{
	default void setProperty(String key, String value)
	{
		System.setProperty(key,value);
	}

	default void println(String s)
	{
		System.out.println(s);
	}

	default void exit(int status)
	{
		System.exit(status);
	}
}
