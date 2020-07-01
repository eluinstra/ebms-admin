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
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.querydsl.sql.SQLExpressions;
import com.querydsl.sql.SQLQueryFactory;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.var;
import lombok.experimental.FieldDefaults;
import nl.clockwork.ebms.Action;
import nl.clockwork.ebms.querydsl.model.QCpa;
import nl.clockwork.ebms.querydsl.model.QEbmsAttachment;
import nl.clockwork.ebms.querydsl.model.QEbmsEvent;
import nl.clockwork.ebms.querydsl.model.QEbmsEventLog;
import nl.clockwork.ebms.querydsl.model.QEbmsMessage;
import nl.clockwork.ebms.querydsl.model.QEbmsMessageEvent;
import nl.clockwork.ebms.transaction.DataSourceTransactionTemplate;
import nl.clockwork.ebms.transaction.TransactionTemplate;

@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@RequiredArgsConstructor
public class DBClean extends Start
{
	private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

	public static void main(String[] args) throws Exception
	{
		val options = createOptions();
		val cmd = new DefaultParser().parse(options,args);
		if (cmd.hasOption("h"))
			printUsage(options);
		
		try (val context = new AnnotationConfigApplicationContext(DBCleanConfig.class))
		{
			val dbClean = createDBClean(context);
			dbClean.execute(cmd);
		}
	}

	private static DBClean createDBClean(AnnotationConfigApplicationContext context)
	{
		val queryFactory = context.getBean(SQLQueryFactory.class);
		val transactionTemplate = context.getBean(DataSourceTransactionTemplate.class);
		val dbClean = new DBClean(queryFactory,transactionTemplate);
		return dbClean;
	}

	protected static Options createOptions()
	{
		val result = new Options();
		result.addOption("h",false,"print this message");
		result.addOption("cmd",true,"objects to clean [vales: cpa|messages]");
		result.addOption("cpaId",true,"the cpaId of the CPA to delete");
		result.addOption("dateFrom",true,"the date from which objects will be deleted [format: YYYYMMDD][default: " + dateFormatter.format(LocalDate.now().minusDays(30)) + "]");
		return result;
	}

	SQLQueryFactory queryFactory;
	TransactionTemplate transactionTemplate;
	BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	QCpa cpaTable = QCpa.cpa1;
	QEbmsMessage messageTable = QEbmsMessage.ebmsMessage;
	QEbmsAttachment attachmentTable = QEbmsAttachment.ebmsAttachment;
	QEbmsMessageEvent messageEventTable = QEbmsMessageEvent.ebmsMessageEvent;
	QEbmsEvent eventTable = QEbmsEvent.ebmsEvent;
	QEbmsEventLog eventLogTable = QEbmsEventLog.ebmsEventLog;
	
	private void execute(final org.apache.commons.cli.CommandLine cmd) throws Exception
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
				print(cmd.getOptionValue("cmd") + " not recognized");
		}
	}

	private boolean validateCleanCPA(CommandLine cmd)
	{
		if (!cmd.hasOption("cpaId"))
		{
			print("Option cpaId missing");
			return false;
		}
		return true;
	}

	private void executeCleanCPA(CommandLine cmd) throws IOException
	{
		val cpaId = cmd.getOptionValue("cpaId");
		Action action = () ->
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
				print("CPA " + cpaId + " not found!");
		};
		transactionTemplate.executeTransaction(action);
	}

	private void executeCleanMessages(CommandLine cmd) throws IOException
	{
		val dateFrom = createDateFrom(cmd.getOptionValue("dateFrom"));
		if (dateFrom != null)
		{
			print("using fromDate " + dateFrom);
			transactionTemplate.executeTransaction(() -> cleanMessages(dateFrom));
		}
		print("Unable to parse date " + cmd.getOptionValue("dateFrom"));
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

		var result = queryFactory.delete(eventLogTable).where(eventLogTable.messageId.in(selectMessageIdsByCpaId)).execute();
		print(result + " eventLogs deleted");

		result = queryFactory.delete(eventTable).where(eventTable.messageId.in(selectMessageIdsByCpaId)).execute();
		print(result + " events deleted");

		result = queryFactory.delete(messageEventTable).where(messageEventTable.messageId.in(selectMessageIdsByCpaId)).execute();
		print(result + " messageEvents deleted");

		result = queryFactory.delete(attachmentTable).where(attachmentTable.messageId.in(selectMessageIdsByCpaId)).execute();
		print(result + " attachments deleted");

		result = queryFactory.delete(messageTable).where(messageTable.cpaId.eq(cpaId)).execute();
		print(result + " messages deleted");

		//result = queryFactory.delete(cpaTable).where(cpaTable.cpaId.eq(cpaId)).execute();
		//print("cpa " + cpaId + " deleted");
		print("delete cpa " + cpaId + " in ebms-admin to delete it from the cache!!!");
	}

	private void cleanMessages(Instant dateFrom)
	{
		val selectMessageIdsByPersistTime = SQLExpressions.select(messageTable.messageId).from(messageTable).where(messageTable.persistTime.loe(dateFrom));

		var result = queryFactory.delete(eventLogTable).where(eventLogTable.messageId.in(selectMessageIdsByPersistTime)).execute();
		print(result + " eventLogs deleted");

		result = queryFactory.delete(eventTable).where(eventTable.messageId.in(selectMessageIdsByPersistTime)).execute();
		print(result + " events deleted");

		result = queryFactory.delete(messageEventTable).where(messageEventTable.messageId.in(selectMessageIdsByPersistTime)).execute();
		print(result + " messageEvents deleted");

		result = queryFactory.delete(attachmentTable).where(attachmentTable.messageId.in(selectMessageIdsByPersistTime)).execute();
		print(result + " attachments deleted");

		result = queryFactory.delete(messageTable).where(messageTable.persistTime.loe(dateFrom)).execute();
		print(result + " messages deleted");
	}

	private void print(String s)
	{
		System.out.println(s);
	}
}
