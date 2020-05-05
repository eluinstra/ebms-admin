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
package nl.clockwork.ebms.admin.web.service.message;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.io.IClusterable;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.Value;
import lombok.val;
import lombok.experimental.FieldDefaults;
import nl.clockwork.ebms.admin.web.AjaxButton;
import nl.clockwork.ebms.admin.web.Consumer;
import nl.clockwork.ebms.service.model.EbMSDataSource;

public class DefaultDataSourcesPanel extends DataSourcesPanel
{
	@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
	private class EbMSDataSourceListView extends ListView<EbMSDataSource>
	{
		private static final long serialVersionUID = 1L;
		@NonNull
		DataSourcesForm dataSourcesForm;

		public EbMSDataSourceListView(String id, @NonNull DataSourcesForm dataSourcesForm)
		{
			super(id);
			this.dataSourcesForm = dataSourcesForm;
			setOutputMarkupId(true);
		}

		@Override
		protected void populateItem(final ListItem<EbMSDataSource> item)
		{
			item.setModel(new CompoundPropertyModel<>(item.getModelObject()));
			item.add(new Label("name"));
			item.add(new Label("contentType"));
			Consumer<AjaxRequestTarget> onSubmit = t ->
			{
				dataSourcesForm.getModelObject().getDataSources().remove(item.getModelObject());
				t.add(dataSourcesForm);
			};
			item.add(AjaxButton.builder()
					.id("remove")
					.model(new ResourceModel("cmd.remove"))
					.form(dataSourcesForm)
					.onSubmit(onSubmit)
					.build());
		}
	}

	private static final long serialVersionUID = 1L;

	public DefaultDataSourcesPanel(String id)
	{
		super(id);
		add(new DataSourcesForm("form"));
	}
	
	public class DataSourcesForm extends Form<DataSourcesModel>
	{
		private static final long serialVersionUID = 1L;

		public DataSourcesForm(String id)
		{
			super(id,new CompoundPropertyModel<>(new DataSourcesModel()));
			add(new EbMSDataSourceListView("dataSources",DataSourcesForm.this));
			val dataSourceModalWindow = new DataSourceModalWindow("dataSourceModelWindow",getModelObject().getDataSources(),DataSourcesForm.this);
			add(dataSourceModalWindow);
			add(createAddButton("add",dataSourceModalWindow));
		}

		private AjaxButton createAddButton(String id, final ModalWindow dataSourceModalWindow)
		{
			return AjaxButton.builder()
					.id(id)
					.onSubmit(t -> dataSourceModalWindow.show(t))
					.build();
		}
	}
	
	@Override
	public List<EbMSDataSource> getDataSources()
	{
		return ((DataSourcesForm)this.get("form")).getModelObject().getDataSources();
	}

	@Value
	public static class DataSourcesModel implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		List<EbMSDataSource> dataSources = new ArrayList<>();
	}

}