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
package nl.clockwork.ebms.admin.web.message;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import nl.clockwork.ebms.admin.Constants;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Getter
public enum TimeUnit
{
	HOUR("Minutes",ChronoUnit.MINUTES,ChronoUnit.HOURS,DateTimeFormatter.ofPattern(Constants.DATETIME_HOUR_FORMAT),DateTimeFormatter.ofPattern("mm"),"mm"),
	DAY("Hours",ChronoUnit.HOURS,ChronoUnit.DAYS,DateTimeFormatter.ofPattern(Constants.DATE_FORMAT),DateTimeFormatter.ofPattern("HH"),"HH"),
	/*WEEK("Days",ChronoUnit.DAYS,ChronoUnit.WEEKS,DateTimeFormatter.ofPattern(Constants.DATE_FORMAT),DateTimeFormatter.ofPattern("dd"),"dd"),
	MONTH("Weeks",ChronoUnit.WEEKS,MONTHS,DateTimeFormatter.ofPattern(Constants.DATE_FORMAT),DateTimeFormatter.ofPattern("ww"),"ww"),*/
	MONTH("Days",ChronoUnit.DAYS,ChronoUnit.MONTHS,DateTimeFormatter.ofPattern(Constants.DATE_MONTH_FORMAT),DateTimeFormatter.ofPattern("dd"),"dd"),
	YEAR("Months",ChronoUnit.MONTHS,ChronoUnit.YEARS,DateTimeFormatter.ofPattern(Constants.DATE_YEAR_FORMAT),DateTimeFormatter.ofPattern("MM"),"MM");
	
	String units;
	ChronoUnit timeUnit;
	ChronoUnit period;
	DateTimeFormatter dateFormatter;
	DateTimeFormatter timeUnitDateFormat;
	String sqlDateFormat;

	public LocalDateTime getFrom()
	{
		return getFrom(LocalDateTime.now());
	}

	public LocalDateTime getFrom(LocalDateTime dateTime)
	{
		switch(this)
		{
			case HOUR:
				return dateTime.truncatedTo(ChronoUnit.HOURS).plusHours(1).minus(this.getPeriod().getDuration());
			case DAY:
				return dateTime.truncatedTo(ChronoUnit.DAYS).plusDays(1).minus(this.getPeriod().getDuration());
			//case WEEK:
				//return dateTime.truncatedTo(ChronoUnit.DAYS).withDayOfWeek(1).plusWeeks(1).minus(this.getPeriod().getDuration());
			case MONTH:
				return dateTime.truncatedTo(ChronoUnit.DAYS).withDayOfMonth(1).plusMonths(1).minus(this.getPeriod().getDuration());
			case YEAR:
				return dateTime.truncatedTo(ChronoUnit.DAYS).withDayOfYear(1).plusYears(1).minus(this.getPeriod().getDuration());
			default:
				return null;
		}
	}
}