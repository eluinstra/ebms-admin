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
