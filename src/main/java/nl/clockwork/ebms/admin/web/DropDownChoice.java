package nl.clockwork.ebms.admin.web;

import java.util.List;

import org.apache.wicket.model.IModel;

import lombok.Builder;

public class DropDownChoice<T> extends org.apache.wicket.markup.html.form.DropDownChoice<T>
{
	private static final long serialVersionUID = 1L;
	Supplier<Boolean> isVisible;
	Supplier<Boolean> isEnabled;
	Supplier<Boolean> isRequired;
	Supplier<Boolean> localizeDisplayValues;

	public DropDownChoice(String id, IModel<? extends List<? extends T>> choices)
	{
		this(id,null,choices,null,null,null,null);
	}

	@Builder
	public DropDownChoice(String id, IModel<T> model, IModel<? extends List<? extends T>> choices, Supplier<Boolean> isVisible, Supplier<Boolean> isEnabled, Supplier<Boolean> isRequired, Supplier<Boolean> localizeDisplayValues)
	{
		super(id,model,choices);
		this.isVisible = isVisible == null ? () -> super.isVisible() : isVisible;
		this.isEnabled = isEnabled == null ? () -> super.isEnabled() : isEnabled;
		this.isRequired = isRequired == null ? () -> super.isRequired() : isRequired;
		this.localizeDisplayValues = localizeDisplayValues == null ? () -> super.localizeDisplayValues() : localizeDisplayValues;
	}

	@Override
	public boolean isVisible()
	{
		return isVisible.get();
	}

	@Override
	public boolean isEnabled()
	{
		return isEnabled.get();
	}

	@Override
	public boolean isRequired()
	{
		return isRequired.get();
	}

	@Override
	protected boolean localizeDisplayValues()
	{
		return localizeDisplayValues.get();
	}
}
