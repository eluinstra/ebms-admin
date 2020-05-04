package nl.clockwork.ebms.admin.web.message;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAmount;

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
	HOUR("Minutes",Duration.ofMinutes(1),Duration.ofHours(1),DateTimeFormatter.ofPattern(Constants.DATETIME_HOUR_FORMAT),DateTimeFormatter.ofPattern("mm"),"mm"),
	DAY("Hours",Duration.ofHours(1),Duration.ofDays(1),DateTimeFormatter.ofPattern(Constants.DATE_FORMAT),DateTimeFormatter.ofPattern("HH"),"HH"),
	/*WEEK("Days",Period.ofDays(1),Period.ofWeeks(1),DateTimeFormatter.ofPattern(Constants.DATE_FORMAT),DateTimeFormatter.ofPattern("dd"),"dd"),
	MONTH("Weeks",Period.ofWeeks(1),Period.ofMonths(1),DateTimeFormatter.ofPattern(Constants.DATE_FORMAT),DateTimeFormatter.ofPattern("ww"),"ww"),*/
	MONTH("Days",Period.ofDays(1),Period.ofMonths(1),DateTimeFormatter.ofPattern(Constants.DATE_MONTH_FORMAT),DateTimeFormatter.ofPattern("dd"),"dd"),
	YEAR("Months",Period.ofMonths(1),Period.ofYears(1),DateTimeFormatter.ofPattern(Constants.DATE_YEAR_FORMAT),DateTimeFormatter.ofPattern("MM"),"MM");
	
	String units;
	TemporalAmount timeUnit;
	TemporalAmount period;
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
				return dateTime.withNano(0).with(ChronoField.MILLI_OF_SECOND,0).withSecond(0).withMinute(0).plusHours(1).minus(this.getPeriod());
			case DAY:
				return dateTime.withNano(0).with(ChronoField.MILLI_OF_SECOND,0).withSecond(0).withMinute(0).withHour(0).plusDays(1).minus(this.getPeriod());
			//case WEEK:
				//return dateTime.withNano(0).with(ChronoField.MILLI_OF_SECOND,0).withSecond(0).withMinute(0).withHour(0).withDayOfWeek(1).plusWeeks(1).minus(this.getPeriod());
			case MONTH:
				return dateTime.withNano(0).with(ChronoField.MILLI_OF_SECOND,0).withSecond(0).withMinute(0).withHour(0).withDayOfMonth(1).plusMonths(1).minus(this.getPeriod());
			case YEAR:
				return dateTime.withNano(0).with(ChronoField.MILLI_OF_SECOND,0).withSecond(0).withMinute(0).withHour(0).withDayOfYear(1).plusYears(1).minus(this.getPeriod());
			default:
				return null;
		}
	}
}