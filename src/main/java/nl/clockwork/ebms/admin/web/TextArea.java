package nl.clockwork.ebms.admin.web;

import org.apache.wicket.model.IModel;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TextArea<T> extends org.apache.wicket.markup.html.form.TextArea<T>
{
	private static final long serialVersionUID = 1L;
	Supplier<Boolean> isVisible;

	public TextArea(String id, IModel<T> model)
	{
		this(id,model,null);
	}

	@Builder
	public TextArea(String id, IModel<T> model, Supplier<Boolean> isVisible)
	{
		super(id,model);
		this.isVisible = isVisible == null ? () -> super.isVisible() : isVisible;
	}

	@Override
	public boolean isVisible()
	{
		return isVisible.get();
	}
}
