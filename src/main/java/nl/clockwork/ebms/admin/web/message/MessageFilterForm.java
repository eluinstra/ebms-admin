package nl.clockwork.ebms.admin.web.message;

import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBException;

import nl.clockwork.ebms.Constants.EbMSMessageStatus;
import nl.clockwork.ebms.admin.CPAUtils;
import nl.clockwork.ebms.admin.Utils;
import nl.clockwork.ebms.admin.model.LocalizedStringResource;
import nl.clockwork.ebms.admin.web.BootstrapDateTimePicker;
import nl.clockwork.ebms.admin.web.StringTextField;
import nl.clockwork.ebms.admin.web.message.MessageFilterPanel.MessageFilterFormModel;
import nl.clockwork.ebms.common.XMLMessageBuilder;
import nl.clockwork.ebms.service.CPAService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.oasis_open.committees.ebxml_cppa.schema.cpp_cpa_2_0.CollaborationProtocolAgreement;

class MessageFilterForm extends Form<MessageFilterFormModel>
{
	private static final long serialVersionUID = 1L;
	protected transient Log logger = LogFactory.getLog(getClass());
	protected CPAService cpaService;
	private BootstrapDateTimePicker from;
	private BootstrapDateTimePicker to;

	public MessageFilterForm(String id, MessageFilterFormModel model, final CPAService cpaService, final MessageFilterPanel messageFilterPanel)
	{
		super(id,new CompoundPropertyModel<MessageFilterFormModel>(model));
		this.cpaService = cpaService;

		add(createCPAIdsChoice("cpaIds"));
		add(createFromPartyIdsChoice("fromPartyIds"));
		add(createFromRolesChoice("fromRoles"));
		add(createToPartyIdsChoice("toPartyIds"));
		add(createToRolesChoice("toRoles"));
		add(createServicesChoice("services"));
		add(createActionsChoice("actions"));
		add(new StringTextField("conversationId",new LocalizedStringResource("lbl.conversationId",MessageFilterForm.this)));
		add(new StringTextField("messageId",new LocalizedStringResource("lbl.messageId",MessageFilterForm.this)));
		add(new StringTextField("refToMessageId",new LocalizedStringResource("lbl.refToMessageId",MessageFilterForm.this)));
		add(createStatusesChoice("statuses"));
		from = new BootstrapDateTimePicker("from",new LocalizedStringResource("lbl.from",MessageFilterForm.this),"dd-MM-yyyy",BootstrapDateTimePicker.Type.DATE);
		add(from);
		to = new BootstrapDateTimePicker("to",new LocalizedStringResource("lbl.to",MessageFilterForm.this),"dd-MM-yyyy",BootstrapDateTimePicker.Type.DATE);
		add(to);

		add(new Button("search",new ResourceModel("cmd.search"))
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit()
			{
				setResponsePage(messageFilterPanel.getPage(MessageFilterForm.this.getModelObject()));
			}
		});
		
