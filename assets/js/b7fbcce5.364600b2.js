"use strict";(self.webpackChunkdocumentation=self.webpackChunkdocumentation||[]).push([[414],{7039:(e,s,n)=>{n.r(s),n.d(s,{assets:()=>t,contentTitle:()=>a,default:()=>h,frontMatter:()=>d,metadata:()=>i,toc:()=>o});const i=JSON.parse('{"id":"ebms-admin/examples","title":"Examples","description":"Below you\'ll find and simple example of how to configure 2 EbMS Admin Consoles with embedded EbMS adapter to communicate with each other.","source":"@site/docs/ebms-admin/examples.md","sourceDirName":"ebms-admin","slug":"/ebms-admin/examples","permalink":"/ebms-admin/docs/ebms-admin/examples","draft":false,"unlisted":false,"editUrl":"https://github.com/eluinstra/ebms-admin/tree/documentation/docs/ebms-admin/examples.md","tags":[],"version":"current","sidebarPosition":9,"frontMatter":{"sidebar_position":9},"sidebar":"tutorialSidebar","previous":{"title":"Database support","permalink":"/ebms-admin/docs/ebms-admin/database"},"next":{"title":"SOAP Interface","permalink":"/ebms-admin/docs/ebms-admin/soap"}}');var r=n(4848),l=n(8453);const d={sidebar_position:9},a="Examples",t={},o=[{value:"Docker",id:"docker",level:2},{value:"Kubernetes",id:"kubernetes",level:2},{value:"Ansible",id:"ansible",level:2},{value:"Example with 2 EbMS adapters",id:"example-with-2-ebms-adapters",level:2},{value:"Prerequisites",id:"prerequisites",level:3},{value:"Create and configure party Overheid",id:"create-and-configure-party-overheid",level:3},{value:"Create and configure party Digipoort",id:"create-and-configure-party-digipoort",level:3},{value:"Send messages from party DigiPoort to Overheid",id:"send-messages-from-party-digipoort-to-overheid",level:3},{value:"Send messages from party Overheid to Digipoort",id:"send-messages-from-party-overheid-to-digipoort",level:3}];function c(e){const s={a:"a",code:"code",h1:"h1",h2:"h2",h3:"h3",header:"header",li:"li",p:"p",pre:"pre",ul:"ul",...(0,l.R)(),...e.components};return(0,r.jsxs)(r.Fragment,{children:[(0,r.jsx)(s.header,{children:(0,r.jsx)(s.h1,{id:"examples",children:"Examples"})}),"\n",(0,r.jsxs)(s.p,{children:[(0,r.jsx)(s.a,{href:"#example-with-2-ebms-adapters",children:"Below"})," you'll find and simple example of how to configure 2 EbMS Admin Consoles with embedded EbMS adapter to communicate with each other.\nYou can also find this and other examples configured in ",(0,r.jsx)(s.a,{href:"#docker",children:"Docker"}),", ",(0,r.jsx)(s.a,{href:"#kubernetes",children:"Kubernetes"})," and ",(0,r.jsx)(s.a,{href:"#ansible",children:"Ansible"}),"."]}),"\n",(0,r.jsx)(s.h2,{id:"docker",children:"Docker"}),"\n",(0,r.jsx)(s.p,{children:"For Docker examples see"}),"\n",(0,r.jsxs)(s.ul,{children:["\n",(0,r.jsx)(s.li,{children:(0,r.jsx)(s.a,{href:"https://github.com/eluinstra/ebms-docker",children:"https://github.com/eluinstra/ebms-docker"})}),"\n",(0,r.jsx)(s.li,{children:(0,r.jsx)(s.a,{href:"https://gitlab.com/peterzandbergen/ebms-admin-docker",children:"https://gitlab.com/peterzandbergen/ebms-admin-docker"})}),"\n"]}),"\n",(0,r.jsx)(s.h2,{id:"kubernetes",children:"Kubernetes"}),"\n",(0,r.jsx)(s.p,{children:"For Kubernetes examples see"}),"\n",(0,r.jsxs)(s.ul,{children:["\n",(0,r.jsx)(s.li,{children:(0,r.jsx)(s.a,{href:"https://github.com/eluinstra/ebms-kubernetes",children:"https://github.com/eluinstra/ebms-kubernetes"})}),"\n"]}),"\n",(0,r.jsx)(s.h2,{id:"ansible",children:"Ansible"}),"\n",(0,r.jsx)(s.p,{children:"For Ansible examples see"}),"\n",(0,r.jsxs)(s.ul,{children:["\n",(0,r.jsx)(s.li,{children:(0,r.jsx)(s.a,{href:"https://github.com/eluinstra/ebms-ansible",children:"https://github.com/eluinstra/ebms-ansible"})}),"\n"]}),"\n",(0,r.jsx)(s.h2,{id:"example-with-2-ebms-adapters",children:"Example with 2 EbMS adapters"}),"\n",(0,r.jsxs)(s.p,{children:["In this example you will configure 2 ebms adapters that will communicate with each other. One represents party ",(0,r.jsx)(s.code,{children:"Overheid"}),", the other one represents party ",(0,r.jsx)(s.code,{children:"Digipoort"}),". They will use the ",(0,r.jsx)(s.code,{children:"aanleveren"})," and ",(0,r.jsx)(s.code,{children:"afleveren"})," services as defined in the CPA."]}),"\n",(0,r.jsx)(s.h3,{id:"prerequisites",children:"Prerequisites"}),"\n",(0,r.jsxs)(s.ul,{children:["\n",(0,r.jsx)(s.li,{children:"download and install Java 8 (or later)"}),"\n",(0,r.jsxs)(s.li,{children:["download ",(0,r.jsx)(s.a,{href:"https://github.com/eluinstra/ebms-admin/releases/download/ebms-admin-2.19.3/ebms-admin-2.19.3.jar",children:"ebms-admin-2.19.3.jar"})]}),"\n",(0,r.jsxs)(s.li,{children:["download ",(0,r.jsx)(s.a,{href:"https://raw.githubusercontent.com/eluinstra/ebms-admin/ebms-admin-2.17.3/resources/CPAs/cpaStubEBF.rm.https.signed.xml",children:"cpaStubEBF.rm.https.signed.xml"})]}),"\n"]}),"\n",(0,r.jsx)(s.h3,{id:"create-and-configure-party-overheid",children:"Create and configure party Overheid"}),"\n",(0,r.jsxs)(s.ul,{children:["\n",(0,r.jsxs)(s.li,{children:["create directory ",(0,r.jsx)(s.code,{children:"overheid"})]}),"\n",(0,r.jsxs)(s.li,{children:["copy ebms-admin-2.19.3.jar to ",(0,r.jsx)(s.code,{children:"overheid"})]}),"\n",(0,r.jsxs)(s.li,{children:["start ebms-admin on port ",(0,r.jsx)(s.code,{children:"8000"})," with the SOAP Interface using a HSQLDB server"]}),"\n"]}),"\n",(0,r.jsx)(s.pre,{children:(0,r.jsx)(s.code,{className:"language-sh",children:"java -cp ebms-admin-2.19.3.jar nl.clockwork.ebms.admin.StartEmbedded -port 8000 -soap -hsqldb\n"})}),"\n",(0,r.jsxs)(s.ul,{children:["\n",(0,r.jsxs)(s.li,{children:["open web browser at ",(0,r.jsx)(s.a,{href:"http://localhost:8000",children:"http://localhost:8000"})]}),"\n",(0,r.jsxs)(s.li,{children:["configure properties at the ",(0,r.jsx)(s.a,{href:"https://localhost:8000/wicket/bookmarkable/nl.clockwork.ebms.admin.web.configuration.EbMSAdminPropertiesPage",children:"EbMSAdmin Properties Page"}),"\n",(0,r.jsxs)(s.ul,{children:["\n",(0,r.jsxs)(s.li,{children:["set port ",(0,r.jsx)(s.code,{children:"8088"})]}),"\n",(0,r.jsxs)(s.li,{children:["set database port ",(0,r.jsx)(s.code,{children:"9000"})]}),"\n",(0,r.jsx)(s.li,{children:"save"}),"\n",(0,r.jsx)(s.li,{children:"restart ebms-admin"}),"\n"]}),"\n"]}),"\n",(0,r.jsxs)(s.li,{children:["upload CPA ",(0,r.jsx)(s.code,{children:"cpaStubEBF.rm.https.signed.xml"})," at the ",(0,r.jsx)(s.a,{href:"http://localhost:8000/wicket/bookmarkable/nl.clockwork.ebms.admin.web.service.cpa.CPAUploadPage",children:"CPA Upload Page"})]}),"\n"]}),"\n",(0,r.jsx)(s.h3,{id:"create-and-configure-party-digipoort",children:"Create and configure party Digipoort"}),"\n",(0,r.jsxs)(s.ul,{children:["\n",(0,r.jsxs)(s.li,{children:["create directory ",(0,r.jsx)(s.code,{children:"digipoort"})]}),"\n",(0,r.jsxs)(s.li,{children:["copy ebms-admin-2.19.3.jar to ",(0,r.jsx)(s.code,{children:"digipoort"})]}),"\n",(0,r.jsxs)(s.li,{children:["start ebms-admin on default port ",(0,r.jsx)(s.code,{children:"8080"})," with the SOAP Interface using a HSQLDB server"]}),"\n"]}),"\n",(0,r.jsx)(s.pre,{children:(0,r.jsx)(s.code,{className:"language-sh",children:"java -cp ebms-admin-2.19.3.jar nl.clockwork.ebms.admin.StartEmbedded -soap -hsqldb\n"})}),"\n",(0,r.jsxs)(s.ul,{children:["\n",(0,r.jsxs)(s.li,{children:["open web browser at ",(0,r.jsx)(s.a,{href:"http://localhost:8080",children:"http://localhost:8080"})]}),"\n",(0,r.jsxs)(s.li,{children:["configure properties at the ",(0,r.jsx)(s.a,{href:"http://localhost:8080/wicket/bookmarkable/nl.clockwork.ebms.admin.web.configuration.EbMSAdminPropertiesPage",children:"EbMSAdmin Properties Page"})," [Optional]","\n",(0,r.jsxs)(s.ul,{children:["\n",(0,r.jsx)(s.li,{children:"use default properties, so no changes"}),"\n",(0,r.jsx)(s.li,{children:"save"}),"\n",(0,r.jsx)(s.li,{children:"restart ebms-admin"}),"\n"]}),"\n"]}),"\n",(0,r.jsxs)(s.li,{children:["upload CPA cpaStubEBF.rm.https.signed.xml at the ",(0,r.jsx)(s.a,{href:"http://localhost:8080/wicket/bookmarkable/nl.clockwork.ebms.admin.web.service.cpa.CPAUploadPage",children:"CPA Upload Page"})]}),"\n"]}),"\n",(0,r.jsx)(s.h3,{id:"send-messages-from-party-digipoort-to-overheid",children:"Send messages from party DigiPoort to Overheid"}),"\n",(0,r.jsxs)(s.ul,{children:["\n",(0,r.jsxs)(s.li,{children:["send a ping to party OVerheid at the ",(0,r.jsx)(s.a,{href:"http://localhost:8080/wicket/bookmarkable/nl.clockwork.ebms.admin.web.service.message.PingPage",children:"Ping Page"}),"\n",(0,r.jsxs)(s.ul,{children:["\n",(0,r.jsxs)(s.li,{children:["CPA Id ",(0,r.jsx)(s.code,{children:"cpaStubEBF.rm.https.signed"})]}),"\n",(0,r.jsxs)(s.li,{children:["From Party Id ",(0,r.jsx)(s.code,{children:"urn:osb:oin:00000000000000000000"})]}),"\n",(0,r.jsxs)(s.li,{children:["To Party Id ",(0,r.jsx)(s.code,{children:"urn:osb:oin:000000000000000000001"})]}),"\n"]}),"\n"]}),"\n",(0,r.jsxs)(s.li,{children:["send a message to party OVerheid at the ",(0,r.jsx)(s.a,{href:"http://localhost:8080/wicket/bookmarkable/nl.clockwork.ebms.admin.web.service.message.SendMessagePageX",children:"SendMessage Page"}),"\n",(0,r.jsxs)(s.ul,{children:["\n",(0,r.jsxs)(s.li,{children:["CPA Id ",(0,r.jsx)(s.code,{children:"cpaStubEBF.rm.https.signed"})]}),"\n",(0,r.jsxs)(s.li,{children:["From Party Id ",(0,r.jsx)(s.code,{children:"urn:osb:oin:00000000000000000000"})]}),"\n",(0,r.jsxs)(s.li,{children:["From Role ",(0,r.jsx)(s.code,{children:"DIGIPOORT"})]}),"\n",(0,r.jsxs)(s.li,{children:["To Party Id ",(0,r.jsx)(s.code,{children:"urn:osb:oin:00000000000000000001"})]}),"\n",(0,r.jsxs)(s.li,{children:["To Role ",(0,r.jsx)(s.code,{children:"OVERHEID"})]}),"\n",(0,r.jsxs)(s.li,{children:["Service ",(0,r.jsx)(s.code,{children:"urn:osb:services:osb:afleveren:1.1$1.0"})]}),"\n",(0,r.jsxs)(s.li,{children:["Action ",(0,r.jsx)(s.code,{children:"afleveren"})]}),"\n",(0,r.jsx)(s.li,{children:"Add a Data Source [Optional]"}),"\n"]}),"\n"]}),"\n",(0,r.jsxs)(s.li,{children:["view traffic at the ",(0,r.jsx)(s.a,{href:"http://localhost:8080/wicket/bookmarkable/nl.clockwork.ebms.admin.web.message.TrafficPage",children:"Traffic Page"})]}),"\n",(0,r.jsxs)(s.li,{children:["find the different endpoints of the ",(0,r.jsx)(s.a,{href:"soap",children:"SOAP Interface"})," ",(0,r.jsx)(s.a,{href:"http://localhost:8080/service",children:"here"})]}),"\n"]}),"\n",(0,r.jsx)(s.h3,{id:"send-messages-from-party-overheid-to-digipoort",children:"Send messages from party Overheid to Digipoort"}),"\n",(0,r.jsxs)(s.ul,{children:["\n",(0,r.jsxs)(s.li,{children:["send a ping to party Digipoort at the ",(0,r.jsx)(s.a,{href:"http://localhost:8000/wicket/bookmarkable/nl.clockwork.ebms.admin.web.service.message.PingPage",children:"Ping Page"}),"\n",(0,r.jsxs)(s.ul,{children:["\n",(0,r.jsxs)(s.li,{children:["CPA Id ",(0,r.jsx)(s.code,{children:"cpaStubEBF.rm.https.signed.xml"})]}),"\n",(0,r.jsxs)(s.li,{children:["From Party Id ",(0,r.jsx)(s.code,{children:"urn:osb:oin:00000000000000000001"})]}),"\n",(0,r.jsxs)(s.li,{children:["To Party Id ",(0,r.jsx)(s.code,{children:"urn:osb:oin:00000000000000000000"})]}),"\n"]}),"\n"]}),"\n",(0,r.jsxs)(s.li,{children:["send a message to party Digipoort at the ",(0,r.jsx)(s.a,{href:"http://localhost:8000/wicket/bookmarkable/nl.clockwork.ebms.admin.web.service.message.SendMessagePageX",children:"SendMessage Page"}),"\n",(0,r.jsxs)(s.ul,{children:["\n",(0,r.jsxs)(s.li,{children:["CPA Id ",(0,r.jsx)(s.code,{children:"cpaStubEBF.rm.https.signed.xml"})]}),"\n",(0,r.jsxs)(s.li,{children:["From Party Id ",(0,r.jsx)(s.code,{children:"urn:osb:oin:00000000000000000001"})]}),"\n",(0,r.jsxs)(s.li,{children:["From Role ",(0,r.jsx)(s.code,{children:"OVERHEID"})]}),"\n",(0,r.jsxs)(s.li,{children:["To Party Id ",(0,r.jsx)(s.code,{children:"urn:osb:oin:00000000000000000000"})]}),"\n",(0,r.jsxs)(s.li,{children:["To Role ",(0,r.jsx)(s.code,{children:"DIGIPOORT"})]}),"\n",(0,r.jsxs)(s.li,{children:["Service ",(0,r.jsx)(s.code,{children:"urn:osb:services:osb:afleveren:1.1$1.0"})]}),"\n",(0,r.jsxs)(s.li,{children:["Action ",(0,r.jsx)(s.code,{children:"bevestigAfleveren"})]}),"\n",(0,r.jsx)(s.li,{children:"Add a Data Source [Optional]"}),"\n"]}),"\n"]}),"\n",(0,r.jsxs)(s.li,{children:["view traffic at the ",(0,r.jsx)(s.a,{href:"http://localhost:8000/wicket/bookmarkable/nl.clockwork.ebms.admin.web.message.TrafficPage",children:"Traffic Page"})]}),"\n",(0,r.jsxs)(s.li,{children:["find the different endpoints of the ",(0,r.jsx)(s.a,{href:"soap",children:"SOAP Interface"})," ",(0,r.jsx)(s.a,{href:"http://localhost:8000/service",children:"here"})]}),"\n"]})]})}function h(e={}){const{wrapper:s}={...(0,l.R)(),...e.components};return s?(0,r.jsx)(s,{...e,children:(0,r.jsx)(c,{...e})}):c(e)}},8453:(e,s,n)=>{n.d(s,{R:()=>d,x:()=>a});var i=n(6540);const r={},l=i.createContext(r);function d(e){const s=i.useContext(l);return i.useMemo((function(){return"function"==typeof e?e(s):{...s,...e}}),[s,e])}function a(e){let s;return s=e.disableParentContext?"function"==typeof e.components?e.components(r):e.components||r:d(e.components),i.createElement(l.Provider,{value:s},e.children)}}}]);