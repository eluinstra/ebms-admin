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
import lombok.Builder;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WebMarkupContainer extends org.apache.wicket.markup.html.WebMarkupContainer
{
	private static final long serialVersionUID = 1L;
	Supplier<Boolean> isVisible;

	public WebMarkupContainer(String id)
	{
		this(id, null);
	}

	@Builder
	public WebMarkupContainer(String id, Supplier<Boolean> isVisible)
	{
		super(id);
		this.isVisible = isVisible == null ? () -> super.isVisible() : isVisible;
		setOutputMarkupId(true);
	}

	@Override
	public boolean isVisible()
	{
		return isVisible.get();
	}
}
