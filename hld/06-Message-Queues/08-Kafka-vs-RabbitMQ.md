# ⚡ 08 - Kafka vs RabbitMQ

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C099 |
| **Category** | Messaging & Queues |
| **Difficulty** | 🟡 Medium |
| **Interview Frequency** | 🔥 High |
| **Understanding** | [🔴 None / 🟡 Conceptual / 🟢 Applied] |
| **Can Explain** | [ ] Yes / [ ] No |
| **Whiteboard Drawn** | [ ] Yes / [ ] No |
| **Taught Someone** | [ ] Yes / [ ] No |
| **Next Review** | YYYY-MM-DD |
| **Mastery** | [🔴 Familiar / 🟡 Competent / 🟢 Expert] |

---

## ⚡ 1. The Core Definition & Trigger
*   **Two-Sentence Trigger:** Kafka and RabbitMQ solve fundamentally different problems: Kafka is a **distributed persistent event log** optimized for high-throughput streaming and replay, while RabbitMQ is a **message broker** optimized for flexible routing, task queues, and low-latency point-to-point delivery. Choosing the wrong one for your use case either adds unnecessary operational complexity (Kafka for simple task queues) or loses critical capabilities (RabbitMQ for event sourcing).
*   **Scalability Dimension:** Primary: **Use-Case Fit over Technical Capability**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### The Architecture Philosophy
| Dimension | Kafka | RabbitMQ |
| :--- | :--- | :--- |
| **Mental Model** | "Dumb Broker, Smart Consumer" — Broker stores. Consumer tracks state (offsets). | "Smart Broker, Dumb Consumer" — Broker routes, tracks ACK, re-queues on failure. |
| **Storage Model** | Append-only log. Persistent for retention period. | Queue: Deleted after ACK. Optional persistence. |
| **Consumer Model** | Pull-based. Consumer polls at its own pace. | Push-based. Broker pushes to consumer. |
| **Ordering** | Guaranteed within a partition. | FIFO within a queue (single consumer). |
| **Throughput** | Millions of messages/second (1M+ msg/s). | Tens of thousands/second (20–50k msg/s). |
| **Latency** | Higher (batching, replication): 5–15ms typical. | Lower: sub-millisecond in low-contention. |
| **Message Replay** | ✅ Yes — any consumer can replay from any offset. | ❌ No — once ACKed, message is deleted. |
| **Routing Complexity** | Simple (topic + partition key). | Complex (Direct, Fanout, Topic, Headers exchanges). |
| **Ops Complexity** | High — ZooKeeper/KRaft, partition management. | Moderate — standard broker operations. |

### When to Use Which
| Use Case | Winner | Reason |
| :--- | :--- | :--- |
| Send password reset email | RabbitMQ / SQS | Task queue, simple point-to-point, low throughput. |
| Process 1M IoT sensor readings/s | Kafka | Massive throughput, persistent log, stream processing. |
| Fan-out order event to 10 services | Either | Kafka: replay + fan-out via consumer groups. RabbitMQ: Fanout Exchange. |
| Event Sourcing (reconstruct state) | Kafka | Replay is a core requirement → persistent log essential. |
| Priority queues (urgent tasks first) | RabbitMQ | Native priority queue support. Kafka has no priority concept. |
| Exactly-Once processing (payments) | Kafka | Transactional API + idempotent producers built-in. |

### The "Kafka is Overkill" Anti-Pattern
Kafka requires: ZooKeeper/KRaft cluster, partition planning, consumer group management, offset commit strategies, schema registry for Avro/Protobuf. For a simple async email job with 100 messages/minute, this is massive operational overhead with zero benefit.
> **Rule of Thumb:** Start with SQS/RabbitMQ. Graduate to Kafka when you need: replay, >100k msg/s throughput, multi-consumer fan-out with independent offsets, or stream processing.

---

## 💥 3. Resiliency & Operations
*   **Observability:**
    *   Kafka: Consumer lag, URP count, ISR shrink.
    *   RabbitMQ: Queue depth, consumer count, message rates (publish/deliver/ack).
*   **Numbers to Know:**
    *   RabbitMQ: ~50k messages/s on standard hardware.
    *   Kafka: 1M+ messages/s per broker; LinkedIn peak: 7 trillion messages/day.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Defaulting to Kafka for every messaging use case (overkill for simple task queues).
*   Not knowing that RabbitMQ deletes messages after ACK (cannot replay history — use Kafka for event sourcing).
*   Not mentioning the **Transactional Outbox pattern** when discussing how to ensure events are published reliably.

### Interview Tip (The "Strong Hire" Signal)
> *"I'd use RabbitMQ for our async task queue (email sends, PDF generation) — it's simpler to operate and has native dead-letter and retry support. I'd use Kafka for our analytics pipeline and event sourcing — we need replay capability and 500k+ events/second throughput. The two coexist: Kafka for the event backbone, RabbitMQ for task delegation."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*
