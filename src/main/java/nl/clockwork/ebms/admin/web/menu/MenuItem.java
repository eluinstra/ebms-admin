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
package nl.clockwork.ebms.admin.web.menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.wicket.util.io.IClusterable;

public class MenuItem implements IClusterable
{
	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private MenuItem parent;
	private List<MenuItem> children = new ArrayList<MenuItem>();

	public MenuItem(String id, String name)
	{
		this.id = id;
		this.name = name;
	}
	
	public MenuItem(MenuItem parent, String id, String name)
	{
		this.id = parent.getId() + "." + id;
		this.name = name;
		this.parent = parent;
		this.parent.children.add(this);
	}
	
	public String getId()
	{
		return id;
	}
	
	public String getName()
	{
		return name;
	}
	
	public MenuItem getParent()
	{
		return parent;
	}
	
	public List<MenuItem> getChildren()
	{
		return Collections.unmodifiableList(children);
	}
	
	public void addChild(MenuItem child)
	{
		child.id = this.id + "." + children.size();
		child.parent = this;
		children.add(child);
	}

	@Override
	public String toString()
	{
		return id;
	}

}
