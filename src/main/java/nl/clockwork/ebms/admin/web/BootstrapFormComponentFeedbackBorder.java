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


import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.feedback.FeedbackCollector;
import org.apache.wicket.feedback.IFeedback;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.form.FormComponent;

public class BootstrapFormComponentFeedbackBorder extends Border implements IFeedback
{
	private static final long serialVersionUID = 1L;

	public BootstrapFormComponentFeedbackBorder(String id, FormComponent<?>...formComponents)
	{
		super(id);
		add(formComponents);
	}

	@Override
	protected void onBeforeRender()
	{
		super.onBeforeRender();
	}

	@Override
	protected void onComponentTag(ComponentTag tag)
	{
		if (new FeedbackCollector(getPage()).collect(getMessagesFilter()).size() > 0)
			tag.put("class",(tag.getAttribute("class") == null ? "" : tag.getAttribute("class") + " ") + "has-error");
		super.onComponentTag(tag);
	}

	protected IFeedbackMessageFilter getMessagesFilter()
	{
		return new ContainerFeedbackMessageFilter(this);
	}
}
