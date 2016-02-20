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
import nl.clockwork.ebms.admin.web.BootstrapPagingNavigator;
import nl.clockwork.ebms.admin.web.MaxItemsPerPageChoice;
import nl.clockwork.ebms.admin.web.WebMarkupContainer;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class CPAsPage extends BasePage
{
	private class CPADataView extends DataView<CPA>
	{
		private static final long serialVersionUID = 1L;

		protected CPADataView(String id, IDataProvider<CPA> dataProvider)
		{
			super(id,dataProvider);
			setOutputMarkupId(true);
		}

		@Override
		public long getItemsPerPage()
		{
			return maxItemsPerPage;
		}

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
	}

	private static final long serialVersionUID = 1L;
	@SpringBean(name="ebMSAdminDAO")
	private EbMSDAO ebMSDAO;
	@SpringBean(name="maxItemsPerPage")
	private Integer maxItemsPerPage;

	public CPAsPage()
	{
		final WebMarkupContainer container = new WebMarkupContainer("container");
		add(container);
		DataView<CPA> cpas = new CPADataView("cpas",new CPADataProvider(ebMSDAO));
		container.add(cpas);
		final BootstrapPagingNavigator navigator = new BootstrapPagingNavigator("navigator",cpas);
		add(navigator);
		add(new MaxItemsPerPageChoice("maxItemsPerPage",new PropertyModel<Integer>(this,"maxItemsPerPage"),container,navigator));
	}

	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("cpas",this);
	}

}
