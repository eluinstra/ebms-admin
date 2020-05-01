package nl.clockwork.ebms.admin.web;

import org.apache.wicket.ajax.AjaxRequestTarget;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.experimental.FieldDefaults;

@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class OnChangeAjaxBehavior extends org.apache.wicket.ajax.form.OnChangeAjaxBehavior
{
	private static final long serialVersionUID = 1L;
	Consumer<AjaxRequestTarget> onUpdate;

	@Override
	protected void onUpdate(AjaxRequestTarget target)
	{
		onUpdate.accept(target);
	}
}
