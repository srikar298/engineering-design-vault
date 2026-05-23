# ⚡ 13 - Cache Avalanche (Cache Snowslide)

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C027 |
| **Category** | Cache Failure & Mitigation |
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
*   **Two-Sentence Trigger:** A Cache Avalanche occurs when a large percentage of cache keys expire at the exact same moment, or when the caching cluster crashes entirely. As a result, all client read traffic fails over to the database simultaneously, causing a cascading system outage due to database CPU and connection pool exhaustion.
*   **Scalability Dimension:** Primary: **System Availability** & **Fault Tolerance**.

---

## ⚖️ 2. Trade-offs & Deep Dive
| Mitigation Strategy | Pros | Cons |
| :--- | :--- | :--- |
| **TTL Jitter (Random Offsets)** | Cost-free, simple math, permanently spreads out the expiration timeline. | Does not protect against physical cache cluster hardware crashes. |
| **Circuit Breakers & Degradation** | Prevents DB collapse by shedding load or returning fallback states. | Clients experience degraded user experience (e.g., empty search results or cached static defaults). |
| **Multi-AZ Replication (Redis Sentinel)** | Protects against hardware failures and node crashes. | Doubles infrastructure cost and introduces master-to-replica lag. |

*   **Ideal Use Cases:**
    *   Any high-volume production cache deployment to prevent catastrophic database crashes.
*   **Anti-Patterns / When NOT to use:**
    *   Do not use uniform, un-jittered TTLs (e.g., `expire_at_midnight = true`) for large database records.

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Database CPU Utilization`: Vertical spikes to 100%.
    *   `Cache Node Health/Pings`: Drops to zero or timeouts.
    *   `Http 5xx/504 Gateway Timeouts` rising rapidly.
*   **Blast Radius (The "Impact"):**
    *   Global system downtime, database locking, and potential transaction corruption if primary instances lock up under load.

---

## 🚫 4. Interview Playbook
*   **Common Mistakes:**
    *   Assuming TTL Jitter resolves hardware node crashes (you need high-availability replication and clustering for hardware faults).
    *   Failing to explain what happens to users during a crash (always mention returning a stale fallback or error screen gracefully).
*   **Interview Tip (The "Strong Hire" Signal):**
    *   Incorporate Resilience Patterns: *"If our cache cluster goes down entirely, we must protect the database. I will implement a **Circuit Breaker** (e.g., Resilience4j) to limit database queries, and return degraded fallback data (e.g., static popular items) to keep the app operational."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*
