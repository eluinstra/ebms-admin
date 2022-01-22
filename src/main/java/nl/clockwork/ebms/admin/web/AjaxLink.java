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

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AjaxLink<T> extends org.apache.wicket.ajax.markup.html.AjaxLink<T>
{
	private static final long serialVersionUID = 1L;
	@NonNull
	Consumer<AjaxRequestTarget> onClick;

	public AjaxLink(String id, @NonNull Consumer<AjaxRequestTarget> onClick)
	{
		super(id);
		this.onClick = onClick;
	}

	@Builder
	public AjaxLink(String id, IModel<T> model, @NonNull Consumer<AjaxRequestTarget> onClick)
	{
		super(id,model);
		this.onClick = onClick;
	}

	@Override
	public MarkupContainer setDefaultModel(IModel<?> model)
	{
		return super.setDefaultModel(model);
	}

	@Override
	public void onClick(AjaxRequestTarget target)
	{
		onClick.accept(target);
	}
}
