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

import nl.clockwork.ebms.Constants.EbMSMessageStatus;

import org.joda.time.DateTime;
import org.joda.time.Period;

public class Constants
{
	public final static String DATE_FORMAT = "dd-MM-yyyy";
	public final static String DATETIME_FORMAT = "dd-MM-yyyy hh:mm:ss";

	public enum TimeUnit
	{
		HOURS(Period.hours(1),Period.days(1),"HH"), DAYS(Period.days(1),Period.weeks(1),"dd"), WEEKS(Period.weeks(1),Period.months(1),"ww"), MONTHS(Period.months(1),Period.years(1),"MM");
		
		private Period period;
		private Period defaultPeriod;
		private String dateFormat;

		TimeUnit(Period period, Period defaultPeriod, String dateFormat)
		{
			this.period = period;
			this.defaultPeriod = defaultPeriod;
			this.dateFormat = dateFormat;
		}
		public Period period()
		{
			return period;
		}
		public Period defaultPeriod()
		{
			return defaultPeriod;
		}
		public String dateFormat()
		{
			return dateFormat; 
		}
		public DateTime getFrom()
		{
			if (HOURS.equals(this))
				return new DateTime().withMillisOfSecond(0).withSecondOfMinute(0).withMinuteOfHour(0).withHourOfDay(0).plusDays(1).minus(this.defaultPeriod());
			else if (DAYS.equals(this))
				return new DateTime().withMillisOfSecond(0).withSecondOfMinute(0).withMinuteOfHour(0).withHourOfDay(0).withDayOfWeek(1).plusWeeks(1).minus(this.defaultPeriod());
			else if (WEEKS.equals(this))
				return new DateTime().withMillisOfSecond(0).withSecondOfMinute(0).withMinuteOfHour(0).withHourOfDay(0).withDayOfMonth(1).plusMonths(1).minus(this.defaultPeriod());
			else if (MONTHS.equals(this))
				return new DateTime().withMillisOfSecond(0).withSecondOfMinute(0).withMinuteOfHour(0).withHourOfDay(0).withDayOfYear(1).plusYears(1).minus(this.defaultPeriod());
			return null;
		}
	}
	
	public enum EbMSMessageTrafficChartSerie
	{
		RECEIVE_STATUS_OK("Ok",Color.GREEN,new EbMSMessageStatus[]{EbMSMessageStatus.PROCESSED,EbMSMessageStatus.FORWARDED}),
		RECEIVE_STATUS_WARN("Warn",Color.ORANGE,new EbMSMessageStatus[]{EbMSMessageStatus.RECEIVED}),
		RECEIVE_STATUS_NOK("Failed",Color.RED,new EbMSMessageStatus[]{EbMSMessageStatus.UNAUTHORIZED,EbMSMessageStatus.NOT_RECOGNIZED,EbMSMessageStatus.FAILED}),
		RECEIVE_STATUS("Received",Color.BLACK,new EbMSMessageStatus[]{EbMSMessageStatus.UNAUTHORIZED,EbMSMessageStatus.NOT_RECOGNIZED,EbMSMessageStatus.RECEIVED,EbMSMessageStatus.PROCESSED,EbMSMessageStatus.FORWARDED,EbMSMessageStatus.FAILED}),
		SEND_STATUS_OK("Ok",Color.GREEN,new EbMSMessageStatus[]{EbMSMessageStatus.DELIVERED}),
		SEND_STATUS_WARN("Warn",Color.ORANGE,new EbMSMessageStatus[]{EbMSMessageStatus.SENT}),
		SEND_STATUS_NOK("Failed",Color.RED,new EbMSMessageStatus[]{EbMSMessageStatus.DELIVERY_ERROR,EbMSMessageStatus.DELIVERY_FAILED}),
		SEND_STATUS("Sent",Color.BLUE,new EbMSMessageStatus[]{EbMSMessageStatus.SENT,EbMSMessageStatus.DELIVERED,EbMSMessageStatus.DELIVERY_ERROR,EbMSMessageStatus.DELIVERY_FAILED});
		
		private String name;
		private Color color;
		private EbMSMessageStatus[] ebMSMessageStatuses;

		private EbMSMessageTrafficChartSerie(String name, Color color, EbMSMessageStatus[] ebMSMessageStatuses)
		{
			this.name = name;
			this.color = color;
			this.ebMSMessageStatuses = ebMSMessageStatuses;
		}
		public String getName()
		{
			return name;
		}
		public Color getColor()
		{
			return color;
		}
		public EbMSMessageStatus[] getEbMSMessageStatuses()
		{
			return ebMSMessageStatuses;
		}
	}
	
	public enum EbMSMessageTrafficChartOption
	{
		ALL(new EbMSMessageTrafficChartSerie[]{EbMSMessageTrafficChartSerie.RECEIVE_STATUS,EbMSMessageTrafficChartSerie.SEND_STATUS}),
		RECEIVED(new EbMSMessageTrafficChartSerie[]{EbMSMessageTrafficChartSerie.RECEIVE_STATUS,EbMSMessageTrafficChartSerie.RECEIVE_STATUS_OK,EbMSMessageTrafficChartSerie.RECEIVE_STATUS_WARN,EbMSMessageTrafficChartSerie.RECEIVE_STATUS_NOK}),
		SEND(new EbMSMessageTrafficChartSerie[]{EbMSMessageTrafficChartSerie.SEND_STATUS,EbMSMessageTrafficChartSerie.SEND_STATUS_OK,EbMSMessageTrafficChartSerie.SEND_STATUS_WARN,EbMSMessageTrafficChartSerie.SEND_STATUS_NOK});
		
		private EbMSMessageTrafficChartSerie[] ebMSMessageTrafficChartSeries;

		private EbMSMessageTrafficChartOption(EbMSMessageTrafficChartSerie[] ebMSMessageTrafficChartSeries)
		{
			this.ebMSMessageTrafficChartSeries = ebMSMessageTrafficChartSeries;
		}
		
		public EbMSMessageTrafficChartSerie[] getEbMSMessageTrafficChartSeries()
		{
			return ebMSMessageTrafficChartSeries;
		}
	}
}
