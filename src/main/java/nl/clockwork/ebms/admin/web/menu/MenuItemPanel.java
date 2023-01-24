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


import lombok.val;
import nl.clockwork.ebms.admin.web.Utils;
import nl.clockwork.ebms.admin.web.WebMarkupContainer;
import nl.clockwork.ebms.admin.web.menu.MenuPanel.MenuItems;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class MenuItemPanel extends Panel
{
	private static final long serialVersionUID = 1L;

	public MenuItemPanel(String id, IModel<? extends MenuItem> model, int level)
	{
		super(id,model);
		val menuItem = new WebMarkupContainer("menuListItem");
		menuItem.add(new AttributeModifier("class",new Model<String>(level < 1 ? "dropdown" : "dropdown-submenu")));
		add(menuItem);
		menuItem.add(new Label("name",Utils.getResourceString(getClass(),model.getObject().getName())));
		menuItem.add(new WebMarkupContainer("menuItemCaret").setVisible(level < 1));
		menuItem.add(new MenuItems("menuItems",model.getObject().getChildren(),level + 1));
	}
}