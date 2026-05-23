# ⚡ 01 - Cache Fundamentals

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C015 |
| **Category** | Caching Foundations |
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
*   **Two-Sentence Trigger:** Caching is the process of storing copies of active data in a high-speed, volatile RAM-based storage layer (e.g., Redis, local memory) to bypass slow primary disk-based databases. It turns expensive $O(N)$ or network-constrained queries into $O(1)$ sub-millisecond memory lookups.
*   **Scalability Dimension:** Primary: **Read Latency** & **Throughput (QPS)**. Secondary: **Database Load Reduction**.

---

## ⚖️ 2. Trade-offs & Deep Dive
| Caching Tier | Pros | Cons |
| :--- | :--- | :--- |
| **Application Local (In-Memory)** | Incredibly fast (<1μs), no network overhead. | Consumes JVM/process heap memory, state is isolated to one node (causes drift). |
| **External Distributed (Redis)** | Shared state across instances, massive storage space, independent scaling. | Network overhead (1-2ms), cluster management complexity. |

*   **Ideal Use Cases:**
    *   High-read, low-write data patterns (e.g., static configuration, product descriptions).
    *   Repeatedly requested computations (e.g., user search results).
*   **Anti-Patterns / When NOT to use:**
    *   Strict transactional data where any stale read causes financial double-spends.
    *   Highly write-heavy access patterns with unique keys (leads to very low Hit Ratios and wasted memory).

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Cache Hit Ratio`: Hits / (Hits + Misses). Aim for > 85%.
    *   `Eviction Rate`: Rapidly increasing eviction rate indicates the cache size is too small.
    *   `Cache Memory Usage`: Checking for OOM (Out Of Memory) states.
*   **Blast Radius (The "Impact"):**
    *   If cache hit ratio drops unexpectedly, the database CPU spikes, leading to thread exhaustion, cascading timeouts, and global API HTTP 504 errors.
*   **Numbers to Know:**
    *   RAM access latency: **~100 ns**
    *   Distributed cache over network (Redis): **~1 ms**
    *   Relational DB read: **~50 ms - 200 ms**

---

## 🚫 4. Interview Playbook
*   **Common Mistakes:**
    *   Assuming caching is a substitute for proper database indexing.
    *   Not accounting for memory usage footprint and capacity planning when designing the cache cluster size.
*   **Interview Tip (The "Strong Hire" Signal):**
    *   Always state: *"Caching is a dual-edged sword. It decreases read latency but introduces cache invalidation challenges. I will ensure our eviction policies and TTLs are configured to handle data consistency."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*
