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
package nl.clockwork.ebms.admin.web.configuration;

import java.util.Arrays;
import java.util.List;

import nl.clockwork.ebms.admin.web.BootstrapFormComponentFeedbackBorder;
import nl.clockwork.ebms.admin.web.WebMarkupContainer;
import nl.clockwork.ebms.client.EbMSHttpClientFactory.EbMSHttpClientType;
import nl.clockwork.ebms.event.EventListenerFactory.EventListenerType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.io.IClusterable;

public class CorePropertiesFormPanel extends Panel
{
	private static final long serialVersionUID = 1L;
	protected transient Log logger = LogFactory.getLog(this.getClass());

	public CorePropertiesFormPanel(String id, final IModel<CorePropertiesFormModel> model, boolean enableConsoleProperties)
	{
		super(id,model);
		add(new CorePropertiesForm("form",model,enableConsoleProperties));
	}

	public class CorePropertiesForm extends Form<CorePropertiesFormModel>
	{
		private static final long serialVersionUID = 1L;

		public CorePropertiesForm(String id, final IModel<CorePropertiesFormModel> model, boolean enableConsoleProperties)
		{
			super(id,new CompoundPropertyModel<CorePropertiesFormModel>(model));
			add(createContainer("digipoortPatchContainer",enableConsoleProperties,new CheckBox("digipoortPatch").setLabel(new ResourceModel("lbl.digipoortPatch"))));
			add(createContainer("oraclePatchContainer",enableConsoleProperties,new CheckBox("oraclePatch").setLabel(new ResourceModel("lbl.oraclePatch"))));
			add(new CheckBox("cleoPatch").setLabel(new ResourceModel("lbl.cleoPatch")));
			add(createContainer("deleteMessageContentOnProcessedContainer",enableConsoleProperties,new CheckBox("deleteMessageContentOnProcessed").setLabel(new ResourceModel("lbl.deleteMessageContentOnProcessed"))));
			add(createContainer("storeDuplicateMessageContainer",enableConsoleProperties,new CheckBox("storeDuplicateMessage").setLabel(new ResourceModel("lbl.storeDuplicateMessage"))));
			add(createContainer("storeDuplicateMessageContentContainer",enableConsoleProperties,new CheckBox("storeDuplicateMessageContent").setLabel(new ResourceModel("lbl.storeDuplicateMessageContent"))));
			add(createContainer("httpClientContainer",enableConsoleProperties,new BootstrapFormComponentFeedbackBorder("httpClientFeedback",createHttpClientChoice("httpClient",model))));
			add(createContainer("eventListenerContainer",enableConsoleProperties,new BootstrapFormComponentFeedbackBorder("eventListenerFeedback",createEventListenerChoice("eventListener",model))));
			add(createJmsContainer("jmsContainer",enableConsoleProperties));
		}

		private WebMarkupContainer createContainer(String id, boolean enableConsoleProperties, Component...components)
		{
			WebMarkupContainer result = new WebMarkupContainer(id);
			result.setVisible(!enableConsoleProperties);
			result.add(components);
			return result;
		}

