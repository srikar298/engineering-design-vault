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

## ⚖️ 2. Trade-offs & Deep Dive
| Optimization Strategy | Pros | Cons |
| :--- | :--- | :--- |
| **Adding Composite Indices** | Drastically speeds up specific queries containing multiple filter columns. | Slows down writes; index ordering is highly specific (order of fields matters). |
| **De-normalizing (Data Duplication)** | Eliminates complex table joins, resolving read bottlenecks. | Increases write complexity to maintain synchronization. |
| **Query Rewriting / CTEs** | Simplifies queries, isolates intermediate data sets. | Poorly written CTEs in older DB engines acts as optimization barriers, slowing execution. |

*   **Ideal Use Cases:**
    *   Reducing latency on high-frequency API endpoints that query relational databases.
*   **Anti-Patterns / When NOT to use:**
    *   The **N+1 Query Pattern**: Querying a parent table once, then executing a separate query for each child row in a loop.
    *   *Solution:* Use eager loading (Join queries) or batching.

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Slow Query Logs`: Database logging thresholds (e.g., log any query taking > 100ms).
    *   `Database locks and transaction durations`.
*   **Blast Radius (The "Impact"):**
    *   A single un-optimized query executing at high QPS can lock up database tables, block pool connections, and trigger a complete service outage.

---

## 🚫 4. Interview Playbook
*   **Common Mistakes:**
    *   Optimizing queries blindly without running `EXPLAIN ANALYZE` to find the actual bottlenecks (e.g., nested loops vs hash joins).
    *   Selecting `SELECT *` instead of specific columns (wastes network bandwidth and prevents the DB from using covering index scans).
*   **Interview Tip (The "Strong Hire" Signal):**
    *   Narrate the optimization loop: *"To resolve slow DB performance, I will examine the execution plan using `EXPLAIN ANALYZE`. I will check if the query planner is forcing a Sequential Scan due to missing indices, incorrect join order, or function wrapping on indexed columns."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*
