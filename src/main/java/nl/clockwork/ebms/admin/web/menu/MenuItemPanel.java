package nl.clockwork.ebms.admin.web.menu;

import nl.clockwork.ebms.admin.web.menu.MenuPanel.Rows;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class MenuItemPanel extends Panel
{
	private static final long serialVersionUID = 1L;

	public MenuItemPanel(String id, MenuItem menuItem)
	{
		this(id,Model.of(menuItem));
	}
	
	public MenuItemPanel(String id, IModel<MenuItem> model)
	{
		super(id,model);
		add(new Rows("rows",model.getObject().getChildren()).setRenderBodyOnly(true));
		add(new Label("name",model.getObject().getName()));
	}

}