package nl.clockwork.ebms.admin.web.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.cxf.common.util.StringUtils;
import org.fusesource.hawtbuf.ByteArrayInputStream;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.val;
import lombok.experimental.FieldDefaults;
import nl.clockwork.ebms.admin.model.EbMSAttachment;
import nl.clockwork.ebms.admin.model.EbMSMessage;
import nl.clockwork.ebms.admin.web.Utils;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(staticName = "of")
public class CommonsEbMSMessageZipper
{
	EbMSMessage message;
	OutputStream out;

	public void zip() throws IOException
	{
		writeMessageToZip(message,out);
	}

	private void writeMessageToZip(EbMSMessage message, OutputStream out) throws IOException
	{
		try (val o = new ZipArchiveOutputStream(out))
		{
			addEntry(o,"message.xml",new ByteArrayInputStream(message.getContent().getBytes()));
	    for (EbMSAttachment a : message.getAttachments())
        addEntry(o,"attachments/" + (StringUtils.isEmpty(a.getName()) ? a.getContentId() + Utils.getFileExtension(a.getContentType()) : a.getName()),a.getContent().getInputStream());
		}
	}

	private void addEntry(ArchiveOutputStream out, String name, InputStream content) throws IOException
	{
		val entry = new ZipArchiveEntry(name);
		//entry.setSize(content.length);
		out.putArchiveEntry(entry);
		IOUtils.copy(content,out);
		out.closeArchiveEntry();
	}
}
