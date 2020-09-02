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

import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.val;
import lombok.experimental.FieldDefaults;
import nl.clockwork.ebms.admin.Utils;
import nl.clockwork.ebms.service.EbMSMessageService;
import nl.clockwork.ebms.service.model.MessageFilter;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(staticName = "of")
public class MessageDataProvider implements IDataProvider<String>
{
	private static final long serialVersionUID = 1L;
	EbMSMessageService ebMSMessageService;
	MessageFilter filter;

	@Override
	public Iterator<? extends String> iterator(long first, long count)
	{
		val messageIds = Utils.toList(ebMSMessageService.getUnprocessedMessageIds(filter,(int)(first+count)));
		return messageIds == null ? new ArrayList<String>().iterator() : messageIds.listIterator((int)first);
	}

	@Override
	public IModel<String> model(String messageId)
	{
		return Model.of(messageId);
	}

	@Override
	public long size()
	{
		val messageIds = Utils.toList(ebMSMessageService.getUnprocessedMessageIds(filter,null));
		return messageIds == null ? 0 : messageIds.size();
	}

	@Override
	public void detach()
	{
	}
}
