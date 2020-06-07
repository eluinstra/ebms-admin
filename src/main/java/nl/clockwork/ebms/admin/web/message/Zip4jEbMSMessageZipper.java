package nl.clockwork.ebms.admin.web.message;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.cxf.common.util.StringUtils;
import org.apache.cxf.io.CachedOutputStream;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.outputstream.ZipOutputStream;
import net.lingala.zip4j.model.ZipParameters;
import nl.clockwork.ebms.admin.model.EbMSAttachment;
import nl.clockwork.ebms.admin.model.EbMSMessage;
import nl.clockwork.ebms.admin.web.Utils;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(staticName = "of")
public class Zip4jEbMSMessageZipper
{
	EbMSMessage message;
	CachedOutputStream out;

	public void zip() throws IOException
	{
		writeMessageToZip(message,out);
	}

	private void writeMessageToZip(EbMSMessage message, CachedOutputStream out) throws IOException
	{
		try (ZipOutputStream o = new ZipOutputStream(out))
		{
			addEntry(o,"message.xml",new ByteArrayInputStream(message.getContent().getBytes()));
	    for (EbMSAttachment a : message.getAttachments())
				addEntry(o,"attachments/" + (StringUtils.isEmpty(a.getName()) ? a.getContentId() + Utils.getFileExtension(a.getContentType()) : a.getName()),a.getContent().getInputStream());
		}
	}

	private void addEntry(ZipOutputStream out, String name, InputStream content) throws ZipException, IOException
	{
		ZipParameters parameters = new ZipParameters();
		parameters.setFileNameInZip(name);
		//parameters.setEntrySize(content.size());
		out.putNextEntry(parameters);
		IOUtils.copy(content,out);
	}
}
