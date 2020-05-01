package nl.clockwork.ebms.admin.web;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public enum JQueryLocale
{
	EN("en");
	
	String s;

	@Override
	public String toString()
	{
		return s;
	}
}