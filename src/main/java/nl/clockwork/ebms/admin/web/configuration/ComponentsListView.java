package nl.clockwork.ebms.admin.web.configuration;

import java.util.List;

import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import nl.clockwork.ebms.admin.web.BootstrapPanelBorder;

public class ComponentsListView extends ListView<BootstrapPanelBorder>
{
	private static final long serialVersionUID = 1L;

	public ComponentsListView(String id, List<BootstrapPanelBorder> list)
	{
		super(id,list);
		setReuseItems(true);
	}

	@Override
	protected void populateItem(ListItem<BootstrapPanelBorder> item)
	{
		item.add((BootstrapPanelBorder)item.getModelObject()); 
	}
}