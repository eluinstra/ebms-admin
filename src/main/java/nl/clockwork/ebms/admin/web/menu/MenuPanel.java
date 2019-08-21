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
package nl.clockwork.ebms.admin.web.menu;

import java.util.List;

import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

public class MenuPanel extends Panel
{
	public static class MenuItems extends ListView<MenuItem>
	{
		private static final long serialVersionUID = 1L;
		private int level;

		public MenuItems(String id, List<MenuItem> list, int level)
		{
			super(id,list);
			this.level = level;
			setRenderBodyOnly(true);
		}

		@Override
		protected void populateItem(ListItem<MenuItem> item)
		{
			MenuItem menuItem = item.getModelObject();
			if (menuItem instanceof MenuLinkItem)
				item.add(new MenuLinkItemPanel("menuItem",(MenuLinkItem)menuItem)/*.setRenderBodyOnly(true)*/);
			else if (menuItem instanceof MenuDivider)
				item.add(new MenuDividerPanel("menuItem"));
			else
				item.add(new MenuItemPanel("menuItem",menuItem,level)/*.setRenderBodyOnly(true)*/);
			//item.setRenderBodyOnly(true);
		}
	}

	private static final long serialVersionUID = 1L;

	public MenuPanel(String id, List<MenuItem> menuItems)
	{
		super(id,Model.of(menuItems));
		add(new MenuItems("menuItems",menuItems,0));
	}
	
}