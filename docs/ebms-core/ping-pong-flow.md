---
sidebar_position: 9
---

# Ping / Pong flow: 2.20.x vs 2.21.x

This page traces how an EbMS **Ping** and its **Pong** are handled, and how that changes
between **2.20.x** and **2.21.x** in:

- **Basic mode** — no JMS, no Kafka (`deliveryManager.type=DEFAULT`,
  `deliveryTaskHandler.type=DEFAULT`).
- **Advanced scaling** — with JMS or Kafka (2.21 splits JMS/Kafka into separate
  `plugin/messaging/*` modules).

See [Deployment Scenarios](/ebms-admin/deployment) for the topology diagrams.

> The diagrams below are schematic. "HTTP" = the EbMS-over-HTTP(S) call between adapters;
> "queue/topic" = the broker transport used only for **async** correlation and for
> **reliable-message** delivery (a sync Ping never touches a queue).

---

## 1. The Ping → Pong protocol (mode-independent, 2.20 and 2.21)

The actual Ping/Pong processing is identical in every mode of both versions. Only the
**response-correlation store** and the **reliable-delivery dispatcher** differ.

```
  client app A                       EbMS adapter A                    EbMS adapter B
       |  SOAP/REST ping(cpa,from,to) |                                       |
       |------------------------------->|  createEbMSPing (SyncReply from CPA) |
       |                                |  deliveryManager.sendMessage(Ping)  |
       |                                |-------- HTTP Ping --------------->  |
       |                                |                                       |  processPing():
       |                                |                                       |    validate, createPong
       |                                |                                       |    isSyncReply(ping)?
       |                                |                                       |      yes -> Pong as HTTP 200 body
       |                                |                                       |      no  -> sendPong() async
       |                                |<------- HTTP 200 (Pong) ------------|   HTTP 204 + async POST (Pong)
       |                                |  assert action == PONG               |
       |<------------------- ok / error |                                       |
```

- **Sync Ping** (CPA `SyncReply` set): the Pong is returned in the **same HTTP response**.
  No queue, no DB, no correlation store is involved — this path is byte-for-byte the same
  in 2.20 and 2.21.
- **Async Ping**: adapter B answers later on a separate connection. The *receiving* node
  must hand the Pong back to the node that sent the Ping — this is where the mode
  (in-memory vs JMS vs Kafka) matters, and it is where 2.20 and 2.21 differ.

---

## 2. Basic mode — no JMS, no Kafka

### 2.1 2.20.x

Async-Pong correlation is **in-JVM**: `DefaultDeliveryManager` + `EbMSMessageQueue`
(a bounded LRU `LinkedHashMap`, `messageQueue.maxEntries`/`timeout`, default 64 / 30 s,
blocking `Thread.sleep`-based wait). Reliable-message delivery polls the **`delivery_task`
DB table** via a per-node job.

```
  adapter A (sender JVM)                 adapter B (receiver JVM)
        |  sendMessage(Ping, no SyncReply)                                 |
        |  messageQueue.register(pingId)   [in-memory LRU, this JVM only]   |
        |-------- HTTP Ping ------------------------------------------->   |
        |  (thread sleeps up to messageQueue.timeout)                      |  processPing -> sendPong async
        |<----------------- HTTP 204 / async POST (Pong) ----------------|  processPong
        |                                                                     deliveryManager.handleResponseMessage(pong)
        |  messageQueue.put(pingId, pong)  <- wakes sleeping thread          |
        |  return pong (or timeout -> "No response received")               |
```

Reliable delivery (same infrastructure, used for normal messages, not Ping):

```
  send thread:  insert into delivery_task (DB)
  poll job:     SELECT ... FROM delivery_task WHERE time_stamp <= now
                (every node polls; last-writer / no single-writer gate)
                -> HTTP send -> update/delete delivery_task
```

**Constraints:** async Pong must return to the *same JVM* that sent the Ping, so a sync-less
Ping behind a load balancer with multiple nodes loses the correlation. The poll job had no
cross-node single-writer guarantee.

