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
package nl.clockwork.ebms.admin.web.service.cpa;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.resource.AbstractResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(staticName = "of")
public class StringResourceStream extends AbstractResourceStream
{
	private static final long serialVersionUID = 1L;
	String s;
	String contentType;

	@Override
	public String getContentType()
	{
		return contentType;
	}

	@Override
	public Bytes length()
	{
		return Bytes.bytes(s.length());
	}

	@Override
	public InputStream getInputStream() throws ResourceStreamNotFoundException
	{
		return new ByteArrayInputStream(s.getBytes());
	}

	@Override
	public void close() throws IOException
	{
	}
}
