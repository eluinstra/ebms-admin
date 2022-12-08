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
package nl.clockwork.ebms.admin.web.configuration;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.Component;
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

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;
import nl.clockwork.ebms.admin.web.AjaxFormComponentUpdatingBehavior;
import nl.clockwork.ebms.admin.web.BootstrapFormComponentFeedbackBorder;
import nl.clockwork.ebms.admin.web.Supplier;
import nl.clockwork.ebms.admin.web.WebMarkupContainer;
import nl.clockwork.ebms.event.MessageEventListenerConfig.EventListenerType;

public class CorePropertiesFormPanel extends Panel
{
	private static final long serialVersionUID = 1L;

	public CorePropertiesFormPanel(String id, final IModel<CorePropertiesFormData> model, boolean enableConsoleProperties)
	{
		super(id,model);
		add(new CorePropertiesForm("form",model,enableConsoleProperties));
	}

	public class CorePropertiesForm extends Form<CorePropertiesFormData>
	{
		private static final long serialVersionUID = 1L;

		public CorePropertiesForm(String id, final IModel<CorePropertiesFormData> model, boolean enableConsoleProperties)
		{
			super(id,new CompoundPropertyModel<>(model));
			add(createContainer(
					"deleteMessageContentOnProcessedContainer",
					enableConsoleProperties,
					new CheckBox("deleteMessageContentOnProcessed")
							.setLabel(new ResourceModel("lbl.deleteMessageContentOnProcessed"))));
			add(createContainer(
					"storeDuplicateMessageContainer",
					enableConsoleProperties,
					new CheckBox("storeDuplicateMessage")
							.setLabel(new ResourceModel("lbl.storeDuplicateMessage"))));
			add(createContainer(
					"storeDuplicateMessageContentContainer",
					enableConsoleProperties,
					new CheckBox("storeDuplicateMessageContent")
						.setLabel(new ResourceModel("lbl.storeDuplicateMessageContent"))));
			add(createContainer(
					"eventListenerContainer",
					enableConsoleProperties,
					new BootstrapFormComponentFeedbackBorder("eventListenerFeedback",createEventListenerChoice("eventListener"))));
			add(createJmsContainer("jmsContainer",enableConsoleProperties));
		}

		private WebMarkupContainer createContainer(String id, boolean enableConsoleProperties, Component...components)
		{
			val result = new WebMarkupContainer(id);
			result.setVisible(!enableConsoleProperties);
			result.add(components);
			return result;
		}

		private WebMarkupContainer createJmsContainer(String id, final boolean enableConsoleProperties)
		{
			Supplier<Boolean> isVisible = () -> !enableConsoleProperties 
					&& (EventListenerType.SIMPLE_JMS.equals(getModelObject().eventListener)
							|| EventListenerType.JMS.equals(getModelObject().eventListener)
							|| EventListenerType.JMS_TEXT.equals(getModelObject().eventListener));
			val result = WebMarkupContainer.builder()
					.id(id)
					.isVisible(isVisible)
					.build();
			result.add(new BootstrapFormComponentFeedbackBorder(
					"jmsBrokerUrlFeedback",
					new TextField<String>("jmsBrokerUrl")
							.setLabel(new ResourceModel("lbl.jmsBrokerUrl"))));
			result.add(new CheckBox("jmsVirtualTopics").setLabel(new ResourceModel("lbl.jmsVirtualTopics")));
			val checkBox = new CheckBox("startEmbeddedBroker");
			checkBox.setLabel(new ResourceModel("lbl.startEmbeddedBroker"));
			checkBox.add(AjaxFormComponentUpdatingBehavior.builder()
					.event("change")
					.onUpdate(t -> t.add(this))
					.build());
			result.add(checkBox);
			result.add(createActiveMQConfigFileContainer("activeMQConfigFileContainer",enableConsoleProperties));
			return result;
		}

		private WebMarkupContainer createActiveMQConfigFileContainer(String id, final boolean enableConsoleProperties)
		{
			val result = WebMarkupContainer.builder()
					.id(id)
					.isVisible(() -> !enableConsoleProperties && getModelObject().startEmbeddedBroker)
					.build();
			result.add(new BootstrapFormComponentFeedbackBorder(
					"activeMQConfigFileFeedback",
					new TextField<String>("activeMQConfigFile")
							.setLabel(new ResourceModel("lbl.activeMQConfigFile"))));
			result.add(new DownloadActiveMQFileLink("downloadActiveMQFile"));
			return result;
		}

		private DropDownChoice<EventListenerType> createEventListenerChoice(String id)
		{
			val result = new DropDownChoice<EventListenerType>(id,new PropertyModel<>(getModel(),"eventListeners"));
			result.setLabel(new ResourceModel("lbl.eventListener"));
			result.setRequired(true);
			result.add(AjaxFormComponentUpdatingBehavior.builder()
					.event("change")
					.onUpdate(t -> t.add(this))
					.build());
			return result;
		}
	}

	@Data
	@FieldDefaults(level = AccessLevel.PRIVATE)
	@NoArgsConstructor
	public static class CorePropertiesFormData implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		@NonNull
		EventListenerType eventListener;
		String jmsBrokerUrl;
		boolean jmsVirtualTopics;
		boolean startEmbeddedBroker;
		String activeMQConfigFile;
		boolean deleteMessageContentOnProcessed;

		public List<EventListenerType> getEventListeners()
		{
			return Arrays.asList(EventListenerType.values());
		}
	}
}
