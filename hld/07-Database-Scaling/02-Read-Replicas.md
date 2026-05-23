# ⚡ 02 - Read Replicas

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C061 |
| **Category** | Database Scaling |
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
*   **Two-Sentence Trigger:** Read Replicas are secondary database instances that receive asynchronous updates from the primary master database. They are used to offload read queries from the master, scaling read capacity horizontally while keeping the master free to handle writes.
*   **Scalability Dimension:** Primary: **Read Throughput (QPS)**. Secondary: **Master Database Load Reduction**.

---

## ⚖️ 2. Trade-offs & Deep Dive
| Asynchronous Replication | Synchronous Replication |
| :--- | :--- |
| **Asynchronous:** Master commits write locally, then sends update to replicas in background. | **Synchronous:** Master waits for at least one replica to commit before writing success. |
| *Pros:* Zero impact on write latency. Master writes are fast. | *Pros:* Replicas are always up to date. Zero replication lag. |
| *Cons:* Replicas lag behind master. Reads can be stale (Eventual Consistency). | *Cons:* Write latency is bounded by the slowest network link to replicas. If replica dies, writes block. |

*   **Ideal Use Cases:**
    *   Read-heavy applications (e.g., social media feeds, blog post reads, reporting dashboards).
*   **Anti-Patterns / When NOT to use:**
    *   Transactional read-modify-write loops (reading from a stale replica will overwrite data incorrectly).

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Replication Lag Byte/Time Offset`: Indicates replica delay.
    *   `Replica Read IOPS capacity limit`.
*   **Blast Radius (The "Impact"):**
    *   If all replicas crash, traffic cascades back to the master database node, instantly overloading its CPU and taking the entire application offline.
*   **Sequence Diagram:**
    ```
    Client ──(Write)──> Master DB ──(Sync Commit)──> Success
                            │
                  (Async replication log stream)
                            │
                            ▼
                         Replica DB <──(Read)── Client
    ```

---

## 🚫 4. Interview Playbook
*   **Common Mistakes:**
    *   Using read replicas to scale write-heavy applications (since replicas duplicate all writes, they experience write exhaustion alongside the master).
    *   Not configuring connection routing (you must explicitly configure your app code or routing proxy like PgBouncer/ProxySQL to send reads to replicas and writes to master).
*   **Interview Tip (The "Strong Hire" Signal):**
    *   Discuss session pinning: *"To prevent a poor user experience where a user submits a post but doesn't see it on redirect, I will pin the user's session to the master database for 10-15 seconds for read requests immediately after a write."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*
