# ⚡ 11 - Query Optimizer & Execution Plans

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C038 |
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
*   **Two-Sentence Trigger:** The Query Optimizer is a database component that selects the most efficient execution plan for a SQL query from many alternative strategies. It utilizes a Cost-Based Optimizer (CBO) model, calculating estimated CPU and Disk I/O costs using database statistics (table sizes, indexing, and data distribution histograms).
*   **Scalability Dimension:** Primary: **Query Execution Efficiency (CPU & Disk I/O reduction)**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### Execution Plan Scan Operations
1. **Sequential Scan (Seq Scan):** Scans every page of the table. Fast for small tables, but scales poorly ($O(N)$) for large datasets.
2. **Index Scan:** Traverses index tree nodes ($O(\log N)$) to find row IDs, then fetches matching pages from table files.
3. **Index Only Scan:** Satisfies the query entirely from the index tree leaf nodes, skipping data page lookups.
4. **Bitmap Index & Heap Scan:** Combines multi-row index match pointers in memory, sorts them by physical disk location, and reads data sequentially to minimize random I/O.

### Join Types (The SDE-2 Must-Knows)
| Join Algorithm | How it works | Best For | Cost Profile |
| :--- | :--- | :--- | :--- |
| **Nested Loop Join** | For each row in Table A, search Table B. | Small datasets, or queries matching high-selectivity indices. | $O(M \times \log N)$ with index. |
| **Hash Join** | Hashes Table A keys in memory. Scans Table B to match hashes. | Large, unsorted datasets. | $O(M + N)$ (requires RAM for the hash table). |
| **Merge Join** | Sorts both tables by join keys, then merges sequentially. | Large sorted datasets, or when range queries exist. | $O(M \log M + N \log N)$ for sorting. |

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `EXPLAIN (ANALYZE, BUFFERS)`: The gold standard metric for Postgres. Compares estimated plan costs against actual run times and disk pages read from the buffer pool.
    *   `Outdated Statistics`: If a table receives massive changes without running `ANALYZE` (statistics update), the optimizer can choose bad plans (e.g., using a sequential scan over a fast index scan).
*   **Blast Radius (The "Impact"):**
    *   An optimizer selecting a sequential scan on a 100M-row table due to outdated statistics will saturate the disk I/O, queuing database connections and crashing client-facing APIs.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Believing Postgres CTEs (`WITH` clauses) are always optimization barriers (modern Postgres version 12+ optimizes CTEs in-line by default unless the `NOT MATERIALIZED` clause is explicitly overridden).
*   Failing to run database stats maintenance tasks (`ANALYZE`).

### Interview Tip (The "Strong Hire" Signal)
> *"If our database queries degrade suddenly, I will run `EXPLAIN (ANALYZE, BUFFERS)` to trace the plan tree. I will check for **Hash Joins falling back to Disk** (due to exceeding `work_mem` limits) or bad scan paths triggered by stale planner catalog statistics, and trigger manual table analysis or parameter sizing updates."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*
