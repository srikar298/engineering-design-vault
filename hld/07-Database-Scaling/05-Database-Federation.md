# ⚡ 05 - Database Federation

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C064 |
| **Category** | Database Scaling |
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
*   **Two-Sentence Trigger:** Database Federation (also known as Functional Splitting) is the process of splitting a single monolithic database into separate, independent databases based on functional domains (e.g., separating the DB into User DB, Order DB, and Inventory DB). It isolates database workloads by domain, allowing team autonomy and preventing a bottleneck in one domain from affecting others.
*   **Scalability Dimension:** Primary: **System Modularity / Workload Isolation** & **Write Throughput**. Secondary: **Read Joins Latency (cross-database joins)**.

---

## ⚖️ 2. Trade-offs & Deep Dive
| Monolithic Database | Federated Databases |
| :--- | :--- |
| **Monolithic:** One database stores all system tables. | **Federated:** Separate database engines manage separate tables. |
| *Pros:* Simple joins, transactional integrity (ACID) across all tables. | *Pros:* Scale individual databases independently. Fault isolation. |
| *Cons:* Single point of failure. Scaling resource competition. | *Cons:* Cross-domain joins are impossible natively. Distributed transaction complexity. |

*   **Ideal Use Cases:**
    *   Transitioning from a monolithic architecture to microservices where domain boundaries are clear.
*   **Anti-Patterns / When NOT to use:**
    *   Splitting databases when tables still share tight relational integrity (e.g., splitting a table from its primary foreign-key target when joins are continuously required).

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Cross-Database network query latency`.
    *   `API Gateway dependency graph errors`.
*   **Blast Radius (The "Impact"):**
    *   If the Federated User DB crashes, order creation fails if it synchronously validates user IDs. Workload separation reduces the blast radius compared to a monolithic crash, as inventory query reads remain functional.

---

## 🚫 4. Interview Playbook
*   **Common Mistakes:**
    *   Doing functional splitting too early when relational boundaries are blurry.
    *   Using synchronous REST calls to join data across federated databases (use asynchronous replication or event-driven models instead).
*   **Interview Tip (The "Strong Hire" Signal):**
    *   State: *"I will federate our database by domain boundaries to prevent query contention. To handle cross-domain data dependencies, we will replicate reference data asynchronously using Kafka, avoiding blocking network joins at read-time."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*
