package nl.clockwork.ebms.admin.web;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.Link;

public class BackLink extends Link<Void>
{
	private static final long serialVersionUID = 1L;
	private Class<? extends Page> responsePage;

	public BackLink(String id, Class<? extends Page> responsePage)
	{
		super(id);
		this.responsePage = responsePage;
	}

	@Override
	public void onClick()
	{
		setResponsePage(responsePage);
	}
}
