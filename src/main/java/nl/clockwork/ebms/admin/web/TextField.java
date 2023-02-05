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
import org.apache.wicket.util.convert.IConverter;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TextField<T> extends org.apache.wicket.markup.html.form.TextField<T>
{
	private static final long serialVersionUID = 1L;
	Supplier<Boolean> isVisible;
	Function<Class<?>, IConverter<?>> getConverter;

	public TextField(String id)
	{
		this(id, null, null);
	}

	@Builder
	public TextField(String id, Supplier<Boolean> isVisible, Function<Class<?>, IConverter<?>> getConverter)
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
