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
package nl.clockwork.ebms.admin.web.configuration;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import nl.clockwork.ebms.admin.web.configuration.EbMSAdminPropertiesPage.EbMSAdminPropertiesFormData;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SaveEbMSAdminPropertiesButton extends Button
{
	private static final long serialVersionUID = 1L;
	@NonNull
	IModel<EbMSAdminPropertiesFormData> ebMSAdminPropertiesFormData;
	@NonNull
	PropertiesType propertiesType;

	public SaveEbMSAdminPropertiesButton(
			String id,
			ResourceModel resourceModel,
			@NonNull IModel<EbMSAdminPropertiesFormData> ebMSAdminPropertiesFormData,
			@NonNull PropertiesType propertiesType)
	{
		super(id, resourceModel);
		this.ebMSAdminPropertiesFormData = ebMSAdminPropertiesFormData;
		this.propertiesType = propertiesType;
	}

	@Override
	public void onSubmit()
	{
		try
		{
			val file = new File(propertiesType.getPropertiesFile());
			val writer = new FileWriter(file);
			new EbMSAdminPropertiesWriter(writer, true).write(ebMSAdminPropertiesFormData.getObject(), propertiesType);
			info(new StringResourceModel("properties.saved", getPage(), Model.of(file)).getString());
			error(new StringResourceModel("restart", getPage(), null).getString());
		}
		catch (IOException e)
		{
			log.error("", e);
			error(e.getMessage());
		}
	}

}
