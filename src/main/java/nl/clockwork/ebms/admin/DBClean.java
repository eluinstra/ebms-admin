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

import static nl.clockwork.ebms.admin.Constants.DATE_FORMAT_YMD;

import com.querydsl.sql.SQLQueryFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.ToLongFunction;
import javax.sql.DataSource;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import nl.clockwork.ebms.querydsl.model.QCpa;
import nl.clockwork.ebms.querydsl.model.QDeliveryLog;
import nl.clockwork.ebms.querydsl.model.QDeliveryTask;
import nl.clockwork.ebms.querydsl.model.QEbmsAttachment;
import nl.clockwork.ebms.querydsl.model.QEbmsMessage;
import nl.clockwork.ebms.querydsl.model.QMessageEvent;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class DBClean implements SystemInterface
{

	private static final String LOG4J_CONFIGURATION_FILE = "log4j.configurationFile";
	private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT_YMD);
	TextIO textIO = TextIoFactory.getTextIO();

	public static void main(String[] args) throws Exception
	{
		val options = createOptions();
		val cmd = new DefaultParser().parse(options, args);
		if (cmd.hasOption("h"))
			printUsage(options);
		else
		{
			init(cmd);
			try (val context = new AnnotationConfigApplicationContext(DBConfig.class))
			{
				val dbClean = createDBClean(context);
				dbClean.execute(cmd);
			}
			catch (Throwable t)
			{
				printErr(t);
			}
		}
		System.exit(0);
	}

	private static Options createOptions()
	{
		val result = new Options();
		result.addOption("h", false, "print this message");
		result.addOption("cmd", true, "objects to clean [values: cpa|messages]");
		result.addOption("cpaId", true, "the cpaId of the CPA to delete");
		result.addOption(
				"dateFrom",
				true,
				"the date from which objects will be deleted [format: YYYYMMDD][default: " + dateFormatter.format(LocalDate.now().minusDays(30)) + "]");
		result.addOption("retentionDays", true, "the number of days that will be retained during deletion, overrules occurrence of dateFrom option");
		result.addOption("includeNoPersistDuration", false, "whether or not messages from CPAs without PersistDuration set will be deleted");
		result.addOption("configDir", true, "set config directory (default=current dir)");
		return result;
	}

	private static void printUsage(Options options)
	{
		val formatter = new HelpFormatter();
		formatter.printHelp("DBClean", options, true);
	}

	private static void init(CommandLine cmd)
	{
		val configDir = cmd.getOptionValue("configDir", "");
		System.setProperty("ebms.configDir", configDir);
		printStatic("Using config directory: " + configDir);
	}

	private static DBClean createDBClean(AnnotationConfigApplicationContext context)
	{
		val queryFactory = context.getBean(SQLQueryFactory.class);
		val transactionManager = context.getBean("dataSourceTransactionManager", PlatformTransactionManager.class);
		val dataSource = context.getBean(DataSource.class);
		val namedParameterjdbctemplate = new NamedParameterJdbcTemplate(dataSource);
		return new DBClean(queryFactory, transactionManager, namedParameterjdbctemplate);
	}

	@NonNull
	SQLQueryFactory queryFactory;
	@NonNull
	PlatformTransactionManager transactionManager;
	@NonNull
	NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	QCpa cpaTable = QCpa.cpa1;
	QEbmsMessage messageTable = QEbmsMessage.ebmsMessage;
	QEbmsAttachment attachmentTable = QEbmsAttachment.ebmsAttachment;
	QMessageEvent messageEventTable = QMessageEvent.ebmsMessageEvent;
	QDeliveryTask deliveryTaskTable = QDeliveryTask.deliveryTask;
	QDeliveryLog deliveryLogTable = QDeliveryLog.deliveryLog;

	private void execute(final CommandLine cmd) throws IOException
	{
		switch (cmd.getOptionValue("cmd", ""))
		{
			case ("cpa"):
				println("Running CPA deletion script...");
				validateCleanCPA(cmd);
				executeCleanCPA(cmd);
				break;
			case ("messages"):
				println("Running Message deletion script...");
				executeCleanMessages(cmd);
				break;
			default:
				printWarn("Cmd " + cmd.getOptionValue("cmd") + " not recognized");
		}
	}

	private boolean validateCleanCPA(CommandLine cmd)
	{
		if (!cmd.hasOption("cpaId"))
		{
			printWarn("Option cpaId missing");
			return false;
		}
		return true;
	}

	private void executeCleanCPA(CommandLine cmd) throws IOException
	{
		val cpaId = cmd.getOptionValue("cpaId");
		val status = transactionManager.getTransaction(null);
		try
		{
			if (queryFactory.select(cpaTable.cpaId).from(cpaTable).where(cpaTable.cpaId.eq(cpaId)).fetchCount() > 0)
			{
				val ok = textIO.newBooleanInputReader()
						.withDefaultValue(false)
						.read("WARNING: This command will delete all messages and data related to cpa " + cpaId + ". Are you sure?");
				if (ok)
					cleanCPA(cpaId);
			}
			else
				println("CPA " + cpaId + " not found!");

			transactionManager.commit(status);
		}
		catch (Exception e)
		{
			printErr(e);
			transactionManager.rollback(status);
		}
	}

	private void executeCleanMessages(CommandLine cmd)
	{
		val includeNoPersistDuration = cmd.hasOption("includeNoPersistDuration");
		val dateFrom = Objects.nonNull(cmd.getOptionValue("retentionDays"))
				? createDateFromRetentionDays(cmd.getOptionValue("retentionDays"))
				: createDateFrom(cmd.getOptionValue("dateFrom"));
		if (dateFrom != null)
		{
			println("using fromDate " + dateFrom);
			if (includeNoPersistDuration)
			{
				println("Including messages from CPA's without PersistDuration set...");
			}
			val status = transactionManager.getTransaction(null);
			try
			{
				cleanMessages(dateFrom, includeNoPersistDuration);
				transactionManager.commit(status);
			}
			catch (Exception e)
			{
				printErr(e);
				transactionManager.rollback(status);
			}
		}
		else
		{
			printWarn("Unable to parse date " + cmd.getOptionValue("dateFrom"));
		}
	}

	private static Instant createDateFromRetentionDays(String retentionDaysString)
	{
		try
		{
			return StringUtils.isEmpty(retentionDaysString) ? null : Instant.now().minus(Period.ofDays(Integer.parseInt(retentionDaysString)));
		}
		catch (NumberFormatException e)
		{
			return null;
		}
	}

	private static Instant createDateFrom(String s)
	{
		try
		{
			val date = StringUtils.isEmpty(s) ? LocalDate.now().minusDays(30) : LocalDate.parse(s, dateFormatter);
			return date.atStartOfDay(ZoneId.systemDefault()).toInstant();
		}
		catch (DateTimeParseException e)
		{
			return null;
		}
	}

	private boolean alternativeAttachmentImplementation()
	{
		try (val connection = namedParameterJdbcTemplate.getJdbcTemplate().getDataSource().getConnection())
		{
			val vendor = connection.getMetaData().getDatabaseProductName();
			return vendor.equalsIgnoreCase("microsoft sql server") || vendor.equalsIgnoreCase("mariadb") || vendor.equalsIgnoreCase("h2");
		}
		catch (SQLException e)
		{
			printErr(e);
			return false;
		}
	}

	private void cleanCPA(String cpaId)
	{
		val ids = queryFactory.select(messageTable.messageId).from(messageTable).where(messageTable.cpaId.eq(cpaId)).fetch();
		defensiveDelete(ids, "deliveryLogs", idList -> queryFactory.delete(deliveryLogTable).where(deliveryLogTable.messageId.in(idList)).execute());
		defensiveDelete(ids, "deliveryTasks", idList -> queryFactory.delete(deliveryTaskTable).where(deliveryTaskTable.messageId.in(ids)).execute());
		defensiveDelete(ids, "messageEvents", idList -> queryFactory.delete(messageEventTable).where(messageEventTable.messageId.in(ids)).execute());
		if (alternativeAttachmentImplementation())
		{
			val parameterSource = new MapSqlParameterSource();
			parameterSource.addValue("cpaId", cpaId);
			val idsInteger = namedParameterJdbcTemplate.getJdbcTemplate().queryForList("select id from ebms_message where cpa_id = ?", Integer.class, cpaId);
			ToLongFunction<List<String>> query = idList ->
			{
				val parameters = new MapSqlParameterSource("idsInteger", idsInteger);
				return (long)namedParameterJdbcTemplate.update("delete from ebms_attachment where ebms_message_id in (:idsInteger)", parameters);
			};
			defensiveDelete(ids, "attachments", query);
		}
		else
		{
			defensiveDelete(ids, "attachments", idList -> queryFactory.delete(attachmentTable).where(attachmentTable.messageId.in(idList)).execute());
		}
		defensiveDelete(ids, "messages", idList -> queryFactory.delete(messageTable).where(messageTable.cpaId.eq(cpaId)).execute());
		println("delete cpa " + cpaId + " in ebms-admin to delete it from the cache!!!");
	}

	private void cleanMessages(Instant dateFrom, boolean includeNoPersistDuration)
	{
		val messageIdPersistTimeQuery = queryFactory.select(messageTable.messageId).from(messageTable).where(messageTable.persistTime.loe(dateFrom));
		while (messageIdPersistTimeQuery.fetchCount() > 0)
		{
			println("Deleting bucket of 100000 entries (based on persistTime)....");
			val ids = messageIdPersistTimeQuery.limit(100000L).fetch();
			deleteMessagesIdList(ids);
		}
		if (includeNoPersistDuration)
		{
			val messageIdTimeStampQuery =
					queryFactory.select(messageTable.messageId).from(messageTable).where(messageTable.persistTime.isNull().and(messageTable.timeStamp.loe(dateFrom)));
			while (messageIdTimeStampQuery.fetchCount() > 0)
			{
				println("Deleting bucket of 100000 entries (includeNoPersistDuration=true)....");
				val idsWithoutPersistDuration = messageIdTimeStampQuery.limit(100000L).fetch();
				deleteMessagesIdList(idsWithoutPersistDuration);
			}
		}
	}

	private void deleteMessagesIdList(final List<String> idsBucket)
	{
		if (idsBucket.isEmpty())
		{
			println("\tno messages to delete");
		}
		else
		{
			defensiveDelete(idsBucket, "deliveryLogs", idList -> queryFactory.delete(deliveryLogTable).where(deliveryLogTable.messageId.in(idList)).execute());
			defensiveDelete(idsBucket, "deliveryTasks", idList -> queryFactory.delete(deliveryTaskTable).where(deliveryTaskTable.messageId.in(idList)).execute());
			defensiveDelete(idsBucket, "messageEvents", idList -> queryFactory.delete(messageEventTable).where(messageEventTable.messageId.in(idList)).execute());
			if (alternativeAttachmentImplementation())
			{
				ToLongFunction<List<String>> query = idList ->
				{
					val parameterListMessageIds = new MapSqlParameterSource("messageIds", idList);
					val ebmsMessageIds =
							namedParameterJdbcTemplate.queryForList("select id from ebms_message where message_id in (:messageIds)", parameterListMessageIds, String.class);
					val parameterListEmbsMessageIds = new MapSqlParameterSource("embsMessageIds", ebmsMessageIds);
					return namedParameterJdbcTemplate.update("delete from ebms_attachment where ebms_message_id in (:embsMessageIds)", parameterListEmbsMessageIds);
				};
				defensiveDelete(idsBucket, "attachments", query);
			}
			else
			{
				defensiveDelete(
						idsBucket,
						"attachments",
						idList -> queryFactory.delete(attachmentTable).where(attachmentTable.messageId.in((List<String>)idList)).execute());
			}
			defensiveDelete(idsBucket, "messages", idList -> queryFactory.delete(messageTable).where(messageTable.messageId.in(idList)).execute());
		}
	}

	private void defensiveDelete(List<String> ids, String tableString, ToLongFunction<List<String>> query)
	{
		val deleteBlockSize = 4000;
		println("Starting defensive delete of rows in " + tableString + "....");
		val partitions = new ArrayList<>(ListUtils.partition(ids, deleteBlockSize));
		val total = partitions.stream()
				.map(query::applyAsLong)
				.peek(count -> println("    " + count + " of rows in " + tableString + " deleted"))
				.mapToLong(Long::longValue)
				.sum();
		println("A total number of " + total + " " + tableString + " rows deleted");
	}

	@Override
	public void println(String s)
	{
		if (hasLog4jConfig())
			log.info(s);
		else
			SystemInterface.super.println(s);
	}

	@Override
	public void printWarn(String s)
	{
		val help = "\nFor help run nl.clockwork.ebms.admin.DBClean -h";
		if (hasLog4jConfig())
			log.warn(s + help);
		else
			SystemInterface.super.printWarn(s + help);
	}

	private static boolean hasLog4jConfig()
	{
		return StringUtils.isNotEmpty(System.getProperty(LOG4J_CONFIGURATION_FILE));
	}

	private static void printErr(Throwable t)
	{
		if (hasLog4jConfig())
			log.error("ERROR", t);
		else
			t.printStackTrace();
	}

	private static void printStatic(String s)
	{
		if (hasLog4jConfig())
			log.info(s);
		else
			System.out.println(s);
	}
}
