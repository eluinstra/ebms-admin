package nl.clockwork.ebms.sync;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.model.support.PropertiesMap;
import com.eviware.soapui.model.testsuite.TestCase;
import com.eviware.soapui.model.testsuite.TestRunner;
import com.eviware.soapui.model.testsuite.TestRunner.Status;

import nl.clockwork.ebms.admin.StartEmbedded;

import com.eviware.soapui.model.testsuite.TestSuite;

// attempt to run soapui tests from junit (not yet finished!!)
public class SyncSoapUITest
{
	@BeforeClass
	public static void setup() throws Exception
	{
		Thread t = new Thread() {
			@Override
			public void run() {
				super.run();
				try {
					//StartEmbedded.main(new String[] {"-soap", "-hsqldb", "-port", "8080"});
					StartEmbedded.main(new String[] {"-hsqldb", "-soap"});
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		t.start();
	}
	
	@Test
	public void testRunner() throws Exception {
			Thread.sleep(10000);
		  WsdlProject project = new WsdlProject( "src/test/resources/EbMS-soapui-project.xml" ); 
		  TestSuite testSuite = project.getTestSuiteByName( "TestSuite EbMS Sync" ); 
		  TestCase testCase = testSuite.getTestCaseByName( "TestCase EbMS" );
		  
		  // create empty properties and run synchronously
		  TestRunner runner = testCase.run( new PropertiesMap(), false ); 
		  assertEquals( Status.FINISHED, runner.getStatus() ); 
	}

	@AfterClass
	public static void tearDown() throws Exception
	{

	}
}
