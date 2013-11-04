package nl.clockwork.ebms.admin.web;

import org.apache.wicket.markup.html.form.FormComponent;

public class FormComponentFeedbackBorder extends org.apache.wicket.markup.html.form.validation.FormComponentFeedbackBorder
{
	private static final long serialVersionUID = 1L;

	public FormComponentFeedbackBorder(String id, FormComponent<?> formComponent)
	{
		super(id);
		add(formComponent);
	}

}
