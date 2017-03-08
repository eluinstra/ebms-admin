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

import nl.clockwork.ebms.admin.web.configuration.Constants.PropertiesType;
import nl.clockwork.ebms.admin.web.configuration.EbMSAdminPropertiesPage.EbMSAdminPropertiesFormModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.resource.IResourceStream;

public class DownloadEbMSAdminPropertiesButton extends Button
{
	private static final long serialVersionUID = 1L;
	protected transient Log logger = LogFactory.getLog(this.getClass());
	private EbMSAdminPropertiesFormModel ebMSAdminPropertiesFormModel;
	private PropertiesType propertiesType;

	public DownloadEbMSAdminPropertiesButton(String id, ResourceModel resourceModel, EbMSAdminPropertiesFormModel ebMSAdminPropertiesFormModel, PropertiesType propertiesType)
	{
		super(id,resourceModel);
		this.ebMSAdminPropertiesFormModel = ebMSAdminPropertiesFormModel;
		this.propertiesType = propertiesType;
	}

	@Override
	public void onSubmit()
	{
		try
		{
			final StringWriter writer = new StringWriter();
			new EbMSAdminPropertiesWriter(writer,true).write(ebMSAdminPropertiesFormModel,propertiesType);
			IResourceStream resourceStream = new StringWriterResourceStream(writer,"plain/text");
			getRequestCycle().scheduleRequestHandlerAfterCurrent(createRequestHandler(resourceStream));
		}
		catch (IOException e)
		{
			logger.error("",e);
			error(e.getMessage());
		}
	}

	private ResourceStreamRequestHandler createRequestHandler(IResourceStream resourceStream)
	{
		return new ResourceStreamRequestHandler(resourceStream)
		.setFileName(propertiesType.getPropertiesFile())
		.setContentDisposition(ContentDisposition.ATTACHMENT);
	}

}
