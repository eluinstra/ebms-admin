/*
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


import javax.servlet.http.HttpServletResponse;
import org.apache.wicket.request.http.WebResponse;

public class PageNotFoundPage extends BasePage
{
	private static final long serialVersionUID = 1L;

	public PageNotFoundPage()
	{
		add(new HomePageLink("homePageLink"));
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
		return getLocalizer().getString("pageNotFound", this);
	}

}
