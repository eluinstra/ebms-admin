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
package nl.clockwork.ebms.admin.web.service.message;

import nl.clockwork.ebms.admin.web.Utils;
import nl.clockwork.ebms.model.EbMSDataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.encoding.UrlEncoder;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.resource.IResourceStream;

public class DownloadEbMSDataSourceLink extends Link<EbMSDataSource>
{
	private static final long serialVersionUID = 1L;

	public DownloadEbMSDataSourceLink(String id, EbMSDataSource ebMSDataSource)
	{
		super(id,Model.of(Args.notNull(ebMSDataSource,"ebMSDataSource")));
		add(new Label("name"));
	}

	@Override
	public void onClick()
	{
		final EbMSDataSource ebMSDataSource = getModelObject();
		String fileName = UrlEncoder.QUERY_INSTANCE.encode(StringUtils.isEmpty(ebMSDataSource.getName()) ? "ebMSDataSource" + Utils.getFileExtension(ebMSDataSource.getContentType()) : ebMSDataSource.getName(),getRequest().getCharset());
		IResourceStream resourceStream = new EbMSDataSourceResourceStream(ebMSDataSource);
		getRequestCycle().scheduleRequestHandlerAfterCurrent(createRequestHandler(fileName,resourceStream));
	}

	private ResourceStreamRequestHandler createRequestHandler(String fileName, IResourceStream resourceStream)
	{
		return new ResourceStreamRequestHandler(resourceStream)
		.setFileName(fileName)
		.setContentDisposition(ContentDisposition.ATTACHMENT);
	}

}
