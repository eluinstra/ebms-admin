"use strict";(self.webpackChunkdocumentation=self.webpackChunkdocumentation||[]).push([[275],{4306:(e,t,r)=>{r.r(t),r.d(t,{assets:()=>c,contentTitle:()=>a,default:()=>d,frontMatter:()=>o,metadata:()=>s,toc:()=>h});const s=JSON.parse('{"id":"ebms-admin/ssl","title":"SSL Configuration","description":"To configure SSL for the EbMS interface you have to create and configure a keystore and a truststore. You can and should also configure SSL for the Web/SOAP/REST interface.","source":"@site/versioned_docs/version-2.19.x/ebms-admin/ssl.md","sourceDirName":"ebms-admin","slug":"/ebms-admin/ssl","permalink":"/ebms-admin/docs/ebms-admin/ssl","draft":false,"unlisted":false,"editUrl":"https://github.com/eluinstra/ebms-admin/tree/documentation/versioned_docs/version-2.19.x/ebms-admin/ssl.md","tags":[],"version":"2.19.x","sidebarPosition":6,"frontMatter":{"sidebar_position":6},"sidebar":"tutorialSidebar","previous":{"title":"Properties","permalink":"/ebms-admin/docs/ebms-admin/properties"},"next":{"title":"Database support","permalink":"/ebms-admin/docs/ebms-admin/database"}}');var i=r(4848),n=r(8453);const o={sidebar_position:6},a="SSL Configuration",c={},h=[{value:"Keystore",id:"keystore",level:2},{value:"Create a keystore",id:"create-a-keystore",level:3},{value:"Create a private key and a CSR",id:"create-a-private-key-and-a-csr",level:4},{value:"Sign the certificate",id:"sign-the-certificate",level:4},{value:"Create the SSL keystore",id:"create-the-ssl-keystore",level:4},{value:"Configure the EbMS SSL server keystore",id:"configure-the-ebms-ssl-server-keystore",level:3},{value:"Configure the EbMS SSL client keystore",id:"configure-the-ebms-ssl-client-keystore",level:3},{value:"Configure the Web/SOAP/REST SSL server keystore",id:"configure-the-websoaprest-ssl-server-keystore",level:3},{value:"Truststore",id:"truststore",level:2},{value:"Create a truststore",id:"create-a-truststore",level:3},{value:"Create a Java keystore",id:"create-a-java-keystore",level:4},{value:"Convert the Java keystore to a PKSC12 keystore",id:"convert-the-java-keystore-to-a-pksc12-keystore",level:4},{value:"Configure the EbMS truststore",id:"configure-the-ebms-truststore",level:3},{value:"Configure the Web/SOAP/REST SSL truststore",id:"configure-the-websoaprest-ssl-truststore",level:3}];function l(e){const t={a:"a",code:"code",h1:"h1",h2:"h2",h3:"h3",h4:"h4",header:"header",li:"li",ol:"ol",p:"p",pre:"pre",...(0,n.R)(),...e.components};return(0,i.jsxs)(i.Fragment,{children:[(0,i.jsx)(t.header,{children:(0,i.jsx)(t.h1,{id:"ssl-configuration",children:"SSL Configuration"})}),"\n",(0,i.jsxs)(t.p,{children:["To configure SSL for the EbMS interface you have to create and configure a ",(0,i.jsx)(t.a,{href:"#keystore",children:"keystore"})," and a ",(0,i.jsx)(t.a,{href:"#truststore",children:"truststore"}),". You can and should also configure SSL for the Web/SOAP/REST interface."]}),"\n",(0,i.jsx)(t.h2,{id:"keystore",children:"Keystore"}),"\n",(0,i.jsx)(t.p,{children:"You can configure 2 SSL keystores for the EbMS interface (and 2 for the Web/SOAP/REST interface):"}),"\n",(0,i.jsxs)(t.ol,{children:["\n",(0,i.jsx)(t.li,{children:"A SSL Server keystore to configure TLS"}),"\n",(0,i.jsx)(t.li,{children:"A SSL Client keystore to configure mTLS"}),"\n"]}),"\n",(0,i.jsx)(t.p,{children:"In many cases the SSL server certificates are the same as the SSL client certificates. If so you can use the same keystore for both. If not create and configure 2 separate keystores."}),"\n",(0,i.jsx)(t.h3,{id:"create-a-keystore",children:"Create a keystore"}),"\n",(0,i.jsx)(t.p,{children:"To create the SSL keystore you need the private key, the signed certificate and certificate chain. The signed certificate is normally also needed to request/create a CPA and you can configure the EbMS adapter to validate the received certificate with the one in the corresponding CPA (which is off by default)."}),"\n",(0,i.jsx)(t.p,{children:"To create a keystore you need openssl."}),"\n",(0,i.jsx)(t.h4,{id:"create-a-private-key-and-a-csr",children:"Create a private key and a CSR"}),"\n",(0,i.jsx)(t.pre,{children:(0,i.jsx)(t.code,{className:"language-sh",children:'openssl req -new -sha256 -newkey rsa:4096 -keyout localhost.key -out localhost.csr -subj "/C=NL/ST=Groningen/L=Groningen/O=Ordina/OU=OSD/CN=ebms.ordina.nl"\n'})}),"\n",(0,i.jsxs)(t.p,{children:["Where you replace the subject with your own parameters (where the Common Name (CN) should/can contain the EbMS server's Domain Name). This results in the file ",(0,i.jsx)(t.code,{children:"localhost.key"})," that contains the private key and the file ",(0,i.jsx)(t.code,{children:"localhost.csr"})," that contains the Certificate Signing Request (CSR)."]}),"\n",(0,i.jsx)(t.h4,{id:"sign-the-certificate",children:"Sign the certificate"}),"\n",(0,i.jsxs)(t.p,{children:["Send the CSR to your Certificate Authority to sign the certificate. The private key ",(0,i.jsx)(t.code,{children:"localhost.key"})," has to remain secret. You should receive a Signed Certificate and the corresponding Certificate Chain."]}),"\n",(0,i.jsx)(t.h4,{id:"create-the-ssl-keystore",children:"Create the SSL keystore"}),"\n",(0,i.jsxs)(t.p,{children:["Put the Signed certificate in the file ",(0,i.jsx)(t.code,{children:"localhost.pem"})," and the certificate chain in the file ",(0,i.jsx)(t.code,{children:"ca.pem"}),"."]}),"\n",(0,i.jsx)(t.pre,{children:(0,i.jsx)(t.code,{className:"language-sh",children:'openssl pkcs12 -export -out keystore.p12 -name "localhost" -inkey localhost.key -in localhost.pem -certfile ca.pem\n'})}),"\n",(0,i.jsxs)(t.p,{children:["This results in the SSL keystore ",(0,i.jsx)(t.code,{children:"keystore.p12"}),"."]}),"\n",(0,i.jsx)(t.h3,{id:"configure-the-ebms-ssl-server-keystore",children:"Configure the EbMS SSL server keystore"}),"\n",(0,i.jsxs)(t.p,{children:["See ",(0,i.jsx)(t.a,{href:"properties#ssl-server-keystore",children:"here"})," to configure the EbMS server keystore, where ",(0,i.jsx)(t.code,{children:"keystore.path"})," points to the SSL server keystore."]}),"\n",(0,i.jsx)(t.h3,{id:"configure-the-ebms-ssl-client-keystore",children:"Configure the EbMS SSL client keystore"}),"\n",(0,i.jsxs)(t.p,{children:["See ",(0,i.jsx)(t.a,{href:"/ebms-admin/docs/ebms-core/properties#ssl-client-keystore",children:"here"})," to configure the EbMS client keystore, where ",(0,i.jsx)(t.code,{children:"client.keystore.path"})," points to the SSL client keystore."]}),"\n",(0,i.jsx)(t.h3,{id:"configure-the-websoaprest-ssl-server-keystore",children:"Configure the Web/SOAP/REST SSL server keystore"}),"\n",(0,i.jsxs)(t.p,{children:["See ",(0,i.jsx)(t.a,{href:"command#start-with-https",children:"here"})," to configure the Web/SOAP/REST server keystore, where ",(0,i.jsx)(t.code,{children:"-keystorePath"})," points to the SSL server keystore."]}),"\n",(0,i.jsx)(t.h2,{id:"truststore",children:"Truststore"}),"\n",(0,i.jsx)(t.h3,{id:"create-a-truststore",children:"Create a truststore"}),"\n",(0,i.jsx)(t.p,{children:"The EbMS truststore contains the SSL certificate chains for all trusted parties and the EbMS signing and encryption certificate chains of the certificates defined in the CPAs. The Web/SOAP/REST truststore only contains SSL certificate chain if mTLS is configured."}),"\n",(0,i.jsx)(t.p,{children:"Put all the certificates of the certificate chain in separate files. The example below has a chain that contains 2 certificates:"}),"\n",(0,i.jsxs)(t.ol,{children:["\n",(0,i.jsxs)(t.li,{children:["a root certificate in the file ",(0,i.jsx)(t.code,{children:"root.pem"})]}),"\n",(0,i.jsxs)(t.li,{children:["an intermediate certificate in the file ",(0,i.jsx)(t.code,{children:"intermediate.pem"})]}),"\n"]}),"\n",(0,i.jsx)(t.p,{children:"To create a truststore you need openssl and keytool (which is part of a JDK)."}),"\n",(0,i.jsx)(t.h4,{id:"create-a-java-keystore",children:"Create a Java keystore"}),"\n",(0,i.jsx)(t.p,{children:"You only need to trust the first certificate of a chain if you add the certificates in the right order starting at the root certificate."}),"\n",(0,i.jsx)(t.pre,{children:(0,i.jsx)(t.code,{className:"language-sh",children:"keytool -import -trustcacerts -alias root -file root.pem -keystore truststore.jks\nkeytool -import -trustcacerts -alias intermediate -file intermediate.pem -keystore truststore.jks\n"})}),"\n",(0,i.jsxs)(t.p,{children:["This results in the Java keystore file ",(0,i.jsx)(t.code,{children:"truststore.jks"}),"."]}),"\n",(0,i.jsx)(t.h4,{id:"convert-the-java-keystore-to-a-pksc12-keystore",children:"Convert the Java keystore to a PKSC12 keystore"}),"\n",(0,i.jsx)(t.pre,{children:(0,i.jsx)(t.code,{className:"language-sh",children:"keytool -importkeystore -srckeystore truststore.jks -srcstoretype JKS -destkeystore truststore.p12 -deststoretype PKCS12\n"})}),"\n",(0,i.jsxs)(t.p,{children:["This results in the PKCS12 keystore file ",(0,i.jsx)(t.code,{children:"truststore.p12"}),"."]}),"\n",(0,i.jsx)(t.h3,{id:"configure-the-ebms-truststore",children:"Configure the EbMS truststore"}),"\n",(0,i.jsxs)(t.p,{children:["See ",(0,i.jsx)(t.a,{href:"/ebms-admin/docs/ebms-core/properties#truststore",children:"here"})," to configure the EbMS truststore, where ",(0,i.jsx)(t.code,{children:"truststore.path"})," points to the SSL truststore."]}),"\n",(0,i.jsx)(t.h3,{id:"configure-the-websoaprest-ssl-truststore",children:"Configure the Web/SOAP/REST SSL truststore"}),"\n",(0,i.jsxs)(t.p,{children:["See ",(0,i.jsx)(t.a,{href:"command#start-with-https-and-client-authentication",children:"here"})," to configure the Web/SOAP/REST client keystore, where ",(0,i.jsx)(t.code,{children:"-clientTruststorePath"})," points to the SSL truststore."]})]})}function d(e={}){const{wrapper:t}={...(0,n.R)(),...e.components};return t?(0,i.jsx)(t,{...e,children:(0,i.jsx)(l,{...e})}):l(e)}},8453:(e,t,r)=>{r.d(t,{R:()=>o,x:()=>a});var s=r(6540);const i={},n=s.createContext(i);function o(e){const t=s.useContext(n);return s.useMemo((function(){return"function"==typeof e?e(t):{...t,...e}}),[t,e])}function a(e){let t;return t=e.disableParentContext?"function"==typeof e.components?e.components(i):e.components||i:o(e.components),s.createElement(n.Provider,{value:t},e.children)}}}]);