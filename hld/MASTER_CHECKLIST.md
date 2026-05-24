# 🎯 HLD Mastery Checklist: Phase-Wise System Design Guide

This checklist organizes the core evaluation points, architecture invariants, and SDE-2/SDE-3 design bars across all 9 phases of your System Design curriculum. Before wrapping up each phase, verify that you can confidently explain and whiteboard these requirements under pressure.

---

## 🗺️ Phase 1: Scalability & Network Foundations
*Focus: Protocols, scaling dimensions, and transport limits.*

### 🧠 Core Concepts to Explain
- [ ] **Latency vs. Throughput**: Explain why optimizing for one can degrade the other (e.g. queueing, batching).
- [ ] **HTTP/1.1 vs. HTTP/2 vs. HTTP/3**: Discuss multiplexing, Head-of-Line (HoL) blocking at TCP level, and QUIC UDP transport.
- [ ] **WebRTC vs. WebSockets**: Explain the peer-to-peer connection flow (ICE, STUN, TURN) vs. persistent full-duplex TCP client-server sockets.

### ⚖️ Trade-off Decisions
- [ ] **Horizontal vs. Vertical Scaling**: Detail the points where vertical scaling hits resource limits (CPU/RAM bus speeds) vs. the partition complexity and network hops of horizontal scaling.
- [ ] **Long Polling vs. WebSockets**: When to choose stateless polling (e.g. low-frequency client notifications) vs. stateful WebSockets (high-frequency chat/feeds).

### 📐 Whiteboard Invariants
- [ ] Can you diagram the exact flow of a WebRTC session establishment including signaling servers, STUN/TURN, and direct connection?
- [ ] Can you estimate the bandwidth, egress, and API latency budget for a 10M DAU feed system?

---

## 🗺️ Phase 2: High-Performance Caching & CDNs
*Focus: Caching patterns, resilient topologies, and data structure limits.*

### 🧠 Core Concepts to Explain
- [ ] **Cache-Aside vs. Write-Through vs. Write-Behind**: Explain data flow and write paths.
- [ ] **Cache Eviction**: Contrast LRU, LFU, and FIFO under different query access distributions (zipfian vs. uniform).
- [ ] **Probabilistic Data Structures**: Explain how Bloom Filters and HyperLogLog function with zero false negatives (for Bloom) and approximate cardinality.

### ⚖️ Trade-off Decisions
- [ ] **Write-Behind (Write-Back) Caching**: Weigh the latency gains of writing to memory first against the risk of dirty reads and data loss during node failure.
- [ ] **Local Cache vs. Distributed Cache Cluster**: Weigh the low latency of in-memory local maps against the synchronization overhead, consistency challenges, and memory footprint.

### 📐 Whiteboard Invariants
- [ ] **Cache Stampede / Thundering Herd**: Diagram how you protect your database using mutex locking (e.g. single-flight pattern) or background pre-heating when keys expire.
- [ ] **Cache Penetration & Avalanche**: Show how you handle missing keys (using Bloom Filters/caching null values) and node failures (using consistent hashing and jittered TTLs).

---

## 🗺️ Phase 3: Single-Node Databases & Storage Engines
*Focus: Index layouts, transaction ACID, and disk access patterns.*

### 🧠 Core Concepts to Explain
- [ ] **B-Trees vs. LSM-Trees**: Explain write amplification, read amplification, and page-based updates vs. append-only SSTables + compaction.
- [ ] **Write-Ahead Log (WAL) & Commit Path**: Detail how ACID durability is guaranteed before modifications are written to volatile indexes.
- [ ] **MVCC (Multi-Version Concurrency Control)**: Explain how read transactions are isolated from concurrent write transactions using snapshot reads.

### ⚖️ Trade-off Decisions
- [ ] **Row-Oriented vs. Columnar Storage**: When to choose OLTP engines (MySQL/Postgres) for point lookups vs. OLAP engines (Parquet/ClickHouse) for analytical aggregations.
- [ ] **Normal vs. Denormalized Schema**: When to join tables for normalization (consistency, no duplicates) vs. denormalizing to avoid expensive runtime joins.

