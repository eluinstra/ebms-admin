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
package nl.clockwork.ebms.admin;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;

import nl.clockwork.ebms.Constants.EbMSMessageStatus;

import org.joda.time.DateTime;
import org.joda.time.Period;

import de.adesso.wickedcharts.chartjs.chartoptions.colors.SimpleColor;

public class Constants
{
	public final static String DATE_FORMAT = "dd-MM-yyyy";
	public final static String DATE_MONTH_FORMAT = "MM-yyyy";
	public final static String DATE_YEAR_FORMAT = "yyyy";
	public final static String DATETIME_FORMAT = "dd-MM-yyyy HH:mm:ss";
	public final static String DATETIME_HOUR_FORMAT = "dd-MM-yyyy HH:mm";

	public enum JQueryLocale
	{
		EN("en");
		
		private String s;

		private JQueryLocale(String s)
		{
			this.s = s;
		}

		@Override
		public String toString()
		{
			return s;
		}
	}

	public enum TimeUnit
	{
		HOUR("Minutes",Period.minutes(1),Period.hours(1),DATETIME_HOUR_FORMAT,"mm"), DAY("Hours",Period.hours(1),Period.days(1),DATE_FORMAT,"HH")/*, WEEK("Days",Period.days(1),Period.weeks(1),DATE_FORMAT,"dd"), MONTH("Weeks",Period.weeks(1),Period.months(1),DATE_FORMAT,"ww")*/, MONTH("Days",Period.days(1),Period.months(1),DATE_MONTH_FORMAT,"dd"), YEAR("Months",Period.months(1),Period.years(1),DATE_YEAR_FORMAT,"MM");
		
		private String units;
		private Period timeUnit;
		private Period period;
		private String dateFormat;
		private String timeUnitDateFormat;

		TimeUnit(String units, Period timeUnit, Period period, String dateFormat, String timeUnitDateFormat)
		{
			this.units = units;
			this.timeUnit = timeUnit;
			this.period = period;
			this.dateFormat = dateFormat;
			this.timeUnitDateFormat = timeUnitDateFormat;
		}
		public String getUnits()
		{
			return units;
		}
		public Period getTimeUnit()
		{
			return timeUnit;
		}
		public Period getPeriod()
		{
			return period;
		}
		public String getDateFormat()
		{
			return dateFormat;
		}
		public String getTimeUnitDateFormat()
		{
			return timeUnitDateFormat; 
		}
		public DateTime getFrom()
		{
			return getFrom(new Date());
		}
		public DateTime getFrom(Date date)
		{
			if (HOUR.equals(this))
				return new DateTime(date.getTime()).withMillisOfSecond(0).withSecondOfMinute(0).withMinuteOfHour(0).plusHours(1).minus(this.getPeriod());
			else if (DAY.equals(this))
				return new DateTime(date.getTime()).withMillisOfSecond(0).withSecondOfMinute(0).withMinuteOfHour(0).withHourOfDay(0).plusDays(1).minus(this.getPeriod());
			//else if (WEEK.equals(this))
				//return new DateTime().withMillisOfSecond(0).withSecondOfMinute(0).withMinuteOfHour(0).withHourOfDay(0).withDayOfWeek(1).plusWeeks(1).minus(this.getPeriod());
			else if (MONTH.equals(this))
				return new DateTime(date.getTime()).withMillisOfSecond(0).withSecondOfMinute(0).withMinuteOfHour(0).withHourOfDay(0).withDayOfMonth(1).plusMonths(1).minus(this.getPeriod());
			else if (YEAR.equals(this))
				return new DateTime(date.getTime()).withMillisOfSecond(0).withSecondOfMinute(0).withMinuteOfHour(0).withHourOfDay(0).withDayOfYear(1).plusYears(1).minus(this.getPeriod());
			return null;
		}
		public String format(Date date)
		{
			return new SimpleDateFormat(dateFormat).format(date);
		}
	}
	
