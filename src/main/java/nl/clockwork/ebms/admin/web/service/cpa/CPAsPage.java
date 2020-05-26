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
import org.apache.wicket.model.Model;
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
import nl.clockwork.ebms.admin.web.Link;
import nl.clockwork.ebms.admin.web.OddOrEvenIndexStringModel;
import nl.clockwork.ebms.admin.web.PageClassLink;
import nl.clockwork.ebms.admin.web.WebMarkupContainer;
import nl.clockwork.ebms.service.cpa.CPAService;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CPAsPage extends BasePage
{
	private class CPAIdsDataView extends DataView<String>
	{
		private static final long serialVersionUID = 1L;

		protected CPAIdsDataView(String id, IDataProvider<String> dataProvider)
		{
			super(id,dataProvider);
			setOutputMarkupId(true);
		}

		@Override
		protected void populateItem(final Item<String> item)
		{
			val o = item.getModelObject();
			item.add(createViewLink("view",o));
			item.add(new DownloadCPALink("downloadCPA",cpaService,item.getModel()));
			item.add(createDeleteButton("delete",item.getModel()));
			item.add(AttributeModifier.replace("class",OddOrEvenIndexStringModel.of(item.getIndex())));
		}

		private Link<Void> createViewLink(String id, final String cpaId)
		{
			val result = Link.<Void>builder()
					.id(id)
					.onClick(() -> setResponsePage(new CPAPage(Model.of(cpaService.getCPA(cpaId)),CPAsPage.this)))
					.build();
			result.add(new Label("cpaId",cpaId));
			return result;
		}

		private Button createDeleteButton(String id, final IModel<String> cpaId)
		{
			Action onSubmit = () ->
			{
				try
				{
					cpaService.deleteCPA(cpaId.getObject());
					setResponsePage(new CPAsPage());
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

	public CPAsPage()
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
			val container = new WebMarkupContainer("container");
			add(container);
			container.add(new CPAIdsDataView("cpaIds",CPADataProvider.of(cpaService)));
			add(new PageClassLink("new",CPAUploadPage.class));
		}
	}
	
	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("cpas",this);
	}
}
