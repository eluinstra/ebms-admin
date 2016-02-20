package nl.clockwork.ebms.admin.web;

import java.util.Arrays;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;

public class MaxItemsPerPageChoice extends DropDownChoice<Integer>
{
	private static final long serialVersionUID = 1L;

	public MaxItemsPerPageChoice(String id, IModel<Integer> maxItemsPerPage, final Component...components)
	{
		super(id,maxItemsPerPage,Arrays.asList(5,10,15,20,25,50,100));
		add(new AjaxFormComponentUpdatingBehavior("onchange")
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target)
			{
				target.add(components);
			}
		});
	}

}
