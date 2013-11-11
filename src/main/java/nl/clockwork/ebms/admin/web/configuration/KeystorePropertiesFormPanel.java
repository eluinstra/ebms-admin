package nl.clockwork.ebms.admin.web.configuration;

import org.apache.wicket.model.IModel;

public class KeystorePropertiesFormPanel extends JavaKeyStorePropertiesFormPanel
{
	private static final long serialVersionUID = 1L;

	public KeystorePropertiesFormPanel(String id, final IModel<JavaKeyStorePropertiesFormModel> model)
	{
		super(id,model);
	}
}