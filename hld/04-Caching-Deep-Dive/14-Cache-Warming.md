# ⚡ 14 - Cache Warming (Pre-heating)

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C028 |
| **Category** | Cache Operational Pattern |
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
*   **Two-Sentence Trigger:** Cache Warming is the practice of pre-populating the cache with critical or high-frequency data (e.g., top products, configurations) before opening the system to public traffic. It ensures that the initial wave of users experiences sub-millisecond latencies, preventing a cold cache from bottlenecking the database.
*   **Scalability Dimension:** Primary: **Bootstrapping Latency** & **Initial DB Traffic Protection**.

---

## ⚖️ 2. Trade-offs & Deep Dive
| Targeted Warming (Popular Keys) | Global Warming (Full Dump) |
| :--- | :--- |
| **Targeted Warming:** Analyze historic logs and only load the top 10-20% of keys. | **Global Warming:** Dump all database records directly into the cache. |
| *Pros:* Fast warmup time, highly memory-efficient (leaves RAM for organic growth). | *Pros:* Guarantees high initial hit ratio. |
| *Cons:* Requires a telemetry system to track and identify "popular" items. | *Cons:* Extremely slow, wastes massive amounts of RAM on unused keys. |

*   **Ideal Use Cases:**
    *   Deploying a new microservice tier, spinning up a new cache cluster region, or recovering from a major system outage.
    *   Daily e-commerce promotions (e.g., Black Friday deals known in advance).
*   **Anti-Patterns / When NOT to use:**
    *   Do not perform cache warming on systems where key popularity is entirely random and unpredictable.

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Warmup Job Duration`: Time taken to run loader scripts.
    *   `Pre-traffic Cache Hit Ratio`: Verifying cache populated status before opening load balancers.
*   **Blast Radius (The "Impact"):**
    *   If the warming job is run synchronously during server startup, it can block container health checks, leading to deployment failures (Kubernetes reboot loops).

---

## 🚫 4. Interview Playbook
*   **Common Mistakes:**
    *   Running the cache warming job inside the main application thread during application boot (which blocks health checks).
    *   Warming the cache with cold, stale data that is never accessed.
*   **Interview Tip (The "Strong Hire" Signal):**
    *   Decouple execution: *"I will run our cache warming scripts as an asynchronous **Kubernetes InitContainer** or background cron job. This ensures application servers boot immediately and pass readiness health checks, while the cache is warmed concurrently before routing live traffic."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*
