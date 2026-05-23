# ⚡ 04 - Cache-Aside (Lazy Loading)

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C016 |
| **Category** | Caching Strategy |
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
*   **Two-Sentence Trigger:** Cache-Aside is a pattern where the application server directly orchestrates data access, checking the cache first and falling back to the database on a miss. The database record is then backfilled into the cache asynchronously or synchronously before returning the data to the client.
*   **Scalability Dimension:** Primary: **Read Latency** & **Database Query Load**.

---

## ⚖️ 2. Trade-offs & Deep Dive
| Approach | Pros | Cons |
| :--- | :--- | :--- |
| **Cache-Aside (Lazy Loading)** | Simple. Cache size is kept optimal since only requested data is loaded. If the cache dies, the application remains functional (degraded). | Cache miss penalty (two database/network hops). Stale data occurs unless cache is explicitly invalidated when writes hit the DB. |
| **Inline Caching (Read-Through)** | Application code is simplified (delegates database access entirely to the cache driver). | Harder to implement custom fallback logic if the database write goes out of sync. |

*   **Ideal Use Cases:**
    *   General-purpose web applications with unpredictable access patterns.
    *   Systems where database schema reads are dynamic.
*   **Anti-Patterns / When NOT to use:**
    *   Highly consistent real-time systems where cache invalidation lag cannot be tolerated.

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Cache Miss Rate`: High rates indicate poor key configuration or short TTLs.
    *   `Database Query Count (Read Path)`: Check if queries scale linearly with app instances on cache misses.
*   **Blast Radius (The "Impact"):**
    *   If cache invalidation is misconfigured or fails (e.g., event bus message dropped), application clients read stale, outdated data indefinitely until the key TTL expires.
*   **Sequence Diagram:**
    ```
    Client ──> Application ──[Read Cache]──> Cache (Miss)
                   │
                   ├──[Query Database]──> Database
                   │
                   └──[Write to Cache]──> Cache
    ```

---

## 🚫 4. Interview Playbook
*   **Common Mistakes:**
    *   Updating the database first and forgetting to delete/invalidate the cache key.
    *   Failing to handle race conditions when two concurrent threads try to update the cache.
*   **Interview Tip (The "Strong Hire" Signal):**
    *   Mention the Cache Invalidation strategy: *"To avoid race conditions, when updating the database, I will **delete** the cache key rather than updating it directly. This guarantees the next read backfills clean, consistent data."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*
