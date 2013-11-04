package nl.clockwork.ebms.admin.web;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.model.IModel;

public class CancelButton extends Button //GoToButton
{
	private static final long serialVersionUID = 1L;
	private Class<? extends Page> responsePage;

	public CancelButton(String id, IModel<String> model, Class<? extends Page> responsePage)
	{
		super(id,model);
		this.responsePage = responsePage;
		setDefaultFormProcessing(false);
	}

	@Override
	public void onSubmit()
	{
		setResponsePage(responsePage);
	}
}
