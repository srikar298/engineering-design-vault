# ⚡ 02 - Anti-Corruption Layer (ACL)

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C123 |
| **Category** | System Integration |
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
*   **Two-Sentence Trigger:** An Anti-Corruption Layer (ACL) is a translation layer placed between a new, clean subsystem and a legacy, messy subsystem to map communications between their respective domain models. It prevents the legacy system's outdated data models, APIs, and design paradigms from "corrupting" the clean domain boundaries of the new system.
*   **Scalability Dimension:** Primary: **Domain Boundary Integrity / Maintainability**. Secondary: **Read/Write Latency (translation overhead)**.

---

## ⚖️ 2. Trade-offs & Deep Dive
| Anti-Corruption Layer (Translation Proxy) | Direct Integration (Shared Schema/API) |
| :--- | :--- |
| **ACL:** Maps models bidirectionally in an intermediate service. | **Direct:** New system reads/writes directly using legacy schema/APIs. |
| *Pros:* Completely decouples the new domain code. Easy to retire legacy system later. | *Pros:* Low initial latency (no translation step). Simple initial coding. |
| *Cons:* Adds network hop, deployment overhead, and translation complexity. | *Cons:* Legacy models leak into the new database and codebase (makes upgrades painful). |

*   **Ideal Use Cases:**
    *   Incremental migrations of monolithic legacy systems to microservices using the Strangler Fig pattern.
    *   Integrating third-party SaaS APIs that have complex or non-standard payload structures.
*   **Anti-Patterns / When NOT to use:**
    *   Subsystems that naturally share identical domain contexts or are owned and managed by the same team within a single bounded context.

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Translation Error Rate`: Identifies mismatches in fields during updates.
    *   `Latency overhead added by translation functions`.
*   **Blast Radius (The "Impact"):**
    *   An outage in the ACL breaks all communication between the legacy and new systems. Data consistency drift will occur if translated write transactions are partially committed.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Allowing legacy system terminology (e.g., outdated field names or table logic) to enter the database schema of the new system.
*   Designing the ACL inside the legacy system (the ACL should belong to and protect the new clean system).

### Interview Tip (The "Strong Hire" Signal)
> *"During monolithic migration, to prevent legacy constraints from polluting our new microservices, I will deploy an **Anti-Corruption Layer**. This layer will translate incoming legacy payloads into clean domain events, ensuring we can easily retire the old monolithic tables later without modifying our new codebase."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*
