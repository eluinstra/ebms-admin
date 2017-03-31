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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EmptyDataSourcesPanel extends DataSourcesPanel
{
	private static final long serialVersionUID = 1L;
	protected transient Log logger = LogFactory.getLog(this.getClass());

	public EmptyDataSourcesPanel(String id)
	{
		super(id);
	}

	@Override
	public List<EbMSDataSource> getDataSources()
	{
		return new ArrayList<EbMSDataSource>();
	}

}