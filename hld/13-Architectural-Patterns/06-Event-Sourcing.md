# ⚡ 06 - Event Sourcing

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C120 |
| **Category** | Microservice Persistence |
| **Difficulty** | 🔥 Hard |
| **Interview Frequency** | 🟡 Medium |
| **Understanding** | [🔴 None / 🟡 Conceptual / 🟢 Applied] |
| **Can Explain** | [ ] Yes / [ ] No |
| **Whiteboard Drawn** | [ ] Yes / [ ] No |
| **Taught Someone** | [ ] Yes / [ ] No |
| **Next Review** | YYYY-MM-DD |
| **Mastery** | [🔴 Familiar / 🟡 Competent / 🟢 Expert] |

---

## ⚡ 1. The Core Definition & Trigger
*   **Two-Sentence Trigger:** Event Sourcing is a design pattern where changes to application state are stored as a chronological sequence of immutable events in an Event Store. Instead of overwriting records to save the current state, the system reconstructs the current state of an entity (e.g., an Order) by replaying all historical events associated with it.
*   **Scalability Dimension:** Primary: **Write Throughput (sequential appends)** & **Audit Trail Integrity**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### Event Sourcing Workflow
1. **Command Input:** User executes "Change Address".
2. **Event Creation:** System generates an immutable `AddressChangedEvent`.
3. **Event Store Write:** The event is sequentially appended to the Event Store (append-only log).
4. **Projection:** Read models (CQRS views) consume the event to update the current address details database.

```
Write Stream:
[OrderCreated] ──► [ItemAdded] ──► [AddressUpdated] ──► [OrderShipped] (Event Store)
                                                               │
                                                       (Replay to reconstruct state)
                                                               │
                                                               ▼
                                                      Current State: SHIPPED
```

### The SDE-2 Challenge: Snapshotting
If an entity has thousands of events (e.g., a long-running bank account), replaying all events from history to reconstruct the current balance is slow.
*   *Solution:* **Snapshots**. Periodically (e.g., every 100 events), the system saves the current state (a snapshot) in a cache. To reconstruct, the system loads the last snapshot and only replays the events created *after* that snapshot.

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Event Replay Duration`: Time taken to reconstruct aggregates.
    *   `Event Schema Version Drift`: Challenges in updating the structure of historical events (requires "Upcasting" to map old event versions to new structures).
*   **Blast Radius (The "Impact"):**
    *   If the Event Store suffers corruption, historical integrity is compromised. All downstream CQRS read models derived from events will be incorrect.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Deploying Event Sourcing without a CQRS read model (without CQRS, searching or filtering fields in your domain objects requires scanning all events on disk, which is incredibly slow).
*   Mutating historical events (events are strictly immutable; corrections must be appended as new compensating events, like accounting ledgers).

### Interview Tip (The "Strong Hire" Signal)
> *"For our ledger database, we use Event Sourcing to guarantee a tamper-proof audit trail. To optimize query performance, we reconstruct state using **Snapshotting** every 50 events, and project current balances asynchronously into a Redis read cache via CQRS."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*
