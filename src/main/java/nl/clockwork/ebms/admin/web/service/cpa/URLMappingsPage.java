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

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import lombok.AccessLevel;
import lombok.val;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nl.clockwork.ebms.admin.web.Action;
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.BootstrapFeedbackPanel;
import nl.clockwork.ebms.admin.web.Button;
import nl.clockwork.ebms.admin.web.OddOrEvenIndexStringModel;
import nl.clockwork.ebms.admin.web.PageLink;
import nl.clockwork.ebms.admin.web.WebMarkupContainer;
import nl.clockwork.ebms.service.cpa.url.URLMapping;
import nl.clockwork.ebms.service.cpa.url.URLMappingService;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class URLMappingsPage extends BasePage
{
	private class URLMappingsDataView extends DataView<URLMapping>
	{
		protected URLMappingsDataView(String id, IDataProvider<URLMapping> dataProvider)
		{
			super(id,dataProvider);
			setOutputMarkupId(true);
		}

		private static final long serialVersionUID = 1L;

		@Override
		protected void populateItem(final Item<URLMapping> item)
		{
			val o = item.getModelObject();
			item.add(new Label("source",o.getSource()));
			item.add(new Label("destination",o.getDestination()));
			item.add(createEditButton("editUrl",item.getModel()));
			item.add(createDeleteButton("delete",item.getModel()));
			item.add(AttributeModifier.replace("class",OddOrEvenIndexStringModel.of(item.getIndex())));
		}

		private Button createEditButton(String id, final IModel<URLMapping> model)
		{
			Action onSubmit = () ->
			{
				try
				{
					setResponsePage(new URLMappingPage(model));
				}
				catch (Exception e)
				{
					log.error("",e);
					error(e.getMessage());
				}
			};
			return new Button(id,new ResourceModel("cmd.edit"),onSubmit);
		}

		private Button createDeleteButton(String id, final IModel<URLMapping> model)
		{
			Action onSubmit = () ->
			{
				try
				{
					urlMappingService.deleteURLMapping(model.getObject().getSource());
					setResponsePage(new URLMappingsPage());
				}
				catch (Exception e)
				{
					log.error("",e);
					error(e.getMessage());
				}
			};
			val result = new Button(id,new ResourceModel("cmd.delete"),onSubmit);
			result.add(AttributeModifier.replace("onclick","return confirm('" + getLocalizer().getString("confirm",this) + "');"));
			return result;
		}

	}

	private static final long serialVersionUID = 1L;
	@SpringBean(name="urlMappingService")
	URLMappingService urlMappingService;

	public URLMappingsPage()
	{
		add(new BootstrapFeedbackPanel("feedback"));
		add(new EditURLMappingsForm("form"));
	}

	public class EditURLMappingsForm extends Form<Void>
	{
		private static final long serialVersionUID = 1L;

		public EditURLMappingsForm(String id)
		{
			super(id);
			val container = new WebMarkupContainer("container");
			add(container);
			container.add(new URLMappingsDataView("urlMappings",URLMappingDataProvider.of(urlMappingService)));
			add(new PageLink("new",new URLMappingPage()));
		}
	}
	
	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("urlMappings",this);
	}
}
