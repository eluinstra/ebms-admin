package nl.clockwork.ebms.admin.web.service.message;

import java.io.Serializable;

public interface MessageProcessor extends Serializable
{
	void processMessage(String messageId);
}
