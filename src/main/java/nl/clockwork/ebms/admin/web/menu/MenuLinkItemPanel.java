package nl.clockwork.ebms.admin.web.menu;

import nl.clockwork.ebms.admin.web.BasePage;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class MenuLinkItemPanel extends Panel
{
	private static final long serialVersionUID = 1L;

	public MenuLinkItemPanel(String id, MenuLinkItem menuItem)
	{
		this(id,Model.of(menuItem));
	}
	
	public MenuLinkItemPanel(String id, IModel<MenuLinkItem> model)
	{
		super(id,model);
		BookmarkablePageLink<BasePage> link = new BookmarkablePageLink<BasePage>("link",model.getObject().getPageClass());
		link.add(new Label("name",model.getObject().getName()));
		add(link);
	}

}