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
package nl.clockwork.ebms.admin.dao;

import java.util.List;
import java.util.zip.ZipOutputStream;

import org.apache.commons.csv.CSVPrinter;

import nl.clockwork.ebms.admin.model.CPA;
import nl.clockwork.ebms.admin.model.EbMSAttachment;
import nl.clockwork.ebms.admin.model.EbMSMessage;
import nl.clockwork.ebms.admin.web.message.EbMSMessageFilter;

public interface EbMSDAO
{
	CPA getCPA(String cpaId);
	int getCPACount();
	List<String> getCPAIds();
	List<CPA> getCPAs(long first, long count);

	EbMSMessage getMessage(String messageId);
	EbMSMessage getMessage(String messageId, int messageNr);
	int getMessageCount(EbMSMessageFilter filter);
	List<EbMSMessage> getMessages(EbMSMessageFilter filter, long first, long count);

	EbMSAttachment getAttachment(String messageId, int messageNr, String contentId);
	
	void writeMessageToZip(String messageId, int messageNr, ZipOutputStream stream);
	void printMessagesToCSV(CSVPrinter printer, EbMSMessageFilter filter);
}
