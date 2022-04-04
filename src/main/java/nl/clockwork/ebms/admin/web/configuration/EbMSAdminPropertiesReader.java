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
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.val;
import lombok.experimental.FieldDefaults;
import nl.clockwork.ebms.admin.web.configuration.ConsolePropertiesFormPanel.ConsolePropertiesFormData;
import nl.clockwork.ebms.admin.web.configuration.CorePropertiesFormPanel.CorePropertiesFormData;
import nl.clockwork.ebms.admin.web.configuration.EbMSAdminPropertiesPage.EbMSAdminPropertiesFormData;
import nl.clockwork.ebms.admin.web.configuration.EncryptionPropertiesFormPanel.EncryptionPropertiesFormData;
import nl.clockwork.ebms.admin.web.configuration.HttpPropertiesFormPanel.HttpPropertiesFormData;
import nl.clockwork.ebms.admin.web.configuration.JdbcPropertiesFormPanel.JdbcPropertiesFormData;
import nl.clockwork.ebms.admin.web.configuration.ProxyPropertiesFormPanel.ProxyPropertiesFormData;
import nl.clockwork.ebms.admin.web.configuration.ServicePropertiesFormPanel.ServicePropertiesFormData;
import nl.clockwork.ebms.admin.web.configuration.SignaturePropertiesFormPanel.SignaturePropertiesFormData;
import nl.clockwork.ebms.admin.web.configuration.SslPropertiesFormPanel.SslPropertiesFormData;
import nl.clockwork.ebms.client.EbMSHttpClientFactory.EbMSHttpClientType;
import nl.clockwork.ebms.event.listener.EventListenerConfig.EventListenerType;
import nl.clockwork.ebms.security.KeyStoreType;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class EbMSAdminPropertiesReader
{
	Properties properties;

	public EbMSAdminPropertiesFormData read(PropertiesType propertiesType) throws IOException
	{
		val result = new EbMSAdminPropertiesFormData();
		switch (propertiesType)
		{
			case EBMS_ADMIN:
				read(properties,result.getConsoleProperties());
				read(properties,result.getCoreProperties());
				read(properties,result.getServiceProperties());
				read(properties,result.getJdbcProperties());
				break;
			case EBMS_ADMIN_EMBEDDED:
				read(properties,result.getConsoleProperties());
				read(properties,result.getCoreProperties());
				read(properties,result.getHttpProperties());
				read(properties,result.getSignatureProperties());
				read(properties,result.getEncryptionProperties());
				read(properties,result.getJdbcProperties());
				break;
		}
		return result;
	}
	
	private void read(Properties properties, ConsolePropertiesFormData consoleProperties) throws MalformedURLException
	{
		consoleProperties.setMaxItemsPerPage(Integer.parseInt(properties.getProperty("maxItemsPerPage")));
	}

	private void read(Properties properties, ServicePropertiesFormData serviceProperties) throws MalformedURLException
	{
		serviceProperties.setUrl(properties.getProperty("service.ebms.url"));
	}

	private void read(Properties properties, CorePropertiesFormData coreProperties) throws MalformedURLException
	{
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

	private void read(Properties properties, HttpPropertiesFormData httpProperties) throws MalformedURLException
	{
		httpProperties.setHost(properties.getProperty("ebms.host"));
		httpProperties.setPort(properties.getProperty("ebms.port") == null ? null : new Integer(properties.getProperty("ebms.port")));
		httpProperties.setPath(properties.getProperty("ebms.path"));
		httpProperties.setSsl(new Boolean(properties.getProperty("ebms.ssl")));
		httpProperties.setProxy(!StringUtils.isEmpty(properties.getProperty("http.proxy.host")));
		httpProperties.setChunkedStreamingMode(new Boolean(properties.getProperty("http.chunkedStreamingMode")));
		httpProperties.setBase64Writer(new Boolean(properties.getProperty("http.base64Writer")));
		if (httpProperties.isSsl())
			read(properties,httpProperties.getSslProperties());
		if (httpProperties.isProxy())
			read(properties,httpProperties.getProxyProperties());
	}

	private void read(Properties properties, SslPropertiesFormData sslProperties) throws MalformedURLException
	{
		sslProperties.setOverrideDefaultProtocols(!StringUtils.isEmpty(properties.getProperty("https.protocols")));
		sslProperties.setEnabledProtocols(Arrays.asList(StringUtils.stripAll(StringUtils.split(properties.getProperty("https.protocols",""),','))));
		sslProperties.setOverrideDefaultCipherSuites(!StringUtils.isEmpty(properties.getProperty("https.cipherSuites")));
		sslProperties.setEnabledCipherSuites(Arrays.asList(StringUtils.stripAll(StringUtils.split(properties.getProperty("https.cipherSuites",""),','))));
		sslProperties.setRequireClientAuthentication(new Boolean(properties.getProperty("https.requireClientAuthentication")));
		sslProperties.setVerifyHostnames(new Boolean(properties.getProperty("https.verifyHostnames")));
		sslProperties.getKeystoreProperties().setType(KeyStoreType.valueOf(properties.getProperty("keystore.type","PKCS12").toUpperCase()));
		sslProperties.getKeystoreProperties().setUri(properties.getProperty("keystore.path"));
		sslProperties.getKeystoreProperties().setPassword(properties.getProperty("keystore.password"));
		sslProperties.getKeystoreProperties().setDefaultAlias(properties.getProperty("keystore.defaultAlias"));
		sslProperties.getClientKeystoreProperties().setType(KeyStoreType.valueOf(properties.getProperty("client.keystore.type","PKCS12").toUpperCase()));
		sslProperties.getClientKeystoreProperties().setUri(properties.getProperty("client.keystore.path"));
		sslProperties.getClientKeystoreProperties().setPassword(properties.getProperty("client.keystore.password"));
		sslProperties.getClientKeystoreProperties().setDefaultAlias(properties.getProperty("client.keystore.defaultAlias"));
		sslProperties.getTruststoreProperties().setType(KeyStoreType.valueOf(properties.getProperty("truststore.type","PKCS12").toUpperCase()));
		sslProperties.getTruststoreProperties().setUri(properties.getProperty("truststore.path"));
		sslProperties.getTruststoreProperties().setPassword(properties.getProperty("truststore.password"));
	}

	private void read(Properties properties, ProxyPropertiesFormData proxyProperties) throws MalformedURLException
	{
		proxyProperties.setHost(StringUtils.defaultString(properties.getProperty("http.proxy.host")));
		proxyProperties.setPort(Integer.parseInt(properties.getProperty("http.proxy.port")));
		proxyProperties.setNonProxyHosts(StringUtils.defaultString(properties.getProperty("http.proxy.nonProxyHosts")));
		proxyProperties.setUsername(properties.getProperty("http.proxy.username"));
		proxyProperties.setPassword(properties.getProperty("http.proxy.password"));
	}

	private void read(Properties properties, SignaturePropertiesFormData signatureProperties) throws MalformedURLException
	{
		signatureProperties.setSigning(!StringUtils.isEmpty(properties.getProperty("signature.keystore.path")));
		if (signatureProperties.isSigning())
		{
			signatureProperties.getKeystoreProperties().setType(KeyStoreType.valueOf(properties.getProperty("signature.keystore.type","PKCS12").toUpperCase()));
			signatureProperties.getKeystoreProperties().setUri(properties.getProperty("signature.keystore.path"));
			signatureProperties.getKeystoreProperties().setPassword(properties.getProperty("signature.keystore.password"));
		}
	}

	private void read(Properties properties, EncryptionPropertiesFormData encryptionProperties) throws MalformedURLException
	{
		encryptionProperties.setEncryption(!StringUtils.isEmpty(properties.getProperty("encryption.keystore.path")));
		if (encryptionProperties.isEncryption())
		{
			encryptionProperties.getKeystoreProperties().setType(KeyStoreType.valueOf(properties.getProperty("encryption.keystore.type","PKCS12").toUpperCase()));
			encryptionProperties.getKeystoreProperties().setUri(properties.getProperty("encryption.keystore.path"));
			encryptionProperties.getKeystoreProperties().setPassword(properties.getProperty("encryption.keystore.password"));
		}
	}

	private void read(Properties properties, JdbcPropertiesFormData jdbcProperties) throws MalformedURLException
	{
		jdbcProperties.setDriver(JdbcDriver.getJdbcDriver(properties.getProperty("ebms.jdbc.driverClassName")).orElse(null));
		//jdbcProperties.setJdbcURL(properties.getProperty("ebms.jdbc.url"));
		Utils.parseJdbcURL(properties.getProperty("ebms.jdbc.url"),jdbcProperties);
		jdbcProperties.setUsername(properties.getProperty("ebms.jdbc.username"));
		jdbcProperties.setPassword(properties.getProperty("ebms.jdbc.password"));
	}
}
