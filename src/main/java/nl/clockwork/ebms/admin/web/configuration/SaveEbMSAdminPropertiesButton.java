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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import nl.clockwork.ebms.admin.web.configuration.EbMSAdminPropertiesPage.EbMSAdminPropertiesFormModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.model.ResourceModel;

public class SaveEbMSAdminPropertiesButton extends Button
{
	private static final long serialVersionUID = 1L;
	protected transient Log logger = LogFactory.getLog(this.getClass());
	private EbMSAdminPropertiesFormModel ebMSAdminPropertiesFormModel;

	public SaveEbMSAdminPropertiesButton(String id, ResourceModel resourceModel, EbMSAdminPropertiesFormModel ebMSAdminPropertiesFormModel)
	{
		super(id,resourceModel);
		this.ebMSAdminPropertiesFormModel = ebMSAdminPropertiesFormModel;
	}

	@Override
	public void onSubmit()
	{
		try
		{
			File file = new File("ebms-admin.properties");
			FileWriter writer = new FileWriter(file);
			Utils.storeProperties(ebMSAdminPropertiesFormModel,writer);
			info("Properties saved to " + file.getAbsolutePath());
			warn("Restart " + getLocalizer().getString("applicationName",this) + "...");
		}
		catch (IOException e)
		{
			logger.error("",e);
			error(e.getMessage());
		}
	}

}
