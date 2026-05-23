# ⚡ 07 - B-Trees vs. LSM-Trees

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C034 |
| **Category** | Database Internals |
| **Difficulty** | 🟠 Hard |
| **Interview Frequency** | 🔥 High |
| **Understanding** | [🔴 None / 🟡 Conceptual / 🟢 Applied] |
| **Can Explain** | [ ] Yes / [ ] No |
| **Whiteboard Drawn** | [ ] Yes / [ ] No |
| **Taught Someone** | [ ] Yes / [ ] No |
| **Next Review** | YYYY-MM-DD |
| **Mastery** | [🔴 Familiar / 🟡 Competent / 🟢 Expert] |

---

## ⚡ 1. The Core Definition & Trigger
*   **Two-Sentence Trigger:** B-Trees are page-oriented index structures that perform random in-place updates on fixed-size blocks (typically 4KB-16KB), optimizing for low-latency read operations. LSM-Trees (Log-Structured Merge-Trees) accumulate writes in an in-memory MemTable and append them sequentially to immutable SSTables on disk, optimizing for high-velocity write throughput at the cost of background compaction overhead.
*   **Scalability Dimension:** Primary: **Read Latency (B-Trees)** vs. **Write Throughput (LSM-Trees)**. Secondary: **Write/Read Amplification** & **Disk Space Utilization**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### Architectural Deep Dive
| Feature | B-Trees (e.g., PostgreSQL, MySQL) | LSM-Trees (e.g., Cassandra, RocksDB) |
| :--- | :--- | :--- |
| **Write Pattern** | **Random In-Place Writes:** Modifies existing pages on disk. High overhead. | **Sequential Append-Only Writes:** Writes to WAL and MemTable. Flushed sequentially to SSTables. |
| **Read Pattern** | Fast point lookups ($O(\log N)$). Reads exactly 1 page path. | Slow point lookups. Must check MemTable + multiple SSTables (mitigated by Bloom Filters). |
| **Storage Fragmentation** | High fragmentation (empty spaces within pages due to splits/deletions). | Low fragmentation on write; requires background **Compaction** to reclaim space. |
| **Write Amplification** | Double write penalty (writes to WAL + updates page files). | High write amplification due to continuous merging of SSTables during compaction. |

### How LSM-Tree Writes Flow:
1. **MemTable & WAL:** Incoming write is sequentially appended to the Write-Ahead Log (durability) and written to the sorted in-memory **MemTable** (speed).
2. **SSTable Flush:** When the MemTable fills up (e.g., 64MB), it is flushed to disk as an immutable **SSTable (Sorted String Table)**.
3. **Compaction:** Background threads run **Size-Tiered** or **Leveled Compaction** to merge sorted SSTables, resolving duplicate keys and purging deleted rows (tombstones).

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Compaction Backlog / Pending Compaction Bytes`: A growing queue in LSM engines indicates the disk I/O cannot keep up with writes, leading to write stalls.
    *   `Disk I/O Write Saturation`.
*   **Blast Radius (The "Impact"):**
    *   LSM compaction consumes massive disk bandwidth. During peak traffic, compaction runs can saturate disk I/O, causing application write latencies to spike suddenly (write stalls).
*   **Numbers to Know:**
    *   B-Tree Page Size: Default **8KB** in Postgres, **16KB** in InnoDB (MySQL).
    *   LSM Write Latency: Sub-millisecond (sequential memory write + sequential disk append).

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Assuming LSM-Trees do not need a WAL because they write to a MemTable (without a WAL, an LSM-Tree loses all un-flushed MemTable data on power loss).
*   Failing to explain **Tombstones** (when deleting a record in an LSM-tree, you don't delete it; you append a "tombstone" marker that deletes it during compaction).

### Interview Tip (The "Strong Hire" Signal)
> *"For high-velocity time-series ingestion, B-Trees degrade due to random write leaf splits. I will choose Cassandra (LSM-Tree) because writes are purely sequential. To protect reads, we will deploy Bloom Filters on every SSTable shard to ensure we never perform disk I/O on tables that do not contain our target key."*

---

## 💡 5. My Custom Study Notes & Whiteboard

```
LSM-Tree Write Path:
  [Client Write] ──► [Write-Ahead Log (Disk - Sequential)]
         │
         ├──► [MemTable (Sorted RAM)]
                 │
           (When Full)
                 │
                 ▼
         [SSTable Level 0 (Disk - Immutable)] ──┐
                                                ├──► [Compaction Worker] ──► [Level 1 SSTables]
         [SSTable Level 0 (Disk - Immutable)] ──┘
```
