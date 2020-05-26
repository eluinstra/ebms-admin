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
import java.io.FileReader;
import java.io.IOException;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import nl.clockwork.ebms.admin.web.configuration.EbMSAdminPropertiesPage.EbMSAdminPropertiesFormData;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LoadEbMSAdminPropertiesButton extends Button
{
	private static final long serialVersionUID = 1L;
	@NonFinal
	@NonNull
	EbMSAdminPropertiesFormData ebMSAdminPropertiesFormData;
	@NonNull
	PropertiesType propertiesType;

	@Builder
	public LoadEbMSAdminPropertiesButton(String id, ResourceModel resourceModel, @NonNull EbMSAdminPropertiesFormData ebMSAdminPropertiesFormData, @NonNull PropertiesType propertiesType)
	{
		super(id,resourceModel);
		this.ebMSAdminPropertiesFormData = ebMSAdminPropertiesFormData;
		this.propertiesType = propertiesType;
		setDefaultFormProcessing(false);
	}
	
	@Override
	public boolean isEnabled()
	{
		return new File(propertiesType.getPropertiesFile()).exists();
	}

	@Override
	public void onSubmit()
	{
		try
		{
			val file = new File(propertiesType.getPropertiesFile());
			val reader = new FileReader(file);
			ebMSAdminPropertiesFormData = new EbMSAdminPropertiesReader(reader).read(propertiesType);
			val page = new EbMSAdminPropertiesPage(Model.of(ebMSAdminPropertiesFormData));
			page.info(new StringResourceModel("properties.loaded",page,Model.of(file)).getString());
			setResponsePage(page);
		}
		catch (IOException e)
		{
			log.error("",e);
			error(e.getMessage());
		}
	}
}
