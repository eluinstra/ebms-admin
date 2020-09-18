---
sort: 6
---

# EbMS API

## CPA
The CPAService contains functionality to control CPAs  

## Url Mapping
The EbMSMessageService contains functionality to override CPA's urls  

## Certificate Mapping
The EbMSMessageService contains functionality to override CPA's certificates  

## EbMS Message
The EbMSMessageService contains functionality for sending and receiving ebms messages  

	/**
	 * Performs an EbMS ping action for CPA cpaId, from party fromParty and to party toParty
	 * 
	 * @param cpaId
	 * @param fromPartyId
	 * @param toPartyId
	 * @throws EbMSMessageServiceException
	 */
	@WebMethod(operationName = "ping")
	void ping(@WebParam(name = "cpaId") @XmlElement(required = true) String cpaId, @WebParam(name = "fromPartyId") @XmlElement(required = true) String fromPartyId, @WebParam(name = "toPartyId") @XmlElement(required = true) String toPartyId) throws EbMSMessageServiceException;

	/**
	 * Sends the message content message as an EbMS message
	 * 
	 * @param message
	 * @return The messageId of the generated EbMS message
	 * @throws EbMSMessageServiceException
	 */
	@WebResult(name = "messageId")
	@WebMethod(operationName = "sendMessage")
	String sendMessage(@WebParam(name = "message") @XmlElement(required = true) MessageRequest messageRequest) throws EbMSMessageServiceException;

	/**
	 * Sends the message content message as an EbMS message using MTOM/XOP.
	 * 
	 * @param message
	 * @return The messageId of the generated EbMS message
	 * @throws EbMSMessageServiceException
	 */

	/**
	 * Resends the content of message identified by messageId as an EbMS message
	 * 
	 * @param messageId
	 * @return The messageId of the generated EbMS message
	 * @throws EbMSMessageServiceException
	 */
	@WebResult(name = "messageId")
	@WebMethod(operationName = "resendMessage")
	String resendMessage(@WebParam(name = "messageId") @XmlElement(required = true) String messageId) throws EbMSMessageServiceException;

	/**
	 * Gets all messageIds of messages with the RECEIVED status that satisfy the filter messageFilter. If maxNr is given, then maxNr messageIds are returned
	 * 
	 * @param messageFilter
	 * @param maxNr
	 * @return The list of messageIds
	 * @throws EbMSMessageServiceException
	 */
	@WebResult(name = "messageId")
	@WebMethod(operationName = "getUnprocessedMessageIds")
	List<String> getUnprocessedMessageIds(@WebParam(name = "messageFilter") MessageFilter messageFilter, @WebParam(name = "maxNr") Integer maxNr) throws EbMSMessageServiceException;

	/**
	 * Gets the message content of the message identified by messageId. If process is true, the message is given the status PROCESSED, which means that it is no
	 * longer returned in the list of getMessageIds
	 * 
	 * @param messageId
	 * @param process
	 * @return The message
	 * @throws EbMSMessageServiceException
	 */
	@WebResult(name = "message")
	@WebMethod(operationName = "getMessage")
	Message getMessage(@WebParam(name = "messageId") @XmlElement(required = true) String messageId, @WebParam(name = "process") Boolean process) throws EbMSMessageServiceException;

	/**
	 * Sets the status of the message identified by messageId to PROCESSED, so that it is no longer returned in the list of getUnprocessedMessageIds
	 * 
	 * @param messageId
	 * @throws EbMSMessageServiceException
	 */
	@WebMethod(operationName = "processMessage")
	void processMessage(@WebParam(name = "messageId") @XmlElement(required = true) String messageId) throws EbMSMessageServiceException;

	/**
	 * Gets the message status of the message identified by messageId
	 * 
	 * @param messageId
	 * @return The message status
	 * @throws EbMSMessageServiceException
	 */
	@WebResult(name = "messageStatus")
	@WebMethod(operationName = "getMessageStatus")
	MessageStatus getMessageStatus(@WebParam(name = "messageId") @XmlElement(required = true) String messageId) throws EbMSMessageServiceException;

	/**
	 * Gets the events that satisfy the messageFilter filter and the eventTypes eventTypes. If maxNr is included, then maxNr events are returned. The possible
	 * event types are: - RECEIVED – when a message is received - DELIVERED – if a message has been sent successfully - FAILED – if a message returns an error
	 * while sending - EXPIRED – if a message could not be sent within the number of attempts and time agreed in the CPA
	 * 
	 * @param messageFilter
	 * @param eventTypes
	 * @param maxNr
	 * @return The list of events
	 * @throws EbMSMessageServiceException
	 */
	@WebResult(name = "messageEvent")
	@WebMethod(operationName = "getUnprocessedMessageEvents")
	List<MessageEvent> getUnprocessedMessageEvents(@WebParam(name = "messageFilter") MessageFilter messageFilter, @WebParam(name = "eventType") MessageEventType[] eventTypes, @WebParam(name = "maxNr") Integer maxNr) throws EbMSMessageServiceException;

	/**
	 * Sets processed to true for all the current events for the message identified by messageId, so that it is no longer returned in the list of
	 * getUnprocessedMessageEvents (and getUnprocessedMessageIds)
	 * 
	 * @param messageId
	 * @throws EbMSMessageServiceException
	 */
	@WebMethod(operationName = "processMessageEvent")
	void processMessageEvent(@WebParam(name = "messageId") @XmlElement(required = true) String messageId) throws EbMSMessageServiceException;