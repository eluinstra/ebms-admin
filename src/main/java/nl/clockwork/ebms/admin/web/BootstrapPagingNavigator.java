package nl.clockwork.ebms.admin.web;

import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.navigation.paging.IPagingLabelProvider;

public class BootstrapPagingNavigator extends AjaxPagingNavigator
{
	private static final long serialVersionUID = 1L;

	public BootstrapPagingNavigator(final String id, final IPageable pageable)
	{
		this(id,pageable,null);
	}

	public BootstrapPagingNavigator(final String id, final IPageable pageable, final IPagingLabelProvider labelProvider)
	{
		super(id,pageable,labelProvider);
	}

}
