# 🗺️ HLD Mastery OS: Gap Analysis & Alignment Map

This document maps the **Concept IDs (C001-C112)** from your tracking sheet to the local codebase. It identifies where content is solid, thin, or missing.

---

## 🟢 PHASE 1: FUNDAMENTALS & NETWORKING

| Concept ID | Topic Name | Codebase File | Status |
| :--- | :--- | :--- | :--- |
| **C001** | Vertical vs Horizontal Scaling | `hld/00-Fundamentals/README.md` | ✅ Solid |
| **C002** | CAP Theorem | `hld/10-Consistency-Models/README.md` | ✅ Solid |
| **C003** | ACID Properties | `hld/00-Fundamentals/README.md` | ✅ Solid |
| **C004** | BASE Properties | `hld/00-Fundamentals/README.md` | ✅ Solid |
| **C005** | Eventual Consistency | `hld/10-Consistency-Models/README.md` | ✅ Solid |
| **C006** | Strong Consistency | `hld/10-Consistency-Models/README.md` | ✅ Solid |
| **C007** | Linearizability | `hld/10-Consistency-Models/README.md` | 🟡 Thin |
| **C008** | Causal Consistency | `hld/10-Consistency-Models/README.md` | 🟡 Thin |
| **C009** | Read-Your-Writes | `hld/10-Consistency-Models/README.md` | 🟡 Thin |
| **C010** | Monotonic Reads | `hld/10-Consistency-Models/README.md` | 🟡 Thin |
| **C011** | HTTP/1.1 vs 2 vs 3 | `hld/01-Networking-Basics/README.md` | ✅ Solid |
| **C012** | WebSockets | `hld/01-Networking-Basics/README.md` | ✅ Solid |
| **C013** | Long Polling | `hld/01-Networking-Basics/README.md` | ✅ Solid |
| **C014** | Server-Sent Events | `hld/01-Networking-Basics/README.md` | 🟡 Thin |
| **C015** | gRPC | `hld/19-API-Design-gRPC-vs-REST/README.md` | ✅ Solid |
| **C016** | REST vs GraphQL | `hld/19-API-Design-gRPC-vs-REST/README.md` | ✅ Solid |
| **C017** | DNS Resolution | `hld/01-Networking-Basics/README.md` | ✅ Solid |
| **C018** | TCP vs UDP | `hld/01-Networking-Basics/README.md` | ✅ Solid |
| **C019** | TLS/SSL | `hld/11-Security-Basics/README.md` | 🟡 Thin |
| **C020** | Content Negotiation | `hld/19-API-Design-gRPC-vs-REST/README.md` | ✅ Solid *(fixed 2026-05-26)* |

---

## 🟡 PHASE 2: DATABASES & CACHING

| Concept ID | Topic Name | Codebase File | Status |
| :--- | :--- | :--- | :--- |
| **C021** | SQL vs NoSQL Decision | `hld/05-Databases/README.md` | ✅ Solid |
| **C022** | Database Indexing | `hld/05-Databases/README.md` | ✅ Solid |
| **C023** | Database Sharding | `hld/07-Database-Scaling/README.md` | ✅ Solid |
| **C024** | Database Replication | `hld/07-Database-Scaling/README.md` | ✅ Solid |
| **C025** | Read Replicas | `hld/07-Database-Scaling/README.md` | ✅ Solid |
| **C026** | Database Partitioning | `hld/07-Database-Scaling/README.md` | ✅ Solid |
| **C027** | Connection Pooling | `hld/05-Databases/README.md` | 🟡 Thin |
| **C028** | Query Optimization | `hld/05-Databases/README.md` | 🟡 Thin |
| **C029** | Distributed Transactions | `hld/08-Distributed-Transactions/README.md` | ✅ Solid |
| **C030** | Database Federation | `hld/07-Database-Scaling/README.md` | ✅ Solid |
| **C033-39** | NoSQL Types (Graph, NewSQL, etc.)| `hld/05-Databases/README.md` | 🟡 Thin (Need dedicated sections) |
| **C040-53** | Caching (Redis, CDN, Stamps) | `hld/04-Caching-Deep-Dive/README.md` | ✅ Solid |

---

## 🟠 PHASE 3: DISTRIBUTED SYSTEMS & MESSAGING

