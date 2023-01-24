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
package nl.clockwork.ebms.admin.web.message;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import nl.clockwork.ebms.admin.dao.EbMSDAO;
import nl.clockwork.ebms.admin.model.EbMSMessage;
import org.apache.wicket.model.LoadableDetachableModel;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = false)
public class MessageDataModel extends LoadableDetachableModel<EbMSMessage>
{
	private static final long serialVersionUID = 1L;
	@NonNull
	transient EbMSDAO ebMSDAO;
	@NonNull
	String messageId;
	int messageNr;

	public static MessageDataModel of(EbMSDAO ebMSDAO, EbMSMessage message)
	{
		return new MessageDataModel(ebMSDAO, message.getMessageId(), message.getMessageNr());
	}

	@Override
	protected EbMSMessage load()
	{
		return ebMSDAO.findMessage(messageId, messageNr);
	}
}
