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
package nl.clockwork.ebms.admin.web;


import java.util.Set;

import nl.clockwork.ebms.admin.web.menu.MenuItem;
import nl.clockwork.ebms.admin.web.menu.MenuItemExpansion;
import nl.clockwork.ebms.admin.web.menu.MenuLinkItem;
import nl.clockwork.ebms.admin.web.menu.MenuProvider;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.tree.DefaultNestedTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.content.Folder;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public abstract class BasePage extends WebPage
{
	private static final long serialVersionUID = 1L;
	private DefaultNestedTree<MenuItem> menu;

	public BasePage()
	{
		this(new PageParameters());
	}
	
	protected BasePage(final PageParameters parameters)
	{
		super(parameters);

    menu = 
    	new DefaultNestedTree<MenuItem>("menu",new MenuProvider(),new MenuItemExpansionModel())
    	{
				private static final long serialVersionUID = 1L;
    	
				protected Component newContentComponent(String id, org.apache.wicket.model.IModel<MenuItem> node)
				{
					return
						new Folder<MenuItem>(id,menu,node)
						{
							private static final long serialVersionUID = 1L;
							
							protected boolean isClickable()
							{
								return false;
							};
							
							protected org.apache.wicket.model.IModel<?> newLabelModel(org.apache.wicket.model.IModel<MenuItem> model)
							{
								return new PropertyModel<String>(model,"name");
							};

							protected org.apache.wicket.MarkupContainer newLinkComponent(String id, org.apache.wicket.model.IModel<MenuItem> model)
							{
								if (model.getObject() instanceof MenuLinkItem)
									return new BookmarkablePageLink<Void>(id,((MenuLinkItem)model.getObject()).getPageClass());
								else
									return super.newLinkComponent(id,model);
							};
							
						}
					;
				}
    	}
    ;

    add(new BookmarkablePageLink<Object>("home",WicketApplication.get().getHomePage()));
		add(new Label("pageTitle",new PropertyModel<String>(this,"pageTitle")));
		add(menu);
	}
	
	public abstract String getPageTitle();

	private class MenuItemExpansionModel extends AbstractReadOnlyModel<Set<MenuItem>>
	{
		private static final long serialVersionUID = 1L;

		@Override
		public Set<MenuItem> getObject()
		{
			return MenuItemExpansion.get();
		}
	}
	
	@Override
	public void renderHead(IHeaderResponse response)
	{
		//response.render(CssHeaderItem.forReference(new PackageResourceReference(BasePage.class,"bootstrap.min.css")));
		//response.render(CssHeaderItem.forReference(new PackageResourceReference(BasePage.class,"ebms-admin.css")));
	}
}
