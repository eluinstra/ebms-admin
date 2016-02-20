package nl.clockwork.ebms.admin.web;

public class WebMarkupContainer extends org.apache.wicket.markup.html.WebMarkupContainer
{
	private static final long serialVersionUID = 1L;

	public WebMarkupContainer(String id)
	{
		super(id);
		setOutputMarkupId(true);
	}

}
