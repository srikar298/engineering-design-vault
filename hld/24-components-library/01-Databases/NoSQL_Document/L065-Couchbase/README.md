## 📖 Overview
### What is Couchbase?
Couchbase is an open-source, distributed NoSQL document-oriented database with a memory-first architecture. Born from the merger of Membase (Memcached) and CouchDB, it provides the sub-millisecond performance of a KV cache combined with the querying power of a document database.

### Core Capabilities
*   **Memory-First Architecture:** Automatically caches all active data in memory, bypassing disk I/O for hot reads and writes.
*   **N1QL (SQL for JSON):** Allows developers to query JSON documents using standard SQL syntax (`SELECT`, `JOIN`, `GROUP BY`), vastly reducing the NoSQL learning curve.
*   **Active-Active XDCR:** Cross Data Center Replication (XDCR) provides natively resilient, bidirectional replication across global regions.

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Category** | Database |
| **Type** | NoSQL Document / KV |
| **Primary Use Case** | Session stores, user profiles |
| **Strengths** | Memory-first arch, SQL-like N1QL |
| **Weaknesses** | Complex multi-dimensional scaling |
| **Best For** | Sub-ms KV reads + Document queries |
| **Never Use When** | Strict relational ledgers |
| **Max Scale** | Terabytes |
| **Consistency Model** | Eventual (Cross Datacenter) |
| **CAP Choice** | CP |
| **Understanding** | [ ] None / [ ] Conceptual / [x] Applied |
| **Internals Known** | [x] Yes / [ ] No |
| **Interview Ready** | [x] Yes / [ ] No |
| **Used In Projects** | [x] Yes / [ ] No |
| **Key Config Known** | [x] Yes / [ ] No |
| **Comparison Known** | [x] Yes / [ ] No |
| **Last Revised** | YYYY-MM-DD |
| **Next Review** | YYYY-MM-DD |
| **Mastery** | [ ] Familiar / [x] Competent / [ ] Expert |

---

## ⚖️ Architectural Trade-offs & Deep Dive
1. **Memory vs Disk:** Unlike MongoDB which relies on the OS page cache, Couchbase manages its own managed cache (rooted in Memcached). Writes hit RAM and are acknowledged instantly before async disk flush.
2. **Multi-Dimensional Scaling (MDS):** Allows separating the Data, Index, and Query services onto different physical hardware. (e.g., CPU-heavy nodes for Queries, RAM-heavy for Data).
3. **No Multi-Document ACID:** Historically lacked multi-document transactions (added recently in v6.5, but not its primary strength compared to SQL).
4. **N1QL Flexibility vs Speed:** N1QL is incredibly flexible, but using SQL `JOIN`s on JSON documents distributed across a cluster will always be slower than denormalized, single-document fetches.
5. **vBucket Architecture:** Data is sharded automatically into 1024 active vBuckets spread evenly across the cluster.
6. **Never Use When:** Your data does not fit in RAM and you expect high performance, or your data model is strictly relational and static.

### 🚫 When NOT to Use (Anti-Patterns)
*(Do not use Couchbase if you only need a pure disk-backed warehouse; its memory-first architecture makes it prohibitively expensive if you aren't utilizing the RAM speed.)*

---

## ⚙️ Internal Architecture (The "Deep Dive")
### 1. Core Engine Mechanics
Combines a distributed object cache (Memcached) with a persistent storage engine. Every node runs a cluster manager. When an application writes data, it is written to RAM and the client receives success. A background thread flushes the RAM queue to the storage engine (Disk).

### 2. Storage & Persistence Layer
Uses Append-Only B-Trees (Couchstore) or the newer Magma storage engine for disk persistence. Data fragmentation is cleaned up via continuous background compaction.

### 3. Replication & Consensus
Intra-cluster replication is memory-to-memory. Cross Data Center Replication (XDCR) is conflict-resolving and bidirectional, making global active-active deployments highly resilient.

---

## 📐 Standard Whiteboard Patterns
### 1. Common Integration Architecture
**The High-Speed User Profile Service:** A global authentication API uses Couchbase as both the session cache (KV lookups <1ms) and the user profile repository (Document queries for analytics).

### 2. Failure Modes & Blast Radius
If a Data node fails, Auto-Failover kicks in after a timeout. Replicas are promoted to Active vBuckets. During the timeout window, specific documents residing on the dead node are inaccessible.

---

## 🛠️ Critical Configurations & Tuning
### 1. Consistency vs. Latency Flags
*   `scan_consistency`: When querying via N1QL, `not_bounded` returns the index as-is (super fast, potentially stale). `request_plus` forces the query to wait for the index to catch up to the latest writes (strongly consistent, slower).

### 2. Eviction & Memory Management
*   `Value Eviction` vs `Full Eviction`: Value eviction keeps all keys in RAM but removes values when full. Full eviction removes keys and values, saving RAM but increasing read latency.

### 3. Connection & Thread Pools
*   The Smart Client libraries automatically map the cluster topology (vBucket map). The client connects directly to the correct data node, bypassing the need for a load balancer.

---

## 💰 Cost & Operational Overhead
MDS architecture allows precise cost scaling, but managing Couchbase clusters requires deep operational knowledge of its specific memory quotas and compaction routines.

## 🥊 Direct Competitors & Alternatives
*   **Couchbase vs MongoDB:** Mongo dominates mindshare, but Couchbase generally wins in pure KV lookup speed and offers N1QL, which is vastly easier for SQL developers to learn than Mongo's aggregation framework.
*   **Couchbase vs Redis:** Redis is purely in-memory (disk is secondary). Couchbase is a true durable database that leads with memory.

## 📊 Benchmarking & True Scale Constraints
Easily handles hundreds of thousands of operations per second at sub-millisecond latency, provided the working set fits entirely in RAM.

## 🔒 Security & Compliance
Enterprise edition supports Role-Based Access Control, Auditing, and x.509 certificate authentication.

---

## 💼 Production Experience
### 1. Real-World Use Case
*(Example: "Replaced a Redis + Postgres caching layer with a single Couchbase cluster, reducing architectural complexity while maintaining sub-millisecond read times for user sessions.")*

### 2. Lessons Learned (Gotchas)
*(Example: "Used `request_plus` consistency on a highly concurrent N1QL query, which bottle-necked the indexer and caused massive query timeouts across the cluster.")*