### 2.2 2.21.x

Async-Pong correlation is **unchanged** (still in-JVM `EbMSMessageQueue`). What changed is
**reliable delivery**: JGroups/Raft is gone and the single-writer poll is now a **ShedLock
JDBC gate**.

```
  adapter A (sender JVM)                 adapter B (receiver JVM)
        |  sendMessage(Ping, no SyncReply)                                 |
        |  messageQueue.register(pingId)   [in-memory, same as 2.20]       |
        |-------- HTTP Ping ------------------------------------------->   |   (unchanged)
        |<----------------- HTTP 204 / async POST (Pong) ----------------|   (unchanged)
        |  messageQueue.put(pingId, pong) -> return pong                   |
        |                                                                   
  [NEW] Reliable delivery — ShedLock-gated poll (works across a shared DB):
  every node:  try { ShedLock.lock("ebms-delivery-task-executor", lease=lockAtMostFor) }
               on success (the single writer):
                 SELECT ... FROM delivery_task WHERE time_stamp<=now [AND server_id=?]
                 FOR each task -> DirectDeliveryTaskDispatcher -> HTTP send -> update/delete
                 renew lease (SimpleLock.extend) before each awaited task
                 unlock
               on lock held -> sleep, retry
```

**What changed 2.20 → 2.21 (basic):**

| | 2.20.x | 2.21.x |
|---|---|---|
| Async Pong correlation | in-JVM `EbMSMessageQueue` | **unchanged** (in-JVM `EbMSMessageQueue`) |
| Reliable-delivery poll | per-node DB poll, no gate | **ShedLock-gated** DB poll (single writer per cycle) |
| Cross-node single writer | Raft/JGroups (removed in 2.21) | **ShedLock JDBC lease** |
| `QUARTZ` / `QUARTZ_JMS` | available | **removed** (`V2.21.0__Drop_Quartz_Tables`) |

> **Note:** the in-JVM async-Pong limitation remains in basic mode in both versions — a
> sync-less Ping is only correctly correlated when it returns to the sending JVM. Multi-node
> basic deployments rely on the shared DB + ShedLock for *reliable delivery*, and should use
> sync Pings (or a broker) for cross-node async correlation.

---

## 3. Advanced scaling — with JMS

JMS is a module in 2.21 (`plugin/messaging/jms`); in 2.20 it lived in core. Two independent
concerns, cleanly separated by the 2.21 SPI.

### 3.1 2.20.x

```
  async Pong correlation (queue "MESSAGE"):
    B:  handleResponseMessage -> JMS send "MESSAGE" (JMSCorrelationID = pingId, TTL 1min)
    A:  receiveSelectedAndConvert("MESSAGE", "JMSCorrelationID='<pingId>'")   [any node]
  reliable delivery (queue "DELIVERY_TASK"):
    A:  JMSDeliveryTaskManager.insertTask -> JMS send "DELIVERY_TASK"
    any node: JMSDeliveryTaskListener -> DeliveryTaskHandler -> HTTP send
             (retry = re-publish "DELIVERY_TASK" with ActiveMQ scheduled delay)
```

### 3.2 2.21.x

The response-correlation path is the same idea (queue `MESSAGE`, correlation-ID receive,
now a **local `JmsTransactionManager`** instead of Atomikos XA). Reliable delivery is
expressed through the new **dispatcher SPI**:

```
  async Pong correlation (queue "MESSAGE") — same, plus messageId sanitizing:
    B:  handleResponseMessage -> JMS send "MESSAGE" (JMSCorrelationID = refToMessageId)
    A:  receiveSelectedAndConvert("MESSAGE", "JMSCorrelationID='<pingId>'")
        createCorrelationIdSelector(): rejects CR/LF, escapes quotes   [new hardening]

  reliable delivery (queue "DELIVERY_TASK") — now push, via the dispatcher:
    A:  DAODeliveryTaskManager still writes delivery_task rows (bookkeeping)
        JMSDeliveryTaskDispatcher.dispatch(task) -> JMS send "DELIVERY_TASK"
            (ObjectMessage, JMSCorrelationID = messageId)
    any node: DefaultMessageListenerContainer (concurrentConsumers->max)
              -> JMSDeliveryTaskListener -> DeliveryTaskHandler -> HTTP send
    NOTE: in JMS mode the core ShedLock DAO poller is NOT created
          (it is @Conditional(type=DEFAULT)); the queue is the work-distributor
```

