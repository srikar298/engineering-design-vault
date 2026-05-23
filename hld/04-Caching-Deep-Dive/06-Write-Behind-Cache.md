# ⚡ 06 - Write-Behind Cache

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C018 |
| **Category** | Caching Strategy |
| **Difficulty** | 🟡 Medium |
| **Interview Frequency** | 🟡 Medium |
| **Understanding** | [🔴 None / 🟡 Conceptual / 🟢 Applied] |
| **Can Explain** | [ ] Yes / [ ] No |
| **Whiteboard Drawn** | [ ] Yes / [ ] No |
| **Taught Someone** | [ ] Yes / [ ] No |
| **Next Review** | YYYY-MM-DD |
| **Mastery** | [🔴 Familiar / 🟡 Competent / 🟢 Expert] |

---

## ⚡ 1. The Core Definition & Trigger
*   **Two-Sentence Trigger:** Write-Behind Cache (also called Write-Back) is a pattern where writes are written directly to the cache, which acknowledges the update immediately to the client. The cache then asynchronously flushes the writes to the database in background threads, typically batched or coalesced.
*   **Scalability Dimension:** Primary: **Write Throughput (QPS)** & **Write Latency**.

---

## ⚖️ 2. Trade-offs & Deep Dive
| Write-Behind | Write-Through |
| :--- | :--- |
| **Write-Behind:** Asynchronously flushes updates from cache to DB in background. | **Write-Through:** Synchronously updates cache and DB in one transaction block. |
| *Pros:* Sub-millisecond write times. Completely buffers the database from high write spikes. | *Pros:* High data integrity. No risk of data loss on server crash. |
| *Cons:* Risk of data loss if the cache server crashes before flushing dirty keys to DB. | *Cons:* Slow write speeds. High DB write pressure. |

*   **Ideal Use Cases:**
    *   High-frequency event telemetry (e.g., IoT trackers, clickstream data).
    *   Real-time system state accumulation (e.g., user profiles or video progress bars).
*   **Anti-Patterns / When NOT to use:**
    *   Strict transactional operations (e.g., banking transactions, inventory reservation).

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Dirty Page Count / Flush Queue Size`: Monitor how many writes are pending DB persistence. A growing queue indicates DB write bottlenecks.
    *   `Cache Instance Crash Alerts`: Immediate failover protocol activation.
*   **Blast Radius (The "Impact"):**
    *   If a cache node experiences an OOM crash or power outage before dirty data is flushed, that data is permanently lost.
*   **Sequence Diagram:**
    ```
    Client ──> Application ──[Write]──> Cache (Dirty Key)
      │                                    │
    Client <──[Success] <──────────────────┘ (Async Worker writes to DB later)
    ```

---

## 🚫 4. Interview Playbook
*   **Common Mistakes:**
    *   Ignoring replication lag or assuming data written to the cache is safely persisted.
    *   Not offering a backup solution (e.g., streaming writes to an append-only log like Kafka first for recovery).
*   **Interview Tip (The "Strong Hire" Signal):**
    *   Propose write coalescing: *"To protect our DB from high-velocity updates, Write-Behind allows us to execute **write coalescing**—if a key is updated 10 times in 1 second, we only write the final state to the database once."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*
