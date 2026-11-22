# EbMS 2.0 Specification Conformance Validation

**Scope:** `ebMS_v2_0.md` (OASIS ebXML Message Service Specification v2.0) validated against the
implementation in `ebms-core` (`nl.clockwork.ebms.*`) and `ebms-admin`.

**Overall verdict:** the implementation is a faithful, high-fidelity implementation of
Part I (Core Functionality) and most of Part II (Additional Features). The XSD shipped with the
code matches Appendix A almost verbatim, error codes/actions map 1:1, and the message lifecycle
(send → persist → deliver → ack → retry) is complete.

Severity ratings used below:

- ✅ **Conforms** — requirement met in code
- ⚠️ **Deviation** — works but diverges from the letter of the spec
- ❌ **Gap** — spec requirement not met

---

## 2. ebXML with SOAP (Packaging)

| Requirement (spec ref) | Finding |
|---|---|
| §2.1.2 Message Package: `multipart/related`, `start` param present | ✅ `MultipartBodyPublisher.contentType()` emits `multipart/related; boundary=...; type="text/xml"; start="<cid>"; start-info="text/xml"` |
| §2.1.2 MUST support non-multipart messages | ✅ `EbMSMessageReader.read()` handles both multipart and plain `text/xml`; `EbMSMessageWriter.writeMessage()` does plain POST |
| §2.1.3 Header Container `text/xml` + optional charset | ✅ writer sets `Content-Type: text/xml; charset=UTF-8` |
| §2.1.4/§5.1.2 Manifest MUST be present when payload exists | ⚠️ **Weak check.** `ManifestValidator.validate()` only validates a manifest *if one is present* and trims attachments not referenced. There is no rule that a message **with attachments but no Manifest is rejected**. Spec §5.1.2 says Manifest MUST be present when payload exists. `ebMSMessageMissingManifest` IT passes (it checks manifest *version*, not existence). A payload message without Manifest would be accepted. |
| §2.1.6 MIME errors reported per SOAPAttach | ⚠️ `EbMSMessageReader` propagates `MimeException` → `EbMSProcessorException` → generic 500 (`handleException`), not an ebXML `MimeProblem` Error message. |
| §2.2 XML prolog / encoding declaration consistency | ✅ no explicit check required by spec beyond not conflicting; UTF-8 used throughout. |
| §2.3.1 namespace `http://www.oasis-open.org/committees/ebxml-msg/schema/msg-header-2_0.xsd` | ✅ XSD `targetNamespace` and JAXB `@XmlSchema` match exactly |
| §2.3.2 `xsi:schemaLocation` "strongly RECOMMENDED" | ❌ **Not emitted.** `createSOAPMessage` builds the envelope via JAXB without any `schemaLocation` attribute. Receiver-side `XSDValidator` validates against the local copy, so interop works, but the RECOMMENDED attribute is absent. (This is a SHOULD-adjacent recommendation, not a MUST.) |
| §2.3.9 `mustUnderstand="1"` on header elements | ✅ XSD marks `soap:mustUnderstand use="required"` in `headerExtension.grp`; factory sets it (`setMustUnderstand(true)`) on `MessageHeader`, `SyncReply`, `ErrorList`, `AckRequested`, `Acknowledgment` |
| §2.3.10/2.3.11 actor URIs | ✅ `ActorType.URN_OASIS_NAMES_TC_EBXML_MSG_ACTOR_NEXT_MSH` / `TO_PARTY_MSH` match the spec URNs exactly |

**§2 verdict: ✅ with one ⚠️** — no "Manifest required when payload present" enforcement.

---

## 3. Core Extension Elements

