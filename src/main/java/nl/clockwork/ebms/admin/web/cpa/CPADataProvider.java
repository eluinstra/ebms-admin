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
package nl.clockwork.ebms.admin.web.cpa;


import java.util.Iterator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import nl.clockwork.ebms.admin.dao.EbMSDAO;
import nl.clockwork.ebms.admin.model.CPA;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(staticName = "of")
public class CPADataProvider implements IDataProvider<CPA>
{
	private static final long serialVersionUID = 1L;
	EbMSDAO ebMSDAO;

	@Override
	public Iterator<? extends CPA> iterator(long first, long count)
	{
		return ebMSDAO.selectCPAs(first, count).iterator();
	}

	@Override
	public IModel<CPA> model(CPA cpa)
	{
		return CPADataModel.of(ebMSDAO, cpa);
	}

	@Override
	public long size()
	{
		return (int)ebMSDAO.countCPAs();
	}

	@Override
	public void detach()
	{
	}
}
