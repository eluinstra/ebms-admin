package nl.clockwork.ebms.admin.web;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class IntegerTextField extends TextField<Integer>
{
	private LocalizedStringResource resource;

	public IntegerTextField(String id, LocalizedStringResource resource)
	{
		super(id);
		this.resource = resource;
	}

	private static final long serialVersionUID = 1L;

	@Override
	public IModel<String> getLabel()
	{
		return Model.of(getLocalizer().getString(resource.getKey(),resource.getComponent()));
	}
}
