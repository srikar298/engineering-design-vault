# 🏗️ 13 - Architectural Patterns (C056-C126)

## 🧭 Microservices & Architectural Patterns Study Path
Use this structured path aligned with your **Google Sheet Tracker** to master microservices and distributed systems architectures:

### 🟢 1. Integration & Infrastructure Patterns
*   [C056 - Ambassador Pattern](./01-Ambassador-Pattern.md)
*   [C123 - Anti-Corruption Layer (ACL)](./02-Anti-Corruption-Layer.md)
*   [C057 - Sidecar Pattern](./10-Sidecar.md)
*   [C124 - Strangler Fig Pattern](./11-Strangler-Fig.md)

### 🟡 2. Microservice Database & Query Patterns
*   [C116 - Database per Service](./05-Database-per-Service.md)
*   [C117 - Shared Database Pattern](./09-Shared-Database.md)
*   [C118 - API Composition](./03-API-Composition.md)
*   [C119 - CQRS (Command Query Responsibility Segregation)](./04-CQRS.md)

### 🔴 3. Distributed Transactions & Resiliency
*   [C120 - Event Sourcing](./06-Event-Sourcing.md)
*   [C121 - Transactional Outbox Pattern](./08-Outbox-Pattern.md)
*   [C122 - Idempotency](./07-Idempotency.md)

### 🟠 4. Microservice Architecture & Infrastructure
*   [C114 - Monolith vs. Microservices](./13-Monolith-vs-Microservices.md)
*   [C115 - Microservices Architecture](./12-Microservices.md)
*   [C058 - Service Discovery](./14-Service-Discovery.md)
*   [C059 - Service Mesh](./15-Service-Mesh.md)
*   [C057 - Sidecar Pattern](./10-Sidecar.md) *(See also Section 1)*

---


Choosing the right architectural style is the most fundamental decision in a project. It determines how your team scales, how you deploy, and how you handle failure.

---

## 🏢 1. Monolith vs. Microservices

| Feature | Monolithic | Microservices |
| :--- | :--- | :--- |
| **Complexity** | Low (Single codebase). | High (Network, Service Discovery). |
| **Deployment** | All-or-nothing. | Independent deployments. |
| **Scalability** | Scale the whole app. | Scale specific bottlenecks. |
| **Reliability** | One crash can kill everything. | Fault isolation (Bulkheads). |

*Founding Engineer Pro-Tip:* Start with a **Modular Monolith**. It gives you clean boundaries without the "distributed systems tax" until you actually have 10k+ concurrent users.

---

## 🌊 2. Event-Driven Architecture (EDA)
Instead of services calling each other directly (Request-Response), they communicate via **Events**.

### CQRS (Command Query Responsibility Segregation)
- **Commands**: Writes (Create/Update/Delete). Optimized for consistency.
- **Queries**: Reads. Optimized for performance (often uses a separate Read DB/Cache).

### Saga Pattern
How to handle distributed transactions without 2PC (Two-Phase Commit).

| Feature | Choreography (Event-based) | Orchestration (Command-based) |
| :--- | :--- | :--- |
| **Logic** | Distributed across services. | Centralized in an "Orchestrator". |
| **Coupling** | Low (Services just listen to events). | High (Orchestrator knows about all services). |
| **Complexity** | High (Hard to track the whole flow). | Lower (Easy to see the state). |
| **Use Case** | Simple workflows. | Complex, multi-step business logic. |

#### Example: Food Delivery Saga (Mermaid)
```mermaid
sequenceDiagram
    participant O as Order Service
    participant P as Payment Service
    participant I as Inventory Service
    
    O->>O: Create Order (PENDING)
    O->>P: Process Payment
    alt Payment Success
        P->>O: PaymentSuccess Event
        O->>I: Reserve Items
        alt Inventory Success
            I->>O: ItemsReserved Event
            O->>O: Approve Order (SUCCESS)
        else Inventory Fail
            I->>O: ItemsUnavailable Event
            O->>P: Refund Payment (Compensating Trans)
            O->>O: Cancel Order (FAIL)
        end
    else Payment Fail
        P->>O: PaymentFailed Event
        O->>O: Cancel Order (FAIL)
    end
```

