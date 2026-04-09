# ⚖️ 10 - Consistency Models (The SDE-3 Edge)

## 📖 The Concept
In distributed systems, data is replicated across multiple nodes. Consistency models define *when* and *how* those nodes agree on the state of the data. 

## 📊 The SDE-2 Trade-off Table: CAP Theorem

The CAP Theorem states that a distributed data store can only provide two of the following three guarantees simultaneously:
*   **C (Consistency):** Every read receives the most recent write.
*   **A (Availability):** Every request receives a response (even if it's stale data).
*   **P (Partition Tolerance):** The system continues to operate despite network drops between nodes.

| System Type | What it Sacrifices | Use Case | Examples |
| :--- | :--- | :--- | :--- |
| **CP (Consistent/Partition Tolerant)** | Sacrifices Availability. | Financial ledgers, Inventory management. | MongoDB, HBase, Redis, Zookeeper. |
| **AP (Available/Partition Tolerant)** | Sacrifices Consistency. | Social media feeds, Shopping carts. | Cassandra, DynamoDB, CouchDB. |
| **CA (Consistent/Available)** | *Impossible* in distributed systems. | N/A (Networks will always have partitions). | Single-node PostgreSQL. |

## 🚫 The Interview Trap
**"Our system needs to be highly available AND strongly consistent globally."**
This violates physics (the speed of light). You cannot update a database in Tokyo and instantly read that exact update in New York without sacrificing availability (waiting for the sync to finish).
*Better Answer:* "We have to choose. For a social media feed, we will prioritize Availability (AP) and accept Eventual Consistency. For our billing service, we will prioritize Consistency (CP) and accept that the service might reject requests during a network partition."

---

## 🔐 11 - Transaction Isolation Levels (DDIA Mastery)

In a high-concurrency system (10k+ users), multiple transactions happen simultaneously. **Isolation** is how the DB hides these concurrent changes from each other.

| Isolation Level | Phenomenon Prevented | How it works |
| :--- | :--- | :--- |
| **Read Committed** | Dirty Reads | You only see data that has been committed. |
| **Snapshot Isolation** | Read Skew | Each transaction reads from a consistent snapshot. Standard in Postgres. |
| **Serializable** | **All Race Conditions** | Strongest isolation. DB acts as if transactions ran one after another. |

**The Senior Signal:** "We chose Snapshot Isolation using MVCC (Multi-Version Concurrency Control) for our financial ledger. It allows us to perform consistent backups and complex reports without locking the entire database and blocking writes."

---

## 🚀 The SDE-3 Edge: PACELC & Quorums

### 1. PACELC Theorem
If the interviewer asks: *"CAP theorem only applies during network failures (Partitions). What about normal operation?"*

Introduce **PACELC**:
*   If there is a **P**artition, you must choose between **A**vailability and **C**onsistency.
*   **E**lse (normal operation), you must choose between **L**atency and **C**onsistency.

### 2. Tuned Consistency (Quorum Logic)
How do databases like Cassandra or DynamoDB let you choose your consistency? They use **Quorums**.

*   **N**: Number of replicas.
*   **W**: Number of nodes that must acknowledge a write.
*   **R**: Number of nodes that must respond to a read.

| Configuration | Rule | Strength | Use Case |
| :--- | :--- | :--- | :--- |
| **Strong Consistency** | `R + W > N` | High. Read is guaranteed to see the latest write. | Financial data. |
| **Eventual Consistency** | `R + W <= N` | Low. Stale reads are possible but latency is lower. | Social media comments. |
| **Write-Heavy Opt.** | `W = N, R = 1` | High Write Durability. | Logging, Audit trails. |
| **Read-Heavy Opt.** | `W = 1, R = N` | High Read Consistency. | Reference data, Configuration. |

**The Senior Signal:** "We configure our cluster for `R=2, W=2, N=3`. This gives us **Quorum Consistency**, allowing us to tolerate the failure of any single node without losing data or the ability to perform strongly consistent reads/writes."

---