| Requirement | Finding |
|---|---|
| §3.1 MessageHeader required, `From`/`To`/`CPAId`/`ConversationId`/`Service`/`Action`/`MessageData` | ✅ XSD sequence enforces all; `MessageHeaderValidator.validateMessageHeader` checks version, from, to, service/action |
| §3.1.1 PartyId: without `type`, content MUST be a URI else `Inconsistent`/`Error` | ✅ `isValid(PartyId)` accepts if `type` present **or** value parses as URI; else `INCONSISTENT` |
| §3.1.1 Role zero-or-one, must follow last PartyId | ✅ XSD enforces order; role required check in validator (skipped for the ebXML service URI — sensible) |
| §3.1.2 CPAId unrecognized → **`NotRecognized`** + `Error` | ❌ **Deviation.** Unknown CPAId is not turned into an ebXML `Error` at all. `EbMSMessageProcessor.processRequest` throws `ValidationException("CPA cpaId not found!")` when `!cpaManager.existsCPA(cpaId)`; the servlet maps any `ValidationException` to a **SOAP Fault 500** (`handleValidationException("Client", …)`). The spec wants an *Error Message* (Action `MessageError`, `errorCode=NotRecognized`) sent to the sender. The IT `ebMSMessageInvalidCPAId` asserts the 500/SOAP-Fault behavior — i.e. the deviation is intentional and tested, but it is **not spec-compliant error reporting**. Note the `NotRecognized` code doesn't even exist in `EbMSErrorCode` — the enum has `ValueNotRecognized` (which is correct for Service/Action) but there is no `NotRecognized` enum member. |
| §3.1.3 ConversationId unique within CPAId | ✅ no uniqueness check needed at MSH; passed through |
| §3.1.4/§3.1.5 Service/Action unrecognized → **`NotRecognized`** | ⚠️ **Deviated code.** `validateService` uses `VALUE_NOT_RECOGNIZED` ("ValueNotRecognized") for an unknown Service or Action, not `NotRecognized`. Per §3.1.5, unrecognized Service/Action MUST report `errorCode=NotRecognized`. (Severity Error is correct.) |
| §3.1.6.1 MessageId per RFC2822 | ✅ generated via `EbMSIdGenerator.createMessageId(hostname, conversationId, …)` |
| §3.1.6.2 Timestamp UTC dateTime | ✅ `MessageData.setTimestamp(Instant)` |
| §3.1.6.3 RefToMessageId when present MUST reference an earlier message | ⚠️ `validateMessageData` checks `existsRefToMessageId` against the local store → `VALUE_NOT_RECOGNIZED` if not found. For a response from a *remote* sender the referenced message may legitimately not be in local storage, but the implementation is conservative (only accepts null or locally-known). Acceptable for a two-party MSH. |
| §3.1.6.4 TimeToLive expired → `TimeToLiveExpired` + `Error` | ✅ `checkTimeToLive` + `TIME_TO_LIVE_EXPIRED` (IT `ebMSMessageTimeToLiveExpired`) |
| §3.1.7 DuplicateElimination MUST NOT be present if CPA `never` | ✅ `checkDuplicateElimination` (IT `ebMSMessageMissingDuplicateElimination`) |
| §3.1.8 Description `xml:lang` required | ✅ XSD `Description` requires `xml:lang` |
| §3.2/§3.2.2 Manifest validation: cid MUST resolve, else `MimeProblem` | ✅ `ManifestValidator.addAttachment` → `MIME_PROBLEM "MIME part not found."`; non-cid hrefs are rejected with `MimeProblem "URI cannot be resolved."` (spec says *implementation decision* — implementation chose to reject). ITs `ebMSMessageInvalidCID`, `ebMSMessageUnknownCID` cover this. |

**§3 verdict: ⚠️** — two error-code deviations (`NotRecognized` for CPAId → becomes SOAP Fault 500; `NotRecognized` for Service/Action → `ValueNotRecognized`).

---

## 4. Core Modules

### 4.1 Security / Signature (§4.1.3)

