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
	private final long id;

	public MessageDataModel(EbMSDAO ebMSDAO, EbMSMessage message)
	{
		this(ebMSDAO,message.getId());
	}

	public MessageDataModel(EbMSDAO ebMSDAO, long id)
	{
		this.ebMSDAO = ebMSDAO;
		this.id = id;
	}

	protected EbMSDAO getEbMSDAO()
	{
		return ebMSDAO;
	}

	@Override
	protected EbMSMessage load()
	{
		return getEbMSDAO().findMessage(id);
	}

	@Override
	public int hashCode()
	{
    return new Long(id).intValue();
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
			return id == other.id;
		}
		return false;
	}
}
