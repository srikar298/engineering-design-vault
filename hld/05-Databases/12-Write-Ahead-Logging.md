# ⚡ 12 - Write-Ahead Logging (WAL)

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C036 |
| **Category** | Database Internals |
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
*   **Two-Sentence Trigger:** Write-Ahead Logging (WAL) is a protocol that ensures transaction durability and atomicity by logging database modifications sequentially to an append-only file on disk before writing them to the actual table page files. In the event of a system crash, the database engine reads the WAL to replay committed changes (Redo) and roll back uncommitted ones (Undo).
*   **Scalability Dimension:** Primary: **Write Throughput (QPS)** & **Transaction Durability**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### The Transaction Commit Sequence:
1. **Application Write:** Client initiates a write query.
2. **Buffer Log:** The DB updates the page in the Buffer Pool and writes the change to the in-memory **WAL Buffer**.
3. **Commit & Fsync:** The client issues `COMMIT`. The DB synchronously writes the WAL buffer to the physical disk WAL file and executes an `fsync()` system call. The client receives write success.
4. **Asynchronous Checkpoint:** Later, a background checkpoint process writes the modified "dirty" pages from the Buffer Pool to the primary database table files on disk.

### Sequential vs. Random disk I/O
*   Writing to database table page files involves random disk I/O (updating values on different pages across physical disk sectors), which is slow.
*   Writing to the WAL involves **sequential I/O** (appending to the end of a single file), which is extremely fast.

### Fsync Configuration (The Durability vs. Latency Trade-off)
*   **`fsync = ON` (Default):** Synchronously flushes WAL to disk per transaction. Maximum durability, but write latency is limited by disk sync speeds.
*   **`fsync = OFF` (Async Commit):** Writes to WAL buffer but returns success immediately. If the server loses power, recent transactions are lost, but write throughput rises dramatically.

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Disk Write Latency`: High latency indicates bottlenecks in writing to the WAL disk partition.
    *   `LSN (Log Sequence Number) Drift`: Comparing the LSN between the Master and Replicas. Growing drift indicates replication lag.
*   **Blast Radius (The "Impact"):**
    *   If the WAL disk partition runs out of space, the database engine immediately halts all write queries and shuts down to prevent data corruption.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Assuming writes are written to the database table files immediately on commit (they reside in the WAL and Buffer Pool, and are flushed later).
*   Not separating the physical disk storage of the WAL from the main database table files (having them on the same physical disk creates I/O head competition).

### Interview Tip (The "Strong Hire" Signal)
> *"For high-performance databases, I will isolate the Write-Ahead Log (WAL) on a dedicated, high-performance NVMe SSD partition separated from the table data files. This ensures that sequential WAL writes never compete for disk I/O heads with random read/write page flushes."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*
