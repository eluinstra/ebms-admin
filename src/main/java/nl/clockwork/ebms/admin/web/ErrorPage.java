/**
 * Copyright 2013 Clockwork
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.clockwork.ebms.admin.web;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.authorization.UnauthorizedActionException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.protocol.http.PageExpiredException;

public class ErrorPage extends BasePage
{
	private static final long serialVersionUID = 1L;
	protected transient Log logger = LogFactory.getLog(this.getClass());
	private String pageTitle;
	private String message;
	private boolean showStackTrace;
	private String stackTrace;

	public ErrorPage(Exception exception)
	{
		logger.error("",exception);
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
