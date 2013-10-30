package nl.clockwork.ebms.admin.web;

import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.http.WebResponse;

public class PageNotFoundPage extends BasePage
{
	private static final long serialVersionUID = 1L;

	public PageNotFoundPage()
	{
		add(new BookmarkablePageLink<Void>("continue",WicketApplication.get().getHomePage()));
	}

	@Override
	protected void configureResponse(WebResponse response)
	{
		super.configureResponse(response);
		((HttpServletResponse)response.getContainerResponse()).setStatus(HttpServletResponse.SC_NOT_FOUND);
	}
	
	@Override
	public boolean isVersioned()
	{
		return false;
	}

	@Override
	public boolean isErrorPage()
	{
		return true;
	}

	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("pageNotFound",this);
	}

}
