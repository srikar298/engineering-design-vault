# 🌐 2025 HLD Master Guide: The SDE-2+ Standard

> **"SDE-1s build features. SDE-2s navigate trade-offs. SDE-3s protect invariants."**

Welcome to the **High-Level Design (HLD)** master class. This curriculum is explicitly designed for engineers targeting **SDE-2** roles, with enough **SDE-3 (Senior/Founding)** depth to guarantee a "Strong Hire" rating.

---

## ⚖️ The SDE-2 Mindset: Trade-offs Over Tools
In a modern system design interview, saying "I will use Kafka" gets you 0 points. Saying "I will use Kafka because we need log-based persistence and replayability, despite the higher operational overhead compared to RabbitMQ" gets you the job.

Every module here is structured around the **Trade-off Table**. You must know *why* you are choosing a technology, what it will cost, and how it fails.

---

## 📈 The Curriculum

### 🟢 LEVEL 1: The Basics (Brief Refresher)
*Focus: Concepts you must know flawlessly before discussing architecture.*
- [01 - Networking Basics](./01-Networking-Basics/): DNS, CDN, TCP/UDP, HTTP/2 vs HTTP/3.
- [02 - Scale From Zero](./02-Scale-From-Zero/): Vertical vs Horizontal Scaling, the Stateless Web Tier.

### 🟡 LEVEL 2: The SDE-2 Core (Deep Dive)
*Focus: The bulk of SDE-2 interviews. Mastering these guarantees a solid performance.*
- [03 - Load Balancing](./03-Load-Balancing/): Algorithms and the critical importance of **Consistent Hashing**.
- [04 - Caching Deep Dive](./04-Caching-Deep-Dive/): Redis vs Memcached, Write Policies, Eviction, Thundering Herd mitigation.
- [05 - Databases](./05-Databases/): Relational (ACID) vs NoSQL (Base). When to use Document vs Column-family vs Graph.
- [06 - Message Queues](./06-Message-Queues/): Log-based (Kafka) vs Queue-based (RabbitMQ/SQS). Asynchronous architectures.

### 🟠 LEVEL 3: The SDE-3 Edge (The "Strong Hire" Factor)
*Focus: Advanced distributed systems concepts that set you apart as a Senior or Founding Engineer.*
- [07 - Database Scaling](./07-Database-Scaling/): Sharding strategies, Replication lag, and Leader-Follower anomalies.
- [08 - Distributed Transactions](./08-Distributed-Transactions/): 2PC vs Saga Pattern (Choreography vs Orchestration).
- [09 - System Resiliency](./09-System-Resiliency/): Circuit Breakers, Bulkheads, and Distributed Rate Limiting (Sliding Window Log).
- [10 - Consistency Models](./10-Consistency-Models/): CAP Theorem, PACELC, Strong vs Eventual Consistency, Quorums.

---

## 🎯 How to Use This Guide
For each module, focus on three things:
1.  **The Concept**: Be able to explain it in two sentences.
2.  **The Trade-off Table**: Memorize the exact scenarios where Tool A beats Tool B.
3.  **The Interview Trap**: Learn the common mistakes candidates make so you can actively avoid them during your whiteboard session.
