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

## ⚡ Advanced Caching (HLD + LLD Parallel)

### 11. [Thread-Safe LFU Cache](./11-LFU-Cache/)
- **Patterns:** HashMap + LinkedHashSet + Frequency Sets.
- **Why it matters:** A senior-level alternative to LRU. It tests your ability to handle $O(1)$ complexity using multiple synchronized data structures.

### 12. [Resilient Cache Client](./12-Cache-Client/)
- **Patterns:** Decorator + Strategy + Fallback.
- **Why it matters:** In distributed systems, a cache is a network dependency. This implementation shows how to add reliability (Retries) and robustness (DB Fallbacks) using clean object-oriented decorators.

### 13. [Local Cache Basics](./13-Local-Cache-Basics/)
- **Patterns:** ConcurrentHashMap + Atomic Compute.
- **Why it matters:** The starting point for all caching. It demonstrates why `ConcurrentHashMap` is preferred for high-concurrency environments and how to prevent local "Cache Stampedes" using atomic operations.

---

## 📘 DDIA Mastery (Distributed Systems Invariants)

### 14. [Binary Serialization](./14-Binary-Serialization/)
- **Concepts:** DDIA Chapter 4. JSON vs. Protobuf simulation.
- **Why it matters:** For 10k+ concurrent users, the CPU and bandwidth cost of parsing JSON is a major bottleneck. This proves you understand data encoding efficiency.

### 15. [Lamport Logical Clocks](./15-Lamport-Clocks/)
- **Concepts:** DDIA Chapter 8. Total Ordering of Events.
- **Why it matters:** Physical clocks drift. This implementation shows how to order events across multiple servers using logical timestamps instead of wall-clock time.

### 16. [Version Vectors](./16-Version-Vectors/)
- **Concepts:** DDIA Chapter 5. Multi-Leader Conflict Detection.
- **Why it matters:** Essential for multi-region systems. This algorithm detects if two updates are "concurrent" or if one "happened-before" the other.

### 17. [Distributed Leader Election](./17-Leader-Election/)
- **Concepts:** DDIA Chapter 9. High Availability & Failover.
- **Why it matters:** Demonstrates how a cluster of nodes automatically recovers when the Master crashes, ensuring zero downtime for your 10k users.

---

## 🏆 Top-Tier Industry Clones (Machine Coding)

### 18. [Uber Task Scheduler](./18-Advanced-Task-Scheduler/)
- **Concepts:** Concurrency + PriorityQueue + Delay Logic.
- **Why it matters:** A classic problem asked at Uber/Amazon. It tests your ability to handle multi-threaded execution, recurring tasks, and precise timing without using basic loops.

### 19. [Custom Logging Library](./19-Logging-Library/)
- **Patterns:** Chain of Responsibility + Singleton + Strategy.
- **Why it matters:** Mirrors the architecture of log4j. It proves you can design a complex utility used by millions of developers, focusing on extensibility and clean APIs.

### 20. [Uber App LLD](./20-Uber-LLD/)
- **Concepts:** Spatial Indexing + Atomic Concurrency + Pricing Strategy.
- **Why it matters:** The ultimate SDE-2 test. It combines real-world requirements (finding nearby drivers) with senior-level constraints (surge pricing, race conditions).

---

## 💡 How to Use These
1.  **Read the Code:** Each file contains `Senior SDE-2 Insights` and `Interview-MVP` sections.
2.  **Understand the Trade-offs:** These patterns add complexity in exchange for flexibility and robustness. Be ready to explain *why* you would choose these over simpler alternatives.
3.  **Practice Implementations:** Try building these from scratch in < 15 minutes to sharpen your "Vibe Coding" skills.

---

> **"A Senior Engineer doesn't just write code that works. They write code that can be changed easily and survives failure gracefully."**
