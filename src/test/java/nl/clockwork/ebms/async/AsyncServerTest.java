package nl.clockwork.ebms.async;
/**
 * Copyright 2011 Clockwork
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


import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.oasis_open.committees.ebxml_msg.schema.msg_header_2_0.AckRequested;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.matching.RequestPattern;
import com.github.tomakehurst.wiremock.verification.FindRequestsResult;

import nl.clockwork.ebms.Constants.EbMSMessageStatus;
import nl.clockwork.ebms.client.apache.EbMSHttpClient;
import nl.clockwork.ebms.event.TestMessageRegistry;
import nl.clockwork.ebms.model.EbMSDocument;
import nl.clockwork.ebms.model.EbMSMessageContent;
import nl.clockwork.ebms.model.EbMSMessageContext;
import nl.clockwork.ebms.model.Party;
import nl.clockwork.ebms.model.Role;
import nl.clockwork.ebms.processor.EbMSProcessorException;
import nl.clockwork.ebms.service.CPAServiceException;
import nl.clockwork.ebms.service.EbMSMessageServiceException;

public class AsyncServerTest {
	private static WireMockServer wireMockServer = new WireMockServer(8088);
	private static TestServer ts = new TestServer();
	private static String cpaId = ""; 

	@BeforeClass
	public static void setup() throws Exception
	{
		wireMockServer.start();
		ts.start();
		cpaInsert();
	}

	public static void cpaInsert() throws CPAServiceException, IOException
	{
		File cpaForTest = new File("./resources/CPAs/cpaStubEBF.rm.http.unsigned.xml");
		cpaId = ts.getCPAService().insertCPA(FileUtils.readFileToString(cpaForTest), true);
		assertEquals("cpaStubEBF.rm.http.unsigned", cpaId);
	}
	
	// simple sleep test, to keep the server running for manual testing
	//@Test
	public void sleep() throws InterruptedException
	{
		Thread.sleep(1500000);
	}
	
	@Test
	public void pingTest()
	{
		wireMockServer.stubFor(post(urlEqualTo("/overheidStub"))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type","text/xml; charset=UTF-8")
						.withHeader("SOAPAction", "ebXML")
						.withBody("<ns1:Envelope xmlns:ns1=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns3=\"http://www.oasis-open.org/committees/ebxml-msg/schema/msg-header-2_0.xsd\" xmlns:ns4=\"http://www.w3.org/1999/xlink\" xmlns:ns5=\"http://www.w3.org/2000/09/xmldsig#\">" + 
								"    <ns1:Header>" + 
								"        <ns3:MessageHeader ns1:mustUnderstand=\"1\" ns3:version=\"2.0\">" + 
								"            <ns3:From>" + 
								"                <ns3:PartyId ns3:type=\"urn:osb:oin\">00000000000000000001</ns3:PartyId>" + 
								"            </ns3:From>" + 
								"            <ns3:To>" + 
								"                <ns3:PartyId ns3:type=\"urn:osb:oin\">00000000000000000000</ns3:PartyId>" + 
								"            </ns3:To>" + 
								"            <ns3:CPAId>" + cpaId + "</ns3:CPAId>" + 
								"            <ns3:ConversationId></ns3:ConversationId>" + 
								"            <ns3:Service>urn:oasis:names:tc:ebxml-msg:service</ns3:Service>" + 
								"            <ns3:Action>Pong</ns3:Action>" + 
								"            <ns3:MessageData>" + 
								"                <ns3:MessageId></ns3:MessageId>" + 
								"                <ns3:Timestamp>2018-05-29T10:38:44Z</ns3:Timestamp>" + 
								"            </ns3:MessageData>" + 
								"        </ns3:MessageHeader>" + 
								"    </ns1:Header>" + 
								"    <ns1:Body/>" + 
								"</ns1:Envelope>")));
		
		ts.getEbmsService().ping(cpaId
				, new Party("urn:osb:oin:00000000000000000000", "DIGIPOORT")
				, new Party("urn:osb:oin:00000000000000000001", "OVERHEID"));
	}
	
	
	@Test
	public void pingFailedTest()
	{
		wireMockServer.stubFor(post(urlEqualTo("/overheidStub"))
				.willReturn(aResponse()
						.withStatus(500)
						.withHeader("Content-Type","text/xml; charset=UTF-8")
						.withHeader("SOAPAction", "ebXML")
						.withBody("<ns1:Envelope xmlns:ns1=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns3=\"http://www.oasis-open.org/committees/ebxml-msg/schema/msg-header-2_0.xsd\" xmlns:ns4=\"http://www.w3.org/1999/xlink\" xmlns:ns5=\"http://www.w3.org/2000/09/xmldsig#\">" + 
								"    <ns1:Body>" + 
								"        <ns1:Fault>" + 
								"            <faultcode>ns1:Client</faultcode>" + 
								"            <faultstring>CPA " + cpaId + " not found!</faultstring>" + 
								"        </ns1:Fault>" + 
								"    </ns1:Body>" + 
								"</ns1:Envelope>")));
		try
		{
			ts.getEbmsService().ping(cpaId
					, new Party("urn:osb:oin:00000000000000000000", "DIGIPOORT")
					, new Party("urn:osb:oin:00000000000000000001", "OVERHEID"));
			fail("No exception thrown");
		} catch (EbMSMessageServiceException e)
		{
			assert(e.getMessage().contains("CPA " + cpaId + " not found!"));
		}
	}
	
	@Test
	public void deliveredTest() throws InterruptedException, CPAServiceException, IOException, ParserConfigurationException, SAXException, EbMSProcessorException
	{
		// setup mock response
		String messageId = UUID.randomUUID().toString();
		wireMockServer.stubFor(post(urlEqualTo("/overheidStub"))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type","text/xml; charset=UTF-8")
						.withHeader("SOAPAction", "ebXML")
						.withBody("")));
		
		EbMSMessageContent testMessage = new EbMSMessageContent();
		testMessage.setContext(new EbMSMessageContext());
		testMessage.getContext().setConversationId(UUID.randomUUID().toString());
		testMessage.getContext().setMessageId(messageId);
		testMessage.getContext().setCpaId(cpaId);
		testMessage.getContext().setFromRole(new Role("urn:osb:oin:00000000000000000000", "DIGIPOORT"));
		testMessage.getContext().setToRole(new Role("urn:osb:oin:00000000000000000001", "OVERHEID"));
		testMessage.getContext().setService("urn:osb:services:osb:afleveren:1.1$1.0");
		testMessage.getContext().setAction("afleveren");
//		testMessage.getContext().setTimestamp(new Date());
		ts.getEbmsService().sendMessage(testMessage);
		
		// send ack to ebms server (from "stub")
		EbMSHttpClient httpc = new EbMSHttpClient();
		Document message = stringToDocument("<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:eb=\"http://www.oasis-open.org/committees/ebxml-msg/schema/msg-header-2_0.xsd\" xmlns:ns5=\"http://www.w3.org/2000/09/xmldsig#\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">\r\n" + 
				"    <soap:Header>\r\n" + 
				"        <eb:MessageHeader eb:version=\"2.0\" soap:mustUnderstand=\"1\">\r\n" + 
				"            <eb:From>\r\n" + 
				"                <eb:PartyId eb:type=\"urn:osb:oin\">00000000000000000001</eb:PartyId>\r\n" + 
				"            </eb:From>\r\n" + 
				"            <eb:To>\r\n" + 
				"                <eb:PartyId eb:type=\"urn:osb:oin\">00000000000000000000</eb:PartyId>\r\n" + 
				"            </eb:To>\r\n" + 
				"            <eb:CPAId>" + cpaId + "</eb:CPAId>\r\n" + 
				"            <eb:ConversationId>" + messageId + "</eb:ConversationId>\r\n" + 
				"            <eb:Service>urn:oasis:names:tc:ebxml-msg:service</eb:Service>\r\n" + 
				"            <eb:Action>Acknowledgment</eb:Action>\r\n" + 
				"            <eb:MessageData>\r\n" + 
				"                <eb:MessageId>" + UUID.randomUUID().toString() + "@localhost</eb:MessageId>\r\n" + 
				"                <eb:Timestamp>2018-07-03T22:18:03Z</eb:Timestamp>\r\n" + 
				"                <eb:RefToMessageId>"+ messageId + "@localhost</eb:RefToMessageId>\r\n" + 
				"            </eb:MessageData>\r\n" + 
				"        </eb:MessageHeader>\r\n" + 
				"        <eb:Acknowledgment eb:version=\"2.0\" soap:actor=\"urn:oasis:names:tc:ebxml-msg:actor:toPartyMSH\" soap:mustUnderstand=\"1\">\r\n" + 
				"            <eb:Timestamp>2018-07-03T22:18:03Z</eb:Timestamp>\r\n" + 
				"            <eb:RefToMessageId>"+ messageId +"@localhost</eb:RefToMessageId>\r\n" + 
				"            <eb:From>\r\n" + 
				"                <eb:PartyId eb:type=\"urn:osb:oin\">00000000000000000001</eb:PartyId>\r\n" + 
				"            </eb:From>\r\n" + 
				"        </eb:Acknowledgment>\r\n" + 
				"    </soap:Header>\r\n" + 
				"    <soap:Body/>\r\n" + 
				"</soap:Envelope>");
		EbMSDocument document = new EbMSDocument("1234", message);
		EbMSDocument response = httpc.sendMessage(ts.getEbmsEndpoint(), document);
	
		// response sent, now wait for delivered status
		assertEquals(EbMSMessageStatus.DELIVERED, 
				TestMessageRegistry.getInstance().waitFor(testMessage.getContext().getMessageId()));
		
	}
	
	@Test
	public void deliveryFailTest() throws InterruptedException, CPAServiceException, IOException, ParserConfigurationException, SAXException, EbMSProcessorException
	{
		// setup mock response
		String messageId = UUID.randomUUID().toString();
		wireMockServer.resetMappings();
		wireMockServer.stubFor(post(urlEqualTo("/overheidStub"))
				.willReturn(aResponse()
						.withStatus(500)
						.withBody("")));
		
		EbMSMessageContent testMessage = new EbMSMessageContent();
		testMessage.setContext(new EbMSMessageContext());
		testMessage.getContext().setConversationId(UUID.randomUUID().toString());
		testMessage.getContext().setMessageId(messageId);
		testMessage.getContext().setCpaId(cpaId);
		testMessage.getContext().setFromRole(new Role("urn:osb:oin:00000000000000000000", "DIGIPOORT"));
		testMessage.getContext().setToRole(new Role("urn:osb:oin:00000000000000000001", "OVERHEID"));
		testMessage.getContext().setService("urn:osb:services:osb:afleveren:1.1$1.0");
		testMessage.getContext().setAction("afleveren");
//		testMessage.getContext().setTimestamp(new Date());
		ts.getEbmsService().sendMessage(testMessage);
		
		// send MessageError to ebms server (from "stub")
		EbMSHttpClient httpc = new EbMSHttpClient();
		Document message = stringToDocument("<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:eb=\"http://www.oasis-open.org/committees/ebxml-msg/schema/msg-header-2_0.xsd\" xmlns:ns5=\"http://www.w3.org/2000/09/xmldsig#\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">\r\n" + 
				"    <soap:Header>\r\n" + 
				"        <eb:MessageHeader eb:version=\"2.0\" soap:mustUnderstand=\"1\">\r\n" + 
				"            <eb:From>\r\n" + 
				"                <eb:PartyId eb:type=\"urn:osb:oin\">00000000000000000001</eb:PartyId>\r\n" + 
				"            </eb:From>\r\n" + 
				"            <eb:To>\r\n" + 
				"                <eb:PartyId eb:type=\"urn:osb:oin\">00000000000000000000</eb:PartyId>\r\n" + 
				"            </eb:To>\r\n" + 
				"            <eb:CPAId>" + cpaId + "</eb:CPAId>\r\n" + 
				"            <eb:ConversationId>" + messageId + "</eb:ConversationId>\r\n" + 
				"            <eb:Service>urn:oasis:names:tc:ebxml-msg:service</eb:Service>\r\n" + 
				"            <eb:Action>MessageError</eb:Action>\r\n" + 
				"            <eb:MessageData>\r\n" + 
				"                <eb:MessageId>" + UUID.randomUUID().toString() + "@localhost</eb:MessageId>\r\n" + 
				"                <eb:Timestamp>2018-07-03T22:18:03Z</eb:Timestamp>\r\n" + 
				"                <eb:RefToMessageId>"+ messageId + "@localhost</eb:RefToMessageId>\r\n" + 
				"            </eb:MessageData>\r\n" + 
				"        </eb:MessageHeader>\r\n" + 
				"    </soap:Header>\r\n" + 
				"    <soap:Body/>\r\n" + 
				"</soap:Envelope>");
		EbMSDocument document = new EbMSDocument("1234", message);
		EbMSDocument response = httpc.sendMessage(ts.getEbmsEndpoint(), document);

		assertEquals(EbMSMessageStatus.DELIVERY_FAILED, 
				TestMessageRegistry.getInstance().waitFor(testMessage.getContext().getMessageId()));
	}

	private Document stringToDocument (String message) throws ParserConfigurationException, SAXException, IOException
	{
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		return dBuilder.parse(new ByteArrayInputStream(message.getBytes()));	
	}
	
	@Test
	public void unknownAckTest() throws ParserConfigurationException, SAXException, IOException
	{
		String messageId = UUID.randomUUID().toString();

		// send ack to ebms server (from "stub"), but there's no AckRequest in DB..
		EbMSHttpClient httpc = new EbMSHttpClient();
		Document message = stringToDocument("<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:eb=\"http://www.oasis-open.org/committees/ebxml-msg/schema/msg-header-2_0.xsd\" xmlns:ns5=\"http://www.w3.org/2000/09/xmldsig#\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">" + 
				"    <soap:Header>" + 
				"        <eb:MessageHeader eb:version=\"2.0\" soap:mustUnderstand=\"1\">" + 
				"            <eb:From><eb:PartyId eb:type=\"urn:osb:oin\">00000000000000000001</eb:PartyId></eb:From>" + 
				"            <eb:To><eb:PartyId eb:type=\"urn:osb:oin\">00000000000000000000</eb:PartyId></eb:To>" + 
				"            <eb:CPAId>cpaStubEBF.be.http.unsigned</eb:CPAId>" + 
				"            <eb:ConversationId>" + messageId + "</eb:ConversationId>" + 
				"            <eb:Service>urn:oasis:names:tc:ebxml-msg:service</eb:Service>" + 
				"            <eb:Action>Acknowledgment</eb:Action>" + 
				"            <eb:MessageData>" + 
				"                <eb:MessageId>" + UUID.randomUUID().toString() + "@localhost</eb:MessageId>" + 
				"                <eb:Timestamp>2018-04-18T11:10:17Z</eb:Timestamp>" + 
				"                <eb:RefToMessageId>512425b0-aee3-4fdc-9e4e-d2ce337b12b1@localhost</eb:RefToMessageId>" + 
				"            </eb:MessageData>" + 
				"        </eb:MessageHeader>" + 
				"        <eb:Acknowledgment eb:version=\"2.0\" soap:actor=\"urn:oasis:names:tc:ebxml-msg:actor:toPartyMSH\" soap:mustUnderstand=\"1\">" + 
				"            <eb:Timestamp>2018-04-18T11:10:17Z</eb:Timestamp>" + 
				"            <eb:RefToMessageId>" + messageId + "@localhost</eb:RefToMessageId>" + 
				"            <eb:From>" + 
				"                <eb:PartyId eb:type=\"urn:osb:oin\">00000000000000000001</eb:PartyId>" + 
				"            </eb:From>" + 
				"        </eb:Acknowledgment>" + 
				"    </soap:Header>" + 
				"    <soap:Body/>" + 
				"</soap:Envelope>");
		EbMSDocument document = new EbMSDocument("1234", message);
		try {
			EbMSDocument response = httpc.sendMessage(ts.getEbmsEndpoint(), document);
			fail("missing exception");
		} catch (EbMSProcessorException e) {
			assertTrue(e.getMessage().contains("StatusCode: 500"));
		}
	}

	@Test
	public void clientPing() throws ParserConfigurationException, SAXException, IOException, EbMSProcessorException, InterruptedException
	{
		String messageId = UUID.randomUUID().toString();
		
		// accept Pong
		wireMockServer.resetMappings();
		wireMockServer.stubFor(post(urlEqualTo("/overheidStub"))
				.willReturn(aResponse()
						.withStatus(200)
						.withBody("")));

		// send ack to ebms server (from "stub")
		EbMSHttpClient httpc = new EbMSHttpClient();
		Document message = stringToDocument("<ns1:Envelope xmlns:ns1=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns3=\"http://www.oasis-open.org/committees/ebxml-msg/schema/msg-header-2_0.xsd\" xmlns:ns4=\"http://www.w3.org/1999/xlink\" xmlns:ns5=\"http://www.w3.org/2000/09/xmldsig#\">\r\n" + 
				"    <ns1:Header>\r\n" + 
				"        <ns3:MessageHeader ns1:mustUnderstand=\"1\" ns3:version=\"2.0\">\r\n" + 
				"            <ns3:From>\r\n" + 
				"                <ns3:PartyId ns3:type=\"urn:osb:oin\">00000000000000000001</ns3:PartyId>\r\n" + 
				"            </ns3:From>\r\n" + 
				"            <ns3:To>\r\n" + 
				"                <ns3:PartyId ns3:type=\"urn:osb:oin\">00000000000000000000</ns3:PartyId>\r\n" + 
				"            </ns3:To>\r\n" + 
				"            <ns3:CPAId>" + cpaId + "</ns3:CPAId>\r\n" + 
				"            <ns3:ConversationId>" + messageId + "</ns3:ConversationId>\r\n" + 
				"            <ns3:Service>urn:oasis:names:tc:ebxml-msg:service</ns3:Service>\r\n" + 
				"            <ns3:Action>Ping</ns3:Action>\r\n" + 
				"            <ns3:MessageData>\r\n" + 
				"                <ns3:MessageId>" + messageId + "@localhost</ns3:MessageId>\r\n" + 
				"                <ns3:Timestamp>2018-07-02T22:02:38Z</ns3:Timestamp>\r\n" + 
				"            </ns3:MessageData>\r\n" + 
				"        </ns3:MessageHeader>\r\n" + 
				"    </ns1:Header>\r\n" + 
				"    <ns1:Body/>\r\n" + 
				"</ns1:Envelope>");
		EbMSDocument document = new EbMSDocument("4321", message);
		httpc.sendMessage(ts.getEbmsEndpoint(), document); // Fails in log due to CPA id not available ?!
		
		Thread.sleep(3000);
		// check if Pong was received
		FindRequestsResult result = wireMockServer.findRequestsMatching(RequestPattern.everything());
		assertEquals(1, result.getRequests().size());
		assert(result.getRequests().get(0).getBodyAsString().contains("Pong"));
	}
	
	@AfterClass
	public static void tearDown() throws Exception
	{
		if (ts != null)
			ts.stop();
		if (wireMockServer != null)
			wireMockServer.stop();
	}
}
