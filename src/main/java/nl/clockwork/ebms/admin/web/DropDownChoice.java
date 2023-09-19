/*
 * Copyright 2013 Clockwork
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.clockwork.ebms.admin.web;

import java.util.List;
import lombok.Builder;
import org.apache.wicket.model.IModel;

public class DropDownChoice<T> extends org.apache.wicket.markup.html.form.DropDownChoice<T>
{
	private static final long serialVersionUID = 1L;
	Supplier<Boolean> isVisible;
	Supplier<Boolean> isEnabled;
	Supplier<Boolean> isRequired;
	Supplier<Boolean> localizeDisplayValues;

	public DropDownChoice(String id, IModel<? extends List<? extends T>> choices)
	{
		this(id, null, choices, null, null, null, null);
	}

	@Builder
	public DropDownChoice(
			String id,
			IModel<T> model,
			IModel<? extends List<? extends T>> choices,
			Supplier<Boolean> isVisible,
			Supplier<Boolean> isEnabled,
			Supplier<Boolean> isRequired,
			Supplier<Boolean> localizeDisplayValues)
	{
		super(id, model, choices);
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
