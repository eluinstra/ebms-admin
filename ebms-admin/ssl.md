---
sort: 11
---

# SSL Configuration

To configure SSL for the EbMS interface you have to create and configure a [keystore](#keystore) and a [truststore](#truststore).

## Keystore

You can configure 2 SSL keystores in the EbMS adapter

1. A SSL Server keystore
2. A SSL Client keystore to configure mTLS

In many cases the SSL server certificates are the same as the SSL client certificates. If so you can use the same keystore for both. If not create and configure 2 separate keystores.

### Create a keystore

To create the SSL keystore you need the private key, the signed certificate and certificate chain. The signed certificate is normally also needed to request/create a CPA and you can configure the EbMS adapter to validate the received certificate with the one in the corresponding CPA (which is off by default).

To create a keystore you need openssl.

#### Create a private key and a CSR

```sh
openssl req -new -sha256 -newkey rsa:4096 -keyout localhost.key -out localhost.csr -subj "/C=NL/ST=Groningen/L=Groningen/O=Ordina/OU=OSD/CN=ebms.ordina.nl"
```

Where you replace the subject with your own parameters (where the Common Name (CN) should/can contain the EbMS server's Domain Name). This results in the file `localhost.key` that contains the private key and the file `localhost.csr` that contains the Certificate Signing Request (CSR).

#### Sign the certificate

Send the CSR to your Certificate Authority to sign the certificate. The private key `localhost.key` has to remain secret. You should receive a Signed Certificate and the corresponding Certificate Chain.

#### Create the SSL keystore

Put the Signed certificate in the file `localhost.pem` and the certificate chain in the file `ca.pem`.

```sh
openssl pkcs12 -export -out keystore.p12 -name "localhost" -inkey localhost.key -in localhost.pem -certfile ca.pem
```

This results in the SSL keystore `keystore.p12`.

### Configure the server keystore

See [here]({{ site.baseurl }}/ebms-admin/properties.html#ssl-server-keystore) to configure the EbMS server keystore, where `keystore.path` points to the SSL server keystore.

### Configure the client keystore

See [here]({{ site.baseurl }}/ebms-core/properties.html#ssl-client-keystore) to configure the EbMS client keystore, where `client.keystore.path` points to the SSL client keystore. 

## Truststore

### Create a truststore

The EbMS truststore contains the SSL certificate chains for all trusted parties and the EbMS signing and encryption certificate chains of the certificates defined in the CPAs.

Put all the certificates of the certificate chain in separate files. The example below has a chain that contains 2 certificates:

1. a root certificate in the file `root.pem`
2. an intermediate certificate in the file `intermediate.pem`

To create a truststore you need openssl and keytool (which is part of a JDK).

#### Create a Java keystore

You only need to trust the first certificate of a chain if you add the certificates in the right order starting at the root certificate.

```sh
keytool -import -trustcacerts -alias root -file root.pem -keystore truststore.jks
keytool -import -trustcacerts -alias intermediate -file intermediate.pem -keystore truststore.jks
```

This results in the Java keystore file `truststore.jks`.

#### Convert the Java keystore to a PKSC12 keystore

```sh
keytool -importkeystore -srckeystore truststore.jks -srcstoretype JKS -destkeystore truststore.p12 -deststoretype PKCS12
```

This results in the PKCS12 keystore file `truststore.p12`.

### Configure the truststore

See [here]({{ site.baseurl }}/ebms-core/properties.html#truststore) to configure the EbMS truststore, where `truststore.path` points to the SSL truststore.
