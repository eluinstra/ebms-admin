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
package nl.clockwork.ebms.admin.web.message;

import org.apache.cxf.common.util.StringUtils;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.encoding.UrlEncoder;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.resource.IResourceStream;

import lombok.val;
import nl.clockwork.ebms.admin.model.EbMSAttachment;
import nl.clockwork.ebms.admin.web.Utils;

public class DownloadEbMSAttachmentLinkX extends Link<EbMSAttachment>
{
	private static final long serialVersionUID = 1L;

	public DownloadEbMSAttachmentLinkX(String id, IModel<EbMSAttachment> model)
	{
		super(id,Args.notNull(model,"attachment"));
	}

	@Override
	public void onClick()
	{
		val o = getModelObject();
		val fileName = UrlEncoder.QUERY_INSTANCE.encode(
				StringUtils.isEmpty(o.getName()) ? o.getContentId() + Utils.getFileExtension(o.getContentType()) : o.getName(),
				getRequest().getCharset());
		val resourceStream = AttachmentResourceStream.of(o);
		getRequestCycle().scheduleRequestHandlerAfterCurrent(createRequestHandler(fileName,resourceStream));
	}

	private ResourceStreamRequestHandler createRequestHandler(String fileName, IResourceStream resourceStream)
	{
		return new ResourceStreamRequestHandler(resourceStream)
				.setFileName(fileName)
				.setContentDisposition(ContentDisposition.ATTACHMENT);
	}

}
