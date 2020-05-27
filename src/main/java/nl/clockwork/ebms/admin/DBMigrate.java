package nl.clockwork.ebms.admin;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.flywaydb.core.Flyway;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.val;
import lombok.var;
import lombok.experimental.FieldDefaults;

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
		VERSION_2_17("2.17","2.17.0");
		
		String ebmsVersion;
		String baselineVersion;
		
		public static Optional<String> getBaselineVersion(String ebmsVersion)
		{
			return Arrays.stream(values())
					.filter(v -> ebmsVersion.startsWith(v.ebmsVersion))
					.map(v -> v.baselineVersion)
					.findFirst();
		}
	}

	public static final String BASEPATH = "classpath:/nl/clockwork/ebms/db/migration/";

	@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
	@AllArgsConstructor
	@Getter
	enum Location
	{
		DB2("jdbc:db2:",BASEPATH + "db2",false),
		DB2_STRICT("jdbc:db2:",BASEPATH + "db2.strict",true),
		HSQLDB("jdbc:hsqldb:",BASEPATH + "hsqldb",false),
		HSQLDB_STRICT("jdbc:hsqldb:",BASEPATH + "hsqldb.strict",true),
		MSSQL("jdbc:sqlserver:",BASEPATH + "mssql",false),
		MYSQL("jdbc:sqlserver:",BASEPATH + "mysql",false),
		ORACLE("jdbc:oracle:",BASEPATH + "oracle",false),
		ORACLE_STRICT("jdbc:oracle:",BASEPATH + "oracle.strict",true),
		POSTGRES("jdbc:postgresql:",BASEPATH + "postgresql",false),
		POSTGRES_STRICT("jdbc:postgresql:",BASEPATH + "postgresql.strict",true);
		
		String jdbcUrl;
		String location;
		boolean strict;
		
		public static Optional<String> getLocation(String jdbcUrl, boolean strict)
		{
			return Arrays.stream(values())
					.filter(l -> jdbcUrl.startsWith(l.jdbcUrl) && (l.strict == strict) || EnumSet.of(MSSQL,MYSQL).contains(l))
					.map(l -> l.location)
					.findFirst();
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
		result.addOption("strict",false,"use strict db scripts");
		result.addOption("ebmsVersion",true,"set ebmsVersion");
		return result;
	}
	
	protected static void printUsage(Options options)
	{
		val formatter = new HelpFormatter();
		formatter.printHelp("Start",options,true);
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
		var config = Flyway.configure()
				.dataSource(jdbcUrl,username,password)
				.locations(location)
				.ignoreMissingMigrations(true);
		if (StringUtils.isNotEmpty(baselineVersion))
				config = config
						.baselineVersion(baselineVersion)
						.baselineOnMigrate(true);
		config.load().migrate();
	}

	private static String parseLocation(String jdbcUrl, boolean isStrict) throws ParseException
	{
		return Location.getLocation(jdbcUrl,isStrict).orElseThrow(() -> new ParseException("No location found for jdbcUrl " + jdbcUrl));
	}

	private static String parseBaselineVersion(String ebmsVersion) throws ParseException
	{
		return StringUtils.isNotEmpty(ebmsVersion) ? BaselineVersion.getBaselineVersion(ebmsVersion).orElseThrow(() -> new ParseException("ebmsVersion " + ebmsVersion + " not found!")) : null;
	}

}
