# ⚡ 04 - Database Sharding

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C063 |
| **Category** | Database Scaling |
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
*   **Two-Sentence Trigger:** Database Sharding is a horizontal database scaling strategy where data is partitioned across multiple physical database server nodes (shards). It divides write workload and storage across independent physical machines, allowing a system to scale writes beyond the capacity of a single master node.
*   **Scalability Dimension:** Primary: **Write Throughput (QPS)** & **Storage Capacity (horizontal disk scaling)**.

---

## ⚖️ 2. Trade-offs & Deep Dive
| Sharding Strategy | Pros | Cons |
| :--- | :--- | :--- |
| **Key-Based (Hash Sharding)** | Uniform data distribution, avoids hot-spots. | Re-sharding is complex. Cross-shard joins are impossible. |
| **Range-Based Sharding** | Simple routing, allows efficient range queries within a shard. | Creates hot-spots (e.g., active current data concentrated on one shard). |
| **Directory-Based Sharding** | Flexible; moving rows or shards is easy via metadata registry. | The directory registry is a single point of failure and adds network latency. |

*   **Ideal Use Cases:**
    *   Massive, high-scale applications (e.g., chat apps, social networks, payment processors) where writes exceed a single database master node's capacity.
*   **Anti-Patterns / When NOT to use:**
    *   Small systems that can easily scale writes via vertical hardware upgrades or scale reads via read-replicas.

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Shard CPU/IO utilization imbalances`: Identifies hot spots.
    *   `Cross-shard query frequency`: Monitoring queries that span multiple shards (scatter-gather queries).
*   **Blast Radius (The "Impact"):**
    *   If a shard node crashes, all users mapped to that shard experience outage states. A misconfigured partition key can route all traffic to a single shard, crashing it (The Celebrity Problem).
*   **Logical Architecture:**
    ```
    Application ──> [Routing Layer (Vitess/Proxy)]
                                 │
                   ┌─────────────┼─────────────┐
                   ▼             ▼             ▼
              [Shard 1]     [Shard 2]     [Shard 3] (Independent physical servers)
    ```

---

## 🚫 4. Interview Playbook
*   **Common Mistakes:**
    *   Selecting a bad **Shard Key** (e.g., sharding by date or tenant ID, which creates hot nodes).
    *   Failing to explain how to join sharded tables (joins must be executed in application memory, which is slow).
*   **Interview Tip (The "Strong Hire" Signal):**
    *   Solve the Celebrity Problem: *"To handle hot-spotting for popular keys, I will implement **Shard Key Salting**—appending a random prefix (e.g., `user123_1`, `user123_2`) to the partition key for massive users, spreading their write load uniformly across multiple physical shards."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*
