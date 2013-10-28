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

import javax.xml.bind.JAXBException;

import nl.clockwork.ebms.admin.CPAUtils;
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.BootstrapFeedbackPanel;
import nl.clockwork.ebms.common.XMLMessageBuilder;
import nl.clockwork.ebms.model.EbMSDataSource;
import nl.clockwork.ebms.model.EbMSMessageContent;
import nl.clockwork.ebms.service.CPAService;
import nl.clockwork.ebms.service.EbMSMessageService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.validation.FormComponentFeedbackBorder;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
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
		add(new BootstrapFeedbackPanel("feedback"));
		add(new MessageForm("messageForm"));
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
			MarkupContainer cpaIdFeedback = new FormComponentFeedbackBorder("cpaIdFeedback");
			add(cpaIdFeedback);
			cpaIdFeedback.add(cpaIds);

			final DropDownChoice<String> fromRoles = new DropDownChoice<String>("fromRoles",new PropertyModel<String>(this.getModelObject(),"fromRole"),new PropertyModel<List<String>>(this.getModelObject(),"fromRoles"))
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
			MarkupContainer fromRoleFeedback = new FormComponentFeedbackBorder("fromRoleFeedback");
			add(fromRoleFeedback);
			fromRoleFeedback.add(fromRoles);
			
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
						ArrayList<String> roleNames = CPAUtils.getRoleNames(cpa);
						model.setFromRoles(roleNames);
						target.add(fromRoles);
					}
					catch (JAXBException e)
					{
						logger.error("",e);
						error(e.getMessage());
					}
				}
      });

			final DropDownChoice<String> services = new DropDownChoice<String>("services",new PropertyModel<String>(this.getModelObject(),"service"),new PropertyModel<List<String>>(this.getModelObject(),"services"))
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
			MarkupContainer serviceFeedback = new FormComponentFeedbackBorder("serviceFeedback");
			add(serviceFeedback);
			serviceFeedback.add(services);
			
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
						List<String> serviceNames = CPAUtils.getServiceNames(cpa,model.getFromRole());
						model.setServices(serviceNames);
						target.add(services);
					}
					catch (JAXBException e)
					{
						logger.error("",e);
						error(e.getMessage());
					}
				}
      });

			final DropDownChoice<String> actions = new DropDownChoice<String>("actions",new PropertyModel<String>(this.getModelObject(),"action"),new PropertyModel<List<String>>(this.getModelObject(),"actions"))
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
			MarkupContainer actionFeedback = new FormComponentFeedbackBorder("actionFeedback");
			add(actionFeedback);
			actionFeedback.add(actions);
			
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
						List<String> actionNames = CPAUtils.getActionNames(cpa,model.getFromRole(),model.getService());
						model.setActions(actionNames);
						target.add(actions);
					}
					catch (JAXBException e)
					{
						logger.error("",e);
						error(e.getMessage());
					}
				}
      });
			
			final DataSourcesForm dataSourcesForm = new DataSourcesForm("dataSourcesForm",getModelObject().getDataSources());
			dataSourcesForm.setOutputMarkupId(true);
			add(dataSourcesForm);
			//FIXME
			getModelObject().setDataSourcesForm(dataSourcesForm);

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
						info("Send message succesful. MessageId is " + messageId);
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
		}
	}

	public class DataSourcesForm extends Form<List<EbMSDataSource>>
	{
		private static final long serialVersionUID = 1L;

		public DataSourcesForm(String id, final List<EbMSDataSource> dataSources)
		{
			super(id);//,Model.ofList(dataSources));

			final ListView<EbMSDataSource> dataSources_ = new ListView<EbMSDataSource>("dataSources",dataSources)
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
							dataSources.remove(item.getModelObject());
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
