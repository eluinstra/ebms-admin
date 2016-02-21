package nl.clockwork.ebms.admin.web;

import java.io.Serializable;

import org.apache.wicket.Component;

public class LocalizedStringResource implements Serializable
{
	private static final long serialVersionUID = 1L;
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
