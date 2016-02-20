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

import java.util.ArrayList;
import java.util.List;

import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.BootstrapDateTimePicker;
import nl.clockwork.ebms.admin.web.BootstrapFeedbackPanel;
import nl.clockwork.ebms.admin.web.PageClassLink;
import nl.clockwork.ebms.service.CPAService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

public abstract class MessageFilterPanel extends Panel
{
	private static final long serialVersionUID = 1L;
	protected transient Log logger = LogFactory.getLog(getClass());
	@SpringBean(name="cpaService")
	private CPAService cpaService;
	private BootstrapDateTimePicker from;
	private BootstrapDateTimePicker to;

	public MessageFilterPanel(String id, MessageFilterFormModel filter)
	{
		super(id);
		add(new BootstrapFeedbackPanel("feedback").setOutputMarkupId(true));
		MessageFilterForm messageFilterForm = new MessageFilterForm("form",filter,cpaService,this);
		from  = messageFilterForm.getFrom();
		to = messageFilterForm.getTo();
		add(messageFilterForm);
		add(new PageClassLink("clear",getPage().getClass()));
	}
	
	@Override
	public void renderHead(IHeaderResponse response)
	{
		response.render(OnDomReadyHeaderItem.forScript(BootstrapDateTimePicker.getLinkBootstrapDateTimePickersJavaScript(from,to)));
		super.renderHead(response);
	}

	
	public abstract BasePage getPage(MessageFilterFormModel filter);
	
	public static MessageFilterFormModel createMessageFilter()
	{
		return new MessageFilterFormModel();
	}

	public static class MessageFilterFormModel extends EbMSMessageFilter
	{
		private static final long serialVersionUID = 1L;
		private List<String> fromPartyIds = new ArrayList<String>();
		private List<String> fromRoles = new ArrayList<String>();
		private List<String> toPartyIds = new ArrayList<String>();
		private List<String> toRoles = new ArrayList<String>();
		private List<String> services = new ArrayList<String>();
		private List<String> actions = new ArrayList<String>();
		
		public List<String> getFromPartyIds()
		{
			return fromPartyIds;
		}
		public void resetFromPartyIds()
		{
			getFromPartyIds().clear();
			setFromRole(null);
		}
		public void resetFromPartyIds(List<String> partyIds)
		{
			resetFromPartyIds();
			getFromPartyIds().addAll(partyIds);
		}
		public List<String> getFromRoles()
		{
			return fromRoles;
		}
		public void resetFromRoles()
		{
			getFromRoles().clear();
			if (getFromRole() != null)
				getFromRole().setRole(null);
		}
		public void resetFromRoles(ArrayList<String> roleNames)
		{
			resetFromRoles();
			getFromRoles().addAll(roleNames);
		}
		public List<String> getToPartyIds()
		{
			return toPartyIds;
		}
		public void resetToPartyIds()
		{
			getToPartyIds().clear();
			setToRole(null);
		}
		public void resetToPartyIds(List<String> partyIds)
		{
			resetToPartyIds();
			getToPartyIds().addAll(partyIds);
		}
		public List<String> getToRoles()
		{
			return toRoles;
		}
		public void resetToRoles()
		{
			getToRoles().clear();
			if (getToRole() != null)
				getToRole().setRole(null);
		}
		public void resetToRoles(ArrayList<String> roleNames)
		{
			resetToRoles();
			getToRoles().addAll(roleNames);
		}
		public List<String> getServices()
		{
			return services;
		}
		public void resetServices()
		{
			getServices().clear();
			setService(null);
		}
		public void resetServices(List<String> serviceNames)
		{
			resetServices();
			getServices().addAll(serviceNames);
		}
		public List<String> getActions()
		{
			return actions;
		}
		public void resetActions()
		{
			getActions().clear();
			setAction(null);
		}
		public void resetActions(List<String> actionNames)
		{
			resetActions();
			getActions().addAll(actionNames);
		}
	}
}
