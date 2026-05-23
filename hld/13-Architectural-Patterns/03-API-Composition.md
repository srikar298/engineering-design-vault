# ⚡ 03 - API Composition

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C118 |
| **Category** | Microservice Queries |
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
*   **Two-Sentence Trigger:** API Composition is a microservices query pattern where an aggregator service (often the API Gateway or a dedicated composer service) retrieves data from multiple downstream services in parallel to satisfy a client read request. It acts as an application-level `JOIN` engine when data is split across isolated service databases.
*   **Scalability Dimension:** Primary: **Read Query Latency** & **Network Bandwidth Consumption**.

---

## ⚖️ 2. Trade-offs & Deep Dive
| API Composition (App-level Join) | CQRS (Read-Optimized View Database) |
| :--- | :--- |
| **API Composition:** Dynamically queries multiple API endpoints at read-time. | **CQRS:** Asynchronously replicates and pre-joins data in a single Read DB. |
| *Pros:* Simple to implement. Zero data synchronization replication lag (reads are live). | *Pros:* High performance. Complex queries execute via one DB look-up. |
| *Cons:* Slow latency (limited by the slowest downstream API). High network overhead. | *Cons:* Eventual consistency lag. High infrastructure complexity. |

*   **Ideal Use Cases:**
    *   Simple aggregation queries across 2-3 services (e.g., displaying a user profile alongside their active subscription status).
*   **Anti-Patterns / When NOT to use:**
    *   Queries requiring massive aggregations or filtering across thousands of rows from multiple databases (leads to network choke points and CPU saturation on the composer node).

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Downstream response latency distribution`: Used to identify which downstream microservice is delaying the API composer.
    *   `Compose Thread Pool Saturation`.
*   **Blast Radius (The "Impact"):**
    *   If one downstream service fails or returns slow responses, the API composer's threads can lock up, causing a cascading failure that takes down the entire aggregate page.
*   *Resiliency Fix:* Implement **Circuit Breakers** and timeouts with fallback data (e.g., if the user recommendation service is down, the page still loads but recommendation frames display default items).

---

## 🚫 4. Interview Playbook
*   **Common Mistakes:**
    *   Querying downstream services sequentially instead of launching parallel threads (e.g., using `CompletableFuture` in Java or `Promise.all` in JavaScript).
    *   Not accounting for partial failure (if service C fails, does the whole client page break, or do we degrade gracefully?).
*   **Interview Tip (The "Strong Hire" Signal):**
    *   State: *"To satisfy the user profile page query, our API Gateway will act as an API Composer, querying the User, Wallet, and Order services in parallel. If our wallet service fails to respond within 150ms, our gateway will fall back to returning a default 'Status Unavailable' state for the wallet card rather than blocking the entire page render."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*
