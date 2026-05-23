# ⚡ 06 - OLTP vs. OLAP

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C042 |
| **Category** | Core Databases |
| **Difficulty** | 🟢 Easy |
| **Interview Frequency** | 🟡 Medium |
| **Understanding** | [🔴 None / 🟡 Conceptual / 🟢 Applied] |
| **Can Explain** | [ ] Yes / [ ] No |
| **Whiteboard Drawn** | [ ] Yes / [ ] No |
| **Taught Someone** | [ ] Yes / [ ] No |
| **Next Review** | YYYY-MM-DD |
| **Mastery** | [🔴 Familiar / 🟡 Competent / 🟢 Expert] |

---

## ⚡ 1. The Core Definition & Trigger
*   **Two-Sentence Trigger:** OLTP (Online Transaction Processing) databases are optimized for low-latency, high-concurrency read/write operations on individual rows (e.g., executing checkout orders). OLAP (Online Analytical Processing) databases are column-oriented data warehouses optimized for complex aggregation queries over millions of rows (e.g., generating annual revenue reports).
*   **Scalability Dimension:** Primary: **Query Throughput (OLTP)** vs. **Data Aggregation Speed / Columnar Scanning (OLAP)**.

---

## ⚖️ 2. Trade-offs & Deep Dive
| Feature | OLTP (e.g., Postgres, MySQL) | OLAP (e.g., Snowflake, ClickHouse) |
| :--- | :--- | :--- |
| **Storage Layout** | Row-oriented. All columns of a row are stored together. | Column-oriented. Values of a single column are stored sequentially. |
| **Query Pattern** | Fast CRUD on specific rows via keys. | Scans billions of rows over a few specific columns. |
| **Latency** | Milliseconds. | Seconds to Minutes. |
| **Data Flow** | Real-time user transactions. | Periodic ingestion (ETL/ELT processes) from OLTP sources. |

*   **Ideal Use Cases:**
    *   User session management, banking ledgers, shopping carts (OLTP).
    *   Business intelligence (BI) dashboards, data warehousing, clickstream analytics (OLAP).
*   **Anti-Patterns / When NOT to use:**
    *   Running reporting aggregates like `SUM(sales)` over billions of rows directly on your OLTP production database (locks tables, causing client checkout failures).

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `ETL Pipeline Latency / SLA Status`: Measures how behind the data warehouse is from real-time events.
    *   `DB Lock durations on OLTP databases`.
*   **Blast Radius (The "Impact"):**
    *   If reporting queries lag in OLAP, business intelligence operations stall. If reporting queries are run on OLTP databases, they can cause thread pools to lock, crashing the application.

---

## 🚫 4. Interview Playbook
*   **Common Mistakes:**
    *   Failing to separate the transactional database (OLTP) from the analytics warehouse (OLAP).
    *   Not explaining *why* columnar storage makes aggregations fast (columnar storage only reads selected columns, bypassing unneeded columns and compressing data heavily).
*   **Interview Tip (The "Strong Hire" Signal):**
    *   State: *"To generate business reports, I will isolate our transactional workload from analytics. We will stream OLTP database changes using CDC (Change Data Capture) via Debezium and Kafka into an OLAP warehouse like Snowflake, protecting our user-facing transactional path."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*
