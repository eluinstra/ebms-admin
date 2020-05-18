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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.convert.converter.AbstractConverter;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class XMLGregorianCalendarTextField extends TextField<XMLGregorianCalendar>
{
	private class XMLGregorianCalendarConverter extends AbstractConverter<XMLGregorianCalendar>
	{
		private static final long serialVersionUID = 1L;

		@Override
		public XMLGregorianCalendar convertToObject(String value, Locale locale)
		{
			try
			{
				val formatter = new SimpleDateFormat(datePattern);
				val calendar = new GregorianCalendar();
				val date = formatter.parse(value);
				calendar.setTime(date);
				return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
			}
			catch (ParseException e)
			{
				throw new RuntimeException(e);
			}
			catch (DatatypeConfigurationException e)
			{
				throw new RuntimeException(e);
			}
		}

		@Override
		public String convertToString(XMLGregorianCalendar value, Locale locale)
		{
			val formatter = new SimpleDateFormat(datePattern);
			return formatter.format(value.toGregorianCalendar().getTime());
		}

		@Override
		protected Class<XMLGregorianCalendar> getTargetType()
		{
			return XMLGregorianCalendar.class;
		}
	}

	private static final long serialVersionUID = 1L;
	@NonNull
	String datePattern;
	Supplier<IModel<String>> getLabel;
	Supplier <Boolean> isRequired;

	@Builder
	public XMLGregorianCalendarTextField(String id, IModel<XMLGregorianCalendar> model, @NonNull String datePattern, Supplier<IModel<String>> getLabel, Supplier <Boolean> isRequired)
	{
		super(id,model);
		this.datePattern = datePattern;
		this.getLabel = getLabel;
		this.isRequired = isRequired;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <C> IConverter<C> getConverter(Class<C> type)
	{
		if (XMLGregorianCalendar.class.isAssignableFrom(type))
			return (IConverter<C>)new XMLGregorianCalendarConverter();
		else
			return super.getConverter(type);
	}
	
	@Override
	public IModel<String> getLabel()
	{
		return getLabel.get();
	}

	@Override
	public boolean isRequired()
	{
		return isRequired.get();
	}
}
