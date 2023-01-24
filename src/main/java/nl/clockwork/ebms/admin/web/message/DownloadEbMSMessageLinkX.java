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
package nl.clockwork.ebms.admin.web.message;


import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import nl.clockwork.ebms.admin.model.EbMSMessage;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.resource.IResourceStream;

@Slf4j
public class DownloadEbMSMessageLinkX extends Link<EbMSMessage>
{
	private static final long serialVersionUID = 1L;

	public DownloadEbMSMessageLinkX(String id, IModel<EbMSMessage> model)
	{
		super(id, Args.notNull(model, "message"));
	}

	@Override
	public void onClick()
	{
		val o = getModelObject();
		try (val out = new CachedOutputStream())
		{
			EbMSMessageZipper.of(o, out).zip();
			val resourceStream = CachedOutputResourceStream.of(out, "application/gzip");
			getRequestCycle().scheduleRequestHandlerAfterCurrent(createRequestHandler(o, resourceStream));
		}
		catch (IOException e)
		{
			log.error("", e);
			error(e.getMessage());
		}
	}

	private ResourceStreamRequestHandler createRequestHandler(EbMSMessage message, IResourceStream resourceStream)
	{
		return new ResourceStreamRequestHandler(resourceStream).setFileName("message." + message.getMessageId() + "." + message.getMessageNr() + ".zip")
				.setContentDisposition(ContentDisposition.ATTACHMENT);
	}

}
