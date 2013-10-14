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
package nl.clockwork.ebms.admin;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.oasis_open.committees.ebxml_cppa.schema.cpp_cpa_2_0.CanSend;
import org.oasis_open.committees.ebxml_cppa.schema.cpp_cpa_2_0.CollaborationProtocolAgreement;
import org.oasis_open.committees.ebxml_cppa.schema.cpp_cpa_2_0.CollaborationRole;
import org.oasis_open.committees.ebxml_cppa.schema.cpp_cpa_2_0.PartyId;
import org.oasis_open.committees.ebxml_cppa.schema.cpp_cpa_2_0.PartyInfo;
import org.oasis_open.committees.ebxml_cppa.schema.cpp_cpa_2_0.ServiceType;

public class CPAUtils
{

	public static ArrayList<String> getPartyNames(CollaborationProtocolAgreement cpa)
	{
		ArrayList<String> result = new ArrayList<String>();
		for (PartyInfo partyInfo : cpa.getPartyInfo())
			result.add(partyInfo.getPartyName());
		return result;
	}
	
	public static String getOtherPartyName(CollaborationProtocolAgreement cpa, String partyName)
	{
		for (PartyInfo partyInfo : cpa.getPartyInfo())
			if (!partyName.equals(partyInfo.getPartyName()))
				return partyInfo.getPartyName();
		return null;
	}
	
	public static String getPartyIdbyPartyName(CollaborationProtocolAgreement cpa, String partyName)
	{
		for (PartyInfo partyInfo : cpa.getPartyInfo())
			if (partyName.equals(partyInfo.getPartyName()))
				return getPartyId(partyInfo.getPartyId().get(0));
		return null;
	}

	public static String getPartyId(PartyId partyId)
	{
		return (partyId.getType() == null ? "" : partyId.getType() + ":" ) + partyId.getValue();
	}

	public static ArrayList<String> getRoleNames(CollaborationProtocolAgreement cpa)
	{
		LinkedHashSet<String> result = new LinkedHashSet<String>();
		for (PartyInfo partyInfo : cpa.getPartyInfo())
			for (CollaborationRole role : partyInfo.getCollaborationRole())
				result.add(role.getRole().getName());
		return new ArrayList<String>(result);
	}

	public static List<String> getServiceNames(CollaborationProtocolAgreement cpa, String roleName)
	{
		List<String> result = new ArrayList<String>();
		List<CollaborationRole> roles = findRoles(cpa,roleName);
		for (CollaborationRole role : roles)
			result.add(getServiceName(role.getServiceBinding().getService()));
		return result;
	}

	private static List<CollaborationRole> findRoles(CollaborationProtocolAgreement cpa, String roleName)
	{
		ArrayList<CollaborationRole> result = new ArrayList<CollaborationRole>();
		for (PartyInfo partyInfo : cpa.getPartyInfo())
			for (CollaborationRole role : partyInfo.getCollaborationRole())
				if (role.getRole().getName().equals(roleName))
					result.add(role);
		return result;
	}
	
	public static String getServiceName(ServiceType service)
	{
		return (service.getType() == null ? "" : service.getType() + ":") + service.getValue();
	}
	
	public static boolean equals(ServiceType service, String serviceName)
	{
		return getServiceName(service).equals(serviceName);
	}

	public static List<String> getActionNames(CollaborationProtocolAgreement cpa, String roleName, String serviceName)
	{
		List<String> result = new ArrayList<String>();
		List<CollaborationRole> roles = findRoles(cpa,roleName,serviceName);
		for (CollaborationRole role : roles)
			for (CanSend canSend : role.getServiceBinding().getCanSend())
				result.add(canSend.getThisPartyActionBinding().getAction());
		return result;
	}
	
	private static List<CollaborationRole> findRoles(CollaborationProtocolAgreement cpa, String roleName, String serviceName)
	{
		List<CollaborationRole> result = new ArrayList<CollaborationRole>();
		List<CollaborationRole> roles = findRoles(cpa,roleName);
		for (CollaborationRole role : roles)
			if (getServiceName(role.getServiceBinding().getService()).equals(serviceName))
				result.add(role);
		return result;
	}

}
