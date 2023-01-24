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
import java.util.ArrayList;
import javax.xml.datatype.XMLGregorianCalendar;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BootstrapXMLGregorianCalendarDateTimePicker extends FormComponentPanel<XMLGregorianCalendar>
{
	public enum Type
	{
		DATE_TIME, DATE, TIME;
	}

	public enum HourFormat
	{
		H12, H24;
	}

	private static final long serialVersionUID = 1L;
	@NonNull
	String format;
	@NonNull
	String formatJS;
	Type type = Type.DATE_TIME;
	@NonNull
	HourFormat hourFormat;
	@NonFinal
	@Setter
	Instant startDate;
	@NonFinal
	@Setter
	Instant endDate;
	@NonFinal
	@Getter
	@Setter
	XMLGregorianCalendar dateTime;
	@NonNull
	TextField<XMLGregorianCalendar> dateTimeField;

	public BootstrapXMLGregorianCalendarDateTimePicker(final String id)
	{
		this(id, (IModel<XMLGregorianCalendar>)null);
	}

	public BootstrapXMLGregorianCalendarDateTimePicker(final String id, String format)
	{
		this(id, null, format);
	}

	public BootstrapXMLGregorianCalendarDateTimePicker(final String id, IModel<XMLGregorianCalendar> model)
	{
		this(id, model, "dd-MM-yyyy HH:mm:ss");
	}

	public BootstrapXMLGregorianCalendarDateTimePicker(final String id, IModel<XMLGregorianCalendar> model, String format)
	{
		super(id, model);
		this.format = format;
		this.hourFormat = format.contains("H") ? HourFormat.H24 : HourFormat.H12;
		this.formatJS = format.replaceAll("H", "h");
		setType(XMLGregorianCalendar.class);

		val dateTimePicker = new WebMarkupContainer("dateTimePicker");
		dateTimePicker.setMarkupId(getDateTimePickerId());
		dateTimePicker.setOutputMarkupId(true);
		add(dateTimePicker);

		dateTimeField = XMLGregorianCalendarTextField.builder()
				.id("dateTime")
				.model(new PropertyModel<>(this, "dateTime"))
				.datePattern(format)
				.getLabel(() -> getLabel())
				.isRequired(() -> isRequired())
				.build();
		dateTimePicker.add(dateTimeField);
	}

	public static
			String
			getLinkBootstrapDateTimePickersJavaScript(BootstrapXMLGregorianCalendarDateTimePicker startDate, BootstrapXMLGregorianCalendarDateTimePicker endDate)
	{
		return "$(function () {"
				+ "$('#"
				+ startDate.getDateTimePickerId()
				+ "').on('changeDate',function () {"
				+ "var d = $('#"
				+ startDate.getDateTimePickerId()
				+ "').data('datetimepicker').getDate();"
				+ "d.setDate(d.getDate() + 1);"
				+ "$('#"
				+ endDate.getDateTimePickerId()
				+ "').data('datetimepicker').setStartDate(d);"
				+ "});"
				+ "$('#"
				+ endDate.getDateTimePickerId()
				+ "').on('changeDate',function (e) {"
				+ "var d = $('#"
				+ endDate.getDateTimePickerId()
				+ "').data('datetimepicker').getDate();"
				+ "d.setDate(d.getDate() - 1);"
				+ "$('#"
				+ startDate.getDateTimePickerId()
				+ "').data('datetimepicker').setEndDate(d);"
				+ "});"
				+ "});";
	}

	@Override
	public void renderHead(IHeaderResponse response)
	{
		val options = new ArrayList<String>();
		if (formatJS != null)
			options.add("format: '" + formatJS + "'");
		if (!Type.DATE_TIME.equals(type) & !Type.DATE.equals(type))
			options.add("pickDate: false");
		if (!Type.DATE_TIME.equals(type) & !Type.TIME.equals(type))
			options.add("pickTime: false");
		if (HourFormat.H12.equals(hourFormat))
			options.add("pick12HourFormat: true");
		if (getJQueryLocale() != null)
			options.add("language: '" + getLocale().toString() + "'");
		if (startDate != null)
			options.add("startDate: new Date(" + startDate.toEpochMilli() + ")");
		if (endDate != null)
			options.add("endDate: new Date(" + endDate.toEpochMilli() + ")");
		response.render(
				OnDomReadyHeaderItem.forScript("$(function () {$('#" + getDateTimePickerId() + "').datetimepicker({" + StringUtils.join(options, ",") + "});});"));
		super.renderHead(response);
	}

	@Override
	public void convertInput()
	{
		dateTime = dateTimeField.getConvertedInput();
		setConvertedInput(dateTime);
	}

	@Override
	protected void onBeforeRender()
	{
		dateTime = getModelObject();
		super.onBeforeRender();
	}

	public String getDateFormat()
	{
		return format;
	}

	public JQueryLocale getJQueryLocale()
	{
		return JQueryLocale.EN;
	}

	private String getDateTimePickerId()
	{
		return getMarkupId() + "DateTimePicker";
	}
}