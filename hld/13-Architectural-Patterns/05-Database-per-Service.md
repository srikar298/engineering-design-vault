# ⚡ 05 - Database per Service

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C116 |
| **Category** | Microservice Architecture |
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
*   **Two-Sentence Trigger:** Database per Service is a microservice pattern where each service owns a private database that is inaccessible to other services. All data access must go through the service's public API endpoints, preventing direct database coupling and enabling teams to deploy and scale database engines independently.
*   **Scalability Dimension:** Primary: **Development Velocity (decoupled schemas)** & **Database Connection Capacity**.

---

## ⚖️ 2. Trade-offs & Deep Dive
| Database per Service (Decoupled) | Shared Database (Coupled) |
| :--- | :--- |
| **Decoupled:** Each service has its own private schema/instance. | **Shared Database:** Multiple services query the same database tables. |
| *Pros:* Clean boundaries. Scale databases separately. Polyglot persistence allowed (SQL vs NoSQL). | *Pros:* Easy ACID transactions. Simple joins across all entities. |
| *Cons:* Distributed queries require API Composition or CQRS. Distributed transactions require Saga. | *Cons:* Direct schema changes break other teams. Single point of failure. Connection exhaustion. |

*   **Ideal Use Cases:**
    *   Enterprise microservice architectures where services are owned by separate engineering teams and have distinct scale characteristics.
*   **Anti-Patterns / When NOT to use:**
    *   Small, simple applications where microservices are not warranted, or when data domains are highly relational and require constant strong consistency joins.

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Cross-service query traffic count`: High ratios indicate domain boundaries are split incorrectly (chunky APIs).
    *   `Service-specific database CPU/Connection health`.
*   **Blast Radius (The "Impact"):**
    *   Fault isolation is maximized. If the Order DB crashes, user profile modifications in the User DB remain functional, limiting the blast radius compared to a monolithic database outage.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Allowing service A to read service B's database directly because "writing an API was too slow" (this completely defeats the purpose of the pattern and leads to a Distributed Monolith).
*   Ignoring database licensing and hosting costs when spinning up 30 separate database instances.

### Interview Tip (The "Strong Hire" Signal)
> *"We enforce the Database per Service pattern to preserve microservice autonomy. No service is allowed to execute direct SQL queries on another service's tables. Any data dependency must be resolved via REST APIs, gRPC calls, or asynchronous event replication."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*
