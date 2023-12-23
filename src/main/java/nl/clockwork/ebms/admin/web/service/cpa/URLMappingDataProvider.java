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
package nl.clockwork.ebms.admin.web.service.cpa;

import java.util.ArrayList;
import java.util.Iterator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import nl.clockwork.ebms.admin.Utils;
import nl.clockwork.ebms.cpa.url.URLMapping;
import nl.clockwork.ebms.cpa.url.URLMappingService;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(staticName = "of")
public class URLMappingDataProvider implements IDataProvider<URLMapping>
{
	private static final long serialVersionUID = 1L;
	URLMappingService urlMappingService;

	@Override
	public Iterator<? extends URLMapping> iterator(long first, long count)
	{
		val urlMappings = Utils.toList(urlMappingService.getURLMappings());
		return urlMappings == null ? new ArrayList<URLMapping>().iterator() : urlMappings.iterator();
	}

	@Override
	public IModel<URLMapping> model(URLMapping urlMapping)
	{
		return Model.of(urlMapping);
	}

	@Override
	public long size()
	{
		val urlMappings = Utils.toList(urlMappingService.getURLMappings());
		return urlMappings == null ? 0 : urlMappings.size();
	}

	@Override
	public void detach()
	{
	}
}
