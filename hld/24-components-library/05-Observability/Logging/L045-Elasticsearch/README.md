# L045: Elasticsearch

## 📖 Overview
### What is this component?
*(A brief 2-3 sentence explanation of what this technology is, its primary purpose, and its role in modern system design.)*

### Core Capabilities
*(List 3-4 bullet points detailing exactly what this component does best.)*

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Category** | Observability |
| **Type** |  |
| **Primary Use Case** | Log storage |
| **Strengths** | Kibana |
| **Weaknesses** |  |
| **Best For** |  |
| **Never Use When** |  |
| **Max Scale** |  |
| **Consistency Model** |  |
| **CAP Choice** |  |
| **Understanding** | [🔴 None / 🟡 Conceptual / 🟢 Applied] |
| **Internals Known** | [ ] Yes / [ ] No |
| **Interview Ready** | [ ] Yes / [ ] No |
| **Used In Projects** | [ ] Yes / [ ] No |
| **Key Config Known** | [ ] Yes / [ ] No |
| **Comparison Known** | [ ] Yes / [ ] No |
| **Last Revised** | YYYY-MM-DD |
| **Next Review** | YYYY-MM-DD |
| **Mastery** | [🔴 Familiar / 🟡 Competent / 🟢 Expert] |

---

## ⚖️ Architectural Trade-offs & Deep Dive


1. **Inverted Index Core:** Built on Apache Lucene, optimizing for high-speed full-text search and relevance scoring (TF-IDF/BM25) over simple exact-match lookups.
2. **Schema-Free but Typed:** Accepts unstructured JSON documents but strictly enforces dynamic type mapping under the hood (e.g., indexing strings as `text` or `keyword`).
3. **Heavy JVM Overhead:** Very resource intensive on CPU and Memory (heap). Requires careful JVM tuning to avoid Garbage Collection pauses.
4. **Near Real-Time (NRT):** Documents are written to an in-memory buffer and flushed to disk segments periodically (e.g., 1s), meaning there is a slight delay before data is searchable.
5. **Sharding and Rebalancing:** Natively horizontal, but over-sharding causes the "cluster state" to become a massive bottleneck.
6. **Poor for Primary Transactions:** Not ACID compliant. Do not use as the primary source of truth for critical financial transactions.
7. **Never Use When:** You just need simple key-value lookups or highly relational standard OLTP workloads (use Postgres/Redis).


### 🚫 When NOT to Use (Anti-Patterns)
*(Detail the anti-patterns. What specific system constraints or access patterns make this technology the absolute wrong choice?)*

---

## ⚙️ Internal Architecture (The "Deep Dive")
### 1. Core Engine Mechanics
*(Document how the engine actually works under the hood. e.g., LSM Trees vs B-Trees, Append-only logs, Event loops)*

### 2. Storage & Persistence Layer
*(How data is physically stored on disk vs memory. e.g., SSTables, CommitLogs, Memory-mapped files)*

### 3. Replication & Consensus
*(How nodes talk to each other. e.g., Leader-Follower, Masterless Ring, Raft/Paxos consensus, Quorum writes)*

---

## 📐 Standard Whiteboard Patterns
### 1. Common Integration Architecture
*(Sketch/describe the standard way this fits into a system. e.g., Cache-Aside pattern, Outbox Pattern with CDC, API Gateway fronting Lambdas)*

### 2. Failure Modes & Blast Radius
*(What happens when a node dies? How does the system degrade gracefully? e.g., Split-brain resolution, Thundering herd protection)*

---

## 🛠️ Critical Configurations & Tuning
### 1. Consistency vs. Latency Flags
*(Configuration flags that dictate CAP choices. e.g., `acks=all` vs `acks=1`, `min.insync.replicas`, strict quorum vs local quorum)*

### 2. Eviction & Memory Management
*(How it handles running out of space. e.g., `allkeys-lru`, TTLs, garbage collection overhead)*

### 3. Connection & Thread Pools
*(How it handles high concurrency. e.g., max connections, thread counts)*

---

---

## 💰 Cost & Operational Overhead
*(Detail the TCO and DevOps burden. e.g., Requires a dedicated 3-person team to manage ZooKeeper, or fully managed but expensive per API call).*

## 🥊 Direct Competitors & Alternatives
*(Quick 1-to-1 comparisons. e.g., Cassandra vs. DynamoDB, or Redis vs. Memcached).*

## 📊 Benchmarking & True Scale Constraints
*(Actual numbers. e.g., "Saturates at 30k RPS per node", or "Degrades heavily past 5TB per shard").*

## 🔒 Security & Compliance
*(Enterprise capabilities. e.g., At-rest encryption support, RBAC, IAM integration).*

## 💼 Production Experience
### 1. Real-World Use Case
*(Brief 2-sentence blurb about a specific project where you used this component)*

### 2. Lessons Learned (Gotchas)
*(What went wrong in production? e.g., "Over-sharded the Elasticsearch cluster causing master-node timeout.")*
