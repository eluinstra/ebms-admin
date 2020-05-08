/**
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

import org.apache.wicket.Session;
import org.apache.wicket.markup.html.form.AbstractTextComponent.ITextFormatProvider;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.IConverter;

import lombok.AccessLevel;
import lombok.val;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LocalDateTimeTextField extends TextField<LocalDateTime> implements ITextFormatProvider
{
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_PATTERN = "MM/dd/yyyy";
	String datePattern;
	IConverter<LocalDateTime> converter;

	public LocalDateTimeTextField(final String id)
	{
		this(id, null,defaultDatePattern());
	}

	public LocalDateTimeTextField(final String id, final IModel<LocalDateTime> model)
	{
		this(id,model,defaultDatePattern());
	}

	public LocalDateTimeTextField(final String id, final String datePattern)
	{
		this(id,null,datePattern);
	}

	public LocalDateTimeTextField(final String id, final IModel<LocalDateTime> model, final String datePattern)
	{
		super(id,model,LocalDateTime.class);
		this.datePattern = datePattern;
		converter = new LocalDateTimeConverter(datePattern);
	}

	@Override
	protected IConverter<?> createConverter(Class<?> type)
	{
		if (LocalDateTime.class.isAssignableFrom(type))
			return converter;
		return null;
	}

	@Override
	public String getTextFormat()
	{
		return datePattern;
	}

	private static String defaultDatePattern()
	{
		val locale = Session.get().getLocale();
		if (locale != null)
		{
			val format = DateFormat.getDateInstance(DateFormat.SHORT, locale);
			if (format instanceof SimpleDateFormat)
				return ((SimpleDateFormat)format).toPattern();
		}
		return DEFAULT_PATTERN;
	}

	@Override
	protected String[] getInputTypes()
	{
		return new String[] { "text", "date", "datetime", "datetime-local", "month", "time", "week" };
	}
}
