# 🚀 06 - Addons: Senior LLD Extensions

This section contains "Expert Level" LLD patterns and architectural improvements that go beyond the standard GoF (Gang of Four) patterns. These are designed to showcase **Engineering Judgment** and **Advanced Problem Solving** during senior-level interviews (SDE-2/SDE-3).

---

## 🛠️ Included Addons

### 1. [Advanced Rule Engine](./01-Rule-Engine/)
- **Patterns:** Strategy + Composite + Specification.
- **Why it matters:** Most real-world business logic (Promotions, Risk Scoring, Validation) is too complex for simple `if-else` blocks. This demonstrates how to build a composable, testable engine using a Fluent API.

### 2. [Distributed Lock Simulator](./02-Distributed-Lock/)
- **Patterns:** Lease Expiration + Heartbeat + Concurrency Control.
- **Why it matters:** In modern distributed architectures, single-process locks aren't enough. This simulator demonstrates the critical thinking needed for cross-node resource protection, including handling "crash-and-stay-dead" scenarios.

### 3. [Plugin Architecture (SPI)](./03-Plugin-System/)
- **Patterns:** ServiceLoader (SPI) + Inversion of Control.
- **Why it matters:** This is the ultimate "Open-Closed Principle" (OCP) implementation. It shows how to build an core system that is completely decoupled from its extensions, a common requirement for large-scale platforms (like IDEs or Payment Gateways).

### 4. [Thread-Safe LRU Cache](./04-LRU-Cache/)
- **Patterns:** HashMap + Doubly Linked List + Fine-grained Locking.
- **Why it matters:** A classic senior LLD problem that tests your understanding of data structures, time complexity (O(1)), and concurrency. This implementation avoids global synchronization for better performance.

---

## 🤖 AI-Native Architecture (SDE-2+ Level)

### 5. [AI Semantic Cache](./05-AI-Semantic-Cache/)
- **Patterns:** Proxy + Fuzzy Matching (ACL).
- **Why it matters:** Cost and latency are the biggest bottlenecks in AI systems. This proxy implementation demonstrates how to reduce API calls by returning semantically similar cached results.

### 6. [AI Agent Tool-Use Registry](./06-AI-Agent-Tool-Registry/)
- **Patterns:** Command + Registry (Inversion of Control).
- **Why it matters:** Building reliable AI agents requires a secure way to execute local functions. This registry decouples the AI orchestrator from the tool execution and adds a layer of validation (ACL).

### 7. [AI Token-based Rate Limiter](./07-AI-Token-Rate-Limiter/)
- **Patterns:** Sliding Window Log + Token Estimation.
- **Why it matters:** Modern AI Gateways must limit **TPM (Tokens Per Minute)** rather than just request count. This implementation uses a sliding window log for high-precision resource protection.

---

## 🌐 Distributed & High-Scale Systems (SDE-3+ Level)

### 8. [Transactional Outbox](./08-Transactional-Outbox/)
- **Patterns:** Transactional Outbox + At-Least-Once Delivery.
- **Why it matters:** In distributed systems, keeping a database and message broker in sync is a classic "Reliability" challenge. This ensures total consistency even during broker failures.

### 9. [Adaptive Load Shedding](./09-Adaptive-Load-Shedding/)
- **Patterns:** Load Shedding + Priority Queuing.
- **Why it matters:** High-scale systems (10k+ users) need a "self-protection" layer. This implementation rejects non-critical traffic during peak load to save the core business path.

### 10. [API Versioning Strategy](./10-API-Versioning/)
- **Patterns:** Versioned Router + Decorator.
- **Why it matters:** Essential for long-term maintainability. This shows how to evolve an API without breaking thousands of existing clients.

---

## 💡 How to Use These
1.  **Read the Code:** Each file contains `Senior SDE-2 Insights` and `Interview-MVP` sections.
2.  **Understand the Trade-offs:** These patterns add complexity in exchange for flexibility and robustness. Be ready to explain *why* you would choose these over simpler alternatives.
3.  **Practice Implementations:** Try building these from scratch in < 15 minutes to sharpen your "Vibe Coding" skills.

---

> **"A Senior Engineer doesn't just write code that works. They write code that can be changed easily and survives failure gracefully."**
