## 📖 Overview
### What is HBase?
Apache HBase is an open-source, distributed, versioned, non-relational database modeled after Google's BigTable. It runs on top of the Hadoop Distributed File System (HDFS), providing strictly consistent read/write access to massive datasets.

### Core Capabilities
*   **Strong Consistency:** Unlike Cassandra (AP), HBase guarantees Strong Consistency (CP) for reads and writes, making it safer for certain analytical ledgers.
*   **Hadoop Native:** Integrates seamlessly into the Hadoop big data ecosystem (MapReduce, Hive, Spark).
*   **Sparse Data Handling:** Efficiently stores billions of rows with millions of columns, inherently handling nulls without consuming storage space.

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Category** | Database |
| **Type** | NoSQL Wide-Column |
| **Primary Use Case** | Hadoop Ecosystem, Batch Analytics |
| **Strengths** | Strong consistency, massive scale |
| **Weaknesses** | Heavy Java ecosystem, difficult setup |
| **Best For** | Big Data lakes, sparse data |
| **Never Use When** | Real-time web apps with complex relations |
| **Max Scale** | Petabytes |
| **Consistency Model** | Strong |
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
1. **HDFS Dependency:** HBase relies entirely on HDFS for underlying storage. If the HDFS NameNode fails, HBase goes down.
2. **CP over AP:** In the event of a network partition, HBase prioritizes Consistency over Availability. Regions will go offline rather than serve stale data.
3. **ZooKeeper Coordination:** Requires Apache ZooKeeper to manage cluster state and region assignments, adding another heavy Java dependency to the stack.
4. **Column Family Model:** Data is physically grouped by Column Families. Columns accessed together must be in the same family to avoid cross-disk I/O.
5. **No Secondary Indexes:** Natively, you can only query by the Row Key. To query by another column, you must use Apache Phoenix or manage your own index tables.
6. **Built-in Versioning:** Automatically keeps multiple versions of a cell (based on timestamp), allowing temporal queries ("What was this value yesterday?").
7. **Never Use When:** You need sub-millisecond latency, lack a Hadoop ecosystem, or need high availability during network partitions (use Cassandra instead).

### 🚫 When NOT to Use (Anti-Patterns)
*(Do not use HBase for a simple web-backend CRUD application. It is designed for offline Big Data analytics and requires massive infrastructure overhead to run.)*

---

## ⚙️ Internal Architecture (The "Deep Dive")
### 1. Core Engine Mechanics
Uses an LSM-Tree. Writes go to an in-memory `MemStore` and a Write-Ahead Log (WAL). When the `MemStore` fills, it flushes to disk as an `HFile`.

### 2. Storage & Persistence Layer
The `HFiles` are stored directly inside HDFS. This means HBase does not handle actual disk replication; HDFS handles the 3x block replication natively beneath it.

### 3. Replication & Consensus
The cluster consists of a `HMaster` (assigns regions, handles schema changes) and multiple `RegionServers` (handle actual read/write requests). ZooKeeper maintains the master election and cluster state. Data is sharded into `Regions` based on Row Key ranges.

---

## 📐 Standard Whiteboard Patterns
### 1. Common Integration Architecture
**The Big Data Pipeline:** Raw logs land in HDFS. Apache Spark runs batch jobs to aggregate the logs and bulk-loads the results into HBase. A front-end analytics dashboard queries HBase for the aggregated views via Apache Phoenix (which provides a SQL interface).

### 2. Failure Modes & Blast Radius
If a `RegionServer` dies, ZooKeeper detects the failure. The `HMaster` reassigns the dead server's Regions to healthy servers. During this recovery phase (replaying the WAL), those specific data regions are completely unavailable for reads and writes.

---

## 🛠️ Critical Configurations & Tuning
### 1. Consistency vs. Latency Flags
*   Strictly consistent by design for primary operations. Timeline-consistent read replicas can be configured for highly available, eventually consistent reads.

### 2. Eviction & Memory Management
*   `BlockCache`: Crucial for read performance. Caches frequently read HFile blocks in RAM. Must be tuned carefully against the JVM heap limits.

### 3. Connection & Thread Pools
*   Row Key design is the most critical tuning aspect. Sequential keys (like timestamps) cause "Region Spotting" (hot-spotting a single server). Keys must be salted or hashed.

---

## 💰 Cost & Operational Overhead
Extreme. Managing HDFS, ZooKeeper, and HBase RegionServers requires dedicated Big Data/Hadoop engineering teams. Modern cloud setups usually replace this entirely with managed services like Bigtable.

## 🥊 Direct Competitors & Alternatives
*   **HBase vs Cassandra:** HBase is CP (Consistent) and relies on a Master/ZooKeeper. Cassandra is AP (Available) and Masterless.
*   **HBase vs Google Bigtable:** Bigtable is the fully managed, closed-source Google Cloud equivalent that inspired HBase.

## 📊 Benchmarking & True Scale Constraints
Designed for tables with billions of rows and millions of columns. However, read latency can easily spike to hundreds of milliseconds during heavy HDFS compaction.

## 🔒 Security & Compliance
Integrates deeply with Kerberos for authentication and Apache Ranger for authorization within the Hadoop ecosystem.

---

## 💼 Production Experience
### 1. Real-World Use Case
*(Example: "Used HBase to store massive telecommunications Call Detail Records (CDRs) for a nationwide carrier, allowing fast lookup of any call history by `hash(phone_number)+timestamp`.")*

### 2. Lessons Learned (Gotchas)
*(Example: "Used a raw timestamp as the Row Key, which caused all incoming writes to hit a single RegionServer, bringing the node down while the rest of the cluster sat idle.")*
