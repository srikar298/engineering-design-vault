# ⚡ 12 - Microservices Architecture

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C115 |
| **Category** | System Architecture |
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
*   **Two-Sentence Trigger:** Microservices Architecture decomposes a large application into small, independently deployable services, each owning a single bounded business domain, its own database, and its own deployment pipeline. This enables teams to develop, scale, and deploy services independently — but introduces distributed systems complexity: network latency, distributed transactions, service discovery, and observability challenges.
*   **Scalability Dimension:** Primary: **Development Velocity (at scale)** & **Independent Component Scalability**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### The Real Cost of Microservices (The "Distributed Tax")
Every synchronous call between services adds latency, failure modes, and operational complexity that simply don't exist in a monolith:

| Problem | Monolith | Microservices |
| :--- | :--- | :--- |
| **Transactions** | Single ACID transaction. | Requires Saga or 2PC. |
| **Data Queries** | Direct SQL JOIN. | API Composition or CQRS. |
| **Debugging** | Stack trace in one log. | Distributed tracing (Jaeger, Zipkin). |
| **Deployment** | One deploy, all or nothing. | 30 independent CI/CD pipelines. |
| **Latency** | In-process function call (~ns). | Network call (~1–10ms per hop). |
| **Testing** | Integration test locally. | Requires contract tests + service mocks. |

### The Microservices Readiness Checklist
Before splitting into microservices, your team should have:
- [x] Containerization (Docker) and orchestration (Kubernetes).
- [x] Centralized logging (ELK/Loki) and distributed tracing (Jaeger).
- [x] A CI/CD pipeline per service.
- [x] Service discovery (Consul/etcd) or a service mesh (Istio).
- [x] Clear domain boundaries (Domain-Driven Design bounded contexts).
- [x] If missing any of the above → start with a Modular Monolith.

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Inter-service P99 latency`: Degradation in one service propagates through call chains.
    *   `Deployment frequency per service`: Low frequency indicates a deployment bottleneck — defeats the purpose.
*   **Blast Radius (The "Impact"):**
    *   Without Circuit Breakers and Bulkheads, a single slow downstream service can cascade through the entire call graph and take down all services.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Proposing microservices for a 3-person startup (the operational overhead requires significant platform engineering capacity — premature decomposition is the #1 microservices anti-pattern).
*   Not addressing how you'll handle distributed transactions, service discovery, or observability after splitting.

### Interview Tip (The "Strong Hire" Signal)
> *"I default to a **Modular Monolith** with clean domain boundaries. I only extract a service when a specific module has a distinct scale requirement that the monolith can't satisfy, or when two teams need truly independent release cycles. The first extraction is the hardest — it validates our distributed infrastructure is in place."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*
