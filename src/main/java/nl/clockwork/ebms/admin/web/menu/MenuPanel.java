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
package nl.clockwork.ebms.admin.web.menu;

import java.util.List;

import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import static io.vavr.API.*;
import static io.vavr.Predicates.*;
import lombok.AccessLevel;
import lombok.val;
import lombok.experimental.FieldDefaults;

public class MenuPanel extends Panel
{
	@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
	public static class MenuItems extends ListView<MenuItem>
	{
		private static final long serialVersionUID = 1L;
		int level;

		public MenuItems(String id, List<MenuItem> list, int level)
		{
			super(id,list);
			this.level = level;
			setRenderBodyOnly(true);
		}

		@Override
		protected void populateItem(ListItem<MenuItem> item)
		{
			val o = item.getModelObject();
			Match(o).of(
					Case($(instanceOf(MenuLinkItem.class)),i -> item.add(new MenuLinkItemPanel("menuItem",Model.of((MenuLinkItem)o))/*.setRenderBodyOnly(true)*/)),
					Case($(instanceOf(MenuDivider.class)),i -> item.add(new MenuDividerPanel("menuItem"))),
					Case($(),i -> item.add(new MenuItemPanel("menuItem",item.getModel(),level)/*.setRenderBodyOnly(true)*/)));
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