package nl.clockwork.ebms.admin.web.configuration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.FormComponentFeedbackBorder;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.io.IClusterable;

public class ConsolePropertiesFormPanel extends Panel
{
	private static final long serialVersionUID = 1L;
	protected transient Log logger = LogFactory.getLog(this.getClass());

	public ConsolePropertiesFormPanel(String id, final IModel<ConsolePropertiesFormModel> model)
	{
		super(id,model);
		add(new ConsolePropertiesForm("form",model));
	}

	public class ConsolePropertiesForm extends Form<ConsolePropertiesFormModel>
	{
		private static final long serialVersionUID = 1L;

		public ConsolePropertiesForm(String id, final IModel<ConsolePropertiesFormModel> model)
		{
			super(id,new CompoundPropertyModel<ConsolePropertiesFormModel>(model));

			TextField<Integer> maxItemsPerPage = new TextField<Integer>("maxItemsPerPage")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.maxItemsPerPage",ConsolePropertiesForm.this));
				}
			};
			maxItemsPerPage.setRequired(true);
			MarkupContainer maxItemsPerPageFeedback = new FormComponentFeedbackBorder("maxItemsPerPageFeedback");
			add(maxItemsPerPageFeedback);
			maxItemsPerPageFeedback.add(maxItemsPerPage);
			add(maxItemsPerPageFeedback);
		}
	}

	public static class ConsolePropertiesFormModel extends JdbcURL implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		private int maxItemsPerPage = 20;

		public int getMaxItemsPerPage()
		{
			return maxItemsPerPage;
		}
		public void setMaxItemsPerPage(int maxItemsPerPage)
		{
			this.maxItemsPerPage = maxItemsPerPage;
		}
	}
}
