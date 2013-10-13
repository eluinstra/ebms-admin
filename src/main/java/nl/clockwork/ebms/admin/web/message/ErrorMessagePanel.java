package nl.clockwork.ebms.admin.web.message;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class ErrorMessagePanel extends Panel
{
	private static final long serialVersionUID = 1L;

	public ErrorMessagePanel(final ModalWindow window, IModel<String> model)
	{
		super(window.getContentId(),model);
		add(new MultiLineLabel("errorMessage",model));
		add(new AjaxLink<Void>("close")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target)
			{
				window.close(target);
			}
		});
	}

}
