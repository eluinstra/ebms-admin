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
package nl.clockwork.ebms.admin.web.service.message;


import java.util.ArrayList;
import java.util.Iterator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.val;
import nl.clockwork.ebms.admin.Utils;
import nl.clockwork.ebms.event.MessageEventType;
import nl.clockwork.ebms.service.EbMSMessageService;
import nl.clockwork.ebms.service.model.MessageEvent;
import nl.clockwork.ebms.service.model.MessageFilter;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(staticName = "of")
public class MessageEventDataProvider implements IDataProvider<MessageEvent>
{
	private static final long serialVersionUID = 1L;
	@NonNull
	private EbMSMessageService ebMSMessageService;
	@NonNull
	private MessageFilter filter;
	@NonNull
	private MessageEventType[] eventTypes;

	@Override
	public Iterator<? extends MessageEvent> iterator(long first, long count)
	{
		val messageEvents = Utils.toList(ebMSMessageService.getUnprocessedMessageEvents(filter,eventTypes,(int)(first + count)));
		return messageEvents == null ? new ArrayList<MessageEvent>().iterator() : messageEvents.listIterator((int)first);
	}

	@Override
	public IModel<MessageEvent> model(MessageEvent messageEvent)
	{
		return Model.of(messageEvent);
	}

	@Override
	public long size()
	{
		val messageEvents = Utils.toList(ebMSMessageService.getUnprocessedMessageEvents(filter,eventTypes,null));
		return messageEvents == null ? 0 : messageEvents.size();
	}

	@Override
	public void detach()
	{
	}
}
