# ⚡ 12 - Cache Stampede (Cache Breakdown)

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C026 |
| **Category** | Cache Failure & Mitigation |
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
*   **Two-Sentence Trigger:** A Cache Stampede (also known as Cache Breakdown or Thundering Herd) occurs when a highly active "hot key" expires under heavy read traffic. Because the key is gone, thousands of concurrent user threads read a cache miss at the exact same millisecond and attempt to query the database and re-populate the cache simultaneously, overloading the DB.
*   **Scalability Dimension:** Primary: **Concurrency Control** & **Database Peak Read Load Protection**.

---

## ⚖️ 2. Trade-offs & Deep Dive
| Mitigation A: Mutex / Distributed Lock | Mitigation B: Probabilistic Expiration |
| :--- | :--- |
| **Mutex:** Force the first thread experiencing a miss to acquire a lock before hitting the DB. | **Probabilistic Expiration (XFetch):** Asynchronously recalculates keys before official expiry. |
| *Pros:* Simple to write; guarantees exactly 1 query reaches the database. | *Pros:* Flat read latency profile. Clients never experience a blocking wait. |
| *Cons:* Latency penalty for waiting threads; deadlocks or connection timeouts are risks if lock holder dies. | *Cons:* Highly complex math (uses logarithmic probability functions). |

*   **Ideal Use Cases:**
    *   System home page configurations, celebrity user feeds, or trending catalog items.
*   **Anti-Patterns / When NOT to use:**
    *   Sparsely accessed keys where concurrent misses are mathematically improbable.

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Db Read Spikes`: Massive, vertical spikes on specific tables aligning with hot-key expiries.
    *   `Thread Count saturation` on application servers.
*   **Blast Radius (The "Impact"):**
    *   Cascading thread pool exhaustion on the application tier and database locking.
*   **Logical Walkthrough:**
    ```
    Multiple Threads ──> [Cache Miss] ──> [Acquire Redis Lock?]
                                               │
                                     ┌─────────┴─────────┐
                             (Yes - Thread 1)    (No - Threads 2-N)
                                     │                   │
                                     ▼                   ▼
                                 Query DB &          Sleep/Retry
                                Update Cache        or serve stale
    ```

---

## 🚫 4. Interview Playbook
*   **Common Mistakes:**
    *   Confusing Cache Stampede (hot key expiry) with Cache Avalanche (mass server or multi-key expiry).
    *   Suggesting a local application lock (Java `synchronized`) instead of a **Distributed Lock** (e.g., Redis `SETNX`) in a multi-instance web cluster.
*   **Interview Tip (The "Strong Hire" Signal):**
    *   State: *"In a distributed setup, a local lock only protects a single node. I will use a distributed lock (e.g., Redlock or Redis SETNX) to ensure only one database query is generated across our entire application cluster, while other nodes serve the slightly stale value during the refresh."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*
