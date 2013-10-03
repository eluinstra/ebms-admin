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
package nl.clockwork.ebms.admin.web.cpa;

import nl.clockwork.ebms.admin.dao.EbMSDAO;
import nl.clockwork.ebms.admin.model.CPA;
import nl.clockwork.ebms.admin.web.BasePage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.FormComponentFeedbackBorder;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class CPAEditPage extends BasePage
{
	private static final long serialVersionUID = 1L;
	protected transient Log logger = LogFactory.getLog(getClass());
	@SpringBean(name="ebMSDAO")
	private EbMSDAO ebMSDAO;

	public CPAEditPage(WebPage responsePage)
	{
		this(new CPA(),true,responsePage);
	}
	
	public CPAEditPage(final CPA cpa, WebPage responsePage)
	{
		this(cpa,false,responsePage);
	}	
	
	protected CPAEditPage(final CPA cpa, boolean isNew, WebPage responsePage)
	{
		add(new FeedbackPanel("feedback"));
		add(new EditCPAForm("editCPAForm",cpa,isNew,responsePage));
	}
	
	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("cpaEdit",this);
	}

	public class EditCPAForm extends Form<CPA>
	{
		private static final long serialVersionUID = 1L;

		public EditCPAForm(String id, CPA cpa, final boolean isNew, final WebPage responsePage)
		{
			super(id,new CompoundPropertyModel<CPA>(cpa));
			
			//final boolean isNew = cpa.getId() == null; //ebMSDAO.existsCPA(cpa.getId());

			TextField<String> cpaId = (TextField<String>)new TextField<String>("cpaId");
			cpaId.setLabel(Model.of(getLocalizer().getString("lbl.cpaId",this)));
			cpaId.setRequired(true);
			MarkupContainer cpaIdFeedback = new FormComponentFeedbackBorder("cpaIdFeedback");
			add(cpaIdFeedback);
			cpaIdFeedback.add(cpaId);

			TextArea<String> cpa_ = new TextArea<String>("cpa");
			cpa_.setLabel(Model.of(getLocalizer().getString("lbl.cpa",this)));
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
						CPA cpa = (CPA)getParent().getDefaultModelObject();
						if (isNew)
							ebMSDAO.insert(cpa);
						else
							ebMSDAO.update(cpa);
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

			Button delete = new Button("delete",new ResourceModel("cmd.delete"))
			{
				private static final long serialVersionUID = 1L;
	
				@Override
				public void onSubmit()
				{
					try
					{
						CPA service = (CPA)getParent().getDefaultModelObject();
						ebMSDAO.delete(service);
						setResponsePage(new CPAsPage());
					}
					catch (Exception e)
					{
						logger.error("",e);
						error(e.getMessage());
					}
				}
			};
			delete.setDefaultFormProcessing(false);
			delete.setVisible(!isNew);
			delete.add(AttributeModifier.replace("onclick","return confirm('" + getLocalizer().getString("confirm",this) + "');"));
			add(delete);

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
