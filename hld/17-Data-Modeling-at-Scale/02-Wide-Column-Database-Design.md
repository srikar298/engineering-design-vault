# ⚡ 02 - Wide-Column Database Design

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C128 |
| **Category** | Data Modeling |
| **Difficulty** | 🔴 Hard |
| **Interview Frequency** | 🔥 High |
| **Understanding** | [🔴 None / 🟡 Conceptual / 🟢 Applied] |
| **Can Explain** | [ ] Yes / [ ] No |
| **Whiteboard Drawn** | [ ] Yes / [ ] No |
| **Taught Someone** | [ ] Yes / [ ] No |
| **Next Review** | YYYY-MM-DD |
| **Mastery** | [🔴 Familiar / 🟡 Competent / 🟢 Expert] |

---

## ⚡ 1. The Core Definition & Trigger
*   **Two-Sentence Trigger:** Wide-column database design involves structuring data tables into partitions using a Partition Key, and sorting the rows within those partitions using one or more Clustering Keys. Architects choose wide-column stores (e.g., Apache Cassandra, ScyllaDB, HBase) when they require linear horizontal write scalability, high-throughput time-series ingestion, and predictable low-latency queries on petabyte-scale datasets while completely avoiding relational joins.
*   **Scalability Dimension:** Primary: **Write Throughput** & **Storage Capacity Scaling**. Secondary: **Read Latency (Partition-Bounded)**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### Core Architecture & The Write Path

Wide-column databases like Cassandra use a decentralized, masterless architecture based on a DHT (Distributed Hash Table) ring. Data is distributed across nodes using the hash of the **Partition Key**.

```
                   Token Ring (Gossip Protocol)
                      ┌───────────────┐
                      │    Node A     │
                      │ (Tokens: 0-9) │
                      └───────┬───────┘
                              │
             ┌────────────────┴────────────────┐
             ▼                                 ▼
     ┌───────────────┐                 ┌───────────────┐
     │    Node C     │                 │    Node B     │
     │(Tokens:20-29) │                 │(Tokens:10-19) │
     └───────────────┘                 └───────────────┘
```

#### LSM-Tree Write Path (Log-Structured Merge-Tree)
Writes are incredibly fast because they avoid random disk updates:
1.  **Commit Log:** The write is appended sequentially to a commit log on disk for durability.
2.  **Memtable:** The write is recorded in an in-memory sorted data structure (Memtable).
3.  **SSTable (Sorted String Table):** When the Memtable is full, it is flushed to disk as an immutable SSTable.

Because SSTables are immutable, there are no disk in-place updates. Updates and deletes simply write a new record. Deletes write a **tombstone** (a marker indicating deletion).

```
[Write Client] ──► [Commit Log (Disk, Sequential)]
               └──► [Memtable (RAM, Sorted)] ──► Flush ──► [SSTable (Disk, Immutable)]
```

#### The Compaction Process
To prevent disk bloat and slow reads, wide-column databases run **Compaction**:
*   **Mechanism:** Background threads merge multiple SSTables, keeping only the latest version of each column and discarding expired tombstones.
*   **Types:**
    *   *Size-Tiered Compaction Strategy (STCS):* Merges SSTables of similar sizes. Good for write-heavy workloads, but requires up to 50% free disk space for execution.
    *   *Leveled Compaction Strategy (LCS):* Divides SSTables into small, tiered levels. Reduces read amplification (good for read-heavy workloads) but increases write amplification.

---

### Keys & Query-Driven Modeling

In wide-column design, you **must design schemas around access patterns**, not entity relationships. You cannot perform `JOIN` queries.

#### 1. Partition Key vs. Clustering Key
*   **Partition Key:** Determines which physical node(s) store the partition.
    *   `PRIMARY KEY (user_id)` -> `user_id` is the partition key.
*   **Clustering Key:** Determines the sorted physical layout of rows *inside* the partition.
    *   `PRIMARY KEY (user_id, post_id)` -> `user_id` partitions, `post_id` sorts.
*   **Composite Partition Key:** Combines multiple columns to create finer partitions.
    *   `PRIMARY KEY ((user_id, bucket_id), post_id)` -> The hash of `(user_id, bucket_id)` determines the node.

#### 2. The 100MB Partition Limit Rule
A single wide-column partition should not exceed **100MB** in size or **100,000 rows**. Partitions larger than this cause high memory pressure (JVM Heap exhaustion during reads), slow compaction, and read timeouts.
*   **Unbounded Partitions:** Partitioning solely by `sensor_id` for time-series data creates an unbounded partition as data accumulates forever.
*   **Synthetic Bucketing:** To prevent this, architects add a `bucket_id` to the partition key (e.g., `bucket_id = timestamp / 7 days` or `timestamp / 24 hours`).

