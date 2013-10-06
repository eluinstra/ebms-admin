package nl.clockwork.ebms.admin.web;

import org.apache.commons.lang.StringUtils;

public class Utils
{
	public static String getFileExtension(String contentType)
	{
		if (StringUtils.isEmpty(contentType))
			return null;
		return "." + (contentType.contains("text") ? "txt" : contentType.split("/")[1]);
	}
}
