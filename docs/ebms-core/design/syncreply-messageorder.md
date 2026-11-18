# SyncReply & MessageOrder — Spec Alignment and Design

Design note covering the EbMS 2.0 `SyncReply` element, CPA `syncReplyMode`
handling, the `MessageOrder` module, and the `AckRequested` NextMSH
interaction, as implemented in `ebms-core`.

Spec reference: `documentation/static/assets/ebMS_v2_0.md` (EbMS 2.0).

## 1. What the spec mandates

### 1.1 `SyncReply` element (§4.3, §4.3.1)

- `SyncReply` is a SOAP header child. When present it indicates the
  connection over which the message was received **SHOULD be kept open**
  in expectation of a response returned over the same connection.
- It MUST NOT be used to override `syncReplyMode` in the CPA.
- If `syncReplyMode` is **`none`** and a `SyncReply` element is present,
  the *Receiving* MSH should issue an error with `errorCode`
  **`Inconsistent`**, `severity` **`Error`** (§4.1.5).

### 1.2 `syncReplyMode` (§6.4.7)

- Used only when the data communications protocol is synchronous (e.g.
  HTTP); otherwise the value is **ignored**.
- Absent ⇒ semantically **`none`**.
- If not `none`, a `SyncReply` element **MUST be present** and the MSH
  must return any response from the application/business process in the
  payload of the *synchronous* reply message, as specified in the CPA.
- Valid values: `mshSignalsOnly`, `signalsOnly`, `signalsAndRespose`
  (sic — spec typo), `responseOnly`, `none`.

### 1.3 `MessageOrder` module (§9, §9.1)

- Allows messages to be presented to the *To Party* in a particular order
  via the `MessageOrder` element (containing a `SequenceNumber`).
- **Reliable Messaging MUST be used** when `MessageOrder` is present,
  and only with a **Once-And-Only-Once** scheme (§6.6).
- When `MessageOrder` is present: `DuplicateElimination` **MUST** be
  present and `SyncReply` **MUST NOT** be present.
- A receiving MSH MUST NOT pass a message with a `SequenceNumber` to the
  application until all messages with a lower `SequenceNumber` have been
  passed. When an implementation-defined limit for saved out-of-sequence
  messages is reached, the MSH MUST signal a delivery failure
  (`errorCode` **`DeliveryFailure`**).

### 1.4 `AckRequested` NextMSH actor (§2.3.10, §10.1.1)

- In the multi-hop scenario, `AckRequested` MAY be targeted at the
  *Next MSH* (`urn:oasis:names:tc:ebxml-msg:actor:nextMSH`); a
  `NextMSH` acknowledgment is sent by itself and not bundled with other
  headers.
- In the single-hop (non multi-hop) case, an `AckRequested` targeted at
  the NextMSH actor is not a supported request for this MSH.

## 2. Current implementation (Option B — implemented)

### 2.1 Files

- `core/src/main/java/nl/clockwork/ebms/validation/EbMSMessageValidator.java`
  - `isSyncReply(...)` — resolves the *effective* sync-reply mode by
    looking up the bound delivery channel's `syncReplyMode`
    (`CPAManager.getSendSyncReply`) and returning
    `syncReply != null && !syncReply.equals(SyncReplyModeType.NONE)`.
  - Removed the dead commented-out `return message.getSyncReply() != null;`
    line.
- `core/src/main/java/nl/clockwork/ebms/validation/MessageHeaderValidator.java`
  - `validateSyncReply(...)` — enforces §4.3.1: `SyncReply` present with
    `syncReplyMode=none` ⇒ `Inconsistent` at `//Header/SyncReply/@actor`
    ("Wrong value.").
  - `validateAckRequested(deliveryChannel, ackRequested, syncReply)` —
    enforces the NextMSH interaction: `AckRequested` actor NextMSH ⇒
    `Inconsistent` ("SyncReply not allowed.") at
    `//Header/AckRequested/@actor` when `SyncReply` is present;
    `NotSupported` ("NextMSH not supported.") otherwise.
  - `validateMessageOrder(messageOrder, syncReply)` — enforces §9.1/§9.2:
    `MessageOrder` present **with** `SyncReply` ⇒ `Inconsistent`
    ("SyncReply not allowed.") at `//Header/MessageOrder`; `MessageOrder`
    present **without** `SyncReply` ⇒ `NotSupported`
    ("MessageOrder not supported.") at `//Header/MessageOrder`.

