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
package nl.clockwork.ebms.admin.web.service.cpa;

import nl.clockwork.ebms.service.CPAService;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.resource.IResourceStream;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.val;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DownloadCPALink extends Link<String>
{
	private static final long serialVersionUID = 1L;
	CPAService cpaService;

	@Builder
	public DownloadCPALink(String id, CPAService cpaService, String cpaId)
	{
		super(id,Model.of(cpaId));
		this.cpaService = cpaService;
	}

	@Override
	public void onClick()
	{
		val cpaId = getModelObject();
		val cpa = cpaService.getCPA(cpaId);
		val resourceStream = new StringResourceStream(cpa,"text/xml");
		getRequestCycle().scheduleRequestHandlerAfterCurrent(createRequestHandler(cpaId,resourceStream));
	}

	private ResourceStreamRequestHandler createRequestHandler(String cpaId, IResourceStream resourceStream)
	{
		return new ResourceStreamRequestHandler(resourceStream)
				.setFileName("cpa." + cpaId + ".xml")
				.setContentDisposition(ContentDisposition.ATTACHMENT);
	}
}
