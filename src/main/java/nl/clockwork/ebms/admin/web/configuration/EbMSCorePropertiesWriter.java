package nl.clockwork.ebms.admin.web.configuration;

import java.io.IOException;
import java.io.Writer;
import java.util.Properties;

import nl.clockwork.ebms.admin.web.configuration.EbMSCorePropertiesPage.EbMSCorePropertiesFormModel;
import nl.clockwork.ebms.admin.web.configuration.HttpPropertiesFormPanel.HttpPropertiesFormModel;
import nl.clockwork.ebms.admin.web.configuration.JdbcPropertiesFormPanel.JdbcPropertiesFormModel;
import nl.clockwork.ebms.admin.web.configuration.SignaturePropertiesFormPanel.SignaturePropertiesFormModel;
import nl.clockwork.ebms.admin.web.configuration.SslPropertiesFormPanel.SslPropertiesFormModel;

import org.apache.commons.lang.StringUtils;

public class EbMSCorePropertiesWriter
{
	protected Writer writer;

  public EbMSCorePropertiesWriter(Writer writer)
	{
		this.writer = writer;
	}

	public void write(EbMSCorePropertiesFormModel ebMSCoreProperties) throws IOException
	{
		Properties p = new Properties();
		write(p,ebMSCoreProperties.getHttpProperties());
		write(p,ebMSCoreProperties.getSignatureProperties());
		write(p,ebMSCoreProperties.getJdbcProperties());
		p.store(writer,"EbMS Core properties");
	}

	protected void write(Properties properties, HttpPropertiesFormModel httpProperties)
  {
		//properties.setProperty("ebms.host",httpProperties.getHost());
		properties.setProperty("ebms.port",httpProperties.getPort() == null ? "" : httpProperties.getPort().toString());
		properties.setProperty("ebms.path",httpProperties.getPath());
		properties.setProperty("ebms.ssl",Boolean.toString(httpProperties.getSsl()));
		properties.setProperty("http.chunkedStreamingMode",Boolean.toString(httpProperties.isChunkedStreamingMode()));
		if (httpProperties.getSsl())
			write(properties,httpProperties.getSslProperties());
  }

	protected void write(Properties properties, SslPropertiesFormModel sslProperties)
  {
		properties.setProperty("https.allowedCipherSuites",StringUtils.join(sslProperties.getAllowedCipherSuites(),','));
		properties.setProperty("https.requireClientAuthentication",Boolean.toString(sslProperties.getRequireClientAuthentication()));
		properties.setProperty("https.verifyHostnames",Boolean.toString(sslProperties.getVerifyHostnames()));
 		properties.setProperty("keystore.path",StringUtils.defaultString(sslProperties.getKeystoreProperties().getUri()));
 		properties.setProperty("keystore.password",StringUtils.defaultString(sslProperties.getKeystoreProperties().getPassword()));
 		properties.setProperty("truststore.path",StringUtils.defaultString(sslProperties.getTruststoreProperties().getUri()));
 		properties.setProperty("truststore.password",StringUtils.defaultString(sslProperties.getTruststoreProperties().getPassword()));
  }

	protected void write(Properties properties, SignaturePropertiesFormModel signatureProperties)
  {
  	if (signatureProperties.getSigning())
  	{
  		properties.setProperty("signature.keystore.path",StringUtils.defaultString(signatureProperties.getKeystoreProperties().getUri()));
  		properties.setProperty("signature.keystore.password",StringUtils.defaultString(signatureProperties.getKeystoreProperties().getPassword()));
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
