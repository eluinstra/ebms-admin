package nl.clockwork.ebms.admin.web;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;

public class CSSFeedbackPanel extends FeedbackPanel
{
	private static final long serialVersionUID = 1L;
	private enum ErrorLevel
	{
		UNDEFINED(0,"text-primary"), DEBUG(100,"text-muted"), INFO(200,"text-info"), SUCCESS(250,"text-success"), WARNING(300,"text-warning"), ERROR(400,"text-danger"), FATAL(500,"text-danger");
		
		private int errorCode;
		private String cssClass;

		private ErrorLevel(int errorCode, String cssClass)
		{
			this.errorCode = errorCode;
			this.cssClass = cssClass;
		}
		
		public static ErrorLevel getErrorLevel(int errorCode)
		{
			for (ErrorLevel errorLevel : ErrorLevel.values())
				if (errorCode == errorLevel.errorCode)
					return errorLevel;
			return null;
		}
		
		public String getCssClass()
		{
			return cssClass;
		}
	}

	public CSSFeedbackPanel(final String id, final IFeedbackMessageFilter filter)
	{
		super(id,filter);
	}

	public CSSFeedbackPanel(final String id)
	{
		super(id);
	}

	@Override
	protected Component newMessageDisplayComponent(final String id, final FeedbackMessage message)
	{
		final Component newMessageDisplayComponent = super.newMessageDisplayComponent(id,message);
		newMessageDisplayComponent.add(new AttributeAppender("class",new Model<String>(ErrorLevel.getErrorLevel(message.getLevel()).getCssClass())," "));
		return newMessageDisplayComponent;
	}
}