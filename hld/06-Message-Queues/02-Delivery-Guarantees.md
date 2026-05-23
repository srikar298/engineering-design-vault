# ⚡ 02 - Delivery Guarantees (At-Most-Once, At-Least-Once, Exactly-Once)

## 📋 Tracker Metadata — At-Most-Once Delivery (C055)
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C094 |
| **Category** | Messaging & Queues |
| **Difficulty** | 🟡 Medium |
| **Interview Frequency** | 🔥 High |

## 📋 Tracker Metadata — At-Least-Once Delivery (C056)
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C094 |
| **Category** | Messaging & Queues |
| **Difficulty** | 🟡 Medium |
| **Interview Frequency** | 🔥 High |

## 📋 Tracker Metadata — Exactly-Once Delivery (C057)
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C094 |
| **Category** | Messaging & Queues |
| **Difficulty** | 🔴 Hard |
| **Interview Frequency** | 🔥 High |

---

## ⚡ 1. The Core Definition & Trigger
*   **Two-Sentence Trigger:** Delivery guarantees define what happens to a message when failures occur mid-flight — the three levels are At-Most-Once (possible loss, no duplicates), At-Least-Once (possible duplicates, no loss), and Exactly-Once (no loss, no duplicates). **Exactly-Once is the hardest guarantee** and is computationally expensive — most production systems use At-Least-Once with idempotent consumers as the practical equivalent.
*   **Scalability Dimension:** Primary: **Data Integrity vs System Complexity vs Throughput Trade-off**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### The Three Guarantees Compared
| Guarantee | How It Works | Message Loss? | Duplicates? | Cost | Use Case |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **At-Most-Once** | Fire and forget. Producer sends, does not wait for ACK. Consumer does not re-queue on failure. | ✅ Yes | ❌ No | Cheapest — zero coordination. | Metrics/telemetry where losing occasional data points is acceptable. |
| **At-Least-Once** | Producer retries until ACK received. Consumer ACKs only *after* successful processing. | ❌ No | ✅ Yes (on retry) | Moderate — retry logic + deduplication. | Most business events: orders, payments, notifications. |
| **Exactly-Once** | Distributed transaction across producer + broker + consumer. Idempotent producer IDs + transactional offsets in Kafka. | ❌ No | ❌ No | Highest — 2× latency, coordination overhead. | Financial transactions, inventory deduction. |

### How Exactly-Once Works in Kafka
1. **Idempotent Producer:** Each producer gets a unique PID. Each message has a sequence number. The broker deduplicates messages with the same PID+sequence — even if the producer retries on failure.
2. **Transactional API:** `producer.beginTransaction()` → send to multiple partitions → `consumer.commitSync()` → `producer.commitTransaction()`. All succeed or all roll back atomically.
3. **Read-Committed Isolation:** Consumers only see messages from committed transactions (not in-flight ones from open transactions).

### The Practical Reality
> True Exactly-Once is **extremely expensive** and rarely needed end-to-end. The industry standard approach:
> **At-Least-Once delivery + Idempotent consumer = effective Exactly-Once semantics at the application layer.**

```
Message arrives (possibly duplicate)
  └─► Consumer checks: "Have I already processed idempotency_key = abc123?"
        ├─► Yes: Skip (return 200) → Duplicate ignored ✅
        └─► No:  Process → Store result → Mark idempotency_key as processed ✅
```

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Duplicate message rate`: High rates with At-Least-Once indicate consumers are frequently crashing mid-processing.
    *   `Idempotency key cache hit rate`: Measures how often duplicates are correctly deduplicated.
*   **Blast Radius (The "Impact"):**
    *   At-Least-Once without idempotent consumers → **double charges, double emails, double shipments** on any retry.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Saying "we need Exactly-Once for payments" without knowing the cost. The correct answer is: "At-Least-Once with an idempotency check in the payment handler using the payment request UUID."
*   Not knowing that a consumer must **ACK after processing** (not before) to achieve At-Least-Once. ACKing before processing = At-Most-Once.

### Interview Tip (The "Strong Hire" Signal)
> *"We use Kafka with At-Least-Once delivery. Our payment consumer uses idempotency keys — each payment event carries a unique payment_request_id. The consumer checks Redis before processing: if the key exists, it's a duplicate and we skip it. This gives us effective Exactly-Once semantics at the business logic layer without the overhead of Kafka's transactional API."*

---

## 💡 5. My Custom Study Notes & Whiteboard
```
ACK Timing Determines Guarantee:

Consumer receives message
  ├── ACK *before* processing → AT-MOST-ONCE (crash after ACK = lost)
  └── ACK *after* processing  → AT-LEAST-ONCE (crash before ACK = redelivered)

"At-Least-Once + Idempotent Consumer = Practically Exactly-Once" ← memorize this
```
