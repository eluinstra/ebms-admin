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
package nl.clockwork.ebms.admin.web;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import nl.clockwork.ebms.admin.model.EbMSAttachment;
import nl.clockwork.ebms.admin.model.EbMSMessage;
import nl.clockwork.ebms.admin.web.service.message.DataSourcesPanel;

import org.apache.wicket.markup.html.panel.Panel;

public abstract class MessageProvider
{
	public static List<MessageProvider> get()
	{
		ServiceLoader<MessageProvider> providers = ServiceLoader.load(MessageProvider.class);
		List<MessageProvider> result = new ArrayList<>();
		for (MessageProvider provider : providers)
			result.add(provider);
		return result;
	}

	public static Object createId(EbMSMessage message)
	{
		return createId(message.getService(),message.getAction());
	}
	public static String createId(String service, String action)
	{
		return service + ":" + action;
	}

	public abstract List<MessageViewPanel> getMessageViewPanels();

	public abstract List<MessageEditPanel> getMessageEditPanels();

	public static class MessagePanel
	{
		private String service;
		private String action;

		public MessagePanel()
		{
		}
		public MessagePanel(String service, String action)
		{
			this.service = service;
			this.action = action;
		}
		public String getId()
		{
			return createId(service,action);
		}
		public String getService()
		{
			return service;
		}
		public void setService(String service)
		{
			this.service = service;
		}
		public String getAction()
		{
			return action;
		}
		public void setAction(String action)
		{
			this.action = action;
		}
	}

	public static abstract class MessageViewPanel extends MessagePanel 
	{
		public MessageViewPanel(String service, String action)
		{
			super(service,action);
		}

		public abstract Panel getPanel(String id, List<EbMSAttachment> attachments) throws Exception;
	}

	public static abstract class MessageEditPanel extends MessagePanel 
	{
		public MessageEditPanel(String service, String action)
		{
			super(service,action);
		}

		public abstract DataSourcesPanel getPanel(String id);
	}
}
