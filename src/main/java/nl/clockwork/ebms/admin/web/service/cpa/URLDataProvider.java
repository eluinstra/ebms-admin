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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nl.clockwork.ebms.admin.Utils;
import nl.clockwork.ebms.service.CPAService;

import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class URLDataProvider implements IDataProvider<String>
{
	private static final long serialVersionUID = 1L;
	private CPAService cpaService;

	public URLDataProvider(CPAService cpaService)
	{
		this.cpaService = cpaService;
	}
	
	@Override
	public Iterator<? extends String> iterator(long first, long count)
	{
		List<String> urls = Utils.toList(cpaService.getURLs());
		return urls == null ? new ArrayList<String>().iterator() : urls.iterator();
	}

	@Override
	public IModel<String> model(final String url)
	{
		return Model.of(url);
	}

	@Override
	public long size()
	{
		List<String> urls = Utils.toList(cpaService.getURLs());
		return urls == null ? 0 : urls.size();
	}

	@Override
	public void detach()
	{
	}

}