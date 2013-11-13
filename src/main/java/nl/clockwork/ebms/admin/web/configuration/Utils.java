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

import java.beans.PropertyVetoException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Scanner;

import nl.clockwork.ebms.admin.web.configuration.ConsolePropertiesFormPanel.ConsolePropertiesFormModel;
import nl.clockwork.ebms.admin.web.configuration.Constants.JdbcDriver;
import nl.clockwork.ebms.admin.web.configuration.Constants.PropertiesType;
import nl.clockwork.ebms.admin.web.configuration.EbMSAdminPropertiesPage.EbMSAdminPropertiesFormModel;
import nl.clockwork.ebms.admin.web.configuration.EbMSCorePropertiesPage.EbMSCorePropertiesFormModel;
import nl.clockwork.ebms.admin.web.configuration.HttpPropertiesFormPanel.HttpPropertiesFormModel;
import nl.clockwork.ebms.admin.web.configuration.JdbcPropertiesFormPanel.JdbcPropertiesFormModel;
import nl.clockwork.ebms.admin.web.configuration.ServicePropertiesFormPanel.ServicePropertiesFormModel;
import nl.clockwork.ebms.admin.web.configuration.SignaturePropertiesFormPanel.SignaturePropertiesFormModel;
import nl.clockwork.ebms.admin.web.configuration.SslPropertiesFormPanel.SslPropertiesFormModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

public class Utils
{
	public static String createURL(String hostname, int port)
	{
		return hostname + (port == -1 ? "" : ":" + port); 
	}
	
	public static String createURL(String hostname, Integer port)
	{
		return hostname + (port == null ? "" : ":" + port); 
	}
	
	public static void loadProperties(EbMSAdminPropertiesFormModel ebMSAdminProperties, PropertiesType propertiesType, FileReader reader) throws IOException
	{
		Properties properties = new Properties();
		properties.load(reader);
		switch (propertiesType)
		{
			case EBMS_ADMIN:
				loadProperties(properties,ebMSAdminProperties.getConsoleProperties());
				loadProperties(properties,ebMSAdminProperties.getServiceProperties());
				loadProperties(properties,ebMSAdminProperties.getJdbcProperties());
				break;
			case EBMS_ADMIN_EMBEDDED:
				loadProperties(properties,ebMSAdminProperties.getConsoleProperties());
				loadProperties(properties,ebMSAdminProperties.getHttpProperties());
				loadProperties(properties,ebMSAdminProperties.getSignatureProperties());
				loadProperties(properties,ebMSAdminProperties.getJdbcProperties());
				break;
			case EBMS_CORE:
				loadProperties(properties,ebMSAdminProperties.getServiceProperties());
				loadProperties(properties,ebMSAdminProperties.getHttpProperties());
				loadProperties(properties,ebMSAdminProperties.getSignatureProperties());
				loadProperties(properties,ebMSAdminProperties.getJdbcProperties());
				break;
		}
	}
	
	public static void loadProperties(Properties properties, ConsolePropertiesFormModel consoleProperties) throws MalformedURLException
	{
		consoleProperties.setMaxItemsPerPage(Integer.parseInt(properties.getProperty("maxItemsPerPage")));
	}

	public static void loadProperties(Properties properties, ServicePropertiesFormModel serviceProperties) throws MalformedURLException
	{
		serviceProperties.setUrl(properties.getProperty("service.ebms.url"));
	}

	public static void loadProperties(Properties properties, HttpPropertiesFormModel httpProperties) throws MalformedURLException
	{
		//httpProperties.setHost(properties.getProperty("ebms.host"));
		httpProperties.setPort(properties.getProperty("ebms.port") == null ? null : new Integer(properties.getProperty("ebms.port")));
		httpProperties.setPath(properties.getProperty("ebms.path"));
		httpProperties.setSsl(new Boolean(properties.getProperty("ebms.ssl")));
		httpProperties.setChunkedStreamingMode(new Boolean(properties.getProperty("http.chunkedStreamingMode")));
		if (httpProperties.getSsl())
			loadProperties(properties,httpProperties.getSslProperties());
	}

