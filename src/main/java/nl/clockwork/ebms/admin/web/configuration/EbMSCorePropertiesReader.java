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
package nl.clockwork.ebms.admin.web.configuration;

import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Properties;

import nl.clockwork.ebms.admin.web.configuration.Constants.JdbcDriver;
import nl.clockwork.ebms.admin.web.configuration.EbMSCorePropertiesPage.EbMSCorePropertiesFormModel;
import nl.clockwork.ebms.admin.web.configuration.HttpPropertiesFormPanel.HttpPropertiesFormModel;
import nl.clockwork.ebms.admin.web.configuration.JdbcPropertiesFormPanel.JdbcPropertiesFormModel;
import nl.clockwork.ebms.admin.web.configuration.SignaturePropertiesFormPanel.SignaturePropertiesFormModel;
import nl.clockwork.ebms.admin.web.configuration.SslPropertiesFormPanel.SslPropertiesFormModel;

import org.apache.commons.lang.StringUtils;

public class EbMSCorePropertiesReader
{
	protected Reader reader;

	public EbMSCorePropertiesReader(Reader reader)
	{
		this.reader = reader;
	}

	public void read(EbMSCorePropertiesFormModel ebMSCoreProperties) throws IOException
	{
		Properties properties = new Properties();
		properties.load(reader);
		read(properties,ebMSCoreProperties.getHttpProperties());
		read(properties,ebMSCoreProperties.getSignatureProperties());
		read(properties,ebMSCoreProperties.getJdbcProperties());
	}
	
	protected void read(Properties properties, HttpPropertiesFormModel httpProperties) throws MalformedURLException
	{
		//httpProperties.setHost(properties.getProperty("ebms.host"));
		httpProperties.setPort(properties.getProperty("ebms.port") == null ? null : new Integer(properties.getProperty("ebms.port")));
		httpProperties.setPath(properties.getProperty("ebms.path"));
		httpProperties.setSsl(new Boolean(properties.getProperty("ebms.ssl")));
		httpProperties.setChunkedStreamingMode(new Boolean(properties.getProperty("http.chunkedStreamingMode")));
		if (httpProperties.getSsl())
			read(properties,httpProperties.getSslProperties());
	}

	protected void read(Properties properties, SslPropertiesFormModel sslProperties) throws MalformedURLException
	{
		sslProperties.setAllowedCipherSuites(Arrays.asList(StringUtils.stripAll(StringUtils.split(properties.getProperty("https.allowedCipherSuites"),','))));
		sslProperties.setRequireClientAuthentication(new Boolean(properties.getProperty("https.requireClientAuthentication")));
		sslProperties.setVerifyHostnames(new Boolean(properties.getProperty("https.verifyHostnames")));
		sslProperties.getKeystoreProperties().setUri(properties.getProperty("keystore.path"));
		sslProperties.getKeystoreProperties().setPassword(properties.getProperty("keystore.password"));
		sslProperties.getTruststoreProperties().setUri(properties.getProperty("truststore.path"));
		sslProperties.getTruststoreProperties().setPassword(properties.getProperty("truststore.password"));
	}

	protected void read(Properties properties, SignaturePropertiesFormModel signatureProperties) throws MalformedURLException
	{
		if (signatureProperties.getSigning())
		{
			signatureProperties.getKeystoreProperties().setUri(properties.getProperty("signature.keystore.path"));
			signatureProperties.getKeystoreProperties().setPassword(properties.getProperty("signature.keystore.password"));
		}
	}

	protected void read(Properties properties, JdbcPropertiesFormModel jdbcProperties) throws MalformedURLException
	{
		jdbcProperties.setDriver(JdbcDriver.getJdbcDriver(properties.getProperty("ebms.jdbc.driverClassName")));
		//jdbcProperties.setJdbcURL(properties.getProperty("ebms.jdbc.url"));
		Utils.parseJdbcURL(properties.getProperty("ebms.jdbc.url"),jdbcProperties);
		jdbcProperties.setUsername(properties.getProperty("ebms.jdbc.username"));
		jdbcProperties.setPassword(properties.getProperty("ebms.jdbc.password"));
		//jdbcProperties.setPreferredTestQuery(properties.getProperty("ebms.pool.preferredTestQuery"));
	}

}
