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

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
//import org.apache.wicket.markup.repeater.OddEvenItem;
//import org.apache.wicket.model.IModel;

public class CPAsPage extends BasePage
{
	private static final long serialVersionUID = 1L;
	@SpringBean(name="ebMSDAO")
	private EbMSDAO ebMSDAO;
	@SpringBean(name="maxItemsPerPage")
	private Integer maxItemsPerPage;

	public CPAsPage()
	{
		WebMarkupContainer container = new WebMarkupContainer("container");
		container.setOutputMarkupId(true);

		DataView<CPA> cpas = new DataView<CPA>("cpas",new CPADataProvider(ebMSDAO))
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final Item<CPA> item)
			{
				final CPA cpa = item.getModelObject();
				Link<Void> link = new Link<Void>("view")
				{
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick()
					{
						//setResponsePage(new CPAPage(ebMSDAO.getCPA(cpa.getId(),CPAsPage.this)));
						setResponsePage(new CPAPage(cpa,CPAsPage.this));
					}
				};
				link.add(new Label("cpaId",cpa.getCpaId()));
				item.add(link);
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

//			@Override
//			protected Item<CPA> newItem(String id, int index, IModel<CPA> model)
//			{
//				return new OddEvenItem<CPA>(id,index,model);
//			}
		};
		cpas.setOutputMarkupId(true);
		cpas.setItemsPerPage(maxItemsPerPage);

		container.add(cpas);
		add(container);
		add(new AjaxPagingNavigator("navigator",cpas));
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

	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("cpas",this);
	}
}