### 2.2 Supported `syncReplyMode` values

All CPA `syncReplyMode` values are parsed, stored, and enforced at
validation time. Wire behaviour:

| `syncReplyMode` | Behaviour |
|---|---|
| `none` | async; `SyncReply` element rejected with `Inconsistent`. |
| `mshSignalsOnly` / `signalsOnly` | MSH signals (Acknowledgment / MessageError) returned inline in the synchronous reply. |
| `signalsAndResponse` | MSH signals returned inline; the *application response payload* is **not** returned in the synchronous reply (see §3.1 Option A). |
| `responseOnly` | MSH signals returned inline today; application response payload not returned (see §3.1 Option A). |

All *test* CPAs in `core/src/test/resources/nl/clockwork/ebms/cpas/` use
`syncReplyMode="none"` (async) or `syncReplyMode="signalsAndResponse"`
(sync). Both are valid production values whose production behaviour is
fully implemented; keeping all test CPAs on these values means Option A
requires **no CPA schema change**.

### 2.3 Test coverage

- `EbMSMessageServiceImplIT#ebMSMessage` — canonical synchronous
  `EbMSMessage` with `SyncReply` + `AckRequested` (To Party MSH actor) on
  `cpaStubEBF.rm.http.unsigned.sync`; expects the inline
  `Acknowledgment` with `From.Role = OVERHEID`.
