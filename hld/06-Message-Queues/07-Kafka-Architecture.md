# ⚡ 07 - Kafka Architecture

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C098 |
| **Category** | Messaging & Queues |
| **Difficulty** | 🔥 Hard |
| **Interview Frequency** | 🔥 High |
| **Understanding** | [🔴 None / 🟡 Conceptual / 🟢 Applied] |
| **Can Explain** | [ ] Yes / [ ] No |
| **Whiteboard Drawn** | [ ] Yes / [ ] No |
| **Taught Someone** | [ ] Yes / [ ] No |
| **Next Review** | YYYY-MM-DD |
| **Mastery** | [🔴 Familiar / 🟡 Competent / 🟢 Expert] |

---

## ⚡ 1. The Core Definition & Trigger
*   **Two-Sentence Trigger:** Kafka is a distributed, append-only log system built for high-throughput, durable, and replayable event streaming — treating events as an immutable ordered log rather than a queue that deletes messages on consumption. Its architecture of **Topics → Partitions → Brokers → Consumer Groups** enables horizontal scalability to millions of events per second while providing configurable retention, replay, and exactly-once guarantees.
*   **Scalability Dimension:** Primary: **Massive Write Throughput** (MB/s to GB/s) & **Replay / Event Sourcing**. Secondary: **Multi-Consumer Fan-Out**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### Core Architecture Components
```
┌──────────────────────────────────────────────────────────────────┐
│  KAFKA CLUSTER                                                   │
│                                                                  │
│  Topic: "orders" (3 Partitions, Replication Factor = 3)         │
│                                                                  │
│  Partition 0: [msg0][msg1][msg2][msg3] → Broker 1 (Leader)      │
│                                        → Broker 2 (Follower)    │
│                                        → Broker 3 (Follower)    │
│                                                                  │
│  Partition 1: [msg4][msg5]             → Broker 2 (Leader)      │
│  Partition 2: [msg6][msg7][msg8]       → Broker 3 (Leader)      │
│                                                                  │
│  ZooKeeper / KRaft: Manages leader election, cluster metadata   │
└──────────────────────────────────────────────────────────────────┘
```

### Key Design Decisions & Why They Enable Scale
| Decision | Why It Enables Scale |
| :--- | :--- |
| **Append-Only Log** | Sequential writes are orders of magnitude faster than random writes. Disk seeks are eliminated. |
| **Zero-Copy Reads** | `sendfile()` syscall transfers data directly from page cache to network socket — no CPU copy. |
| **Pull-Based Consumers** | Consumers poll at their own pace. Broker never needs to track consumer state (except offsets). |
| **Partitioning** | Write throughput scales linearly with partition count (each partition is an independent log). |
| **Replication Factor** | `acks=all` requires all in-sync replicas (ISR) to confirm before ACKing to producer → durability. |

### Kafka Guarantees
*   **Durability:** Message is written to `replication.factor` brokers. If `acks=all`, producer waits for all ISR confirmation.
*   **Ordering:** Guaranteed **within a single partition** only. Cross-partition ordering is not guaranteed.
*   **Retention:** Messages are retained for a configured duration (`log.retention.hours=168` = 7 days default) regardless of consumption.

### Numbers to Know
| Metric | Value |
| :--- | :--- |
| **Typical throughput** | 100s of MB/s per broker |
| **LinkedIn scale** | 7 trillion messages/day at peak |
| **Default retention** | 7 days |
| **Default segment size** | 1 GB log segments |
| **Producer batch latency** | `linger.ms=5` (batch for 5ms to improve throughput) |

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Consumer Group Lag per Partition`: #1 operational metric.
    *   `Under-Replicated Partitions (URP)`: Partitions where follower replicas are behind the leader → data at risk.
    *   `ISR Shrink Rate`: Replicas falling out of the In-Sync Replica set → durability risk.
*   **Blast Radius (The "Impact"):**
    *   **Partition leader failure:** Zookeeper/KRaft elects a new partition leader from the ISR in <30 seconds. During this window, that partition is unavailable for writes.
    *   **All ISR down:** If all replicas of a partition fail simultaneously and `unclean.leader.election.enable=true`, Kafka elects an out-of-sync replica → potential data loss.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Thinking Kafka deletes messages after consumption (Kafka retains messages for the full retention period regardless of consumption — any consumer group can replay from any offset).
*   Not knowing that increasing partitions in Kafka is a **one-way operation** — you can add partitions but never reduce them without data loss risk.
*   Confusing `acks=1` (leader only) vs `acks=all` (full ISR) — the trade-off is latency vs durability.

### Interview Tip (The "Strong Hire" Signal)
> *"Kafka is our event backbone for 50M+ daily events. We partition by `user_id` to preserve per-user ordering. We use `acks=all` + `min.insync.replicas=2` for our financial events to ensure durability even if one broker crashes. For analytics events where occasional loss is acceptable, we use `acks=1` for 3× lower latency."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*
