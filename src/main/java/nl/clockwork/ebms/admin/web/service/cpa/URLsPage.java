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
import nl.clockwork.ebms.admin.web.OddOrEvenIndexStringModel;
import nl.clockwork.ebms.admin.web.PageLink;
import nl.clockwork.ebms.admin.web.WebMarkupContainer;
import nl.clockwork.ebms.service.CPAService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class URLsPage extends BasePage
{
	private class URLsDataView extends DataView<String>
	{
		protected URLsDataView(String id, IDataProvider<String> dataProvider)
		{
			super(id,dataProvider);
			setOutputMarkupId(true);
		}

		private static final long serialVersionUID = 1L;

		@Override
		protected void populateItem(final Item<String> item)
		{
			String url = item.getModelObject();
			item.add(createViewLink("view",url));
			item.add(createDeleteButton("delete"));
			item.add(AttributeModifier.replace("class",new OddOrEvenIndexStringModel(item.getIndex())));
		}

		private Link<Void> createViewLink(String id, final String url)
		{
			Link<Void> result = new Link<Void>(id)
			{
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick()
				{
					setResponsePage(new URLEditPage(url,cpaService.getURL(url),URLsPage.this));
				}
			};
			result.add(new Label("url",url));
			return result;
		}

		private Button createDeleteButton(String id)
		{
			Button result = new Button(id,new ResourceModel("cmd.delete"))
			{
				private static final long serialVersionUID = 1L;
	
				@Override
				public void onSubmit()
				{
					try
					{
						String url = (String)getParent().getDefaultModelObject();
						cpaService.setURL(url,null);
						setResponsePage(new URLsPage());
					}
					catch (Exception e)
					{
						logger.error("",e);
						error(e.getMessage());
					}
				}
			};
			result.add(AttributeModifier.replace("onclick","return confirm('" + getLocalizer().getString("confirm",this) + "');"));
			return result;
		}

	}

	private static final long serialVersionUID = 1L;
	private Log logger = LogFactory.getLog(this.getClass());
	@SpringBean(name="cpaService")
	private CPAService cpaService;

	public URLsPage()
	{
		add(new BootstrapFeedbackPanel("feedback"));
		add(new EditCPAsForm("form"));
	}

	public class EditCPAsForm extends Form<Void>
	{
		private static final long serialVersionUID = 1L;

		public EditCPAsForm(String id)
		{
			super(id);
			WebMarkupContainer container = new WebMarkupContainer("container");
			add(container);
			container.add(new URLsDataView("urls",new URLDataProvider(cpaService)));
			add(new PageLink("new",new URLEditPage(URLsPage.this)));
		}
	}
	
	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("urls",this);
	}
}
