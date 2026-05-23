# ⚡ 10 - MVCC (Multi-Version Concurrency Control)

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C037 |
| **Category** | Database Internals |
| **Difficulty** | 🔥 Hard |
| **Interview Frequency** | 🔥 High |
| **Understanding** | [🔴 None / 🟡 Conceptual / 🟢 Applied] |
| **Can Explain** | [ ] Yes / [ ] No |
| **Whiteboard Drawn** | [ ] Yes / [ ] No |
| **Taught Someone** | [ ] Yes / [ ] No |
| **Next Review** | YYYY-MM-DD |
| **Mastery** | [🔴 Familiar / 🟡 Competent / 🟢 Expert] |

---

## ⚡ 1. The Core Definition & Trigger
*   **Two-Sentence Trigger:** Multi-Version Concurrency Control (MVCC) is a database isolation technique where updates create new versions of records instead of overwriting existing data. This allows readers to access a consistent snapshot of data without acquiring locks, ensuring readers never block writers and writers never block readers.
*   **Scalability Dimension:** Primary: **Concurrency (Read/Write parallel execution)** & **Read Latency**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### How MVCC Works (e.g., PostgreSQL Implementation)
Every row (tuple) contains hidden metadata fields:
*   `xmin`: The Transaction ID (txid) of the transaction that inserted the row.
*   `xmax`: The Transaction ID of the transaction that updated or deleted the row.

When Transaction A updates a row, the database:
1. Sets `xmax` of the original row to Transaction A's ID (marking it deleted for future transactions).
2. Inserts a new row version with `xmin` set to Transaction A's ID.

```
Row Version 1: [Data: "Alice"] [xmin: 100] [xmax: 105] ──► (Seen by txids < 105)
Row Version 2: [Data: "Bob"]   [xmin: 105] [xmax: 0]   ──► (Seen by txids >= 105)
```

### The SDE-2 Challenge: Database Bloat & Vacuuming
Because updates do not overwrite rows, **Dead Tuples** (old versions of rows no longer visible to any active transaction) accumulate.
*   *Postgres Solution:* A background daemon called **Autovacuum** scans tables, reclaims space occupied by dead tuples, and updates statistics.
*   *MySQL Solution:* Uses **Undo Logs** to build historical snapshots on-the-fly and purges them asynchronously.

| Metric | MVCC Snapshot Isolation | Direct Locking (Two-Phase Locking - 2PL) |
| :--- | :--- | :--- |
| **Read Performance** | Ultra-fast. No locks acquired during select. | Slow. Readers acquire shared locks, blocking updates. |
| **Disk/RAM Overhead** | High. Multiple tuple versions or undo logs consume space. | Low. Only active version is stored. |
| **Conflict Handling** | Rollback on concurrent write conflicts (first committer wins). | Transactions block and queue up waiting for locks. |

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Dead Tuple Count / Table Bloat ratio`: High rates indicate Autovacuum is falling behind.
    *   `Transaction Wraparound Risk`: PostgreSQL Transaction ID wraparound (reaching 2 billion transactions without vacuuming) forces the DB into read-only safety mode.
    *   `Long-Running Transactions`: A transaction left open for hours prevents Autovacuum from cleaning *any* dead tuples created after its start, leading to system bloat.
*   **Blast Radius (The "Impact"):**
    *   Table bloat increases sequentially scanned page counts, slowing down queries globally and potentially running the database server out of disk space.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Assuming "Isolation Levels" (like Repeatable Read) prevent writes from conflicting under MVCC (if two transactions update the same row concurrently, one will fail with a serialization conflict).
*   Forgetting to monitor long-running background cron transactions.

### Interview Tip (The "Strong Hire" Signal)
> *"We isolate our OLTP database from table bloat by monitoring long-running transactions. An uncommitted connection blocks MVCC vacuum engines from reclaiming dead tuples. I will configure query timeouts at the application layer and monitor the Postgres `pg_stat_activity` views to kill idle connections."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*
