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
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import nl.clockwork.ebms.admin.model.EbMSAttachment;
import nl.clockwork.ebms.admin.model.EbMSMessage;
import nl.clockwork.ebms.admin.web.Utils;
import org.apache.commons.io.IOUtils;
import org.apache.cxf.common.util.StringUtils;
import org.fusesource.hawtbuf.ByteArrayInputStream;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(staticName = "of")
public class EbMSMessageZipper
{
	EbMSMessage message;
	OutputStream out;

	public void zip() throws IOException
	{
		writeMessageToZip(message,out);
	}

	private void writeMessageToZip(EbMSMessage message, OutputStream out) throws IOException
	{
		try (val zip = new ZipOutputStream(out))
		{
			addEntry(zip,"message.xml",new ByteArrayInputStream(message.getContent().getBytes()));
			for (EbMSAttachment a : message.getAttachments())
				addEntry(zip,
						"attachments/" + (StringUtils.isEmpty(a.getName()) ? a.getContentId() + Utils.getFileExtension(a.getContentType()) : a.getName()),
						a.getContent().getInputStream());
			zip.finish();
		}
	}

	private void addEntry(ZipOutputStream out, String name, InputStream content) throws IOException
	{
		val entry = new ZipEntry(name);
		// entry.setSize(content.getSize());
		out.putNextEntry(entry);
		IOUtils.copy(content,out);
		out.closeEntry();
	}
}
