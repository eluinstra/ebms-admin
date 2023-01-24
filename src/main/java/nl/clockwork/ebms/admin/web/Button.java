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
import org.apache.wicket.model.IModel;

public class Button extends org.apache.wicket.markup.html.form.Button
{
	private static final long serialVersionUID = 1L;
	Action onSubmit;

	public Button(String id)
	{
		this(id,null,null);
	}

	@Builder
	public Button(String id, IModel<String> model, Action onSubmit)
	{
		super(id,model);
		this.onSubmit = onSubmit == null ? () -> super.onSubmit() : onSubmit;
	}

	@Override
	public void onSubmit()
	{
		onSubmit.run();
	}
}
