package nl.clockwork.ebms.admin.web.message;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Period;

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
	HOUR("Minutes",Period.minutes(1),Period.hours(1),Constants.DATETIME_HOUR_FORMAT,"mm"), DAY("Hours",Period.hours(1),Period.days(1),Constants.DATE_FORMAT,"HH")/*, WEEK("Days",Period.days(1),Period.weeks(1),DATE_FORMAT,"dd"), MONTH("Weeks",Period.weeks(1),Period.months(1),DATE_FORMAT,"ww")*/, MONTH("Days",Period.days(1),Period.months(1),Constants.DATE_MONTH_FORMAT,"dd"), YEAR("Months",Period.months(1),Period.years(1),Constants.DATE_YEAR_FORMAT,"MM");
	
	String units;
	Period timeUnit;
	Period period;
	String dateFormat;
	String timeUnitDateFormat;

	public DateTime getFrom()
	{
		return getFrom(new Date());
	}

	public DateTime getFrom(Date date)
	{
		switch(this)
		{
			case HOUR:
				return new DateTime(date.getTime()).withMillisOfSecond(0).withSecondOfMinute(0).withMinuteOfHour(0).plusHours(1).minus(this.getPeriod());
			case DAY:
				return new DateTime(date.getTime()).withMillisOfSecond(0).withSecondOfMinute(0).withMinuteOfHour(0).withHourOfDay(0).plusDays(1).minus(this.getPeriod());
			//case WEEK:
				//return new DateTime().withMillisOfSecond(0).withSecondOfMinute(0).withMinuteOfHour(0).withHourOfDay(0).withDayOfWeek(1).plusWeeks(1).minus(this.getPeriod());
			case MONTH:
				return new DateTime(date.getTime()).withMillisOfSecond(0).withSecondOfMinute(0).withMinuteOfHour(0).withHourOfDay(0).withDayOfMonth(1).plusMonths(1).minus(this.getPeriod());
			case YEAR:
				return new DateTime(date.getTime()).withMillisOfSecond(0).withSecondOfMinute(0).withMinuteOfHour(0).withHourOfDay(0).withDayOfYear(1).plusYears(1).minus(this.getPeriod());
			default:
				return null;
		}
	}

	public String format(Date date)
	{
		return new SimpleDateFormat(dateFormat).format(date);
	}
}