| Concept ID | Topic Name | Codebase File | Status |
| :--- | :--- | :--- | :--- |
| **C054-65** | Messaging (Kafka vs Rabbit) | `hld/06-Message-Queues/README.md` | ✅ Solid |
| **C066** | Consistent Hashing | `hld/03-Load-Balancing/README.md` | ✅ Solid |
| **C067-69** | Leader Election, Raft, Paxos | `hld/20-Distributed-Consensus/README.md` | ✅ Solid |
| **C070-71** | Vector/Lamport Clocks | `hld/20-Distributed-Consensus/08-Vector-and-Lamport-Clocks.md` | ✅ Solid *(fixed 2026-05-26)* |
| **C072** | Gossip Protocol | `hld/20-Distributed-Consensus/README.md` | 🟡 Thin |
| **C073** | Bloom Filters | `hld/04-Caching-Deep-Dive/07-Bloom-Filters.md` | ✅ Solid *(fixed 2026-05-26)* |
| **C074** | Merkle Trees | `hld/07-Database-Scaling/06-Merkle-Trees.md` | ✅ Solid *(fixed 2026-05-26)* |
| **C075** | Distributed Locking | `hld/20-Distributed-Consensus/09-Distributed-Locking.md` | ✅ Solid *(fixed 2026-05-26)* |
| **C077** | Saga Pattern | `hld/08-Distributed-Transactions/README.md` | ✅ Solid |
| **C079** | Quorum | `hld/20-Distributed-Consensus/README.md` | ✅ Solid |

---

## 🔴 PHASE 4: INFRASTRUCTURE & PATTERNS

| Concept ID | Topic Name | Codebase File | Status |
| :--- | :--- | :--- | :--- |
| **C081-83** | LB, Reverse Proxy | `hld/03-Load-Balancing/README.md` | ✅ Solid |
| **C084** | API Gateway | `hld/13-Architectural-Patterns/README.md` | ✅ Solid |
| **C085** | Service Mesh | `hld/13-Architectural-Patterns/README.md` | 🟡 Thin |
| **C086-87** | Monolith vs Microservices | `hld/13-Architectural-Patterns/README.md` | ✅ Solid |
| **C089** | Sidecar Pattern | `hld/13-Architectural-Patterns/19-Sidecar-and-Ambassador-Pattern.md` | ✅ Solid *(fixed 2026-05-26)* |
| **C090-95** | Resiliency (Circuit, Retry) | `hld/09-System-Resiliency/README.md` | ✅ Solid |
| **C096-98** | CQRS, Event Sourcing, Outbox | `hld/13-Architectural-Patterns/README.md` | ✅ Solid |
| **C099-105** | Strangler Fig, Ambassador, ACL | `hld/13-Architectural-Patterns/18-Strangler-Fig-and-ACL.md` + `19-Sidecar-and-Ambassador-Pattern.md` | ✅ Solid *(fixed 2026-05-26)* |
| **C106-112** | Observability (ELK, Metrics) | `hld/12-Observability-and-Ops/README.md` | ✅ Solid |

---

## 🏗️ ACTION PLAN: Status Update (2026-05-26)

### ✅ Resolved This Session
- **C020** Content Negotiation → `hld/19-API-Design-gRPC-vs-REST/README.md` (full rewrite)
- **C070-71** Vector/Lamport Clocks → `hld/20-Distributed-Consensus/08-Vector-and-Lamport-Clocks.md` (427 lines)
- **C073** Bloom Filters → `hld/04-Caching-Deep-Dive/07-Bloom-Filters.md` (460 lines)
- **C074** Merkle Trees → `hld/07-Database-Scaling/06-Merkle-Trees.md` (513 lines)
- **C075** Distributed Locking → `hld/20-Distributed-Consensus/09-Distributed-Locking.md` (455 lines)
- **C089** Sidecar Pattern → `hld/13-Architectural-Patterns/19-Sidecar-and-Ambassador-Pattern.md` (564 lines)
- **C099-101** Strangler Fig + ACL → `hld/13-Architectural-Patterns/18-Strangler-Fig-and-ACL.md` (429 lines)
- **Cursor Pagination** → `hld/19-API-Design-gRPC-vs-REST/02-Pagination-Strategies.md` (new file)

### 🟡 Still Remaining (Thin — needs expansion)
- **C007-C010** Linearizability, Causal Consistency, Read-Your-Writes, Monotonic Reads (in `10-Consistency-Models`)
- **C014** Server-Sent Events (thin in `01-Networking-Basics`)
- **C027** Connection Pooling (thin in `05-Databases`)
- **C028** Query Optimization (thin in `05-Databases`)
- **C072** Gossip Protocol (thin in `20-Distributed-Consensus`)
- **C085** Service Mesh (now partially covered in `19-Sidecar-and-Ambassador-Pattern.md`)
