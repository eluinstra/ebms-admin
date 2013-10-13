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

import org.oasis_open.committees.ebxml_cppa.schema.cpp_cpa_2_0.CollaborationProtocolAgreement;
import org.oasis_open.committees.ebxml_cppa.schema.cpp_cpa_2_0.PartyId;
import org.oasis_open.committees.ebxml_cppa.schema.cpp_cpa_2_0.PartyInfo;

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
		{
			if (!partyName.equals(partyInfo.getPartyName()))
				return partyInfo.getPartyName();
		}
		return null;
	}
	
	public static String getPartyIdbyPartyName(CollaborationProtocolAgreement cpa, String partyName)
	{
		for (PartyInfo partyInfo : cpa.getPartyInfo())
			if (partyName.equals(partyInfo.getPartyName()))
			{
				PartyId partyId = partyInfo.getPartyId().get(0);
				return (partyId.getType() == null ? "" : partyId.getType() + ":" ) + partyId.getValue();
			}
		return null;
	}

}
