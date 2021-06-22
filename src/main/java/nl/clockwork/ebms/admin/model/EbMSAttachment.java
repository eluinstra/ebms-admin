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
package nl.clockwork.ebms.admin.model;

import org.apache.cxf.io.CachedOutputStream;
import org.apache.wicket.util.io.IClusterable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

@Value
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class EbMSAttachment implements IClusterable
{
	private static final long serialVersionUID = 1L;
	@NonFinal
	@Setter
	EbMSMessage message;
	String name;
	@NonNull
	String contentId;
	@NonNull
	String contentType;
	CachedOutputStream content;

	public EbMSAttachment(String name, @NonNull String contentId, @NonNull String contentType)
	{
		this(null,name,contentId,contentType,null);
	}

	public EbMSAttachment(String name, @NonNull String contentId, @NonNull String contentType, CachedOutputStream content)
	{
		this(null,name,contentId,contentType,content);
	}
}
