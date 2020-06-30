package nl.clockwork.ebms.admin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
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
	public static void main(String[] args) throws Exception
	{
		val options = createOptions();
		val cmd = new DefaultParser().parse(options,args);
		if (cmd.hasOption("h"))
			printUsage(options);
		
		try (val context = new AnnotationConfigApplicationContext())
		{
			context.register(DBCleanConfig.class);
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
		result.addOption("dateFrom",true,"the date from which objects will be deleted");
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
	
	private void execute(final org.apache.commons.cli.CommandLine cmd) throws Exception
	{
		switch(cmd.getOptionValue("cmd",null))
		{
			case("cpa"):
				validateCleanCPA(cmd);
				executeCleanCPA(cmd);
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
		val cpaId = cmd.getOptionValue("cpa");
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

		result = queryFactory.delete(cpaTable).where(cpaTable.cpaId.eq(cpaId)).execute();
		print("CPA " + cpaId + " deleted");
	}

	private void print(String s)
	{
		System.out.println(s);
	}
}
