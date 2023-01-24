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
package nl.clockwork.ebms.admin.web.service.cpa;


import java.util.ArrayList;
import java.util.Iterator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import nl.clockwork.ebms.admin.Utils;
import nl.clockwork.ebms.cpa.certificate.CertificateMapping;
import nl.clockwork.ebms.cpa.certificate.CertificateMappingService;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(staticName = "of")
public class CertificateMappingDataProvider implements IDataProvider<CertificateMapping>
{
	private static final long serialVersionUID = 1L;
	CertificateMappingService certificateMappingService;

	@Override
	public Iterator<? extends CertificateMapping> iterator(long first, long count)
	{
		val certificateMappings = Utils.toList(certificateMappingService.getCertificateMappings());
		return certificateMappings == null ? new ArrayList<CertificateMapping>().iterator() : certificateMappings.iterator();
	}

	@Override
	public IModel<CertificateMapping> model(CertificateMapping certificateMapping)
	{
		return Model.of(certificateMapping);
	}

	@Override
	public long size()
	{
		val certificateMappings = Utils.toList(certificateMappingService.getCertificateMappings());
		return certificateMappings == null ? 0 : certificateMappings.size();
	}

	@Override
	public void detach()
	{
	}
}
