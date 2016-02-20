package nl.clockwork.ebms.admin.web;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;

public class CloseButtonCallback implements ModalWindow.CloseButtonCallback
{
	private static final long serialVersionUID = 1L;

	@Override
	public boolean onCloseButtonClicked(AjaxRequestTarget target)
	{
		return true;
	}
}
