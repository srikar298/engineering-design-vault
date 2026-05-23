# ⚡ 10 - Cache Eviction - LFU (Least Frequently Used)

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C022 |
| **Category** | Cache Eviction Policy |
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
*   **Two-Sentence Trigger:** Least Frequently Used (LFU) is a cache eviction policy that discards keys with the lowest hit frequency when memory is full. It maintains a reference counter for every key, ensuring that highly requested items remain in memory regardless of how recently they were last accessed.
*   **Scalability Dimension:** Primary: **Cache hit ratio stability for power-law distributions**.

---

## ⚖️ 2. Trade-offs & Deep Dive
| LFU Eviction | LRU Eviction |
| :--- | :--- |
| **LFU:** Evicts based on cumulative frequency counters. | **LRU:** Evicts based on elapsed time since last access. |
| *Pros:* Immune to cache pollution from occasional database scans or random queries. | *Pros:* Extremely low metadata overhead. Matches temporal-locality patterns. |
| *Cons:* Requires additional memory to store frequency counters. Historic hot keys remain cached even if dead (requires frequency decay). | *Cons:* Sub-optimal if access patterns are frequent but separated by large time gaps. |

*   **Ideal Use Cases:**
    *   Highly stable popularity access patterns (e.g., streaming platform top-10, catalog categories).
*   **Anti-Patterns / When NOT to use:**
    *   Dynamic datasets where popularity shifts rapidly over short windows (unless using frequency decay).
*   *Implementation Reference:* Uses Frequency Lists mapped to Doubly Linked Lists. See the thread-safe implementation at [ThreadSafeLFUCacheSDE2.java](file:///e:/job-hunt/LLD/LLD-Design-Patterns-main/lld/06-Addons/11-LFU-Cache/ThreadSafeLFUCacheSDE2.java).

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Frequency counter metrics`: Monitoring the distribution of key hit rates.
    *   `OOM Errors / Eviction spikes`.
*   **Blast Radius (The "Impact"):**
    *   Memory footprint expansion. Without **Frequency Decay** (halving counters over elapsed time), LFU caches run out of capacity, evicting new hot keys due to outdated records.

---

## 🚫 4. Interview Playbook
*   **Common Mistakes:**
    *   Not knowing how to handle frequency aging/decay (without decay, a key hit 1M times last Christmas will block new keys forever).
    *   Failing to explain how to store the frequency metadata efficiently.
*   **Interview Tip (The "Strong Hire" Signal):**
    *   Mention frequency decay: *"To prevent dead keys from occupying the cache permanently, I will implement **Frequency Decay**—periodically scaling down or halving access counters over time, allowing new hot items to overcome historical records."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*