---

## 🚀 The SDE-3 Edge: The Transactional Outbox Pattern

**The Problem:** You update the Database and then send a Message to Kafka. If the message send fails (network blip) but the DB commit succeeds, your system is **Inconsistent**.

**The SDE-3 Solution:**
1. Instead of sending directly to Kafka, you write the message into an `OUTBOX` table in the *same* database transaction as your business logic.
2. A separate **Message Relayer** (or CDC tool like Debezium) polls the `OUTBOX` table and publishes to Kafka.
3. This guarantees **At-Least-Once Delivery** and ensures the DB and Message Broker are always in sync.

---

## 🚀 The SDE-4/Architect Signal: Cell-Based Architecture
If asked how to scale a social network to 100M+ users without a single point of failure:
"We use **Cell-Based Architecture**. We partition our entire stack (LB, Web, DB) into independent 'Cells' (e.g., 1M users per cell). If Cell A goes down, it has **zero impact** on Cell B. This limits the 'Blast Radius' of any failure."

---

## 🛡️ 3. Idempotency <a name="idempotency"></a>
An operation is **Idempotent** if performing it multiple times has the same effect as performing it once.
- *Crucial for:* Retry logic in distributed systems.
- *Implementation:* Use an **Idempotency Key** (e.g., Request UUID) stored in Redis/DB.

---

## 🚪 4. API Gateways & Proxies <a name="gateway"></a>

### API Gateway
A single entry point for all clients. Handles:
- Authentication & Authorization.
- Rate Limiting.
- Request Routing & Aggregation.

### Reverse Proxy (Nginx/HAProxy)
- Terminates SSL.
- Handles Load Balancing.
- Caches static content.

---

## 🏗️ 5. Modern Infrastructure Patterns

### Sidecar Pattern (C089, C100)
- **Concept:** A separate container that runs alongside your application container in the same "Pod" (Kubernetes).
- **Purpose:** Offload cross-cutting concerns (Logging, Service Discovery, Security/mTLS) so your application code stays pure.
- **Example:** Istio uses an Envoy proxy as a sidecar.

### Ambassador Pattern (C101)
- **Concept:** A specialized sidecar that acts as a client-side proxy for outgoing requests.
- **Purpose:** Handles retries, logging, and circuit breaking for external APIs.

### Strangler Fig Pattern (C099)
- **Concept:** Incrementally migrating a Monolith to Microservices by "strangling" old features with new ones.
- **Goal:** Move logic feature-by-feature rather than a "Big Bang" rewrite.

### Anti-Corruption Layer (ACL) (C102)
- **Concept:** A translation layer between two systems that have different data models or semantics.
- **Purpose:** Prevents your new clean system from being "corrupted" by a messy legacy system's model.

---

## 🚫 The SDE-2 Interview Tip
If you suggest Microservices, the interviewer will ask: **"How do you handle a failure in Service B when Service A calls it?"**
*Answer:* "I would use a **Circuit Breaker** to stop cascading failures and provide a **Fallback** response."

---

## 🧠 Tracker Integration (C084-C105)

*   **Core Trade-off:** Microservices (Flexibility) vs Monolith (Simplicity).
*   **The "Senior Signal":** Knowing when to use a **Sidecar** to keep business logic clean.
*   **Interview Trap:** Splitting a monolith too early when the scale doesn't justify the operational overhead.

### 🔬 Self-Assessment Prompts
1. What is the difference between a Sidecar and an Ambassador?
2. When should you use a "Modular Monolith" instead of Microservices?
3. How does CQRS help in building a high-performance Read model?

