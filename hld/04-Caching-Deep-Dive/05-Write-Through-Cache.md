# ⚡ 05 - Write-Through Cache

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C017 |
| **Category** | Caching Strategy |
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
*   **Two-Sentence Trigger:** Write-Through Cache is a write pattern where the application updates the cache first, and the cache synchronously writes the updated data to the backend database before completing the write transaction. It ensures that the cache and database are always in lockstep consistency.
*   **Scalability Dimension:** Primary: **Data Consistency** & **Read Latency (for subsequent reads)**. Secondary: Negative impact on **Write Latency**.

---

## ⚖️ 2. Trade-offs & Deep Dive
| Write-Through | Write-Around (Write-Aside) |
| :--- | :--- |
| **Write-Through:** Synchronously updates both cache and database. | **Write-Around:** Writes directly to the DB, bypassing the cache. |
| *Pros:* Data in the cache is always fresh. Subsequent reads are instant ($O(1)$ cache hit). | *Pros:* Saves cache RAM from being filled with keys that might never be read again. |
| *Cons:* High write latency due to synchronous double-writes (Cache + DB). | *Cons:* Subsequent read of the written key will result in a cache miss. |

*   **Ideal Use Cases:**
    *   System data that is read immediately after creation (e.g., user registration, session token validation).
    *   Applications requiring strict, real-time read consistency without cache invalidation complexity.
*   **Anti-Patterns / When NOT to use:**
    *   Write-heavy environments where the written data is rarely read (leads to high write latency and cache pollution).

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Write Latency`: High latency indicates DB write queues or cache synchronization limits.
    *   `DB Write IOPs`: Checking if database write bottlenecks are scaling up.
*   **Blast Radius (The "Impact"):**
    *   If the database crashes or locks, writes fail completely back to the client, even if the cache cluster is healthy.
*   **Sequence Diagram:**
    ```
    Client ──> Application ──[Write]──> Cache ──[Synchronous Write]──> Database
                                          │
    Client <──[Success] <─────────────────┘
    ```

---

## 🚫 4. Interview Playbook
*   **Common Mistakes:**
    *   Forgetting that write-through does not save the database from write pressure (every write still hits the DB).
    *   Not accounting for network partitions where cache writes succeed but DB writes fail.
*   **Interview Tip (The "Strong Hire" Signal):**
    *   Point out the write latency trade-off: *"I will select Write-Through because we have a read-immediately access pattern. Although write latency will increase by 1-2ms, we eliminate cache invalidation lag entirely."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*
