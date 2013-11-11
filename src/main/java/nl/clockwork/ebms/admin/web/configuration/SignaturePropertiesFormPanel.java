package nl.clockwork.ebms.admin.web.configuration;

import nl.clockwork.ebms.admin.web.configuration.JavaKeyStorePropertiesFormPanel.JavaKeyStorePropertiesFormModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.io.IClusterable;

public class SignaturePropertiesFormPanel extends Panel
{
	private static final long serialVersionUID = 1L;
	protected transient Log logger = LogFactory.getLog(this.getClass());

	public SignaturePropertiesFormPanel(String id, final IModel<SignaturePropertiesFormModel> model)
	{
		super(id,model);
		add(new SignaturePropertiesForm("form",model));
	}

	public class SignaturePropertiesForm extends Form<SignaturePropertiesFormModel>
	{
		private static final long serialVersionUID = 1L;

		public SignaturePropertiesForm(String id, final IModel<SignaturePropertiesFormModel> model)
		{
			super(id,new CompoundPropertyModel<SignaturePropertiesFormModel>(model));

			add(new JavaKeyStorePropertiesFormPanel("properties",new PropertyModel<JavaKeyStorePropertiesFormModel>(getModelObject(),"keystoreProperties"),false));
		}
	}

	public static class SignaturePropertiesFormModel implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		private JavaKeyStorePropertiesFormModel keystoreProperties = new JavaKeyStorePropertiesFormModel();

		public JavaKeyStorePropertiesFormModel getKeystoreProperties()
		{
			return keystoreProperties;
		}
		public void setKeystoreProperties(JavaKeyStorePropertiesFormModel keystoreProperties)
		{
			this.keystoreProperties = keystoreProperties;
		}
	}
}
