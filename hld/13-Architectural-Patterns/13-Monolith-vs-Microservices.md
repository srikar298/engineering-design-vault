# ⚡ 13 - Monolith vs. Microservices

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C114 |
| **Category** | System Architecture |
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
*   **Two-Sentence Trigger:** A Monolith is a single deployable unit containing all application functionality — simple to develop and debug but hard to scale and modify without risking the whole system. Microservices decompose the application into independent services with separate deployment pipelines and databases — maximizing team autonomy and scalability but at the cost of distributed systems complexity.
*   **Scalability Dimension:** Primary: **Development Velocity (early vs late stage)** & **Operational Complexity Trade-off**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### The Full Spectrum
```
Monolith ──────────────────────────────────────────────────► Microservices
    │              │                    │                          │
Simplest       Modular             Macro-services           Fine-grained
Single deploy  Monolith            (2–5 services)           100+ services
               (Recommended start) (Common at mid-scale)    (Netflix/Uber scale)
```

### Detailed Comparison
| Dimension | Monolith | Modular Monolith | Microservices |
| :--- | :--- | :--- | :--- |
| **Deployment** | All-or-nothing. One artifact. | All-or-nothing, but modules are isolated. | Independent per service. |
| **Scalability** | Scale everything together. | Scale the whole app, but waste resources. | Scale individual services independently. |
| **Development Speed (early)** | ✅ Fast — no distributed concerns. | ✅ Fast — clean code boundaries. | ❌ Slow — infra setup, contracts, mocks. |
| **Development Speed (at scale)** | ❌ Slow — large codebase, merge conflicts. | 🟡 Moderate — still one deploy. | ✅ Fast — parallel team autonomy. |
| **Failure Isolation** | ❌ One bug can crash everything. | ❌ One deploy failure affects all. | ✅ Service failures are isolated. |
| **Database** | Shared single DB. | Shared DB with logical schemas. | Database per service. |
| **Testing** | Simple integration tests. | Moderate — module boundary tests. | Complex — contract tests, service mocks. |
| **Operational Cost** | Low — 1 deployment, 1 log stream. | Low. | High — 30 pipelines, 30 log streams. |

### The "Distributed Monolith" Anti-Pattern
The worst outcome: you split into microservices but services are still **tightly coupled**:
*   Service A cannot deploy without also deploying Service B (shared DB schema or synchronous hard dependencies).
*   Called a "Distributed Monolith" — you've paid the microservices operational cost but received none of the benefits.

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Deployment coupling ratio`: How often do multiple services deploy together? High coupling = Distributed Monolith.
    *   `Time-to-Deploy per service`: Slow deploys in microservices indicate shared infra bottlenecks.
*   **Blast Radius (The "Impact"):**
    *   Monolith: A single bad deploy or OOM crash takes down the entire application instantly.
    *   Microservices: Blast radius is contained to the affected service + its direct synchronous callers.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Treating this as a binary choice (monolith bad, microservices good) instead of a spectrum driven by team size, scale, and operational maturity.
*   Not knowing what a "Modular Monolith" is — this is the recommended starting point for most systems.

### Interview Tip (The "Strong Hire" Signal)
> *"I always start with a **Modular Monolith** — clean domain module boundaries in a single deployable. I extract the first microservice when: (1) a specific module has a scale requirement that warrants independent scaling, or (2) two teams need fully independent deployment pipelines. Premature microservices are the #1 architecture mistake I see."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*
