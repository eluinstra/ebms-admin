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

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.Link;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PageClassLink extends Link<Void>
{
	private static final long serialVersionUID = 1L;
	@NonNull
	Class<? extends Page> page;

	public PageClassLink(String id, Class<? extends Page> page)
	{
		super(id);
		this.page = page;
	}

	@Override
	public void onClick()
	{
		setResponsePage(page);
	}
}
