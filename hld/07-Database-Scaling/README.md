# 🚀 07 - Database Scaling (C060-C064)

## 🧭 Database Scaling Study Path
Use this structured path aligned with your **Google Sheet Tracker** to study database scaling:
*   [C060 - Database Replication](./01-Database-Replication.md)
*   [C061 - Read Replicas](./02-Read-Replicas.md)
*   [C062 - Database Partitioning](./03-Database-Partitioning.md)
*   [C063 - Database Sharding](./04-Database-Sharding.md)
*   [C064 - Database Federation](./05-Database-Federation.md)

---

## 📖 1. The Basics
Scaling a database is the hardest part of a 10k+ user system. You must balance between **Replication** (Copying data for High Availability and Reads) and **Sharding** (Splitting data for Writes).

---

## 🌐 2. Replication Topologies (DDIA Chapter 5)

| Topology | How it works | Best For | The Challenge |
| :--- | :--- | :--- | :--- |
| **Single-Leader** | 1 Master (Writes), N Followers (Reads). | Most web apps. | **Replication Lag**. A user writes to Master but reads from a slow Follower and sees "stale" data. |
| **Multi-Leader** | Writes allowed to 2+ Masters (e.g., across regions). | Global availability. | **Write Conflicts**. What if two users update the same key in different regions? |
| **Leaderless** | Writes are sent to all nodes. Quorum ($W+R>N$) decides result. | Massive write scale. | **Conflict Resolution** (LWW, Version Vectors). |

---

## ⚡ 3. The "Strong Hire" Differentiator: Replication Lag Solutions

In an SDE-2+ interview, if you mention Read Replicas, you **MUST** mention how to solve Replication Lag:
1. **Read-your-own-writes Consistency:** Ensure the user always reads from the Master for a short period (e.g., 60s) after they make a write.
2. **Monotonic Reads:** Ensure that after a user has seen a certain piece of data, they don't see an older version later (Pin a user session to a specific replica).
3. **Consistent Prefix Reads:** Ensure that if a sequence of writes happens in a certain order, anyone reading those writes will see them in that same order.

---

## 🚫 4. Sharding (Partitioning)

When your writes exceed the capacity of a single Master, you must **Shard** (Partition) your data.

| Sharding Strategy | How it works | Pros | Cons |
| :--- | :--- | :--- | :--- |
| **Key-based (Hash)** | `shard = hash(user_id) % N` | Uniform distribution. No hot-spots (usually). | Adding a shard is hard (**Resharding**). No range queries. |
| **Range-based** | `shard 1: A-M, shard 2: N-Z` | Allows range queries (e.g., "Find all users starting with B"). | **Hot Spots**. If many users start with 'A', Shard 1 crashes while Shard 2 is idle. |
| **Directory-based** | A lookup table stores `user_id -> shard_ip`. | Extremely flexible. Moving data is easy. | The Lookup Table becomes a **Single Point of Failure** and adds a network hop. |

---

## 🚀 5. The SDE-3 Edge: The Celebrity Problem (Hot Keys)
If the interviewer asks: *"What if Justin Bieber (100M followers) posts a tweet? Your User-ID shard will be crushed while others are empty."*

**The Senior Solution:**
1. **Adaptive Sharding:** For extremely hot keys, further split that specific key across multiple shards. Instead of just `JustinBieber`, use `JustinBieber_1`, `JustinBieber_2`, etc.
2. **Caching (The Hybrid Approach):**
    - **Fan-out on write** for regular users (push to their feed).
    - **Fan-out on read** for celebrities. When a user logs in, the app queries the "Celebrity Cache" separately.
3. **Write Path Isolation:** Use a dedicated, higher-provisioned message queue or database cluster for ultra-high-volume users.

**Senior Signal:** "We don't solve the celebrity problem at the database layer alone. We use a **tiered caching strategy** and treat the top 0.1% of users as a 'Special Case' in our application logic to prevent them from degrading the experience for the other 99.9%."

---
