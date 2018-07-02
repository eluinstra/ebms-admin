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
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.tomakehurst.wiremock.WireMockServer;

import nl.clockwork.ebms.Constants.EbMSMessageStatus;
import nl.clockwork.ebms.event.TestMessageRegistry;
import nl.clockwork.ebms.model.EbMSMessageContent;
import nl.clockwork.ebms.model.EbMSMessageContext;
import nl.clockwork.ebms.model.Party;
import nl.clockwork.ebms.model.Role;
import nl.clockwork.ebms.service.CPAServiceException;

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

//	@Test
	public static void cpaInsert() throws CPAServiceException, IOException
	{
		File cpaForTest = new File("./resources/CPAs/cpaStubEBF.be.http.unsigned.xml");
		// load CPA using soap client
		cpaId = ts.getCPAService().insertCPA(FileUtils.readFileToString(cpaForTest), true);
		assertEquals("cpaStubEBF.be.http.unsigned", cpaId);
	}
	
	@Test
	public void pingTest()
	{
		Party fromParty = new Party("urn:osb:oin:00000000000000000000", "DIGIPOORT");
		Party toParty = new Party("urn:osb:oin:00000000000000000001", "OVERHEID");
		
		// setup mock response
		wireMockServer.stubFor(post(urlEqualTo("/overheidStub"))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type","text/xml; charset=UTF-8")
						.withHeader("SOAPAction", "ebXML")
						.withBody("<ns1:Envelope xmlns:ns1=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns3=\"http://www.oasis-open.org/committees/ebxml-msg/schema/msg-header-2_0.xsd\" xmlns:ns4=\"http://www.w3.org/1999/xlink\" xmlns:ns5=\"http://www.w3.org/2000/09/xmldsig#\">\r\n" + 
								"    <ns1:Header>\r\n" + 
								"        <ns3:MessageHeader ns1:mustUnderstand=\"1\" ns3:version=\"2.0\">\r\n" + 
								"            <ns3:From>\r\n" + 
								"                <ns3:PartyId ns3:type=\"urn:osb:oin\">00000000000000000001</ns3:PartyId>\r\n" + 
								"            </ns3:From>\r\n" + 
								"            <ns3:To>\r\n" + 
								"                <ns3:PartyId ns3:type=\"urn:osb:oin\">00000000000000000000</ns3:PartyId>\r\n" + 
								"            </ns3:To>\r\n" + 
								"            <ns3:CPAId>" + cpaId + "</ns3:CPAId>\r\n" + 
								"            <ns3:ConversationId>b0c9e910-e765-4883-b1a3-12e29a5fc7f1</ns3:ConversationId>\r\n" + 
								"            <ns3:Service>urn:oasis:names:tc:ebxml-msg:service</ns3:Service>\r\n" + 
								"            <ns3:Action>Pong</ns3:Action>\r\n" + 
								"            <ns3:MessageData>\r\n" + 
								"                <ns3:MessageId>b0c9e910-e765-4883-b1a3-12e29a5fc7f1@localhost</ns3:MessageId>\r\n" + 
								"                <ns3:Timestamp>2018-05-29T10:38:44Z</ns3:Timestamp>\r\n" + 
								"            </ns3:MessageData>\r\n" + 
								"        </ns3:MessageHeader>\r\n" + 
								"    </ns1:Header>\r\n" + 
								"    <ns1:Body/>\r\n" + 
								"</ns1:Envelope>")));
		
		try
		{
			ts.getEbmsService().ping(cpaId, fromParty, toParty);
		} catch (Exception e)
		{
			fail("Ping exception caught " + e.getMessage());
		}	
	}
	
	
	@Test
	public void deliveredTest() throws InterruptedException, CPAServiceException, IOException
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
		assertEquals(EbMSMessageStatus.DELIVERED, 
				TestMessageRegistry.getInstance().waitFor(testMessage.getContext().getMessageId()));
	}
	
	@Test
	public void deliveryFailTest() throws InterruptedException, CPAServiceException, IOException
	{
		// setup mock response
		String messageId = UUID.randomUUID().toString();
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
		assertEquals(EbMSMessageStatus.DELIVERY_FAILED, 
				TestMessageRegistry.getInstance().waitFor(testMessage.getContext().getMessageId()));
	}
	
	public void acknowledgementTest()
	{
		// send ack to ebms server
		
		// how to verify ??
	}

	
	public void unknownAckTest()
	{
		// send ack of unknown message to ebms server
		
		// how to verify ??
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