- `EbMSMessageServiceImplIT#ebMSMessageNextMshAckRequested` —
  `SyncReply` + `AckRequested` actor NextMSH ⇒ `MessageError` with
  `Inconsistent` at `//Header/AckRequested/@actor` ("SyncReply not
  allowed.").
- `EbMSMessageServiceImplIT#ebMSMessageMessageOrder` — `SyncReply` +
  `MessageOrder` ⇒ `MessageError` with `Inconsistent` at
  `//Header/MessageOrder` ("SyncReply not allowed.").
- `EbMSMessageServiceImplIT#ebMSMessageMessageOrderNoSyncReply` (Order 46)
  — `MessageOrder` without `SyncReply` on the async CPA
  (`cpaStubEBF.rm.http.unsigned`, `syncReplyMode=none`) ⇒ HTTP `204`
  (no body); the rejected message is stored with status `FAILED`
  (verified via `getMessage`). The async CPA is registered *inside* this
  test, not in `insertCPA`, to avoid corrupting binding resolution for
  the other ITs.
- New test template:
  `core/src/test/resources/nl/clockwork/ebms/soap/EbMSMessageMessageOrderNoSyncReply.xml`;
  new `WithTemplate` helper `ebMSMessageMessageOrderNoSyncReply`.

## 3. Option A — Full plan (to be implemented later)

Option A closes the remaining spec gap: the *application response* half
of `syncReplyMode` (§6.4.7) and the `MessageOrder` module (§9). Option B
(§2) is the spec-aligned baseline and leaves these as documented,
intentional limitations.

### 3.1 `syncReplyMode` application-response support

Spec (§6.4.7): when `syncReplyMode` is not `none`, the MSH must return
any response from the application/business process **in the payload of
the synchronous reply message**, as specified in the CPA.

Today the MSH returns only MSH signals (Acknowledgment / MessageError)
in the synchronous reply for the non-`none` modes; it does not invoke
the application and return its response in the same reply.

Work items (in order):

1. **Configuration** — extend `EbMSMessageProperties`
   (`ebms-server.properties` keys in `core/src/main/resources`):
   - `syncReply.applicationResponseTimeout` — bounded wait for the
     application response before the MSH signals a timeout.
   - `syncReply.applicationResponse.maxMessages` (optional) — guard
     against unbounded synchronous reply growth.
2. **Message processor** — extend
   `EbMSMessageProcessor#processMessage` to detect
   `syncReplyMode ∈ {signalsAndResponse, responseOnly, signalsOnly}` and
   `ackRequested != null`; when true:
   - invoke the application handler (extend `MessageEventListener` / the
     `nl.clockwork.ebms.application` layer) to produce an application
     response;
   - assemble a *new* `EbMSMessage` whose `Service`/`Action` match the
     CPA's *response* binding, whose body carries the application
     response, and whose `MessageData.RefToMessageId` is the inbound
     `MessageId`;
   - append the MSH signals (Acknowledgment / MessageError) as headers;
   - return the combined document on the same HTTP connection;
   - on application timeout or application error, fall back to the
     current signal-only reply.
3. **Validator** — `MessageHeaderValidator.validateSyncReply` should also
   reject `responseOnly`/`signalsAndResponse` when no response binding is
   configured (fail fast rather than silently dropping the response).
4. **Tests** — new ITs in `EbMSMessageServiceImplIT` (or a dedicated
   class): a mock application handler returning a canned response;
   synchronous `EbMSMessage` on `syncReplyMode="signalsAndResponse"` and
   `syncReplyMode="responseOnly"` CPAs asserting the response body in the
   synchronous reply; the timeout path degrading to signal-only.
   Use a **new** CPA
   (`cpaStubEBF.rm.http.unsigned.applicationResponse.xml`) with a distinct
   `cpaId` so existing tests are unaffected. Do not modify existing CPAs.

### 3.2 `MessageOrder` module support

Spec (§9, §9.1): `MessageOrder` requires Reliable Messaging
(Once-And-Only-Once), `DuplicateElimination`, no `SyncReply`,
sequence-number-based delivery ordering, and `DeliveryFailure` when an
out-of-sequence buffer limit is reached.

Today `MessageOrder` is always rejected: `Inconsistent` when `SyncReply`
is present, `NotSupported` otherwise. Option A adds real support.

Work items (in order):

1. **Configuration** — `EbMSMessageProperties`:
   - `messageOrder.maxOutOfSequenceBuffer` (default e.g. 100) — the
     implementation-defined limit from §9.1.1.
   - `messageOrder.retentionMillis` (optional) — how long to keep
     out-of-sequence messages.
2. **CPA** — no schema change required. `MessageOrder` is a header
   element, not a CPA attribute. All existing test CPAs keep working; a
   dedicated `cpaStubEBF.rm.http.unsigned.ordered.xml` (same
   party/service/action, `syncReplyMode="none"`) keeps the new tests
   isolated.
3. **Model** — `EbMSMessage` already exposes
   `getMessageOrder(): MessageOrder`. Add a `MessageOrderContext` value
   object holding the `SequenceNumber`, its `status`
   (`Reset`/`Continue`), and per-conversation state (next expected
   sequence number, out-of-sequence buffer).
4. **DAO** — `EbMSDAO` needs:
   - `getConversationSequence(cpaId, conversationId)` →
     `Optional<Integer>`;
   - `storeOutOfSequenceMessage(messageId, sequenceNumber)`;
   - `releaseMessagesUpTo(sequenceNumber)` → `List<MessageId>`;
   - `removeConversationState(cpaId, conversationId)` (TTL expiry /
     explicit reset).
   These are new SQL operations: because persistence is a plugin pattern
   with per-driver SQL dialects, every plugin
   (`h2`, `hsqldb`, `postgres`, `mariadb`, `oracle`, `db2`, `mssql`)
   needs the implementation — the largest blast radius of Option A.
5. **Processor** — add a `MessageOrderProcessor` (sibling of
   `AcknowledgmentProcessor`) invoked from
   `EbMSMessageProcessor#processMessage` *before* the `ackRequested`
   branch when `messageOrder != null`:
   - validate `DuplicateElimination` present (spec-required);
   - validate `SyncReply` absent (already enforced by
     `MessageHeaderValidator.validateMessageOrder`);
   - look up / initialise the conversation sequence state;
   - `SequenceNumber == expected`: deliver to the application, advance
     the expected number, release any buffered out-of-sequence messages
     in order;
   - `SequenceNumber > expected`, buffer not full: store as
     out-of-sequence, return the MSH signal only;
   - `SequenceNumber > expected`, buffer full: `DeliveryFailure`
     (`errorCode=DeliveryFailure`, `severity=Error`);
   - `SequenceNumber < expected` without `status=Reset`: `Inconsistent`
     ("SequenceNumber not increasing.").
6. **Tests** — ITs in `EbMSMessageServiceImplIT` or a new
   `EbMSMessageOrderServiceImplIT`:
   - in-order (0,1,2) on the async CPA: each accepted (204), stored
     `RECEIVED`;
   - out-of-order (1 before 0): 1 buffered; 0 delivers 1;
   - out-of-order beyond buffer limit: `DeliveryFailure` in the async
     `MessageError`;
   - `MessageOrder` + `SyncReply`: still `Inconsistent` (existing test);
   - `MessageOrder` without `DuplicateElimination`: `Inconsistent`;
   - `status=Reset` with `SequenceNumber=0` starts a fresh conversation.
7. **Reliable Messaging gate** — `MessageOrder` requires
   Once-And-Only-Once (§6.6). Gate acceptance on the delivery
   channel's reliable-messaging scheme (reuse existing RM machinery)
   rather than accepting it on store-and-forward channels; document
   the gate.

### 3.3 Consequences of Option A

- **New configuration surface**: `syncReply.applicationResponseTimeout`
  and `messageOrder.maxOutOfSequenceBuffer` (plus the optional keys).
  Both need safe defaults so existing deployments are unaffected.
- **New IT CPAs**: `cpaStubEBF.rm.http.unsigned.applicationResponse.xml`
  and `cpaStubEBF.rm.http.unsigned.ordered.xml`. Existing test CPAs stay
  byte-for-byte identical and remain production-supported.
- **DAO/plugin blast radius**: new `EbMSDAO` methods must be implemented
  in every `plugin/db/*` SQL dialect.
- **Application integration point**: a new SPI (or extension of
  `MessageEventListener`) so deployments can plug in an application
  response handler without forking the MSH.
- **Backwards compatibility**: additive. Existing behaviour for
  `syncReplyMode=none` and for `MessageOrder` (rejected) is preserved
  when the new configuration is not set.
- **Documentation**: update `documentation/docs/ebms-core/properties.md`
  with the new keys and this design note with the `MessageOrder`
  behaviour; no REST/OpenAPI change required (the REST surface does not
  expose `messageOrder`/`syncReply` today).
- **Estimate**: multi-week effort spanning `ebms-core` (model,
  validator, processor, DAO), every `plugin/db/*` (SQL dialects), and —
  only if the REST surface is extended — `ebms-admin`. Option B is the
  safe spec-aligned baseline; Option A is the full closure.

### 3.4 Sequencing

1. Land Option B (done) and stabilise the test suite.
2. Add the new configuration properties with safe defaults.
3. Implement the `MessageOrder` DAO + processor and its tests, per-driver
   SQL.
4. Implement the application-response path for `syncReplyMode` and its
   tests.
5. Update documentation (`properties.md`, this design note).

## 4. Related pitfalls (also recorded in `AGENTS.md`)

- Java braceless-`if` scoping: a `throw` indented under an inner `if`
  but outside its braces is **not** nested. The original
  `validateMessageOrder` wrote `throw NOT_SUPPORTED` indented inside the
  outer `if` but outside the inner `if`; `javap` confirmed the throw was
  unconditional (and silently rejected *every* message). Always brace
  multi-statement `if` bodies in this codebase.
- Test isolation: an async CPA sharing channel IDs with the sync test CPA
  corrupts binding resolution for all subsequent ITs. Register test-only
  CPAs inside the test method that needs them, not in `insertCPA`.
