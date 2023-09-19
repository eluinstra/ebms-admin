/*
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

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import nl.clockwork.ebms.admin.dao.EbMSDAO;
import nl.clockwork.ebms.admin.web.Action;
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.BootstrapFeedbackPanel;
import nl.clockwork.ebms.admin.web.BootstrapFormComponentFeedbackBorder;
import nl.clockwork.ebms.admin.web.Button;
import nl.clockwork.ebms.admin.web.ResetButton;
import nl.clockwork.ebms.cpa.CPAService;
import nl.clockwork.ebms.service.EbMSMessageService;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.io.IClusterable;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResendMessagePage extends BasePage
{
	private static final long serialVersionUID = 1L;
	@SpringBean(name = "ebMSAdminDAO")
	public EbMSDAO ebMSDAO;
	@SpringBean(name = "cpaService")
	private CPAService cpaService;
	@SpringBean(name = "ebMSMessageService")
	private EbMSMessageService ebMSMessageService;

	public ResendMessagePage()
	{
		add(new BootstrapFeedbackPanel("feedback").setOutputMarkupId(true));
		add(new MessageStatusForm("form"));
	}

	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("messageStatus", this);
	}

	public class MessageStatusForm extends Form<ResendMessageFormData>
	{
		private static final long serialVersionUID = 1L;

		public MessageStatusForm(String id)
		{
			super(id, new CompoundPropertyModel<>(new ResendMessageFormData()));
			add(new BootstrapFormComponentFeedbackBorder("messageIdFeedback", createMessageIdField("messageId")));
			val resend = createResendButton("resend");
			setDefaultButton(resend);
			add(resend);
			add(new ResetButton("reset", new ResourceModel("cmd.reset"), ResendMessagePage.class));
		}

		private TextField<String> createMessageIdField(String id)
		{
			val result = new TextField<String>(id);
			result.setLabel(new ResourceModel("lbl.messageId"));
			result.setRequired(true).setOutputMarkupPlaceholderTag(true);
			return result;
		}

		private Button createResendButton(String id)
		{
			Action onSubmit = () ->
			{
				try
				{
					val o = getModelObject();
					val messageId = ebMSMessageService.resendMessage(o.getMessageId());
					info(new StringResourceModel("resendMessage.ok", Model.of(messageId)).getString());
				}
				catch (Exception e)
				{
					log.error("", e);
					error(e.getMessage());
				}
			};
			return new Button(id, new ResourceModel("cmd.check"), onSubmit);
		}
	}

	@Data
	@FieldDefaults(level = AccessLevel.PRIVATE)
	@NoArgsConstructor
	public class ResendMessageFormData implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		String messageId;

		public void resetMessageId()
		{
			setMessageId(null);
		}
	}
}
