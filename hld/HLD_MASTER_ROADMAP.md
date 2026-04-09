# 🗺️ System Design (HLD) Master Roadmap

> **"From Zero to Founding Engineer: A structured path to mastering High-Level Design."**

This roadmap is designed to take you from foundational concepts to advanced distributed systems invariants. It is structured by complexity and importance, ensuring a solid base before tackling SDE-2/3 trade-offs.

---

## 🟢 Phase 1: The Fundamentals (Foundations)
*Estimated Time: 1–2 weeks*
The goal here is to speak the language of System Design.

1.  **[Functional vs Non-Functional Requirements](./00-Fundamentals/README.md#requirements)**: Top 3 features vs. SLOs/SLAs.
2.  **[Scalability Basics](./02-Scale-From-Zero/)**: Vertical vs Horizontal, Scaling from 1 to 10M users.
3.  **[Availability & Reliability](./00-Fundamentals/README.md#availability)**: The "Nines" (99.9%), Fault tolerance.
4.  **[SLIs, SLOs, and Latency Budgets](./00-Fundamentals/README.md#availability)**: Calculating time for each component.
5.  **[Consistency Models](./10-Consistency-Models/)**: Strong vs Eventual consistency.
6.  **[Latency vs Throughput](./00-Fundamentals/README.md#latency)**: Response time vs requests per unit time.
7.  **[CAP & PACELC Theorems](./10-Consistency-Models/)**: The fundamental trade-offs of distributed data.
8.  **[Capacity Estimation](./00-Fundamentals/README.md#capacity)**: Back-of-the-envelope math for storage, bandwidth, and CPU.

---

## 🟡 Phase 2: Component Deep Dives (Building Blocks)
*Estimated Time: 2–3 weeks*
Understanding the tools in your toolbox and how they fail.

1.  **[Networking & Infra](./01-Networking-Basics/)**: DNS, HTTP/HTTPS, CDN, TCP vs UDP.
2.  **[Load Balancing](./03-Load-Balancing/)**: Algorithms, L4 vs L7, Consistent Hashing.
3.  **[Databases Overview](./05-Databases/)**: SQL vs NoSQL, Indexing, Storage Engines (B-Trees vs LSM).
4.  **[Database Scaling](./07-Database-Scaling/)**: Replication (Leader-Follower), Sharding, Quorums.
5.  **[Caching Mastery](./04-Caching-Deep-Dive/)**: Write policies, Eviction, Thundering Herd, Multi-layer caching.
6.  **[Messaging & Streams](./06-Message-Queues/)**: Pub-Sub, Kafka vs RabbitMQ.
7.  **[File & Object Storage](./14-Object-Storage/)**: S3, HDFS, Blob Storage for media at scale.
8.  **[API Gateway & Proxies](./13-Architectural-Patterns/README.md#gateway)**: Ingress, Auth, Rate Limiting.

---

## 🟠 Phase 3: Architectural Patterns & Resiliency
*Estimated Time: 1-2 weeks*
How components interact to form a reliable system.

1.  **[Monolith vs Microservices](./13-Architectural-Patterns/)**: When to split and why.
2.  **[Event-Driven Architecture](./13-Architectural-Patterns/)**: CQRS, Saga Pattern, Idempotency.
3.  **[Stream Processing & Backpressure](./15-Stream-Processing-and-Backpressure/)**: Flow control and Exactly-once processing.
4.  **[Multi-Region Architectures](./16-Multi-Region-Architectures/)**: Active-Active vs. Active-Passive and Disaster Recovery.
5.  **[Data Modeling at Scale](./17-Data-Modeling-at-Scale/)**: Normalization vs. Denormalization and Fan-out strategies.
6.  **[System Resiliency](./09-System-Resiliency/)**: Circuit Breakers, Bulkheads, Retries with Jitter.
7.  **[Distributed Transactions](./08-Distributed-Transactions/)**: 2PC vs Sagas.
8.  **[Security Basics](./11-Security-Basics/)**: Auth (OAuth/JWT), Encryption, VPCs.
9.  **[Observability & Ops](./12-Observability-and-Ops/)**: Metrics (Prometheus), Logging (ELK), Tracing (Jaeger).
10. **[Frontend System Design](./18-Frontend-System-Design/)**: Rendering strategies and Micro-frontends.

---

## 🔴 Phase 4: Case Studies & Mock Drills
*Estimated Time: 2–3 weeks*
Translating requirements into blueprints.

- **[HLD Delivery Framework](./HLD_DELIVERY_FRAMEWORK.md)**: The step-by-step interview strategy.
- **[Practice Questions](./HLD_PRACTICE_QUESTIONS.md)**: 20+ real-world problems (Bit.ly to Uber).
- **[DDIA Master Synopsis](./DDIA-MASTER-SYNOPSIS.md)**: Deep dive into distributed data invariants.

---

## 📚 Recommended Resources
- **Roadmap.sh (System Design)**: The industry standard visual roadmap.
- **HelloInterview**: "System Design in a Hurry" (Essentials & framing).
- **System Design Primer (GitHub)**: The classic open-source guide.
- **Alex Xu (ByteByteGo)**: Best visual explanations for frameworks.
- **Designing Data-Intensive Applications (Kleppmann)**: The "Bible" of SDE-3 design.
- **Take U Forward / Apna College**: Excellent video roadmaps for HLD/LLD.
