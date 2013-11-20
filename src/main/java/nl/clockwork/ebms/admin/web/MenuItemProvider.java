package nl.clockwork.ebms.admin.web;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import nl.clockwork.ebms.admin.web.menu.MenuItem;

public abstract class MenuItemProvider
{
	public static List<MenuItemProvider> get()
	{
		ServiceLoader<MenuItemProvider> providers = ServiceLoader.load(MenuItemProvider.class);
		List<MenuItemProvider> result = new ArrayList<MenuItemProvider>();
		for (MenuItemProvider provider : providers)
			result.add(provider);
		return result;
	}

	public abstract List<MenuItem> getMenuItems();
}
