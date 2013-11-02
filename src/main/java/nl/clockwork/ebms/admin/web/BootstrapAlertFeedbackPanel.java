package nl.clockwork.ebms.admin.web;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

public class BootstrapAlertFeedbackPanel extends FeedbackPanel
{
	private static final long serialVersionUID = 1L;
	private enum ErrorLevel
	{
		UNDEFINED(0,"alert-info"), DEBUG(100,"alert-info"), INFO(200,"alert-info"), SUCCESS(250,"alert-success"), WARNING(300,"alert-warning"), ERROR(400,"alert-danger"), FATAL(500,"alert-danger");
		
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

	public BootstrapAlertFeedbackPanel(final String id)
	{
		super(id);
	}

	public BootstrapAlertFeedbackPanel(final String id, final IFeedbackMessageFilter filter)
	{
		super(id,filter);
	}

	@Override
	protected String getCSSClass(FeedbackMessage message)
	{
		return "alert " + ErrorLevel.getErrorLevel(message.getLevel()).getCssClass() + " alert-dismissable";
	}
	
	@Override
	protected Component newMessageDisplayComponent(final String id, final FeedbackMessage message)
	{
		WebMarkupContainer container = new WebMarkupContainer(id);
		container.add(new Button("close"));
		Serializable serializable = message.getMessage();
		Label label = new Label("content",serializable == null ? "" : serializable.toString());
		label.setEscapeModelStrings(BootstrapAlertFeedbackPanel.this.getEscapeModelStrings());
		container.add(label);
		return container;
	}
}