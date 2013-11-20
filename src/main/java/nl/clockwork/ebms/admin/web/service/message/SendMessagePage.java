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

import javax.xml.bind.JAXBException;

import nl.clockwork.ebms.admin.CPAUtils;
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.BootstrapFeedbackPanel;
import nl.clockwork.ebms.admin.web.BootstrapFormComponentFeedbackBorder;
import nl.clockwork.ebms.admin.web.ResetButton;
import nl.clockwork.ebms.common.XMLMessageBuilder;
import nl.clockwork.ebms.model.EbMSDataSource;
import nl.clockwork.ebms.model.EbMSMessageContent;
import nl.clockwork.ebms.service.CPAService;
import nl.clockwork.ebms.service.EbMSMessageService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.oasis_open.committees.ebxml_cppa.schema.cpp_cpa_2_0.CollaborationProtocolAgreement;

public class SendMessagePage extends BasePage
{
	private static final long serialVersionUID = 1L;
	protected transient Log logger = LogFactory.getLog(getClass());
	@SpringBean(name="cpaService")
	private CPAService cpaService;
	@SpringBean(name="ebMSMessageService")
	private EbMSMessageService ebMSMessageService;

	public SendMessagePage()
	{
		add(new BootstrapFeedbackPanel("feedback").setOutputMarkupId(true));
		add(new MessageForm("form"));
	}
	
	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("message",this);
	}

	public class MessageForm extends Form<EbMSMessageContextModel>
	{
		private static final long serialVersionUID = 1L;

		public MessageForm(String id)
		{
			super(id,new CompoundPropertyModel<EbMSMessageContextModel>(new EbMSMessageContextModel()));
			setMultiPart(true);

			DropDownChoice<String> cpaIds = new DropDownChoice<String>("cpaIds",new PropertyModel<String>(this.getModelObject(),"cpaId"),Model.ofList(cpaService.getCPAIds()))
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.cpaId",MessageForm.this));
				}
			};
			cpaIds.setRequired(true);
			add(new BootstrapFormComponentFeedbackBorder("cpaIdFeedback",cpaIds));

			cpaIds.add(new AjaxFormComponentUpdatingBehavior("onchange")
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					try
					{
						EbMSMessageContextModel model = MessageForm.this.getModelObject();
						CollaborationProtocolAgreement cpa = XMLMessageBuilder.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
						model.resetFromRoles(CPAUtils.getRoleNames(cpa));
						model.resetServices();
						model.resetActions();
						model.resetDataSources();
						target.add(getPage().get("feedback"));
						target.add(getPage().get("form"));
					}
					catch (JAXBException e)
					{
						logger.error("",e);
						error(e.getMessage());
					}
				}
			});

			DropDownChoice<String> fromRoles = new DropDownChoice<String>("fromRoles",new PropertyModel<String>(this.getModelObject(),"fromRole"),new PropertyModel<List<String>>(this.getModelObject(),"fromRoles"))
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.fromRole",MessageForm.this));
				}
			};
			fromRoles.setRequired(true);
			fromRoles.setOutputMarkupId(true);
			add(new BootstrapFormComponentFeedbackBorder("fromRoleFeedback",fromRoles));
			
			fromRoles.add(new AjaxFormComponentUpdatingBehavior("onchange")
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					try
					{
						EbMSMessageContextModel model = MessageForm.this.getModelObject();
						CollaborationProtocolAgreement cpa = XMLMessageBuilder.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
						model.resetServices(CPAUtils.getServiceNames(cpa,model.getFromRole()));
						model.resetActions();
						model.resetDataSources();
						target.add(getPage().get("feedback"));
						target.add(getPage().get("form"));
					}
					catch (JAXBException e)
					{
						logger.error("",e);
						error(e.getMessage());
					}
				}
			});

			DropDownChoice<String> services = new DropDownChoice<String>("services",new PropertyModel<String>(this.getModelObject(),"service"),new PropertyModel<List<String>>(this.getModelObject(),"services"))
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.service",MessageForm.this));
				}
			};
			services.setRequired(true);
			services.setOutputMarkupId(true);
			add(new BootstrapFormComponentFeedbackBorder("serviceFeedback",services));
			
			services.add(new AjaxFormComponentUpdatingBehavior("onchange")
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					try
					{
						EbMSMessageContextModel model = MessageForm.this.getModelObject();
						CollaborationProtocolAgreement cpa = XMLMessageBuilder.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
						model.resetActions(CPAUtils.getFromActionNames(cpa,model.getFromRole(),model.getService()));
						model.resetDataSources();
						target.add(getPage().get("feedback"));
						target.add(getPage().get("form"));
					}
					catch (JAXBException e)
					{
						logger.error("",e);
						error(e.getMessage());
					}
				}
			});

			DropDownChoice<String> actions = new DropDownChoice<String>("actions",new PropertyModel<String>(this.getModelObject(),"action"),new PropertyModel<List<String>>(this.getModelObject(),"actions"))
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.action",MessageForm.this));
				}
			};
			actions.setRequired(true);
			actions.setOutputMarkupId(true);
			add(new BootstrapFormComponentFeedbackBorder("actionFeedback",actions));

			add(new TextField<String>("conversationId")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.conversationId",MessageForm.this));
				}
			});

			add(new TextField<String>("messageId")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.messageId",MessageForm.this));
				}
			});

			add(new TextField<String>("refToMessageId")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.refToMessageId",MessageForm.this));
				}
			});

			DataSourcesForm dataSourcesForm = new DataSourcesForm("form",getModelObject().getDataSources());
			dataSourcesForm.setOutputMarkupId(true);
			add(dataSourcesForm);

			Button send = new Button("send",new ResourceModel("cmd.send"))
			{
				private static final long serialVersionUID = 1L;
	
				@Override
				public void onSubmit()
				{
					try
					{
						EbMSMessageContextModel model = MessageForm.this.getModelObject();
						EbMSMessageContent messageContent = new EbMSMessageContent(model,model.getDataSources());
						String messageId = ebMSMessageService.sendMessage(messageContent );
						info(new StringResourceModel("sendMessage.ok",Model.of(messageId)).getString());
					}
					catch (Exception e)
					{
						logger.error("",e);
						error(e.getMessage());
					}
				}
			};
			setDefaultButton(send);
			add(send);

			add(new ResetButton("reset",new ResourceModel("cmd.reset"),SendMessagePage.class));
		}
	}

	public class DataSourcesForm extends Form<List<? extends EbMSDataSource>>
	{
		private static final long serialVersionUID = 1L;

		public DataSourcesForm(String id, List<EbMSDataSource> dataSources)
		{
			super(id,Model.ofList(dataSources));

			ListView<EbMSDataSource> dataSources_ = new ListView<EbMSDataSource>("dataSources",dataSources)
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
							DataSourcesForm.this.getModelObject().remove(item.getModelObject());
							target.add(DataSourcesForm.this);
						}
					});
				}
			};
			dataSources_.setOutputMarkupId(true);
			add(dataSources_);

			final ModalWindow dataSourceModalWindow = new DataSourceModalWindow("dataSourceModelWindow",dataSources,DataSourcesForm.this);
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

	public class DataSourceModalWindow extends ModalWindow
	{
		private static final long serialVersionUID = 1L;

		public DataSourceModalWindow(String id, final List<EbMSDataSource> dataSources, final Component...components)
		{
			super(id);
			//setTitle(getLocalizer().getString("dataSource",this));
			setCssClassName(ModalWindow.CSS_CLASS_GRAY);
			setContent(new DataSourcePanel(this,getContentId())
			{
				private static final long serialVersionUID = 1L;

				@Override
				public void addDataSource(EbMSDataSource dataSource)
				{
					dataSources.add(dataSource);
				}
				
				@Override
				public Component[] getComponents()
				{
					return components;
				}
				
				@Override
				public ModalWindow getWindow()
				{
					return DataSourceModalWindow.this;
				}
			});
			setCookieName("eventError");
			setCloseButtonCallback(new ModalWindow.CloseButtonCallback()
			{
				private static final long serialVersionUID = 1L;

				public boolean onCloseButtonClicked(AjaxRequestTarget target)
				{
					return true;
				}
			});
		}
		
		@Override
		public IModel<String> getTitle()
		{
			return Model.of(getLocalizer().getString("dataSource",this));
		}
	}
}
