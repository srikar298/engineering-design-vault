# ⚡ 08 - Cache Eviction - TTL (Time-to-Live)

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C020 |
| **Category** | Cache Eviction Policy |
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
*   **Two-Sentence Trigger:** Time-To-Live (TTL) is an eviction strategy that sets a timer or expiry timestamp on a cached key. Once the TTL duration expires, the key is evicted from memory, forcing subsequent requests to fetch fresh data from the database.
*   **Scalability Dimension:** Primary: **Data Freshness / Consistency** & **Cache Memory Size**.

---

## ⚖️ 2. Trade-offs & Deep Dive
| Short TTL (e.g., 60s) | Long TTL (e.g., 24h) |
| :--- | :--- |
| **Short TTL:** Fast key rotation. | **Long TTL:** Persistent data residency. |
| *Pros:* Fast data consistency. Stale windows are small. | *Pros:* High cache hit ratio, offloads database traffic significantly. |
| *Cons:* High cache miss rates, database query load spikes. | *Cons:* Risk of clients viewing stale, outdated data for hours. |

*   **Ideal Use Cases:**
    *   Dynamic content that changes predictably (e.g., dashboard statistics).
    *   General cache layers to protect against stale data when invalidation mechanisms fail.
*   **Anti-Patterns / When NOT to use:**
    *   Stateless caches where keys never change (should use infinite TTL with explicit programmatic invalidation).

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Expired Keys Metric`: Rates of active vs passive key cleanups.
    *   `Stale Data Complaints`: Customer service reports indicating TTL is too long.
*   **Blast Radius (The "Impact"):**
    *   If many keys share the exact same TTL, they will expire concurrently, resulting in a **Cache Avalanche** that floods the database.
*   **Active vs. Passive Eviction:**
    *   *Passive:* Key is removed when a client attempts to read it after expiry.
    *   *Active:* Background cron process randomly samples keys and deletes expired ones.

---

## 🚫 4. Interview Playbook
*   **Common Mistakes:**
    *   Hardcoding identical TTL values for all keys, creating systemic expiry stampedes.
    *   Failing to explain how Redis processes key expiries in background loops.
*   **Interview Tip (The "Strong Hire" Signal):**
    *   Advocate for TTL Jitter: *"To prevent a cache avalanche, I will add a **random jitter** (e.g., random variation of 1-5 minutes) to the baseline TTL of our keys, distributing the expiration workload smoothly over time."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*
