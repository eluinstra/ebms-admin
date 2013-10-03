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

import java.util.Iterator;

import nl.clockwork.ebms.admin.web.WicketApplication;

import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

public class MenuProvider implements ITreeProvider<MenuItem>
{
	private static final long serialVersionUID = 1L;

	@Override
	public void detach()
	{
	}

	@Override
	public Iterator<? extends MenuItem> getChildren(MenuItem menuItem)
	{
		return menuItem.getChildren().iterator();
	}

	@Override
	public Iterator<? extends MenuItem> getRoots()
	{
		return WicketApplication.get().menu.iterator();
	}

	@Override
	public boolean hasChildren(MenuItem menuItem)
	{
		return /*menuItem.getParent() == null || */!menuItem.getChildren().isEmpty();
	}

	@Override
	public IModel<MenuItem> model(MenuItem menuItem)
	{
		return new MenuItemModel(menuItem);
	}

	private static class MenuItemModel extends LoadableDetachableModel<MenuItem>
	{
		private static final long serialVersionUID = 1L;
		private final String id;
		
		public MenuItemModel(MenuItem menuItem)
		{
			super(menuItem);
			this.id = menuItem.getId();
		}
		
		@Override
		protected MenuItem load()
		{
			return WicketApplication.get().getMenuItem(id);
		}

		@Override
		public boolean equals(Object obj)
		{
			if (obj instanceof MenuItemModel)
				return ((MenuItemModel)obj).id.equals(id);
			else
				return false;
		}
		
		@Override
		public int hashCode()
		{
			return id.hashCode();
		}
	}
}
