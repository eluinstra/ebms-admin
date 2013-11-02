package nl.clockwork.ebms.admin.web;

import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

public class BootstrapFeedbackPanel extends FeedbackPanel
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

	public BootstrapFeedbackPanel(final String id, final IFeedbackMessageFilter filter)
	{
		super(id,filter);
	}

	public BootstrapFeedbackPanel(final String id)
	{
		super(id);
	}

	@Override
	protected String getCSSClass(FeedbackMessage message)
	{
		return ErrorLevel.getErrorLevel(message.getLevel()).getCssClass();
	}
	
}