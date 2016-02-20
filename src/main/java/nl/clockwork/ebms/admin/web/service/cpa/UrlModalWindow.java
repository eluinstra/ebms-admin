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

import java.io.Serializable;

import nl.clockwork.ebms.admin.web.BootstrapFeedbackPanel;
import nl.clockwork.ebms.admin.web.LocalizedStringResource;
import nl.clockwork.ebms.admin.web.StringTextField;
import nl.clockwork.ebms.service.CPAService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

public class UrlModalWindow extends ModalWindow
{
	private static final long serialVersionUID = 1L;

	public UrlModalWindow(String id, final CPAService cpaService, final String cpaId, final Component...components)
	{
		super(id);
		setCssClassName(ModalWindow.CSS_CLASS_GRAY);
		setContent(createUrlPanel(cpaService,cpaId,components));
		setCookieName("url");
		setCloseButtonCallback(new nl.clockwork.ebms.admin.web.CloseButtonCallback());
	}

	@Override
	public IModel<String> getTitle()
	{
		return Model.of(getLocalizer().getString("url",this));
	}

	private UrlPanel createUrlPanel(final CPAService cpaService, final String cpaId, final Component...components)
	{
		return new UrlPanel(getContentId(),cpaService.getURL(cpaId))
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void saveUrl(String url)
			{
				cpaService.setURL(cpaId,url);
			}
			
			@Override
			public Component[] getComponents()
			{
				return components;
			}
			
			@Override
			public ModalWindow getWindow()
			{
				return UrlModalWindow.this;
			}
		};
	}
	
	public abstract class UrlPanel extends Panel
	{
		private static final long serialVersionUID = 1L;
		protected Log logger = LogFactory.getLog(this.getClass());

		public UrlPanel(String id, String url)
		{
			super(id);
			add(new UrlForm("form",url));
		}
		
		public abstract void saveUrl(String url);
		public abstract Component[] getComponents();
		public abstract ModalWindow getWindow();

		public class UrlForm extends Form<UrlModel>
		{
			private static final long serialVersionUID = 1L;

			public UrlForm(String id, String url)
			{
				super(id,new CompoundPropertyModel<UrlModel>(new UrlModel(url)));
				add(new BootstrapFeedbackPanel("feedback"));
				add(new StringTextField("url",new LocalizedStringResource("lbl.url",UrlForm.this)));
				add(createSaveButton("save"));
				add(createCancelButton("cancel"));
			}

			private AjaxButton createSaveButton(String id)
			{
				AjaxButton result = new AjaxButton(id,new ResourceModel("cmd.save"))
				{
					private static final long serialVersionUID = 1L;

					@Override
					protected void onSubmit(AjaxRequestTarget target, Form<?> form)
					{
						UrlModel model = UrlForm.this.getModelObject();
						saveUrl(model.getUrl());
						if (target != null)
						{
							target.add(getComponents());
							getWindow().close(target);
						}
					}

					@Override
					protected void onError(AjaxRequestTarget target, Form<?> form)
					{
						super.onError(target,form);
						if (target != null)
						{
							target.add(form);
						}
					}
				};
				return result;
			}

			private AjaxButton createCancelButton(String id)
			{
				AjaxButton result = new AjaxButton(id,new ResourceModel("cmd.cancel"))
				{
					private static final long serialVersionUID = 1L;

					@Override
					protected void onSubmit(AjaxRequestTarget target, Form<?> form)
					{
						getWindow().close(target);
					}
				};
				result.setDefaultFormProcessing(false);
				return result;
			}

		}
	}

	public static class UrlModel implements Serializable
	{
		private static final long serialVersionUID = 1L;
		private String url;

		public UrlModel(String url)
		{
			this.url = url;
		}

		public String getUrl()
		{
			return url;
		}
	}
}
