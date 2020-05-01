package nl.clockwork.ebms.admin.web;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AjaxLink<T> extends org.apache.wicket.ajax.markup.html.AjaxLink<T>
{
	private static final long serialVersionUID = 1L;
	@NonNull
	Consumer<AjaxRequestTarget> onClick;

	public AjaxLink(String id, @NonNull Consumer<AjaxRequestTarget> onClick)
	{
		super(id);
		this.onClick = onClick;
	}

	@Builder
	public AjaxLink(String id, IModel<T> model, @NonNull Consumer<AjaxRequestTarget> onClick)
	{
		super(id,model);
		this.onClick = onClick;
	}

	@Override
	public MarkupContainer setDefaultModel(IModel<?> model)
	{
		return super.setDefaultModel(model);
	}

	@Override
	public void onClick(AjaxRequestTarget target)
	{
		onClick.accept(target);
	}
}
