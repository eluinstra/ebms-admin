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
package nl.clockwork.ebms.admin.web;


import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.navigation.paging.IPagingLabelProvider;

public class BootstrapPagingNavigator extends AjaxPagingNavigator
{
	private static final long serialVersionUID = 1L;

	public BootstrapPagingNavigator(final String id, final IPageable pageable)
	{
		this(id, pageable, null);
	}

	public BootstrapPagingNavigator(final String id, final IPageable pageable, final IPagingLabelProvider labelProvider)
	{
		super(id, pageable, labelProvider);
	}

}