**What changed 2.20 → 2.21 (JMS):**

| | 2.20.x | 2.21.x |
|---|---|---|
| Correlation store | queue `MESSAGE` | **same** (queue `MESSAGE`) |
| Tx across broker+DB | **Atomikos XA** | **local `JmsTransactionManager`** (no XA; at-least-once) |
| Reliable delivery | `JMSDeliveryTaskManager` (core) | `JMSDeliveryTaskDispatcher` (plugin, SPI) |
| Cross-node single writer | Raft (removed) | not needed — queue distributes; ShedLock only if `type=DEFAULT` |
| Upgrade note | — | docs tell former `JMS`+Atomikos users: accept at-least-once, or switch to `DEFAULT` |

> **Known gap:** the 2.21 changelog cites a *per-task delivery claim* backstop (a `V2.23.0`
> migration) that is **not present in code yet**. The exact documented-vs-actual diff and
> resolution options are tracked in `PLAN-delivery-claim-gap.md` in the ebms-core
> repository.

---

## 4. Advanced scaling — with Kafka (new in 2.21)

Kafka is a **new** plugin in 2.21 (`plugin/messaging/kafka`); there is no 2.20 equivalent.
`deliveryManager.type=KAFKA`, `deliveryTaskHandler.type=KAFKA`.

```
  async Pong correlation (topic "ebms-message-replies"):
    B:  handleResponseMessage -> kafka.send(repliesTopic, key=refToMessageId, value=Pong)
    A:  each instance subscribes to repliesTopic in a UNIQUE consumer group
        (groupIdPrefix + "-replies-" + UUID)  -> every instance gets every reply
        onMessage: pendingReplies.remove(refToMessageId).complete(pong)
        sendMessage: awaitReply(pending, replyTimeout=180s)   [in-app filter, no broker selector]

  reliable delivery (topic "ebms-delivery-task"):
    A:  DAODeliveryTaskManager writes delivery_task rows (bookkeeping)
        KafkaDeliveryTaskDispatcher.dispatch(task) -> kafka.send(topic, key=messageId, value=task)
              (idempotent producer: acks=all, enableIdempotence=true)
    any node: ConcurrentMessageListenerContainer (groupIdPrefix + "-delivery-task")
              -> KafkaDeliveryTaskListener -> DeliveryTaskHandler -> HTTP send
    NOTE: in KAFKA mode the core ShedLock DAO poller is NOT created
          (it is @Conditional(type=DEFAULT)); the topic is the work-distributor
```

**Kafka vs JMS correlation:** JMS selects the matching reply at the broker
(`JMSCorrelationID='…'`); Kafka has no broker-side selector, so the reply topic fans out to
**every** instance (unique consumer group) and each instance **filters in-app** by key and
completes its own `CompletableFuture`.

---

## 5. One-line summary per mode

| Mode | 2.20.x | 2.21.x |
|---|---|---|
| **Basic** (no broker) | in-JVM Pong queue; per-node DB poll (Raft single-writer) | in-JVM Pong queue (same); **ShedLock-gated** DB poll |
| **Advanced + JMS** | queue `MESSAGE`; `JMSDeliveryTaskManager`; **Atomikos XA** | queue `MESSAGE` (same); `JMSDeliveryTaskDispatcher`; **local JMS tx, at-least-once** |
| **Advanced + Kafka** | *—* (not available) | **new**: replies topic (fan-out + in-app filter); delivery topic; idempotent producer |

**Common to all:** a **sync Ping/Pong is a plain HTTP request/response** in every mode of
both versions; the broker / ShedLock machinery only matters for **async** Pong correlation
and for **reliable-message** delivery.
