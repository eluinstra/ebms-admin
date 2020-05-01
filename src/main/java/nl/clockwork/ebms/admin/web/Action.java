package nl.clockwork.ebms.admin.web;

import java.io.Serializable;

public interface Action extends Serializable
{
	void doIt();
}