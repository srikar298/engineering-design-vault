# 🎯 HLD Practice Questions (Classified by Difficulty)

To master High-Level Design, you must move from "knowing components" to "designing flows." Practice these in order.

---

## 🟢 LEVEL 1: Easy (Focus on Basic Components)
*Target: SDE-1 / Junior SDE-2*

1.  **Design Bit.ly (URL Shortener)**: Focus on hashing and storage estimation.
2.  **Design Dropbox (File Storage)**: Focus on block storage vs metadata storage and sync.
3.  **Design a News Aggregator**: Focus on basic crawling and read-heavy caching.
4.  **Design a Local Delivery Service**: Focus on basic state management (Order placed -> Picked up -> Delivered).

---

## 🟡 LEVEL 2: Medium (Focus on Trade-offs & Scaling)
*Target: SDE-2 / Senior SDE-2*

1.  **Design Ticketmaster**: *Key Point:* Concurrency (Distributed Locking using Redis/Zookeeper) and handling the "10k users booking 1 seat" problem.
2.  **Design FB News Feed**: *Key Point:* **Fanout-on-read** vs. **Fanout-on-write**. How to handle celebrity users with 100M+ followers.
3.  **Design WhatsApp**: *Key Point:* WebSocket management, Message Queues (Kafka) for async delivery, and Last Seen status (Heartbeat service).
4.  **Design Tinder**: *Key Point:* **Geo-sharding** (Quad-Trees vs Geo-hashing) and efficient proximity matching.
5.  **Design a Rate Limiter**: *Key Point:* Distributed counters in Redis using the **Sliding Window Counter** algorithm.
6.  **Design Yelp (Proximity Server)**: *Key Point:* Spatial indexing and the trade-offs of **SQL (PostGIS)** vs **NoSQL (Elasticsearch/Redis Geo)**.

---

## 🟠 LEVEL 3: Hard (Focus on High Throughput & Distributed Consensus)
*Target: SDE-3 / Architect*

1.  **Design Instagram**: *Key Point:* Massive image storage (S3 + CDN) and pre-computing timelines for active users.
2.  **Design Uber**: *Key Point:* High-frequency location updates (WebSockets/gRPC) and handling the **Grid/Cell partitioning** system (S2 Geometry).
3.  **Design a Job Scheduler**: *Key Point:* Task persistence (DB over MQ), retry logic with backoff, and worker heartbeats.
4.  **Design a Payment System**: *Key Point:* **Idempotency**, **Transactional Outbox**, and reconciliation processes.
5.  **Design Search Autocomplete**: *Key Point:* **Trie-based** storage, prefix searching, and real-time top-K aggregation.

---

## 💡 How to Practice
1.  **Pick a Problem**: Give yourself 45 minutes.
2.  **Use the [Delivery Framework](./HLD_DELIVERY_FRAMEWORK.md)**: Don't skip steps.
3.  **Identify the "Pivot"**: Every HLD question has one core problem (e.g., "Scaling writes" or "Global low latency"). Find it early.
4.  **Self-Critique**: Where is the single point of failure? How much will this cost? What happens if the network partitions (CAP)?

---
