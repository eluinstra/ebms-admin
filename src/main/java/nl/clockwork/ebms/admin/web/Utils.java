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

import nl.clockwork.ebms.Constants.EbMSMessageStatus;

import org.apache.commons.lang.StringUtils;

public class Utils
{
	public static String getFileExtension(String contentType)
	{
		if (StringUtils.isEmpty(contentType))
			return "";
		return "." + (contentType.contains("text") ? "txt" : contentType.split("/")[1]);
	}

	public static String getTableCellCssClass(EbMSMessageStatus ebMSMessageStatus)
	{
		if (EbMSMessageStatus.PROCESSED.equals(ebMSMessageStatus) || EbMSMessageStatus.FORWARDED.equals(ebMSMessageStatus) || EbMSMessageStatus.DELIVERED.equals(ebMSMessageStatus))
			return "text-success";
		if (EbMSMessageStatus.RECEIVED.equals(ebMSMessageStatus) || EbMSMessageStatus.SENT.equals(ebMSMessageStatus))
			return "text-warning";
		if (EbMSMessageStatus.UNAUTHORIZED.equals(ebMSMessageStatus) || EbMSMessageStatus.NOT_RECOGNIZED.equals(ebMSMessageStatus) || EbMSMessageStatus.FAILED.equals(ebMSMessageStatus) || EbMSMessageStatus.DELIVERY_ERROR.equals(ebMSMessageStatus) || EbMSMessageStatus.DELIVERY_FAILED.equals(ebMSMessageStatus))
			return "text-danger";
		return null;
	}

	public static String getTableRowCssClass(EbMSMessageStatus ebMSMessageStatus)
	{
		if (EbMSMessageStatus.PROCESSED.equals(ebMSMessageStatus) || EbMSMessageStatus.FORWARDED.equals(ebMSMessageStatus) || EbMSMessageStatus.DELIVERED.equals(ebMSMessageStatus))
			return "success";
		if (EbMSMessageStatus.RECEIVED.equals(ebMSMessageStatus) || EbMSMessageStatus.SENT.equals(ebMSMessageStatus))
			return "warning";
		if (EbMSMessageStatus.UNAUTHORIZED.equals(ebMSMessageStatus) || EbMSMessageStatus.NOT_RECOGNIZED.equals(ebMSMessageStatus) || EbMSMessageStatus.FAILED.equals(ebMSMessageStatus) || EbMSMessageStatus.DELIVERY_ERROR.equals(ebMSMessageStatus) || EbMSMessageStatus.DELIVERY_FAILED.equals(ebMSMessageStatus))
			return "danger";
		return null;
	}

}
