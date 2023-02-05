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
package nl.clockwork.ebms.admin.web.cpa;

import static io.vavr.API.*;
import static io.vavr.Predicates.*;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import nl.clockwork.ebms.admin.dao.EbMSDAO;
import nl.clockwork.ebms.admin.model.CPA;
import org.apache.wicket.model.LoadableDetachableModel;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(staticName = "of")
public class CPADataModel extends LoadableDetachableModel<CPA>
{
	private static final long serialVersionUID = 1L;
	@NonNull
	EbMSDAO ebMSDAO;
	@NonNull
	String cpaId;

	public static CPADataModel of(EbMSDAO ebMSDAO, CPA cpa)
	{
		return of(ebMSDAO, cpa.getCpaId());
	}

	@Override
	protected CPA load()
	{
		return ebMSDAO.findCPA(cpaId);
	}

	@Override
	public int hashCode()
	{
		return cpaId.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		return Match(obj).of(Case($(this), true), Case($(null), false), Case($(instanceOf(CPADataModel.class)), o -> cpaId.equals(o.cpaId)), Case($(), false));
	}
}
