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
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import lombok.AccessLevel;
import lombok.val;
import lombok.experimental.FieldDefaults;
import lombok.extern.apachecommons.CommonsLog;
import nl.clockwork.ebms.admin.web.Action;
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.BootstrapFeedbackPanel;
import nl.clockwork.ebms.admin.web.Button;
import nl.clockwork.ebms.admin.web.OddOrEvenIndexStringModel;
import nl.clockwork.ebms.admin.web.PageLink;
import nl.clockwork.ebms.admin.web.WebMarkupContainer;
import nl.clockwork.ebms.model.URLMapping;
import nl.clockwork.ebms.service.CPAService;

@CommonsLog
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
			val urlMapping = item.getModelObject();
			item.add(new Label("source",Model.of(urlMapping.getSource())));
			item.add(new Label("destination",Model.of(urlMapping.getDestination())));
			item.add(createEditButton("editUrl",urlMapping));
			item.add(createDeleteButton("delete",urlMapping));
			item.add(AttributeModifier.replace("class",new OddOrEvenIndexStringModel(item.getIndex())));
		}

		private Button createEditButton(String id, final URLMapping urlMapping)
		{
			Action onSubmit = () ->
			{
				try
				{
					setResponsePage(new URLMappingPage(urlMapping));
				}
				catch (Exception e)
				{
					log.error("",e);
					error(e.getMessage());
				}
			};
			return new Button(id,new ResourceModel("cmd.edit"),onSubmit );
		}

		private Button createDeleteButton(String id, final URLMapping urlMapping)
		{
			Action onSubmit = () ->
			{
				try
				{
					cpaService.deleteURLMapping(urlMapping.getSource());
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
	@SpringBean(name="cpaService")
	CPAService cpaService;

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
			container.add(new URLMappingsDataView("urlMappings",new URLMappingDataProvider(cpaService)));
			add(new PageLink("new",new URLMappingPage()));
		}
	}
	
	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("urlMappings",this);
	}
}
