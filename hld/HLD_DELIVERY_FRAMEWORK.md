# 🚀 HLD Delivery Framework: The 45-Minute Interview Strategy

This is the exact framework to follow during a High-Level Design interview. It ensures you cover all bases without getting bogged down in low-level details too early.

---

## 🕒 1. Requirements & Scope (≈ 5m)
*Goal: Define the boundaries. Don't design a "generic" system.*

- **Functional Requirements**: Pick and prioritize the **Top 3 features**. (e.g., "User can post a tweet", "User can follow others", "User can see a timeline").
- **Non-Functional Requirements**:
    - **Scalability**: Number of DAU (Daily Active Users), Peak concurrent users.
    - **Availability**: Is 99.9% (3 nines) acceptable?
    - **Consistency**: Choice between CAP/PACELC (Strong vs. Eventual).
    - **Latency**: P99 response time targets (e.g., < 200ms).

---

## 📊 2. Capacity Estimation (≈ 5m)
*Goal: Quantify the scale. Only do this if it changes your design.*

- **Storage**: (Avg object size) * (Total objects over 5 years).
- **Throughput**: (RPS) based on DAU and read/write ratio (e.g., 100:1 read-heavy).
- **Bandwidth**: (Throughput) * (Object size).

---

## 🏗️ 3. High-Level Design (≈ 10m)
*Goal: Draw the boxes and arrows. Start simple.*

- **Clients**: Mobile, Web.
- **DNS & CDN**: For static assets.
- **API Gateway/Load Balancer**: The entry point.
- **Microservices**: Decompose by domain (User Service, Tweet Service).
- **Storage**: Primary Database (SQL vs NoSQL choice).
- **Cache**: Where to place it (Redis/Memcached).

---

## 🗄️ 4. Data Model & API Contract (≈ 5m)
*Goal: Define how data lives and moves.*

- **API Endpoints**: `POST /v1/tweet`, `GET /v1/feed?userId=...`
- **Database Schema**: Key fields, Primary keys, Foreign keys.
- **Storage Choice**: "We use Cassandra for the feed because it handles high-velocity appends better than Postgres."

---

## 🔬 5. Deep Dives & Bottlenecks (≈ 15m)
*Goal: Prove you are an SDE-2+ by identifying and solving failures.*

- **Scaling**: Sharding strategy (Sharding by UserID vs TweetID).
- **Hot Keys**: How to handle celebrity users (e.g., Fanout-on-read vs Fanout-on-write).
- **Resiliency**: Circuit breakers, Retries with exponential backoff.
- **Consistency**: How to handle replication lag.

---

## 📡 6. Observability & Ops (≈ 5m)
*Goal: How do you know it's working?*

- **Monitoring**: SLIs/SLOs (Availability, Latency).
- **Logging**: Centralized log aggregation (ELK stack).
- **Tracing**: Distributed tracing for microservice calls.

---

## 🚫 Common Mistakes to Avoid
1.  **Silence**: Talk through your trade-offs. The interviewer wants to hear your "Engineering Judgment."
2.  **Over-engineering**: Don't suggest a global multi-region setup if the requirement is for 1,000 users.
3.  **Missing Trade-offs**: Never say "I'll use Kafka" without saying "because it gives us log-based persistence which we need for replayability."
