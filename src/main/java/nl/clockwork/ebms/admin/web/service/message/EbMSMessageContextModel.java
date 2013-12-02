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
import java.util.List;

import nl.clockwork.ebms.model.EbMSDataSource;
import nl.clockwork.ebms.model.EbMSMessageContext;

public class EbMSMessageContextModel extends EbMSMessageContext
{
	private static final long serialVersionUID = 1L;
	//private List<String> fromParties = new ArrayList<String>();
	private List<String> fromRoles = new ArrayList<String>();
	private List<String> services = new ArrayList<String>();
	private List<String> actions = new ArrayList<String>();
	private List<EbMSDataSource> dataSources = new ArrayList<EbMSDataSource>();

	public List<String> getFromRoles()
	{
		return fromRoles;
	}
	public void resetFromRoles()
	{
		getFromRoles().clear();
		setFromRole(null);
	}
	public void resetFromRoles(List<String> roles)
	{
		resetFromRoles();
		getFromRoles().addAll(roles);
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
	public List<EbMSDataSource> getDataSources()
	{
		return dataSources;
	}
	public void resetDataSources()
	{
		getDataSources().clear();
	}
}
