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

import lombok.Builder;
import lombok.NonNull;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

public class EbMSAjaxButton extends org.apache.wicket.ajax.markup.html.form.AjaxButton
{
	private static final long serialVersionUID = 1L;
	@NonNull
	SerializableConsumer<AjaxRequestTarget> onSubmit;
	SerializableConsumer<AjaxRequestTarget> onError;

	@Builder
	public EbMSAjaxButton(
			String id,
			IModel<String> model,
			Form<?> form,
			@NonNull SerializableConsumer<AjaxRequestTarget> onSubmit,
			SerializableConsumer<AjaxRequestTarget> onError)
	{
		super(id, model, form);
		this.onSubmit = onSubmit;
		this.onError = onError == null ? t ->
		{
		} : onError;
	}

	@Override
	protected void onSubmit(AjaxRequestTarget target)
	{
		onSubmit.accept(target);
	}

	@Override
	protected void onError(AjaxRequestTarget target)
	{
		super.onError(target);
		onError.accept(target);
	}
}
