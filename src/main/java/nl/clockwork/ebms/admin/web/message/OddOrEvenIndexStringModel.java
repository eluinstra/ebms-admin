package nl.clockwork.ebms.admin.web.message;

import org.apache.wicket.model.AbstractReadOnlyModel;

public class OddOrEvenIndexStringModel extends AbstractReadOnlyModel<String>
{
	private static final long serialVersionUID = 1L;
	private int index;

	public OddOrEvenIndexStringModel(int index)
	{
		this.index = index;
	}

	@Override
	public String getObject()
	{
		return (index % 2 == 0) ? "even" : "odd";
	}
}
