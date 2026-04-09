# 🏗️ 13 - Architectural Patterns (Monolith, Microservices, & Beyond)

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
How to handle transactions across microservices.
- **Choreography**: Services exchange events without a central controller.
- **Orchestration**: A central "Saga Manager" coordinates the workflow.

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

## 🚀 The SDE-2 Interview Tip
If you suggest Microservices, the interviewer will ask: **"How do you handle a failure in Service B when Service A calls it?"**
*Answer:* "I would use a **Circuit Breaker** to stop cascading failures and provide a **Fallback** response."
