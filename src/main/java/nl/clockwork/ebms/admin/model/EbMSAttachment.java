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

import org.apache.wicket.util.io.IClusterable;

public class EbMSAttachment implements IClusterable
{
	private static final long serialVersionUID = 1L;
	private EbMSMessage message;
	private String name;
	private String contentId;
	private String contentType;
	private byte[] content;

	public EbMSAttachment(String name, String contentId, String contentType, byte[] content)
	{
		this.name = name;
		this.contentId = contentId;
		this.contentType = contentType;
		this.content = content;
	}

	public EbMSMessage getMessage()
	{
		return message;
	}
	
	public void setMessage(EbMSMessage message)
	{
		this.message = message;
	}
	
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getContentId()
	{
		return contentId;
	}
	public void setContentId(String contentId)
	{
		this.contentId = contentId;
	}
	public String getContentType()
	{
		return contentType;
	}
	public void setContentType(String contentType)
	{
		this.contentType = contentType;
	}
	public byte[] getContent()
	{
		return content;
	}
	public void setContent(byte[] content)
	{
		this.content = content;
	}
	
}
