package nl.clockwork.ebms.admin.web;

import org.apache.wicket.util.convert.IConverter;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TextField<T> extends org.apache.wicket.markup.html.form.TextField<T>
{
	private static final long serialVersionUID = 1L;
	Supplier<Boolean> isVisible;
	Function<Class<?>,IConverter<?>> getConverter;

	public TextField(String id)
	{
		this(id,null,null);
	}

	@Builder
	public TextField(String id, Supplier<Boolean> isVisible, Function<Class<?>,IConverter<?>> getConverter)
	{
		super(id);
		this.isVisible = isVisible == null ? () -> super.isVisible() : isVisible;
		this.getConverter = getConverter == null ? t -> super.getConverter(t) : getConverter;
	}

	@Override
	public boolean isVisible()
	{
		return isVisible.get();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <C> IConverter<C> getConverter(Class<C> type)
	{
		return (IConverter<C>)this.getConverter.apply(type);
	}
}
