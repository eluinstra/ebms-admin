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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.string.Strings;

public abstract class InstantConverter implements IConverter<Instant>
{
	private static final long serialVersionUID = 1L;

	@Override
	public Instant convertToObject(String value, Locale locale)
	{
		if (Strings.isEmpty(value))
			return null;
		try
		{
			return Instant.from(DateTimeFormatter.ofPattern(getDatePattern(locale),locale).parse(value));
		}
		catch (RuntimeException e)
		{
			throw newConversionException(e,locale);
		}
	}

	private ConversionException newConversionException(RuntimeException cause, Locale locale)
	{
		return new ConversionException(cause).setVariable("format",getDatePattern(locale));
	}

	@Override
	public String convertToString(Instant value, Locale locale)
	{
		return DateTimeFormatter.ofPattern(getDatePattern(locale),locale).format(ZonedDateTime.ofInstant(value,ZoneId.systemDefault()));
	}

	public abstract String getDatePattern(Locale locale);

	protected abstract DateTimeFormatter getFormat(Locale locale);
}
