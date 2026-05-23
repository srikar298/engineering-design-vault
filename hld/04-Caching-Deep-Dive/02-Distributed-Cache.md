# ⚡ 02 - Distributed Cache

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C023 |
| **Category** | Caching Topology |
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
*   **Two-Sentence Trigger:** A distributed cache is an external, shared caching tier spanning multiple nodes (e.g., Redis Cluster, Memcached) that pools aggregate RAM to serve horizontally scaled, stateless application servers. It partitions data across the cluster using sharding algorithms (like Consistent Hashing or Hash Slots) to maintain high availability and sub-millisecond access.
*   **Scalability Dimension:** Horizontally scales Read/Write QPS and RAM storage capacity, decoupling state from application computation.

---

## ⚖️ 2. Trade-offs & Deep Dive

### Redis vs. Memcached (The SDE-2 Baseline)
| Feature | Redis (Cluster) | Memcached |
| :--- | :--- | :--- |
| **Core Architecture** | Single-threaded event loop (uses non-blocking `epoll` multiplexing). | Multi-threaded architecture (utilizes helper threads to scale across CPU cores). |
| **Data Types** | Rich structures: Strings, Hashes, Lists, Sets, Sorted Sets (ZSets), HyperLogLogs, Geospatial indices. | Simple Key-Value Strings only (must serialize complex objects to JSON/binary). |
| **Persistence** | Supported (RDB snapshots, Append-Only Files (AOF), or hybrid). | Ephemeral only. No persistence (power loss = data loss). |
| **Replication & HA** | Native Master-Slave replication, Sentinel failover, and Sharded Cluster. | No native replication. Requires client-side routing or third-party proxies. |
| **Memory Management** | High overhead per key due to rich data type metadata descriptors. | Extremely memory-efficient; slab allocator prevents memory fragmentation. |

### How Data Sharding Works (Crucial for Interviews)
1. **Modulo Sharding (`hash(key) % N`):**
   * *How it works:* Keys are mapped directly to a fixed number of nodes $N$.
   * *Why it fails:* If a node dies or a new node is added ($N$ changes to $N \pm 1$), **almost all keys** hash to different nodes. This triggers a total cache wipeout and crashes the database.
2. **Consistent Hashing:**
   * *How it works:* Both keys and cache nodes are hashed onto a conceptual 360-degree "hash ring". Keys are assigned to the first node encountered moving clockwise.
   * *The SDE-2 edge:* Virtual nodes are mapped to physical nodes to ensure uniform distribution of keys, preventing hot-spotting. When a node is added or removed, only $1/N$ of the keys need to be remapped.
3. **Redis Hash Slots (The Hybrid Model):**
   * Redis Cluster uses a fixed space of **16,384 Hash Slots**.
   * Keys are mapped to slots via `CRC16(key) % 16384`.
   * Slots are distributed across active master nodes. When nodes are added/removed, slots are moved between nodes without downtime.

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `used_memory` vs `maxmemory`: If memory hits maximum, Redis starts evicting keys based on the configured policy (`maxmemory-policy`).
    *   `evicted_keys`: High eviction rates indicate your cache capacity is too small for the working set.
    *   `instantaneous_ops_per_sec`: Identifies traffic spikes.
    *   `rejected_connections`: Indicates Redis has run out of file descriptors (max clients hit).
*   **Blast Radius (The "Impact"):**
    *   If a master node fails, there is a transient **failover window** (typically 10-30 seconds) where replica nodes negotiate promotion via election. During this window, writes to that shard will fail, and reads may return stale data or trigger database fallbacks.
*   **Numbers to Know:**
    *   Memory lookup latency: **~100-200 microseconds**
    *   Network hop latency to cache: **~1-3 ms**
    *   Redis Cluster Max Clients: Default is **10,000 connections**.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   **The Single-Thread Block:** Running an $O(N)$ command like `KEYS *` or `FLUSHALL` in production. Because Redis is single-threaded, this blocks all incoming client requests, causing timeouts.
*   **Multi-Key Cross-Slot Queries:** Attempting transactions or multi-key operations (like `MGET` or transactions) across keys that reside on different cluster shards. Redis Cluster will return a `CROSSSLOT Keys in request don't hash to the same slot` error.
*   *Solution:* Use **Hash Tags** (e.g., `{user123}:profile` and `{user123}:orders`) to force keys containing the same bracketed prefix to hash to the same slot.

### Interview Tip (The "Strong Hire" Signal)
> *"If we need high write throughput for key updates, I'll select Redis, but we must watch out for single-threaded CPU bottlenecks. For commands on massive datasets, I will enforce client-side batching and routing using Redis Pipelines, and avoid commands with $O(N)$ time complexity like KEYS, preferring SCAN which yields control back to the event loop."*

---

## 💡 5. My Custom Study Notes & Whiteboard

### How Redis Sentinel vs. Redis Cluster Differ:
*   **Redis Sentinel (High Availability):** Best for smaller datasets that fit on a single node. Sentinel acts as an orchestrator that monitors a master node and automatically promotes a slave if the master dies. It does **not** shard data.
*   **Redis Cluster (HA + Sharding):** Best for massive datasets. Data is sharded across multiple master nodes. The master nodes monitor each other directly via a Gossip protocol and handle failovers automatically without needing Sentinel.

```
                  [Client]
                     │
         ┌───────────┼───────────┐ (Gossip protocol monitors state)
         ▼           ▼           ▼
     [Master 1]  [Master 2]  [Master 3] (Sharded via 16,384 slots)
         │           │           │
         ▼           ▼           ▼
     [Replica 1] [Replica 2] [Replica 3] (Asynchronous replication)
```
