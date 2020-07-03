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
package nl.clockwork.ebms.admin.web;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import lombok.val;
import nl.clockwork.ebms.admin.web.menu.MenuItem;

public abstract class ExtensionProvider
{
	public static List<ExtensionProvider> get()
	{
		val providers = ServiceLoader.load(ExtensionProvider.class);
		val result = new ArrayList<ExtensionProvider>();
		for (val provider : providers)
			result.add(provider);
		return result;
	}

	public abstract Class<?> getSpringConfigurationClass();
	public abstract String getDbMigrationLocation();
	public abstract String getName();
	public abstract MenuItem createSubMenu(MenuItem parent, int index, String name);
}
