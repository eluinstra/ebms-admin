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
package nl.clockwork.ebms.admin;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

import nl.clockwork.ebms.admin.web.configuration.JdbcURL;

public class Utils
{
	public static String readVersion(String propertiesFile)
	{
		try
		{
			File file = new File(propertiesFile);
			Properties properties = Utils.readProperties(new FileReader(file));
			return properties.getProperty("artifactId") + "-" + properties.getProperty("version");
		}
		catch (Exception e)
		{
			return "unknown";
		}
	}
	
	public static Properties readProperties(FileReader reader) throws IOException
	{
		Properties properties = new Properties();
		properties.load(reader);
		return properties;
	}
	
	public static void writeProperties(Properties properties, Writer writer)
	{
		properties.list(new PrintWriter(writer,true));
	}
	
	public static void writeProperties(Map<String,String> properties, Writer writer)
	{
		try
		{
			Set<String> keySet = new TreeSet<String>(properties.keySet());
			for (String key : keySet)
			{
				writer.write(key);
				writer.write(" = ");
				writer.write(key.matches(".*(password|pwd).*") ? properties.get(key).replaceAll(".","*") : properties.get(key));
				writer.write("\n");
			}
		}
		catch (IOException e)
		{
		}
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
				model.setJdbcHost(url.getHost());
				model.setJdbcPort(url.getPort() == -1 ? null : url.getPort());
				model.setJdbcDatabase(database);
			}
		}
		return model;
	}

}
