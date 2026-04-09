# 🗄️ 05 - Databases (The SDE-2 Perspective)

## 📖 The Concept
Databases store system state. The fundamental choice is between Relational (SQL) and Non-Relational (NoSQL). An SDE-2 must know exactly *why* a specific data model fits the business requirement.

## 📊 The SDE-2 Trade-off Table: SQL vs NoSQL

| Feature | SQL (PostgreSQL, MySQL) | NoSQL (DynamoDB, Cassandra) |
| :--- | :--- | :--- |
| **Schema** | Rigid (Schema-on-write). Changes are painful. | Flexible (Schema-on-read). Add fields dynamically. |
| **Scaling** | Vertical scaling is easy. Horizontal (Sharding) is hard. | Designed for massive Horizontal scaling out-of-the-box. |
| **Transactions** | Strong ACID guarantees. | Typically BASE (Eventually Consistent) or single-row ACID. |
| **Ideal For** | Financial systems, Complex Joins, Structured domains. | High-velocity writes (IoT), Unstructured catalogs, Massive scale. |

## 🚫 The Interview Trap
**"I'll use MongoDB because it's web-scale and faster."**
Choosing NoSQL "for speed" without analyzing the access pattern is a red flag. If your application relies heavily on `JOIN` operations (e.g., an Order needs User, Payment, and Inventory data), doing this in NoSQL requires multiple network round-trips (Application-side joins), which is *slower* and error-prone.
*Better Answer:* "The data is highly relational with strict consistency requirements, so PostgreSQL is the right choice. We will scale reads via read-replicas."

## 🚀 The SDE-3 Edge: Storage Engines (DDIA Mastery)
If the interviewer asks: *"How does the DB actually store data on disk?"*

| **B-Trees** | Balanced Tree. Updates in-place. | Read-heavy workloads. Strong ACID. | PostgreSQL, MySQL. |
| **LSM-Trees** | Append-only Logs + Merging. | Write-heavy workloads. Massive throughput. | Cassandra, DynamoDB, BigTable. |

---

## 🛠️ 2. Indexing: The Multipliers of Speed

| Index Type | How it works | Best For |
| :--- | :--- | :--- |
| **B-Tree** | Sorted balanced tree. | Range queries, exact matches, sorting. (Default). |
| **Hash Index** | Converts value to a hash. | $O(1)$ exact matches only. No range queries. |
| **GIN (Inverted)** | Maps values to a list of IDs. | Full-text search, JSONB fields in Postgres. |

**The Senior Signal:** "Indices aren't free. They speed up reads but slow down writes (Write Amplification) because every write must update the index. We will only index fields used in `WHERE` clauses with high selectivity."

---

## 🚀 The SDE-3 Edge: Database Scaling Patterns

When a single DB instance maxes out its CPU/IO, you have three options:

| Strategy | Complexity | Best For |
| :--- | :--- | :--- |
| **Read Replicas** | Low | Read-heavy apps (90% reads). Async sync lag is the trade-off. |
| **Database Sharding** | Very High | Massive datasets that don't fit on one disk. Requires a partition key. |
| **Federation** | Medium | Splitting DB by domain (e.g., User DB, Order DB). |

### The Sharding Trap
How do you shard?
*   **Key-based (Hash):** `shard = hash(key) % N`. Good distribution, but adding a shard is a nightmare (reshuffling).
*   **Range-based:** `shard1 = User A-M, shard2 = User N-Z`. Good for range queries, but leads to **Hot Spots** (e.g., many users starting with 'A').
*   **Directory-based:** A lookup table stores where each key lives. Most flexible but adds one network hop.

**Senior Signal:** "We chose **Hashed Sharding** with **Consistent Hashing** (via a middleware like Vitess) to avoid the reshuffling cost when we inevitably grow from 10 to 20 shards next year."

---
