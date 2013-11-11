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

	public BootstrapFormComponentFeedbackBorder(String id)
	{
		super(id);
	}

	public BootstrapFormComponentFeedbackBorder(String id, FormComponent<?> formComponent)
	{
		this(id);
		add(formComponent);
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
			tag.put("class",(tag.getAttribute("class") == null ? "" :  tag.getAttribute("class") + " ") + "has-error");
		super.onComponentTag(tag);
	}
	
	protected IFeedbackMessageFilter getMessagesFilter()
	{
		return new ContainerFeedbackMessageFilter(this);
	}
}
