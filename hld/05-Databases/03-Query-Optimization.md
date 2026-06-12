# ⚡ 03 - Query Optimization

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C039 |
| **Category** | Core Databases |
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
*   **Two-Sentence Trigger:** Query Optimization is the process of improving the performance of database queries by analyzing execution plans (e.g., via `EXPLAIN ANALYZE`). It aims to replace expensive operations like sequential scans or nested loop joins with index scans, hash joins, and efficient sub-queries.
*   **Scalability Dimension:** Primary: **Query Latency (P99)** & **Database CPU / RAM Consumption**.

---

## ⚖️ 2. Core Optimization Strategies
| Optimization Strategy | How it Works | Best For | Trade-off / Risk |
| :--- | :--- | :--- | :--- |
| **Composite Indexing** | Creating an index spanning multiple columns using the Left-Prefix Rule. | Highly specific, high-frequency read queries. | High write amplification; sensitive to column ordering. |
| **Materialized Views** | Pre-computing and storing the physical results of an expensive query/join. | Dashboards, heavy analytical aggregations. | Stale data. Requires manual or triggered refreshes. |
| **De-normalization** | Duplicating data across tables to eliminate `JOIN` operations. | Resolving extreme read bottlenecks on complex graphs. | Increases application-side write complexity to keep duplicated data in sync. |
| **Keyset (Cursor) Pagination** | Paginating using `WHERE id > last_id` instead of `OFFSET X LIMIT Y`. | Massive tables where deep pagination causes timeouts. | You cannot jump directly to "Page 10" (must traverse sequentially). |

---

## 🧠 3. Advanced Execution Plan Mechanics (SDE-3 Level)

### 1. Cost-Based Optimizers (CBO)
Modern databases (like [PostgreSQL](../24-components-library/01-Databases/SQL/L001-PostgreSQL/README.md)) do not just blindly execute your SQL. They pass it through a Cost-Based Optimizer, which uses internal statistics (row counts, data distribution histograms) to calculate the "cheapest" physical path to fetch your data. If you don't run `ANALYZE` (or let Autovacuum run it), the optimizer's statistics get stale, and it will choose terrible execution plans.

### 2. The 3 Join Algorithms (Crucial Interview Knowledge)
If you join `Users` and `Orders`, the query planner must pick one of three physical algorithms:
*   **Nested Loop Join:** Iterates through every row in Table A, and for each row, scans Table B.
    *   *Best for:* Very small datasets, or when Table B has a highly optimized index on the join key.
*   **Hash Join:** Builds an in-memory Hash Table of the smaller table, then scans the larger table, probing the hash table for matches.
    *   *Best for:* Large datasets without sorted indexes. It requires enough `work_mem` to fit the smaller table entirely in RAM.
*   **Merge Join:** Both tables must be pre-sorted by the join key (usually via an index). It then zips them together in a single pass.
    *   *Best for:* Massive datasets where both sides are already indexed/sorted. Highly efficient but requires sorting overhead if no index exists.

### 3. SARGability (Search Argument Able)
A query is SARGable if the database engine can actually use the index to fulfill it. If you apply a function to the indexed column, the database is forced to perform a Full Table Scan because the index tree only holds raw values, not computed values.
*   🚫 **Bad (Not SARGable):** `SELECT * FROM users WHERE YEAR(created_at) = 2023;` (Forces full table scan, calculating `YEAR()` on every row).
*   ✅ **Good (SARGable):** `SELECT * FROM users WHERE created_at >= '2023-01-01' AND created_at < '2024-01-01';` (Instantly traverses the B-Tree index).

### 4. The Offset Pagination Death Trap
*   🚫 **The Problem:** `SELECT * FROM orders ORDER BY date DESC OFFSET 1000000 LIMIT 50;`
    The database literally has to compute, sort, and fetch the first 1,000,000 rows, throw them away, and return the next 50. This crushes database CPU.
*   ✅ **The Solution (Keyset/Cursor):** `SELECT * FROM orders WHERE id < last_seen_id ORDER BY id DESC LIMIT 50;`
    This uses the B-Tree index to jump instantly to `last_seen_id` in $O(\log N)$ time and read 50 rows sequentially.

---

## 💥 4. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Slow Query Logs`: Database logging thresholds (e.g., log any query taking > 100ms).
    *   `pg_stat_statements`: Postgres extension that tracks execution statistics of all SQL statements, revealing cumulative time spent.
*   **Blast Radius (The "Impact"):**
    *   A single un-optimized query executing at high QPS (like an un-indexed `ORDER BY`) will consume all CPU cores, queuing other legitimate queries, exhausting connection pools (PgBouncer), and taking down the entire API.

---

## 🚫 5. Interview Playbook
*   **Common Mistakes:**
    *   Blaming the "database is slow" without pulling an `EXPLAIN ANALYZE` to see if a Nested Loop Join or Sequential Scan was triggered.
    *   Selecting `SELECT *` instead of specific columns. This wastes network bandwidth, thrashes the `shared_buffers` memory pool, and entirely prevents the use of Covering Indexes (Index-Only Scans).
*   **Interview Tip (The "Strong Hire" Signal):**
    *   Narrate the optimization loop: *"If this microservice experiences latency spikes, I will first check `pg_stat_statements` to identify the specific query. I'll run `EXPLAIN ANALYZE` to check the execution plan. If the Cost-Based Optimizer is choosing a Sequential Scan over a Hash Join, I'll verify if statistics are stale, if `work_mem` is sufficient, or if the developer inadvertently broke SARGability by wrapping the `WHERE` clause in a function."*

---

## 💡 6. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*