### 📐 Whiteboard Invariants
- [ ] Can you sketch the step-by-step write path of an LSM-Tree engine (MemTable $\rightarrow$ WAL $\rightarrow$ SSTable compaction)?
- [ ] Can you explain the difference between Phantom Reads and Non-Repeatable Reads, and which SQL Isolation Level prevents each?

---

## 🗺️ Phase 4: Distributed Routing, Load Balancing & Resilience
*Focus: Layer 4/7 proxies, consistent hashing, and failure recovery.*

### 🧠 Core Concepts to Explain
- [ ] **Layer 4 vs. Layer 7 Load Balancing**: Contrast IP/TCP packet routing (NAT, LVS) with application-layer parsing (Nginx, Envoy) supporting SSL termination and path routing.
- [ ] **Consistent Hashing**: Explain how consistent hashing rings with virtual nodes prevent hot-spotting and minimize partition re-sharding when nodes join/leave.
- [ ] **Circuit Breakers**: Describe the three states (Closed, Open, Half-Open) and transition rules based on error thresholds.

### ⚖️ Trade-off Decisions
- [ ] **Rate Limiting Algorithms**: Weigh Token Bucket (bursty traffic) vs. Sliding Window Log (no boundary spikes but high memory usage).
- [ ] **Active-Active vs. Active-Passive Routing**: Weigh the load distribution of active-active against the split-brain and synchronization complexities.

### 📐 Whiteboard Invariants
- [ ] Diagram a multi-layered boundary routing tier: Geo-DNS $\rightarrow$ Anycast IP Edge $\rightarrow$ L4 Load Balancer $\rightarrow$ L7 API Gateway with TLS termination $\rightarrow$ Service Mesh sidecars.

---

## 🗺️ Phase 5: Database Scaling, Distributed Time & Consensus
*Focus: Partitioning, CAP/PACELC theorems, replication, and clocks.*

### 🧠 Core Concepts to Explain
- [ ] **CAP vs. PACELC Theorems**: Detail why PACELC is a more complete model (evaluating Consistency vs. Latency during normal operations).
- [ ] **Paxos vs. Raft Consensus**: Explain leader election, log replication, safety invariants, and split-brain resolution (quorums).
- [ ] **Distributed Clocks**: Discuss Lamport Clocks and Vector Clocks (logical time, causality) vs. NTP synchronization limits (TrueTime API).

### ⚖️ Trade-off Decisions
- [ ] **Strong vs. Eventual Consistency**: Compare transactional safety (Read-Your-Writes, Linearizability) with high throughput and low-latency availability.
- [ ] **Saga vs. Two-Phase Commit (2PC)**: Compare blocking, coordinate-dependent 2PC with async, rollback-driven, idempotent Saga Orchestration / Choreography.

### 📐 Whiteboard Invariants
- [ ] Diagram how a Saga orchestrator recovers from a shipping failure, showing the forward execution path, step failures, and compensation trigger sequence in reverse order.
- [ ] Diagram consistent hashing node ring layout with replica routing paths.

---

## 🗺️ Phase 6: Async Messaging & Event-Driven Systems
*Focus: Queues, streaming logs, delivery guarantees, and backpressure.*

### 🧠 Core Concepts to Explain
- [ ] **Message Queue vs. Append-only Log**: Contrast broker-centric queues (RabbitMQ: message state tracked by broker, ACK deletes) with partition-based logs (Kafka: offset tracked by client, persistent logs).
- [ ] **Exactly-Once Processing**: Explain how idempotency keys, transactional coordinator writes, and upstream deduplication are required to achieve exactly-once delivery.
- [ ] **Backpressure Topologies**: Explain how system components handle consumer slow-downs (blocking, dropping, buffering, reactive pull loops).

### ⚖️ Trade-off Decisions
- [ ] **Push vs. Pull Message Delivery**: Weigh the latency gains of push models against the consumer saturation risks, and contrasting with pull-based batching.
- [ ] **Lambda vs. Kappa Stream Architectures**: Compare maintaining separate batch/speed layers (Lambda) with a unified, replayable log-based speed layer (Kappa).

