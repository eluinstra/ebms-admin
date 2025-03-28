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
package nl.clockwork.ebms.querydsl.model;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.BooleanPath;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;
import jakarta.annotation.Generated;
import java.sql.Types;

/**
 * QDeliveryTask is a Querydsl query type for QDeliveryTask
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QDeliveryTask extends com.querydsl.sql.RelationalPathBase<QDeliveryTask>
{

	private static final long serialVersionUID = 462050168;

	public static final QDeliveryTask deliveryTask = new QDeliveryTask("delivery_task");

	public final StringPath cpaId = createString("cpaId");

	public final BooleanPath isConfidential = createBoolean("isConfidential");

	public final StringPath messageId = createString("messageId");

	public final StringPath receiveChannelId = createString("receiveChannelId");

	public final NumberPath<Integer> retries = createNumber("retries", Integer.class);

	public final StringPath sendChannelId = createString("sendChannelId");

	public final StringPath serverId = createString("serverId");

	public final DateTimePath<java.time.Instant> timeStamp = createDateTime("timeStamp", java.time.Instant.class);

	public final DateTimePath<java.time.Instant> timeToLive = createDateTime("timeToLive", java.time.Instant.class);

	public QDeliveryTask(String variable)
	{
		super(QDeliveryTask.class, forVariable(variable), "PUBLIC", "delivery_task");
		addMetadata();
	}

	public QDeliveryTask(String variable, String schema, String table)
	{
		super(QDeliveryTask.class, forVariable(variable), schema, table);
		addMetadata();
	}

	public QDeliveryTask(String variable, String schema)
	{
		super(QDeliveryTask.class, forVariable(variable), schema, "delivery_task");
		addMetadata();
	}

	public QDeliveryTask(Path<? extends QDeliveryTask> path)
	{
		super(path.getType(), path.getMetadata(), "PUBLIC", "delivery_task");
		addMetadata();
	}

	public QDeliveryTask(PathMetadata metadata)
	{
		super(QDeliveryTask.class, metadata, "PUBLIC", "delivery_task");
		addMetadata();
	}

	public void addMetadata()
	{
		addMetadata(cpaId, ColumnMetadata.named("cpa_id").withIndex(1).ofType(Types.VARCHAR).withSize(256).notNull());
		addMetadata(isConfidential, ColumnMetadata.named("is_confidential").withIndex(7).ofType(Types.BOOLEAN).withSize(0).notNull());
		addMetadata(messageId, ColumnMetadata.named("message_id").withIndex(3).ofType(Types.VARCHAR).withSize(256).notNull());
		addMetadata(receiveChannelId, ColumnMetadata.named("receive_channel_id").withIndex(2).ofType(Types.VARCHAR).withSize(256).notNull());
		addMetadata(retries, ColumnMetadata.named("retries").withIndex(6).ofType(Types.SMALLINT).withSize(16).notNull());
		addMetadata(sendChannelId, ColumnMetadata.named("send_channel_id").withIndex(9).ofType(Types.VARCHAR).withSize(256));
		addMetadata(serverId, ColumnMetadata.named("server_id").withIndex(8).ofType(Types.VARCHAR).withSize(256));
		addMetadata(timeStamp, ColumnMetadata.named("time_stamp").withIndex(5).ofType(Types.TIMESTAMP).withSize(26).notNull());
		addMetadata(timeToLive, ColumnMetadata.named("time_to_live").withIndex(4).ofType(Types.TIMESTAMP).withSize(26));
	}

}
