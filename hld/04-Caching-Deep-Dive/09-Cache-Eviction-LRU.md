# ⚡ 09 - Cache Eviction - LRU (Least Recently Used)

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C021 |
| **Category** | Cache Eviction Policy |
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
*   **Two-Sentence Trigger:** Least Recently Used (LRU) is a cache eviction algorithm that discards the item that has not been accessed for the longest period when memory reaches capacity. It leverages temporal locality, assuming that data accessed recently is likely to be accessed again in the near future.
*   **Scalability Dimension:** Primary: **Memory Utilization Efficiency (maximizes Hit Ratio under size constraints)**.

---

## ⚖️ 2. Trade-offs & Deep Dive
| LRU Eviction | LFU Eviction |
| :--- | :--- |
| **LRU:** Evicts based on recency of access. | **LFU:** Evicts based on frequency of access. |
| *Pros:* Extremely fast; performs optimally for general-purpose user access patterns (e.g., social feeds). | *Pros:* Better for long-term hot keys (celebrity profiles). |
| *Cons:* Vulnerable to "cache pollution" during batch database reads/scans (which evicts active keys). | *Cons:* Higher tracking memory footprint, historic hot keys stay cached. |

*   **Ideal Use Cases:**
    *   Application local memory caching (e.g., Guava/Caffeine).
    *   General web app user caches where access patterns follow a power-law distribution.
*   **Anti-Patterns / When NOT to use:**
    *   Predictable cyclical workloads where keys are requested sequentially (e.g., scanning database rows in a loop).

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Eviction Count Rate`: A spike indicates the cache is thrashing (frequent swap-out due to small capacity).
    *   `Cache hit ratio stabilization trends`.
*   **Blast Radius (The "Impact"):**
    *   If cache thrashing occurs, latency increases to database read times, causing service delays.
*   *Implementation Reference:* Doubly Linked List + HashMap. See the thread-safe implementation at [ThreadSafeLRUCacheSDE2.java](file:///e:/job-hunt/LLD/LLD-Design-Patterns-main/lld/06-Addons/04-LRU-Cache/ThreadSafeLRUCacheSDE2.java).

---

## 🚫 4. Interview Playbook
*   **Common Mistakes:**
    *   Failing to explain how Redis implements LRU (Redis uses an *approximated LRU algorithm* by testing a random sample of keys to save memory overhead, rather than maintaining a strict doubly linked list).
    *   Not knowing the $O(1)$ operations required to design it.
*   **Interview Tip (The "Strong Hire" Signal):**
    *   Describe the approximated LRU: *"To conserve memory, production systems like Redis do not use a strict Doubly Linked List for LRU. Instead, they sample a random subset of keys (e.g., 5 keys) and evict the oldest key in that sample."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*
