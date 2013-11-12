package nl.clockwork.ebms.admin.web;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.border.Border;

public class BootstrapPanelBorder extends Border
{
	private static final long serialVersionUID = 1L;

	public BootstrapPanelBorder(String id, String title, Component...components)
	{
		super(id);
		addToBorder(new Label("title",title));
		add(components);
	}

}
