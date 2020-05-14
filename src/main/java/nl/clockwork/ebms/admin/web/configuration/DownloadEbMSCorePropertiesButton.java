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
package nl.clockwork.ebms.admin.web.configuration;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.resource.IResourceStream;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;
import lombok.extern.apachecommons.CommonsLog;
import nl.clockwork.ebms.admin.web.configuration.EbMSCorePropertiesPage.EbMSCorePropertiesFormData;

@CommonsLog
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DownloadEbMSCorePropertiesButton extends Button
{
	private static final long serialVersionUID = 1L;
	@NonNull
	IModel<EbMSCorePropertiesFormData> ebMSCorePropertiesFormData;

	public DownloadEbMSCorePropertiesButton(String id, ResourceModel resourceModel, @NonNull IModel<EbMSCorePropertiesFormData> ebMSCorePropertiesFormData)
	{
		super(id,resourceModel);
		this.ebMSCorePropertiesFormData = ebMSCorePropertiesFormData;
	}

	@Override
	public void onSubmit()
	{
		try
		{
			val writer = new StringWriter();
			new EbMSCorePropertiesWriter(writer,false).write(ebMSCorePropertiesFormData.getObject());
			val resourceStream = StringWriterResourceStream.of(writer,"plain/text");
			getRequestCycle().scheduleRequestHandlerAfterCurrent(createRequestHandler(resourceStream));
		}
		catch (IOException e)
		{
			log.error("",e);
			error(e.getMessage());
		}
	}

	private ResourceStreamRequestHandler createRequestHandler(IResourceStream resourceStream)
	{
		return new ResourceStreamRequestHandler(resourceStream)
				.setFileName(PropertiesType.EBMS_CORE.getPropertiesFile())
				.setContentDisposition(ContentDisposition.ATTACHMENT);
	}
}
