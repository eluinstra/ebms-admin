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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nl.clockwork.ebms.admin.Constants.JQueryLocale;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.internal.HtmlHeaderContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

public class BootstrapDateTimePicker extends FormComponentPanel<Date>
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
	private String format;
	private String formatJS;
	private Type type = Type.DATE_TIME;
	private HourFormat hourFormat;
	private Date startDate;
	private Date endDate;
	private Date dateTime;
	private TextField<Date> dateTimeField;

	public BootstrapDateTimePicker(final String id)
	{
		this(id,(IModel<Date>)null);
	}
	public BootstrapDateTimePicker(final String id, String format)
	{
		this(id,null,format);
	}
	
	public BootstrapDateTimePicker(final String id, IModel<Date> model)
	{
		this(id,model,"dd-MM-yyyy HH:mm:ss");
	}

	public BootstrapDateTimePicker(final String id, IModel<Date> model, String format)
	{
		super(id,model);
		this.format = format;
		this.hourFormat = format.contains("H") ? HourFormat.H24 : HourFormat.H12;
		this.formatJS = format.replaceAll("H","h");
		setType(Date.class);
		
		MarkupContainer dateTimePicker = new MarkupContainer("dateTimePicker")
		{
			private static final long serialVersionUID = 1L;
		};
		dateTimePicker.setMarkupId(getDateTimePickerId());
		dateTimePicker.setOutputMarkupId(true);
		add(dateTimePicker);

		dateTimeField = new DateTextField("dateTime",new PropertyModel<Date>(this,"dateTime"),format)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isRequired()
			{
				return BootstrapDateTimePicker.this.isRequired();
			}
		};
		dateTimePicker.add(dateTimeField);
	}
	
	public static String getLinkJavaScript(BootstrapDateTimePicker startDate, BootstrapDateTimePicker endDate)
	{
		return
			"$(function () {" +
			"$('#" + startDate.getDateTimePickerId() + "').on('changeDate',function () {" +
			   "$('#" + endDate.getDateTimePickerId() + "').data('datetimepicker').setStartDate($('#" + startDate.getDateTimePickerId() + "').data('datetimepicker').getDate());" +
			"});" +
			"$('#" + endDate.getDateTimePickerId() + "').on('changeDate',function (e) {" +
			   "$('#" + startDate.getDateTimePickerId() + "').data('datetimepicker').setEndDate($('#" + endDate.getDateTimePickerId() + "').data('datetimepicker').getDate());" +
			"});" +
		"});";
	}
	
	@Override
	public void renderHead(HtmlHeaderContainer container)
	{
		org.apache.wicket.markup.head.IHeaderResponse response = container.getHeaderResponse();
		List<String> options = new ArrayList<String>();
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
			options.add("startDate: new Date(" + startDate.getTime() + ")");
		if (endDate != null)
			options.add("endDate: new Date(" + endDate.getTime() + ")");
		response.render(OnDomReadyHeaderItem.forScript("$(function () {$('#" + getDateTimePickerId() + "').datetimepicker({" + StringUtils.join(options,",") + "});});"));
		super.renderHead(container);
	}
	
	@Override
	protected void convertInput()
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
	
	public BootstrapDateTimePicker setType(Type type)
	{
		this.type = type;
		return this;
	}
	
	public JQueryLocale getJQueryLocale()
	{
		return JQueryLocale.EN;
	}

	public void setStartDate(Date startDate)
	{
		this.startDate = startDate;
	}

	public void setEndDate(Date endDate)
	{
		this.endDate = endDate;
	}
	
	private String getDateTimePickerId()
	{
		return getMarkupId() + "DateTimePicker";
	}
	
	public Date getDateTime()
	{
		return dateTime;
	}
	
	public void setDateTime(Date dateTime)
	{
		this.dateTime = dateTime;
	}
}