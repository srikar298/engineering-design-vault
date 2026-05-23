# 🏢 22 - Real-Systems Gallery (SD001-SD040)

This gallery contains the high-level blueprints for the most common system design interview problems. Each problem is mapped to its unique **Problem ID (SDXXX)** from your tracker.

---

## 🔴 TIER 1: MUST KNOW (Asked Everywhere)

| ID | Problem | Key Technologies | Status |
| :--- | :--- | :--- | :--- |
| **SD001** | URL Shortener | Hashing (Base62), Redis, SQL | [View Solution](./SD001-URL-Shortener.md) |
| **SD002** | Rate Limiter | Redis (ZSet), Token Bucket | [View Solution](./SD002-Rate-Limiter.md) |
| **SD003** | Consistent Hashing | Virtual Nodes, Hash Ring | [View Solution](./SD003-Consistent-Hashing.md) |
| **SD006** | News Feed | Fan-out on Write/Read, Redis | [View Solution](./SD006-News-Feed.md) |
| **SD008** | Chat System | WebSockets, Cassandra, Kafka | [View Solution](../21-Case-Study-WhatsApp/) |
| **SD011** | YouTube | CDN, HLS/DASH, Blob Storage | [View Solution](./SD011-YouTube.md) |
| **SD013** | Uber / Lyft | Quadtrees, Geospatial Indexing | [View Solution](./SD013-Uber.md) |

---

## 🟠 TIER 2: VERY COMMON (Senior/SDE-2)

| ID | Problem | Key Technologies | Status |
| :--- | :--- | :--- | :--- |
| **SD016** | Payment System | Idempotency, Saga, 2PC | [View Solution](./SD016-Payments.md) |
| **SD022** | Hotel Booking | Distributed Locking, Optimistic Locking | [View Solution](./SD022-Booking.md) |
| **SD029** | ID Generator | Snowflake ID, Zookeeper | [View Solution](./SD029-ID-Generator.md) |
| **SD030** | Monitoring | Time-series DB, Pull vs Push | [View Solution](./SD030-Metrics.md) |

---

## 🏗️ How to Practice
1. Pick a Problem ID.
2. Set a **45-minute timer** in your Google Sheet.
3. Whiteboard the solution in your IDE or on paper.
4. Compare your architectural decisions with the **"Senior Deep Dives"** in each file.
