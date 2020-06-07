package nl.clockwork.ebms.admin.web.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.cxf.common.util.StringUtils;
import org.fusesource.hawtbuf.ByteArrayInputStream;

import io.vavr.control.Either;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.val;
import lombok.experimental.FieldDefaults;
import nl.clockwork.ebms.admin.model.EbMSAttachment;
import nl.clockwork.ebms.admin.model.EbMSMessage;
import nl.clockwork.ebms.admin.web.Utils;

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
		try (val o = new ZipOutputStream(out))
		{
			addEntry(o,"message.xml",new ByteArrayInputStream(message.getContent().getBytes())).getOrElseThrow(e -> e);
	    for (EbMSAttachment a : message.getAttachments())
				addEntry(o,"attachments/" + (StringUtils.isEmpty(a.getName()) ? a.getContentId() + Utils.getFileExtension(a.getContentType()) : a.getName()),a.getContent().getInputStream());
		}
	}

	private Either<IOException,Void> addEntry(ZipOutputStream out, String name, InputStream content)
	{
		try
		{
			val entry = new ZipEntry(name);
			//entry.setSize(content.getSize());
			out.putNextEntry(entry);
			IOUtils.copy(content,out);
			out.closeEntry();
			return Either.right(null);
		}
		catch (IOException e)
		{
			return Either.left(e);
		}
	}

}
