# ⚡ 04 - Data Warehousing (BigQuery & Snowflake)

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C108 |
| **Category** | Object Storage & Big Data |
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
*   **Two-Sentence Trigger:** Modern Cloud Data Warehousing refers to distributed, serverless, or decoupled compute-and-storage analytical database architectures designed to run SQL queries over petabytes of data. It is triggered when database storage and analytical query volumes exceed the limits of traditional relational databases (like Postgres/MySQL), choosing Snowflake or BigQuery to execute Massive Parallel Processing (MPP) analytical aggregations without hardware constraints.
*   **Scalability Dimension:** Primary: **Decoupled Compute and Storage Scaling vs. Query Scanning Costs**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### Decoupled Compute & Storage Architecture (Snowflake Model)
The key architectural innovation of modern cloud data warehouses is the separation of storage and computation:
```
  [ Virtual Compute Warehouse A (ETL) ] ──────┐
  [ Virtual Compute Warehouse B (BI Users) ] ─┼─► [ Centralised Storage (S3 / GCS) ]
  [ Virtual Compute Warehouse C (Finance) ] ──┘   (Immutable Columnar Micro-partitions)
```
*   **The Storage Layer:** Immutable, metadata-indexed, columnar micro-partitions stored in cheap cloud object storage (S3/GCS).
*   **The Compute Layer:** Independent virtual machine clusters (virtual warehouses). You can spin up an "Extra Large" compute cluster to run a heavy ETL job, and shut it down when done, without affecting the performance of BI reporting users running on a "Small" cluster.

---

### BigQuery vs. Snowflake Comparison
| Feature | Snowflake (Multi-Cluster Shared Data) | Google BigQuery (Serverless Shared Pool) |
| :--- | :--- | :--- |
| **Architecture** | Virtual Warehouses (user manages cluster sizing). | Completely Serverless (Google manages slot execution). |
| **Billing Model** | Pay for compute time (credits per hour) + storage. | Pay for bytes scanned per query (or flat slot capacity). |
| **Concurrency** | 🟢 Auto-scales by spinning up duplicate compute warehouses. | 🟢 Scales by allocating Google slot workers dynamically. |
| **Cold Starts** | 🔴 Low delay when waking suspended warehouses. | 🟢 Instant. Shared worker pool is always warm. |
| **Best For** | Multi-department enterprises requiring compute isolation. | Ad-hoc queries, ML integration, and real-time streaming ingestion. |

---

### Optimization Techniques: Partitioning vs. Clustering
To prevent queries from scanning full petabyte-scale tables (which increases cost and latency):
1. **Partitioning:** Splitting a table based on a column value (typically a date). E.g., partitioning by `created_date` ensures a query searching for last week's data only scans the directories corresponding to those 7 days.
2. **Clustering:** Sorting data *within* partitions based on specific columns (e.g., sorting by `user_id`). This creates metadata boundary limits:
```
  Micro-partition 1: Min UserID: 100, Max UserID: 500  (Scan only if query falls in range)
  Micro-partition 2: Min UserID: 501, Max UserID: 999
```

---

## 💥 3. Resiliency & Operations

### Operational Pitfalls & Mitigations
*   **Runaway Query Costs (The BigQuery Shock):**
    *   *Problem:* A junior analyst runs `SELECT * FROM massive_table` on an unpartitioned 50 Terabyte table. In BigQuery, this scans the entire table, instantly costing **$250** for a single query.
    *   *Mitigation:* Configure BigQuery **Query Limits** (e.g., maximum bytes billed per query) at the user group level, enforce partitioned query policies (rejecting queries that do not contain a partitioning filter), and use materialized views to cache common aggregations.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Recommending a data warehouse for OLTP point queries (e.g., lookup username where ID = 123). Warehouses are columnar and optimized for scans; point queries have high latency (often 1-2 seconds) and waste compute credits.
*   Not explaining the separation of compute and storage. This is the single most important architectural detail of Snowflake/BigQuery.

### Interview Tip (The "Strong Hire" Signal)
> *"We manage our analytical queries by leveraging Snowflake's decoupled compute and storage architecture. We store our master data as compressed micro-partitions in S3, and spin up isolated virtual warehouses for different workloads: a large cluster for our nightly ETL imports, and a small, auto-scaling cluster for our BI dashboards. This ensures our ETL data writes never consume resources or cause latency spikes for our business reporting users."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*
