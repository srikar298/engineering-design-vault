# ⚡ 11 - Strangler Fig Pattern

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C124 |
| **Category** | Monolith Migration |
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
*   **Two-Sentence Trigger:** The Strangler Fig Pattern is an incremental migration pattern used to replace a monolithic legacy system with microservices by slowly routing specific API paths to new services. An intercepting routing layer (like an API Gateway or Reverse Proxy) redirects traffic feature-by-feature, eventually "strangling" and decommissioning the legacy monolith without a high-risk big-bang rewrite.
*   **Scalability Dimension:** Primary: **Operational Migration Safety** & **Zero-Downtime Deployment**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### Strangler Fig Migration Sequence
1.  **Deploy New Service:** Build the new microservice for a specific sub-domain (e.g., `/orders`).
2.  **Intercept & Route:** Configure the API Gateway to route all `/orders` requests to the new service, while other paths (`/users`, `/products`) continue going to the Monolith.
3.  **Synchronize Data:** If the Monolith database still needs order data, replicate writes from the `/orders` service back to the monolith database via an Anti-Corruption Layer (ACL).
4.  **Repeat:** Select the next domain and repeat until the Monolith handles no traffic.

```
Migration Phase:
                ┌───► [New Orders Service]
                │
[API Gateway] ──┼───► [New Users Service]
                │
                └───► [Legacy Monolith] (Strangled)
```

| Metric | Strangler Fig Migration | Big-Bang Rewrite |
| :--- | :--- | :--- |
| **Risk Profile** | Low. Features are migrated and tested incrementally in production. | High. Rewriting years of business rules in one release often fails. |
| **Time to Value** | Fast. First migrated feature delivers value to production in weeks. | Slow. No value is delivered until the entire rewrite is complete. |
| **Operational Overhead** | High. Must run and synchronize both legacy and new systems in parallel. | Low. Clean swap over at a single cut-off point. |

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `API Gateway route distribution percentages`: Tracks the migration progress.
    *   `Data synchronization lag between legacy and new databases`.
*   **Blast Radius (The "Impact"):**
    *   If the data synchronization layer fails during the parallel run phase, data inconsistencies arise between the Monolith and the new microservice database, blocking rollbacks.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Advocating for a complete "Big-Bang" database and application rewrite during monolith migration discussions (this is high risk and rarely approved by senior architects).
*   Ignoring database sync requirements (migrated services often need to write data back to the monolith's database because other un-migrated features still query it).

### Interview Tip (The "Strong Hire" Signal)
> *"To migrate our legacy monolith, I will use the **Strangler Fig** pattern. We will deploy an API Gateway to intercept traffic. We will extract the checkout logic first, routing `/checkout` to a new microservice while using an Anti-Corruption Layer to sync orders back to the monolith database until it can be retired."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*
