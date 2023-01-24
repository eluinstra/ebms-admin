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
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.apache.wicket.model.IModel;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Link<T> extends org.apache.wicket.markup.html.link.Link<T>
{
	private static final long serialVersionUID = 1L;
	@NonNull
	Action onClick;

	public Link(String id, @NonNull Action onClick)
	{
		super(id);
		this.onClick = onClick;
	}

	@Builder
	public Link(String id, IModel<T> model, @NonNull Action onClick)
	{
		super(id, model);
		this.onClick = onClick;
	}

	@Override
	public void onClick()
	{
		onClick.run();
	}
}
