# ⚡ 09 - Event-Driven Architecture (EDA)

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C100 |
| **Category** | Messaging & Queues |
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
*   **Two-Sentence Trigger:** Event-Driven Architecture (EDA) is a software design paradigm where services communicate by producing and consuming **events** — immutable records of things that happened — rather than making direct synchronous calls to each other. This maximizes service decoupling: the Order Service doesn't know or care which services react to `OrderPlaced`; it simply publishes the event and continues.
*   **Scalability Dimension:** Primary: **Service Decoupling** & **Independent Team Velocity**. Secondary: **Temporal Decoupling** (services don't need to be online simultaneously).

---

## ⚖️ 2. Trade-offs & Deep Dive

### Request-Driven vs Event-Driven
```
REQUEST-DRIVEN (Synchronous):
Order Service ──► POST /reserve-inventory ──► Inventory Service (wait...)
              ──► POST /send-email ──────────► Email Service (wait...)
              ──► POST /detect-fraud ──────────► Fraud Service (wait...)
              ← Sum of all latencies. Any downstream failure = Order fails.

EVENT-DRIVEN (Asynchronous):
Order Service ──► Publish: OrderPlaced { order_id, user_id, amount }
                     │
         ┌───────────┼────────────────────┐
         ▼           ▼                    ▼
Inventory Svc   Email Svc           Fraud Svc
(independent)   (independent)       (independent)
← Order Service returns immediately. Downstream failures don't affect order creation.
```

### Types of Events
| Type | Description | Example |
| :--- | :--- | :--- |
| **Domain Event** | Something that happened in the business domain. | `OrderPlaced`, `PaymentCompleted`, `UserRegistered` |
| **Command Event** | A request for something to happen (targeted). | `SendWelcomeEmail`, `ReserveInventory` |
| **Integration Event** | A cross-service notification (broadcast). | `OrderShipped` → tells downstream apps to update delivery status |

### The EDA Trade-offs
| Benefit | Cost |
| :--- | :--- |
| Services are temporally decoupled. | **Eventual consistency** — state is not immediately synchronized across services. |
| Services can evolve independently. | **Choreography complexity** — hard to trace a business workflow across multiple event handlers. |
| Absorbs traffic spikes via queues. | **Debugging difficulty** — no single stack trace; distributed tracing required. |
| Fan-out is free (multiple subscribers). | **Schema evolution** — changing event structure requires versioning to avoid breaking consumers. |

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Event Processing Lag per Service`: How far behind is each consumer from the event source?
    *   `Event Schema Validation Failure Rate`: Consumers rejecting events due to unexpected schema → schema versioning issue.
*   **Blast Radius (The "Impact"):**
    *   EDA failure modes are subtle — a consumer failing silently doesn't immediately surface to users, but business state diverges (e.g., inventory not reserved even though order was placed).

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Not addressing **eventual consistency** — in EDA, state is temporarily inconsistent across services. Users may see stale reads between event publication and all consumers processing it.
*   Using EDA for everything (some flows genuinely need synchronous response: checking if a product is in stock before confirming an order requires a synchronous inventory call, not async event).

### Interview Tip (The "Strong Hire" Signal)
> *"Our checkout flow is hybrid. We make a synchronous inventory reservation call (must confirm stock exists before accepting payment). Once payment succeeds, we publish `OrderPlaced` event — inventory deduction, email, fraud scoring, and analytics all happen asynchronously. This gives us consistency where it matters (stock availability) and decoupling everywhere else."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*
