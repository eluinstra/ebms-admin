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
import java.io.Writer;
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

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class EbMSAdminPropertiesWriter
{
	Writer writer;
	boolean enableSslOverridePropeties;

	public void write(EbMSAdminPropertiesFormData ebMSAdminProperties, PropertiesType propertiesType) throws IOException
	{
		val p = new Properties();
		switch (propertiesType)
		{
			case EBMS_ADMIN:
				write(p,ebMSAdminProperties.getConsoleProperties());
				write(p,ebMSAdminProperties.getCoreProperties());
				write(p,ebMSAdminProperties.getServiceProperties());
				write(p,ebMSAdminProperties.getJdbcProperties());
				p.store(writer,"EbMS Admin properties");
				break;
			case EBMS_ADMIN_EMBEDDED:
				write(p,ebMSAdminProperties.getConsoleProperties());
				write(p,ebMSAdminProperties.getCoreProperties());
				write(p,ebMSAdminProperties.getHttpProperties(),enableSslOverridePropeties);
				write(p,ebMSAdminProperties.getSignatureProperties());
				write(p,ebMSAdminProperties.getEncryptionProperties());
				write(p,ebMSAdminProperties.getJdbcProperties());
				p.store(writer,"EbMS Admin Embedded properties");
				break;
		}
	}

  private void write(Properties properties, ConsolePropertiesFormData consoleProperties)
  {
		properties.setProperty("maxItemsPerPage",Integer.toString(consoleProperties.getMaxItemsPerPage()));
		properties.setProperty("log4j.file",consoleProperties.getLog4jPropertiesFile() != null ? "file:" + consoleProperties.getLog4jPropertiesFile() : "");
  }

  private void write(Properties properties, ServicePropertiesFormData serviceProperties)
  {
		properties.setProperty("service.ebms.url",serviceProperties.getUrl());
  }

  private void write(Properties properties, CorePropertiesFormData coreProperties)
  {
		properties.setProperty("http.client",coreProperties.getHttpClient().name());
		properties.setProperty("eventListener.type",coreProperties.getEventListener().name());
		properties.setProperty("jms.brokerURL",coreProperties.getJmsBrokerUrl() == null ? "" : coreProperties.getJmsBrokerUrl());
		properties.setProperty("jms.virtualTopics", Boolean.toString(coreProperties.isJmsVirtualTopics()));
		properties.setProperty("jms.broker.start", Boolean.toString(coreProperties.isStartEmbeddedBroker()));
		properties.setProperty("jms.broker.config",coreProperties.getActiveMQConfigFile() == null ? "" : coreProperties.getActiveMQConfigFile());
		properties.setProperty("ebmsMessage.deleteContentOnProcessed",Boolean.toString(coreProperties.isDeleteMessageContentOnProcessed()));
		properties.setProperty("ebmsMessage.storeDuplicate",Boolean.toString(coreProperties.isStoreDuplicateMessage()));
		properties.setProperty("ebmsMessage.storeDuplicateContent",Boolean.toString(coreProperties.isStoreDuplicateMessageContent()));
  }

	private void write(Properties properties, HttpPropertiesFormData httpProperties, boolean enableSslOverridePropeties)
  {
		properties.setProperty("ebms.host",httpProperties.getHost());
		properties.setProperty("ebms.port",httpProperties.getPort() == null ? "" : httpProperties.getPort().toString());
		properties.setProperty("ebms.path",httpProperties.getPath());
		properties.setProperty("ebms.ssl",Boolean.toString(httpProperties.isSsl()));
		properties.setProperty("http.chunkedStreamingMode",Boolean.toString(httpProperties.isChunkedStreamingMode()));
		properties.setProperty("http.base64Writer",Boolean.toString(httpProperties.isBase64Writer()));
		if (httpProperties.isSsl())
			write(properties,httpProperties.getSslProperties(),enableSslOverridePropeties);
		if (httpProperties.isProxy())
			write(properties,httpProperties.getProxyProperties());
  }

	private void write(Properties properties, SslPropertiesFormData sslProperties, boolean enableSslOverridePropeties)
  {
		if (enableSslOverridePropeties && sslProperties.isOverrideDefaultProtocols())
			properties.setProperty("https.protocols",StringUtils.join(sslProperties.getEnabledProtocols(),','));
		if (enableSslOverridePropeties && sslProperties.isOverrideDefaultCipherSuites())
			properties.setProperty("https.cipherSuites",StringUtils.join(sslProperties.getEnabledCipherSuites(),','));
		properties.setProperty("https.requireClientAuthentication",Boolean.toString(sslProperties.isRequireClientAuthentication()));
		properties.setProperty("https.verifyHostnames",Boolean.toString(sslProperties.isVerifyHostnames()));
 		properties.setProperty("keystore.type",sslProperties.getKeystoreProperties().getType().name());
 		properties.setProperty("keystore.path",StringUtils.defaultString(sslProperties.getKeystoreProperties().getUri()));
 		properties.setProperty("keystore.password",StringUtils.defaultString(sslProperties.getKeystoreProperties().getPassword()));
 		properties.setProperty("keystore.defaultAlias",StringUtils.defaultString(sslProperties.getKeystoreProperties().getDefaultAlias()));
		properties.setProperty("client.keystore.type",sslProperties.getClientKeystoreProperties().getType().name());
		properties.setProperty("client.keystore.path",StringUtils.defaultString(sslProperties.getClientKeystoreProperties().getUri()));
		properties.setProperty("client.keystore.password",StringUtils.defaultString(sslProperties.getClientKeystoreProperties().getPassword()));
		properties.setProperty("client.keystore.defaultAlias",StringUtils.defaultString(sslProperties.getClientKeystoreProperties().getDefaultAlias()));
 		properties.setProperty("truststore.type",sslProperties.getTruststoreProperties().getType().name());
 		properties.setProperty("truststore.path",StringUtils.defaultString(sslProperties.getTruststoreProperties().getUri()));
 		properties.setProperty("truststore.password",StringUtils.defaultString(sslProperties.getTruststoreProperties().getPassword()));
  }

	private void write(Properties properties, ProxyPropertiesFormData proxyProperties)
  {
		properties.setProperty("http.proxy.host",StringUtils.defaultString(proxyProperties.getHost()));
		properties.setProperty("http.proxy.port",proxyProperties.getPort() == null ? "80" : proxyProperties.getPort().toString());
		properties.setProperty("http.proxy.nonProxyHosts",StringUtils.defaultString(proxyProperties.getNonProxyHosts()));
 		properties.setProperty("http.proxy.username",StringUtils.defaultString(proxyProperties.getUsername()));
 		properties.setProperty("http.proxy.password",StringUtils.defaultString(proxyProperties.getPassword()));
  }

	private void write(Properties properties, SignaturePropertiesFormData signatureProperties)
  {
  	if (signatureProperties.isSigning())
  	{
  		properties.setProperty("signature.keystore.type",signatureProperties.getKeystoreProperties().getType().name());
  		properties.setProperty("signature.keystore.path",StringUtils.defaultString(signatureProperties.getKeystoreProperties().getUri()));
  		properties.setProperty("signature.keystore.password",StringUtils.defaultString(signatureProperties.getKeystoreProperties().getPassword()));
  	}
  }

	private void write(Properties properties, EncryptionPropertiesFormData encryptionProperties)
  {
  	if (encryptionProperties.isEncryption())
  	{
  		properties.setProperty("encryption.keystore.type",encryptionProperties.getKeystoreProperties().getType().name());
  		properties.setProperty("encryption.keystore.path",StringUtils.defaultString(encryptionProperties.getKeystoreProperties().getUri()));
  		properties.setProperty("encryption.keystore.password",StringUtils.defaultString(encryptionProperties.getKeystoreProperties().getPassword()));
  	}
  }

	private void write(Properties properties, JdbcPropertiesFormData jdbcProperties)
  {
		properties.setProperty("ebms.jdbc.driverClassName",jdbcProperties.getDriver().getDriverClassName());
		properties.setProperty("ebms.jdbc.url",jdbcProperties.getUrl());
		properties.setProperty("ebms.jdbc.username",jdbcProperties.getUsername());
		properties.setProperty("ebms.jdbc.password",StringUtils.defaultString(jdbcProperties.getPassword()));
		properties.setProperty("ebms.pool.preferredTestQuery",jdbcProperties.getDriver().getPreferredTestQuery());
  }
  
}
