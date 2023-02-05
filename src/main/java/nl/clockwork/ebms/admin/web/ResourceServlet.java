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

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static nl.clockwork.ebms.Predicates.endsWith;

import java.io.IOException;
import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.val;
import org.apache.wicket.util.io.IOUtils;

public class ResourceServlet extends GenericServlet
{
	private static final long serialVersionUID = 1L;

	@Override
	public void init() throws ServletException
	{
		super.init();
	}

	@Override
	public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException
	{
		val input = this.getClass().getResourceAsStream(((HttpServletRequest)request).getServletPath() + ((HttpServletRequest)request).getPathInfo());
		if (input == null)
			((HttpServletResponse)response).setStatus(204);
		else
		{
			((HttpServletResponse)response).setStatus(200);
			((HttpServletResponse)response).setContentType(getContentType(((HttpServletRequest)request).getPathInfo()));
			IOUtils.copy(input, response.getOutputStream());
		}
	}

	private String getContentType(String pathInfo)
	{
		return Match(pathInfo).of(
				Case($(endsWith(".css")), "text/css"),
				Case($(endsWith(".js")), "text/javascript"),
				Case($(endsWith(".gif")), "image/gif"),
				Case($(endsWith(".eot")), "application/vnd.ms-fontobject"),
				Case($(endsWith(".svg")), "image/svg+xml"),
				Case($(endsWith(".ttf")), "font/ttf"),
				Case($(endsWith(".woff")), "application/font-woff"),
				Case($(), (String)null));
	}
}