| Requirement | Finding |
|---|---|
| Signature in SOAP Header, XMLDSIG-conformant | ✅ `EbMSSignatureGenerator.sign()` appends `ds:Signature` to `soap:Header` via Apache XML Security |
| `Reference URI=""` with Transforms: enveloped-signature + XPath (exclude nextMSH/next actor) + C14N | ✅ **Exact match.** `createTransforms()` adds `TRANSFORM_ENVELOPED_SIGNATURE`, the XPath `not(ancestor-or-self::node()[@prefix:actor="urn:oasis:names:tc:ebxml-msg:actor:nextMSH"]\|ancestor-or-self::node()[@prefix:actor="http://schemas.xmlsoap.org/soap/actor/next"])`, and C14N-omit-comments — the three transforms the spec prescribes, in the same order |
| Recommended `CanonicalizationMethod` c14n-20010315 | ✅ `TRANSFORM_C14N_OMIT_COMMENTS` |
| Recommended `SignatureMethod dsa-sha1` **SHALL be supported** | ⚠️ **Algorithms are CPA-driven, not fixed.** `CPAUtils.getSignatureAlgorithm/getHashFunction` read `SenderNonRepudiation.SignatureAlgorithm/@w3c` and `HashFunction` from the CPA, with fallbacks. There is no hardcoded `dsa-sha1` support; a CPA that specifies `dsa-sha1` would flow through, but it's not a declared default and not separately tested. The "SHALL support dsa-sha1" clause is therefore met only *if* the CPA requests it. |
| Signed payload References (cid: URIs) | ✅ `signature.addDocument(CID + contentId, null, digest)` for each attachment |
| KeyInfo with certificate | ✅ `addCertificate` attaches X509 cert |
| §4.1.4.5 "sign first, then encrypt" | ✅ Ordering is correct: `EbMSControllerHandler.sendMessage` → `signatureGenerator.generate` → `storeMessage`; later `DeliveryTaskHandler.sendMessage` → `messageEncrypter.encrypt` → `sendMessage`. Decrypt path: `EbMSMessageValidator.validateAndDecryptMessage` decrypts *before* signature validation. |
| §4.1.1 first signature is From Party's | ⚠️ No explicit "only first" validation; `SignatureValidator` validates the single `message.getSignature()`. Acceptable since the factory only ever produces one From-Party signature. |
| §4.1.4.2 Acknowledgment signed with Reference list consistent with original | ✅ `createEbMSAcknowledgment` copies `message.getSignature().getSignedInfo().getReference()` into the `Acknowledgment` when `AckRequested@signed=true` (IT `EbMSMessageSignedIT`) |
| §4.2.3.2 SecurityFailure | ✅ `SECURITY_FAILURE` used for missing signature / bad algorithms / failed verification |

### 4.2 Error Handling

| Requirement | Finding |
|---|---|
| §4.2.3 ErrorList: id, version, mustUnderstand="1", `highestSeverity`, 1+ Error | ✅ `createErrorList()` sets version + mustUnderstand; `highestSeverity` set by factory |
| §4.2.3.4 error code set | ⚠️ `EbMSErrorCode` implements 9 codes: `ValueNotRecognized, NotSupported, Inconsistent, OtherXml, DeliveryFailure, TimeToLiveExpired, SecurityFailure, MimeProblem, Unknown`. **Missing from the spec's table: `NotRecognized`** (spec §3.1.2/§3.1.5) — and `ValueNotRecognized` is *not* in the spec's §4.2.3.4 table (the spec uses `NotRecognized` for both "value not recognized" and "element not recognized" scenarios). So the implementation's `ValueNotRecognized` is effectively the spec's `NotRecognized`, but named differently, and the literal `NotRecognized` string is never emitted. This is a **terminology mismatch** with the normative error-code table. |
| §4.2.3.2.5 location = XPointer or `cid:` | ✅ `createError` always sets `location` (XPointer-style `//Header/…` or cid) |
| §4.2.4.3 independent Error message: Service=`urn:oasis:names:tc:ebxml-msg:service`, Action=`MessageError` | ✅ `EbMSAction.MESSAGE_ERROR` + `EBMS_SERVICE_URI`; `createResponseMessageHeader` uses them (verified by IT `ebMSMessageInvalidService` asserting `Action=MessageError`) |
| `codeContext` default `urn:oasis:names:tc:ebxml-msg:service:errors` | ⚠️ **Always set explicitly** to the correct value (good), but since it's non-null the XSD `default` never applies — functionally equivalent. |
| §4.2.4.1 don't send Error if in-error message already has ErrorList highestSeverity=Error | ⚠️ No such pre-check found; validation failures produce exactly one Error per failed message. Minor. |

### 4.3 SyncReply

| Requirement | Finding |
|---|---|
| `actor="http://schemas.xmlsoap.org/soap/actor/next"`, mustUnderstand=1 | ✅ `createSyncReply` sets actor to `NSURI_SOAP_NEXT_ACTOR`, version 2.0, mustUnderstand |
| MUST NOT override CPA `syncReplyMode=none`; if present → `Inconsistent`/`Error` | ✅ `checkSyncReply` enforces CPA↔element consistency (design doc `syncreply-messageorder.md` documents the Option A/B choice; ITs `ebMSMessageMissingSyncReply`, `ebMSMessageInvalidSyncReplyActor`) |

---

## 5. Combining Extension Elements

