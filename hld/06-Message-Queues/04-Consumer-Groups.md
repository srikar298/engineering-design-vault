# ⚡ 04 - Consumer Groups

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C095 |
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
*   **Two-Sentence Trigger:** A Consumer Group is a logical grouping of consumer instances that collectively share the work of consuming from a Kafka topic — each partition is assigned to exactly one consumer within the group, parallelizing processing while preserving per-partition ordering. Multiple different Consumer Groups on the same topic each receive an independent copy of every message, enabling the Pub/Sub fan-out pattern.
*   **Scalability Dimension:** Primary: **Horizontal Consumer Scalability** & **Parallel Processing with Ordering Guarantees**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### How Kafka Partition Assignment Works

```
Topic: "orders" — 4 Partitions (P0, P1, P2, P3)

Consumer Group "email-service" (3 consumers):
  Consumer-1: P0, P1       ← 2 partitions (extra partition assigned)
  Consumer-2: P2
  Consumer-3: P3

Consumer Group "fraud-service" (2 consumers):
  Consumer-A: P0, P1
  Consumer-B: P2, P3       ← Each group gets its own independent copy
```

### The Golden Rules of Kafka Consumer Groups
| Rule | Explanation |
| :--- | :--- |
| **1 Partition → 1 Consumer** | Within a group, each partition is owned by exactly one consumer. No two consumers in the same group read the same partition simultaneously. |
| **Max Parallelism = Partition Count** | You can have at most as many *active* consumers as partitions. Extra consumers are idle (hot standby). |
| **Ordering within Partition** | Messages within a single partition are processed in order. Cross-partition ordering is NOT guaranteed. |
| **Different Groups = Independent** | Adding a new Consumer Group does not affect existing groups. The same message is independently consumed by each group from their own offset. |

### Offset Management
*   **Offset:** An integer representing the position of a message in a partition. The consumer tracks which offset it has processed up to.
*   **Committing Offsets:** After processing a batch, the consumer commits its current offset to Kafka (or an external store like ZooKeeper/Kafka's internal `__consumer_offsets` topic).
*   **Crash Recovery:** On restart, the consumer reads its last committed offset and resumes from there → At-Least-Once delivery.

### Rebalancing (The Operations Trap)
When a consumer joins or leaves a group, Kafka triggers a **Rebalance** — reassigning partitions across the remaining consumers. During rebalancing, **all consumers in the group pause processing** (stop-the-world). Minimize rebalances by tuning `session.timeout.ms` and `max.poll.interval.ms`.

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Consumer Lag per Partition (Group + Partition + Offset)`: The most critical Kafka operational metric. Growing lag = consumers falling behind.
    *   `Rebalance Frequency`: Frequent rebalances indicate consumer crashes or slow poll intervals.
*   **Blast Radius (The "Impact"):**
    *   During a rebalance, that consumer group pauses entirely. For a high-traffic topic, even 5s of paused processing can cause visible lag spikes.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Adding more consumers than partitions to scale up (the extra consumers are idle — you must **increase partition count** to scale beyond the current partition ceiling).
*   Committing offsets **before** processing (if the consumer crashes after commit but before processing → data loss / At-Most-Once).

### Interview Tip (The "Strong Hire" Signal)
> *"We partition our `orders` topic by `customer_id % N`. This ensures all orders for the same customer go to the same partition, preserving per-customer ordering. Our Email Consumer Group has 6 consumers on 6 partitions — that's our maximum parallelism. When we need to scale higher, we increase partitions (careful — this is a one-way operation in Kafka) and add consumer instances."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*
