package nl.clockwork.ebms.admin.web;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

import lombok.Builder;
import lombok.NonNull;

public class AjaxButton extends org.apache.wicket.ajax.markup.html.form.AjaxButton
{
	private static final long serialVersionUID = 1L;
	@NonNull
	Consumer<AjaxRequestTarget> onSubmit;
	Consumer<AjaxRequestTarget> onError;

	@Builder
	public AjaxButton(String id, IModel<String> model, Form<?> form, @NonNull Consumer<AjaxRequestTarget> onSubmit, Consumer<AjaxRequestTarget> onError)
	{
		super(id,model,form);
		this.onSubmit = onSubmit;
		this.onError = onError == null ? t -> {} : onError;
	}

	@Override
	protected void onSubmit(AjaxRequestTarget target)
	{
		onSubmit.accept(target);
	}

	@Override
	protected void onError(AjaxRequestTarget target)
	{
		super.onError(target);
		onError.accept(target);
	}
}
