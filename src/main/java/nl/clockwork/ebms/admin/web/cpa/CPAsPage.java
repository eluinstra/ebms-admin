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

import java.util.Arrays;

import nl.clockwork.ebms.admin.dao.EbMSDAO;
import nl.clockwork.ebms.admin.model.CPA;
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.BootstrapPagingNavigator;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
//import org.apache.wicket.markup.repeater.OddEvenItem;
//import org.apache.wicket.model.IModel;

public class CPAsPage extends BasePage
{
	private class CPADataView extends DataView<CPA>
	{
		private static final long serialVersionUID = 1L;

		protected CPADataView(String id, IDataProvider<CPA> dataProvider)
		{
			super(id,dataProvider);
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

//		@Override
//		protected Item<CPA> newItem(String id, int index, IModel<CPA> model)
//		{
//			return new OddEvenItem<CPA>(id,index,model);
//		}
	}
	private static final long serialVersionUID = 1L;
	@SpringBean(name="ebMSAdminDAO")
	private EbMSDAO ebMSDAO;
	@SpringBean(name="maxItemsPerPage")
	private Integer maxItemsPerPage;

	public CPAsPage()
	{
		final WebMarkupContainer container = createContainer("container");
		add(container);
		DataView<CPA> cpas = createCPAs("cpas");
		container.add(cpas);
		final BootstrapPagingNavigator navigator = createNavigator("navigator",cpas);
		add(navigator);
		add(createMaxItemsPerPage("maxItemsPerPage",container,navigator));
	}

	private WebMarkupContainer createContainer(String id)
	{
		final WebMarkupContainer result = new WebMarkupContainer(id);
		result.setOutputMarkupId(true);
		return result;
	}

	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("cpas",this);
	}

	private DataView<CPA> createCPAs(String id)
	{
		DataView<CPA> result = new CPADataView(id,new CPADataProvider(ebMSDAO));
		result.setOutputMarkupId(true);
		return result;
	}

	private BootstrapPagingNavigator createNavigator(String id, DataView<CPA> cpas)
	{
		return new BootstrapPagingNavigator(id,cpas);
	}

	private DropDownChoice<Integer> createMaxItemsPerPage(String id, final WebMarkupContainer container, final BootstrapPagingNavigator navigator)
	{
		DropDownChoice<Integer> result = new DropDownChoice<Integer>(id,new PropertyModel<Integer>(this,"maxItemsPerPage"),Arrays.asList(5,10,15,20,25,50,100));
		result.add(new AjaxFormComponentUpdatingBehavior("onchange")
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target)
			{
				target.add(navigator);
				target.add(container);
			}
		});
		return result;
	}

}
