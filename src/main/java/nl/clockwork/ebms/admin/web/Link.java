package nl.clockwork.ebms.admin.web;

import org.apache.wicket.model.IModel;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Link<T> extends org.apache.wicket.markup.html.link.Link<T>
{
	private static final long serialVersionUID = 1L;
	@NonNull
	Action onClick;

	public Link(String id, @NonNull Action onClick)
	{
		super(id);
		this.onClick = onClick;
	}

	@Builder
	public Link(String id, IModel<T> model, @NonNull Action onClick)
	{
		super(id,model);
		this.onClick = onClick;
	}

	@Override
	public void onClick()
	{
		onClick.doIt();
	}
}
