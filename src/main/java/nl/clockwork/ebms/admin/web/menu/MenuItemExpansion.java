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

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.wicket.MetaDataKey;
import org.apache.wicket.Session;

public class MenuItemExpansion implements Serializable, Set<MenuItem>
{
	private static final long serialVersionUID = 1L;
	private static MetaDataKey<MenuItemExpansion> KEY = new MetaDataKey<MenuItemExpansion>()
	{
		private static final long serialVersionUID = 1L;
	};
	private Set<String> ids = new HashSet<String>();
	private boolean inverse = true;

	public void expandAll()
	{
		ids.clear();
		inverse = true;
	}

	public void collapseAll()
	{
		ids.clear();
		inverse = false;
	}

	@Override
	public int size()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isEmpty()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean contains(Object menuItem)
	{
		if (inverse)
			return !ids.contains(((MenuItem)menuItem).getId());
		else
			return ids.contains(((MenuItem)menuItem).getId());
	}

	@Override
	public Iterator<MenuItem> iterator()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Object[] toArray()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T[] toArray(T[] a)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean add(MenuItem menuItem)
	{
		if (inverse)
			return ids.remove(menuItem.getId());
		else
			return ids.add(menuItem.getId());
	}

	@Override
	public boolean remove(Object menuItem)
	{
		if (inverse)
			return ids.add(((MenuItem)menuItem).getId());
		else
			return ids.remove(((MenuItem)menuItem).getId());
	}

	@Override
	public boolean containsAll(Collection<?> c)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends MenuItem> c)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear()
	{
		throw new UnsupportedOperationException();
	}

	public static MenuItemExpansion get()
	{
		MenuItemExpansion expansion = Session.get().getMetaData(KEY);
		if (expansion == null)
		{
			expansion = new MenuItemExpansion();
			Session.get().setMetaData(KEY, expansion);
		}
		return expansion;
	}

}
