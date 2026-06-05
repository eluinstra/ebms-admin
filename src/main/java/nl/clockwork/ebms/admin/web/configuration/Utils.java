/*
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
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Scanner;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import nl.clockwork.ebms.common.security.KeyStoreType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Utils
{
	private static SSLEngine sslEngine;

	static
	{
		try
		{
			val sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, null, null);
			sslEngine = sslContext.createSSLEngine();
		}
		catch (NoSuchAlgorithmException | KeyManagementException | RuntimeException e)
		{
			// Keep startup resilient even if TLS engine initialization is unavailable.
		}
	}

	public static String[] getSupportedSSLProtocols()
	{
		return sslEngine.getSupportedProtocols();
	}

	public static String[] getSupportedSSLCipherSuites()
	{
		return sslEngine.getSupportedCipherSuites();
	}

	public static String createURL(String hostname, int port)
	{
		return hostname + (port == -1 ? "" : ":" + port);
	}

	public static String createURL(String hostname, Integer port)
	{
		return hostname + (port == null ? "" : ":" + port);
	}

	public static JdbcURL parseJdbcURL(String jdbcURL, JdbcURL model) throws MalformedURLException
	{
		try (val scanner = new Scanner(jdbcURL))
		{
			val protocol = scanner.findInLine("(://|@|:@//)");
			if (protocol != null)
			{
				val urlString = scanner.findInLine("[^/:]+(:\\d+){0,1}");
				scanner.findInLine("(/|:|;databaseName=)");
				val database = scanner.findInLine("[^;]*");
				if (urlString != null)
				{
					val url = URI.create("http://" + urlString).toURL();
					model.setHost(url.getHost());
					model.setPort(url.getPort() == -1 ? null : url.getPort());
					model.setDatabase(database);
				}
			}
			return model;
		}
	}

	public static void testEbMSUrl(String uri) throws IOException
	{
		// val url = new URL(uri + "/message?wsdl");
		val url = URI.create(uri + "/cpa?wsdl").toURL();
		val connection = url.openConnection();
		if (connection instanceof HttpURLConnection httpURLConnection)
		{
			connection.setDoOutput(true);
			httpURLConnection.setRequestMethod("GET");
			connection.connect();
			if (httpURLConnection.getResponseCode() != 200)
				throw new IOException("Unexpected HTTP status code " + httpURLConnection.getResponseCode() + " for " + url);
		}
		else
			throw new IllegalArgumentException("Unknown protocol: " + uri);
	}

	public static Resource getResource(@org.springframework.lang.NonNull String path) throws IOException
	{
		val result = new FileSystemResource(path);
		return result.exists() ? result : new ClassPathResource(path);
	}

	public static void testTrustStore(KeyStoreType type, @org.springframework.lang.NonNull String path, String password)
			throws IOException, KeyStoreException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException
	{
		testKeyStore(type, path, password, null, false);
	}

	public static
			void
			testKeyStore(KeyStoreType type, @org.springframework.lang.NonNull String path, String password, String defaultAlias, boolean validateKeyPassword)
					throws IOException, KeyStoreException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException
	{
		val keyPassword = password.toCharArray();
		val resource = getResource(path);
		val keyStore = KeyStore.getInstance(type.name());
		keyStore.load(resource.getInputStream(), keyPassword);
		val aliases = keyStore.aliases();
		if (!aliases.hasMoreElements())
			throw new IllegalStateException("No keys found in keystore " + path);
		if (StringUtils.isEmpty(defaultAlias))
		{
			validateAliasPassword(keyStore, aliases.nextElement(), validateKeyPassword, keyPassword);
			return;
		}
		validateDefaultAlias(path, defaultAlias, keyStore, aliases, validateKeyPassword, keyPassword);
	}

	private static
			void
			validateDefaultAlias(String path, String defaultAlias, KeyStore keyStore, Enumeration<String> aliases, boolean validateKeyPassword, char[] keyPassword)
					throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException
	{
		while (aliases.hasMoreElements())
		{
			val alias = aliases.nextElement();
			if (alias.equals(defaultAlias))
			{
				validateAliasPassword(keyStore, alias, validateKeyPassword, keyPassword);
				return;
			}
		}
		throw new IllegalArgumentException("Alias " + defaultAlias + " not found in keystore " + path);
	}

	private static void validateAliasPassword(KeyStore keyStore, String alias, boolean validateKeyPassword, char[] keyPassword)
			throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException
	{
		if (validateKeyPassword)
			keyStore.getKey(alias, keyPassword);
	}

	public static void testJdbcConnection(String driverClassName, String jdbcUrl, String username, String password)
			throws PropertyVetoException, SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException
	{
		val loader = Utils.class.getClassLoader();
		val driverClass = loader.loadClass(driverClassName);
		val driver = (Driver)driverClass.getDeclaredConstructor().newInstance();
		if (!driver.acceptsURL(jdbcUrl))
			throw new IllegalArgumentException("Jdbc Url '" + jdbcUrl + "' not valid!");
		val info = new Properties();
		info.setProperty("user", username);
		if (password != null)
			info.setProperty("password", password);
		try (val connection = driver.connect(jdbcUrl, info))
		{
			if (connection == null)
				throw new SQLException("Could not establish JDBC connection for URL: " + jdbcUrl);
		}
	}

}
