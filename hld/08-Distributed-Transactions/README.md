# 🤝 08 - Distributed Transactions (The SDE-3 Edge)

## 📖 The Concept
In a microservices architecture, a single business workflow (e.g., Booking a trip: Flight + Hotel + Car) spans multiple independent databases. Ensuring all succeed, or all fail together, is the problem of Distributed Transactions.

## 📊 The SDE-2 Trade-off Table: 2PC vs Saga

| Feature | Two-Phase Commit (2PC) | Saga Pattern |
| :--- | :--- | :--- |
| **Mechanism** | Coordinator asks all DBs to prepare, then commit. | Sequence of local transactions triggering the next via events. |
| **Consistency** | **Strong Consistency** (ACID). | **Eventual Consistency** (BASE). |
| **Performance** | Slow. Locks resources across services during the "prepare" phase. | Fast. No cross-service locking. |
| **Failure Handling** | Built-in abort/rollback. | Requires writing custom **Compensating Transactions** (Undo operations). |

## 🚫 The Interview Trap
**"I will use Two-Phase Commit to ensure data integrity."**
2PC is notoriously anti-scalable. It is a blocking protocol. If one node is slow, the entire transaction blocks. It is rarely used in modern microservices.
*Better Answer:* "2PC doesn't scale well for high-throughput microservices. I will use the Saga Pattern to maintain eventual consistency and high availability."

## 🚀 The SDE-3 Edge: Saga Orchestration vs Choreography
If the interviewer asks: *"How do you coordinate a Saga for a complex 5-step checkout process?"*

**The SDE-3 Solution:**
1. **Choreography (Event-driven):** Service A emits an event, Service B listens and reacts. 
    *   *Pros:* Decoupled.
    *   *Cons:* Hard to track the overall status. Debugging is a nightmare ("Where did the transaction get stuck?").
2. **Orchestration (Command-driven):** A central "Saga Orchestrator" service tells each service what to do.
    *   *Pros:* Complete visibility into the transaction state. Easy to handle rollbacks.
    *   *Cons:* Introduces a single point of failure (though the Orchestrator itself can be highly available). For complex workflows (like 5 steps), Orchestration is almost always preferred by seniors.
