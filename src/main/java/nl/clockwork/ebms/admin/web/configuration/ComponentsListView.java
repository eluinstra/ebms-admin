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


import java.util.List;
import nl.clockwork.ebms.admin.web.BootstrapPanelBorder;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

public class ComponentsListView extends ListView<BootstrapPanelBorder>
{
	private static final long serialVersionUID = 1L;

	public ComponentsListView(String id, List<BootstrapPanelBorder> list)
	{
		super(id, list);
		setReuseItems(true);
	}

	@Override
	protected void populateItem(ListItem<BootstrapPanelBorder> item)
	{
		item.add((BootstrapPanelBorder)item.getModelObject());
	}
}