		private WebMarkupContainer createJmsContainer(String id, final boolean enableConsoleProperties)
		{
			WebMarkupContainer result = new WebMarkupContainer(id)
			{
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isVisible()
				{
					return !enableConsoleProperties && (EventListenerType.SIMPLE_JMS.equals(CorePropertiesForm.this.getModelObject().eventListener) || EventListenerType.JMS.equals(CorePropertiesForm.this.getModelObject().eventListener) || EventListenerType.JMS_TEXT.equals(CorePropertiesForm.this.getModelObject().eventListener));
				}
			};
			result.add(new BootstrapFormComponentFeedbackBorder("jmsBrokerUrlFeedback",new TextField<String>("jmsBrokerUrl").setLabel(new ResourceModel("lbl.jmsBrokerUrl"))));
			result.add(new CheckBox("jmsVirtualTopics").setLabel(new ResourceModel("lbl.jmsVirtualTopics")));
			CheckBox checkBox = new CheckBox("startEmbeddedBroker");
			checkBox.setLabel(new ResourceModel("lbl.startEmbeddedBroker"));
			checkBox.add(new AjaxFormComponentUpdatingBehavior("onchange")
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					target.add(CorePropertiesForm.this);
				}
			});
			result.add(checkBox);
			result.add(createActiveMQConfigFileContainer("activeMQConfigFileContainer",enableConsoleProperties));
			return result;
		}

		private WebMarkupContainer createActiveMQConfigFileContainer(String id, final boolean enableConsoleProperties)
		{
			WebMarkupContainer result = new WebMarkupContainer(id)
			{
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isVisible()
				{
					return !enableConsoleProperties && CorePropertiesForm.this.getModelObject().startEmbeddedBroker;
				}
			};
			result.add(new BootstrapFormComponentFeedbackBorder("activeMQConfigFileFeedback",new TextField<String>("activeMQConfigFile").setLabel(new ResourceModel("lbl.activeMQConfigFile"))));
			result.add(new DownloadActiveMQFileLink("downloadActiveMQFile"));
			return result;
		}

		private DropDownChoice<EbMSHttpClientType> createHttpClientChoice(String id, IModel<CorePropertiesFormModel> model)
		{
			DropDownChoice<EbMSHttpClientType> result = new DropDownChoice<EbMSHttpClientType>(id,new PropertyModel<List<EbMSHttpClientType>>(model.getObject(),"httpClients"));
			result.setLabel(new ResourceModel("lbl.httpClient"));
			result.setRequired(true);
			return result;
		}

		private DropDownChoice<EventListenerType> createEventListenerChoice(String id, IModel<CorePropertiesFormModel> model)
		{
			DropDownChoice<EventListenerType> result = new DropDownChoice<EventListenerType>(id,new PropertyModel<List<EventListenerType>>(model.getObject(),"eventListeners"));
			result.setLabel(new ResourceModel("lbl.eventListener"));
			result.setRequired(true);
			result.add(new AjaxFormComponentUpdatingBehavior("onchange")
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					target.add(CorePropertiesForm.this);
				}
			});
			return result;
		}
	}

	public static class CorePropertiesFormModel implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		private boolean digipoortPatch = true;
		private boolean oraclePatch = true;
		private boolean cleoPatch = false;
		private EbMSHttpClientType httpClient = EbMSHttpClientType.DEFAULT;
		private EventListenerType eventListener = EventListenerType.DEFAULT;
		private String jmsBrokerUrl = "vm://localhost";
		private boolean jmsVirtualTopics = false;
		private boolean startEmbeddedBroker = true;
		private String activeMQConfigFile = "classpath:nl/clockwork/ebms/activemq.xml";
		private boolean deleteMessageContentOnProcessed = false;
		private boolean storeDuplicateMessage = true;
		private boolean storeDuplicateMessageContent = true;

		public boolean isDigipoortPatch()
		{
			return digipoortPatch;
		}
		public void setDigipoortPatch(boolean digipoortPatch)
		{
			this.digipoortPatch = digipoortPatch;
		}
		public boolean isOraclePatch()
		{
			return oraclePatch;
		}
		public void setOraclePatch(boolean oraclePatch)
		{
			this.oraclePatch = oraclePatch;
		}
		public boolean isCleoPatch()
		{
			return cleoPatch;
		}
		public void setCleoPatch(boolean cleoPatch)
		{
			this.cleoPatch = cleoPatch;
		}
		public List<EbMSHttpClientType> getHttpClients()
		{
			return Arrays.asList(EbMSHttpClientType.values());
		}
		public EbMSHttpClientType getHttpClient()
		{
			return httpClient;
		}
		public void setHttpClient(EbMSHttpClientType httpClient)
		{
			this.httpClient = httpClient;
		}
		public List<EventListenerType> getEventListeners()
		{
			return Arrays.asList(EventListenerType.values());
		}
		public EventListenerType getEventListener()
		{
			return eventListener;
		}
		public void setEventListener(EventListenerType eventListener)
		{
			this.eventListener = eventListener;
		}
		public void setJmsBrokerUrl(String jmsBrokerUrl)
		{
			this.jmsBrokerUrl = jmsBrokerUrl;
		}
		public String getJmsBrokerUrl()
		{
			return jmsBrokerUrl;
		}
		public boolean isJmsVirtualTopics()
		{
			return jmsVirtualTopics;
		}
		public void setJmsVirtualTopics(boolean jmsVirtualTopics)
		{
			this.jmsVirtualTopics = jmsVirtualTopics;
		}
		public void setStartEmbeddedBroker(boolean startEmbeddedBroker)
		{
			this.startEmbeddedBroker = startEmbeddedBroker;
		}
		public boolean isStartEmbeddedBroker()
		{
			return startEmbeddedBroker;
		}
		public String getActiveMQConfigFile()
		{
			return activeMQConfigFile;
		}
		public void setActiveMQConfigFile(String activeMQConfigFile)
		{
			this.activeMQConfigFile = activeMQConfigFile;
		}
		public boolean isDeleteMessageContentOnProcessed()
		{
			return deleteMessageContentOnProcessed;
		}
		public void setDeleteMessageContentOnProcessed(boolean deleteMessageContentOnProcessed)
		{
			this.deleteMessageContentOnProcessed = deleteMessageContentOnProcessed;
		}
		public boolean isStoreDuplicateMessage()
		{
			return storeDuplicateMessage;
		}
		public void setStoreDuplicateMessage(boolean storeDuplicateMessage)
		{
			this.storeDuplicateMessage = storeDuplicateMessage;
		}
		public boolean isStoreDuplicateMessageContent()
		{
			return storeDuplicateMessageContent;
		}
		public void setStoreDuplicateMessageContent(boolean storeDuplicateMessageContent)
		{
			this.storeDuplicateMessageContent = storeDuplicateMessageContent;
		}
	}
}
