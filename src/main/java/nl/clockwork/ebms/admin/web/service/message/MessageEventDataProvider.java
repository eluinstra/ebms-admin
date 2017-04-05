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
package nl.clockwork.ebms.admin.web.service.message;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nl.clockwork.ebms.Constants.EbMSMessageEventType;
import nl.clockwork.ebms.admin.Utils;
import nl.clockwork.ebms.model.EbMSMessageContext;
import nl.clockwork.ebms.model.EbMSMessageEvent;
import nl.clockwork.ebms.service.EbMSMessageService;

import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class MessageEventDataProvider implements IDataProvider<EbMSMessageEvent>
{
	private static final long serialVersionUID = 1L;
	private EbMSMessageService ebMSMessageService;
	private EbMSMessageContext filter;
	private EbMSMessageEventType[] eventTypes;

	public MessageEventDataProvider(EbMSMessageService ebMSMessageService, EbMSMessageContext filter, EbMSMessageEventType...eventTypes)
	{
		this.ebMSMessageService = ebMSMessageService;
		this.filter = filter;
		this.eventTypes = eventTypes;
	}
	
	@Override
	public Iterator<? extends EbMSMessageEvent> iterator(long first, long count)
	{
		List<EbMSMessageEvent> messageEvents = Utils.toList(ebMSMessageService.getMessageEvents(filter,eventTypes,(int)(first+count)));
		return messageEvents == null ? new ArrayList<EbMSMessageEvent>().iterator() : messageEvents.listIterator((int)first);
	}

	@Override
	public IModel<EbMSMessageEvent> model(EbMSMessageEvent messageEvent)
	{
		return Model.of(messageEvent);
	}

	@Override
	public long size()
	{
		List<EbMSMessageEvent> messageEvents = Utils.toList(ebMSMessageService.getMessageEvents(filter,eventTypes,null));
		return messageEvents == null ? 0 : messageEvents.size();
	}

	@Override
	public void detach()
	{
	}

}
