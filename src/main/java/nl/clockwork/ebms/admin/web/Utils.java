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

import java.net.URLConnection;
import java.util.Arrays;
import java.util.EnumSet;

import org.apache.commons.lang3.StringUtils;

import io.vavr.Function2;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import lombok.experimental.FieldDefaults;
import nl.clockwork.ebms.EbMSMessageStatus;

public class Utils
{
	@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
	@AllArgsConstructor
	@Getter
	private enum Status
	{
		SUCCESS(EnumSet.of(EbMSMessageStatus.PROCESSED,EbMSMessageStatus.FORWARDED,EbMSMessageStatus.DELIVERED),"success","text-success"),
		WARNING(EnumSet.of(EbMSMessageStatus.RECEIVED,EbMSMessageStatus.CREATED),"warning","text-warning"),
		DANGER(EnumSet.of(EbMSMessageStatus.UNAUTHORIZED,EbMSMessageStatus.NOT_RECOGNIZED,EbMSMessageStatus.FAILED,EbMSMessageStatus.DELIVERY_FAILED,EbMSMessageStatus.EXPIRED),"danger","text-danger");
		
		EnumSet<EbMSMessageStatus> statuses;
		String rowClass;
		String cellClass;

		public static Function2<EbMSMessageStatus,Function<Status,String>,String> getCssClass = (status,getClass) ->
				Arrays.stream(Status.values())
					.filter(s -> s.statuses.contains(status))
					.map(s -> getClass.apply(s))
					.findFirst()
					.orElse(null);
	}

	public static String getResourceString(Class<?> clazz, String propertyName)
	{
		val loaders = WicketApplication.get().getResourceSettings().getStringResourceLoaders();
		return loaders.stream().map(l -> l.loadStringResource(clazz,propertyName,null,null,null)).filter(s -> StringUtils.isNotBlank(s)).findFirst().orElse(propertyName);
	}

	public static String getContentType(String pathInfo)
	{
		val result = URLConnection.guessContentTypeFromName(pathInfo);
		//val result = new MimetypesFileTypeMap().getContentType(pathInfo);
		//val result = URLConnection.getFileNameMap().getContentTypeFor(pathInfo);
		return result == null ? "application/octet-stream" : result;
	}

	public static String getFileExtension(String contentType)
	{
		return StringUtils.isNotEmpty(contentType) ? "." + (contentType.contains("text") ? "txt" : contentType.split("/")[1]) : "";
	}

	public static String getTableCellCssClass(EbMSMessageStatus ebMSMessageStatus)
	{
		return Status.getCssClass.apply(ebMSMessageStatus,Status::getCellClass);
	}

	public static String getTableRowCssClass(EbMSMessageStatus ebMSMessageStatus)
	{
		return Status.getCssClass.apply(ebMSMessageStatus,Status::getRowClass);
	}

	public static String getErrorList(String content)
	{
		return content.replaceFirst("(?ms)^.*(<[^<>]*:?ErrorList.*ErrorList>).*$","$1");
	}
}
