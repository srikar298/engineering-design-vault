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

## 🚀 The SDE-3 Edge: B-Trees vs. LSM Trees
If the interviewer asks: *"Why is Cassandra better for write-heavy workloads than PostgreSQL?"*

Explain the underlying storage engine:
1. **PostgreSQL uses B-Trees:** When you write, it must update the tree structure in-place on the disk (Random I/O), which is slow.
2. **Cassandra uses LSM (Log-Structured Merge) Trees:** When you write, it simply appends the data to an in-memory structure (MemTable) and a sequential commit log. Sequential I/O is vastly faster than Random I/O. Later, data is flushed to disk in immutable segments (SSTables).
