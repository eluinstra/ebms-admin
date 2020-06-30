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
import org.springframework.transaction.PlatformTransactionManager;

import com.querydsl.sql.SQLExpressions;
import com.querydsl.sql.SQLQueryFactory;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.var;
import lombok.experimental.FieldDefaults;
import nl.clockwork.ebms.querydsl.model.QCpa;
import nl.clockwork.ebms.querydsl.model.QEbmsAttachment;
import nl.clockwork.ebms.querydsl.model.QEbmsEvent;
import nl.clockwork.ebms.querydsl.model.QEbmsEventLog;
import nl.clockwork.ebms.querydsl.model.QEbmsMessage;
import nl.clockwork.ebms.querydsl.model.QEbmsMessageEvent;

@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@RequiredArgsConstructor
public class DBClean extends Start
{
	private static final int DAY_IN_SECONDS = 60 * 60 * 24;

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
		val transactionManager = context.getBean("dataSourceTransactionManager",PlatformTransactionManager.class);
		val dbClean = new DBClean(queryFactory,transactionManager);
		return dbClean;
	}

	protected static Options createOptions()
	{
		val result = new Options();
		result.addOption("h",false,"print this message");
		result.addOption("cmd",true,"objects to clean [cpa|messages]");
		result.addOption("cpaId",true,"the cpaId of the CPA to delete");
		result.addOption("dateFrom",true,"the date from which objects will be deleted (format: YYYYMMDD)");
		return result;
	}

	SQLQueryFactory queryFactory;
	PlatformTransactionManager transactionManager;
	BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	QCpa cpaTable = QCpa.cpa1;
	QEbmsMessage messageTable = QEbmsMessage.ebmsMessage;
	QEbmsAttachment attachmentTable = QEbmsAttachment.ebmsAttachment;
	QEbmsMessageEvent messageEventTable = QEbmsMessageEvent.ebmsMessageEvent;
	QEbmsEvent eventTable = QEbmsEvent.ebmsEvent;
	QEbmsEventLog eventLogTable = QEbmsEventLog.ebmsEventLog;
	private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
	
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
		val status = transactionManager.getTransaction(null);
		try
		{
			if (queryFactory.select(cpaTable.cpaId).from(cpaTable).where(cpaTable.cpaId.eq(cpaId)).fetchCount() > 0)
			{
				val ok = readLine("WARNING: This command will delete all messages and data related to cpa " + cpaId + ". Are you sure?(Y/N): ",reader);
				if (ok.equalsIgnoreCase("Y"))
					cleanCPA(cpaId);
			}
			else
				print("CPA " + cpaId + " not found!");
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
	}

	private Instant createDateFrom(String s)
	{
		try
		{
			val date = StringUtils.isEmpty(s) ? LocalDate.now().minusDays(30) : LocalDate.parse(s,dateFormatter);
			val result = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
			print("using fromDate " + result);
			return result;
		}
		catch (DateTimeParseException e)
		{
			print("Unable to parse date " + s);
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
