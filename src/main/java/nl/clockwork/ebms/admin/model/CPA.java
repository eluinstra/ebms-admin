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
package nl.clockwork.ebms.admin.model;

import org.apache.wicket.util.io.IClusterable;

public class CPA implements IClusterable
{
	private static final long serialVersionUID = 1L;
	private String cpaId;
	private String cpa;
	
	public CPA()
	{
	}
	
	public CPA(String id, String cpa)
	{
		this.cpaId = id;
		this.cpa = cpa;
	}

	public String getCpaId()
	{
		return cpaId;
	}
	
	public void setCpaId(String cpaId)
	{
		this.cpaId = cpaId;
	}
	
	public String getCpa()
	{
		return cpa;
	}
	
	public void setCpa(String cpa)
	{
		this.cpa = cpa;
	}
}
