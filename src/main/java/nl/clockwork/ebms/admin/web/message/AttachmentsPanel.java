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
package nl.clockwork.ebms.admin.web.message;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import lombok.AccessLevel;
import lombok.val;
import lombok.experimental.FieldDefaults;
import nl.clockwork.ebms.admin.dao.EbMSDAO;
import nl.clockwork.ebms.admin.model.EbMSAttachment;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttachmentsPanel extends Panel
{
	private class EbMSAttachmentPropertyListView extends PropertyListView<EbMSAttachment>
	{
		private static final long serialVersionUID = 1L;

		public EbMSAttachmentPropertyListView(String id, IModel<List<EbMSAttachment>> list)
		{
			super(id,list);
		}

		@Override
		protected void populateItem(ListItem<EbMSAttachment> item)
		{
			item.add(new Label("name"));
			val link = new DownloadEbMSAttachmentLink("downloadAttachment",ebMSDAO,item.getModel());
			link.add(new Label("contentId"));
			item.add(link);
			item.add(new Label("contentType"));
		}
	}

	private static final long serialVersionUID = 1L;
	@SpringBean(name="ebMSAdminDAO")
	EbMSDAO ebMSDAO;

	public AttachmentsPanel(String id, IModel<List<EbMSAttachment>> attachments)
	{
		super(id);
		add(new EbMSAttachmentPropertyListView("attachments",attachments));
	}

}