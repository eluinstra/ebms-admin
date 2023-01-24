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
package nl.clockwork.ebms.admin;


import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.val;
import nl.clockwork.ebms.datasource.DataSourceConfig.Location;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.flywaydb.core.Flyway;

public class DBMigrate
{
	@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
	@AllArgsConstructor
	@Getter
	@ToString
	enum BaselineVersion
	{
		VERSION_2_10("2.10","2.10.0"),
		VERSION_2_11("2.11","2.11.0"),
		VERSION_2_12("2.12","2.12.0"),
		VERSION_2_13("2.13","2.13.0"),
		VERSION_2_14("2.14","2.13.0"),
		VERSION_2_15("2.15","2.15.0"),
		VERSION_2_16("2.16","2.15.0"),
		VERSION_2_17("2.17","2.17.0"),
		VERSION_2_18("2.18","2.18.0");

		String ebmsVersion;
		String baselineVersion;

		public static Optional<String> getBaselineVersion(String ebmsVersion)
		{
			return Arrays.stream(values()).filter(v -> ebmsVersion.startsWith(v.ebmsVersion)).map(v -> v.baselineVersion).findFirst();
		}
	}

	public static void main(String[] args) throws ParseException
	{
		val options = createOptions();
		val cmd = new DefaultParser().parse(options,args);
		if (cmd.hasOption("h"))
			printUsage(options);

		migrate(cmd);
	}

	protected static Options createOptions()
	{
		val result = new Options();
		result.addOption("h",false,"print this message");
		result.addOption("jdbcUrl",true,"set jdbcUrl");
		result.addOption("username",true,"set username");
		result.addOption("password",true,"set password");
		result.addOption("strict",false,"use strict db scripts (default: false)");
		result.addOption("ebmsVersion",true,"set current ebmsVersion (default: none)");
		return result;
	}

	protected static void printUsage(Options options)
	{
		val formatter = new HelpFormatter();
		formatter.printHelp("DBMigrate",options,true);
		val versions = Arrays.stream(BaselineVersion.values()).map(v -> v.ebmsVersion).collect(Collectors.joining("\n"));
		System.out.println("\nValid ebmsVersions:\n" + versions);
		System.exit(0);
	}

	private static void migrate(CommandLine cmd) throws ParseException
	{
		val jdbcUrl = cmd.getOptionValue("jdbcUrl");
		val username = cmd.getOptionValue("username");
		val password = cmd.getOptionValue("password");
		val isStrict = "true".equals(cmd.getOptionValue("strict"));
		val location = parseLocation(jdbcUrl,isStrict);
		val baselineVersion = parseBaselineVersion(cmd.getOptionValue("ebmsVersion"));
		var config = Flyway.configure().dataSource(jdbcUrl,username,password).locations(location).ignoreMissingMigrations(true).outOfOrder(true);
		if (StringUtils.isNotEmpty(baselineVersion))
			config = config.baselineVersion(baselineVersion).baselineOnMigrate(true);
		System.out.println("Migration starting...");
		config.load().migrate();
		System.out.println("Migration finished");
	}

	private static String parseLocation(String jdbcUrl, boolean isStrict) throws ParseException
	{
		return Location.getLocation(jdbcUrl,isStrict).orElseThrow(() -> new ParseException("No location found for jdbcUrl " + jdbcUrl));
	}

	private static String parseBaselineVersion(String ebmsVersion) throws ParseException
	{
		return StringUtils.isNotEmpty(ebmsVersion)
				? BaselineVersion.getBaselineVersion(ebmsVersion).orElseThrow(() -> new ParseException("ebmsVersion " + ebmsVersion + " not found!"))
				: null;
	}

}