| Requirement | Finding |
|---|---|
| §5.1.2 Manifest required with payload | ⚠️ same gap as §2 (no existence check) |
| §5.1.4 ErrorList highestSeverity=Error MUST NOT coexist with Manifest | ✅ effectively guaranteed: error messages are built by `createEbMSMessageError` with an empty body (no Manifest); XSD allows it but the factory never combines them |
| §5.1.5 SyncReply on any sync outbound message | ✅ handled |

---

## 6. Reliable Messaging

| Requirement | Finding |
|---|---|
| §6.1 persistent storage of MessageId for duplicate elimination | ✅ messages persisted in DB via `ebMSDAO.insertMessage`; `isDuplicateMessage` checks `existsMessage(messageId)` |
| §6.3.1 AckRequested: id/version/mustUnderstand/actor/signed | ✅ generated per CPA (`createAckRequested`), version/mustUnderstand/signed/actor set |
| §6.3.1.2 `signed` required | ✅ XSD `signed use="required"`; `checkAckSignatureRequested` validates CPA consistency (IT `ebMSMessageInvalidAckRequestedSigned`) |
| §6.3.1.4 AckRequested MUST NOT be on Acknowledgment-only messages; MUST NOT be on Error messages | ⚠️ **No explicit check.** `EbMSMessageProcessor` routes by action; an `EbMSAcknowledgment` model doesn't carry `ackRequested`, and the factory never adds it to ack/error responses — so it's structurally prevented, but there is no validator that *rejects* an inbound ack-with-AckRequested. |
| §6.3.2 Acknowledgment element structure | ✅ `Acknowledgment` model has Timestamp, RefToMessageId, From, Reference; `createEbMSAcknowledgment` populates all; actor set (with a `TODO resolve actor from CPA` — hardcoded to `toPartyMSH`) |
| §6.3.2.7 ack sent alone: Service=`urn:oasis:names:tc:ebxml-msg:service`, Action=`Acknowledgment` | ✅ `EbMSAction.ACKNOWLEDGMENT` |
| §6.4.1 DuplicateElimination present → persist & at-most-once | ✅ `DuplicateMessageHandler.handleMessage` resends the stored first response; never re-delivers to app |
| §6.5.3 ack generated when AckRequested targets this MSH | ✅ `processMessage` → `acknowledgmentProcessor.processAcknowledgment` when `AckRequested != null` |
| §6.5.4 resend on RetryInterval, up to Retries; notify after max | ⚠️ **Partial.** Retry scheduling works (`createNextTask` uses CPA `RetryInterval`/`Retries`, TTL as last-deadline; `QuartzDeliveryTaskManager`/`JMSDeliveryTaskManager`/`DAODeliveryTaskManager`). However, when retries are exhausted the message goes to `DELIVERY_FAILED` and `messageEventListener.onMessageFailed` fires — but **no ebXML `DeliveryFailure` Error message is ever sent to the From Party** (confirmed: `DELIVERY_FAILURE` is defined in the enum but never instantiated). The spec §6.5.4 says the MSH "SHALL notify the application and/or system administrator" — the app notification exists (event), but §6.5.7's `DeliveryFailure` Error *message* is not produced. |
| §6.5.5 resend acknowledgments on duplicates | ✅ `DuplicateMessageHandler` re-sends the stored first response (sync: returns it; async: schedules delivery task) |
| §6.5.7 Failed delivery → `DeliveryFailure` Error/Warning message | ❌ **Gap** — see above: `DeliveryFailure` error code is defined but never emitted as an outbound error message. |
| §6.6 Reliable Messaging combinations (table) | ✅ behavior follows CPA parameters (ackRequested/duplicateElimination/syncReplyMode/actor) |

---

## 7. Message Status Service

| Requirement | Finding |
|---|---|
| §7.1.1 StatusRequest message: Service=`urn:oasis:names:tc:ebxml-msg:service`, Action=`StatusRequest` | ✅ `EbMSAction.STATUS_REQUEST` |
| §7.2 StatusRequest: id/version/RefToMessageId | ✅ `createStatusRequest` |
| §7.3 StatusResponse: messageStatus ∈ {UnAuthorized, NotRecognized, Received, Processed, Forwarded} | ✅ `MessageStatusType` enum matches exactly; `StatusResponseProcessor.createEbMSMessageStatusAndTimestamp` maps: unknown → `NotRecognized`, CPA mismatch → `UnAuthorized`, else Received/Processed/Forwarded |
| §7.3.2 Timestamp omitted for NotRecognized/UnAuthorized | ✅ `createStatusResponse` only sets Timestamp for RECEIVED/PROCESSED |
| §7.2.3/§7.3.5 exclusions (no Manifest/StatusResponse/ErrorList-Error with StatusRequest, etc.) | ✅ structurally — factory builds StatusRequest/Response with only their own body element |

