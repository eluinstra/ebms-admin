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
package nl.clockwork.ebms.admin.web.service.cpa;

import nl.clockwork.ebms.service.CPAService;

import org.apache.wicket.model.LoadableDetachableModel;

public class CPADataModel extends LoadableDetachableModel<String>
{
	private static final long serialVersionUID = 1L;
	private CPAService cpaClient;
	private final String cpaId;

	public CPADataModel(CPAService cpaClient, String cpaId)
	{
		if (cpaId == null || "".equals(cpaId))
			throw new IllegalArgumentException("cpaId is empty!");
		this.cpaClient = cpaClient;
		this.cpaId = cpaId;
	}

	protected CPAService getCpaClient()
	{
		return cpaClient;
	}

	@Override
	protected String load()
	{
		return getCpaClient().getCPA(cpaId);
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
