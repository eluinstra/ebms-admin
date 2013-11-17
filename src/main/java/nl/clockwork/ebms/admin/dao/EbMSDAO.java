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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipOutputStream;

import nl.clockwork.ebms.Constants.EbMSMessageStatus;
import nl.clockwork.ebms.admin.Constants.TimeUnit;
import nl.clockwork.ebms.admin.model.CPA;
import nl.clockwork.ebms.admin.model.EbMSAttachment;
import nl.clockwork.ebms.admin.model.EbMSMessage;
import nl.clockwork.ebms.admin.web.message.EbMSMessageFilter;

import org.apache.commons.csv.CSVPrinter;

public interface EbMSDAO
{
	CPA findCPA(String cpaId);
	int countCPAs();
	List<String> selectCPAIds();
	List<CPA> selectCPAs(long first, long count);

	EbMSMessage findMessage(String messageId);
	EbMSMessage findMessage(long id);
	int countMessages(EbMSMessageFilter filter);
	List<EbMSMessage> selectMessages(EbMSMessageFilter filter, long first, long count);

	EbMSAttachment findAttachment(long id, String contentId);
	
	List<String> selectMessageIds(String cpaId, String fromParty, String toParty, EbMSMessageStatus status);

	HashMap<Date,Number> selectMessageTraffic(Date from, Date to, TimeUnit timeUnit, EbMSMessageStatus...status);
	
	void writeMessageToZip(String messageId, int messageNr, ZipOutputStream stream);
	void printMessagesToCSV(CSVPrinter printer, EbMSMessageFilter filter);
}
