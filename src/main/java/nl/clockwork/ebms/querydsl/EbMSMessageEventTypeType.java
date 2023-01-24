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
package nl.clockwork.ebms.querydsl;


import com.querydsl.sql.types.AbstractType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.val;
import nl.clockwork.ebms.event.MessageEventType;

public class EbMSMessageEventTypeType extends AbstractType<MessageEventType>
{
	public EbMSMessageEventTypeType(int type)
	{
		super(type);
	}

	@Override
	public Class<MessageEventType> getReturnedClass()
	{
		return MessageEventType.class;
	}

	@Override
	public MessageEventType getValue(ResultSet rs, int startIndex) throws SQLException
	{
		val id = rs.getObject(startIndex,Integer.class);
		return id != null ? MessageEventType.get(id).orElseThrow(() -> new IllegalArgumentException("EbMSMessageEventType " + id + " is not valid!")) : null;
	}

	@Override
	public void setValue(PreparedStatement st, int startIndex, MessageEventType value) throws SQLException
	{
		st.setInt(startIndex,value != null ? value.getId() : null);
	}
}
