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


import java.time.Instant;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.apache.wicket.IGenericComponent;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.IConverter;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InstantLabel extends Label implements IGenericComponent<Instant,InstantLabel>
{
	private static final long serialVersionUID = 1L;
	@NonNull
	InstantConverter converter;

	public static InstantLabel of(String id, IModel<Instant> model, String pattern)
	{
		return new InstantLabel(id,model,new PatternInstantConverter(pattern));
	}

	public static InstantLabel of(String id, String pattern)
	{
		return of(id,null,pattern);
	}

	public InstantLabel(String id, InstantConverter converter)
	{
		this(id,null,converter);
	}

	public InstantLabel(String id, IModel<Instant> model, @NonNull InstantConverter converter)
	{
		super(id,model);
		this.converter = converter;
	}

	@Override
	protected IConverter<?> createConverter(Class<?> type)
	{
		if (Instant.class.isAssignableFrom(type))
			return converter;
		return null;
	}

	@Override
	public void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag)
	{
		val s = getDefaultModelObjectAsString();
		replaceComponentTagBody(markupStream,openTag,s);
	}
}