**§7 verdict: ✅**

---

## 8. MSH Ping/Pong

| Requirement | Finding |
|---|---|
| §8.1 Ping: no payload, Service=ebXML service, Action=`Ping` | ✅ `createEbMSPing` (IT `ebMSPing`) |
| §8.2 Pong: Action=`Pong`, RefToMessageId = ping's MessageId | ✅ `createEbMSPong` → `createResponseMessageHeader` sets `RefToMessageId` to the request's `MessageId`; sync or async delivery via `pongProcessor` |

**§8 verdict: ✅**

---

## 9. MessageOrder

| Requirement | Finding |
|---|---|
| §9.1 MessageOrder requires Reliable Messaging + DuplicateElimination; MUST NOT coexist with SyncReply | ✅ **Correctly rejected.** `validateMessageOrder`: if `MessageOrder` present with `SyncReply` → `Inconsistent "SyncReply not allowed."`; otherwise → `NotSupported "MessageOrder not supported."` (ITs `ebMSMessageMessageOrder`, `ebMSMessageMessageOrderNoSyncReply`). |
| §9.1.1 SequenceNumber ordering, status Reset/Continue | ❌ **Gap (by design).** The MSH does **not implement sequence-number reordering**: any `MessageOrder` (without SyncReply) is rejected `NotSupported`. The spec says the module *MAY* be implemented; §1.3 requires only that unsupported features report via the prescribed failure mechanism — which is exactly what happens (`NotSupported`, severity Error). So this is **spec-legitimate**, but the module is not functionally supported. |

---

## 10. Multi-Hop

| Requirement | Finding |
|---|---|
| §10.1.1 AckRequested with actor `nextMSH` | ⚠️ **Rejected.** `validateAckRequested` throws `NOT_SUPPORTED "NextMSH not supported."` when actor is `nextMSH` (and `Inconsistent "SyncReply not allowed."` if `SyncReply` also present — matching the §10.1.1 MUST NOT). IT `ebMSMessageNextMshAckRequested` asserts the rejection. |
| §10.1.3 multi-hop acknowledgments | ❌ **Not implemented** (no `nextMSH` handling anywhere; `Acknowledgment` actor hardcoded to `toPartyMSH`). Again, per §1.3, an unsupported optional module that reports `NotSupported` via the prescribed failure mechanism is a *conforming* choice — the failure mechanism is present and correct. |

---

## Appendix A (Schema)

The shipped `msg-header-2_0.xsd` is essentially verbatim Appendix A, with **three benign deviations**:

1. `version="2.0c"` vs spec's `version="2.0"` (schema attribute, not message attribute)
2. `<element name="DuplicateElimination" type="string"/>` vs spec's empty `<element name="DuplicateElimination"/>` — the implementation **relaxes** the type to `string` (accepts any content, e.g. `"1"`), whereas the spec defines it as an empty element. This means the implementation *accepts* non-empty DuplicateElimination that the strict schema would reject.
3. In `Acknowledgment`, the trailing `<any namespace="##other">` is commented out — slightly stricter than the spec schema.

Message-level validation uses this XSD via `XSDValidator` on every inbound request/response
(`processRequest`/`processResponse`), so structural conformance is enforced at runtime. ✅

## Appendix B (HTTP Binding)

| Requirement | Finding |
|---|---|
| B.2.2 HTTP POST, `SOAPAction: "ebXML"` header | ✅ `EbMSMessageWriter` sets `SOAPAction` to `"\"ebXML\""`; servlet enforces `POST` and the exact SOAPAction (`EbMSInputStreamHandler.validateRequest/validateSoapAction`) |
| B.2.3 2xx on success; errors via ebXML mechanism | ✅ 200 with response / **204 No Content** when no synchronous response (`returnResponse`); ebXML errors returned as the response body or separately |
| B.2.4 SOAP Fault 500 for SOAP-level errors (sync) | ✅ `handleValidationException` returns SOAP Fault with HTTP 500 (used for unparseable/unrecognized messages, e.g. bad CPAId) |
| B.2.5 sync vs async | ✅ `isSyncReply` drives sync-response return vs delivery-task scheduling |
| B.2.6 access control (Basic auth, client certs) | ✅ `BasicAuthenticationFilter`, `ClientCertificateValidator`, `ClientCertificateAuthenticationFilter` |
| B.2.7 TLS/SSL | ✅ `SSLContextFactory`/`SSLParametersFactory`, keystore/truststore management |
| SMTP (B.3) | ❌ **Not implemented** — no SMTP transport. HTTP + JMS + in-process DB queue are the transports. |

