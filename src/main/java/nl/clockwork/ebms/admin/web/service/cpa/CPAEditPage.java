/**
 * Copyright 2013 Clockwork
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.clockwork.ebms.admin.web.service.cpa;

import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.BootstrapFeedbackPanel;
import nl.clockwork.ebms.service.CPAService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.validation.FormComponentFeedbackBorder;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class CPAEditPage extends BasePage
{
	private static final long serialVersionUID = 1L;
	protected transient Log logger = LogFactory.getLog(getClass());
	@SpringBean(name="cpaService")
	private CPAService cpaService;

	public CPAEditPage(WebPage responsePage)
	{
		this(null,true,responsePage);
	}
	
	public CPAEditPage(String cpa, WebPage responsePage)
	{
		this(cpa,false,responsePage);
	}	
	
	protected CPAEditPage(String cpa, boolean isNew, WebPage responsePage)
	{
		add(new BootstrapFeedbackPanel("feedback"));
		add(new EditCPAForm("editCPAForm",cpa,isNew,responsePage));
	}
	
	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("cpaEdit",this);
	}

	public class EditCPAForm extends Form<String>
	{
		private static final long serialVersionUID = 1L;

		public EditCPAForm(String id, String cpa, final boolean isNew, final WebPage responsePage)
		{
			super(id,Model.of(cpa));
			
			final TextArea<String> cpa_ = new TextArea<String>("cpa",Model.of(cpa))
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.cpa",EditCPAForm.this));
				}
			};
			cpa_.setRequired(true);
			MarkupContainer cpaFeedback = new FormComponentFeedbackBorder("cpaFeedback");
			add(cpaFeedback);
			cpaFeedback.add(cpa_);

			Button save = new Button("save",new ResourceModel("cmd.save"))
			{
				private static final long serialVersionUID = 1L;
	
				@Override
				public void onSubmit()
				{
					try
					{
						//String cpa = (String)getParent().getDefaultModelObject();
						String cpa = cpa_.getModelObject();
						if (isNew)
							cpaService.insertCPA(cpa,false);
						else
							cpaService.insertCPA(cpa,true);
						setResponsePage(new CPAsPage());
					}
					catch (Exception e)
					{
						logger.error("",e);
						error(e.getMessage());
					}
				}
			};
			setDefaultButton(save);
			add(save);

			Button cancel = new Button("cancel",new ResourceModel("cmd.cancel"))
			{
				private static final long serialVersionUID = 1L;

				@Override
				public void onSubmit()
				{
					setResponsePage(responsePage);
				}
			};
			cancel.setDefaultFormProcessing(false);
			add(cancel);
		}
	}

}
