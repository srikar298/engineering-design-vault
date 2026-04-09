# ⏱️ Rate Limiter (API Guard)

> **Interview Time:** 40 Minutes | **Level:** SDE-2 (Algorithm + Concurrency)

## 📋 The Prompt
Design a generic rate limiter that can be used to protect various APIs from being overwhelmed by too many requests.

### 🛠️ Core Requirements
1.  **Algorithms:** Support at least one algorithm (Token Bucket or Leaky Bucket).
2.  **Granularity:** Allow rate limiting per User, per IP, or per API Endpoint.
3.  **Thread Safety:** The limiter must handle thousands of concurrent requests without race conditions.
4.  **Configuration:** Ability to define rules like "100 requests per minute".

### ⚙️ Constraints & Invariants
-   Minimal Overhead: The rate limiter should not add significant latency to the API call.
-   Accurate tracking of time windows.

---

## ✅ Self-Evaluation Checklist
- [ ] **Locking:** Did you use `synchronized` (heavy) or `AtomicLong` (lightweight) for token counts?
- [ ] **Singleton Pattern:** Is the `RateLimiterManager` a singleton?
- [ ] **Strategy Pattern:** Did you make the Algorithm (Token vs Leaky) interchangeable?
- [ ] **Clean Logic:** How do you handle the "Refill" logic? (Pro-tip: Don't use a background thread; calculate refill on-the-fly during the request).

---

## 📂 Practice
Go to the `practice/` folder and implement the `isAllowed(id)` method.