	public static void loadProperties(Properties properties, SslPropertiesFormModel sslProperties) throws MalformedURLException
	{
		sslProperties.setAllowedCipherSuites(Arrays.asList(StringUtils.stripAll(StringUtils.split(properties.getProperty("https.allowedCipherSuites"),','))));
		sslProperties.setRequireClientAuthentication(new Boolean(properties.getProperty("https.requireClientAuthentication")));
		sslProperties.setVerifyHostnames(new Boolean(properties.getProperty("https.verifyHostnames")));
		sslProperties.getKeystoreProperties().setUri(properties.getProperty("keystore.path"));
		sslProperties.getKeystoreProperties().setPassword(properties.getProperty("keystore.password"));
		sslProperties.getTruststoreProperties().setUri(properties.getProperty("truststore.path"));
		sslProperties.getTruststoreProperties().setPassword(properties.getProperty("truststore.password"));
	}

	public static void loadProperties(Properties properties, SignaturePropertiesFormModel signatureProperties) throws MalformedURLException
	{
		if (signatureProperties.getSigning())
		{
			signatureProperties.getKeystoreProperties().setUri(properties.getProperty("signature.keystore.path"));
			signatureProperties.getKeystoreProperties().setPassword(properties.getProperty("signature.keystore.password"));
		}
	}

	public static void loadProperties(Properties properties, JdbcPropertiesFormModel jdbcProperties) throws MalformedURLException
	{
		jdbcProperties.setDriver(JdbcDriver.getJdbcDriver(properties.getProperty("ebms.jdbc.driverClassName")));
		//jdbcProperties.setJdbcURL(properties.getProperty("ebms.jdbc.url"));
		parseJdbcURL(properties.getProperty("ebms.jdbc.url"),jdbcProperties);
		jdbcProperties.setUsername(properties.getProperty("ebms.jdbc.username"));
		jdbcProperties.setPassword(properties.getProperty("ebms.jdbc.password"));
		//jdbcProperties.setPreferredTestQuery(properties.getProperty("ebms.pool.preferredTestQuery"));
	}

  public static void storeProperties(EbMSCorePropertiesFormModel ebMSCoreProperties, Writer writer) throws IOException
	{
		Properties p = new Properties();
		storeProperties(p,ebMSCoreProperties.getHttpProperties());
		storeProperties(p,ebMSCoreProperties.getSignatureProperties());
		storeProperties(p,ebMSCoreProperties.getJdbcProperties());
		p.store(writer,"EbMS Core properties");
	}
  public static void storeProperties(EbMSAdminPropertiesFormModel ebMSAdminProperties, PropertiesType propertiesType, Writer writer) throws IOException
	{
		Properties p = new Properties();
		switch (propertiesType)
		{
			case EBMS_ADMIN:
				storeProperties(p,ebMSAdminProperties.getConsoleProperties());
				storeProperties(p,ebMSAdminProperties.getServiceProperties());
				storeProperties(p,ebMSAdminProperties.getJdbcProperties());
				p.store(writer,"EbMS Admin properties");
				break;
			case EBMS_ADMIN_EMBEDDED:
				storeProperties(p,ebMSAdminProperties.getConsoleProperties());
				storeProperties(p,ebMSAdminProperties.getHttpProperties());
				storeProperties(p,ebMSAdminProperties.getSignatureProperties());
				storeProperties(p,ebMSAdminProperties.getJdbcProperties());
				p.store(writer,"EbMS Admin Embedded properties");
				break;
			case EBMS_CORE:
				storeProperties(ebMSAdminProperties,writer);
				break;
		}
	}

  public static void storeProperties(Properties properties, ConsolePropertiesFormModel consoleProperties)
  {
		properties.setProperty("maxItemsPerPage",Integer.toString(consoleProperties.getMaxItemsPerPage()));
  }

  public static void storeProperties(Properties properties, ServicePropertiesFormModel serviceProperties)
  {
		properties.setProperty("service.ebms.url",serviceProperties.getUrl());
  }

  public static void storeProperties(Properties properties, HttpPropertiesFormModel httpProperties)
  {
		//properties.setProperty("ebms.host",httpProperties.getHost());
		properties.setProperty("ebms.port",httpProperties.getPort() == null ? "" : httpProperties.getPort().toString());
		properties.setProperty("ebms.path",httpProperties.getPath());
		properties.setProperty("ebms.ssl",Boolean.toString(httpProperties.getSsl()));
		properties.setProperty("http.chunkedStreamingMode",Boolean.toString(httpProperties.isChunkedStreamingMode()));
		if (httpProperties.getSsl())
			storeProperties(properties,httpProperties.getSslProperties());
  }

