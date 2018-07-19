package nl.clockwork.ebms.event;

import java.util.HashMap;

import nl.clockwork.ebms.Constants.EbMSMessageStatus;

public class TestMessageRegistry
{
	private static TestMessageRegistry _this = new TestMessageRegistry();
	private HashMap<String,EbMSMessageStatus> registry = new HashMap<>();
	
	
	public static TestMessageRegistry getInstance() {
		return _this;
	}
	
	public void setMessageStatus (String messageId, EbMSMessageStatus status)
	{
		synchronized (registry) {
			registry.put(messageId.split("@")[0], status);
			registry.notify();
		}
	}
	
	public EbMSMessageStatus getStatus(String messageId)
	{
		return registry.get(messageId);
	}

	public EbMSMessageStatus waitFor(String messageId) throws InterruptedException
	{
		synchronized(registry) {
			registry.wait(10000);
		}
		return getStatus(messageId); 
	}
}
