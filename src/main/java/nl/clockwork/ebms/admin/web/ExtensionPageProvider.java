package nl.clockwork.ebms.admin.web;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import nl.clockwork.ebms.admin.web.menu.MenuItem;

public abstract class ExtensionPageProvider
{
	public static List<ExtensionPageProvider> get()
	{
		ServiceLoader<ExtensionPageProvider> providers = ServiceLoader.load(ExtensionPageProvider.class);
		List<ExtensionPageProvider> result = new ArrayList<ExtensionPageProvider>();
		for (ExtensionPageProvider provider : providers)
			result.add(provider);
		return result;
	}

	public abstract List<MenuItem> getMenuItems();

}
