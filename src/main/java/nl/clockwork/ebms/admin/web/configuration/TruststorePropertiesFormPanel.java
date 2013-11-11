package nl.clockwork.ebms.admin.web.configuration;

import org.apache.wicket.model.IModel;

public class TruststorePropertiesFormPanel extends JavaKeyStorePropertiesFormPanel
{
	private static final long serialVersionUID = 1L;

	public TruststorePropertiesFormPanel(String id, IModel<JavaKeyStorePropertiesFormModel> model)
	{
		super(id,model);
	}
}