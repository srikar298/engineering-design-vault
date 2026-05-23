# 🏢 SD016 - Distributed Cache

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Problem ID** | SD016 |
| **Category** | Infrastructure |
| **Difficulty** | 🔴 Hard |
| **Interview Frequency** | 🔥 Must Know |
| **Target Companies** | Meta, Twitter |
| **Status** | Not Started |
| **Times Practiced** | 0 |
| **Last Practiced** | YYYY-MM-DD |
| **Next Review** | YYYY-MM-DD |
| **Confidence** | 🔴 None / 🟡 Conceptual / 🟢 Applied |
| **Mastery** | 🔴 Familiar / 🟡 Competent / 🟢 Expert |

---

## 📋 1. Core Requirements & Scale

### Functional Requirements
- [ ] Feature 1: User can ...
- [ ] Feature 2: System should ...
- [ ] Feature 3: ...

### Non-Functional Requirements
- **High Availability**: Target 99.99% availability.
- **Low Latency**: P99 read latency < 100ms.
- **Data Durability**: Zero data loss for state changes.
- **Scalability**: Designed to scale gracefully under traffic spikes.

### Scale Targets (Back-of-the-Envelope)
- **Daily Active Users (DAU)**: 10M - 100M+
- **Write throughput**: ~1,000 - 10,000 RPS peak
- **Read throughput**: ~10,000 - 100,000+ RPS peak
- **Storage Estimate**: 100GB - 1TB+ storage per day
- **Bandwidth Estimate**: Egress: XX MB/s, Ingress: XX MB/s

---

## 📐 2. High-Level Architecture
```
               [ Client Requests ]
                       │
                       ▼
               [ Load Balancer ]
                       │
         ┌─────────────┴─────────────┐
         ▼                           ▼
   [ Write API ]               [ Read API ]
         │                           │
         ▼                           ▼
 [ Message Queue ]             [ Cache Cluster ]
         │                           │
         ▼                           ▼
  [ Worker Pool ]           [ Read Replica Cluster ]
         │
         ▼
 [ Primary Database ]
```

---

## ⚖️ 3. Deep Dive & Core Components
Focus on the primary technical building blocks: **Eviction, consistency**

### Key Architectures & Data Flows
1. **Data Ingestion/Write Path**:
   - Describe how writes are queued, distributed, and committed to disk.
2. **Query/Read Path**:
   - Detail caching, indexing, and lookup mechanisms.

### Technology Options Comparison
| Tech Stack Option A | Tech Stack Option B | Trade-off / Decision |
| :--- | :--- | :--- |
| SQL (PostgreSQL/MySQL) | NoSQL (Cassandra/DynamoDB) | SQL for relational transactions; NoSQL for massive key-value writes. |
| Redis Cache | In-Memory Application State | Redis keeps web servers stateless; local memory causes stickiness. |

---

## 🚫 4. Common Mistakes & Interview Playbook

### Common Mistakes (The "Junior" Signals)
- Proposing a monolithic database with no caching under high write traffic.
- Forgetting to account for edge cases such as network partition or split-brain.
- Neglecting back-of-the-envelope estimations and proceeding directly to architecture.

### Interview Tip (The "Strong Hire" Signal)
> *"When designing this system, we avoid single points of failure by keeping the compute tier stateless. We handle high-frequency hot spots by caching metadata, and use consistent partitioning keys to avoid write hot-spotting in our database nodes."*

---

## 💡 5. My Practice Notes & Whiteboard
*Use this section to sketch your designs, document review feedback, or note specific learnings.*

- **Key Learnings**: ...
- **Mistakes Made**: ...
- **Interviewer Feedback**: ...