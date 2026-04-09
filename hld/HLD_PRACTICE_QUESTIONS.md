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

1.  **Design Ticketmaster**: Focus on concurrency, locking, and "10k users booking 1 seat" problem.
2.  **Design FB News Feed**: Focus on Fanout-on-read vs. Fanout-on-write.
3.  **Design WhatsApp**: Focus on WebSockets, Message Queues, and Last Seen status.
4.  **Design Tinder**: Focus on Geo-sharding and matching algorithms.
5.  **Design a Rate Limiter**: Focus on Distributed counters and Sliding Window algorithms.
6.  **Design Yelp (Proximity Server)**: Focus on Quad-Trees vs Geo-hashing.

---

## 🟠 LEVEL 3: Hard (Focus on High Throughput & Distributed Consensus)
*Target: SDE-3 / Architect / Founding Engineer*

1.  **Design Instagram**: Focus on massive image storage (S3 + CDN) and timeline scaling.
2.  **Design Uber**: Focus on high-frequency location updates and driver-rider matching.
3.  **Design a Job Scheduler**: Focus on task persistence, retry logic, and worker management.
4.  **Design Web Crawler at Scale**: Focus on deduplication, politeness, and multi-threaded indexing.
5.  **Design a Payment System**: Focus on idempotency, transactional outbox, and reconciliation.
6.  **Design Top K / Leaderboard**: Focus on Count-Min Sketch and real-time aggregation.
7.  **Design Search Autocomplete**: Focus on Trie-based storage and prefix searching at scale.

---

## 💡 How to Practice
1.  **Pick a Problem**: Give yourself 45 minutes.
2.  **Use the [Delivery Framework](./HLD_DELIVERY_FRAMEWORK.md)**: Don't skip steps.
3.  **Diagram it**: Use Excalidraw or a physical whiteboard.
4.  **Critique yourself**: Where is the single point of failure? How much will this cost?
