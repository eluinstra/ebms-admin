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

import java.time.format.DateTimeFormatter;
import java.util.Locale;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class PatternInstantConverter extends InstantConverter
{
	private static final long serialVersionUID = 1L;
	String datePattern;

	@Override
	public String getDatePattern(Locale locale)
	{
		return datePattern;
	}

	@Override
	protected DateTimeFormatter getFormat(Locale locale)
	{
		return DateTimeFormatter.ofPattern(datePattern).withLocale(locale);
	}
}
