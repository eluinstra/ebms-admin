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

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
		InputStream input = this.getClass().getResourceAsStream(((HttpServletRequest)request).getRequestURI());
		if (input == null)
			((HttpServletResponse)response).setStatus(204);
		else
		{
			((HttpServletResponse)response).setStatus(200);
			((HttpServletResponse)response).setContentType(getContentType(((HttpServletRequest)request).getPathInfo()));
			//System.out.println(URLConnection.guessContentTypeFromName(((HttpServletRequest)request).getRequestURI()));
			//System.out.println(new MimetypesFileTypeMap().getContentType(((HttpServletRequest)request).getRequestURI()));
			//System.out.println(URLConnection.getFileNameMap().getContentTypeFor(((HttpServletRequest)request).getRequestURI()));
			IOUtils.copy(input,response.getOutputStream());
		}
	}

	private String getContentType(String pathInfo)
	{
		String result = null;
		if (pathInfo.endsWith(".css"))
			return "text/css";
		if (pathInfo.endsWith(".js"))
			return "text/javascript";
		if (pathInfo.endsWith(".gif"))
			return "image/gif";
		if (pathInfo.endsWith(".eot"))
			return "application/vnd.ms-fontobject";
		if (pathInfo.endsWith(".svg"))
			return "image/svg+xml";
		if (pathInfo.endsWith(".ttf"))
			return "font/ttf";
		if (pathInfo.endsWith(".woff"))
			return "application/font-woff";
		return result;
	}
}
