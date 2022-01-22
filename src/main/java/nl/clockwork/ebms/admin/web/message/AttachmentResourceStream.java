/*
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

import java.io.IOException;
import java.io.InputStream;

import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.resource.AbstractResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import nl.clockwork.ebms.admin.model.EbMSAttachment;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(staticName = "of")
public class AttachmentResourceStream extends AbstractResourceStream
{
	private static final long serialVersionUID = 1L;
	@NonNull
	EbMSAttachment attachment;

	@Override
	public String getContentType()
	{
		return attachment.getContentType();
	}
	
	@Override
	public Bytes length()
	{
		return Bytes.bytes(attachment.getContent().size());
	}
	
	@Override
	public InputStream getInputStream() throws ResourceStreamNotFoundException
	{
		try
		{
			return attachment.getContent().getInputStream();
		}
		catch (IOException e)
		{
			throw new ResourceStreamNotFoundException(e);
		}
	}
	
	@Override
	public void close() throws IOException
	{
	}
}