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

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.val;
import lombok.experimental.FieldDefaults;
import nl.clockwork.ebms.admin.web.AjaxButton;
import nl.clockwork.ebms.admin.web.BootstrapFeedbackPanel;
import nl.clockwork.ebms.admin.web.BootstrapFormComponentFeedbackBorder;
import nl.clockwork.ebms.admin.web.Consumer;
import nl.clockwork.ebms.admin.web.Utils;
import nl.clockwork.ebms.service.model.DataSource;

public class DataSourceModalWindow extends ModalWindow
{
	private static final long serialVersionUID = 1L;

	public DataSourceModalWindow(String id, final List<DataSource> dataSources, final Component...components)
	{
		super(id);
		setCssClassName(ModalWindow.CSS_CLASS_GRAY);
		setContent(new DataSourcePanel(getContentId(),dataSources,components));
		setCookieName("dataSource");
		setCloseButtonCallback(new nl.clockwork.ebms.admin.web.CloseButtonCallback());
	}

	@Override
	public IModel<String> getTitle()
	{
		return Model.of(getLocalizer().getString("dataSource",this));
	}

	public class DataSourcePanel extends Panel
	{
		private static final long serialVersionUID = 1L;
		private List<DataSource> dataSources;
		private Component[] components;

		public DataSourcePanel(String id, List<DataSource> dataSources, Component[] components)
		{
			super(id);
			this.dataSources = dataSources;
			this.components = components;
			add(new DataSourceForm("form"));
		}
		
		public void addDataSource(DataSource dataSource)
		{
			dataSources.add(dataSource);
		}

		public Component[] getComponents()
		{
			return components;
		}
		public ModalWindow getWindow()
		{
			return DataSourceModalWindow.this;
		}

		public class DataSourceForm extends Form<DataSourceModel>
		{
			private static final long serialVersionUID = 1L;

			public DataSourceForm(String id)
			{
				super(id,new CompoundPropertyModel<>(new DataSourceModel()));
				add(new BootstrapFeedbackPanel("feedback"));
				add(new BootstrapFormComponentFeedbackBorder("fileFeedback",createFileField("file")));
				add(new TextField<String>("name").setLabel(new ResourceModel("lbl.name")));
				add(new TextField<String>("contentType").setLabel(new ResourceModel("lbl.contentType")));
				add(createAddButton("add"));
				add(createCancelButton("cancel"));
			}

			private FileUploadField createFileField(String id)
			{
				FileUploadField result = new FileUploadField(id);
				result.setLabel(new ResourceModel("lbl.file"));
				result.setRequired(true);
				return result;
			}

			private AjaxButton createAddButton(String id)
			{
				Consumer<AjaxRequestTarget> onSubmit = t ->
				{
					val o = getModelObject();
					o.getFile().forEach(f -> addDataSource(new DataSource(
								StringUtils.isBlank(o.getName()) ? f.getClientFileName() : o.getName(),
								null,
								StringUtils.isBlank(o.getContentType()) ? Utils.getContentType(f.getClientFileName()) : o.getContentType(),
								f.getBytes())));
					if (t != null)
					{
						t.add(getComponents());
						getWindow().close(t);
					}
				};
				Consumer<AjaxRequestTarget> onError = t ->
				{
					if (t != null)
					{
						t.add(this);
					}
				};
				val result = AjaxButton.builder()
						.id(id)
						.model(new ResourceModel("cmd.add"))
						.onSubmit(onSubmit)
						.onError(onError)
						.build();
				return result;
			}

			private AjaxButton createCancelButton(String id)
			{
				val cancel = AjaxButton.builder()
						.id(id)
						.model(new ResourceModel("cmd.cancel"))
						.onSubmit(t -> getWindow().close(t))
						.build();
				cancel.setDefaultFormProcessing(false);
				return cancel;
			}
		}
	}

	@Data
	@FieldDefaults(level = AccessLevel.PRIVATE)
	@EqualsAndHashCode(callSuper = true)
	public static class DataSourceModel extends DataSource
	{
		private static final long serialVersionUID = 1L;
		List<FileUpload> file;
	}
}
