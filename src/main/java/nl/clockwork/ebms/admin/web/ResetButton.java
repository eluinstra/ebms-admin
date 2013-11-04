package nl.clockwork.ebms.admin.web;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.model.IModel;

public class ResetButton extends Button
{
	private static final long serialVersionUID = 1L;
	private Class<? extends Page> page;

	public ResetButton(String id, IModel<String> model, Class<? extends Page> page)
	{
		super(id,model);
		this.page = page;
		setDefaultFormProcessing(false);
	}

	@Override
	public void onSubmit()
	{
		setResponsePage(page);
	}
}
