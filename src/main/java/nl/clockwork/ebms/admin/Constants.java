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

import nl.clockwork.ebms.Constants.EbMSMessageStatus;

import org.apache.commons.lang.ArrayUtils;
import org.joda.time.Period;

public class Constants
{
	public final static String DATETIME_FORMAT = "dd-MM-yyyy hh:mm:ss";

	public enum TimeUnit
	{
		HOURS(Period.hours(1),Period.hours(48),"HH"), DAYS(Period.days(1),Period.days(14),"dd"), WEEKS(Period.weeks(1),Period.weeks(13),"ww"), MONTHS(Period.months(1),Period.months(12),"MM")/*, YEARS(Period.years(1),Period.years(10),"yyyy")*/;
		
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
	}

	public final static EbMSMessageStatus[] receiveStatus = {EbMSMessageStatus.UNAUTHORIZED,EbMSMessageStatus.NOT_RECOGNIZED,EbMSMessageStatus.RECEIVED,EbMSMessageStatus.PROCESSED,EbMSMessageStatus.FORWARDED,EbMSMessageStatus.FAILED};
	public final static EbMSMessageStatus[] sendStatus = {EbMSMessageStatus.SENT,EbMSMessageStatus.ACKNOWLEDGED,EbMSMessageStatus.DELIVERY_FAILED,EbMSMessageStatus.NOT_ACKNOWLEDGED};
	public final static EbMSMessageStatus[] allStatus = (EbMSMessageStatus[])ArrayUtils.addAll(receiveStatus,sendStatus);
}
