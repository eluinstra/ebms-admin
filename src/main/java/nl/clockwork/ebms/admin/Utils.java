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

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;

import lombok.val;

public class Utils
{
	public static String readVersion(String propertiesFile)
	{
		try
		{
			val properties = Utils.readProperties(Utils.class.getResourceAsStream(propertiesFile));
			return properties.getProperty("artifactId") + "-" + properties.getProperty("version");
		}
		catch (Exception e)
		{
			return "unknown";
		}
	}
	
	public static Properties readProperties(InputStream inputStream) throws IOException
	{
		val properties = new Properties();
		properties.load(inputStream);
		return properties;
	}
	
	public static void writeProperties(Properties properties, Writer writer)
	{
		properties.list(new PrintWriter(writer,true));
	}
	
	public static void writeProperties(Map<String,String> properties, Writer writer) throws IOException
	{
		val keySet = new TreeSet<String>(properties.keySet());
		for (val key : keySet)
		{
			writer.write(key);
			writer.write(" = ");
			writer.write(key.matches(".*(password|pwd).*") ? properties.get(key).replaceAll(".","*") : properties.get(key));
			writer.write("\n");
		}
	}

	public static <T> List<T> toList(List<T> list)
	{
		return list == null ? new ArrayList<>() : list;
	}

	public static String getHost(String host)
	{
		return "0.0.0.0".equals(host) || "::".equals(host) ? "localhost" : host;
	}

}
