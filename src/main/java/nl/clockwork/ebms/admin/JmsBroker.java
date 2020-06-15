package nl.clockwork.ebms.admin;

import nl.clockwork.ebms.jms.EbMSBrokerFactoryBean;

public class JmsBroker
{
	public static void main(String[] args) throws Exception
	{
	 	//System.setProperty("activemq.base",System.getProperty("user.dir"));
	  EbMSBrokerFactoryBean.init(true,"classpath:nl/clockwork/ebms/activemq.xml");
	  System.out.println("Broker started");
	  Thread.currentThread().join();
	}
}
