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
package nl.clockwork.ebms.admin.web.menu;

import org.apache.wicket.Page;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class MenuLinkItem extends MenuItem
{
	private static final long serialVersionUID = 1L;
	@NonNull
	Class<? extends Page> pageClass;
	
	public MenuLinkItem(String id, String name, Class<? extends Page> pageClass)
	{
		super(id,name);
		this.pageClass = pageClass;
	}

	public MenuLinkItem(MenuItem parent, String id, String name, Class<? extends Page> pageClass)
	{
		super(parent,id,name);
		this.pageClass = pageClass;
	}
}
