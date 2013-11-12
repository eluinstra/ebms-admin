package nl.clockwork.ebms.admin.web.configuration;

import nl.clockwork.ebms.admin.web.configuration.JavaKeyStorePropertiesFormPanel.JavaKeyStorePropertiesFormModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
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

			CheckBox signing = new CheckBox("signing")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.signing",SignaturePropertiesForm.this));
				}
			};
			add(signing);

			final JavaKeyStorePropertiesFormPanel keystoreProperties = new JavaKeyStorePropertiesFormPanel("keystoreProperties",new PropertyModel<JavaKeyStorePropertiesFormModel>(getModelObject(),"keystoreProperties"),false)
			{
				private static final long serialVersionUID = 1L;

				public boolean isVisible()
				{
					return getModelObject().getSigning();
				}
			};
			keystoreProperties.setOutputMarkupId(true);
			add(keystoreProperties);

			signing.add(new AjaxFormComponentUpdatingBehavior("onchange")
      {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					//FIXME
					//target.add(keystoreProperties);
					target.add(SignaturePropertiesForm.this);
				}
      });
		}
	}

	public static class SignaturePropertiesFormModel implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		private boolean signing = true;
		private JavaKeyStorePropertiesFormModel keystoreProperties = new JavaKeyStorePropertiesFormModel();

		public boolean getSigning()
		{
			return signing;
		}
		public void setSigning(boolean signing)
		{
			this.signing = signing;
		}
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
