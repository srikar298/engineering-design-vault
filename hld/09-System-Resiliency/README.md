# 🛡️ 09 - System Resiliency (The SDE-3 Edge)

## 📖 The Concept
Distributed systems *will* fail. Resiliency is the ability of a system to recover from failures and continue to function, preventing a single broken component from taking down the entire architecture.

## 📊 The SDE-2 Trade-off Table: Resiliency Patterns

| Pattern | How it Works | When to Use It |
| :--- | :--- | :--- |
| **Circuit Breaker** | Stops making calls to a failing downstream service for a set time. | When a 3rd party API is timing out, preventing your threads from hanging. |
| **Bulkhead** | Isolates resources (e.g., Thread pools) so one failure doesn't consume everything. | When Service A and Service B share a server; if B spikes, A shouldn't crash. |
| **Rate Limiting** | Restricts the number of requests a client can make. | To prevent DDoS attacks or abusive clients from hogging resources. |
| **Retry w/ Backoff** | Automatically retries failed network calls with increasing delays. | For transient network blips (e.g., a momentary 503 error). |

## 🚫 The Interview Trap
**"I will just add retries to all external API calls."**
Retries without backoff or jitter can cause a **Retry Storm**. If a downstream service is struggling, hitting it immediately with thousands of retries will kill it completely.
*Better Answer:* "I will implement an Exponential Backoff strategy with Random Jitter to space out the retries and give the downstream service time to recover."

## 🚀 The SDE-3 Edge: Distributed Rate Limiting
If the interviewer asks: *"How do you rate-limit an API to 100 requests/minute when you have 50 API Gateway nodes?"*

**The Trap:** Using an in-memory counter on each node. If a user hits different nodes, they bypass the limit.

**The SDE-3 Solution:**
1. **Centralized Counter (Redis):** Use Redis to store the rate limit counts.
2. **Algorithm Choice:**
    *   *Token Bucket:* Good for smooth traffic, but allows bursts.
    *   *Sliding Window Log:* Stores exact timestamps. 100% accurate but memory heavy.
    *   *Sliding Window Counter (The Winner):* A hybrid approach that stores counts in overlapping windows. It provides a near-perfect estimation with minimal memory footprint in Redis.