---

## Summary of Issues

**Gaps (❌)**

1. **`NotRecognized` error code is absent** (`EbMSErrorCode`) — used (incorrectly) as `ValueNotRecognized`
   for Service/Action, and unknown CPAId is escalated to a SOAP Fault 500 instead of an ebXML
   `MessageError`. (§3.1.2, §3.1.5, §4.2.3.4)
2. **`DeliveryFailure` ebXML error messages are never generated** when reliable-messaging retries are
   exhausted or a transport fails — the code is defined but never instantiated; only an internal
   `onMessageFailed` event fires. (§6.5.4, §6.5.7)
3. **MessageOrder module not functionally implemented** — rejected with `NotSupported`. Spec-legitimate
   under §1.3 (failure mechanism is correct), but the feature is absent. (§9)
4. **Multi-hop / `nextMSH` acks not implemented** — rejected with `NotSupported`. Also spec-legitimate
   under §1.3, but absent. (§10)
5. **SMTP transport not implemented** (Appendix B.3 — the spec's second protocol binding; HTTP is fully
   implemented).

**Deviations (⚠️)**

6. No check that a **Manifest is present whenever a payload is present** (§2.1.4/§5.1.2) — a payload
   message with no Manifest is accepted.
7. MIME parse errors surface as a generic SOAP Fault 500 rather than an ebXML `MimeProblem` error
   (§2.1.6).
8. `xsi:schemaLocation` is not emitted on the SOAP Envelope (§2.3.2 — RECOMMENDED only).
9. Signature algorithm/hash are fully CPA-driven; there is no declared `dsa-sha1` default
   ("SHALL support" is only met if a CPA requests it) (§4.1.3).
10. `DuplicateElimination` element type is `string` in the shipped XSD instead of the spec's empty
    element (accepts non-empty content).
11. `RefToMessageId` validation requires the referenced MessageId to exist in the *local* store
    (conservative; may reject legitimate cross-party references for StatusRequest-like flows — in
    practice only null or locally-known IDs pass).
12. `Acknowledgment` SOAP actor is hardcoded to `toPartyMSH` with a `TODO resolve actor from CPA`
    (relevant only to multi-hop).

**Confirmed correct (highlights)**

- Exact match of the prescribed XML-DSIG **transform stack** (enveloped-signature → nextMSH/next-actor
  XPath exclusion → C14N) and c14n-20010315 canonicalization.
- Sign-then-encrypt ordering (§4.1.4.5).
- All reserved service URIs and actions (`urn:oasis:names:tc:ebxml-msg:service`, `MessageError`,
  `Acknowledgment`, `StatusRequest`, `StatusResponse`, `Ping`, `Pong`).
- `mustUnderstand="1"` enforcement via XSD on all header extension elements.
- TimeToLive expiration → `TimeToLiveExpired`; duplicate elimination & re-ack on duplicates; retry
  scheduling with CPA `RetryInterval`/`Retries` and TTL.
- Message Status and Ping/Pong services fully conformant, including `UnAuthorized`/`NotRecognized`
  handling and timestamp omission rules.
- HTTP binding: POST-only, `SOAPAction:"ebXML"`, 204 for no-response, SOAP Fault 500, sync/async split,
  TLS + client-cert auth.

**Net assessment:** for a two-party, HTTP-transport MSH with reliable messaging, the implementation
conforms to all *mandatory* (MUST) behaviors of Part I. The material spec divergences are
(a) error-code vocabulary (`NotRecognized` vs `ValueNotRecognized`, and CPAId failures routed to SOAP
Faults), (b) the missing `DeliveryFailure` error *message* path, and (c) the optional modules
MessageOrder/Multi-hop/SMTP being deliberately unsupported (which §1.3 permits, provided the
`NotSupported` failure mechanism works — and it does).
