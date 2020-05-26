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

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.io.IClusterable;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nl.clockwork.ebms.admin.dao.EbMSDAO;
import nl.clockwork.ebms.admin.web.Action;
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.BootstrapFeedbackPanel;
import nl.clockwork.ebms.admin.web.BootstrapFormComponentFeedbackBorder;
import nl.clockwork.ebms.admin.web.Button;
import nl.clockwork.ebms.admin.web.TextField;
import nl.clockwork.ebms.service.EbMSMessageService;
import nl.clockwork.ebms.service.cpa.CPAService;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageStatusPage extends BasePage
{
	private static final long serialVersionUID = 1L;
	@SpringBean(name="ebMSAdminDAO")
	EbMSDAO ebMSDAO;
	@SpringBean(name="cpaService")
	CPAService cpaService;
	@SpringBean(name="ebMSMessageService")
	EbMSMessageService ebMSMessageService;

	public MessageStatusPage()
	{
		add(new BootstrapFeedbackPanel("feedback").setOutputMarkupId(true));
		add(new MessageStatusForm("form"));
	}
	
	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("messageStatus",this);
	}

	public class MessageStatusForm extends Form<MessageStatusFormData>
	{
		private static final long serialVersionUID = 1L;

		public MessageStatusForm(String id)
		{
			super(id,new CompoundPropertyModel<>(new MessageStatusFormData()));
			add(new BootstrapFormComponentFeedbackBorder("messageIdFeedback",createMessageIdField("messageId")));
			val check = createCheckButton("check");
			setDefaultButton(check);
			add(check);
		}

		private TextField<String> createMessageIdField(String id)
		{
			val result = new TextField<String>(id);
			result.setLabel(new ResourceModel("lbl.messageId"));
			result.setRequired(true).setOutputMarkupPlaceholderTag(true);
			return result;
		}

		private Button createCheckButton(String id)
		{
			Action onSubmit = () ->
			{
				try
				{
					val o = getModelObject();
					val messageStatus = ebMSMessageService.getMessageStatus(o.getMessageId());
					info(new StringResourceModel("getMessageStatus.ok",Model.of(messageStatus.getStatus())).getString());
				}
				catch (Exception e)
				{
					log.error("",e);
					error(e.getMessage());
				}
			};
			return new Button(id,new ResourceModel("cmd.check"),onSubmit);
		}
	}

	@Data
	@FieldDefaults(level = AccessLevel.PRIVATE)
	@NoArgsConstructor
	public class MessageStatusFormData implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		String messageId;
	}		
}
