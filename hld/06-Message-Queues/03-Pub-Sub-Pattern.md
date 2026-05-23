# ⚡ 03 - Pub/Sub Pattern

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C093 |
| **Category** | Messaging & Queues |
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
*   **Two-Sentence Trigger:** Publish/Subscribe (Pub/Sub) is a messaging pattern where **Publishers** emit events to a named **Topic** without knowing who will receive them, and **Subscribers** receive all events published to topics they subscribe to — enabling one-to-many fan-out without point-to-point coupling. It is the backbone of Event-Driven Architecture — a single `OrderPlaced` event can simultaneously trigger inventory reservation, email notification, fraud detection, and analytics.
*   **Scalability Dimension:** Primary: **One-to-Many Fan-Out** & **Producer/Consumer Decoupling**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### Point-to-Point Queue vs Pub/Sub
```
Point-to-Point Queue:
Producer → [Queue] → Consumer A (one consumer wins)

Pub/Sub Topic:
Producer → [Topic] → Consumer A (email service) ← ALL receive the message
                   → Consumer B (inventory service)
                   → Consumer C (fraud service)
```

### Pub/Sub Implementations
| Tool | Model | Retention | Consumer Tracking |
| :--- | :--- | :--- | :--- |
| **Kafka** | Persistent log + consumer group offsets. Subscribers pull. | Days/weeks/forever. | Consumer manages its own offset. |
| **Google Pub/Sub** | Managed push or pull. Per-subscription message retention. | 7 days default. | Subscription-level ACK tracking. |
| **AWS SNS + SQS** | SNS = Pub/Sub bus. SQS = queue per subscriber. | SQS: 14 days max. | Per-queue ACK. |
| **RabbitMQ Fanout Exchange** | Messages copied to all bound queues. | Until consumed (no persistence by default). | Per-queue ACK. |

### The SNS + SQS Fan-Out Pattern (The AWS Way)
```
[Order Service] → SNS Topic: "order.placed"
                      │
          ┌───────────┼───────────────┐
          ▼           ▼               ▼
     [SQS Queue]  [SQS Queue]   [SQS Queue]
     Email Lambda  Inventory Svc  Fraud Svc

Each downstream service gets its own SQS queue → independent processing speed.
If Fraud Svc crashes, its SQS queue buffers messages → no lost events.
```

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Per-subscriber lag / queue depth`: Each subscriber should be monitored independently — one slow subscriber doesn't impact others.
*   **Blast Radius (The "Impact"):**
    *   If a subscriber crashes without a queue buffer (pure push Pub/Sub), events published during downtime are **lost** for that subscriber. SNS+SQS pattern prevents this by buffering per subscriber.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Sending a Pub/Sub event with the **full data payload** as a field — instead of just an event ID and reference. Downstream services should pull data they need (skinny events pattern), not trust the embedded stale snapshot.
*   Not knowing that in Kafka, all consumers in the **same Consumer Group** share the load (each partition goes to one consumer). Different consumer groups each get their own independent copy of every message.

### Interview Tip (The "Strong Hire" Signal)
> *"When an order is placed, we publish a skinny `OrderPlaced` event (just order_id + timestamp) to an SNS topic. Each downstream service (email, inventory, fraud) has its own SQS queue subscribed to that topic. They independently pull the order details via REST. This fan-out decouples all downstream services and ensures no service crash affects the others."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*
