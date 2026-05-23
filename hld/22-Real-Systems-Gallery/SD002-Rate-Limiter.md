# 🛑 SD002: Design a Distributed Rate Limiter

## 📋 1. Requirements
- **Functional**: Limit requests from a single user/IP (e.g., 5 req/sec).
- **Non-Functional**: Distributed (scale across nodes), Accurate, Low latency.

## 🏗️ 2. High-Level Design
API Gateway ➔ Rate Limiter Middleware ➔ Redis (Central counter).

## 🗄️ 3. Algorithms
- **Token Bucket**: Simple, memory-efficient.
- **Sliding Window Log**: Most accurate, but high memory usage.
- **Sliding Window Counter**: The best balance between accuracy and memory.

## 🔬 4. Senior Deep Dives
- **Performance**: Don't do a network trip for every request. Use **Local Caching** with periodic sync to Redis (using Lua scripts to ensure atomicity).
- **Failure**: If Redis goes down, should we block all requests or allow all? (Answer: **Fail-Open** to prioritize availability over perfect rate limiting).

---

## 🔬 Tracker Diagnostics (SD002)

*   **Primary Technologies:** Redis (Sorted Sets/ZSet), Lua Scripts.
*   **The "Freeze Trap":** Candidates often struggle with **Race Conditions** when multiple nodes update the same counter. (Answer: Use Redis Lua Scripts or atomic `INCR` operations).
*   **Architecture Checklist:**
    *   [ ] Centralized Counter (Redis)
    *   [ ] Low-latency Middleware
    *   [ ] Sliding Window logic
*   **Trade-off Audit:**
    *   **Fail Mode:** Fail-Open over Fail-Closed (Availability priority).
    *   **Accuracy:** Sliding Window over Fixed Window (Prevents boundary bursts).
