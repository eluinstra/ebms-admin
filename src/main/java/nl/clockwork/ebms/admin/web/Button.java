package nl.clockwork.ebms.admin.web;

import org.apache.wicket.model.IModel;

import lombok.Builder;

public class Button extends org.apache.wicket.markup.html.form.Button
{
	private static final long serialVersionUID = 1L;
	Action onSubmit;

	public Button(String id)
	{
		this(id,null,null);
	}

	@Builder
	public Button(String id, IModel<String> model, Action onSubmit)
	{
		super(id,model);
		this.onSubmit = onSubmit == null ? () -> super.onSubmit() : onSubmit;
	}

	@Override
	public void onSubmit()
	{
		onSubmit.doIt();
	}
}
