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

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import nl.clockwork.ebms.security.KeyStoreType;
import nl.clockwork.ebms.security.KeyStoreUtils;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString(onlyExplicitlyIncluded = true)
public class EbMSKeyStore
{
	private static Map<String, EbMSKeyStore> keyStores = new ConcurrentHashMap<>();
	@NonNull
	@ToString.Include
	String path;
	@NonNull
	KeyStore keyStore;
	@NonNull
	String password;
	String defaultAlias;

	public static EbMSKeyStore of(@NonNull KeyStoreType type, @NonNull String path, @NonNull String password) throws GeneralSecurityException, IOException
	{
		return of(type, path, password, null);
	}

	public static EbMSKeyStore of(@NonNull KeyStoreType type, @NonNull String path, @NonNull String password, String defaultAlias)
			throws GeneralSecurityException, IOException
	{
		if (!keyStores.containsKey(path))
			keyStores.put(path, new EbMSKeyStore(path, KeyStoreUtils.loadKeyStore(type, path, password), password, defaultAlias));
		return keyStores.get(path);
	}

	public Certificate getCertificate(String alias) throws KeyStoreException
	{
		return keyStore.getCertificate(alias);
	}

	public String getCertificateAlias(X509Certificate cert) throws KeyStoreException
	{
		return keyStore.getCertificateAlias(cert);
	}

	public Certificate[] getCertificateChain(String alias) throws KeyStoreException
	{
		return keyStore.getCertificateChain(alias);
	}

	public Key getKey(String alias, char[] password) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException
	{
		return keyStore.getKey(alias, password);
	}
}
