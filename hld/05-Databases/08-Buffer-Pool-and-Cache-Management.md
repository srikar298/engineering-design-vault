# ⚡ 08 - Buffer Pool & Cache Management

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C035 |
| **Category** | Database Internals |
| **Difficulty** | 🟡 Medium |
| **Interview Frequency** | 🟡 Medium |
| **Understanding** | [🔴 None / 🟡 Conceptual / 🟢 Applied] |
| **Can Explain** | [ ] Yes / [ ] No |
| **Whiteboard Drawn** | [ ] Yes / [ ] No |
| **Taught Someone** | [ ] Yes / [ ] No |
| **Next Review** | YYYY-MM-DD |
| **Mastery** | [🔴 Familiar / 🟡 Competent / 🟢 Expert] |

---

## ⚡ 1. The Core Definition & Trigger
*   **Two-Sentence Trigger:** The Buffer Pool is a database engine's primary in-memory cache that caches database table pages and indices read from disk. It manages read-ahead scheduling and asynchronous flushing of modified "dirty" pages to disk, minimizing physical storage I/O and boosting query performance.
*   **Scalability Dimension:** Primary: **Disk I/O Offloading** & **Memory Access Speeds**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### Core Architecture Components:
1. **Buffer Frame:** The memory slot that holds exactly one database page.
2. **Page Table:** An in-memory hash map mapping `page_id` to its physical frame location in the buffer pool.
3. **Dirty Pages:** Pages that have been updated in memory but not yet written to disk. The database uses a background thread (page cleaner/writer) to flush dirty pages asynchronously.
4. **Pinning:** When an active database thread reads or writes to a page, it "pins" (locks) the page frame, preventing the eviction thread from swapping it out.

### The Scan Pollution Problem & Eviction Algorithms
Standard Least Recently Used (LRU) evicts active pages if a user executes a sequential table scan (e.g., `SELECT * FROM huge_table`). Production databases implement specialized algorithms to protect hot pages:

| Eviction Algorithm | Mechanism | Used By |
| :--- | :--- | :--- |
| **Clock Sweep (Approximated LRU)** | Tracks access bits in a circular queue. Hands sweep to check and decrement access bits. | PostgreSQL |
| **Midpoint Insertion LRU** | Splits the LRU chain into a "New" list (3/8ths) and an "Old" list (5/8ths). New pages insert at the midpoint. | MySQL (InnoDB) |
| **2Q (Two Queue)** | Uses two queues: FIFO queue for page misses, and standard LRU queue for active hits. | RocksDB |

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Buffer Pool Hit Ratio`: Target is > 99%. Drops indicate the working set exceeds RAM capacity.
    *   `Dirty Page Ratio`: If dirty pages exceed thresholds (e.g., 75%), MySQL/Postgres starts synchronous flushing, blocking application writes.
*   **Blast Radius (The "Impact"):**
    *   If the database crashes, all unwritten data in dirty pages is lost. The database must run recovery protocols on restart, reading the Write-Ahead Log (WAL) to "redo" lost modifications.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Assuming buffer pool flushing is synchronous on every update (writes are written to memory and the WAL instantly, but data files are updated asynchronously).
*   Configuring the buffer pool size too small (on dedicated DB servers, the buffer pool should occupy 70%-80% of system RAM).

### Interview Tip (The "Strong Hire" Signal)
> *"To ensure optimal read performance, I will configure our database buffer pool (e.g., InnoDB Buffer Pool) to occupy roughly 80% of our system's memory. When optimization queries are run, we will monitor our Clock Sweep sweep rates to prevent scan pollution from flushing active transaction contexts."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*
