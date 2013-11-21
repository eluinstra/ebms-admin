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
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.io.IClusterable;

public class DataSourcesPanel extends Panel implements DataSources
{
	private static final long serialVersionUID = 1L;
	protected Log logger = LogFactory.getLog(this.getClass());

	public DataSourcesPanel(String id)
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

			ListView<EbMSDataSource> dataSources = new ListView<EbMSDataSource>("dataSources")
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void populateItem(final ListItem<EbMSDataSource> item)
				{
					item.setModel(new CompoundPropertyModel<EbMSDataSource>(item.getModelObject()));
					item.add(new Label("name"));
					item.add(new Label("contentType"));
					item.add(new AjaxButton("remove",new ResourceModel("cmd.remove"),DataSourcesForm.this)
					{
						private static final long serialVersionUID = 1L;
						
						@Override
						protected void onSubmit(AjaxRequestTarget target, Form<?> form)
						{
							DataSourcesForm.this.getModelObject().getDataSources().remove(item.getModelObject());
							target.add(DataSourcesForm.this);
						}
					});
				}
			};
			dataSources.setOutputMarkupId(true);
			add(dataSources);

			final ModalWindow dataSourceModalWindow = new DataSourceModalWindow("dataSourceModelWindow",getModelObject().getDataSources(),DataSourcesForm.this);
			add(dataSourceModalWindow);
			
			AjaxButton add = new AjaxButton("add")
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form)
				{
					dataSourceModalWindow.show(target);
				}
			};
			add(add);
		}

	}
	
	@Override
	public List<EbMSDataSource> getDataSources()
	{
		return ((DataSourcesForm)this.get("form")).getModelObject().getDataSources();
	}

	@Override
	public void resetDataSources()
	{
		((DataSourcesForm)this.get("form")).getModelObject().resetDataSources();
	}

	public static class DataSourcesModel implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		private List<EbMSDataSource> dataSources = new ArrayList<EbMSDataSource>();

		public List<EbMSDataSource> getDataSources()
		{
			return dataSources;
		}
		public void resetDataSources()
		{
			dataSources.clear();
		}
	}

}