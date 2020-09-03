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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.StringUtils;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;

import com.querydsl.sql.SQLExpressions;
import com.querydsl.sql.SQLQueryFactory;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.var;
import lombok.experimental.FieldDefaults;
import nl.clockwork.ebms.cpa.QCpa;
import nl.clockwork.ebms.event.QMessageEvent;
import nl.clockwork.ebms.model.QEbmsAttachment;
import nl.clockwork.ebms.model.QEbmsMessage;
import nl.clockwork.ebms.task.QSendLog;
import nl.clockwork.ebms.task.QSendTask;

@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@RequiredArgsConstructor
public class DBClean implements SystemInterface
{
	private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
	TextIO textIO = TextIoFactory.getTextIO();

	public static void main(String[] args) throws Exception
	{
		val options = createOptions();
		val cmd = new DefaultParser().parse(options,args);
		if (cmd.hasOption("h"))
			printUsage(options);
		
		try (val context = new AnnotationConfigApplicationContext(DBConfig.class))
		{
			val dbClean = createDBClean(context);
			dbClean.execute(cmd);
		}
	}

	private static Options createOptions()
	{
		val result = new Options();
		result.addOption("h",false,"print this message");
		result.addOption("cmd",true,"objects to clean [vales: cpa|messages]");
		result.addOption("cpaId",true,"the cpaId of the CPA to delete");
		result.addOption("dateFrom",true,"the date from which objects will be deleted [format: YYYYMMDD][default: " + dateFormatter.format(LocalDate.now().minusDays(30)) + "]");
		return result;
	}

	private static void printUsage(Options options)
	{
		val formatter = new HelpFormatter();
		formatter.printHelp("DBClean",options,true);
		System.exit(0);
	}

	private static DBClean createDBClean(AnnotationConfigApplicationContext context)
	{
		val queryFactory = context.getBean(SQLQueryFactory.class);
		val transactionManager = context.getBean("dataSourceTransactionManager",PlatformTransactionManager.class);
		return new DBClean(queryFactory,transactionManager);
	}

	@NonNull
	SQLQueryFactory queryFactory;
	@NonNull
	PlatformTransactionManager transactionManager;
	BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	QCpa cpaTable = QCpa.cpa1;
	QEbmsMessage messageTable = QEbmsMessage.ebmsMessage;
	QEbmsAttachment attachmentTable = QEbmsAttachment.ebmsAttachment;
	QMessageEvent messageEventTable = QMessageEvent.ebmsMessageEvent;
	QSendTask sendTaskTable = QSendTask.sendTask;
	QSendLog sendLogTable = QSendLog.sendLog;
	
	private void execute(final CommandLine cmd) throws Exception
	{
		switch(cmd.getOptionValue("cmd",""))
		{
			case("cpa"):
				validateCleanCPA(cmd);
				executeCleanCPA(cmd);
				break;
			case("messages"):
				executeCleanMessages(cmd);
				break;
			default:
				println(cmd.getOptionValue("cmd") + " not recognized");
		}
	}

	private boolean validateCleanCPA(CommandLine cmd)
	{
		if (!cmd.hasOption("cpaId"))
		{
			println("Option cpaId missing");
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
		}
		catch (Exception e)
		{
			transactionManager.rollback(status);
		}
		transactionManager.commit(status);
	}

	private void executeCleanMessages(CommandLine cmd) throws IOException
	{
		val dateFrom = createDateFrom(cmd.getOptionValue("dateFrom"));
		if (dateFrom != null)
		{
			println("using fromDate " + dateFrom);
			val status = transactionManager.getTransaction(null);
			try
			{
				cleanMessages(dateFrom);
			}
			catch (Exception e)
			{
				transactionManager.rollback(status);
			}
			transactionManager.commit(status);
		}
		println("Unable to parse date " + cmd.getOptionValue("dateFrom"));
	}

	private static Instant createDateFrom(String s)
	{
		try
		{
			val date = StringUtils.isEmpty(s) ? LocalDate.now().minusDays(30) : LocalDate.parse(s,dateFormatter);
			return date.atStartOfDay(ZoneId.systemDefault()).toInstant();
		}
		catch (DateTimeParseException e)
		{
			return null;
		}
	}

	private void cleanCPA(String cpaId)
	{
		val selectMessageIdsByCpaId = SQLExpressions.select(messageTable.messageId).from(messageTable).where(messageTable.cpaId.eq(cpaId));

		var result = queryFactory.delete(sendLogTable).where(sendLogTable.messageId.in(selectMessageIdsByCpaId)).execute();
		println(result + " sendLogs deleted");

		result = queryFactory.delete(sendTaskTable).where(sendTaskTable.messageId.in(selectMessageIdsByCpaId)).execute();
		println(result + " sendTasks deleted");

		result = queryFactory.delete(messageEventTable).where(messageEventTable.messageId.in(selectMessageIdsByCpaId)).execute();
		println(result + " messageEvents deleted");

		result = queryFactory.delete(attachmentTable).where(attachmentTable.messageId.in(selectMessageIdsByCpaId)).execute();
		println(result + " attachments deleted");

		result = queryFactory.delete(messageTable).where(messageTable.cpaId.eq(cpaId)).execute();
		println(result + " messages deleted");

		//result = queryFactory.delete(cpaTable).where(cpaTable.cpaId.eq(cpaId)).execute();
		//print("cpa " + cpaId + " deleted");
		println("delete cpa " + cpaId + " in ebms-admin to delete it from the cache!!!");
	}

	private void cleanMessages(Instant dateFrom)
	{
		val selectMessageIdsByPersistTime = SQLExpressions.select(messageTable.messageId).from(messageTable).where(messageTable.persistTime.loe(dateFrom));

		var result = queryFactory.delete(sendLogTable).where(sendLogTable.messageId.in(selectMessageIdsByPersistTime)).execute();
		println(result + " sendLogs deleted");

		result = queryFactory.delete(sendTaskTable).where(sendTaskTable.messageId.in(selectMessageIdsByPersistTime)).execute();
		println(result + " sendTasks deleted");

		result = queryFactory.delete(messageEventTable).where(messageEventTable.messageId.in(selectMessageIdsByPersistTime)).execute();
		println(result + " messageEvents deleted");

		result = queryFactory.delete(attachmentTable).where(attachmentTable.messageId.in(selectMessageIdsByPersistTime)).execute();
		println(result + " attachments deleted");

		result = queryFactory.delete(messageTable).where(messageTable.persistTime.loe(dateFrom)).execute();
		println(result + " messages deleted");
	}
}
