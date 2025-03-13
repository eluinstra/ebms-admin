"use strict";(self.webpackChunkdocumentation=self.webpackChunkdocumentation||[]).push([[4887],{5535:(e,s,i)=>{i.r(s),i.d(s,{assets:()=>d,contentTitle:()=>c,default:()=>o,frontMatter:()=>t,metadata:()=>n,toc:()=>l});const n=JSON.parse('{"id":"ebms-core/api","title":"EbMS API","description":"The EbMS API consists of the following services","source":"@site/versioned_docs/version-2.19.x/ebms-core/api.md","sourceDirName":"ebms-core","slug":"/ebms-core/api","permalink":"/ebms-admin/docs/2.19.x/ebms-core/api","draft":false,"unlisted":false,"editUrl":"https://github.com/eluinstra/ebms-admin/tree/documentation/versioned_docs/version-2.19.x/ebms-core/api.md","tags":[],"version":"2.19.x","sidebarPosition":6,"frontMatter":{"sidebar_position":6},"sidebar":"tutorialSidebar","previous":{"title":"Database support","permalink":"/ebms-admin/docs/2.19.x/ebms-core/database"},"next":{"title":"Release Notes","permalink":"/ebms-admin/docs/2.19.x/ebms-core/release"}}');var r=i(4848),a=i(8453);const t={sidebar_position:6},c="EbMS API",d={},l=[{value:"CPAService",id:"cpaservice",level:2},{value:"validateCPA(cpa)",id:"validatecpacpa",level:3},{value:"insertCPA(cpa, overwrite)",id:"insertcpacpa-overwrite",level:3},{value:"deleteCPA(cpaId)",id:"deletecpacpaid",level:3},{value:"getCPAIds()",id:"getcpaids",level:3},{value:"getCPA(cpaId)",id:"getcpacpaid",level:3},{value:"UrlMappingService",id:"urlmappingservice",level:2},{value:"setURLMapping(urlMapping)",id:"seturlmappingurlmapping",level:3},{value:"deleteURLMapping(source)",id:"deleteurlmappingsource",level:3},{value:"getURLMappings()",id:"geturlmappings",level:3},{value:"CertificateMappingService",id:"certificatemappingservice",level:2},{value:"setCertificateMapping(certificateMapping)",id:"setcertificatemappingcertificatemapping",level:3},{value:"deleteCertificateMapping(source)",id:"deletecertificatemappingsource",level:3},{value:"getCertificateMappings()",id:"getcertificatemappings",level:3},{value:"EbMSMessageService",id:"ebmsmessageservice",level:2},{value:"ping(cpaId, fromPartyId, toPartyId)",id:"pingcpaid-frompartyid-topartyid",level:3},{value:"sendMessage(message)",id:"sendmessagemessage",level:3},{value:"resendMessage(messageId)",id:"resendmessagemessageid",level:3},{value:"getUnprocessedMessageIds(messageFilter, maxNr)",id:"getunprocessedmessageidsmessagefilter-maxnr",level:3},{value:"getMessage(messageId, process)",id:"getmessagemessageid-process",level:3},{value:"processMessage(messageId)",id:"processmessagemessageid",level:3},{value:"getMessageStatus(messageId)",id:"getmessagestatusmessageid",level:3},{value:"getUnprocessedMessageEvents(messageFilter, eventTypes, maxNr)",id:"getunprocessedmessageeventsmessagefilter-eventtypes-maxnr",level:3},{value:"processMessageEvent(messageId)",id:"processmessageeventmessageid",level:3}];function p(e){const s={a:"a",br:"br",code:"code",h1:"h1",h2:"h2",h3:"h3",header:"header",li:"li",p:"p",ul:"ul",...(0,a.R)(),...e.components};return(0,r.jsxs)(r.Fragment,{children:[(0,r.jsx)(s.header,{children:(0,r.jsx)(s.h1,{id:"ebms-api",children:"EbMS API"})}),"\n",(0,r.jsx)(s.p,{children:"The EbMS API consists of the following services"}),"\n",(0,r.jsxs)(s.ul,{children:["\n",(0,r.jsx)(s.li,{children:(0,r.jsx)(s.a,{href:"#cpaservice",children:"CPAService"})}),"\n",(0,r.jsx)(s.li,{children:(0,r.jsx)(s.a,{href:"#urlmappingservice",children:"UrlMappingService"})}),"\n",(0,r.jsx)(s.li,{children:(0,r.jsx)(s.a,{href:"#certificatemappingservice",children:"CertificateMappingService"})}),"\n",(0,r.jsx)(s.li,{children:(0,r.jsx)(s.a,{href:"#ebmsmessageservice",children:"EbMSMessageService"})}),"\n"]}),"\n",(0,r.jsx)(s.p,{children:"These services are implemented as a SOAP interface and since EbMS 2.18.3 also as a REST interface."}),"\n",(0,r.jsx)(s.h2,{id:"cpaservice",children:"CPAService"}),"\n",(0,r.jsxs)(s.p,{children:["The ",(0,r.jsx)(s.a,{href:"https://github.com/eluinstra/ebms-core/blob/ebms-core-2.19.x/src/main/java/nl/clockwork/ebms/cpa/CPAService.java",children:"CPAService"})," contains functionality to manage CPAs."]}),"\n",(0,r.jsx)(s.h3,{id:"validatecpacpa",children:"validateCPA(cpa)"}),"\n",(0,r.jsxs)(s.p,{children:["Validates CPA ",(0,r.jsx)(s.code,{children:"cpa"}),"."]}),"\n",(0,r.jsx)(s.h3,{id:"insertcpacpa-overwrite",children:"insertCPA(cpa, overwrite)"}),"\n",(0,r.jsxs)(s.p,{children:["Stores CPA ",(0,r.jsx)(s.code,{children:"cpa"}),". If ",(0,r.jsx)(s.code,{children:"overwrite"})," is true and the CPA exists, it will be overwritten.",(0,r.jsx)(s.br,{}),"\n","Returns the cpaId of the CPA."]}),"\n",(0,r.jsx)(s.h3,{id:"deletecpacpaid",children:"deleteCPA(cpaId)"}),"\n",(0,r.jsxs)(s.p,{children:["Removes CPA identified by ",(0,r.jsx)(s.code,{children:"cpaId"}),"."]}),"\n",(0,r.jsx)(s.h3,{id:"getcpaids",children:"getCPAIds()"}),"\n",(0,r.jsx)(s.p,{children:"Returns a list of all cpaIds."}),"\n",(0,r.jsx)(s.h3,{id:"getcpacpaid",children:"getCPA(cpaId)"}),"\n",(0,r.jsxs)(s.p,{children:["Returns the CPA identified by ",(0,r.jsx)(s.code,{children:"cpaId"}),"."]}),"\n",(0,r.jsx)(s.h2,{id:"urlmappingservice",children:"UrlMappingService"}),"\n",(0,r.jsxs)(s.p,{children:["The ",(0,r.jsx)(s.a,{href:"https://github.com/eluinstra/ebms-core/blob/ebms-core-2.19.x/src/main/java/nl/clockwork/ebms/cpa/url/URLMappingService.java",children:"UrlMappingService"})," contains functionality to override CPA's urls."]}),"\n",(0,r.jsx)(s.h3,{id:"seturlmappingurlmapping",children:"setURLMapping(urlMapping)"}),"\n",(0,r.jsxs)(s.p,{children:["Stores URL mapping ",(0,r.jsx)(s.code,{children:"urlMapping"}),"."]}),"\n",(0,r.jsx)(s.h3,{id:"deleteurlmappingsource",children:"deleteURLMapping(source)"}),"\n",(0,r.jsxs)(s.p,{children:["Removes URL mapping identified by source URL ",(0,r.jsx)(s.code,{children:"source"}),"."]}),"\n",(0,r.jsx)(s.h3,{id:"geturlmappings",children:"getURLMappings()"}),"\n",(0,r.jsx)(s.p,{children:"Returns a list of all URL mappings."}),"\n",(0,r.jsx)(s.h2,{id:"certificatemappingservice",children:"CertificateMappingService"}),"\n",(0,r.jsxs)(s.p,{children:["The ",(0,r.jsx)(s.a,{href:"https://github.com/eluinstra/ebms-core/blob/ebms-core-2.19.x/src/main/java/nl/clockwork/ebms/cpa/certificate/CertificateMappingService.java",children:"CertificateMappingService"})," contains functionality to override CPA's certificates."]}),"\n",(0,r.jsx)(s.h3,{id:"setcertificatemappingcertificatemapping",children:"setCertificateMapping(certificateMapping)"}),"\n",(0,r.jsxs)(s.p,{children:["Stores Certificate mapping ",(0,r.jsx)(s.code,{children:"certificateMapping"}),"."]}),"\n",(0,r.jsx)(s.h3,{id:"deletecertificatemappingsource",children:"deleteCertificateMapping(source)"}),"\n",(0,r.jsxs)(s.p,{children:["Removes Certificate mapping identified by source Certificate ",(0,r.jsx)(s.code,{children:"source"}),"."]}),"\n",(0,r.jsx)(s.h3,{id:"getcertificatemappings",children:"getCertificateMappings()"}),"\n",(0,r.jsx)(s.p,{children:"Returns a list of all Certificate mappings."}),"\n",(0,r.jsx)(s.h2,{id:"ebmsmessageservice",children:"EbMSMessageService"}),"\n",(0,r.jsxs)(s.p,{children:["The ",(0,r.jsx)(s.a,{href:"https://github.com/eluinstra/ebms-core/blob/ebms-core-2.19.x/src/main/java/nl/clockwork/ebms/service/EbMSMessageService.java",children:"EbMSMessageService"})," contains functionality for sending and receiving EbMS messages. There is also an MTOM ",(0,r.jsx)(s.a,{href:"https://github.com/eluinstra/ebms-core/blob/ebms-core-2.19.x/src/main/java/nl/clockwork/ebms/service/EbMSMessageServiceMTOM.java",children:"EbMSMessageService"})," available, which is more efficient."]}),"\n",(0,r.jsx)(s.h3,{id:"pingcpaid-frompartyid-topartyid",children:"ping(cpaId, fromPartyId, toPartyId)"}),"\n",(0,r.jsxs)(s.p,{children:["Performs an EbMS ping action for CPA ",(0,r.jsx)(s.code,{children:"cpaId"}),", from party ",(0,r.jsx)(s.code,{children:"fromPartyId"})," to party ",(0,r.jsx)(s.code,{children:"toPartyId"})]}),"\n",(0,r.jsx)(s.h3,{id:"sendmessagemessage",children:"sendMessage(message)"}),"\n",(0,r.jsxs)(s.p,{children:["Sends message ",(0,r.jsx)(s.code,{children:"message"})," as an EbMS message.",(0,r.jsx)(s.br,{}),"\n","Returns the messageId of the generated EbMS message."]}),"\n",(0,r.jsx)(s.h3,{id:"resendmessagemessageid",children:"resendMessage(messageId)"}),"\n",(0,r.jsxs)(s.p,{children:["Resends message identified by ",(0,r.jsx)(s.code,{children:"messageId"})," as an EbMS message.",(0,r.jsx)(s.br,{}),"\n","Returns the messageId of the new EbMS message."]}),"\n",(0,r.jsx)(s.h3,{id:"getunprocessedmessageidsmessagefilter-maxnr",children:"getUnprocessedMessageIds(messageFilter, maxNr)"}),"\n",(0,r.jsxs)(s.p,{children:["Returns all messageIds of messages with status ",(0,r.jsx)(s.code,{children:"RECEIVED"})," that satisfy filter ",(0,r.jsx)(s.code,{children:"messageFilter"}),". If ",(0,r.jsx)(s.code,{children:"maxNr"})," is given, then maxNr messageIds are returned."]}),"\n",(0,r.jsx)(s.h3,{id:"getmessagemessageid-process",children:"getMessage(messageId, process)"}),"\n",(0,r.jsxs)(s.p,{children:["Returns the message identified by ",(0,r.jsx)(s.code,{children:"messageId"}),". If ",(0,r.jsx)(s.code,{children:"process"})," is true, the message is given the status ",(0,r.jsx)(s.code,{children:"PROCESSED"}),", which means that it is no longer returned in the list of ",(0,r.jsx)(s.a,{href:"#getunprocessedmessageidsmessagefilter-maxnr",children:"getUnprocessedMessageIds"}),"."]}),"\n",(0,r.jsx)(s.h3,{id:"processmessagemessageid",children:"processMessage(messageId)"}),"\n",(0,r.jsxs)(s.p,{children:["Sets the status of the message identified by ",(0,r.jsx)(s.code,{children:"messageId"})," to ",(0,r.jsx)(s.code,{children:"PROCESSED"}),", so that it is no longer returned in the list of ",(0,r.jsx)(s.a,{href:"#getunprocessedmessageidsmessagefilter-maxnr",children:"getUnprocessedMessageIds"}),"."]}),"\n",(0,r.jsx)(s.h3,{id:"getmessagestatusmessageid",children:"getMessageStatus(messageId)"}),"\n",(0,r.jsxs)(s.p,{children:["Returns the message status of the message identified by ",(0,r.jsx)(s.code,{children:"messageId"}),"."]}),"\n",(0,r.jsx)(s.h3,{id:"getunprocessedmessageeventsmessagefilter-eventtypes-maxnr",children:"getUnprocessedMessageEvents(messageFilter, eventTypes, maxNr)"}),"\n",(0,r.jsxs)(s.p,{children:["Returns the events that satisfy filter ",(0,r.jsx)(s.code,{children:"messageFilter"})," and event types ",(0,r.jsx)(s.code,{children:"eventTypes"}),". If ",(0,r.jsx)(s.code,{children:"maxNr"})," is given, then maxNr events are returned. The possible event types are"]}),"\n",(0,r.jsxs)(s.ul,{children:["\n",(0,r.jsxs)(s.li,{children:[(0,r.jsx)(s.code,{children:"RECEIVED"})," - when a message is received"]}),"\n",(0,r.jsxs)(s.li,{children:[(0,r.jsx)(s.code,{children:"DELIVERED"})," - when a message has been sent successfully"]}),"\n",(0,r.jsxs)(s.li,{children:[(0,r.jsx)(s.code,{children:"FAILED"})," - when a message returns an error while sending"]}),"\n",(0,r.jsxs)(s.li,{children:[(0,r.jsx)(s.code,{children:"EXPIRED"})," - when a message could not be sent within the number of attempts and time defined in the CPA"]}),"\n"]}),"\n",(0,r.jsxs)(s.p,{children:["Events can only be retrieved with this method when ",(0,r.jsx)(s.a,{href:"/ebms-admin/docs/2.19.x/ebms-core/properties#eventlistener",children:"EventListener property"})," ",(0,r.jsx)(s.code,{children:"eventListener.type"})," is set to ",(0,r.jsx)(s.code,{children:"DAO"}),"."]}),"\n",(0,r.jsx)(s.h3,{id:"processmessageeventmessageid",children:"processMessageEvent(messageId)"}),"\n",(0,r.jsxs)(s.p,{children:["Sets processed to true for the event of the message identified by ",(0,r.jsx)(s.code,{children:"messageId"}),", so that it is no longer returned in the list of ",(0,r.jsx)(s.a,{href:"#getunprocessedmessageeventsmessagefilter-eventtypes-maxnr",children:"getUnprocessedMessageEvents"})," (and ",(0,r.jsx)(s.a,{href:"#getunprocessedmessageidsmessagefilter-maxnr",children:"getUnprocessedMessageIds"})," in case of a ",(0,r.jsx)(s.code,{children:"RECEIVED"})," event)."]})]})}function o(e={}){const{wrapper:s}={...(0,a.R)(),...e.components};return s?(0,r.jsx)(s,{...e,children:(0,r.jsx)(p,{...e})}):p(e)}},8453:(e,s,i)=>{i.d(s,{R:()=>t,x:()=>c});var n=i(6540);const r={},a=n.createContext(r);function t(e){const s=n.useContext(a);return n.useMemo((function(){return"function"==typeof e?e(s):{...s,...e}}),[s,e])}function c(e){let s;return s=e.disableParentContext?"function"==typeof e.components?e.components(r):e.components||r:t(e.components),n.createElement(a.Provider,{value:s},e.children)}}}]);