"use strict";(self.webpackChunkdocumentation=self.webpackChunkdocumentation||[]).push([[620],{8993:(e,n,i)=>{i.r(n),i.d(n,{assets:()=>d,contentTitle:()=>o,default:()=>h,frontMatter:()=>t,metadata:()=>r,toc:()=>l});const r=JSON.parse('{"id":"ebms-admin/installation","title":"Installation and Configuration","description":"Prerequisites","source":"@site/versioned_docs/version-2.19.x/ebms-admin/installation.md","sourceDirName":"ebms-admin","slug":"/ebms-admin/installation","permalink":"/ebms-admin/docs/ebms-admin/installation","draft":false,"unlisted":false,"editUrl":"https://github.com/eluinstra/ebms-admin/tree/documentation/versioned_docs/version-2.19.x/ebms-admin/installation.md","tags":[],"version":"2.19.x","sidebarPosition":3,"frontMatter":{"sidebar_position":3},"sidebar":"tutorialSidebar","previous":{"title":"Deployment Scenarios","permalink":"/ebms-admin/docs/ebms-admin/deployment"},"next":{"title":"Command Line Options","permalink":"/ebms-admin/docs/ebms-admin/command"}}');var s=i(4848),a=i(8453);const t={sidebar_position:3},o="Installation and Configuration",d={},l=[{value:"Prerequisites",id:"prerequisites",level:2},{value:"Installation",id:"installation",level:2},{value:"Configuration",id:"configuration",level:2},{value:"Start",id:"start",level:2}];function c(e){const n={a:"a",code:"code",em:"em",h1:"h1",h2:"h2",header:"header",li:"li",p:"p",pre:"pre",ul:"ul",...(0,a.R)(),...e.components};return(0,s.jsxs)(s.Fragment,{children:[(0,s.jsx)(n.header,{children:(0,s.jsx)(n.h1,{id:"installation-and-configuration",children:"Installation and Configuration"})}),"\n",(0,s.jsx)(n.h2,{id:"prerequisites",children:"Prerequisites"}),"\n",(0,s.jsxs)(n.ul,{children:["\n",(0,s.jsx)(n.li,{children:"download and install Java 11 (or later)"}),"\n",(0,s.jsxs)(n.li,{children:["provide a ",(0,s.jsx)(n.a,{href:"/ebms-admin/docs/ebms-core/database",children:"supported database"})," and download the appropriate JDBC driver"]}),"\n",(0,s.jsxs)(n.li,{children:["download ",(0,s.jsx)(n.a,{href:"https://github.com/eluinstrareleases/download/ebms-admin-2.19.3/ebms-admin-2.19.3.jar",children:"ebms-admin-2.19.3.jar"})]}),"\n"]}),"\n",(0,s.jsx)(n.h2,{id:"installation",children:"Installation"}),"\n",(0,s.jsxs)(n.ul,{children:["\n",(0,s.jsxs)(n.li,{children:["create directory ",(0,s.jsx)(n.code,{children:"ebms-admin"})]}),"\n",(0,s.jsxs)(n.li,{children:["copy ebms-admin-2.19.3.jar to ",(0,s.jsx)(n.code,{children:"ebms-admin"})]}),"\n"]}),"\n",(0,s.jsx)(n.h2,{id:"configuration",children:"Configuration"}),"\n",(0,s.jsxs)(n.ul,{children:["\n",(0,s.jsxs)(n.li,{children:["Create the file ",(0,s.jsx)(n.code,{children:"ebms-admin.embedded.properties"})," in ",(0,s.jsx)(n.code,{children:"ebms-admin"})," and ",(0,s.jsx)(n.a,{href:"properties",children:"configure the properties"})]}),"\n",(0,s.jsxs)(n.li,{children:[(0,s.jsx)(n.a,{href:"database#initialize-flyway",children:"Configure Flyway"})," to load the database scripts automatically or load the ",(0,s.jsx)(n.a,{href:"/ebms-admin/docs/ebms-core/database#database-scripts",children:"database scripts"})," manually"]}),"\n"]}),"\n",(0,s.jsxs)(n.p,{children:["The EbMS interface is configured through ",(0,s.jsx)(n.a,{href:"properties#ebms-server",children:"properties"}),". The Web and SOAP interfaces are configured through ",(0,s.jsx)(n.a,{href:"command#start-on-port-8000",children:"command line options"}),"."]}),"\n",(0,s.jsxs)(n.p,{children:["See ",(0,s.jsx)(n.a,{href:"/ebms-admin/docs/ebms-core/overview",children:"here"})," for a functional overview of the EbMS adapter."]}),"\n",(0,s.jsx)(n.h2,{id:"start",children:"Start"}),"\n",(0,s.jsxs)(n.ul,{children:["\n",(0,s.jsxs)(n.li,{children:["start ebms-admin on default port ",(0,s.jsx)(n.code,{children:"8080"})," with the SOAP interface enabled and using JDBC driver ",(0,s.jsx)(n.code,{children:"<jdbc-driver>.jar"})]}),"\n"]}),"\n",(0,s.jsx)(n.pre,{children:(0,s.jsx)(n.code,{className:"language-sh",children:"java -cp <jdbc-driver>.jar:ebms-admin-2.19.3.jar nl.clockwork.ebms.admin.StartEmbedded -soap\n"})}),"\n",(0,s.jsx)(n.p,{children:"When you start ebms-admin you can see the following output in your console"}),"\n",(0,s.jsx)(n.pre,{children:(0,s.jsx)(n.code,{className:"language-sh",children:"Using config directory: \nWeb Server configured on http://localhost:8080/\nSOAP Service configured on http://localhost:8080/service\nEbMS Service configured on https://localhost:8888/ebms\nStarting Server...\nServer started.\n"})}),"\n",(0,s.jsxs)(n.p,{children:["It shows how the Web interface, SOAP interface en EbMS Server endpoints are configured. You can find the different ",(0,s.jsx)(n.a,{href:"soap",children:"SOAP interface"})," endpoints when you open your browser at ",(0,s.jsx)(n.a,{href:"http://localhost:8080/service",children:"http://localhost:8080/service"}),"."]}),"\n",(0,s.jsxs)(n.p,{children:["See ",(0,s.jsx)(n.a,{href:"command",children:"here"})," for all command line options. See ",(0,s.jsx)(n.a,{href:"examples",children:"here"})," for more examples."]}),"\n",(0,s.jsxs)(n.p,{children:["You can install ebms-admin as a Java service on Windows or Linux using a ",(0,s.jsx)(n.em,{children:"Java Service Wrapper"}),"."]})]})}function h(e={}){const{wrapper:n}={...(0,a.R)(),...e.components};return n?(0,s.jsx)(n,{...e,children:(0,s.jsx)(c,{...e})}):c(e)}},8453:(e,n,i)=>{i.d(n,{R:()=>t,x:()=>o});var r=i(6540);const s={},a=r.createContext(s);function t(e){const n=r.useContext(a);return r.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function o(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(s):e.components||s:t(e.components),r.createElement(a.Provider,{value:n},e.children)}}}]);