### 📐 Whiteboard Invariants
- [ ] Diagram the Kafka Consumer Group partition rebalancing sequence (Group Coordinator $\rightarrow$ Consumer Join $\rightarrow$ Reassignment $\rightarrow$ Offset commit).
- [ ] Can you diagram stream windowing strategies: Tumbling, Sliding, Session?

---

## 🗺️ Phase 7: Cloud Storage, GIS & Specialized Storage
*Focus: Object stores, GIS indexes, Lucene inverted indexes, and Vector DBs.*

### 🧠 Core Concepts to Explain
- [ ] **Object Storage Internals**: Detail the separation of metadata services (partitioned SQL/NoSQL indexes) from physical storage block nodes.
- [ ] **Geohashing vs. Quadtrees**: Explain how proximity search (spatial coordinates) is indexed using space-filling curves (z-order) vs. hierarchical tree splits.
- [ ] **Lucene Inverted Index**: Explain term dictionaries, posting lists, segment merge compaction, and TF-IDF/BM25 scoring algorithms.

### ⚖️ Trade-off Decisions
- [ ] **Data Lakes vs. Data Warehouses**: When to store unstructured raw files (S3, Parquet) for ML/data-science vs. structured relational warehouses (Snowflake, BigQuery) for SQL BI analytics.
- [ ] **Vector Search ANN Algorithms**: Contrast HNSW (hierarchical graphs) with IVF (inverted file flat quantization) on query speed vs. recall accuracy trade-offs.

### 📐 Whiteboard Invariants
- [ ] Sketch how Google Maps resolves a bounding box query `findNearbyDrivers(lat, lon, radius = 5km)` using Geohash ranges.
- [ ] Diagram the write path of an inverted index document insertion.

---

## 🗺️ Phase 8: Microservice Architecture & Security
*Focus: Microservice patterns, data modeling, authentication, and secrets.*

### 🧠 Core Concepts to Explain
- [ ] **CQRS (Command Query Responsibility Segregation)**: Explain decoupling read and write models, updating read replicas asynchronously via CDC (Change Data Capture) or domain events.
- [ ] **Event Sourcing**: Explain representing state as a sequence of immutable event logs.
- [ ] **OAuth 2.0 vs. OpenID Connect**: Distinguish authorization delegation (Access Tokens) from identity verification (ID Tokens, ID Cards, JWTs).

### ⚖️ Trade-off Decisions
- [ ] **Database-per-Service vs. Shared Database**: Weigh domain boundary isolation and independent scaling against transaction boundaries and join overheads.
- [ ] **JWT vs. Stateful Session Token**: Weigh stateless token verification (low DB lookups) against revocation latency and payload size overheads.

### 📐 Whiteboard Invariants
- [ ] Diagram the transactional outbox pattern to guarantee that a database update and a corresponding Kafka domain event are executed atomically.
- [ ] Can you diagram the OAuth 2.0 Authorization Code flow with PKCE?

---

## 🗺️ Phase 9: Observability, Ops & Chaos Engineering
*Focus: Logs, tracing, SLOs, and proactive failure testing.*

### 🧠 Core Concepts to Explain
- [ ] **The 3 Pillars of Observability**: Explain Metrics (numerical aggregations), Logs (unstructured/structured event strings), and Distributed Traces (span IDs, context propagation).
- [ ] **SLI vs. SLO vs. SLA**: Contrast the metrics (latency, error rate) with target objectives (e.g. 99.9% success rate) and service level agreements.
- [ ] **Chaos Engineering Invariants**: Discuss hypothesis validation, blast radius containment, and simulated network partitions/CPU resource starvation.

### ⚖️ Trade-off Decisions
- [ ] **Sampling Traces**: Weigh the storage/network overhead of tracing 100% of requests against probabilistic sampling (e.g. 1%) or tail-based sampling (capturing errors/high latency).
- [ ] **Symptom-based vs. Cause-based Alerting**: Why page on symptoms (high error rate, user latency) instead of cause-level alerts (high CPU, single thread pool saturation).

### 📐 Whiteboard Invariants
- [ ] Diagram context propagation in a microservice trace (tracing headers `traceparent` passed across HTTP/gRPC boundaries).
- [ ] Calculate the latency budget allocation of a checkout API that invokes 4 downstream services in parallel vs. series.
