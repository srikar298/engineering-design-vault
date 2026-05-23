# 🚀 HLD Delivery Framework: The 45-Minute Interview Strategy

This is the exact framework to follow during a High-Level Design interview. It ensures you cover all bases without getting bogged down in low-level details too early.

---

## ⏱️ The 45-Minute Tracker Pacing

Match this pacing against your `INTERVIEW SIMULATOR` sheet:

| Phase | Time | Goal | "Anti-Freeze" Trigger if Stuck |
| :--- | :--- | :--- | :--- |
| **1. Clarification** | 0-5m | Define scope & DAU | Write down Top 3 features. |
| **2. Estimation** | 5-10m | QPS, Storage, Bandwidth | Skip exact math; use placeholders (e.g., "Assume 10k QPS"). |
| **3. High-Level Design** | 10-20m | Boxes and arrows | Draw: Client ➔ LB ➔ API Gateway ➔ DB. |
| **4. Data Model & API** | 20-25m | Schema and endpoints | Write out a simple JSON payload. |
| **5. Deep Dive** | 25-40m | Scale, Sharding, Failures | Ask interviewer: "Which bottleneck should we tackle first?" |
| **6. Wrap Up** | 40-45m | Observability, Trade-offs | State limitations of your design. |

---

## 🧠 The HLD "Anti-Freeze" Protocol

If you blank out during an HLD mock, execute these steps immediately:
1. **The Math Freeze (Capacity):** Stop trying to do complex mental division. Say: *"Based on 10M DAU, I estimate roughly 10k QPS peak. I'll use this as a placeholder to unblock the architecture, and we can refine the math later."*
2. **The Architecture Freeze (Whiteboard):** Draw the "Universal Skeleton": Client ➔ API Gateway ➔ Auth Service ➔ Core Service ➔ Database. You can always modify it, but drawing it gets your brain moving.
3. **The Database Freeze (SQL vs NoSQL):** If unsure, default to **PostgreSQL**. Say: *"I'll start with Postgres because of its ACID guarantees and maturity. If our write-throughput exceeds its horizontal scaling limits later, we can evaluate a NoSQL column-family store."*

---

## 🗣️ "Explain Aloud" Prompts (SDE-2+ Trade-off Articulation)

Your `Trade_off_Articulation` score in the sheet depends on how well you use these scripts:

*   **During Component Choice:** *"I'm choosing [Option A] over [Option B] because our primary constraint is [Latency/Consistency/Write-Throughput]. While [Option B] is simpler, it won't survive our peak load."*
*   **During Database Selection:** *"I'll use Cassandra here. We have a 10:1 write-heavy workload (time-series data). A relational database's B-Trees will bottleneck on writes, whereas Cassandra's LSM-Trees are optimized for high-velocity appends."*
*   **During Caching:** *"I'm placing Redis here with a Cache-Aside pattern. Because this is financial data, I must set a short TTL and implement Cache Invalidation on every write to minimize stale reads."*
*   **During Failure Handling:** *"If this downstream service fails, we don't want a cascading failure. I'm putting a Circuit Breaker here. If it opens, we'll return a degraded response from a fallback cache."*

---

## 🕒 Original HLD Steps (Reference)

### 1. Requirements & Scope (≈ 5m)
*Goal: Define the boundaries. Don't design a "generic" system.*

- **Functional Requirements**: Pick and prioritize the **Top 3 features**. (e.g., "User can post a tweet", "User can follow others", "User can see a timeline").
- **Non-Functional Requirements**:
    - **Scalability**: Number of DAU (Daily Active Users), Peak concurrent users.
    - **Availability**: Is 99.9% (3 nines) acceptable?
    - **Consistency**: Choice between CAP/PACELC (Strong vs. Eventual).
    - **Latency**: P99 response time targets (e.g., < 200ms).

---

### 2. Capacity Estimation (≈ 5m)
*Goal: Quantify the scale. Only do this if it changes your design.*

- **Storage**: (Avg object size) * (Total objects over 5 years).
- **Throughput**: (RPS) based on DAU and read/write ratio (e.g., 100:1 read-heavy).
- **Bandwidth**: (Throughput) * (Object size).

---

### 3. High-Level Design (≈ 10m)
*Goal: Draw the boxes and arrows. Start simple.*

- **Clients**: Mobile, Web.
- **DNS & CDN**: For static assets.
- **API Gateway/Load Balancer**: The entry point.
- **Microservices**: Decompose by domain (User Service, Tweet Service).
- **Storage**: Primary Database (SQL vs NoSQL choice).
- **Cache**: Where to place it (Redis/Memcached).

---

### 4. Data Model & API Contract (≈ 5m)
*Goal: Define how data lives and moves.*

- **API Endpoints**: `POST /v1/tweet`, `GET /v1/feed?userId=...`
- **Database Schema**: Key fields, Primary keys, Foreign keys.
- **Storage Choice**: "We use Cassandra for the feed because it handles high-velocity appends better than Postgres."

---

### 5. Deep Dives & Bottlenecks (≈ 15m)
*Goal: Prove you are an SDE-2+ by identifying and solving failures.*

- **Scaling**: Sharding strategy (Sharding by UserID vs TweetID).
- **Hot Keys**: How to handle celebrity users (e.g., Fanout-on-read vs Fanout-on-write).
- **Resiliency**: Circuit breakers, Retries with exponential backoff.
- **Consistency**: How to handle replication lag.

---

### 6. Observability & Ops (≈ 5m)
*Goal: How do you know it's working?*

- **Monitoring**: SLIs/SLOs (Availability, Latency).
- **Logging**: Centralized log aggregation (ELK stack).
- **Tracing**: Distributed tracing for microservice calls.

---

## 🚫 Common Mistakes to Avoid
1.  **Silence**: Talk through your trade-offs. The interviewer wants to hear your "Engineering Judgment."
2.  **Over-engineering**: Don't suggest a global multi-region setup if the requirement is for 1,000 users.
3.  **Missing Trade-offs**: Never say "I'll use Kafka" without saying "because it gives us log-based persistence which we need for replayability."
