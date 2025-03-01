"use strict";(self.webpackChunkdocumentation=self.webpackChunkdocumentation||[]).push([[17],{2388:(e,r,s)=>{s.r(r),s.d(r,{assets:()=>a,contentTitle:()=>d,default:()=>h,frontMatter:()=>o,metadata:()=>i,toc:()=>c});const i=JSON.parse('{"id":"ebms-admin/properties","title":"Properties","description":"Below the default properties of ebms-admin. For the default properties of ebms-core see here.","source":"@site/docs/ebms-admin/properties.md","sourceDirName":"ebms-admin","slug":"/ebms-admin/properties","permalink":"/ebms-admin/docs/ebms-admin/properties","draft":false,"unlisted":false,"editUrl":"https://github.com/eluinstra/ebms-admin/tree/documentation/docs/ebms-admin/properties.md","tags":[],"version":"current","sidebarPosition":5,"frontMatter":{"sidebar_position":5},"sidebar":"tutorialSidebar","previous":{"title":"Command Line Options","permalink":"/ebms-admin/docs/ebms-admin/command"},"next":{"title":"SSL Configuration","permalink":"/ebms-admin/docs/ebms-admin/ssl"}}');var n=s(4848),t=s(8453);const o={sidebar_position:5},d="Properties",a={},c=[{value:"Override Properties",id:"override-properties",level:2},{value:"Basic Properties",id:"basic-properties",level:2},{value:"Default Properties",id:"default-properties",level:2},{value:"Database",id:"database",level:3},{value:"EbMS Server",id:"ebms-server",level:3},{value:"SSL Server keystore",id:"ssl-server-keystore",level:3},{value:"SSL",id:"ssl",level:3},{value:"User Interface",id:"user-interface",level:3}];function l(e){const r={a:"a",code:"code",h1:"h1",h2:"h2",h3:"h3",header:"header",li:"li",p:"p",pre:"pre",ul:"ul",...(0,t.R)(),...e.components};return(0,n.jsxs)(n.Fragment,{children:[(0,n.jsx)(r.header,{children:(0,n.jsx)(r.h1,{id:"properties",children:"Properties"})}),"\n",(0,n.jsxs)(r.p,{children:["Below the ",(0,n.jsx)(r.a,{href:"#default-properties",children:"default properties"})," of ebms-admin. For the default properties of ebms-core see ",(0,n.jsx)(r.a,{href:"/ebms-admin/docs/ebms-core/properties",children:"here"}),"."]}),"\n",(0,n.jsx)(r.h2,{id:"override-properties",children:"Override Properties"}),"\n",(0,n.jsxs)(r.p,{children:["To override the default properties create the file ",(0,n.jsx)(r.code,{children:"ebms-admin.embedded.properties"})," in the ",(0,n.jsx)(r.a,{href:"command#start-with-config-directory-conf",children:"configDir"})," and add the properties to that file."]}),"\n",(0,n.jsxs)(r.p,{children:["You can also configure the basic properties at ",(0,n.jsx)(r.a,{href:"https://localhost:8080/wicket/bookmarkable/nl.clockwork.ebms.admin.web.configuration.EbMSAdminPropertiesPage",children:"EbMSAdminPropertiesPage"})," after you started ebms-admin. If you want to override 'advanced' properties that are not included in the ",(0,n.jsx)(r.code,{children:"ebms-admin.embedded.properties"})," file that is created, create the file ",(0,n.jsx)(r.code,{children:"ebms-admin.embedded.advanced.properties"})," in the ",(0,n.jsx)(r.a,{href:"command#start-with-config-directory-conf",children:"configDir"})," and add the 'advanced' properties to that file."]}),"\n",(0,n.jsxs)(r.p,{children:["You can also override properties by setting them as environment variables. You can for example override property ",(0,n.jsx)(r.code,{children:"ebms.port"})," with value ",(0,n.jsx)(r.code,{children:"80"})," as follows"]}),"\n",(0,n.jsx)(r.pre,{children:(0,n.jsx)(r.code,{className:"language-sh",children:"export ebms_port=80\n"})}),"\n",(0,n.jsx)(r.p,{children:"This is especially useful when configuring containers"}),"\n",(0,n.jsx)(r.h2,{id:"basic-properties",children:"Basic Properties"}),"\n",(0,n.jsxs)(r.ul,{children:["\n",(0,n.jsx)(r.li,{children:(0,n.jsx)(r.a,{href:"#database",children:"Database"})}),"\n",(0,n.jsx)(r.li,{children:(0,n.jsx)(r.a,{href:"#ebms-server",children:"EbMS Server"})}),"\n",(0,n.jsx)(r.li,{children:(0,n.jsx)(r.a,{href:"#ssl",children:"SSL"})}),"\n",(0,n.jsx)(r.li,{children:(0,n.jsx)(r.a,{href:"#ssl-server-keystore",children:"SSL Server keystore"})}),"\n",(0,n.jsx)(r.li,{children:(0,n.jsx)(r.a,{href:"/ebms-admin/docs/ebms-core/properties#ssl-client-keystore",children:"SSL Client Keystore"})}),"\n",(0,n.jsx)(r.li,{children:(0,n.jsx)(r.a,{href:"/ebms-admin/docs/ebms-core/properties#truststore",children:"Truststore"})}),"\n",(0,n.jsx)(r.li,{children:(0,n.jsx)(r.a,{href:"/ebms-admin/docs/ebms-core/properties#forward-proxy",children:"Forward Proxy"})}),"\n",(0,n.jsx)(r.li,{children:(0,n.jsx)(r.a,{href:"/ebms-admin/docs/ebms-core/properties#overflow-attachments-to-disk",children:"Overflow attachments to disk"})}),"\n",(0,n.jsx)(r.li,{children:(0,n.jsx)(r.a,{href:"/ebms-admin/docs/ebms-core/properties#signature-keystore",children:"EbMS Signature keystore"})}),"\n",(0,n.jsx)(r.li,{children:(0,n.jsx)(r.a,{href:"/ebms-admin/docs/ebms-core/properties#encryption-keystore",children:"EbMS Encryption keystore"})}),"\n",(0,n.jsx)(r.li,{children:(0,n.jsx)(r.a,{href:"/ebms-admin/docs/ebms-core/properties#ebms-message-storage",children:"EbMS Message Storage"})}),"\n",(0,n.jsx)(r.li,{children:(0,n.jsx)(r.a,{href:"/ebms-admin/docs/ebms-core/properties#eventlistener",children:"EbMS EventListener"})}),"\n"]}),"\n",(0,n.jsx)(r.h2,{id:"default-properties",children:"Default Properties"}),"\n",(0,n.jsxs)(r.p,{children:["Below the contents of ebms-admin's ",(0,n.jsx)(r.a,{href:"https://github.com/eluinstrablob/ebms-admin-2.19.x/src/main/resources/nl/clockwork/ebms/admin/default.properties",children:"default.properties"})," file. These are the default settings for ebms-admin."]}),"\n",(0,n.jsx)(r.h3,{id:"database",children:"Database"}),"\n",(0,n.jsxs)(r.p,{children:["These properties override ",(0,n.jsx)(r.a,{href:"/ebms-admin/docs/ebms-core/properties#database",children:"these"})," default ebms-core properties."]}),"\n",(0,n.jsx)(r.pre,{children:(0,n.jsx)(r.code,{className:"language-properties",children:"ebms.jdbc.driverClassName=org.hsqldb.jdbcDriver\nebms.jdbc.url=jdbc:hsqldb:hsql://localhost:9001/ebms\nebms.jdbc.username=sa\nebms.jdbc.password=\n"})}),"\n",(0,n.jsx)(r.h3,{id:"ebms-server",children:"EbMS Server"}),"\n",(0,n.jsx)(r.p,{children:"Properties for the EbMS Server endpoint used to connect to another EbMS adapter."}),"\n",(0,n.jsx)(r.pre,{children:(0,n.jsx)(r.code,{className:"language-properties",children:"ebms.host=0.0.0.0\nebms.port=8888\nebms.path=/ebms\nebms.ssl=true\nebms.connectionLimit=\nebms.queriesPerSecond=\nebms.userQueriesPerSecond=\n"})}),"\n",(0,n.jsx)(r.h3,{id:"ssl-server-keystore",children:"SSL Server keystore"}),"\n",(0,n.jsxs)(r.p,{children:["Holds the SSL key (and related certificates) for the ",(0,n.jsx)(r.a,{href:"#ebms-server",children:"EbMS Server"})," endpoint."]}),"\n",(0,n.jsx)(r.pre,{children:(0,n.jsx)(r.code,{className:"language-properties",children:"# KeystoreType: JCEKS | JKS | DKS | PKCS11 | PKCS12\nkeystore.type=PKCS12\nkeystore.path=nl/clockwork/ebms/keystore.p12\nkeystore.password=password\nkeystore.defaultAlias=\n"})}),"\n",(0,n.jsx)(r.h3,{id:"ssl",children:"SSL"}),"\n",(0,n.jsxs)(r.p,{children:[(0,n.jsx)(r.code,{children:"https.protocols"})," and ",(0,n.jsx)(r.code,{children:"https.cipherSuites"})," override ",(0,n.jsx)(r.a,{href:"/ebms-admin/docs/ebms-core/properties#ssl",children:"these"})," default ebms-core properties. If ",(0,n.jsx)(r.code,{children:"https.requireClientAuthentication=true"})," then the ",(0,n.jsx)(r.a,{href:"#ebms-server",children:"EbMS Server"})," endpoint requires SSL client authentication."]}),"\n",(0,n.jsxs)(r.p,{children:["When SSL offloading is used and the EbMS adapter does not handle incoming SSL itself (see ",(0,n.jsx)(r.a,{href:"deployment#behind-a-reverse-proxy",children:"reverse proxy example"}),") and the EbMS adapter is using ",(0,n.jsx)(r.a,{href:"/ebms-admin/docs/ebms-core/properties#ssl",children:"SSL client certificate validation"}),", then the SSL client certificate can be forwarded as a Base64 DER-encoded HTTP header to the EbMS adapter. The header name can be set in ",(0,n.jsx)(r.code,{children:"https.clientCertificateHeader"})," (since ",(0,n.jsx)(r.a,{href:"/ebms-admin/docs/ebms-core/release#ebms-core-2167jar",children:"v2.16.7"}),")."]}),"\n",(0,n.jsx)(r.pre,{children:(0,n.jsx)(r.code,{className:"language-properties",children:"https.protocols=TLSv1.2\nhttps.cipherSuites=TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384,TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384\nhttps.requireClientAuthentication=false\nhttps.clientCertificateHeader=\n"})}),"\n",(0,n.jsx)(r.h3,{id:"user-interface",children:"User Interface"}),"\n",(0,n.jsx)(r.pre,{children:(0,n.jsx)(r.code,{className:"language-properties",children:"maxItemsPerPage=20\n"})})]})}function h(e={}){const{wrapper:r}={...(0,t.R)(),...e.components};return r?(0,n.jsx)(r,{...e,children:(0,n.jsx)(l,{...e})}):l(e)}},8453:(e,r,s)=>{s.d(r,{R:()=>o,x:()=>d});var i=s(6540);const n={},t=i.createContext(n);function o(e){const r=i.useContext(t);return i.useMemo((function(){return"function"==typeof e?e(r):{...r,...e}}),[r,e])}function d(e){let r;return r=e.disableParentContext?"function"==typeof e.components?e.components(n):e.components||n:o(e.components),i.createElement(t.Provider,{value:r},e.children)}}}]);