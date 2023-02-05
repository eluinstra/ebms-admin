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
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

public class BootstrapFeedbackPanel extends FeedbackPanel
{
	@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
	@AllArgsConstructor
	@Getter
	private enum ErrorLevel
	{
		UNDEFINED(0, "text-primary"),
		DEBUG(100, "text-muted"),
		INFO(200, "text-info"),
		SUCCESS(250, "text-success"),
		WARNING(300, "text-warning"),
		ERROR(400, "text-danger"),
		FATAL(500, "text-danger");

		int errorCode;
		String cssClass;

		public static Optional<ErrorLevel> getErrorLevel(int errorCode)
		{
			return Arrays.stream(ErrorLevel.values()).filter(e -> errorCode == e.errorCode).findFirst();
		}
	}

	private static final long serialVersionUID = 1L;

	public BootstrapFeedbackPanel(final String id)
	{
		super(id);
	}

	public BootstrapFeedbackPanel(final String id, final IFeedbackMessageFilter filter)
	{
		super(id, filter);
	}

	@Override
	protected String getCSSClass(FeedbackMessage message)
	{
		return ErrorLevel.getErrorLevel(message.getLevel()).map(l -> l.cssClass).orElse(ErrorLevel.UNDEFINED.cssClass);
	}

}