package nl.clockwork.ebms.admin.web;

import org.apache.wicket.ajax.AjaxRequestTarget;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AjaxFormComponentUpdatingBehavior extends org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior
{
	private static final long serialVersionUID = 1L;
	@NonNull
	Consumer<AjaxRequestTarget> onUpdate;

	@Builder
	public AjaxFormComponentUpdatingBehavior(String event, @NonNull Consumer<AjaxRequestTarget> onUpdate)
	{
		super(event);
		this.onUpdate = onUpdate;
	}

	@Override
	protected void onUpdate(AjaxRequestTarget target)
	{
		onUpdate.accept(target);
	}
}
