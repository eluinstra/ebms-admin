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

import nl.clockwork.ebms.model.EbMSDataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.io.IClusterable;

public class DefaultDataSourcesPanel extends DataSourcesPanel
{
	private class EbMSDataSourceListView extends ListView<EbMSDataSource>
	{
		private DataSourcesForm dataSourcesForm;

		public EbMSDataSourceListView(String id, DataSourcesForm dataSourcesForm)
		{
			super(id);
			this.dataSourcesForm = dataSourcesForm;
			setOutputMarkupId(true);
		}

		private static final long serialVersionUID = 1L;

		@Override
		protected void populateItem(final ListItem<EbMSDataSource> item)
		{
			item.setModel(new CompoundPropertyModel<EbMSDataSource>(item.getModelObject()));
			item.add(new Label("name"));
			item.add(new Label("contentType"));
			item.add(new AjaxButton("remove",new ResourceModel("cmd.remove"),dataSourcesForm)
			{
				private static final long serialVersionUID = 1L;
				
				@Override
				protected void onSubmit(AjaxRequestTarget target)
				{
					dataSourcesForm.getModelObject().getDataSources().remove(item.getModelObject());
					target.add(dataSourcesForm);
				}
			});
		}
	}
	private static final long serialVersionUID = 1L;
	protected transient Log logger = LogFactory.getLog(this.getClass());

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
			super(id,new CompoundPropertyModel<DataSourcesModel>(new DataSourcesModel()));
			add(new EbMSDataSourceListView("dataSources",DataSourcesForm.this));
			final ModalWindow dataSourceModalWindow = new DataSourceModalWindow("dataSourceModelWindow",getModelObject().getDataSources(),DataSourcesForm.this);
			add(dataSourceModalWindow);
			add(createAddButton("add",dataSourceModalWindow));
		}

		private AjaxButton createAddButton(String id, final ModalWindow dataSourceModalWindow)
		{
			AjaxButton result = new AjaxButton(id)
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void onSubmit(AjaxRequestTarget target)
				{
					dataSourceModalWindow.show(target);
				}
			};
			return result;
		}
	}
	
	@Override
	public List<EbMSDataSource> getDataSources()
	{
		return ((DataSourcesForm)this.get("form")).getModelObject().getDataSources();
	}

	public static class DataSourcesModel implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		private List<EbMSDataSource> dataSources = new ArrayList<EbMSDataSource>();

		public List<EbMSDataSource> getDataSources()
		{
			return dataSources;
		}
	}

}