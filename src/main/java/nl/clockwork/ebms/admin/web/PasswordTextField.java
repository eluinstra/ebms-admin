package nl.clockwork.ebms.admin.web;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class PasswordTextField extends org.apache.wicket.markup.html.form.PasswordTextField
{
	private static final long serialVersionUID = 1L;
	private LocalizedStringResource resource;

	public PasswordTextField(String id, LocalizedStringResource resource)
	{
		super(id);
		this.resource = resource;
	}

	@Override
	public IModel<String> getLabel()
	{
		return Model.of(getLocalizer().getString(resource.getKey(),resource.getComponent()));
	}
}