	public enum EbMSMessageTrafficChartSerie
	{
		RECEIVE_STATUS_OK("Ok",SimpleColor.GREEN,Color.GREEN,new EbMSMessageStatus[]{EbMSMessageStatus.PROCESSED,EbMSMessageStatus.FORWARDED}),
		RECEIVE_STATUS_WARN("Warn",SimpleColor.ORANGE,Color.ORANGE,new EbMSMessageStatus[]{EbMSMessageStatus.RECEIVED}),
		RECEIVE_STATUS_NOK("Failed",SimpleColor.RED,Color.RED,new EbMSMessageStatus[]{EbMSMessageStatus.UNAUTHORIZED,EbMSMessageStatus.NOT_RECOGNIZED,EbMSMessageStatus.FAILED}),
		RECEIVE_STATUS("Received",SimpleColor.GREY,Color.BLACK,new EbMSMessageStatus[]{EbMSMessageStatus.UNAUTHORIZED,EbMSMessageStatus.NOT_RECOGNIZED,EbMSMessageStatus.RECEIVED,EbMSMessageStatus.PROCESSED,EbMSMessageStatus.FORWARDED,EbMSMessageStatus.FAILED}),
		SEND_STATUS_OK("Ok",SimpleColor.GREEN,Color.GREEN,new EbMSMessageStatus[]{EbMSMessageStatus.DELIVERED}),
		SEND_STATUS_WARN("Warn",SimpleColor.ORANGE,Color.ORANGE,new EbMSMessageStatus[]{EbMSMessageStatus.SENDING}),
		SEND_STATUS_NOK("Failed",SimpleColor.RED,Color.RED,new EbMSMessageStatus[]{EbMSMessageStatus.DELIVERY_FAILED,EbMSMessageStatus.EXPIRED}),
		SEND_STATUS("Sending",SimpleColor.BLUE,Color.BLUE,new EbMSMessageStatus[]{EbMSMessageStatus.SENDING,EbMSMessageStatus.DELIVERED,EbMSMessageStatus.DELIVERY_FAILED,EbMSMessageStatus.EXPIRED});
		
		private String name;
		private de.adesso.wickedcharts.chartjs.chartoptions.colors.Color color;
		private Color colorX;
		private EbMSMessageStatus[] ebMSMessageStatuses;

		private EbMSMessageTrafficChartSerie(String name, de.adesso.wickedcharts.chartjs.chartoptions.colors.Color color, Color colorX, EbMSMessageStatus[] ebMSMessageStatuses)
		{
			this.name = name;
			this.color = color;
			this.colorX = colorX;
			this.ebMSMessageStatuses = ebMSMessageStatuses;
		}
		public String getName()
		{
			return name;
		}
		public de.adesso.wickedcharts.chartjs.chartoptions.colors.Color getColor()
		{
			return color;
		}
		public Color getColorX()
		{
			return colorX;
		}
		public EbMSMessageStatus[] getEbMSMessageStatuses()
		{
			return ebMSMessageStatuses;
		}
	}
	
	public enum EbMSMessageTrafficChartOption
	{
		ALL("All Messages",new EbMSMessageTrafficChartSerie[]{EbMSMessageTrafficChartSerie.RECEIVE_STATUS,EbMSMessageTrafficChartSerie.SEND_STATUS}),
		RECEIVED("Received Messages",new EbMSMessageTrafficChartSerie[]{EbMSMessageTrafficChartSerie.RECEIVE_STATUS_NOK,EbMSMessageTrafficChartSerie.RECEIVE_STATUS_WARN,EbMSMessageTrafficChartSerie.RECEIVE_STATUS_OK,EbMSMessageTrafficChartSerie.RECEIVE_STATUS}),
		SENT("Sending Messages",new EbMSMessageTrafficChartSerie[]{EbMSMessageTrafficChartSerie.SEND_STATUS_NOK,EbMSMessageTrafficChartSerie.SEND_STATUS_WARN,EbMSMessageTrafficChartSerie.SEND_STATUS_OK,EbMSMessageTrafficChartSerie.SEND_STATUS});
		
		private String title;
		private EbMSMessageTrafficChartSerie[] ebMSMessageTrafficChartSeries;

		private EbMSMessageTrafficChartOption(String title, EbMSMessageTrafficChartSerie[] ebMSMessageTrafficChartSeries)
		{
			this.title = title;
			this.ebMSMessageTrafficChartSeries = ebMSMessageTrafficChartSeries;
		}
		public String getTitle()
		{
			return title;
		}
		public EbMSMessageTrafficChartSerie[] getEbMSMessageTrafficChartSeries()
		{
			return ebMSMessageTrafficChartSeries;
		}
	}
}
