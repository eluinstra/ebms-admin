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
package nl.clockwork.ebms.admin.web.cpa;

import nl.clockwork.ebms.admin.dao.EbMSDAO;
import nl.clockwork.ebms.admin.model.CPA;

import org.apache.wicket.model.LoadableDetachableModel;

public class CPADataModel extends LoadableDetachableModel<CPA>
{
	private static final long serialVersionUID = 1L;
	private final EbMSDAO ebMSDAO;
	private final String cpaId;

	public CPADataModel(EbMSDAO ebMSDAO, CPA cpa)
	{
		this(ebMSDAO,cpa.getCpaId());
	}
	public CPADataModel(EbMSDAO ebMSDAO, String cpaId)
	{
		if (cpaId == null || "".equals(cpaId))
			throw new IllegalArgumentException("cpaId is empty!");
		this.ebMSDAO = ebMSDAO;
		this.cpaId = cpaId;
	}

	@Override
	protected CPA load()
	{
		return ebMSDAO.findCPA(cpaId);
	}

	@Override
	public int hashCode()
	{
		return cpaId.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
			return true;
		else if (obj == null)
			return false;
		else if (obj instanceof CPADataModel)
		{
			CPADataModel other = (CPADataModel)obj;
			return cpaId.equals(other.cpaId);
		}
		return false;
	}
}
