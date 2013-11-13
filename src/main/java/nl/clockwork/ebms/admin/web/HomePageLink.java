package nl.clockwork.ebms.admin.web;

import org.apache.wicket.markup.html.link.BookmarkablePageLink;

public class HomePageLink extends BookmarkablePageLink<Void>
{
	private static final long serialVersionUID = 1L;

	public HomePageLink(String id)
	{
		super(id,WicketApplication.get().getHomePage());
	}
}