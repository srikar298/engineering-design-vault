# ⚡ 01 - SQL vs. NoSQL Decision

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C031 |
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
*   **Two-Sentence Trigger:** The SQL vs. NoSQL decision is the fundamental choice between relational databases with rigid schemas and ACID guarantees (e.g., PostgreSQL) and non-relational databases designed for flexible, schema-less data structures and horizontal scaling (e.g., MongoDB, DynamoDB). It is dictated by consistency requirements, write-to-read ratios, and query complexity.
*   **Scalability Dimension:** Relational scales vertically easily (horizontal requires sharding). NoSQL scales horizontally natively (partition keys).

---

## ⚖️ 2. Trade-offs & Deep Dive
| Dimension | Relational (SQL) | Non-Relational (NoSQL) |
| :--- | :--- | :--- |
| **Schema** | Rigid (Schema-on-write). Schema changes are painful. | Flexible (Schema-on-read). Attributes can be added dynamically. |
| **Transactions** | Strong ACID guarantees (highly consistent). | BASE (Eventual consistency) or limited single-row transactions. |
| **Queries** | Complex Joins, relational queries, structured lookups. | Key-value or simple queries. Application-side joins required. |
| **Ideal Workloads** | Financial ledgers, CRM, complex relational entities. | IoT streams, catalog management, massive write throughput. |

*   **Ideal Use Cases:**
    *   E-commerce transactional ledgers (SQL).
    *   Dynamic catalog indexing or massive chat history collections (NoSQL).
*   **Anti-Patterns / When NOT to use:**
    *   Choosing MongoDB "just because it's fast" for an application requiring dozens of complex user-table JOINs (ends up slower due to application-side join network trips).

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Database CPU/IO utilization`: Identifies query processing saturation.
    *   `Active/Idle connections`: Indicates scaling thresholds.
*   **Blast Radius (The "Impact"):**
    *   Relational DB exhaustion halts all writes/reads across the entire system. NoSQL partition locks affect only specific keys/shards.
*   **Numbers to Know:**
    *   Typical SQL write latency: **5 - 15 ms**
    *   NoSQL write latency (single-row index): **1 - 5 ms**

---

## 🚫 4. Interview Playbook
*   **Common Mistakes:**
    *   Stating NoSQL is "faster" without analyzing access patterns.
    *   Ignoring write amplification on indices in SQL.
*   **Interview Tip (The "Strong Hire" Signal):**
    *   State: *"I will default to PostgreSQL due to its ACID maturity and strong schema validation. If we find that our write throughput exceeds our horizontal scaling boundaries or our data model becomes highly polymorphic, we will migrate those specific high-velocity sub-domains to a column-family store like Cassandra."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*
