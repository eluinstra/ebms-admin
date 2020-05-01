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

import nl.clockwork.ebms.admin.web.configuration.CorePropertiesFormPanel.CorePropertiesFormModel;
import nl.clockwork.ebms.admin.web.configuration.EbMSCorePropertiesPage.EbMSCorePropertiesFormModel;
import nl.clockwork.ebms.admin.web.configuration.EncryptionPropertiesFormPanel.EncryptionPropertiesFormModel;
import nl.clockwork.ebms.admin.web.configuration.HttpPropertiesFormPanel.HttpPropertiesFormModel;
import nl.clockwork.ebms.admin.web.configuration.JdbcPropertiesFormPanel.JdbcPropertiesFormModel;
import nl.clockwork.ebms.admin.web.configuration.ProxyPropertiesFormPanel.ProxyPropertiesFormModel;
import nl.clockwork.ebms.admin.web.configuration.SignaturePropertiesFormPanel.SignaturePropertiesFormModel;
import nl.clockwork.ebms.admin.web.configuration.SslPropertiesFormPanel.SslPropertiesFormModel;

import org.apache.commons.lang3.StringUtils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.val;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@AllArgsConstructor
public class EbMSCorePropertiesWriter
{
	Writer writer;
	boolean enableSslOverridePropeties;

	public void write(EbMSCorePropertiesFormModel ebMSCoreProperties) throws IOException
	{
		val p = new Properties();
		write(p,ebMSCoreProperties.getCoreProperties());
		write(p,ebMSCoreProperties.getHttpProperties(),enableSslOverridePropeties);
		write(p,ebMSCoreProperties.getSignatureProperties());
		write(p,ebMSCoreProperties.getEncryptionProperties());
		write(p,ebMSCoreProperties.getJdbcProperties());
		p.store(writer,"EbMS Core properties");
	}

  protected void write(Properties properties, CorePropertiesFormModel coreProperties)
  {
		properties.setProperty("patch.digipoort.enable",Boolean.toString(coreProperties.isDigipoortPatch()));
		properties.setProperty("patch.oracle.enable",Boolean.toString(coreProperties.isOraclePatch()));
		properties.setProperty("patch.cleo.enable",Boolean.toString(coreProperties.isCleoPatch()));
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

	protected void write(Properties properties, HttpPropertiesFormModel httpProperties, boolean enableSslOverridePropeties)
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

	protected void write(Properties properties, SslPropertiesFormModel sslProperties, boolean enableSslOverridePropeties)
  {
		if (enableSslOverridePropeties && sslProperties.isOverrideDefaultProtocols())
			properties.setProperty("https.protocols",StringUtils.join(sslProperties.getEnabledProtocols(),','));
		if (enableSslOverridePropeties && sslProperties.isOverrideDefaultCipherSuites())
			properties.setProperty("https.cipherSuites",StringUtils.join(sslProperties.getEnabledCipherSuites(),','));
		properties.setProperty("https.requireClientAuthentication",Boolean.toString(sslProperties.isRequireClientAuthentication()));
		properties.setProperty("https.verifyHostnames",Boolean.toString(sslProperties.isVerifyHostnames()));
		properties.setProperty("https.clientCertificateAuthentication",Boolean.toString(sslProperties.isClientCertificateAuthentication()));
 		properties.setProperty("keystore.type",sslProperties.getKeystoreProperties().getType().name());
 		properties.setProperty("keystore.path",StringUtils.defaultString(sslProperties.getKeystoreProperties().getUri()));
 		properties.setProperty("keystore.password",StringUtils.defaultString(sslProperties.getKeystoreProperties().getPassword()));
		properties.setProperty("client.keystore.type",sslProperties.getClientKeystoreProperties().getType().name());
		properties.setProperty("client.keystore.path",StringUtils.defaultString(sslProperties.getClientKeystoreProperties().getUri()));
		properties.setProperty("client.keystore.password",StringUtils.defaultString(sslProperties.getClientKeystoreProperties().getPassword()));
 		properties.setProperty("truststore.type",sslProperties.getTruststoreProperties().getType().name());
 		properties.setProperty("truststore.path",StringUtils.defaultString(sslProperties.getTruststoreProperties().getUri()));
 		properties.setProperty("truststore.password",StringUtils.defaultString(sslProperties.getTruststoreProperties().getPassword()));
  }

	protected void write(Properties properties, ProxyPropertiesFormModel proxyProperties)
  {
		properties.setProperty("http.proxy.host",StringUtils.defaultString(proxyProperties.getHost()));
		properties.setProperty("http.proxy.port",proxyProperties.getPort() == null ? "80" : proxyProperties.getPort().toString());
		properties.setProperty("http.proxy.nonProxyHosts",StringUtils.defaultString(proxyProperties.getNonProxyHosts()));
 		properties.setProperty("http.proxy.username",StringUtils.defaultString(proxyProperties.getUsername()));
 		properties.setProperty("http.proxy.password",StringUtils.defaultString(proxyProperties.getPassword()));
  }

	protected void write(Properties properties, SignaturePropertiesFormModel signatureProperties)
  {
  	if (signatureProperties.isSigning())
  	{
  		properties.setProperty("signature.keystore.type",signatureProperties.getKeystoreProperties().getType().name());
  		properties.setProperty("signature.keystore.path",StringUtils.defaultString(signatureProperties.getKeystoreProperties().getUri()));
  		properties.setProperty("signature.keystore.password",StringUtils.defaultString(signatureProperties.getKeystoreProperties().getPassword()));
  	}
  }

	protected void write(Properties properties, EncryptionPropertiesFormModel encryptionProperties)
  {
  	if (encryptionProperties.isEncryption())
  	{
  		properties.setProperty("encryption.keystore.type",encryptionProperties.getKeystoreProperties().getType().name());
  		properties.setProperty("encryption.keystore.path",StringUtils.defaultString(encryptionProperties.getKeystoreProperties().getUri()));
  		properties.setProperty("encryption.keystore.password",StringUtils.defaultString(encryptionProperties.getKeystoreProperties().getPassword()));
  	}
  }

	protected void write(Properties properties, JdbcPropertiesFormModel jdbcProperties)
  {
		properties.setProperty("ebms.jdbc.driverClassName",jdbcProperties.getDriver().getDriverClassName());
		properties.setProperty("ebms.jdbc.url",jdbcProperties.getUrl());
		properties.setProperty("ebms.jdbc.username",jdbcProperties.getUsername());
		properties.setProperty("ebms.jdbc.password",StringUtils.defaultString(jdbcProperties.getPassword()));
		properties.setProperty("ebms.pool.preferredTestQuery",jdbcProperties.getDriver().getPreferredTestQuery());
  }
  

}
