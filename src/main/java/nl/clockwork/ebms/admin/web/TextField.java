package nl.clockwork.ebms.admin.web;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class TextField<T> extends org.apache.wicket.markup.html.form.TextField<T>
{
	private LocalizedStringResource resource;

	public TextField(String id, LocalizedStringResource resource)
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
