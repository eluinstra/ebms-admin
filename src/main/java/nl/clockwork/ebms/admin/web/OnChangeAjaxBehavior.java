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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.experimental.FieldDefaults;
import org.apache.wicket.ajax.AjaxRequestTarget;

@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(staticName = "of")
public class OnChangeAjaxBehavior extends org.apache.wicket.ajax.form.OnChangeAjaxBehavior
{
	private static final long serialVersionUID = 1L;
	Consumer<AjaxRequestTarget> onUpdate;

	@Override
	protected void onUpdate(AjaxRequestTarget target)
	{
		onUpdate.accept(target);
	}
}
