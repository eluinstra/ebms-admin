/*
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

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.val;
import nl.clockwork.ebms.admin.dao.EbMSDAO;
import nl.clockwork.ebms.admin.model.CPA;
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.BootstrapPagingNavigator;
import nl.clockwork.ebms.admin.web.Link;
import nl.clockwork.ebms.admin.web.MaxItemsPerPageChoice;
import nl.clockwork.ebms.admin.web.OddOrEvenIndexStringModel;
import nl.clockwork.ebms.admin.web.WebMarkupContainer;
import nl.clockwork.ebms.admin.web.WicketApplication;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class CPAsPage extends BasePage
{
	private class CPADataView extends DataView<CPA>
	{
		private static final long serialVersionUID = 1L;

		protected CPADataView(String id, IDataProvider<CPA> dataProvider)
		{
			super(id, dataProvider);
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
			item.add(createViewLink("view", item.getModel()));
			item.add(AttributeModifier.replace("class", OddOrEvenIndexStringModel.of(item.getIndex())));
		}

		private Link<Void> createViewLink(String id, final IModel<CPA> model)
		{
			val result = Link.<Void>builder().id(id).onClick(() -> setResponsePage(new CPAPage(model, CPAsPage.this))).build();
			result.add(new Label("cpaId", model.getObject().getCpaId()));
			return result;
		}
	}

	private static final long serialVersionUID = 1L;
	@SpringBean(name = "ebMSAdminDAO")
	EbMSDAO ebMSDAO;
	@NonNull
	final Integer maxItemsPerPage;

	public CPAsPage()
	{
		this.maxItemsPerPage = WicketApplication.get().getMaxItemsPerPage();
		val container = new WebMarkupContainer("container");
		add(container);
		val cpas = new CPADataView("cpas", CPADataProvider.of(ebMSDAO));
		container.add(cpas);
		val navigator = new BootstrapPagingNavigator("navigator", cpas);
		add(navigator);
		add(new MaxItemsPerPageChoice("maxItemsPerPage", new PropertyModel<>(this, "maxItemsPerPage"), container, navigator));
	}

	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("cpas", this);
	}

}
