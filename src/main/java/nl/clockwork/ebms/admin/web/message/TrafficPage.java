package nl.clockwork.ebms.admin.web.message;

import nl.clockwork.ebms.admin.web.BasePage;

public class TrafficPage extends BasePage
{
	private static final long serialVersionUID = 1L;

	public TrafficPage()
	{
  }
	
	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("traffic",this);
	}

}
