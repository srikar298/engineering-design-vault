# ⚡ 09 - Shared Database Pattern

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C117 |
| **Category** | Microservice Antipattern |
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
*   **Two-Sentence Trigger:** The Shared Database pattern is a microservice database design where multiple independent services read and write to the same database tables. While it simplifies initial development and enables standard SQL Joins and ACID transactions, it introduces tight schema coupling and creates a single point of failure.
*   **Scalability Dimension:** Primary: **Development Velocity (degrades over time)** & **System Availability / Blast Radius**.

---

## ⚖️ 2. Trade-offs & Deep Dive
| Shared Database (Coupled) | Database per Service (Decoupled) |
| :--- | :--- |
| **Shared Database:** Services access the same database tables directly. | **Database per Service:** Services query private tables via APIs. |
| *Pros:* Easy ACID transactions across entities. Standard SQL Joins. Low infrastructure footprint. | *Pros:* Clean domain boundaries. Scale databases separately. Language-agnostic database choices. |
| *Cons:* Schema changes by one team break other services. Database connection limits are shared. | *Cons:* Requires implementing complex distributed query/transaction patterns (CQRS, Sagas). |

*   **Ideal Use Cases:**
    *   Small startups building a Minimum Viable Product (MVP) where scale boundaries are undefined and development speed is the only metric.
    *   A Modular Monolith where code boundaries exist, but the database execution layer remains unified.
*   **Anti-Patterns / When NOT to use:**
    *   Scale-out microservice architectures. Sharing the database creates a **Distributed Monolith**—retaining the deployment complexity of microservices with the tight coupling of a monolith.

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Database connection starvation on shared instances`.
    *   `Schema change regression rates (incidents triggered by schema upgrades)`.
*   **Blast Radius (The "Impact"):**
    *   Maximum blast radius. A crash in the shared database instance, or a table lock triggered by service A's analytics query, takes down every single service in the system.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Advocating for a Shared Database pattern in a large-scale microservice interview (it is treated as a major anti-pattern by senior interviewers).
*   Ignoring database connection pool limits (if 20 microservices each pool 100 connections, the shared database crashes from connection starvation).

### Interview Tip (The "Strong Hire" Signal)
> *"While a Shared Database simplifies transactions early in an application's lifecycle, it is an anti-pattern for scaling microservices. It binds teams to shared schemas and limits scalability due to connection pool locks. I will enforce a **Database per Service** boundary to ensure service isolation."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*
