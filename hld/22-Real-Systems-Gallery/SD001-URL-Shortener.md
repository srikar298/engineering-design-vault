# 🔗 SD001: Design a URL Shortener (Bit.ly)

## 📋 1. Requirements
- **Functional**: Shorten URL, Redirect short URL to long URL.
- **Non-Functional**: High availability, Low latency, Scalability (100M URLs/month).

## 🏗️ 2. High-Level Design
Client ➔ Load Balancer ➔ Web Servers ➔ NoSQL DB (Document/KV) & Cache (Redis).

## 🗄️ 3. Data Modeling
- **Hashing**: Use **Base62** encoding on an auto-incrementing ID.
- **Storage**: MongoDB or DynamoDB (Value: Long URL, Key: Short URL).

## 🔬 4. Senior Deep Dives
- **Handling Redirects**: Use **HTTP 301 (Permanent)** to reduce server load (browsers cache the redirect) or **HTTP 302 (Temporary)** for analytics tracking.
- **Pre-generating IDs**: Use a separate service (ID Gen) to pre-calculate unique short keys in bulk to avoid collisions during high concurrent writes.

---

## 🔬 Tracker Diagnostics (SD001)

*   **Primary Technologies:** Base62, Redis, DynamoDB.
*   **The "Freeze Trap":** Candidates often get stuck on the math for "how many characters are needed" for 5 years. (Answer: $62^7$ provides 3.5 trillion URLs).
*   **Architecture Checklist:**
    *   [ ] Hashing Strategy (Base62)
    *   [ ] Read-heavy Caching (Redis)
    *   [ ] Write-throughput DB (NoSQL)
*   **Trade-off Audit:**
    *   **DB:** NoSQL over SQL (Simpler KV mapping, better horizontal scale).
    *   **Redirect:** 301 over 302 (Latency optimization).
