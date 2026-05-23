# ⚡ 02 - Saga Pattern

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C091 |
| **Category** | Distributed Transactions |
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
*   **Two-Sentence Trigger:** The Saga Pattern manages distributed transactions in a microservices architecture using a sequence of local transactions across independent databases. If any step fails (e.g., payment declined), the Saga executes a series of compensating transactions (undo operations) in reverse order to return the system to a consistent state.
*   **Scalability Dimension:** Primary: **System Latency** & **Throughput (removes blocking two-phase locks)**. Secondary: Negative impact on **Data Consistency (Eventual Consistency)**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### Choreography vs. Orchestration
| Attribute | Choreography (Event-Driven) | Orchestration (Orchestrator-Driven) |
| :--- | :--- | :--- |
| **Coordination** | Decentralized. Services listen to events and trigger their own actions. | Centralized. An "Orchestrator" service directs steps via commands. |
| **Coupling** | Low. Services do not know about each other; they only publish/listen to events. | High. Orchestrator must understand all service interfaces. |
| **Debugging** | Hard. Difficult to trace transaction state across logs. | Easy. The orchestrator maintains a centralized state machine. |
| **Ideal For** | Simple workflows (2-3 services). | Complex workflows (e.g., checkout flows with multiple steps). |

### Compensating Transactions (Rollbacks)
Because local transactions commit immediately to their respective databases, a traditional rollback is impossible. If step 3 fails, the system executes **Compensating Transactions**:
*   *Action:* Step 1: Reserve Ticket -> Step 2: Pay Money -> Step 3: Book Hotel (FAIL).
*   *Compensation:* Cancel Hotel -> Refund Money -> Release Ticket.
*   *Challenge:* Compensations **cannot be undone** easily and must be designed to be idempotent.

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Saga Rollback Frequency`: Indicates checkout system failures.
    *   `Saga Execution Duration`: Tracking P99 time taken to complete the entire saga lifecycle.
*   **Blast Radius (The "Impact"):**
    *   If a compensating transaction fails (e.g., the refund service crashes mid-rollback), the system enters an **inconsistent state**. This requires Dead Letter Queues (DLQ) and manual intervention.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Assuming Sagas guarantee ACID isolation (they do not; since local transactions commit early, other transactions can view intermediate "dirty" states—a phenomenon called lack of isolation).
*   Forgetting that compensating transactions must be idempotent (if a retry occurs during rollback, you cannot refund a user twice).

### Interview Tip (The "Strong Hire" Signal)
> *"For our booking service, we avoid blocking 2PC locks. We implement the **Saga Orchestrator** pattern. A centralized state machine coordinates steps using Kafka commands, and if a downstream service fails, the orchestrator triggers idempotent compensating events to roll back previous commits asynchronously."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*
