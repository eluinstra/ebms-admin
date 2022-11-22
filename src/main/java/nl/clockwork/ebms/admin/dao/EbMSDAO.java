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
package nl.clockwork.ebms.admin.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import org.apache.commons.csv.CSVPrinter;

import nl.clockwork.ebms.EbMSMessageStatus;
import nl.clockwork.ebms.admin.model.CPA;
import nl.clockwork.ebms.admin.model.EbMSAttachment;
import nl.clockwork.ebms.admin.model.EbMSMessage;
import nl.clockwork.ebms.admin.web.message.EbMSMessageFilter;
import nl.clockwork.ebms.admin.web.message.TimeUnit;

public interface EbMSDAO
{
	CPA findCPA(String cpaId);
	long countCPAs();
	List<String> selectCPAIds();
	List<CPA> selectCPAs(long first, long count);

	EbMSMessage findMessage(String messageId);
	EbMSMessage findMessage(String messageId, int messageNr);
	boolean existsResponseMessage(String messageId);
	EbMSMessage findResponseMessage(String messageId);
	long countMessages(EbMSMessageFilter filter);
	List<EbMSMessage> selectMessages(EbMSMessageFilter filter, long first, long count);

	EbMSAttachment findAttachment(String messageId, int messageNr, String contentId);
	
	List<String> selectMessageIds(String cpaId, String fromRole, String toRole, EbMSMessageStatus...status);

	Map<Integer,Integer> selectMessageTraffic(LocalDateTime from, LocalDateTime to, TimeUnit timeUnit, EbMSMessageStatus...status);
	
	void writeMessageToZip(String messageId, int messageNr, ZipOutputStream stream);
	void printMessagesToCSV(CSVPrinter printer, EbMSMessageFilter filter);
}
