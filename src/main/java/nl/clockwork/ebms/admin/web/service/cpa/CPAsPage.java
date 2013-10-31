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
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class CPAsPage extends BasePage
{
	private static final long serialVersionUID = 1L;
	private Log logger = LogFactory.getLog(this.getClass());
	@SpringBean(name="cpaService")
	private CPAService cpaService;

	public CPAsPage()
	{
		add(new BootstrapFeedbackPanel("feedback"));
		add(new EditCPAsForm("editCPAsForm"));
	}

	public class EditCPAsForm extends Form<Void>
	{
		private static final long serialVersionUID = 1L;

		public EditCPAsForm(String id)
		{
			super(id);

			WebMarkupContainer container = new WebMarkupContainer("container");
			container.setOutputMarkupId(true);

			DataView<String> cpaIds = new DataView<String>("cpaIds",new CPADataProvider(cpaService))
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void populateItem(final Item<String> item)
				{
					final String cpaId = item.getModelObject();
					Link<Void> link = new Link<Void>("view")
					{
						private static final long serialVersionUID = 1L;

						@Override
						public void onClick()
						{
							setResponsePage(new CPAPage(cpaService.getCPA(cpaId),CPAsPage.this));
						}
					};
					link.add(new Label("cpaId",cpaId));
					item.add(link);

					item.add(new DownloadCPALink("downloadCPA",cpaService,cpaId));

					Button delete = new Button("delete",new ResourceModel("cmd.delete"))
					{
						private static final long serialVersionUID = 1L;
			
						@Override
						public void onSubmit()
						{
							try
							{
								String cpaId = (String)getParent().getDefaultModelObject();
								cpaService.deleteCPA(cpaId);
								setResponsePage(new CPAsPage());
							}
							catch (Exception e)
							{
								logger.error("",e);
								error(e.getMessage());
							}
						}
					};
					delete.add(AttributeModifier.replace("onclick","return confirm('" + getLocalizer().getString("confirm",this) + "');"));
					item.add(delete);

					item.add(AttributeModifier.replace("class",new AbstractReadOnlyModel<String>()
					{
						private static final long serialVersionUID = 1L;
					
						@Override
						public String getObject()
						{
							return (item.getIndex() % 2 == 0) ? "even" : "odd";
						}
					}));
				}
			};
			cpaIds.setOutputMarkupId(true);

			container.add(cpaIds);
			add(container);
			add(new Link<Void>("new")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick()
				{
					setResponsePage(new CPAEditPage(CPAsPage.this));
				}
			});
		}
	}
	
	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("cpas",this);
	}
}