  public static void storeProperties(Properties properties, SslPropertiesFormModel sslProperties)
  {
		properties.setProperty("https.allowedCipherSuites",StringUtils.join(sslProperties.getAllowedCipherSuites(),','));
		properties.setProperty("https.requireClientAuthentication",Boolean.toString(sslProperties.getRequireClientAuthentication()));
		properties.setProperty("https.verifyHostnames",Boolean.toString(sslProperties.getVerifyHostnames()));
 		properties.setProperty("keystore.path",StringUtils.defaultString(sslProperties.getKeystoreProperties().getUri()));
 		properties.setProperty("keystore.password",StringUtils.defaultString(sslProperties.getKeystoreProperties().getPassword()));
 		properties.setProperty("truststore.path",StringUtils.defaultString(sslProperties.getTruststoreProperties().getUri()));
 		properties.setProperty("truststore.password",StringUtils.defaultString(sslProperties.getTruststoreProperties().getPassword()));
  }

  public static void storeProperties(Properties properties, SignaturePropertiesFormModel signatureProperties)
  {
  	if (signatureProperties.getSigning())
  	{
  		properties.setProperty("signature.keystore.path",StringUtils.defaultString(signatureProperties.getKeystoreProperties().getUri()));
  		properties.setProperty("signature.keystore.password",StringUtils.defaultString(signatureProperties.getKeystoreProperties().getPassword()));
  	}
  }

  public static void storeProperties(Properties properties, JdbcPropertiesFormModel jdbcProperties)
  {
		properties.setProperty("ebms.jdbc.driverClassName",jdbcProperties.getDriver().getDriverClassName());
		properties.setProperty("ebms.jdbc.url",jdbcProperties.getUrl());
		properties.setProperty("ebms.jdbc.username",jdbcProperties.getUsername());
		properties.setProperty("ebms.jdbc.password",StringUtils.defaultString(jdbcProperties.getPassword()));
		properties.setProperty("ebms.pool.preferredTestQuery",jdbcProperties.getDriver().getPreferredTestQuery());
  }
  
	public static JdbcURL parseJdbcURL(String jdbcURL, JdbcURL model) throws MalformedURLException
	{
		Scanner scanner = new Scanner(jdbcURL);
		String protocol = scanner.findInLine("(://|@|:@//)");
		if (protocol != null)
		{
			String urlString = scanner.findInLine("[^/:]+(:\\d+){0,1}");
			scanner.findInLine("(/|:|;databaseName=)");
			String database = scanner.findInLine("[^;]*");
			if (urlString != null)
			{
				URL url = new URL("http://" + urlString);
				model.setHost(url.getHost());
				model.setPort(url.getPort() == -1 ? null : url.getPort());
				model.setDatabase(database);
			}
		}
		return model;
	}

  public static void testEbMSUrl(String uri) throws IOException
	{
		URL url = new URL(uri + "/cpa?wsdl");
		//URL url = new URL(uri + "/message?wsdl");
		URLConnection connection = url.openConnection();
		if (connection instanceof HttpURLConnection)
		{
			connection.setDoOutput(true);
			((HttpURLConnection)connection).setRequestMethod("GET");
			connection.connect();
			if (((HttpURLConnection)connection).getResponseCode() != 200)
				throw new RuntimeException("Status code " + ((HttpURLConnection)connection).getResponseCode());
		}
		else
			throw new IllegalArgumentException("Unknown protocol: " + uri);
	}
	
	public static Resource getResource(String path) throws MalformedURLException, IOException
	{
		Resource result = new FileSystemResource(path);
		return result.exists() ? result : new ClassPathResource(path);
	}
  
	public static void testKeystore(String path, String password) throws MalformedURLException, IOException, KeyStoreException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException
	{
		Resource resource = getResource(path);
		KeyStore keyStore = KeyStore.getInstance("JKS");
		keyStore.load(resource.getInputStream(),password.toCharArray());
		Enumeration<String> aliases = keyStore.aliases();
		while (aliases.hasMoreElements())
		{
			String alias = aliases.nextElement();
			if (keyStore.isKeyEntry(alias))
				keyStore.getKey(alias,password.toCharArray());
		}
	}

	public static void testJdbcConnection(String driverClassName, String jdbcUrl, String username, String password) throws PropertyVetoException, SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException
	{
    ClassLoader loader = Utils.class.getClassLoader();
    Class<?> driverClass = loader.loadClass(driverClassName);
    Driver driver = (Driver)driverClass.newInstance();
    if (!driver.acceptsURL(jdbcUrl))
    	throw new IllegalArgumentException("Jdbc Url '" + jdbcUrl + "' not valid!");
    Properties info = new Properties();
    info.setProperty("user",username);
    if (password != null)
    	info.setProperty("password",password);
    Connection connection = driver.connect(jdbcUrl,info);
		connection.close();
	}

}
