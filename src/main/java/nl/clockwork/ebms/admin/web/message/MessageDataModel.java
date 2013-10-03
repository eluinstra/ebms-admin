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
package nl.clockwork.ebms.admin.web.message;

import nl.clockwork.ebms.admin.dao.EbMSDAO;
import nl.clockwork.ebms.admin.model.EbMSMessage;

import org.apache.wicket.model.LoadableDetachableModel;

public class MessageDataModel extends LoadableDetachableModel<EbMSMessage>
{
	private static final long serialVersionUID = 1L;
	private EbMSDAO ebMSDAO;
	private final String messageId;
	private final int messageNr;

	public MessageDataModel(EbMSDAO ebMSDAO, EbMSMessage message)
	{
		this(ebMSDAO,message.getMessageId(),message.getMessageNr());
	}
	public MessageDataModel(EbMSDAO ebMSDAO, String messageId, int messageNr)
	{
		if (messageId == null)
			throw new IllegalArgumentException("messageId is null!");
		this.ebMSDAO = ebMSDAO;
		this.messageId = messageId;
		this.messageNr = messageNr;
	}

	protected EbMSDAO getEbMSDAO()
	{
		return ebMSDAO;
	}

	@Override
	protected EbMSMessage load()
	{
		return getEbMSDAO().getMessage(messageId,messageNr);
	}

	@Override
	public int hashCode()
	{
    int hash = 1;
    hash = hash * 13 + (messageId == null ? 0 : messageId.hashCode());
    hash = hash * 17 + Integer.valueOf(messageNr).hashCode();
    return hash;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
			return true;
		else if (obj == null)
			return false;
		else if (obj instanceof MessageDataModel)
		{
			MessageDataModel other = (MessageDataModel)obj;
			return messageId.equals(other.messageId) && messageNr == other.messageNr;
		}
		return false;
	}
}
