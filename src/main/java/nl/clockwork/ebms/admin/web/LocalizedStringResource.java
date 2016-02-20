package nl.clockwork.ebms.admin.web;

import org.apache.wicket.Component;

public class LocalizedStringResource
{
	private String key;
	private Component component;

	public LocalizedStringResource(String key, Component component)
	{
		this.key = key;
		this.component = component;
	}
	public String getKey()
	{
		return key;
	}
	public Component getComponent()
	{
		return component;
	}
}
