"use strict";(self.webpackChunkdocumentation=self.webpackChunkdocumentation||[]).push([[822],{5402:(e,a,s)=>{s.r(a),s.d(a,{assets:()=>o,contentTitle:()=>d,default:()=>m,frontMatter:()=>i,metadata:()=>n,toc:()=>l});const n=JSON.parse('{"id":"ebms-admin/database","title":"Database support","description":"If you want to use a database other then H2 or HSQLDB, you have to provide the JDBC driver yourself.","source":"@site/docs/ebms-admin/database.md","sourceDirName":"ebms-admin","slug":"/ebms-admin/database","permalink":"/ebms-admin/docs/ebms-admin/database","draft":false,"unlisted":false,"editUrl":"https://github.com/eluinstra/ebms-admin/tree/documentation/docs/ebms-admin/database.md","tags":[],"version":"current","sidebarPosition":8,"frontMatter":{"sidebar_position":8},"sidebar":"tutorialSidebar","previous":{"title":"SSL Configuration","permalink":"/ebms-admin/docs/ebms-admin/ssl"},"next":{"title":"Examples","permalink":"/ebms-admin/docs/ebms-admin/examples"}}');var r=s(4848),t=s(8453);const i={sidebar_position:8},d="Database support",o={},l=[{value:"Initialize Flyway",id:"initialize-flyway",level:2},{value:"DBMigrate",id:"dbmigrate",level:2},{value:"Examples",id:"examples",level:3},{value:"Migrate PostgreSQL database",id:"migrate-postgresql-database",level:4},{value:"Initialize existing PostgreSQL database",id:"initialize-existing-postgresql-database",level:4},{value:"DBClean",id:"dbclean",level:2},{value:"Examples",id:"examples-1",level:3},{value:"Cleanup messages from PostgreSQL database",id:"cleanup-messages-from-postgresql-database",level:4},{value:"Cleanup CPA from PostgreSQL database",id:"cleanup-cpa-from-postgresql-database",level:4}];function c(e){const a={a:"a",admonition:"admonition",code:"code",h1:"h1",h2:"h2",h3:"h3",h4:"h4",header:"header",p:"p",pre:"pre",strong:"strong",...(0,t.R)(),...e.components};return(0,r.jsxs)(r.Fragment,{children:[(0,r.jsx)(a.header,{children:(0,r.jsx)(a.h1,{id:"database-support",children:"Database support"})}),"\n",(0,r.jsx)(a.p,{children:"If you want to use a database other then H2 or HSQLDB, you have to provide the JDBC driver yourself."}),"\n",(0,r.jsxs)(a.p,{children:["For more on database support see ",(0,r.jsx)(a.a,{href:"/ebms-admin/docs/ebms-core/database",children:"here"}),"."]}),"\n",(0,r.jsx)(a.admonition,{type:"danger",children:(0,r.jsxs)(a.p,{children:["We strongly advise to ",(0,r.jsx)(a.strong,{children:"not"})," use a MSSQL Database with the EbMS Adapter if you expect a moderate to high message load, because MSSQL cannot handle that because of Page Locking."]})}),"\n",(0,r.jsx)(a.h2,{id:"initialize-flyway",children:"Initialize Flyway"}),"\n",(0,r.jsxs)(a.p,{children:["You can use Flyway to migrate your database. To initialize Flyway for the first time on an existing database run ",(0,r.jsx)(a.a,{href:"#dbmigrate",children:"DBMigrate"}),"."]}),"\n",(0,r.jsx)(a.h2,{id:"dbmigrate",children:"DBMigrate"}),"\n",(0,r.jsxs)(a.p,{children:["With DBMigrate you can migrate your database using Flyway (since ",(0,r.jsx)(a.a,{href:"/ebms-admin/docs/ebms-core/release#ebms-core-2170jar",children:"v2.17.0"}),"). ebms-core also supports ",(0,r.jsx)(a.a,{href:"/ebms-admin/docs/ebms-core/database#flyway",children:"automatic update through Flyway"}),"."]}),"\n",(0,r.jsx)(a.pre,{children:(0,r.jsx)(a.code,{className:"language-sh",children:"java -cp ebms-admin-2.19.3.jar nl.clockwork.ebms.admin.DBMigrate -h\nusage: DBMigrate [-ebmsVersion <arg>] [-h] [-jdbcUrl <arg>] [-password <arg>] [-strict] [-username <arg>]\n -ebmsVersion <arg>   set current ebmsVersion (default: none)\n -h                   print this message\n -jdbcUrl <arg>       set jdbcUrl\n -password <arg>      set password\n -strict              use strict db scripts (default: false)\n -username <arg>      set username\n\nValid ebmsVersions:\n2.10\n2.11\n2.12\n2.13\n2.14\n2.15\n2.16\n2.17\n"})}),"\n",(0,r.jsxs)(a.p,{children:["See ",(0,r.jsx)(a.a,{href:"/ebms-admin/docs/ebms-core/database",children:"here"})," for database settings."]}),"\n",(0,r.jsx)(a.admonition,{type:"caution",children:(0,r.jsxs)(a.p,{children:["If you run Flyway for the first time on an existing database, then you have to use the argument ",(0,r.jsx)(a.code,{children:"-ebmsVersion"})," with your current EbMS database version to ",(0,r.jsx)(a.a,{href:"#initialize",children:"initialize the database"}),"."]})}),"\n",(0,r.jsx)(a.h3,{id:"examples",children:"Examples"}),"\n",(0,r.jsx)(a.h4,{id:"migrate-postgresql-database",children:"Migrate PostgreSQL database"}),"\n",(0,r.jsxs)(a.p,{children:["Migrate a PostgreSQL database ",(0,r.jsx)(a.code,{children:"ebms"})," on ",(0,r.jsx)(a.code,{children:"localhost:5432"})," with user credentials ",(0,r.jsx)(a.code,{children:"username"}),"/",(0,r.jsx)(a.code,{children:"password"})]}),"\n",(0,r.jsx)(a.pre,{children:(0,r.jsx)(a.code,{className:"language-sh",children:"java -cp postgresql-42.2.16.jar:ebms-admin-2.19.3.jar nl.clockwork.ebms.admin.DBMigrate \\\n-jdbcUrl jdbc:postgresql://localhost:5432/ebms \\\n-username=username -password=password\n"})}),"\n",(0,r.jsx)(a.h4,{id:"initialize-existing-postgresql-database",children:"Initialize existing PostgreSQL database"}),"\n",(0,r.jsxs)(a.p,{children:["Initialize and migrate an existing PostgreSQL database ",(0,r.jsx)(a.code,{children:"ebms"})," on ",(0,r.jsx)(a.code,{children:"localhost:5432"})," that is on EbMS database v2.15.x"]}),"\n",(0,r.jsx)(a.pre,{children:(0,r.jsx)(a.code,{className:"language-sh",children:"java -cp postgresql-42.2.16.jar:ebms-admin-2.19.3.jar nl.clockwork.ebms.admin.DBMigrate \\\n-ebmsVersion 2.15 \\\n-jdbcUrl jdbc:postgresql://localhost:5432/ebms \\\n-username username -password password\n"})}),"\n",(0,r.jsx)(a.h2,{id:"dbclean",children:"DBClean"}),"\n",(0,r.jsxs)(a.p,{children:["With DBClean you can cleanup your database (since ",(0,r.jsx)(a.a,{href:"/ebms-admin/docs/ebms-core/release#ebms-core-2170jar",children:"v2.17.0"}),"). You have to configure the ",(0,r.jsx)(a.a,{href:"properties#database",children:"database properties"})," before using DBClean. You can find message storage properties ",(0,r.jsx)(a.a,{href:"/ebms-admin/docs/ebms-core/properties#ebms-message-storage",children:"here"}),"."]}),"\n",(0,r.jsx)(a.pre,{children:(0,r.jsx)(a.code,{className:"language-sh",children:"java -cp ebms-admin-2.19.3.jar nl.clockwork.ebms.admin.DBClean -h\nusage: DBClean [-cmd <arg>] [-configDir <arg>] [-cpaId <arg>] [-dateFrom <arg>] [-h]\n -cmd <arg>         objects to clean [vales: cpa|messages]\n -configDir <arg>   set config directory (default=current dir)\n -cpaId <arg>       the cpaId of the CPA to delete\n -dateFrom <arg>    the date from which objects will be deleted [format:\n                    YYYYMMDD][default: now - 30 days]\n -h                 print this message\n"})}),"\n",(0,r.jsx)(a.h3,{id:"examples-1",children:"Examples"}),"\n",(0,r.jsx)(a.h4,{id:"cleanup-messages-from-postgresql-database",children:"Cleanup messages from PostgreSQL database"}),"\n",(0,r.jsx)(a.p,{children:"Cleanup all EbMS messages (and related objects) with persistence date before 30 days ago"}),"\n",(0,r.jsx)(a.pre,{children:(0,r.jsx)(a.code,{className:"language-sh",children:"java -cp postgresql-42.2.16.jar:ebms-admin-2.19.3.jar nl.clockwork.ebms.admin.DBClean -cmd messages\n"})}),"\n",(0,r.jsx)(a.h4,{id:"cleanup-cpa-from-postgresql-database",children:"Cleanup CPA from PostgreSQL database"}),"\n",(0,r.jsxs)(a.p,{children:["Cleanup CPA with cpaId ",(0,r.jsx)(a.code,{children:"1"})," (and all related EbMS messages and other objects)"]}),"\n",(0,r.jsx)(a.pre,{children:(0,r.jsx)(a.code,{className:"language-sh",children:"java -cp postgresql-42.2.16.jar:ebms-admin-2.19.3.jar nl.clockwork.ebms.admin.DBClean -cmd cpa -cpaId=1\n"})})]})}function m(e={}){const{wrapper:a}={...(0,t.R)(),...e.components};return a?(0,r.jsx)(a,{...e,children:(0,r.jsx)(c,{...e})}):c(e)}},8453:(e,a,s)=>{s.d(a,{R:()=>i,x:()=>d});var n=s(6540);const r={},t=n.createContext(r);function i(e){const a=n.useContext(t);return n.useMemo((function(){return"function"==typeof e?e(a):{...a,...e}}),[a,e])}function d(e){let a;return a=e.disableParentContext?"function"==typeof e.components?e.components(r):e.components||r:i(e.components),n.createElement(t.Provider,{value:a},e.children)}}}]);