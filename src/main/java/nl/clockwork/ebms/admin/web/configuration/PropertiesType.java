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


import java.util.Arrays;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Getter
public enum PropertiesType
{
	EBMS_ADMIN("ebms-admin.properties"), EBMS_ADMIN_EMBEDDED("ebms-admin.embedded.properties");

	String propertiesFile;

	public static Optional<PropertiesType> getPropertiesType(String propertiesFile)
	{
		return Arrays.stream(PropertiesType.values()).filter(p -> p.propertiesFile.equals(propertiesFile)).findFirst();
	}
}