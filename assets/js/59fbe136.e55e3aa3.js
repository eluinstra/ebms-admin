"use strict";(self.webpackChunkdocumentation=self.webpackChunkdocumentation||[]).push([[3548],{5095:(e,r,s)=>{s.r(r),s.d(r,{assets:()=>c,contentTitle:()=>l,default:()=>h,frontMatter:()=>d,metadata:()=>n,toc:()=>t});const n=JSON.parse('{"id":"ebms-core/database","title":"Database support","description":"The EbMS Adapter supports the following databases","source":"@site/versioned_docs/version-2.19.x/ebms-core/database.md","sourceDirName":"ebms-core","slug":"/ebms-core/database","permalink":"/ebms-admin/docs/2.19.x/ebms-core/database","draft":false,"unlisted":false,"editUrl":"https://github.com/eluinstra/ebms-admin/tree/documentation/versioned_docs/version-2.19.x/ebms-core/database.md","tags":[],"version":"2.19.x","sidebarPosition":5,"frontMatter":{"sidebar_position":5},"sidebar":"tutorialSidebar","previous":{"title":"Default Properties","permalink":"/ebms-admin/docs/2.19.x/ebms-core/properties"},"next":{"title":"EbMS API","permalink":"/ebms-admin/docs/2.19.x/ebms-core/api"}}');var a=s(4848),i=s(8453);const d={sidebar_position:5},l="Database support",c={},t=[{value:"Database Scripts",id:"database-scripts",level:2},{value:"Database Configuration",id:"database-configuration",level:2},{value:"Common Properties",id:"common-properties",level:3},{value:"DB2",id:"db2",level:3},{value:"H2",id:"h2",level:3},{value:"HSQLDB",id:"hsqldb",level:3},{value:"MariaDB",id:"mariadb",level:3},{value:"MS SQL Server",id:"ms-sql-server",level:3},{value:"XA Driver",id:"xa-driver",level:4},{value:"Quartz",id:"quartz",level:4},{value:"MySQL",id:"mysql",level:3},{value:"XA Driver",id:"xa-driver-1",level:4},{value:"Oracle",id:"oracle",level:3},{value:"XA Driver",id:"xa-driver-2",level:4},{value:"PostgreSQL",id:"postgresql",level:3},{value:"XA Driver",id:"xa-driver-3",level:4},{value:"Flyway",id:"flyway",level:2}];function o(e){const r={a:"a",admonition:"admonition",br:"br",code:"code",h1:"h1",h2:"h2",h3:"h3",h4:"h4",header:"header",li:"li",p:"p",pre:"pre",strong:"strong",ul:"ul",...(0,i.R)(),...e.components};return(0,a.jsxs)(a.Fragment,{children:[(0,a.jsx)(r.header,{children:(0,a.jsx)(r.h1,{id:"database-support",children:"Database support"})}),"\n",(0,a.jsx)(r.p,{children:"The EbMS Adapter supports the following databases"}),"\n",(0,a.jsxs)(r.ul,{children:["\n",(0,a.jsxs)(r.li,{children:[(0,a.jsx)(r.a,{href:"#database-support",children:"Database support"}),"\n",(0,a.jsxs)(r.ul,{children:["\n",(0,a.jsx)(r.li,{children:(0,a.jsx)(r.a,{href:"#database-scripts",children:"Database Scripts"})}),"\n",(0,a.jsxs)(r.li,{children:[(0,a.jsx)(r.a,{href:"#database-configuration",children:"Database Configuration"}),"\n",(0,a.jsxs)(r.ul,{children:["\n",(0,a.jsx)(r.li,{children:(0,a.jsx)(r.a,{href:"#common-properties",children:"Common Properties"})}),"\n",(0,a.jsx)(r.li,{children:(0,a.jsx)(r.a,{href:"#db2",children:"DB2"})}),"\n",(0,a.jsx)(r.li,{children:(0,a.jsx)(r.a,{href:"#h2",children:"H2"})}),"\n",(0,a.jsx)(r.li,{children:(0,a.jsx)(r.a,{href:"#hsqldb",children:"HSQLDB"})}),"\n",(0,a.jsx)(r.li,{children:(0,a.jsx)(r.a,{href:"#mariadb",children:"MariaDB"})}),"\n",(0,a.jsxs)(r.li,{children:[(0,a.jsx)(r.a,{href:"#ms-sql-server",children:"MS SQL Server"}),"\n",(0,a.jsxs)(r.ul,{children:["\n",(0,a.jsx)(r.li,{children:(0,a.jsx)(r.a,{href:"#xa-driver",children:"XA Driver"})}),"\n",(0,a.jsx)(r.li,{children:(0,a.jsx)(r.a,{href:"#quartz",children:"Quartz"})}),"\n"]}),"\n"]}),"\n",(0,a.jsxs)(r.li,{children:[(0,a.jsx)(r.a,{href:"#mysql",children:"MySQL"}),"\n",(0,a.jsxs)(r.ul,{children:["\n",(0,a.jsx)(r.li,{children:(0,a.jsx)(r.a,{href:"#xa-driver-1",children:"XA Driver"})}),"\n"]}),"\n"]}),"\n",(0,a.jsxs)(r.li,{children:[(0,a.jsx)(r.a,{href:"#oracle",children:"Oracle"}),"\n",(0,a.jsxs)(r.ul,{children:["\n",(0,a.jsx)(r.li,{children:(0,a.jsx)(r.a,{href:"#xa-driver-2",children:"XA Driver"})}),"\n"]}),"\n"]}),"\n",(0,a.jsxs)(r.li,{children:[(0,a.jsx)(r.a,{href:"#postgresql",children:"PostgreSQL"}),"\n",(0,a.jsxs)(r.ul,{children:["\n",(0,a.jsx)(r.li,{children:(0,a.jsx)(r.a,{href:"#xa-driver-3",children:"XA Driver"})}),"\n"]}),"\n"]}),"\n"]}),"\n"]}),"\n",(0,a.jsx)(r.li,{children:(0,a.jsx)(r.a,{href:"#flyway",children:"Flyway"})}),"\n"]}),"\n"]}),"\n"]}),"\n",(0,a.jsx)(r.h2,{id:"database-scripts",children:"Database Scripts"}),"\n",(0,a.jsxs)(r.p,{children:["The database master scripts can be found ",(0,a.jsx)(r.a,{href:"https://github.com/eluinstra/ebms-core/tree/ebms-core-2.19.x/resources/scripts/database/master/",children:"here"}),(0,a.jsx)(r.br,{}),"\n","The database update scripts can be found ",(0,a.jsx)(r.a,{href:"https://github.com/eluinstra/ebms-core/tree/ebms-core-2.19.x/src/main/resources/nl/clockwork/ebms/db/migration",children:"here"}),(0,a.jsx)(r.br,{}),"\n","ebms-core also supports automatic database migration through ",(0,a.jsx)(r.a,{href:"#flyway",children:"Flyway"})]}),"\n",(0,a.jsx)(r.h2,{id:"database-configuration",children:"Database Configuration"}),"\n",(0,a.jsx)(r.p,{children:"You can find the JDBC settings for the supported databases as well as links to the JDBC drivers below."}),"\n",(0,a.jsx)(r.h3,{id:"common-properties",children:"Common Properties"}),"\n",(0,a.jsx)(r.pre,{children:(0,a.jsx)(r.code,{className:"language-properties",children:"ebms.jdbc.username=<username>\nebms.jdbc.password=<password>\n"})}),"\n",(0,a.jsx)(r.h3,{id:"db2",children:"DB2"}),"\n",(0,a.jsx)(r.p,{children:"since v2.14.0"}),"\n",(0,a.jsx)(r.pre,{children:(0,a.jsx)(r.code,{className:"language-properties",children:"# JDBC driver\nebms.jdbc.driverClassName=com.ibm.db2.jcc.DB2Driver\n# or XA driver\nebms.jdbc.driverClassName=com.ibm.db2.jcc.DB2XADataSource\nebms.jdbc.url=jdbc:db2://<host>:<port>/<dbname>\n"})}),"\n",(0,a.jsx)(r.p,{children:"Tested with DB2 11.5.4.0"}),"\n",(0,a.jsxs)(r.p,{children:["Download drivers ",(0,a.jsx)(r.a,{href:"https://www.ibm.com/support/pages/db2-jdbc-driver-versions-and-downloads",children:"here"})]}),"\n",(0,a.jsx)(r.h3,{id:"h2",children:"H2"}),"\n",(0,a.jsxs)(r.p,{children:["since ",(0,a.jsx)(r.a,{href:"release#ebms-core-2172jar",children:"v2.17.2"})]}),"\n",(0,a.jsx)(r.pre,{children:(0,a.jsx)(r.code,{className:"language-properties",children:"# JDBC and XA driver\nebms.jdbc.driverClassName=org.h2.Driver\n# or XA driver\nebms.jdbc.driverClassName=org.h2.jdbcx.JdbcDataSource\n# In memory\nebms.jdbc.url=jdbc:h2:mem:<dbname>\n# or file\nebms.jdbc.url=jdbc:h2:<path>\n# or server\nebms.jdbc.url=jdbc:h2:tcp://<host>:<port>/<path>\n"})}),"\n",(0,a.jsx)(r.p,{children:"Tested with H2 1.4.200"}),"\n",(0,a.jsx)(r.h3,{id:"hsqldb",children:"HSQLDB"}),"\n",(0,a.jsx)(r.pre,{children:(0,a.jsx)(r.code,{className:"language-properties",children:"# JDBC driver\nebms.jdbc.driverClassName=org.hsqldb.jdbcDriver\n# or XA driver\nebms.jdbc.driverClassName=org.hsqldb.jdbc.pool.JDBCXADataSource\n# In memory\nebms.jdbc.url=jdbc:hsqldb:mem:<dbname>\n# or file\nebms.jdbc.url=jdbc:hsqldb:file:<path>\n# or server\nebms.jdbc.url=jdbc:hsqldb:hsql://<host>:<port>/<dbname>\n"})}),"\n",(0,a.jsx)(r.p,{children:"Tested with HSQLDB 2.5.1"}),"\n",(0,a.jsx)(r.h3,{id:"mariadb",children:"MariaDB"}),"\n",(0,a.jsx)(r.pre,{children:(0,a.jsx)(r.code,{className:"language-properties",children:"# JDBC driver\nebms.jdbc.driverClassName=org.mariadb.jdbc.Driver\n# or XA driver\nebms.jdbc.driverClassName=org.mariadb.jdbc.MySQLDataSource\nebms.jdbc.url=jdbc:mariadb://<host>:<port>/<dbname>\n"})}),"\n",(0,a.jsx)(r.p,{children:"Tested with MariaDB 10.3.22"}),"\n",(0,a.jsxs)(r.p,{children:["Download drivers ",(0,a.jsx)(r.a,{href:"https://downloads.mariadb.org/connector-java/",children:"here"})]}),"\n",(0,a.jsxs)(r.p,{children:["Download the right flyway-mysql driver ",(0,a.jsx)(r.a,{href:"https://mvnrepository.com/artifact/org.flywaydb/flyway-mysql",children:"here"})," and add it to the classpath next to the database driver\nCheck the pom.xml of ebms-admin for the right version of the flyway-mysql library"]}),"\n",(0,a.jsx)(r.h3,{id:"ms-sql-server",children:"MS SQL Server"}),"\n",(0,a.jsx)(r.admonition,{type:"danger",children:(0,a.jsxs)(r.p,{children:["We strongly advise to ",(0,a.jsx)(r.strong,{children:"not"})," use a MSSQL Database with the EbMS Adapter if you expect a moderate to high message load, because MSSQL cannot handle that because of Page Locking."]})}),"\n",(0,a.jsx)(r.pre,{children:(0,a.jsx)(r.code,{className:"language-properties",children:"# JDBC driver\nebms.jdbc.driverClassName=com.microsoft.sqlserver.jdbc.SQLServerDriver\n# or XA driver\nebms.jdbc.driverClassName=com.microsoft.sqlserver.jdbc.SQLServerXADataSource\nebms.jdbc.url=jdbc:sqlserver://<host>:<port>;[instanceName=<instanceName>;]databaseName=<dbname>;\n"})}),"\n",(0,a.jsx)(r.p,{children:"Tested with MS SQL Server 2019"}),"\n",(0,a.jsxs)(r.p,{children:["Download drivers ",(0,a.jsx)(r.a,{href:"https://docs.microsoft.com/en-us/sql/connect/jdbc/download-microsoft-jdbc-driver-for-sql-server",children:"here"})]}),"\n",(0,a.jsxs)(r.p,{children:["Download the right flyway-sqlserver driver ",(0,a.jsx)(r.a,{href:"https://mvnrepository.com/artifact/org.flywaydb/flyway-sqlserver",children:"here"})," and add it to the classpath next to the database driver\nCheck the pom.xml of ebms-admin for the right version of the flyway-sqlserver library"]}),"\n",(0,a.jsx)(r.h4,{id:"xa-driver",children:"XA Driver"}),"\n",(0,a.jsx)(r.p,{children:"When using the XA driver execute the following script"}),"\n",(0,a.jsx)(r.pre,{children:(0,a.jsx)(r.code,{className:"language-sql",children:"EXEC sp_sqljdbc_xa_install\nEXEC sp_addrolemember [SqlJDBCXAUser], '<username>'\n"})}),"\n",(0,a.jsx)(r.h4,{id:"quartz",children:"Quartz"}),"\n",(0,a.jsxs)(r.p,{children:["When ",(0,a.jsx)(r.a,{href:"properties#deliverytaskhandler",children:(0,a.jsx)(r.code,{children:"deliveryTaskHandler.type"})})," is set to ",(0,a.jsx)(r.code,{children:"QUARTZ"}),"or ",(0,a.jsx)(r.code,{children:"QUARTZ_JMS"})," then set"]}),"\n",(0,a.jsx)(r.pre,{children:(0,a.jsx)(r.code,{className:"language-properties",children:"deliveryTaskHandler.quartz.jdbc.selectWithLockSQL=SELECT * FROM {0}LOCKS UPDLOCK WHERE LOCK_NAME = ?\n"})}),"\n",(0,a.jsx)(r.h3,{id:"mysql",children:"MySQL"}),"\n",(0,a.jsx)(r.admonition,{type:"danger",children:(0,a.jsx)(r.p,{children:"MySQL support is removed in version 2.19.0"})}),"\n",(0,a.jsx)(r.pre,{children:(0,a.jsx)(r.code,{className:"language-properties",children:"# JDBC driver\nebms.jdbc.driverClassName=com.mysql.cj.jdbc.Driver\n# or XA driver\nebms.jdbc.driverClassName=com.mysql.cj.jdbc.MysqlXADataSource\nebms.jdbc.url=jdbc:mysql://<host>:<port>/<dbname>\n"})}),"\n",(0,a.jsx)(r.p,{children:"Tested with MySQL 8.0.21"}),"\n",(0,a.jsxs)(r.p,{children:["Download drivers ",(0,a.jsx)(r.a,{href:"https://dev.mysql.com/downloads/connector/j/",children:"here"})]}),"\n",(0,a.jsx)(r.h4,{id:"xa-driver-1",children:"XA Driver"}),"\n",(0,a.jsxs)(r.p,{children:["When using the XA driver add line the following line to ",(0,a.jsx)(r.code,{children:"my.ini"})," or ",(0,a.jsx)(r.code,{children:"my.cnf"})]}),"\n",(0,a.jsx)(r.pre,{children:(0,a.jsx)(r.code,{className:"language-properties",children:"default-time-zone='+02:00'\n"})}),"\n",(0,a.jsx)(r.h3,{id:"oracle",children:"Oracle"}),"\n",(0,a.jsx)(r.pre,{children:(0,a.jsx)(r.code,{className:"language-properties",children:"# JDBC driver\nebms.jdbc.driverClassName=oracle.jdbc.OracleDriver\n# or XA driver\nebms.jdbc.driverClassName=oracle.jdbc.xa.client.OracleXADataSource\nebms.jdbc.url=jdbc:oracle:thin:@<host>:<port>:<dbname>\n"})}),"\n",(0,a.jsx)(r.p,{children:"Tested with Oracle XE 18c"}),"\n",(0,a.jsxs)(r.p,{children:["Download drivers ",(0,a.jsx)(r.a,{href:"https://www.oracle.com/database/technologies/appdev/jdbc-downloads.html",children:"here"})]}),"\n",(0,a.jsx)(r.h4,{id:"xa-driver-2",children:"XA Driver"}),"\n",(0,a.jsx)(r.p,{children:"When using the XA driver execute the following script"}),"\n",(0,a.jsx)(r.pre,{children:(0,a.jsx)(r.code,{className:"language-sql",children:"grant select on sys.dba_pending_transactions to <username>;\ngrant select on sys.pending_trans$ to <username>;\ngrant select on sys.dba_2pc_pending to <username>;\n"})}),"\n",(0,a.jsx)(r.h3,{id:"postgresql",children:"PostgreSQL"}),"\n",(0,a.jsx)(r.pre,{children:(0,a.jsx)(r.code,{className:"language-properties",children:"# JDBC driver\nebms.jdbc.driverClassName=org.postgresql.Driver\n# or XA driver\nebms.jdbc.driverClassName=org.postgresql.xa.PGXADataSource\nebms.jdbc.url=jdbc:postgresql://<host>:<port>/<dbname>\n"})}),"\n",(0,a.jsx)(r.p,{children:"Tested with PostgreSQL 12.4"}),"\n",(0,a.jsxs)(r.p,{children:["Download drivers ",(0,a.jsx)(r.a,{href:"https://jdbc.postgresql.org/download.html",children:"here"})]}),"\n",(0,a.jsx)(r.h4,{id:"xa-driver-3",children:"XA Driver"}),"\n",(0,a.jsx)(r.p,{children:"If you get the following error when using the XA driver"}),"\n",(0,a.jsx)(r.pre,{children:(0,a.jsx)(r.code,{className:"language-properties",children:"org.postgresql.util.PSQLException: ERROR: prepared transactions are disabled Hint: Set max_prepared_transactions to a nonzero value.\n"})}),"\n",(0,a.jsxs)(r.p,{children:["then enable the ",(0,a.jsx)(r.code,{children:"max_prepared_transactions"})," attribute in ",(0,a.jsx)(r.code,{children:"postgresql.conf"})]}),"\n",(0,a.jsx)(r.h2,{id:"flyway",children:"Flyway"}),"\n",(0,a.jsxs)(r.p,{children:["Database migration through Flyway is enabled through the following ",(0,a.jsx)(r.a,{href:"properties#database",children:"EbMS property"})," (since ",(0,a.jsx)(r.a,{href:"release#ebms-core-2172jar",children:"v2.17.2"}),")"]}),"\n",(0,a.jsx)(r.pre,{children:(0,a.jsx)(r.code,{className:"language-properties",children:"ebms.jdbc.update=true\n"})}),"\n",(0,a.jsxs)(r.p,{children:["If you already have an existing database and want to use Flyway, then you first have to ",(0,a.jsx)(r.a,{href:"/ebms-admin/docs/2.19.x/ebms-admin/database#initialize-flyway",children:"initialize Flyway"}),". Otherwise you can just enable the property."]})]})}function h(e={}){const{wrapper:r}={...(0,i.R)(),...e.components};return r?(0,a.jsx)(r,{...e,children:(0,a.jsx)(o,{...e})}):o(e)}},8453:(e,r,s)=>{s.d(r,{R:()=>d,x:()=>l});var n=s(6540);const a={},i=n.createContext(a);function d(e){const r=n.useContext(i);return n.useMemo((function(){return"function"==typeof e?e(r):{...r,...e}}),[r,e])}function l(e){let r;return r=e.disableParentContext?"function"==typeof e.components?e.components(a):e.components||a:d(e.components),n.createElement(i.Provider,{value:r},e.children)}}}]);