---

### Comparison: Relational vs. Document vs. Wide-Column

| Feature | Relational DB (PostgreSQL) | Document DB (MongoDB) | Wide-Column DB (Cassandra) |
| :--- | :--- | :--- | :--- |
| **Data Layout** | Rows and columns in normalized tables. | Hierarchical BSON/JSON documents. | Partitioned rows sorted by clustering keys. |
| **Join Support** | Yes (Native, ACID compliant). | No (Requires `$lookup` or application logic). | No. |
| **Write Path** | In-place updates (B-Tree page modifications). | B-Tree page modifications / WiredTiger. | LSM-Tree (Append-only memtable flush to SSTable). |
| **Scale Limits** | Hard to scale writes horizontally (Primary-Replica). | Scale via sharding keys (requires config servers). | Linear horizontal scaling (Masterless peer-to-peer). |
| **Deletes** | Direct record deletion. | Document deletion. | Writes a "tombstone" marker; purged later. |

---

## 💥 3. Resiliency & Operations

### Observability (The "Signal")
*   **Tombstones Scanned per Read:** High numbers of tombstones scanned per read (e.g., > 1,000) indicate that queries are reading deleted records. This causes CPU spikes and JVM GC pauses.
*   **Compaction Pending Tasks:** The number of SSTables waiting to be compacted. A rising trend means the disk system cannot keep up with writes, which degrades read latency.
*   **Read Repair / Hinted Handoffs Activity:** Tracks consistency operations. High rates indicate network partitions or node instability.

### Blast Radius (The "Impact")
*   **Hot Partition Collapse:** An unbounded or hot partition (e.g., a massive celebrity account partition) receives excessive read/write traffic. The node hosting this partition experiences high CPU utilization, JVM GC pauses, and eventually drops off the ring. The cluster coordinator routes the request to replicas, causing them to crash in a cascading failure.
*   **Mitigation:**
    1.  **Synthetic Bucketing:** Ensure data distribution across the entire token space.
    2.  **Read/Write Timeouts:** Set aggressive client-side query timeouts to fail fast.
    3.  **Rate Limiting:** Throttle queries targeting specific partition keys at the API gateway or BFF tier.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   **Designing SQL schemas in Cassandra:** Creating schemas with normalizations and planning to use secondary indexes (`CREATE INDEX`) or `ALLOW FILTERING` to execute arbitrary searches. This forces Cassandra to scan all nodes in the cluster, crashing performance.
*   **Ignoring Partition Size Bounds:** Failing to compute partition sizes for high-frequency time-series data, leading to partitions that grow into gigabytes.
*   **Misunderstanding Deletes:** Suggesting frequent updates/deletes in Cassandra without knowing that deletes write tombstones, which degrades read performance and causes OOMs.

### Interview Tip (The "Strong Hire" Signal)
> "When modeling data in ScyllaDB or Cassandra, query design precedes database layout. To handle a high-volume IoT telemetry stream, I avoid partitioning solely on `device_id` as it results in unbounded partitions. Instead, I model the table with a composite partition key `((device_id, partition_bucket), event_timestamp)`. The `partition_bucket` is derived programmatically (e.g., `event_timestamp / 1 day` or `hash(device_id) % shards`). This guarantees partitions remain under the 100MB limit, distributes writes evenly across the token ring, and allows localized range queries using the clustering key."

---

## 💡 5. My Custom Study Notes & Whiteboard

### How a Read Request Resolves (Read Path)

```
                            [Read Request]
                                   │
                                   ▼
                       [Check Bloom Filter (RAM)]
                       (Determines if SSTable contains key)
                                   │
                         ┌─────────┴─────────┐
                         ▼ (True)            ▼ (False)
              [Check Key Cache (RAM)]     [Skip SSTable]
                         │
               ┌─────────┴─────────┐
               ▼ (Hit)             ▼ (Miss)
        [Read SSTable]      [Check Partition Summary (RAM)]
                                   │
                                   ▼
                            [Check Partition Index (Disk)]
                                   │
                                   ▼
                            [Read SSTable (Disk)]
```

*   **Tombstone Management:**
    *   Tombstones have a Time-To-Live (configured via `gc_grace_seconds`, default is 10 days).
    *   Do not set `gc_grace_seconds` to 0 unless replication is disabled, or deleted data may resurrect during Read Repairs if a node was offline during the delete operation.
