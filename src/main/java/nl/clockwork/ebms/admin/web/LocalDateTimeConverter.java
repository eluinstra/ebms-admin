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


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.string.Strings;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class LocalDateTimeConverter implements IConverter<LocalDateTime>
{
	private static final long serialVersionUID = 1L;
	@NonNull
	String datePattern;

	@Override
	public LocalDateTime convertToObject(String value, Locale locale)
	{
		if (Strings.isEmpty(value))
			return null;
		try
		{
			val date = LocalDate.from(DateTimeFormatter.ofPattern(datePattern).parse(value));
			val time = LocalTime.MIDNIGHT;
			return LocalDateTime.of(date, time);
		}
		catch (RuntimeException e)
		{
			throw newConversionException(e, locale);
		}
	}

	private ConversionException newConversionException(RuntimeException cause, Locale locale)
	{
		return new ConversionException(cause).setVariable("format", datePattern);
	}

	@Override
	public String convertToString(LocalDateTime value, Locale locale)
	{
		return DateTimeFormatter.ofPattern(datePattern).format(value);
	}
}
