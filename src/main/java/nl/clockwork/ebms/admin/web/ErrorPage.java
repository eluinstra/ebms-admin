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

import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.authorization.UnauthorizedActionException;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.protocol.http.PageExpiredException;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ErrorPage extends BasePage
{
	private enum ErrorType
	{
		ERROR("error"), PAGE_EXPIRED("pageExpired"), UNAUTHORIZED_ACTION("unauthorizedAction");
		
		private String title;

		private ErrorType(String title)
		{
			this.title = title;
		}
		public String getTitle()
		{
			return title;
		}
		public static ErrorType get(Exception exception)
		{
			if (exception instanceof PageExpiredException)
				return PAGE_EXPIRED;
			else if(exception instanceof UnauthorizedActionException)
				return UNAUTHORIZED_ACTION;
			else
				return ERROR;
		}
	}
	private static final long serialVersionUID = 1L;
	@NonNull
	ErrorType errorType;

	public ErrorPage(Exception exception)
	{
		log.error("",exception);
		errorType = ErrorType.get(exception);
		add(new WebMarkupContainer("error").add(new HomePageLink("homePageLink")).setVisible(ErrorType.ERROR.equals(errorType)));
		add(new WebMarkupContainer("pageExpired").add(new HomePageLink("homePageLink")).setVisible(ErrorType.PAGE_EXPIRED.equals(errorType)));
		add(new WebMarkupContainer("unauthorizedAction").add(new HomePageLink("homePageLink")).setVisible(ErrorType.UNAUTHORIZED_ACTION.equals(errorType)));
		val showStackTrace = RuntimeConfigurationType.DEVELOPMENT.equals(getApplication().getConfigurationType());
		val stackTrace = showStackTrace ? getStackTrace(exception) : null;
		add(new Label("stackTrace",stackTrace).setVisible(showStackTrace));
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
		return errorType.getTitle();
	}

	private String getStackTrace(Exception exception)
	{
		StringWriter sw = new StringWriter();
		exception.printStackTrace(new PrintWriter(sw));
		return sw.getBuffer().toString();
	}

}
