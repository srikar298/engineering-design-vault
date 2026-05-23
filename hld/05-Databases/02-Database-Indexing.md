# ⚡ 02 - Database Indexing

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C032 |
| **Category** | Core Databases |
| **Difficulty** | 🟢 Easy |
| **Interview Frequency** | 🔥 High |
| **Understanding** | [🔴 None / 🟡 Conceptual / 🟢 Applied] |
| **Can Explain** | [ ] Yes / [ ] No |
| **Whiteboard Drawn** | [ ] Yes / [ ] No |
| **Taught Someone** | [ ] Yes / [ ] No |
| **Next Review** | YYYY-MM-DD |
| **Mastery** | [🔴 Familiar / 🟡 Competent / 🟢 Expert] |

---

## ⚡ 1. The Core Definition & Trigger
*   **Two-Sentence Trigger:** Database Indexing is the creation of auxiliary data structures (such as B-Trees or Hash tables) on database columns to speed up data retrieval. While indices reduce read time from $O(N)$ sequential table scans to $O(\log N)$ tree lookups, they introduce write amplification and consume storage.
*   **Scalability Dimension:** Primary: **Read Latency** & **Query Throughput**. Secondary: **Write Latency Degradation**.

---

## ⚖️ 2. Trade-offs & Deep Dive
| Index Type | How it works | Best For | Trade-off |
| :--- | :--- | :--- | :--- |
| **B-Tree** | Self-balancing tree structure. Keeps data sorted. | Range queries, sorting, inequalities (`<`, `>`). | Balanced, but occupies storage. (Default). |
| **Hash Index** | Computes hash value of column data. | Exact match lookups (`=`). | Cannot perform range queries or sort operations. |
| **GIN (Inverted)** | Maps composite values to row IDs. | Full-text search, JSONB documents. | High write amplification on update/insert. |

*   **Ideal Use Cases:**
    *   Columns heavily queried in `WHERE` clauses, join keys (foreign keys), or sort columns (`ORDER BY`).
*   **Anti-Patterns / When NOT to use:**
    *   Columns with low selectivity (e.g., boolean values like `is_active` where scanning the table is faster than traversing the index tree and fetching pages).

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Sequential Scan vs Index Scan Ratio`: High sequential scan counts on large tables indicate missing indices.
    *   `Write Amplification Factor`: Spikes in DB write IOPs relative to application updates indicate too many indices on a table.
*   **Blast Radius (The "Impact"):**
    *   Adding an index on a multi-million-row production table blocks writes and locks tables if not run concurrently (e.g., using `CREATE INDEX CONCURRENTLY` in Postgres).

---

## 🚫 4. Interview Playbook
*   **Common Mistakes:**
    *   Indexing every column in a table thinking it makes the DB faster (every index slows down inserts/updates because the index structures must be modified).
    *   Not knowing that indices should fit in RAM (if the index exceeds memory limits, the DB thrashes the disk).
*   **Interview Tip (The "Strong Hire" Signal):**
    *   Mention index locking: *"When adding indices to production systems, I will always use `CONCURRENTLY` modifiers in Postgres. This prevents the index creation from acquiring a ShareShareLock, which blocks concurrent writes."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*
