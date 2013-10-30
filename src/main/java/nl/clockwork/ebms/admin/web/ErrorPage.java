package nl.clockwork.ebms.admin.web;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.authorization.UnauthorizedActionException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.protocol.http.PageExpiredException;

public class ErrorPage extends BasePage
{
	private static final long serialVersionUID = 1L;
	private String pageTitle;
	private String message;
	private boolean showStackTrace;
	private String stackTrace;

	public ErrorPage(Exception exception)
	{
		add(new BookmarkablePageLink<Object>("continue",WicketApplication.get().getHomePage()));
		if (exception instanceof PageExpiredException)
		{
			pageTitle = getLocalizer().getString("pageExpired",this);
			message = getLocalizer().getString("pageExpired.message",this);
		}
		else if(exception instanceof UnauthorizedActionException)
		{
			pageTitle = getLocalizer().getString("unauthorizedAction",this);
			message = getLocalizer().getString("unauthorizedAction.message",this);
		}
		else
		{
			pageTitle = getLocalizer().getString("error",this);
			message = getLocalizer().getString("error.message",this);
			showStackTrace = RuntimeConfigurationType.DEVELOPMENT.equals(getApplication().getConfigurationType());
		}
		add(new Label("message",message));
		//add(new Label("message",e.getMessage()));
		if (showStackTrace)
		{
			StringWriter sw = new StringWriter();
			exception.printStackTrace(new PrintWriter(sw));
			stackTrace = sw.getBuffer().toString();
		}
		add(new Label("stackTrace",stackTrace).setVisible(showStackTrace));
	}

	@Override
	public String getPageTitle()
	{
		return pageTitle;
	}

}
