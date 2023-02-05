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


import lombok.AccessLevel;
import lombok.Builder;
import lombok.experimental.FieldDefaults;
import org.apache.wicket.model.IModel;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TextArea<T> extends org.apache.wicket.markup.html.form.TextArea<T>
{
	private static final long serialVersionUID = 1L;
	Supplier<Boolean> isVisible;

	public TextArea(String id, IModel<T> model)
	{
		this(id, model, null);
	}

	@Builder
	public TextArea(String id, IModel<T> model, Supplier<Boolean> isVisible)
	{
		super(id, model);
		this.isVisible = isVisible == null ? () -> super.isVisible() : isVisible;
	}

	@Override
	public boolean isVisible()
	{
		return isVisible.get();
	}
}