		add(new Button("reset",new ResourceModel("cmd.reset"))
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit()
			{
				setResponsePage(getPage().getClass());
			}
		});

	}

	private DropDownChoice<String> createCPAIdsChoice(String id)
	{
		DropDownChoice<String> result = new DropDownChoice<String>(id,new PropertyModel<String>(this.getModelObject(),"cpaId"),Model.ofList(Utils.toList(cpaService.getCPAIds())))
		{
			private static final long serialVersionUID = 1L;

			@Override
			public IModel<String> getLabel()
			{
				return Model.of(getLocalizer().getString("lbl.cpaId",MessageFilterForm.this));
			}
		};
		result.add(new AjaxFormComponentUpdatingBehavior("onchange")
    {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target)
			{
				try
				{
					MessageFilterFormModel model = MessageFilterForm.this.getModelObject();
					CollaborationProtocolAgreement cpa = XMLMessageBuilder.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
					model.resetFromPartyIds(CPAUtils.getPartyIds(cpa));
					model.resetFromRoles();
					model.resetToPartyIds(CPAUtils.getPartyIds(cpa));
					model.resetToRoles();
					model.resetServices();
					model.resetActions();
					target.add(getFeedbackComponent());
					target.add(getForm());
				}
				catch (JAXBException e)
				{
					logger.error("",e);
					error(e.getMessage());
				}
			}
    });
		return result;
	}

	private DropDownChoice<String> createFromPartyIdsChoice(String id)
	{
		DropDownChoice<String> result = new DropDownChoice<String>(id,new PropertyModel<String>(this.getModelObject(),"fromRole.partyId"),new PropertyModel<List<String>>(this.getModelObject(),"fromPartyIds"))
		{
			private static final long serialVersionUID = 1L;

			@Override
			public IModel<String> getLabel()
			{
				return Model.of(getLocalizer().getString("lbl.fromPartyId",MessageFilterForm.this));
			}
			
			@Override
			public boolean isEnabled()
			{
				return MessageFilterForm.this.getModelObject().getToRole() == null;
			}
		};
		result.setOutputMarkupId(true);
		result.add(new AjaxFormComponentUpdatingBehavior("onchange")
    {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target)
			{
				try
				{
					MessageFilterFormModel model = MessageFilterForm.this.getModelObject();
					CollaborationProtocolAgreement cpa = XMLMessageBuilder.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
					model.resetFromRoles(CPAUtils.getRoleNames(cpa,model.getFromRole().getPartyId()));
					model.resetServices();
					model.resetActions();
					target.add(getFeedbackComponent());
					target.add(getForm());
				}
				catch (JAXBException e)
				{
					logger.error("",e);
					error(e.getMessage());
				}
			}
    });
		return result;
	}

	private DropDownChoice<String> createFromRolesChoice(String id)
	{
		DropDownChoice<String> result = new DropDownChoice<String>(id,new PropertyModel<String>(this.getModelObject(),"fromRole.role"),new PropertyModel<List<String>>(this.getModelObject(),"fromRoles"))
		{
			private static final long serialVersionUID = 1L;

			@Override
			public IModel<String> getLabel()
			{
				return Model.of(getLocalizer().getString("lbl.fromRole",MessageFilterForm.this));
			}
			
			@Override
			public boolean isEnabled()
			{
				return MessageFilterForm.this.getModelObject().getToRole() == null;
			}
		};
		result.setOutputMarkupId(true);
		result.add(new AjaxFormComponentUpdatingBehavior("onchange")
    {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target)
			{
				try
				{
					MessageFilterFormModel model = MessageFilterForm.this.getModelObject();
					CollaborationProtocolAgreement cpa = XMLMessageBuilder.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
					model.resetServices(CPAUtils.getServiceNames(cpa,model.getFromRole().getRole()));
					model.resetActions();
					target.add(getFeedbackComponent());
					target.add(getForm());
				}
				catch (JAXBException e)
				{
					logger.error("",e);
					error(e.getMessage());
				}
			}
    });
		return result;
	}

	private DropDownChoice<String> createToPartyIdsChoice(String id)
	{
		DropDownChoice<String> result = new DropDownChoice<String>(id,new PropertyModel<String>(this.getModelObject(),"toRole.partyId"),new PropertyModel<List<String>>(this.getModelObject(),"toPartyIds"))
		{
			private static final long serialVersionUID = 1L;

			@Override
			public IModel<String> getLabel()
			{
				return Model.of(getLocalizer().getString("lbl.toPartyId",MessageFilterForm.this));
			}

			@Override
			public boolean isEnabled()
			{
				return MessageFilterForm.this.getModelObject().getFromRole() == null;
			}
		};
		result.setOutputMarkupId(true);
		result.add(new AjaxFormComponentUpdatingBehavior("onchange")
    {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target)
			{
				try
				{
					MessageFilterFormModel model = MessageFilterForm.this.getModelObject();
					CollaborationProtocolAgreement cpa = XMLMessageBuilder.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
					model.resetToRoles(CPAUtils.getRoleNames(cpa,model.getToRole().getPartyId()));
					model.resetServices();
					model.resetActions();
					target.add(getFeedbackComponent());
					target.add(getForm());
				}
				catch (JAXBException e)
				{
					logger.error("",e);
					error(e.getMessage());
				}
			}
    });
		return result;
	}

	private DropDownChoice<String> createToRolesChoice(String id)
	{
		DropDownChoice<String> result = new DropDownChoice<String>(id,new PropertyModel<String>(this.getModelObject(),"toRole.role"),new PropertyModel<List<String>>(this.getModelObject(),"toRoles"))
		{
			private static final long serialVersionUID = 1L;

			@Override
			public IModel<String> getLabel()
			{
				return Model.of(getLocalizer().getString("lbl.toRole",MessageFilterForm.this));
			}

			@Override
			public boolean isEnabled()
			{
				return MessageFilterForm.this.getModelObject().getFromRole() == null;
			}
		};
		result.setOutputMarkupId(true);
		result.add(new AjaxFormComponentUpdatingBehavior("onchange")
    {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target)
			{
				try
				{
					MessageFilterFormModel model = MessageFilterForm.this.getModelObject();
					CollaborationProtocolAgreement cpa = XMLMessageBuilder.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
					model.resetServices(CPAUtils.getServiceNames(cpa,model.getToRole().getRole()));
					model.resetActions();
					target.add(getFeedbackComponent());
					target.add(getForm());
				}
				catch (JAXBException e)
				{
					logger.error("",e);
					error(e.getMessage());
				}
			}
    });
		return result;
	}

	private DropDownChoice<String> createServicesChoice(String id)
	{
		DropDownChoice<String> result = new DropDownChoice<String>(id,new PropertyModel<String>(this.getModelObject(),"service"),new PropertyModel<List<String>>(this.getModelObject(),"services"))
		{
			private static final long serialVersionUID = 1L;

			@Override
			public IModel<String> getLabel()
			{
				return Model.of(getLocalizer().getString("lbl.service",MessageFilterForm.this));
			}
		};
		result.setOutputMarkupId(true);
		result.add(new AjaxFormComponentUpdatingBehavior("onchange")
    {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target)
			{
				try
				{
					MessageFilterFormModel model = MessageFilterForm.this.getModelObject();
					CollaborationProtocolAgreement cpa = XMLMessageBuilder.getInstance(CollaborationProtocolAgreement.class).handle(cpaService.getCPA(model.getCpaId()));
					model.resetActions(model.getFromRole() == null ? CPAUtils.getToActionNames(cpa,model.getToRole().getRole(),model.getService()) : CPAUtils.getFromActionNames(cpa,model.getFromRole().getRole(),model.getService()));
					target.add(getFeedbackComponent());
					target.add(getForm());
				}
				catch (JAXBException e)
				{
					logger.error("",e);
					error(e.getMessage());
				}
			}
    });
		return result;
	}

	private DropDownChoice<String> createActionsChoice(String id)
	{
		DropDownChoice<String> result = new DropDownChoice<String>(id,new PropertyModel<String>(this.getModelObject(),"action"),new PropertyModel<List<String>>(this.getModelObject(),"actions"))
		{
			private static final long serialVersionUID = 1L;

			@Override
			public IModel<String> getLabel()
			{
				return Model.of(getLocalizer().getString("lbl.action",MessageFilterForm.this));
			}
		};
		result.setOutputMarkupId(true);
		return result;
	}

	private ListMultipleChoice<EbMSMessageStatus> createStatusesChoice(String id)
	{
		return new ListMultipleChoice<EbMSMessageStatus>(id,new PropertyModel<List<EbMSMessageStatus>>(this.getModelObject(),"statuses"),Model.ofList(Arrays.asList(EbMSMessageStatus.values())))
		{
			private static final long serialVersionUID = 1L;

			@Override
			public IModel<String> getLabel()
			{
				return Model.of(getLocalizer().getString("lbl.status",MessageFilterForm.this));
			}
			
			@Override
			protected boolean localizeDisplayValues()
			{
				return true;
			}
		}.setMaxRows(4);
	}

	private Component getFeedbackComponent()
	{
		return this.get("feedback");
	}
	
	private Component getForm()
	{
		return this.get("form");
	}

	public BootstrapDateTimePicker getFrom()
	{
		return from;
	}

	public BootstrapDateTimePicker getTo()
	{
		return to;
	}
}
