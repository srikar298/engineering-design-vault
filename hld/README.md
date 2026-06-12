# 🗺️ High-Level Design (HLD) Master Learning Curriculum

Welcome to the HLD Concept Dictionary. This index structures **145 enterprise-grade system design topics** into a progressive learning path from fundamentals to advanced distributed engineering.

Use this curriculum sequentially to build system design intuition and ace SDE-2/SDE-3 engineering interviews.

## 🧭 Roadmap of Phases

| Phase | Concept Range | Focus Area |
| :--- | :--- | :--- |
| **[Phase 1: Scalability & Network Foundations](#phase-1-scalability--network-foundations)** | `C001 - C014` | Learn basic networking protocols, scaling definitions, latency vs. throughput trade-offs, and communication patterns (gRPC, WebSockets, WebRTC, Long Polling). |
| **[Phase 2: High-Performance Caching & CDNs](#phase-2-high-performance-caching--cdns)** | `C015 - C030` | Master caching patterns (Cache-Aside, Write-Behind), cache eviction algorithms (LRU, LFU), globally-distributed CDNs, and critical cache failures (Stampede, Avalanche, Bloom Filters). |
| **[Phase 3: Single-Node Databases & Storage Engines](#phase-3-single-node-databases--storage-engines)** | `C031 - C042` | Deep-dive into SQL vs. NoSQL decisions, index data structures (B-Trees vs. LSM-Trees), WAL write paths, MVCC concurrency, query optimization, and OLTP vs. OLAP disk layouts. |
| **[Phase 4: Distributed Routing, Load Balancing & Resilience](#phase-4-distributed-routing,-load-balancing--resilience)** | `C043 - C059` | Understand Layer 4 vs. Layer 7 load balancing, consistent hashing, API gateways, boundary security (WAF, TLS handshake), circuit breakers, rate limiters, and microservice meshes. |
| **[Phase 5: Database Scaling, Distributed Time & Consensus](#phase-5-database-scaling,-distributed-time--consensus)** | `C060 - C091` | Study data partitioning/sharding, CAP/PACELC theorems, logical replication limits, distributed clocks (Lamport, Vector), Paxos/Raft consensus, distributed locking, etcd/ZooKeeper config management, and 2PC/Saga transactions. |
| **[Phase 6: Async Messaging & Event-Driven Systems](#phase-6-async-messaging--event-driven-systems)** | `C092 - C104` | Explore queues vs. pub-sub (Kafka vs. RabbitMQ), consumer groups, delivery semantics (Exactly-Once), DLQs, backpressure, event streaming, and stream processing windows/checkpointing (Lambda/Kappa). |
| **[Phase 7: Cloud Storage, GIS & Specialized Storage](#phase-7-cloud-storage,-gis--specialized-storage)** | `C105 - C113` | Scale storage to petabytes using S3 Object Storage, Columnar databases, Data Lakes vs. Warehouses (BigQuery/Snowflake), HDFS/MapReduce, Geohashing proximity search, inverted index full-text search (Lucene), and Vector/Time-series databases. |
| **[Phase 8: Microservice Architecture & Security](#phase-8-microservice-architecture--security)** | `C114 - C136` | Implement architectural decomposition patterns (CQRS, Event Sourcing, Transactional Outbox, ACL, Strangler Fig, Webhooks, Edge workers), database-per-service, fan-out scaling, frontend rendering/performance, user credentials hashing vs encryption, JWT verification, and Secrets Management. |
| **[Phase 9: Observability, Ops & Chaos Engineering](#phase-9-observability,-ops--chaos-engineering)** | `C137 - C145` | Run production-grade operations using Metrics, Logs, Traces (OpenTelemetry), SLI/SLO calculations, latency budgets, symptom alerting, flame graph profiling, and Chaos Engineering. |

---

## 🗺️ Phase 1: Scalability & Network Foundations

> **Overview:** Learn basic networking protocols, scaling definitions, latency vs. throughput trade-offs, and communication patterns (gRPC, WebSockets, WebRTC, Long Polling).

| ID | Concept Topic | Location | Difficulty | Frequency |
| :--- | :--- | :--- | :--- | :--- |
| `C001` | [Vertical vs. Horizontal Scaling](./02-Scale-From-Zero/01-Vertical-vs-Horizontal-Scaling.md) | `02-Scale-From-Zero` | 🟢 Easy | 🔥 High |
| `C002` | [Latency vs. Throughput](./02-Scale-From-Zero/03-Latency-vs-Throughput.md) | `02-Scale-From-Zero` | 🟢 Easy | 🔥 High |
| `C003` | [Capacity Estimation](./02-Scale-From-Zero/02-Capacity-Estimation.md) | `02-Scale-From-Zero` | 🟡 Medium | 🔥 High |
| `C004` | [TCP vs UDP](./01-Networking-Basics/01-TCP-vs-UDP.md) | `01-Networking-Basics` | 🟢 Easy | 🔥 High |
| `C005` | [DNS Resolution](./01-Networking-Basics/03-DNS-Resolution.md) | `01-Networking-Basics` | 🟢 Easy | 🔥 High |
| `C006` | [TLS/SSL](./01-Networking-Basics/02-TLS-SSL.md) | `01-Networking-Basics` | 🟡 Medium | 🔥 High |
| `C007` | [HTTP/1.1 vs HTTP/2 vs HTTP/3](./01-Networking-Basics/04-HTTP-Versions.md) | `01-Networking-Basics` | 🟡 Medium | 🔥 High |
| `C008` | [Content Negotiation](./01-Networking-Basics/07-Content-Negotiation.md) | `01-Networking-Basics` | 🟢 Easy | 🟡 Medium |
| `C009` | [REST vs GraphQL](./01-Networking-Basics/06-REST-vs-GraphQL.md) | `01-Networking-Basics` | 🟡 Medium | 🔥 High |
| `C010` | [gRPC](./01-Networking-Basics/05-gRPC.md) | `01-Networking-Basics` | 🟡 Medium | 🔥 High |
| `C011` | [Long Polling](./01-Networking-Basics/10-Long-Polling.md) | `01-Networking-Basics` | 🟢 Easy | 🟡 Medium |
| `C012` | [Server-Sent Events (SSE)](./01-Networking-Basics/09-Server-Sent-Events.md) | `01-Networking-Basics` | 🟢 Easy | 🟡 Medium |
| `C013` | [WebSockets](./01-Networking-Basics/08-WebSockets.md) | `01-Networking-Basics` | 🟡 Medium | 🔥 High |
| `C014` | [WebRTC and Real-Time Media](./01-Networking-Basics/11-WebRTC-and-Real-Time-Media.md) | `01-Networking-Basics` | 🔴 Hard | 🟡 Medium |

---

## 🗺️ Phase 2: High-Performance Caching & CDNs

> **Overview:** Master caching patterns (Cache-Aside, Write-Behind), cache eviction algorithms (LRU, LFU), globally-distributed CDNs, and critical cache failures (Stampede, Avalanche, Bloom Filters).

| ID | Concept Topic | Location | Difficulty | Frequency |
| :--- | :--- | :--- | :--- | :--- |
| `C015` | [Cache Fundamentals](./04-Caching-Deep-Dive/01-Cache-Fundamentals.md) | `04-Caching-Deep-Dive` | 🟢 Easy | 🔥 High |
| `C016` | [Cache-Aside (Lazy Loading)](./04-Caching-Deep-Dive/04-Cache-Aside.md) | `04-Caching-Deep-Dive` | 🟢 Easy | 🔥 High |
| `C017` | [Write-Through Cache](./04-Caching-Deep-Dive/05-Write-Through-Cache.md) | `04-Caching-Deep-Dive` | 🟢 Easy | 🟡 Medium |
| `C018` | [Write-Behind Cache](./04-Caching-Deep-Dive/06-Write-Behind-Cache.md) | `04-Caching-Deep-Dive` | 🟡 Medium | 🟡 Medium |
| `C019` | [Refresh-Ahead](./04-Caching-Deep-Dive/07-Refresh-Ahead.md) | `04-Caching-Deep-Dive` | 🟡 Medium | 🔴 Low |
| `C020` | [Cache Eviction - TTL (Time-to-Live)](./04-Caching-Deep-Dive/08-Cache-Eviction-TTL.md) | `04-Caching-Deep-Dive` | 🟢 Easy | 🔥 High |
| `C021` | [Cache Eviction - LRU (Least Recently Used)](./04-Caching-Deep-Dive/09-Cache-Eviction-LRU.md) | `04-Caching-Deep-Dive` | 🟡 Medium | 🔥 High |
| `C022` | [Cache Eviction - LFU (Least Frequently Used)](./04-Caching-Deep-Dive/10-Cache-Eviction-LFU.md) | `04-Caching-Deep-Dive` | 🟡 Medium | 🟡 Medium |
| `C023` | [Distributed Cache](./04-Caching-Deep-Dive/02-Distributed-Cache.md) | `04-Caching-Deep-Dive` | 🟡 Medium | 🔥 High |
| `C024` | [CDN (Content Delivery Network)](./04-Caching-Deep-Dive/03-CDN.md) | `04-Caching-Deep-Dive` | 🟢 Easy | 🔥 High |
| `C025` | [Cache Penetration](./04-Caching-Deep-Dive/11-Cache-Penetration.md) | `04-Caching-Deep-Dive` | 🟡 Medium | 🔥 High |
| `C026` | [Cache Stampede (Cache Breakdown)](./04-Caching-Deep-Dive/12-Cache-Stampede.md) | `04-Caching-Deep-Dive` | 🟡 Medium | 🔥 High |
| `C027` | [Cache Avalanche (Cache Snowslide)](./04-Caching-Deep-Dive/13-Cache-Avalanche.md) | `04-Caching-Deep-Dive` | 🟢 Easy | 🔥 High |
| `C028` | [Cache Warming (Pre-heating)](./04-Caching-Deep-Dive/14-Cache-Warming.md) | `04-Caching-Deep-Dive` | 🟢 Easy | 🟡 Medium |
| `C029` | [Connection Pooling](./04-Caching-Deep-Dive/15-Connection-Pooling.md) | `04-Caching-Deep-Dive` | 🟡 Medium | 🔥 High |
| `C030` | [Bloom Filters](./04-Caching-Deep-Dive/16-Bloom-Filters.md) | `04-Caching-Deep-Dive` | 🟡 Medium | 🔥 High |

---

## 🗺️ Phase 3: Single-Node Databases & Storage Engines

> **Overview:** Deep-dive into SQL vs. NoSQL decisions, index data structures (B-Trees vs. LSM-Trees), WAL write paths, MVCC concurrency, query optimization, and OLTP vs. OLAP disk layouts.

| ID | Concept Topic | Location | Difficulty | Frequency |
| :--- | :--- | :--- | :--- | :--- |
| `C031` | [SQL vs. NoSQL Decision](./05-Databases/01-SQL-vs-NoSQL-Decision.md) | `05-Databases` | 🟢 Easy | 🔥 High |
| `C032` | [Database Indexing](./05-Databases/02-Database-Indexing.md) | `05-Databases` | 🟢 Easy | 🔥 High |
| `C033` | [Database Indexing (Clustered/Non-Clustered)](./05-Databases/09-Clustered-vs-Non-Clustered-Indexing.md) | `05-Databases` | 🟡 Medium | 🔥 High |
| `C034` | [B-Trees vs. LSM-Trees](./05-Databases/07-B-Trees-vs-LSM-Trees.md) | `05-Databases` | 🟠 Hard | 🔥 High |
| `C035` | [Buffer Pool & Cache Management](./05-Databases/08-Buffer-Pool-and-Cache-Management.md) | `05-Databases` | 🟡 Medium | 🟡 Medium |
| `C036` | [Write-Ahead Logging (WAL)](./05-Databases/12-Write-Ahead-Logging.md) | `05-Databases` | 🟡 Medium | 🔥 High |
| `C037` | [MVCC (Multi-Version Concurrency Control)](./05-Databases/10-MVCC.md) | `05-Databases` | 🔥 Hard | 🔥 High |
| `C038` | [Query Optimizer & Execution Plans](./05-Databases/11-Query-Optimizer-and-Execution-Plans.md) | `05-Databases` | 🟡 Medium | 🔥 High |
| `C039` | [Query Optimization](./05-Databases/03-Query-Optimization.md) | `05-Databases` | 🟡 Medium | 🔥 High |
| `C040` | [Denormalization](./05-Databases/04-Denormalization.md) | `05-Databases` | 🟢 Easy | 🟡 Medium |
| `C041` | [Connection Pooling](./05-Databases/05-Connection-Pooling.md) | `05-Databases` | 🟡 Medium | 🔥 High |
| `C042` | [OLTP vs. OLAP](./05-Databases/06-OLTP-vs-OLAP.md) | `05-Databases` | 🟢 Easy | 🟡 Medium |

---

## 🗺️ Phase 4: Distributed Routing, Load Balancing & Resilience

> **Overview:** Understand Layer 4 vs. Layer 7 load balancing, consistent hashing, API gateways, boundary security (WAF, TLS handshake), circuit breakers, rate limiters, and microservice meshes.

| ID | Concept Topic | Location | Difficulty | Frequency |
| :--- | :--- | :--- | :--- | :--- |
| `C043` | [Load Balancer Types](./03-Load-Balancing/03-Load-Balancer-Types.md) | `03-Load-Balancing` | 🟢 Easy | 🔥 High |
| `C044` | [Load Balancing Algorithms](./03-Load-Balancing/02-LB-Algorithms.md) | `03-Load-Balancing` | 🟢 Easy | 🔥 High |
| `C045` | [Consistent Hashing](./03-Load-Balancing/01-Consistent-Hashing.md) | `03-Load-Balancing` | 🟡 Medium | 🔥 High |
| `C046` | [Health Checks](./03-Load-Balancing/04-Health-Checks.md) | `03-Load-Balancing` | 🟢 Easy | 🔥 High |
| `C047` | [Reverse Proxy](./03-Load-Balancing/05-Reverse-Proxy.md) | `03-Load-Balancing` | 🟢 Easy | 🔥 High |
| `C048` | [API Gateway](./03-Load-Balancing/06-API-Gateway.md) | `03-Load-Balancing` | 🟢 Easy | 🔥 High |
| `C049` | [Network Security](./11-Security-Basics/03-Network-Security.md) | `11-Security-Basics` | 🟡 Medium | 🔥 High |
| `C050` | [TLS/SSL Handshake](./11-Security-Basics/07-TLS-SSL-Handshake.md) | `11-Security-Basics` | 🟡 Medium | 🔥 High |
| `C051` | [Timeouts](./09-System-Resiliency/05-Timeouts.md) | `09-System-Resiliency` | 🟢 Easy | 🔥 High |
| `C052` | [Retry with Backoff](./09-System-Resiliency/04-Retry-with-Backoff.md) | `09-System-Resiliency` | 🟢 Easy | 🔥 High |
| `C053` | [Circuit Breaker](./09-System-Resiliency/01-Circuit-Breaker.md) | `09-System-Resiliency` | 🟡 Medium | 🔥 High |
| `C054` | [Bulkhead Pattern](./09-System-Resiliency/02-Bulkhead-Pattern.md) | `09-System-Resiliency` | 🟡 Medium | 🟡 Medium |
| `C055` | [Rate Limiting](./09-System-Resiliency/03-Rate-Limiting.md) | `09-System-Resiliency` | 🟡 Medium | 🔥 High |
| `C056` | [Ambassador Pattern](./13-Architectural-Patterns/01-Ambassador-Pattern.md) | `13-Architectural-Patterns` | 🟡 Medium | 🟡 Medium |
| `C057` | [Sidecar Pattern](./13-Architectural-Patterns/10-Sidecar.md) | `13-Architectural-Patterns` | 🟢 Easy | 🔥 High |
| `C058` | [Service Discovery](./13-Architectural-Patterns/14-Service-Discovery.md) | `13-Architectural-Patterns` | 🟡 Medium | 🟡 Medium |
| `C059` | [Service Mesh](./13-Architectural-Patterns/15-Service-Mesh.md) | `13-Architectural-Patterns` | 🔥 Hard | 🟡 Medium |

---

## 🗺️ Phase 5: Database Scaling, Distributed Time & Consensus

> **Overview:** Study data partitioning/sharding, CAP/PACELC theorems, logical replication limits, distributed clocks (Lamport, Vector), Paxos/Raft consensus, distributed locking, etcd/ZooKeeper config management, and 2PC/Saga transactions.

| ID | Concept Topic | Location | Difficulty | Frequency |
| :--- | :--- | :--- | :--- | :--- |
| `C060` | [Database Replication](./07-Database-Scaling/01-Database-Replication.md) | `07-Database-Scaling` | 🟡 Medium | 🔥 High |
| `C061` | [Read Replicas](./07-Database-Scaling/02-Read-Replicas.md) | `07-Database-Scaling` | 🟢 Easy | 🔥 High |
| `C062` | [Database Partitioning](./07-Database-Scaling/03-Database-Partitioning.md) | `07-Database-Scaling` | 🟡 Medium | 🟡 Medium |
| `C063` | [Database Sharding](./07-Database-Scaling/04-Database-Sharding.md) | `07-Database-Scaling` | 🔥 Hard | 🔥 High |
| `C064` | [Database Federation](./07-Database-Scaling/05-Database-Federation.md) | `07-Database-Scaling` | 🟢 Easy | 🟡 Medium |
| `C065` | [Multi-Region Topologies](./16-Multi-Region-Architectures/01-Multi-Region-Topologies.md) | `16-Multi-Region-Architectures` | 🟡 Medium | 🔥 High |
| `C066` | [Write Conflict Resolution](./16-Multi-Region-Architectures/02-Write-Conflict-Resolution.md) | `16-Multi-Region-Architectures` | 🔴 Hard | 🔥 High |
| `C067` | [ACID Properties](./10-Consistency-Models/01-ACID-Properties.md) | `10-Consistency-Models` | 🟡 Medium | 🔥 High |
| `C068` | [BASE Properties](./10-Consistency-Models/02-BASE-Properties.md) | `10-Consistency-Models` | 🟢 Easy | 🟡 Medium |
| `C069` | [CAP Theorem](./10-Consistency-Models/03-CAP-Theorem.md) | `10-Consistency-Models` | 🟢 Easy | 🔥 High |
| `C070` | [PACELC Theorem](./10-Consistency-Models/04-PACELC-Theorem.md) | `10-Consistency-Models` | 🟡 Medium | 🔥 High |
| `C071` | [Strong Consistency](./10-Consistency-Models/05-Strong-Consistency.md) | `10-Consistency-Models` | 🟢 Easy | 🔥 High |
| `C072` | [Eventual Consistency](./10-Consistency-Models/06-Eventual-Consistency.md) | `10-Consistency-Models` | 🟢 Easy | 🔥 High |
| `C073` | [Monotonic Reads](./10-Consistency-Models/09-Monotonic-Reads.md) | `10-Consistency-Models` | 🟡 Medium | 🟡 Medium |
| `C074` | [Read-Your-Writes Consistency](./10-Consistency-Models/10-Read-Your-Writes.md) | `10-Consistency-Models` | 🟡 Medium | 🔥 High |
| `C075` | [Heartbeat Mechanism](./20-Distributed-Consensus/04-Heartbeat-Mechanism.md) | `20-Distributed-Consensus` | 🟢 Easy | 🔥 High |
| `C076` | [Gossip Protocol](./20-Distributed-Consensus/03-Gossip-Protocol.md) | `20-Distributed-Consensus` | 🟡 Medium | 🟡 Medium |
| `C077` | [Lamport Timestamps](./20-Distributed-Consensus/05-Lamport-Timestamps.md) | `20-Distributed-Consensus` | 🟡 Medium | 🟡 Medium |
| `C078` | [Vector Clocks](./20-Distributed-Consensus/06-Vector-Clocks.md) | `20-Distributed-Consensus` | 🔥 Hard | 🟡 Medium |
| `C079` | [Causal-Consistency](./10-Consistency-Models/07-Causal-Consistency.md) | `10-Consistency-Models` | 🟡 Medium | 🟡 Medium |
| `C080` | [Linearizability](./10-Consistency-Models/08-Linearizability.md) | `10-Consistency-Models` | 🟡 Medium | 🔥 High |
| `C081` | [Merkle Trees](./20-Distributed-Consensus/08-Merkle-Trees.md) | `20-Distributed-Consensus` | 🟡 Medium | 🟡 Medium |
| `C082` | [Leader Election](./20-Distributed-Consensus/07-Leader-Election.md) | `20-Distributed-Consensus` | 🟡 Medium | 🔥 High |
| `C083` | [Paxos Algorithm](./20-Distributed-Consensus/09-Paxos-Algorithm.md) | `20-Distributed-Consensus` | 🔥 Hard | 🟡 Medium |
| `C084` | [Raft Consensus](./20-Distributed-Consensus/10-Raft-Consensus.md) | `20-Distributed-Consensus` | 🔥 Hard | 🔥 High |
| `C085` | [Quorum](./20-Distributed-Consensus/11-Quorum.md) | `20-Distributed-Consensus` | 🟡 Medium | 🔥 High |
| `C086` | [Distributed Locking](./20-Distributed-Consensus/01-Distributed-Locking.md) | `20-Distributed-Consensus` | 🟡 Medium | 🔥 High |
| `C087` | [Fencing Tokens](./20-Distributed-Consensus/02-Fencing-Tokens.md) | `20-Distributed-Consensus` | 🟡 Medium | 🟡 Medium |
| `C088` | [Distributed Configuration Management](./20-Distributed-Consensus/12-Distributed-Configuration-Management.md) | `20-Distributed-Consensus` | 🟡 Medium | 🔥 High |
| `C089` | [Distributed Transactions](./08-Distributed-Transactions/01-Distributed-Transactions.md) | `08-Distributed-Transactions` | 🔥 Hard | 🔥 High |
| `C090` | [Two-Phase Commit (2PC)](./08-Distributed-Transactions/03-Two-Phase-Commit.md) | `08-Distributed-Transactions` | 🟡 Medium | 🔥 High |
| `C091` | [Saga Pattern](./08-Distributed-Transactions/02-Saga-Pattern.md) | `08-Distributed-Transactions` | 🟡 Medium | 🔥 High |

---

## 🗺️ Phase 6: Async Messaging & Event-Driven Systems

> **Overview:** Explore queues vs. pub-sub (Kafka vs. RabbitMQ), consumer groups, delivery semantics (Exactly-Once), DLQs, backpressure, event streaming, and stream processing windows/checkpointing (Lambda/Kappa).

| ID | Concept Topic | Location | Difficulty | Frequency |
| :--- | :--- | :--- | :--- | :--- |
| `C092` | [Message Queue Fundamentals](./06-Message-Queues/01-Message-Queue-Fundamentals.md) | `06-Message-Queues` | 🟢 Easy | 🔥 High |
| `C093` | [Pub/Sub Pattern](./06-Message-Queues/03-Pub-Sub-Pattern.md) | `06-Message-Queues` | 🟢 Easy | 🔥 High |
| `C094` | [Delivery Guarantees (At-Most-Once, At-Least-Once, Exactly-Once)](./06-Message-Queues/02-Delivery-Guarantees.md) | `06-Message-Queues` | 🟡 Medium | 🔥 High |
| `C095` | [Consumer Groups](./06-Message-Queues/04-Consumer-Groups.md) | `06-Message-Queues` | 🟡 Medium | 🔥 High |
| `C096` | [Dead Letter Queue (DLQ)](./06-Message-Queues/05-Dead-Letter-Queue.md) | `06-Message-Queues` | 🟡 Medium | 🔥 High |
| `C097` | [Backpressure](./06-Message-Queues/06-Backpressure.md) | `06-Message-Queues` | 🟡 Medium | 🟡 Medium |
| `C098` | [Kafka Architecture](./06-Message-Queues/07-Kafka-Architecture.md) | `06-Message-Queues` | 🔥 Hard | 🔥 High |
| `C099` | [Kafka vs RabbitMQ](./06-Message-Queues/08-Kafka-vs-RabbitMQ.md) | `06-Message-Queues` | 🟡 Medium | 🔥 High |
| `C100` | [Event-Driven Architecture (EDA)](./06-Message-Queues/09-Event-Driven-Architecture.md) | `06-Message-Queues` | 🟡 Medium | 🔥 High |
| `C101` | [Event Streaming](./06-Message-Queues/10-Event-Streaming.md) | `06-Message-Queues` | 🟡 Medium | 🟡 Medium |
| `C102` | [Lambda vs Kappa Architecture](./15-Stream-Processing-and-Backpressure/01-Lambda-vs-Kappa-Architecture.md) | `15-Stream-Processing-and-Backpressure` | 🟡 Medium | 🟡 Medium |
| `C103` | [Stream Processing Windows](./15-Stream-Processing-and-Backpressure/02-Stream-Processing-Windows.md) | `15-Stream-Processing-and-Backpressure` | 🟡 Medium | 🟡 Medium |
| `C104` | [Exactly-Once Processing](./15-Stream-Processing-and-Backpressure/03-Exactly-Once-Processing.md) | `15-Stream-Processing-and-Backpressure` | 🔴 Hard | 🔥 High |

---

## 🗺️ Phase 7: Cloud Storage, GIS & Specialized Storage

> **Overview:** Scale storage to petabytes using S3 Object Storage, Columnar databases, Data Lakes vs. Warehouses (BigQuery/Snowflake), HDFS/MapReduce, Geohashing proximity search, inverted index full-text search (Lucene), and Vector/Time-series databases.

| ID | Concept Topic | Location | Difficulty | Frequency |
| :--- | :--- | :--- | :--- | :--- |
| `C105` | [S3 & Object Storage](./14-Object-Storage/01-S3-and-Object-Storage.md) | `14-Object-Storage` | 🟢 Easy | 🔥 High |
| `C106` | [Columnar vs. Row Storage](./14-Object-Storage/02-Columnar-vs-Row-Storage.md) | `14-Object-Storage` | 🟡 Medium | 🔥 High |
| `C107` | [Data Lakes vs. Data Warehouses](./14-Object-Storage/03-Data-Lakes-vs-Data-Warehouses.md) | `14-Object-Storage` | 🟢 Easy | 🟡 Medium |
| `C108` | [Data Warehousing (BigQuery & Snowflake)](./14-Object-Storage/04-Data-Warehousing-BigQuery-Snowflake.md) | `14-Object-Storage` | 🟡 Medium | 🔥 High |
| `C109` | [HDFS & MapReduce](./14-Object-Storage/05-HDFS-and-MapReduce.md) | `14-Object-Storage` | 🟡 Medium | 🟡 Medium |
| `C110` | [Geohashing and Proximity Search](./23-Specialized-Storage-and-GIS/01-Geohashing-and-Proximity-Search.md) | `23-Specialized-Storage-and-GIS` | 🟡 Medium | 🔥 High |
| `C111` | [Full-Text Search (Lucene Internals)](./23-Specialized-Storage-and-GIS/02-Full-Text-Search-Lucene-Internals.md) | `23-Specialized-Storage-and-GIS` | 🟡 Medium | 🔥 High |
| `C112` | [Vector Databases and ANN (Approximate Nearest Neighbors)](./23-Specialized-Storage-and-GIS/03-Vector-Databases-and-ANN.md) | `23-Specialized-Storage-and-GIS` | 🔴 Hard | 🔥 High |
| `C113` | [Time-Series Databases](./23-Specialized-Storage-and-GIS/04-Time-Series-Databases.md) | `23-Specialized-Storage-and-GIS` | 🟡 Medium | 🟡 Medium |

---

## 🗺️ Phase 8: Microservice Architecture & Security

> **Overview:** Implement architectural decomposition patterns (CQRS, Event Sourcing, Transactional Outbox, ACL, Strangler Fig, Webhooks, Edge workers), database-per-service, fan-out scaling, frontend rendering/performance, user credentials hashing vs encryption, JWT verification, and Secrets Management.

| ID | Concept Topic | Location | Difficulty | Frequency |
| :--- | :--- | :--- | :--- | :--- |
| `C114` | [Monolith vs. Microservices](./13-Architectural-Patterns/13-Monolith-vs-Microservices.md) | `13-Architectural-Patterns` | 🟢 Easy | 🔥 High |
| `C115` | [Microservices Architecture](./13-Architectural-Patterns/12-Microservices.md) | `13-Architectural-Patterns` | 🟡 Medium | 🔥 High |
| `C116` | [Database per Service](./13-Architectural-Patterns/05-Database-per-Service.md) | `13-Architectural-Patterns` | 🟢 Easy | 🔥 High |
| `C117` | [Shared Database Pattern](./13-Architectural-Patterns/09-Shared-Database.md) | `13-Architectural-Patterns` | 🟢 Easy | 🟡 Medium |
| `C118` | [API Composition](./13-Architectural-Patterns/03-API-Composition.md) | `13-Architectural-Patterns` | 🟢 Easy | 🔥 High |
| `C119` | [CQRS (Command Query Responsibility Segregation)](./13-Architectural-Patterns/04-CQRS.md) | `13-Architectural-Patterns` | 🔥 Hard | 🔥 High |
| `C120` | [Event Sourcing](./13-Architectural-Patterns/06-Event-Sourcing.md) | `13-Architectural-Patterns` | 🔥 Hard | 🟡 Medium |
| `C121` | [Transactional Outbox Pattern](./13-Architectural-Patterns/08-Outbox-Pattern.md) | `13-Architectural-Patterns` | 🟡 Medium | 🔥 High |
| `C122` | [Idempotency](./13-Architectural-Patterns/07-Idempotency.md) | `13-Architectural-Patterns` | 🟢 Easy | 🔥 High |
| `C123` | [Anti-Corruption Layer (ACL)](./13-Architectural-Patterns/02-Anti-Corruption-Layer.md) | `13-Architectural-Patterns` | 🟡 Medium | 🟡 Medium |
| `C124` | [Strangler Fig Pattern](./13-Architectural-Patterns/11-Strangler-Fig.md) | `13-Architectural-Patterns` | 🟢 Easy | 🔥 High |
| `C125` | [Webhook Architectures](./13-Architectural-Patterns/16-Webhook-Architectures.md) | `13-Architectural-Patterns` | 🟢 Easy | 🔥 High |
| `C126` | [Edge Computing and Serverless](./13-Architectural-Patterns/17-Edge-Computing-and-Serverless.md) | `13-Architectural-Patterns` | 🟡 Medium | 🔥 High |
| `C127` | [Fan-out Strategies](./17-Data-Modeling-at-Scale/01-Fan-out-Strategies.md) | `17-Data-Modeling-at-Scale` | 🟡 Medium | 🔥 High |
| `C128` | [Wide-Column Database Design](./17-Data-Modeling-at-Scale/02-Wide-Column-Database-Design.md) | `17-Data-Modeling-at-Scale` | 🔴 Hard | 🔥 High |
| `C129` | [Rendering Strategies](./18-Frontend-System-Design/01-Rendering-Strategies.md) | `18-Frontend-System-Design` | 🟢 Easy | 🔥 High |
| `C130` | [Frontend Performance and Caching](./18-Frontend-System-Design/02-Frontend-Performance-and-Caching.md) | `18-Frontend-System-Design` | 🟡 Medium | 🔥 High |
| `C131` | [Hashing vs. Encryption](./11-Security-Basics/01-Hashing-vs-Encryption.md) | `11-Security-Basics` | 🟢 Easy | 🔥 High |
| `C132` | [JSON Web Tokens (JWT)](./11-Security-Basics/02-JWT.md) | `11-Security-Basics` | 🟢 Easy | 🔥 High |
| `C133` | [OAuth 2.0 & OpenID Connect (OIDC)](./11-Security-Basics/04-OAuth-and-OpenID-Connect.md) | `11-Security-Basics` | 🟡 Medium | 🔥 High |
| `C134` | [RBAC vs. ABAC](./11-Security-Basics/05-RBAC-vs-ABAC.md) | `11-Security-Basics` | 🟢 Easy | 🟡 Medium |
| `C135` | [Secrets Management](./11-Security-Basics/06-Secrets-Management.md) | `11-Security-Basics` | 🟢 Easy | 🔥 High |
| `C136` | [TLS/SSL Handshake](./11-Security-Basics/07-TLS-SSL-Handshake.md) | `11-Security-Basics` | 🟡 Medium | 🔥 High |

---

## 🗺️ Phase 9: Observability, Ops & Chaos Engineering

> **Overview:** Run production-grade operations using Metrics, Logs, Traces (OpenTelemetry), SLI/SLO calculations, latency budgets, symptom alerting, flame graph profiling, and Chaos Engineering.

| ID | Concept Topic | Location | Difficulty | Frequency |
| :--- | :--- | :--- | :--- | :--- |
| `C137` | [The Three Pillars of Observability](./12-Observability-and-Ops/01-Three-Pillars.md) | `12-Observability-and-Ops` | 🟢 Easy | 🔥 High |
| `C138` | [Metrics Collection](./12-Observability-and-Ops/02-Metrics-Collection.md) | `12-Observability-and-Ops` | 🟢 Easy | 🔥 High |
| `C139` | [Log Aggregation](./12-Observability-and-Ops/03-Log-Aggregation.md) | `12-Observability-and-Ops` | 🟡 Medium | 🔥 High |
| `C140` | [Distributed Tracing](./12-Observability-and-Ops/04-Distributed-Tracing.md) | `12-Observability-and-Ops` | 🟡 Medium | 🔥 High |
| `C141` | [SLIs, SLOs, and SLAs](./12-Observability-and-Ops/05-SLI-SLO-SLA.md) | `12-Observability-and-Ops` | 🟢 Easy | 🔥 High |
| `C142` | [Latency Budgets & Percentiles](./12-Observability-and-Ops/06-Latency-Budgets.md) | `12-Observability-and-Ops` | 🟡 Medium | 🔥 High |
| `C143` | [Alerting & On-Call Excellence](./12-Observability-and-Ops/07-Alerting.md) | `12-Observability-and-Ops` | 🟢 Easy | 🔥 High |
| `C144` | [Identify Bottlenecks](./12-Observability-and-Ops/08-Identify-Bottlenecks.md) | `12-Observability-and-Ops` | 🟡 Medium | 🔥 High |
| `C145` | [Chaos Engineering](./12-Observability-and-Ops/09-Chaos-Engineering.md) | `12-Observability-and-Ops` | 🟡 Medium | 🟡 Medium |

---

## 🎯 Case Studies & Real Systems Gallery

Apply your theoretical foundations to real-world system design questions:
*   [🔗 SD001: Design a URL Shortener (Bit.ly)](./22-Real-Systems-Gallery/SD001-URL-Shortener.md)
*   [🛑 SD002: Design a Distributed Rate Limiter](./22-Real-Systems-Gallery/SD002-Rate-Limiter.md)

## 📚 Reference Handbooks
*   [🌐 2025 HLD Master Guide: The SDE-2+ Standard](./2025_HLD_SDE2_MASTER_GUIDE.md)
*   [📚 DDIA Master Synopsis: Theory to Architecture](./DDIA-MASTER-SYNOPSIS.md)
*   [🚀 HLD Delivery Framework: The 45-Minute Interview Strategy](./HLD_DELIVERY_FRAMEWORK.md)
*   [🧰 HLD Components Library: System Design Building Blocks](./24-components-library/README.md)