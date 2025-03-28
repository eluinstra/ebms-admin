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
import nl.clockwork.ebms.EbMSMessageStatus;

public class EbMSMessageStatusType extends AbstractType<EbMSMessageStatus>
{
	public EbMSMessageStatusType(int type)
	{
		super(type);
	}

	@Override
	public Class<EbMSMessageStatus> getReturnedClass()
	{
		return EbMSMessageStatus.class;
	}

	@Override
	public EbMSMessageStatus getValue(ResultSet rs, int startIndex) throws SQLException
	{
		val id = rs.getObject(startIndex, Integer.class);
		return id != null ? EbMSMessageStatus.get(id).orElseThrow(() -> new IllegalArgumentException("EbMSMessageStatus " + id + " is not valid!")) : null;
	}

	@Override
	public void setValue(PreparedStatement st, int startIndex, EbMSMessageStatus value) throws SQLException
	{
		st.setInt(startIndex, value != null ? value.getId() : null);
	}
}
