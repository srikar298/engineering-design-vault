# 🚀 07 - Database Scaling: Replication & Sharding

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

## 🚫 4. Sharding (Partitioning) & The "Hot Key" Problem

... [Existing Sharding Table] ...

## 🚀 5. The SDE-3 Edge: The Celebrity Problem (Hot Keys)

... [Existing Hot Key Section] ...
