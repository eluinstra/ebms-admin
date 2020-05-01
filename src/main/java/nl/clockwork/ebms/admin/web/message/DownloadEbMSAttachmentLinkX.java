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

import nl.clockwork.ebms.admin.model.EbMSAttachment;
import nl.clockwork.ebms.admin.web.Utils;

import org.apache.cxf.common.util.StringUtils;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.encoding.UrlEncoder;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.resource.IResourceStream;

import lombok.val;

public class DownloadEbMSAttachmentLinkX extends Link<EbMSAttachment>
{
	private static final long serialVersionUID = 1L;

	public DownloadEbMSAttachmentLinkX(String id, EbMSAttachment attachment)
	{
		super(id,Model.of(Args.notNull(attachment,"attachment")));
	}

	@Override
	public void onClick()
	{
		val attachment = getModelObject();
		val fileName = UrlEncoder.QUERY_INSTANCE.encode(StringUtils.isEmpty(attachment.getName()) ? attachment.getContentId() + Utils.getFileExtension(attachment.getContentType()) : attachment.getName(),getRequest().getCharset());
		val resourceStream = new AttachmentResourceStream(attachment);
		getRequestCycle().scheduleRequestHandlerAfterCurrent(createRequestHandler(fileName,resourceStream));
	}

	private ResourceStreamRequestHandler createRequestHandler(String fileName, IResourceStream resourceStream)
	{
		return new ResourceStreamRequestHandler(resourceStream)
				.setFileName(fileName)
				.setContentDisposition(ContentDisposition.ATTACHMENT);
	}

}
