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


import java.util.Arrays;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.apache.wicket.Component;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

public class BootstrapAlertFeedbackPanel extends FeedbackPanel
{
	@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
	@AllArgsConstructor
	@Getter
	private enum ErrorLevel
	{
		UNDEFINED(0, "alert-info"),
		DEBUG(100, "alert-info"),
		INFO(200, "alert-info"),
		SUCCESS(250, "alert-success"),
		WARNING(300, "alert-warning"),
		ERROR(400, "alert-danger"),
		FATAL(500, "alert-danger");

		int errorCode;
		String cssClass;

		public static Optional<ErrorLevel> getErrorLevel(int errorCode)
		{
			return Arrays.stream(ErrorLevel.values()).filter(e -> errorCode == e.errorCode).findFirst();
		}
	}

	private static final long serialVersionUID = 1L;

	public BootstrapAlertFeedbackPanel(final String id)
	{
		super(id);
	}

	public BootstrapAlertFeedbackPanel(final String id, final IFeedbackMessageFilter filter)
	{
		super(id, filter);
	}

	@Override
	protected String getCSSClass(FeedbackMessage message)
	{
		return "alert " + ErrorLevel.getErrorLevel(message.getLevel()).map(l -> l.cssClass).orElse(ErrorLevel.UNDEFINED.cssClass) + " alert-dismissable";
	}

	@Override
	protected Component newMessageDisplayComponent(final String id, final FeedbackMessage message)
	{
		val container = new WebMarkupContainer(id);
		container.add(new Button("close"));
		val serializable = message.getMessage();
		val label = new Label("content", serializable == null ? "" : serializable.toString());
		label.setEscapeModelStrings(getEscapeModelStrings());
		container.add(label);
		return container;
	}
}