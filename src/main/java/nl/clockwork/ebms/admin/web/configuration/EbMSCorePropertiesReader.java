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
import nl.clockwork.ebms.admin.web.configuration.CorePropertiesFormPanel.CorePropertiesFormModel;
import nl.clockwork.ebms.admin.web.configuration.EbMSCorePropertiesPage.EbMSCorePropertiesFormModel;
import nl.clockwork.ebms.admin.web.configuration.EncryptionPropertiesFormPanel.EncryptionPropertiesFormModel;
import nl.clockwork.ebms.admin.web.configuration.HttpPropertiesFormPanel.HttpPropertiesFormModel;
import nl.clockwork.ebms.admin.web.configuration.JdbcPropertiesFormPanel.JdbcPropertiesFormModel;
import nl.clockwork.ebms.admin.web.configuration.ProxyPropertiesFormPanel.ProxyPropertiesFormModel;
import nl.clockwork.ebms.admin.web.configuration.SignaturePropertiesFormPanel.SignaturePropertiesFormModel;
import nl.clockwork.ebms.admin.web.configuration.SslPropertiesFormPanel.SslPropertiesFormModel;
import nl.clockwork.ebms.client.EbMSHttpClientFactory.EbMSHttpClientType;
import nl.clockwork.ebms.event.EventListenerFactory.EventListenerType;

import org.apache.commons.lang3.StringUtils;

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
		read(properties,ebMSCoreProperties.getCoreProperties());
		read(properties,ebMSCoreProperties.getHttpProperties());
		read(properties,ebMSCoreProperties.getSignatureProperties());
		read(properties,ebMSCoreProperties.getEncryptionProperties());
		read(properties,ebMSCoreProperties.getJdbcProperties());
	}
	
	protected void read(Properties properties, CorePropertiesFormModel coreProperties) throws MalformedURLException
	{
		coreProperties.setDigipoortPatch(Boolean.parseBoolean(properties.getProperty("patch.digipoort.enable")));
		coreProperties.setOraclePatch(Boolean.parseBoolean(properties.getProperty("patch.oracle.enable")));
		coreProperties.setCleoPatch(Boolean.parseBoolean(properties.getProperty("patch.cleo.enable")));
		coreProperties.setHttpClient(properties.getProperty("http.client") != null ? EbMSHttpClientType.valueOf(properties.getProperty("http.client")) : null);
		coreProperties.setEventListener(properties.getProperty("eventListener.type") != null ? EventListenerType.valueOf(properties.getProperty("eventListener.type")) : null);
		coreProperties.setJmsBrokerUrl(properties.getProperty("jms.brokerURL"));
		coreProperties.setJmsVirtualTopics(Boolean.parseBoolean(properties.getProperty("jms.virtualTopics")));
		coreProperties.setStartEmbeddedBroker(Boolean.parseBoolean(properties.getProperty("jms.broker.start")));
		coreProperties.setActiveMQConfigFile(properties.getProperty("jms.broker.config"));
		coreProperties.setDeleteMessageContentOnProcessed(Boolean.parseBoolean(properties.getProperty("ebmsMessage.deleteContentOnProcessed")));
		coreProperties.setStoreDuplicateMessage(Boolean.parseBoolean(properties.getProperty("ebmsMessage.storeDuplicate")));
		coreProperties.setStoreDuplicateMessageContent(Boolean.parseBoolean(properties.getProperty("ebmsMessage.storeDuplicateContent")));
	}

	protected void read(Properties properties, HttpPropertiesFormModel httpProperties) throws MalformedURLException
	{
		httpProperties.setHost(properties.getProperty("ebms.host"));
		httpProperties.setPort(properties.getProperty("ebms.port") == null ? null : new Integer(properties.getProperty("ebms.port")));
		httpProperties.setPath(properties.getProperty("ebms.path"));
		httpProperties.setSsl(new Boolean(properties.getProperty("ebms.ssl")));
		httpProperties.setProxy(!StringUtils.isEmpty(properties.getProperty("http.proxy.host")));
		httpProperties.setChunkedStreamingMode(new Boolean(properties.getProperty("http.chunkedStreamingMode")));
		httpProperties.setBase64Writer(new Boolean(properties.getProperty("http.base64Writer")));
		if (httpProperties.getSsl())
			read(properties,httpProperties.getSslProperties());
		if (httpProperties.getProxy())
			read(properties,httpProperties.getProxyProperties());
	}

	protected void read(Properties properties, SslPropertiesFormModel sslProperties) throws MalformedURLException
	{
		sslProperties.setOverrideDefaultProtocols(!StringUtils.isEmpty(properties.getProperty("https.protocols")));
		sslProperties.setEnabledProtocols(Arrays.asList(StringUtils.stripAll(StringUtils.split(properties.getProperty("https.protocols",""),','))));
		sslProperties.setOverrideDefaultCipherSuites(!StringUtils.isEmpty(properties.getProperty("https.cipherSuites")));
		sslProperties.setEnabledCipherSuites(Arrays.asList(StringUtils.stripAll(StringUtils.split(properties.getProperty("https.cipherSuites",""),','))));
		sslProperties.setRequireClientAuthentication(new Boolean(properties.getProperty("https.requireClientAuthentication")));
		sslProperties.setVerifyHostnames(new Boolean(properties.getProperty("https.verifyHostnames")));
		sslProperties.setValidate(new Boolean(properties.getProperty("https.validate")));
		sslProperties.getKeystoreProperties().setUri(properties.getProperty("keystore.path"));
		sslProperties.getKeystoreProperties().setPassword(properties.getProperty("keystore.password"));
		sslProperties.getClientKeystoreProperties().setUri(properties.getProperty("client.keystore.path"));
		sslProperties.getClientKeystoreProperties().setPassword(properties.getProperty("client.keystore.password"));
		sslProperties.getTruststoreProperties().setUri(properties.getProperty("truststore.path"));
		sslProperties.getTruststoreProperties().setPassword(properties.getProperty("truststore.password"));
	}

	protected void read(Properties properties, ProxyPropertiesFormModel proxyProperties) throws MalformedURLException
	{
		proxyProperties.setHost(StringUtils.defaultString(properties.getProperty("http.proxy.host")));
		proxyProperties.setPort(Integer.getInteger(properties.getProperty("http.proxy.port")));
		proxyProperties.setNonProxyHosts(StringUtils.defaultString(properties.getProperty("http.proxy.nonProxyHosts")));
		proxyProperties.setUsername(properties.getProperty("http.proxy.username"));
		proxyProperties.setPassword(properties.getProperty("http.proxy.password"));
	}

	protected void read(Properties properties, SignaturePropertiesFormModel signatureProperties) throws MalformedURLException
	{
		signatureProperties.setSigning(!StringUtils.isEmpty(properties.getProperty("signature.keystore.path")));
		signatureProperties.getKeystoreProperties().setUri(properties.getProperty("signature.keystore.path"));
		signatureProperties.getKeystoreProperties().setPassword(properties.getProperty("signature.keystore.password"));
	}

	protected void read(Properties properties, EncryptionPropertiesFormModel encryptionProperties) throws MalformedURLException
	{
		encryptionProperties.setEncryption(!StringUtils.isEmpty(properties.getProperty("encryption.keystore.path")));
		encryptionProperties.getKeystoreProperties().setUri(properties.getProperty("encryption.keystore.path"));
		encryptionProperties.getKeystoreProperties().setPassword(properties.getProperty("encryption.keystore.password"));
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
