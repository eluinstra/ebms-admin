package nl.clockwork.ebms.admin.web.menu;

import java.util.List;

import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

public class MenuPanel extends Panel
{
	private static final long serialVersionUID = 1L;

	public MenuPanel(String id, List<MenuItem> menuItems)
	{
		super(id,Model.of(menuItems));
		add(new Rows("rows",menuItems).setRenderBodyOnly(true));
	}
	
	public static class Rows extends ListView<MenuItem>
	{
		private static final long serialVersionUID = 1L;

		public Rows(String id, List<? extends MenuItem> list)
		{
			super(id,list);
			setRenderBodyOnly(true);
		}

		@Override
		protected void populateItem(ListItem<MenuItem> item)
		{
			MenuItem menuItem = item.getModelObject();
			if (menuItem instanceof MenuLinkItem)
				item.add(new MenuLinkItemPanel("row",(MenuLinkItem)menuItem).setRenderBodyOnly(true));
			else
				item.add(new MenuItemPanel("row",menuItem).setRenderBodyOnly(true));
		}
		
	}

}