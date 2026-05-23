# ⚡ 04 - Denormalization

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C040 |
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
*   **Two-Sentence Trigger:** Denormalization is the database optimization technique of adding redundant data or grouping data from multiple tables into a single table to eliminate complex `JOIN` operations. It trading write complexity and storage capacity for maximum read performance.
*   **Scalability Dimension:** Primary: **Read Latency (sub-millisecond reads of composite models)**. Secondary: **Storage Consumption** & **Write Latency / Consistency Challenges**.

---

## ⚖️ 2. Trade-offs & Deep Dive
| Normalized (3NF) | Denormalized |
| :--- | :--- |
| **Normalized:** Data is stored once. No duplication. | **Denormalized:** Redundant data is duplicated across rows/collections. |
| *Pros:* Data integrity is guaranteed. Low write complexity. Low storage. | *Pros:* Extremely fast reads ($O(1)$ scans, no joins). |
| *Cons:* Slow reads due to multiple `JOIN` operations. | *Cons:* Higher risk of data anomalies. Multi-table writes required on update. |

*   **Ideal Use Cases:**
    *   Read-heavy dashboards or feed generation platforms where join operations exceed performance limits.
    *   NoSQL databases (where Joins are unsupported natively).
*   **Anti-Patterns / When NOT to use:**
    *   Dynamic datasets where duplicated values change constantly (forces cascade writes, risking data inconsistencies).

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Data Consistency Auditor Tasks`: Background workers scanning database tables to detect drift between source tables and denormalized views.
    *   `DB Write IOPs spikes`.
*   **Blast Radius (The "Impact"):**
    *   If a write fails partially, the system falls into a **Split-Brain consistency state** where some views display updated data and others display stale historical data.

---

## 🚫 4. Interview Playbook
*   **Common Mistakes:**
    *   Denormalizing data without describing the sync strategy (e.g., how the duplicated data is kept up to date—async event queue, transactional batch, or cron updates).
    *   Assuming denormalization is the first step (it should only be used when caching and indexing fail to meet read SLAs).
*   **Interview Tip (The "Strong Hire" Signal):**
    *   Discuss synchronization: *"I will denormalize the user profile data into our order table to eliminate reads joins. To keep this updated, when a user changes their username, our User service publishes a `UserUpdated` event to Kafka, which our Order service consumes to update its local rows asynchronously."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*
